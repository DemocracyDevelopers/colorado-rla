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
import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A demonstration workflow that tests sample size estimation with and without manifests, comparing
 * the implications and ensuring that manifests are either properly ignored or properly incorporated
 * throughout both sample size estimation and subsequent auditing. For example, when the manifest
 * exactly matches the CVR count, the samples should be the same; extras in the manifest should
 * increase the sample size, and a smaller manifest than CVR list should throw an error.
 * All the tests here are the same plain plurality contest - we just vary the absence of presence of
 * the manifest, and (if present) the number of ballots it states.
 * Note that it does not test for correct sample sizes, only for correct _changes_ to the sample
 * sizes as a consequence of changes to the manifest.
 */
@Test(enabled=true)
public class EstimateSampleSizesVaryingManifests extends Workflow {

  /**
   * Path for all the data files.
   */
  private static final String dataPath = "src/test/resources/CSVs/PluralityOnly/";

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(EstimateSampleSizesVaryingManifests.class);

  /**
   * Container for the mock-up database.
   */
  private final static PostgreSQLContainer<?> postgres = createTestContainer();

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

    runMain("EstimateSampleSizesVaryingManifests");
  }

  /**
   * This "test" uploads CVRs and ballot manifests.
   */
  @Test(enabled = true)
  public void runManifestVaryingDemo() {

    final String margin2Contest = "PluralityMargin2";
    final String margin10Contest = "PluralityMargin10";

    // Upload the CSVs but not the manifests.
    final String CVRFile = dataPath + "Plurality100votes2And10Margins";
    final String[] suffixes = {"", "Copy2", "Copy3"};

    for(int i = 1 ; i <= 3 ; i++) {
      uploadCounty(i, "cvr-export", CVRFile + suffixes[i-1] + ".csv",
                                        CVRFile + suffixes[i-1] + ".csv.sha256sum");
    }

    //TODO figure out how to wait.

    // Get the DoSDashboard refresh response; sanity check.
    JsonPath dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get("asm_state"), DOS_INITIAL_STATE.toString());

    // Set the audit info, including the canonical list and the (stupidly large) risk limit; sanity check.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.5);
    updateAuditInfo(dataPath + "Plurality_Only_Test_Canonical_List.csv", riskLimit);
    setSeed(defaultSeed);
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(0, riskLimit
        .compareTo(new BigDecimal(dashboard.get("audit_info.risk_limit").toString())));

    // Estimate Sample sizes, without the manifest. This should count the CSVs. Sanity check.
    Map<String, EstimateSampleSizes.EstimateData> estimatesWithoutManifests = getSampleSizeEstimates();
    assertEquals(estimatesWithoutManifests.size(), 6);

    // Upload the manifests.
    // County 1 gets a manifest that matches the CSV count
    // County 2 gets a manifest with double the CSV count.
    // County 3 gets a manifest with inadequate count (should cause an error).
    List<String> MANIFESTS = new ArrayList<>();
    MANIFESTS.add(dataPath + "HundredVotes_Manifest.csv");
    MANIFESTS.add(dataPath + "HundredVotes_DoubledManifest.csv");
    MANIFESTS.add(dataPath + "HundredVotes_InsufficientManifest.csv");
    for(int i=1 ; i <= 3 ; i++) {
      uploadCounty(i, "ballot-manifest", MANIFESTS.get(i-1), MANIFESTS.get(i-1) + ".sha256sum");
    }

    // 2. Upload the manifest that matches the CSV count, then get estimates.
    // All the estimate data should be the same, because EstimateSampleSizes doesn't use manifests.
    Map<String, EstimateSampleSizes.EstimateData> estimatesWithManifests = getSampleSizeEstimates();
    dashboard = getDoSDashBoardRefreshResponse();
    for(String s : suffixes) {
      assertEquals(estimatesWithManifests.get(margin2Contest+s), estimatesWithoutManifests.get(margin2Contest+s));
      assertEquals(estimatesWithManifests.get(margin10Contest+s), estimatesWithoutManifests.get(margin10Contest+s));
    }

    final int margin2Estimate =  estimatesWithManifests.get(margin2Contest).estimatedSamples();

    // Target the margin-2 contests in every county.
    // TODO targetContests doesn't work without manifests (which is good) but also doesn't seem to throw an error (though it should).
    targetContests(Map.of(
        margin2Contest, "COUNTY_WIDE_CONTEST",
        margin2Contest + suffixes[1], "COUNTY_WIDE_CONTEST",
        margin2Contest + suffixes[2], "COUNTY_WIDE_CONTEST"
    ));

    startAuditRound();
    dashboard = getDoSDashBoardRefreshResponse();

    // For County 1, the estimated ballots to audit inside corla should also match the pre-audit estimates,
    // because the manifests and CVR files have equal counts.
    final int county1Estimate = dashboard.getInt("county_status.1.estimated_ballots_to_audit");
    assertEquals(county1Estimate, margin2Estimate);

    // County 2 should have a much larger estimated ballots to audit, because of manifest phantoms.
    final int county2Estimate  = dashboard.getInt("county_status.2.estimated_ballots_to_audit");
    assertTrue(county2Estimate > margin2Estimate);

    // Run Sample Size estimation again; check that it doesn't change any of the colorado-rla estimates.
    getSampleSizeEstimates();
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.getInt("county_status.1.estimated_ballots_to_audit"), county1Estimate);
    assertEquals(dashboard.getInt("county_status.2.estimated_ballots_to_audit"), county2Estimate);

    // County 3 should have an error, because the manifest has fewer ballots than the CVR.
    // FIXME - think about what should happen when the manifest is smaller than the CSV list.
    int test = dashboard.getInt("county_status.3.estimated_ballots_to_audit");
  }

}
