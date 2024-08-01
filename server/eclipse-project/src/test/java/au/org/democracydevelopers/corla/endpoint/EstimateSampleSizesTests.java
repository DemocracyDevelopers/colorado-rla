package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.vote.IRVBallotInterpretation;
import au.org.democracydevelopers.corla.util.SparkRequestStub;
import au.org.democracydevelopers.corla.util.TestClassWithAuth;
import au.org.democracydevelopers.corla.util.TestOnlyQueries;
import au.org.democracydevelopers.corla.util.testUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.HaltException;
import spark.Request;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.endpoint.ACVRUpload;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static au.org.democracydevelopers.corla.util.testUtils.tinyIRV;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.endpoint.Endpoint.AuthorizationType.STATE;
import static us.freeandfair.corla.model.CastVoteRecord.RecordType.AUDITOR_ENTERED;

/**
 * Test sample sizes endpoint.
 * This does not test that it produces the correct _values_ - it just mocks some numbers and checks
 * that the CSV is properly printed.
 */
public class EstimateSampleSizesTests extends TestClassWithAuth {

  private final static Logger LOGGER = LogManager.getLogger(EstimateSampleSizesTests.class);

  /**
   * Container for the mock-up database.
   */
  private final static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * An Audit CVR Upload endpoint to test.
   */
  private final ACVRUpload uploadEndpoint = new ACVRUpload();

  private List<ContestResult> mockedContestResults;

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(createHibernateProperties(postgres));

    final var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    // ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");
    ScriptUtils.runInitScript(containerDelegate, "SQL/simple-assertions.sql");
    ScriptUtils.runInitScript(containerDelegate, "SQL/adams-partway-through-audit.sql");


  }

  /**
   * Init mocks, particularly for authentication.
   */
  @BeforeClass
  public void initMocks() {
    testUtils.log(LOGGER, "initMocks");

    // Mock successful auth as a state admin.
    mockAuth("State test 1", 1L, STATE);

    // Set up mocked contest results
    // Matches county name in simple-assertions.
    County oneNEBAssertionCounty = new County("One NEB Assertion County", 1L);
    ContestResult pluralityContestResult = new ContestResult("pluralityContest");
    pluralityContestResult.addContests(Set.of(new Contest()));
    pluralityContestResult.addCounties(Set.of(oneNEBAssertionCounty));
    // matches contest name in simple-assertions.
    ContestResult irvContestResult = new ContestResult("One NEB Assertion Contest");
    pluralityContestResult.addContests(Set.of(new Contest()));
    pluralityContestResult.addCounties(Set.of(oneNEBAssertionCounty));

    mockedContestResults = List.of(pluralityContestResult, irvContestResult);
  }

  /**
   *
   */
  @Test
  @Transactional
  void testACVRUploadAndStorage() {
    testUtils.log(LOGGER, "testACVRUploadAndStorage");

    // Mock the main class; mock its auth as the mocked Adams county auth.
    try (MockedStatic<Main> mockedMain = Mockito.mockStatic(Main.class);
         MockedStatic<ContestCounter> mockedCounter = Mockito.mockStatic(ContestCounter.class)) {
      mockedMain.when(Main::authentication).thenReturn(auth);
      mockedCounter.when(ContestCounter::countAllContests).thenReturn(mockedContestResults);

      // We seem to need a dummy request to run before.
      final Request request = new SparkRequestStub("", new HashSet<>());
      uploadEndpoint.before(request, response);

      uploadEndpoint.endpointBody(request, response);

      String csv = response.body();
      assertEquals(csv, String.join(",", List.of(
          "County",
          "Contest Name",
          "Contest Type",
          "Ballots Cast",
          "Total ballots",
          "Diluted Margin",
          "Sample Size")));
    }
  }
}
