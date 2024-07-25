package au.org.democracydevelopers.corla.endpoint;

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
import us.freeandfair.corla.endpoint.ACVRUpload;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.util.TestClassWithDatabase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static au.org.democracydevelopers.corla.util.testUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static us.freeandfair.corla.Main.GSON;

/**
 * Test upload of IRV audit cvrs. Includes tests of both valid and invalid IRV CVRs, and tests that
 * the interpreted ballots are properly stored in the database
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

    // adams = CountyQueries.fromString("Adams");
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
    String authentication_class = "us.freeandfair.corla.auth.DatabaseAuthentication";
    try {
      adams = new County("Adams", 1L);
      // AuthenticationInterface auth = (AuthenticationInterface)
      //     Class.forName(authentication_class).newInstance();
      // when(Main.authentication()).thenReturn(auth);
      when(auth.authenticatedCounty(any())).thenReturn(adams);
    } catch (Exception e) {
      testUtils.log(LOGGER, "Initiating mocks didn't work.");
      // Do nothing. If auth isn't properly initialized, the tests will fail.
    }
  }

  // Request upload1 = new SparkRequestStub("test");
  Response response = new SparkResponseStub("test2");

  // java.lang.NullPointerException: Cannot invoke "us.freeandfair.corla.auth.AuthenticationInterface.authenticatedCounty(spark.Request)"
  // because the return value of "us.freeandfair.corla.Main.authentication()" is null
  @Test
  void testIfTheTestWorks() throws Exception {

    // Mock the main class; mock its auth as the mocked Adams county auth.
    try (MockedStatic<Main> mockedMain = Mockito.mockStatic(Main.class)) {
      mockedMain.when(Main::authentication).thenReturn(auth);

      // The Audit cvr upload.
      // This has to be constructed as a json string, rather than by constructing and serializing the
      // CVR object(s), because the constructors object to IRV choices with explicit parentheses.
      String acvrAsJson = "{\n" +
          "  \"cvr_id\": 1,\n" +
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
          "    \"ballot_type\": \"ballotType\",\n" +
          "    \"contest_info\": [\n" +
          "      {\n" +
          "        \"contest\": 240503,\n" +
          "        \"comment\": \"A comment\",\n" +
          "        \"consensus\": \"YES\",\n" +
          "        \"choices\": [\n" +
          "          \"Alice(1)\",\n" +
          "          \"Bob(2)\"\n" +
          "        ]\n" +
          "      }\n" +
          "    ]\n" +
          "  },\n" +
          "  \"reaudit\": false,\n" +
          "  \"comment\": \"\",\n" +
          "  \"auditBoardIndex\": -1\n" +
          "}";

      Request acvr = new SparkRequestStub(acvrAsJson);
      String see = uploadEndpoint.endpointBody(acvr, response);
    }
  }
}
