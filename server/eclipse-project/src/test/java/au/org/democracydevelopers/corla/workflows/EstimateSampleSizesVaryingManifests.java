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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;
import static us.freeandfair.corla.model.AuditReason.COUNTY_WIDE_CONTEST;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   * This "test" uploads CVRs and ballot manifests.
   */
  @Test(enabled=true)
  public void runManifestVaryingDemo() throws InterruptedException {
    testUtils.log(LOGGER, "runManifestVaryingDemo");

    PostgreSQLContainer<?> postgres = setupIndividualTestDatabase("EstimateSampleSizesVaryingManifests");

    final String margin2Contest = "PluralityMargin2";
    final String margin10Contest = "PluralityMargin10";

    // Upload the CSVs but not the manifests.
    final String CVRFile = dataPath + "Plurality100votes2And10Margins";
    final String[] suffixes = {"", "Copy2", "Copy3"};

    for(int i = 1 ; i <= 3 ; i++) {
      uploadCounty(i, CVR_FILETYPE, CVRFile + suffixes[i-1] + ".csv",
                                        CVRFile + suffixes[i-1] + ".csv.sha256sum");
    }

    assertTrue(uploadSuccessfulWithin(5, Set.of(1,2,3), CVR_JSON));
    assertFalse(uploadSuccessfulWithin(5, Set.of(1,2,3), MANIFEST_JSON));

    // Get the DoSDashboard refresh response; sanity check.
    JsonPath dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());

    // Set the audit info, including the canonical list and the (stupidly large) risk limit; sanity check.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.5);
    updateAuditInfo(dataPath + "Plurality_Only_Test_Canonical_List.csv", riskLimit);
    setSeed(defaultSeed);
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(riskLimit
        .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())), 0);

    // Estimate Sample sizes, without the manifest. This should count the CSVs. Sanity check.
    Map<String, EstimateSampleSizes.EstimateData> estimatesWithoutManifests = getSampleSizeEstimates();
    assertEquals(estimatesWithoutManifests.size(), 6);

    // Get contest lists. Without manifests, this should be empty for the plain endpoint and
    // non-empty when the ignoreManifests flag is true.
    JsonPath response = getContests(false);
    JsonPath responseIgnoringManifests = getContests(true);
    assertTrue(response.getList("$").isEmpty());
    assertFalse(responseIgnoringManifests.getList("$").isEmpty());

    // Upload the manifests.
    // County 1 gets a manifest that matches the CSV count
    // County 2 gets a manifest with double the CSV count.
    // County 3 gets a manifest with inadequate count (should cause an error).
    List<String> MANIFESTS = new ArrayList<>();
    MANIFESTS.add(dataPath + "HundredVotes_Manifest.csv");
    MANIFESTS.add(dataPath + "HundredVotes_DoubledManifest.csv");
    MANIFESTS.add(dataPath + "HundredVotes_InsufficientManifest.csv");
    for(int i=1 ; i <= 3 ; i++) {
      uploadCounty(i, MANIFEST_FILETYPE, MANIFESTS.get(i-1), MANIFESTS.get(i-1) + ".sha256sum");
    }

    // 2. Upload the manifest that matches the CSV count, then get estimates.
    // All the estimate data should be the same, because EstimateSampleSizes doesn't use manifests.
    Map<String, EstimateSampleSizes.EstimateData> estimatesWithManifests = getSampleSizeEstimates();
    for(String s : suffixes) {
      assertEquals(estimatesWithManifests.get(margin2Contest+s), estimatesWithoutManifests.get(margin2Contest+s));
      assertEquals(estimatesWithManifests.get(margin10Contest+s), estimatesWithoutManifests.get(margin10Contest+s));
    }

    final int margin2Estimate =  estimatesWithManifests.get(margin2Contest).estimatedSamples();

    // Target the margin-2 contests in every county.
    targetContests(Map.of(
        margin2Contest,
        Map.of(Workflow.REASON, COUNTY_WIDE_CONTEST.toString(), Workflow.WINNER, ""),
        margin2Contest + suffixes[1],
        Map.of(Workflow.REASON, COUNTY_WIDE_CONTEST.toString(), Workflow.WINNER, ""),
        margin2Contest + suffixes[2],
        Map.of(Workflow.REASON, COUNTY_WIDE_CONTEST.toString(), Workflow.WINNER, "")
    ));

    // Get contest lists again. Now this should be non-empty regardless of whether manifests are
    // ignored.
    response = getContests(false);
    JsonPath responseIgnoringManifests2 = getContests(true);
    assertFalse(response.getList("$").isEmpty());
    assertFalse(responseIgnoringManifests2.getList("$").isEmpty());
    // Adding the manifests makes no difference to the ignore-manifests response.
    assertEquals(responseIgnoringManifests.get("$").toString(), responseIgnoringManifests2.get("$").toString());

    startAuditRound();
    dashboard = getDoSDashBoardRefreshResponse();

    // For County 1, the estimated ballots to audit inside corla should also match the pre-audit estimates,
    // because the manifests and CVR files have equal counts.
    final int county1Estimate = dashboard.getInt(COUNTY_STATUS + ".1." + ESTIMATED_BALLOTS);
    assertEquals(county1Estimate, margin2Estimate);

    // County 2 should have a much larger estimated ballots to audit, because of manifest phantoms.
    final int county2Estimate  = dashboard.getInt(COUNTY_STATUS + ".2." + ESTIMATED_BALLOTS);
    assertTrue(county2Estimate > margin2Estimate);

    // Run Sample Size estimation again; check that it doesn't change any of the colorado-rla estimates.
    getSampleSizeEstimates();
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.getInt(COUNTY_STATUS + ".1." + ESTIMATED_BALLOTS), county1Estimate);
    assertEquals(dashboard.getInt(COUNTY_STATUS + ".2." + ESTIMATED_BALLOTS), county2Estimate);

    // County 3 should have an error, because the manifest has fewer ballots than the CVR.
    // TODO - Verify how colorado-rla deals with the case where the manifest has fewer votes than the
    // CVR export - see <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/217">...</a>
    // int test = dashboard.getInt("county_status.3.estimated_ballots_to_audit");
  }

}
