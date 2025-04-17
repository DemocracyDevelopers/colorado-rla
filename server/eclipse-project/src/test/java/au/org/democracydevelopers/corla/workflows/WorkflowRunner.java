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

import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;
import static java.lang.Math.max;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.COUNTY_AUDIT_COMPLETE;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

/**
 * This workflow runner is designed to execute all JSON workflows present in a specified
 * directory (defined in the "pathToInstances" member). These JSON workflows define a complete
 * audit to undertake, along with expected results. The workflow runner will execute all stages of
 * the audit: CVR and manifest uploads; defining the audit; selecting contests to target;
 * uploading audited ballots; reauditing ballots; and executing rounds until there are no further
 * ballots to sample. The workflow ends when the audit ends. Reporting is not tested in these workflows.
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
   * Returns a list of parameter lists to supply to the runWorkflow test method in this class.
   * Each parameter list contains one element -- a path to the workflow JSON instance to run.
   * @return A list of test parameter lists as a 2D array of objects.
   */
  @DataProvider(name="workflow-provider")
  public Object[][] supplyWorkflowPaths(){
    final String prefix = "[supplyWorkflowPaths]";
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

    // Convert list of paths into an array of parameter arrays
    Object[][] params = new Object[pathList.size()][1];
    for(int i = 0; i < pathList.size(); ++i){
      params[i][0] = pathList.get(i);
    }
    return params;
  }

  /**
   * Given a JSON file defining an audit workflow (CVRs, Manifests, which CVRs to replace with
   * phantoms, which ballots to treat as phantoms, expected diluted margins and sample sizes,
   * which ballots to simulate discrepancies for, and expected end of round states ...), run
   * the test audit and verify that the expected outcomes arise.
   * @param pathToInstance Path to the JSON workflow instance defining the test.
   * @throws InterruptedException
   */
  @Test(dataProvider = "workflow-provider")
  public void runInstance(final Path pathToInstance) throws InterruptedException {
    final String prefix = "[runInstance] " + pathToInstance;

    try {
      // PostgreSQLContainer<?> postgres = setupDatabaseAndRunMain(pathToInstance.getFileName().toString());
      final PostgreSQLContainer<?> postgres = TestClassWithDatabase.createTestContainer();
      runMainAndInitializeDB(pathToInstance.getFileName().toString(), Optional.of(postgres));

      // Convert data in the JSON workflow file to a workflow Instance.
      ObjectMapper toJson = new ObjectMapper();
      final Instance instance = toJson.readValue(pathToInstance.toFile(), Instance.class);

      // Upload all the manifests and CVRs as defined in the Instance.
      final List<String> cvrs = instance.getCVRs();
      final List<String> manifests = instance.getManifests();

      assertEquals(cvrs.size(), manifests.size());

      for(int i = 0; i < manifests.size(); ++i){
        uploadCounty(i+1, MANIFEST_FILETYPE, manifests.get(i), manifests.get(i) + ".sha256sum");
      }

      // Upload all the CVRs. The order is important because it's an error to try to import a
      // manifest while the CVRs are being read.
      for(int i = 0; i < cvrs.size()  ; ++i){
        uploadCounty(i+1, CVR_FILETYPE, cvrs.get(i), cvrs.get(i) + ".sha256sum");
      }

      final int countyCount = cvrs.size();
      final Set<Integer> countyIDs = IntStream.rangeClosed(1,countyCount).boxed().collect(Collectors.toSet());

      // Wait while the CVRs (and manifests) are uploaded.
      assertTrue(uploadSuccessfulWithin(600, countyIDs, CVR_JSON));
      assertTrue(uploadSuccessfulWithin(20, countyIDs, MANIFEST_JSON));

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
      assertEquals(countyCount,
          dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());

      // Check that the seed is still null.
      assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
      assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

      makeAssertionData(Optional.of(postgres), instance.getSQLs());

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
        if(!sampleSizes.containsKey(c)){
          throw new RuntimeException("When verifying sample sizes, the targeted contest name "
              + c + " does not exist in sample sizes data structure returned by CORLA. " +
              "Likely incorrectly specified contest name in workflow JSON.");
        }
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

        dashboard = getDoSDashBoardRefreshResponse();

        final Map<String, Map<String,Object>> roundStartStatus = dashboard.get(COUNTY_STATUS);

        // Log in as each county whose audit is not yet complete, and audit all ballots in sample.
        List<String> countiesWithAudits = new ArrayList<>();
        List<TestAuditSession> sessions = new ArrayList<>();
        for (final int cty : countyIDs) {
          final String ctyID = String.valueOf(cty);
          final String asmState = roundStartStatus.get(ctyID).get(ASM_STATE).toString();
          if(asmState.equalsIgnoreCase(COUNTY_AUDIT_COMPLETE.toString()))
            continue;

          sessions.add(countyAuditInitialise(cty));
          countiesWithAudits.add(ctyID);
        }

        // ACVR uploads for each county. Cannot run in parallel as corla does not like
        // simultaneous database accesses.
        for (final TestAuditSession entry : sessions) {
          auditCounty(rounds+1, entry, instance);
        }

        // Audit board sign off for each county.
        for (final TestAuditSession entry : sessions) {
          countySignOffLogout(entry);
        }

        // Check that there are no more ballots to sample across all counties
        dashboard = getDoSDashBoardRefreshResponse();
        final Map<String,Integer> statusOptBallotsToAudit = dashboard.get(OPTIMISTIC_BALLOTS);
        final Map<String, Map<String,Integer>> discrepancies = dashboard.get(DISCREPANCY_COUNT);

        auditNotFinished = false;

        // Verify that the result of this round is what we expected in terms of number of
        // estimated and optimistic ballots to audit for each contest mentioned in the associated
        // field in the instance. Also test that the resulting discrepancy counts are as expected.
        final Optional<Map<String,Map<String,Integer>>> results = instance.getRoundContestResult(rounds+1);
        if(results.isPresent()) {
          final Map<String, Map<String, Integer>> roundResults = results.get();

          for (final String contestName : roundResults.keySet()) {
            if(!contestToDBID.containsKey(contestName)){
              throw new RuntimeException("When verifying round results, the contest name "
                  + contestName + " does not exist in contest-database ID map. " +
                  "Likely incorrectly specified contest name in workflow JSON.");
            }

            final String dbID = contestToDBID.get(contestName);
            final Map<String, Integer> contestResult = roundResults.get(contestName);

            final int oneOverCount = contestResult.get(ONE_OVER_COUNT);
            final int oneUnderCount = contestResult.get(ONE_UNDER_COUNT);
            final int twoOverCount = contestResult.get(TWO_OVER_COUNT);
            final int twoUnderCount = contestResult.get(TWO_UNDER_COUNT);
            final int otherCount = contestResult.get(OTHER_COUNT);

            if(!discrepancies.containsKey(dbID)){
              throw new RuntimeException("Likely incorrectly specified contest name (" +
                  contestName + ") in workflow JSON.");
            }
            final Map<String, Integer> contestDiscrepancies = discrepancies.get(dbID);
            assertEquals(contestDiscrepancies.get(ONE_OVER).intValue(), oneOverCount);
            assertEquals(contestDiscrepancies.get(ONE_UNDER).intValue(), oneUnderCount);
            assertEquals(contestDiscrepancies.get(OTHER).intValue(), otherCount);
            assertEquals(contestDiscrepancies.get(TWO_OVER).intValue(), twoOverCount);
            assertEquals(contestDiscrepancies.get(TWO_UNDER).intValue(), twoUnderCount);
          }
        }

        final int maxOptimistic = Collections.max(statusOptBallotsToAudit.values());

        final Map<String, Map<String,Object>> status = dashboard.get(COUNTY_STATUS);
        final Optional<Map<String,Map<String,Integer>>> countyResults = instance.getRoundCountyResult(rounds+1);

        int maxBallotsRemaining = 0;
        if(countyResults.isPresent()){
          for(final Map.Entry<String, Map<String, Integer>> entry : countyResults.get().entrySet()){
            if(!countiesWithAudits.contains(entry.getKey()))
              continue;

            final Map<String,Integer> expStatus = entry.getValue();
            final Map<String,Object> countyStatus = status.get(entry.getKey());

            final int ballotsRemaining = Integer.parseInt(countyStatus.get(BALLOTS_REMAINING).toString());
            final int estimatedBallots = Integer.parseInt(countyStatus.get(ESTIMATED_BALLOTS).toString());

            assertEquals(ballotsRemaining, expStatus.get(BALLOTS_REMAINING).intValue());
            assertEquals(estimatedBallots, expStatus.get(ESTIMATED_BALLOTS).intValue());

            maxBallotsRemaining = max(ballotsRemaining, maxBallotsRemaining);
          }
        }

        if(maxOptimistic > 0 || maxBallotsRemaining > 0){
          auditNotFinished = true;
        }

        rounds += 1;
      }

      // Check that the number of rounds completed is as expected. Note that this may
      // not be specified in the instance.
      final Integer expectedRounds = instance.getExpectedRounds();
      if(expectedRounds != null){
        assertEquals(expectedRounds.intValue(), rounds);
      }

      // Check that the number of audited ballots for targeted contests meets expectations.
      final Map<String,Integer> expectedAuditedBallots = instance.getExpectedAuditedBallots();
      final List<String> content = getReportAsCSV("contest");
      for(final String line : content){
        final String[] tokens = line.split(",");
        for(final String contest : targets.keySet()){
          if(tokens[0].equalsIgnoreCase(contest)){
            final int expected = expectedAuditedBallots.get(contest);
            assert(Integer.parseInt(tokens[9]) >= expected);
            break;
          }
        }
      }

      postgres.stop();
    }
    catch(IOException e){
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }


  /** Load additional SQL data (this is data that we want to add after we have
   * CVRs, manifests, etc loaded for each county). This will mostly be used to load
   * assertion data into the database, simulating a call to the raire-service.
   */
  protected void makeAssertionData(final Optional<PostgreSQLContainer<?>> postgresOpt, final List<String> SQLfiles) {
    assertTrue(postgresOpt.isPresent());
    for (final String s : SQLfiles) {
      TestClassWithDatabase.runSQLSetupScript(postgresOpt.get(), s);
    }
  }

  /**
   * Set up main's configuration file to match the given postgres container, then run main and
   * load the colorado-rla init script into the database.
   * This loads in the properties in resources/test.properties, then overwrites the database
   * location with the one in the newly-created test container.
   * @param testName Name of test instance.
   * @param postgresOpt The PostgreSQL container to use.
   */
  @Override
  protected void runMainAndInitializeDB(String testName, Optional<PostgreSQLContainer<?>> postgresOpt) {
    assertTrue(postgresOpt.isPresent());
    final PostgreSQLContainer<?> postgres = postgresOpt.get();

    Properties config = loadProperties();
    postgres.start();
    config.setProperty("hibernate.url", postgres.getJdbcUrl());
    Persistence.setProperties(config);
    TestClassWithDatabase.runSQLSetupScript(postgres, "SQL/co-counties.sql");

    runMain(config, testName);

    Persistence.beginTransaction();
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
