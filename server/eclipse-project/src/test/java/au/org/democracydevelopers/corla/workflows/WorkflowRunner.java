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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.COMPLETE_AUDIT_INFO_SET;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.PARTIAL_AUDIT_INFO_SET;

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import io.restassured.path.json.JsonPath;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * TODO:
 * -- Modelling database updates (embedding phantom records)
 * -- Checking for correct discrepancy counts
 * -- support for addition of disagreements
 * -- support for reauditing
 */
@Test(enabled=true)
public class WorkflowRunner extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(WorkflowRunner.class);

  /**
   * Directory in which instance JSON files are stored.
   */
  private static final String pathToInstances = "src/test/resources/workflows/instances";

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAllThisClass() {

    // Used to initialize the database, e.g. to set the ASM state to the DOS_INITIAL_STATE
    // and to insert counties and administrator test logins.
    runSQLSetupScript("SQL/co-counties.sql");

    runMain("Workflows");
  }


  /**
   * Run all workflow instances (with JSON extensions) defined in the WorkflowRunner.pathToInstances
   * directory. This method calls WorkflowRunner::runInstance for each of those workflow JSONs.
   */
  @Test(enabled=true)
  public void runWorkflows()  {
    final String prefix = "[runWorkflows]";

    // Run every workflow instance present in the pathToInstances directory.
    // Instances are JSON files.
    List<Path> pathList;

    try (Stream<Path> stream = Files.walk(Paths.get(pathToInstances))) {
      pathList = stream.map(Path::normalize)
          .filter(Files::isRegularFile)
          .filter(p -> isJSON(p.toString()))
          .collect(Collectors.toList());
    }
    catch(IOException e) {
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    pathList.forEach(p -> {
      try {
        runInstance(p);
      } catch (InterruptedException e) {
        final String msg = prefix + " " + e.getMessage();
        LOGGER.error(msg);
        throw new RuntimeException(msg);
      }
    });
  }

  /**
   * Given a JSON file defining an audit workflow (CVRs, Manifests, which CVRs to replace with
   * phantoms, which ballots to treat as phantoms, expected diluted margins and sample sizes,
   * which ballots to simulate discrepancies for, and expected end of round states ...), run
   * the test audit and verify that the expected outcomes arise.
   * @param pathToInstance Path to the JSON workflow instance defining the test.
   * @throws InterruptedException
   */
  private void runInstance(final Path pathToInstance) throws InterruptedException {
    final String prefix = "[runWorkflows] " + pathToInstance;

    try {
      // Convert data in the JSON workflow file to a workflow Instance.
      ObjectMapper toJson = new ObjectMapper();
      final Instance instance = toJson.readValue(pathToInstance.toFile(), Instance.class);

      // Upload all the manifests and CVRs as defined in the Instance.
      final List<String> cvrs = instance.getCVRs();
      final List<String> manifests = instance.getManifests();

      assertEquals(manifests.size(), numCounties);
      assertEquals(cvrs.size(), numCounties);

      for(int i = 0; i < manifests.size(); ++i){
        uploadCounty(i+1, MANIFEST_FILETYPE, manifests.get(i), manifests.get(i) + ".sha256sum");
      }

      // Upload all the CVRs. The order is important because it's an error to try to import a
      // manifest while the CVRs are being read.
      for(int i = 0; i < cvrs.size()  ; ++i){
        uploadCounty(i+1, CVR_FILETYPE, cvrs.get(i), cvrs.get(i) + ".sha256sum");
      }

      // Wait while the CVRs (and manifests) are uploaded.
      assertTrue(uploadSuccessfulWithin(600, allCounties, CVR_JSON));
      assertTrue(uploadSuccessfulWithin(20, allCounties, MANIFEST_JSON));

      // Get the DoSDashboard refresh response; sanity check for initial state.
      JsonPath dashboard = getDoSDashBoardRefreshResponse();
      assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());
      assertEquals(dashboard.getMap(AUDIT_INFO+"."+CANONICAL_CHOICES).toString(), "{}");
      assertNull(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON));
      assertNull(dashboard.get(AUDIT_INFO + "." + SEED));

      // Provide a risk limit, canonicalisation file, and seed as defined in the Instance.
      updateAuditInfo(instance.getCanonicalisationFile(), instance.getRiskLimit());
      dashboard = getDoSDashBoardRefreshResponse();
      assertEquals(0, instance.getRiskLimit()
          .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())));

      // There should be canonical contests for each county.
      assertEquals(numCounties,
          dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());

      // Check that the seed is still null.
      assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
      assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

      // Load additional SQL data (this is data that we want to add after we have
      // CVRs, manifests, etc loaded for each county). This will mostly be used to load
      // assertion data into the database, simulating a call to the raire-service.
      instance.getSQLs().forEach(TestClassWithDatabase::runSQLSetupScript);

      // At this point, if the Instance specifies that some CVRs should be treated as
      // Phantoms, we will need to replace the existing record type for that CVR with a Phantom.
      final List<Long> phantomCVRs = instance.getPhantomCVRS();
      try {
        makePhantoms(phantomCVRs);
      }
      catch(Exception e){
        final String msg = prefix + " cannot run make phantoms SQL script: " + e.getMessage();
        LOGGER.error(msg);
        throw new RuntimeException(msg);
      }

      dashboard = getDoSDashBoardRefreshResponse();

      // Check that the right number of IRV contests are present
      final List<String> irvContests = instance.getIRVContests();
      final Map<String,String> targets = instance.getTargetedContests();
      assertEquals(irvContests.size(), dashboard.getList("generate_assertions_summaries").size());

      // Choose targeted contests for audit as specified in the Instance.
      final Map<String,String> contestToDBID = targetContests(targets);

      // Set the seed (as specified in the Instance).
      setSeed(instance.getSeed());

      // The ASM state for the dashboard should be COMPLETE_AUDIT_INFO_SET.
      dashboard = getDoSDashBoardRefreshResponse();
      assertEquals(dashboard.get(AUDIT_INFO + "." + SEED), defaultSeed);
      assertEquals(dashboard.get(ASM_STATE), COMPLETE_AUDIT_INFO_SET.toString());

      // Estimate sample sizes; and then verify that they are as expected.
      // For each targeted IRV contest, check that the set of assertions: (i) is not empty; (ii) has
      // the correct minimum diluted margin; and (ii) has resulted in the correct sample size estimate.
      Map<String, EstimateSampleSizes.EstimateData> sampleSizes = getSampleSizeEstimates();
      assertFalse(sampleSizes.isEmpty());

      final Map<String,Integer> expectedSamples = instance.getExpectedSamples();
      final Map<String,Double> expectedMargins = instance.getDilutedMargins();
      for(final String c : targets.keySet()) {
        verifySampleSize(c, expectedMargins.get(c), sampleSizes.get(c).estimatedSamples(),
            expectedSamples.get(c), irvContests.contains(c));
      }

      // Run audit rounds until the number of expected samples required for each targeted
      // contest is zero. Check that the number of rounds completed is as expected for the
      // Instance, and that the end of round state for targeted contests is as expected
      // for the Instance.
      boolean auditNotFinished = true;
      int rounds = 0;

      while(auditNotFinished) {

        // Start Audit Round
        startAuditRound();

        // Log in as each county, and audit all ballots in sample.
        List<TestAuditSession> sessions = new ArrayList<>();
        for (final int cty : allCounties) {
          sessions.add(countyAuditInitialise(cty));
        }

        // ACVR uploads for each county. Cannot run in parallel as corla does not like
        // simultaneous database accesses.
        for (final TestAuditSession entry : sessions) {
          auditCounty(1, entry, instance);
        }

        // Audit board sign off for each county.
        for (final TestAuditSession entry : sessions) {
          countySignOffLogout(entry);
        }

        // Check that there are no more ballots to sample across all counties in first round,
        // and as this demo involved no discrepancies, that all audits are complete.
        dashboard = getDoSDashBoardRefreshResponse();
        final Map<String,Integer> statusEstBallotsToAudit = dashboard.get(ESTIMATED_BALLOTS);
        final Map<String,Integer> statusOptBallotsToAudit = dashboard.get(OPTIMISTIC_BALLOTS);
        final Map<String, Map<String,Integer>> discrepancies = dashboard.get(DISCREPANCY_COUNT);

        auditNotFinished = false;

        // Verify that the result of this round is what we expected in terms of number of
        // estimated and optimistic ballots to audit for each contest mentioned in the associated
        // field in the instance. Also test that the resulting discrepancy counts are as expected.
        final Optional<Map<String,Map<String,Integer>>> results = instance.getRoundContestResult(rounds+1);
        if(results.isPresent()){
          final Map<String,Map<String,Integer>> roundResults = results.get();

          for(final String contestName : roundResults.keySet()){
            final String dbID = contestToDBID.get(contestName);
            final Map<String,Integer> contestResult = roundResults.get(contestName);

            final int actEstBallots = statusEstBallotsToAudit.get(dbID);
            final int actOptBallots = statusOptBallotsToAudit.get(dbID);

            assertEquals(actEstBallots, contestResult.get(ESTIMATED_BALLOTS).intValue());
            assertEquals(actOptBallots, contestResult.get(OPTIMISTIC_BALLOTS).intValue());

            final int oneOverCount = contestResult.get(ONE_OVER_COUNT);
            final int oneUnderCount = contestResult.get(ONE_UNDER_COUNT);
            final int twoOverCount = contestResult.get(TWO_OVER_COUNT);
            final int twoUnderCount = contestResult.get(TWO_UNDER_COUNT);
            final int otherCount = contestResult.get(OTHER_COUNT);

            final Map<String,Integer> contestDiscrepancies = discrepancies.get(dbID);
            assertEquals(contestDiscrepancies.get(ONE_OVER).intValue(), oneOverCount);
            assertEquals(contestDiscrepancies.get(ONE_UNDER).intValue(), oneUnderCount);
            assertEquals(contestDiscrepancies.get(OTHER).intValue(), otherCount);
            assertEquals(contestDiscrepancies.get(TWO_OVER).intValue(), twoOverCount);
            assertEquals(contestDiscrepancies.get(TWO_UNDER).intValue(), twoUnderCount);
          }
        }

        // Go through the county statuses for those that have an entry in our instance for
        // the given round.
        final Map<String, Map<String,Object>> status = dashboard.get(COUNTY_STATUS);
        final Optional<Map<String,Map<String,Integer>>> countyResults = instance.getRoundCountyResult(rounds+1);

        if(countyResults.isPresent()){
          for(final Map.Entry<String, Map<String, Integer>> entry : countyResults.get().entrySet()){
            final Map<String,Integer> expStatus = entry.getValue();
            final Map<String,Object> countyStatus = status.get(entry.getKey());

            final int ballotsRemaining = Integer.parseInt(countyStatus.get(BALLOTS_REMAINING).toString());
            final int estimatedBallots = Integer.parseInt(countyStatus.get(ESTIMATED_BALLOTS).toString());

            assertEquals(ballotsRemaining, expStatus.get(BALLOTS_REMAINING).intValue());
            assertEquals(estimatedBallots, expStatus.get(ESTIMATED_BALLOTS).intValue());
          }
        }

        // Check county statuses to determine whether the audit needs to go to another round or not.
        for (final Map.Entry<String, Map<String, Object>> entry : status.entrySet()) {
          final int ballotsRemaining = Integer.parseInt(entry.getValue().get(BALLOTS_REMAINING).toString());
          final int estimatedBallots = Integer.parseInt(entry.getValue().get(ESTIMATED_BALLOTS).toString());

          if(ballotsRemaining > 0 || estimatedBallots > 0){
            auditNotFinished = true;
          }
        }

        rounds += 1;
      }

      assertEquals(rounds, instance.getExpectedRounds());
    }
    catch(IOException e){
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }


  /**
   * Utility that returns true if the given file path represents a JSON file.
   * @param filePath Path to the file (as a string).
   * @return true if the given file is a JSON file.
   */
  private static boolean isJSON(final String filePath){
    return filePath.toLowerCase().endsWith(".json");
  }
}
