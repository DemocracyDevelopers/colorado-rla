package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.assertion.Assertion;
import au.org.democracydevelopers.corla.model.assertion.NEBAssertion;
import au.org.democracydevelopers.corla.model.vote.IRVBallotInterpretation;
import au.org.democracydevelopers.corla.query.AssertionQueries;
import au.org.democracydevelopers.corla.util.SparkRequestStub;
import au.org.democracydevelopers.corla.util.SparkResponseStub;
import au.org.democracydevelopers.corla.util.testUtils;
import org.mockito.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.auth.AuthenticationInterface;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.endpoint.ACVRUpload;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.util.TestClassWithDatabase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static us.freeandfair.corla.model.Administrator.AdministratorType.COUNTY;

/**
 * Test upload of IRV audit cvrs. Includes tests of both valid and invalid IRV CVRs, and tests that
 * the interpreted ballots are properly stored in the database.
 * TODO test valid and invalid IRV ACVR Uploads
 * test that plurality uploads are still good
 * test reaudits, both IRV and plurality (can they be mixed in one CVR?)
 */
public class ACVRUploadTests extends TestClassWithDatabase {

  private static final Logger LOGGER = LogManager.getLogger(ACVRUploadTests.class);

  /**
   * Container for the mock-up database.
   */
  private final static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * An Audit CVR Upload endpoint to test.
   */
  private final ACVRUpload uploadEndpoint = new ACVRUpload();

  /**
   *
   */
  private static County adams;

  @Mock
  private AuthenticationInterface auth;

  @Mock
  private ThreadLocal<List<LogEntry>> mockedAsm;

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(createHibernateProperties(postgres));

    var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");
    ScriptUtils.runInitScript(containerDelegate, "SQL/corla-three-candidates-ten-votes-inconsistent-types.sql");
    ScriptUtils.runInitScript(containerDelegate, "SQL/adams-partway-through-audit.sql");

  }

  /**
   * Init mocked objects, which is a little complicated because we have to simulate auth for a
   * county's audit board in the ACVR endpoint.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);

    // Mock successful auth as Adams county. No need to mock the CountyDashboard retrieval from
    // the database, because that is loaded in via co-counties.sql.
    try {
      adams = new County("Adams", 1L);
      when(auth.authenticatedCounty(any())).thenReturn(adams);
      when(auth.secondFactorAuthenticated(any())).thenReturn(true);
      when(auth.authenticatedAdministrator(any())).thenReturn(new Administrator("countyadmin1",
          COUNTY, "Adams County", adams));
      when(auth.authenticatedAs(any(), any(), any())).thenReturn(true);
      when(mockedAsm.get()).thenReturn(new ArrayList<>());
    } catch (Exception e) {
      testUtils.log(LOGGER, "Initiating mocks didn't work.");
      // Do nothing. If auth isn't properly initialized, the tests will fail.
    }
  }

  private final Response response = new SparkResponseStub();

  /**
   * Basic test of proper functioning for a valid audit CVR. Check that it is accepted and that the
   * right records for CVRContestInfo and CVRAuditInfo are stored in the database.
   * @throws Exception
   */
  @Test
  void testIfTheTestWorks() throws Exception {
    // Mock the main class; mock its auth as the mocked Adams county auth.
    try (MockedStatic<Main> mockedMain = Mockito.mockStatic(Main.class)) {
      mockedMain.when(Main::authentication).thenReturn(auth);

      // The Audit cvr upload.
      // This has to be constructed as a json string, rather than by constructing and serializing the
      // CVR object(s), because the constructors reject IRV choices with explicit parentheses.
      // CVR IDs are in corla-three-candidates-ten-votes-inconsistent-types.sql.
      String acvrAsJson = "{\n" +
          "  \"cvr_id\": 240509,\n" +
          "  \"audit_cvr\": {\n" +
          "    \"record_type\": \"AUDITOR_ENTERED\",\n" +
          "    \"county_id\": 1,\n" +
          "    \"cvr_number\": 1,\n" +
          "    \"sequence_number\": 1,\n" +
          "    \"scanner_id\": 1,\n" +
          "    \"batch_id\": \"1\",\n" +
          "    \"record_id\": 1,\n" +
          "    \"imprinted_id\": \"1-1-1\",\n" +
          "    \"uri\": \"acvr:1:1-1-1\",\n" +
          "    \"ballot_type\": \"Ballot 1 - Type 1\",\n" +
          "    \"contest_info\": [\n" +
          "      {\n" +
          "        \"contest\": 240503,\n" +
          "        \"comment\": \"A comment\",\n" +
          "        \"consensus\": \"YES\",\n" +
          "        \"choices\": [\n" +
          "          \"Bob(1)\",\n" +
          "          \"Chuan(2)\"\n" +
          "        ]\n" +
          "      }\n" +
          "    ]\n" +
          "  },\n" +
          "  \"reaudit\": false,\n" +
          "  \"comment\": \"\",\n" +
          "  \"auditBoardIndex\": -1\n" +
          "}";

      // Set up the endpoint with mocked auth and start the audit round.
      Request request = new SparkRequestStub(acvrAsJson, new HashSet<>());
      final CountyDashboard cdb =
          Persistence.getByID(Main.authentication().authenticatedCounty(request).id(),
              CountyDashboard.class);
      ComparisonAuditController.startRound(cdb, null,
          List.of(240509L,240510L,240511L,240512L,240513L),
          List.of(240509L,240510L,240511L,240512L,240513L));
      uploadEndpoint.before(request, response);

      // This flush is necessary to ensure the audit round data is present in the db when the
      // endpoint runs.
      Persistence.flush();

      // This is the actual test - upload the audit CVR to the endpoint; check that the right
      // database records result.
      uploadEndpoint.endpointBody(request, response);

      Persistence.flush();

      // There should now be two CVRs with cvr_id 240509: the original uploaded one, and the audit one.
      // List<CastVoteRecord> cvrs = CastVoteRecordQueries.get(List.of(240509L));
      List<CastVoteRecord> cvrs = CastVoteRecordQueries.getMatching(1L, CastVoteRecord.RecordType.UPLOADED).toList();
      List<CastVoteRecord> acvrs = CastVoteRecordQueries.getMatching(1L, CastVoteRecord.RecordType.AUDITOR_ENTERED).toList();
      assertEquals(cvrs.size(), 10);
      assertEquals(acvrs.size(), 1);
      Set<CastVoteRecord.RecordType> recordTypes
          = cvrs.stream().map(CastVoteRecord::recordType).collect(Collectors.toSet());

      // There don't seem to be any queries for CVRAuditInfo unfortunately.
      // We therefore check that the right data has gone in via the rather indirect method of checking
      // that appropriate discrepancies are recorded for each assertion.
      // In this case, the CVR is (A,B,C) and the audit CVR is (B,C), so both assertions should have
      // a two-vote overstatement (and no other discrepancies).
      List<Assertion> assertions = AssertionQueries.matching("TinyExample1");
      assertTrue(assertions.get(0).getDiscrepancy(240509).isPresent());
      assertTrue(assertions.get(1).getDiscrepancy(240509).isPresent());
      assertEquals(2, assertions.get(0).getDiscrepancy(240509).getAsInt());
      assertEquals(2, assertions.get(1).getDiscrepancy(240509).getAsInt());

    }
  }
}
