/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire
service (https://github.com/DemocracyDevelopers/raire-service), in order to
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.workflows;

import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import java.math.BigDecimal;
import java.util.*;

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.util.testUtils;
import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;
import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.*;

/**
 * A demonstration workflow that uploads CVRs and ballot manifests for all 64 counties.
 * At the moment, this seems to run fine if run alone, but not to run in parallel with
 * EstimateSamplesVaryingManifests.
 */
@Test(enabled=true)
public class Demo1 extends Workflow {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Demo1.class);


  /**
   * This "test" uploads CVRs and ballot manifests for all 64 counties.
   * The uploads match the following http files in the workflows directory:
   * - demo1_loadCVRs, demo1_loadManifests,
   * - Boulder_loadCVRs, Boulder_loadManifest.
   */
  @Test(enabled=true)
  public void runDemo1() throws InterruptedException {
    testUtils.log(LOGGER, "Demo1");

    PostgreSQLContainer<?> postgres = setupIndividualTestDatabase("Demo1");

    // Empty workflow instance
    final Instance instance = new Instance();

    List<String> CVRS = new ArrayList<>();

    CVRS.add(dataPath + "Demo1/1-adams-cvrexport-plusByron-1.csv");
    CVRS.add(dataPath + "Demo1/2-alamosa-cvrexport-plusByron-2.csv");
    CVRS.add(dataPath + "Demo1/3-arapahoe-Byron-3-plus-tied-irv.csv");
    CVRS.add(dataPath + "Demo1/4-archuleta-kempsey-plusByron-4.csv");
    CVRS.add(dataPath + "split-Byron/Byron-5.csv");
    CVRS.add(dataPath + "split-Byron/Byron-6.csv");
    CVRS.add(dataPath + "Demo1/7-unredacted-boulder-2023-plusByron-7.csv");

    List<String> MANIFESTS = new ArrayList<>();

    MANIFESTS.add(dataPath + "Demo1/1-adams-plusByron-1-manifest.csv");
    MANIFESTS.add(dataPath + "Demo1/2-alamosa-plusByron-2-manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-3-manifest.csv");
    MANIFESTS.add(dataPath + "NewSouthWales21/Kempsey_Mayoral.manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-5-manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-6-manifest.csv");
    MANIFESTS.add(dataPath + "Demo1/7-unredacted-Boulder-IRV-Manifest.csv");

    for(int i = 8 ; i <= numCounties ; ++i){
      CVRS.add(dataPath + "split-Byron/Byron-" + i + ".csv");
      MANIFESTS.add(dataPath + "split-Byron/Byron-" + i + "-manifest.csv");
    }

    // 1. First upload all the manifests
    for(int i = 1 ; i <= numCounties ; ++i){
      uploadCounty(i, MANIFEST_FILETYPE, MANIFESTS.get(i-1), MANIFESTS.get(i-1) + ".sha256sum");
    }

    // 2. Then upload all the CVRs. The order is important because it's an error to try to import a manifest while the CVRs
    // are being read.
    for(int i = 1; i <= numCounties ; ++i){
      uploadCounty(i, CVR_FILETYPE, CVRS.get(i-1), CVRS.get(i-1) + ".sha256sum");
    }

    // Wait while the CVRs (and manifests) are uploaded. This can take a while, especially Boulder(7).
    assertTrue(uploadSuccessfulWithin(600, allCounties, CVR_JSON));
    assertTrue(uploadSuccessfulWithin(20, allCounties, MANIFEST_JSON));

    // Get the DoSDashboard refresh response; sanity check for initial state.
    JsonPath dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());
    assertEquals(dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CHOICES).toString(), "{}");
    assertNull(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON));
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));

    // 3. Set the audit info, including the canonical list and the risk limit; check for update.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.03);
    updateAuditInfo(dataPath + "Demo1/" + "demo1-canonical-list.csv", riskLimit);
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(0, riskLimit
        .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())));
    // There should be canonical contests for each county.
    assertEquals(numCounties, dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());
    // Check that the seed is still null.
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
    assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

    // 4. Generate assertions; sanity check
    generateAssertions(postgres,"SQL/demo1-assertions.sql", 1);
    dashboard = getDoSDashBoardRefreshResponse();

    // There should be 4 IRV contests.
    assertEquals(4, dashboard.getList("generate_assertions_summaries").size());

    // 5. Choose targeted contests for audit.
    final Map<String,String> targets = Map.of("City of Longmont - Mayor","COUNTY_WIDE_CONTEST",
        "Byron Mayoral", "STATE_WIDE_CONTEST",
        "Kempsey Mayoral", "COUNTY_WIDE_CONTEST");
    final List<String> irvContests = List.of("Byron Mayoral", "Kempsey Mayoral");
    targetContests(targets);

    // 6. Set the seed.
    setSeed(defaultSeed);

    // This should be complete audit info.
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(AUDIT_INFO + "." + SEED), defaultSeed);
    assertEquals(dashboard.get(ASM_STATE), COMPLETE_AUDIT_INFO_SET.toString());

    // 7. Estimate sample sizes; sanity check.
    Map<String, EstimateSampleSizes.EstimateData> sampleSizes = getSampleSizeEstimates();
    assertFalse(sampleSizes.isEmpty());

    // Byron should be 3820. Kempsey 332.
    // For each targeted contest, check that the set of assertions: (i) is not empty; (ii) has
    // the correct minimum diluted margin; and (ii) has resulted in the correct sample size estimate.
    final Map<String,Double> expectedDilutedMargins = Map.of("City of Longmont - Mayor", 0.09078192,
        "Byron Mayoral", 0.00684644, "Kempsey Mayoral", 0.02200739);

    final Map<String,Integer> expectedSamples = Map.of("City of Longmont - Mayor", 81,
        "Byron Mayoral", 1065, "Kempsey Mayoral", 332);

    for(final String c : targets.keySet()) {
      verifySampleSize(c, expectedDilutedMargins.get(c),
          sampleSizes.get(c).estimatedSamples(), expectedSamples.get(c), irvContests.contains(c));
    }

    // 8. Start Audit Round
    startAuditRound();

    // 9. Log in as each county, and audit all ballots in sample.
    List<TestAuditSession> sessions = new ArrayList<>();
    for(final int cty : allCounties){
      sessions.add(countyAuditInitialise(cty));
    }

    // ACVR uploads for each county. Cannot run in parallel as corla does not like
    // simultaneous database accesses.
    for(final TestAuditSession  entry : sessions){
      auditCounty(1, entry, instance);
    }

    // Audit board sign off for each county.
    for(final TestAuditSession entry : sessions){
      countySignOffLogout(entry);
    }

    // Check that there are no more ballots to sample across all counties in first round,
    // and as this demo involved no discrepancies, that all audits are complete.
    dashboard = getDoSDashBoardRefreshResponse();
    final Map<String,Map<String,Object>> status = dashboard.get(COUNTY_STATUS);

    for(final Map.Entry<String,Map<String,Object>> entry : status.entrySet()){
      assertEquals(entry.getValue().get(BALLOTS_REMAINING).toString(), "0");
      assertEquals(entry.getValue().get(ESTIMATED_BALLOTS).toString(), "0");
      assertEquals(entry.getValue().get(DISCREPANCY_COUNT).toString(), "{}");
    }

    postgres.stop();
    LOGGER.info("Successfully completed Demo1 (First round audit; No Discrepancies).");
  }

}
