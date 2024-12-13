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

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.util.testUtils;
import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.ext.ScriptUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.*;

/**
 * This class tests proper discrepancy calculations for
 * - a contest that is on the CVR, but is not targeted,
 * - a contest that is on the CVR, and is targeted, but is not the reason this CVR was sampled.
 * In each case, we audit a ballot, claim that there was a discrepancy, and check that the
 * discrepancy count increases as expected.
 * We then reaudit the ballot and enter the 'correct' value (i.e. the same as the CSV), then check
 * that the discrepancy count goes back to zero.
 */
@Test(enabled=true)
public class ContestsNotTargetedOrNotOnCVR extends Workflow {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(ContestsNotTargetedOrNotOnCVR.class);

  /**
   * The number of counties in this test.
   */
  private static final int NUM_COUNTIES_THIS_TEST = 1;

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {

    final var containerDelegate = setupContainerStartPostgres(postgres);

    var s = Persistence.openSession();
    s.beginTransaction();

    // Used to initialize the database, e.g. to set the ASM state to the DOS_INITIAL_STATE
    // and to insert counties and administrator test logins.
    ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");

    runMain("ContestsNotTargetedOrNotOnCVR");
  }

  /**
   * This "test" uploads CVRs and ballot manifests for all 64 counties.
   * The uploads match the following http files in the workflows directory:
   * - demo1_loadCVRs, demo1_loadManifests,
   * - Boulder_loadCVRs, Boulder_loadManifest.
   */
  @Test(enabled=true)
  public void runNotCovered() throws InterruptedException {
    testUtils.log(LOGGER, "Demo1");

    // 1. First upload the manifest
    final String manifestFileName = dataPath + "AdamsAndAlamosa/adams-manifest.csv";
    uploadCounty(1, MANIFEST_FILETYPE, manifestFileName, manifestFileName + ".sha256sum");

    // 2. Then upload the CVRs. The order is important because it's an error to try to import a manifest while the CVRs
    // are being read.
    final String cvrFileName = dataPath + "AdamsAndAlamosa/adams-cvrexport.csv";
    uploadCounty(1, CVR_FILETYPE, cvrFileName, cvrFileName + ".sha256sum");

    // Wait while the CVRs (and manifests) are uploaded.
    assertTrue(uploadSuccessfulWithin(600, Set.of(1), CVR_JSON));
    assertTrue(uploadSuccessfulWithin(20, Set.of(1), MANIFEST_JSON));

    // Get the DoSDashboard refresh response; sanity check for initial state.
    JsonPath dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());
    assertEquals(dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CHOICES).toString(), "{}");
    assertNull(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON));
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));

    // 3. Set the audit info, including the canonical list and the risk limit; check for update.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.03);
    updateAuditInfo(dataPath + "AdamsAndAlamosa/adams-and-alamosa-canonical-list.csv", riskLimit);
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(0, riskLimit
        .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())));
    // There should be two canonical contests, i.e. the two we targeted.
    assertEquals(2, dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());
    // Check that the seed is still null.
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
    assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

    // No need to generate assertions - these are all plurality.

    // 5. Choose targeted contests for audit.
    targetContests(Map.of("Adams COUNTY COMMISSIONER DISTRICT 3","COUNTY_WIDE_CONTEST",
        "Regent of the University of Colorado - At Large", "COUNTY_WIDE_CONTEST"));

    // 6. Set the seed.
    setSeed(defaultSeed);

    // This should be complete audit info.
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(AUDIT_INFO + "." + SEED), defaultSeed);
    assertEquals(dashboard.get(ASM_STATE), COMPLETE_AUDIT_INFO_SET.toString());

    // 7. Start Audit Round
    startAuditRound();
    dashboard = getDoSDashBoardRefreshResponse();
    TestAuditSession session = countyAuditInitialise(1);

    auditCounty(1, session);

    // Audit board sign off for each county.
    countySignOffLogout(session);

    // Check that there are no more ballots to sample across all counties in first round.
    dashboard = getDoSDashBoardRefreshResponse();
    final Map<String,Map<String,Object>> status = dashboard.get(COUNTY_STATUS);

    for(final Map.Entry<String,Map<String,Object>> entry : status.entrySet()){
      assertEquals(entry.getValue().get(BALLOTS_REMAINING).toString(), "0");
    //  assertEquals(entry.getValue().get(ESTIMATED_BALLOTS).toString(), "0");
    }
    // TODO Sanity check of assertions and sample size estimates.

    LOGGER.debug("Successfully completed ContestsNotTargetedOrNotOnCVR.");
  }

}
