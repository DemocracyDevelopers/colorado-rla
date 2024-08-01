package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.query.CountyQueries;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static au.org.democracydevelopers.corla.util.testUtils.*;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static us.freeandfair.corla.endpoint.Endpoint.AuthorizationType.STATE;

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
   * The estimate sample sizes endpoint.
   */
  private final EstimateSampleSizes endpoint = new EstimateSampleSizes();

  /**
   * The IRV contest. Pulled from the database.
   */
  private static Contest irvContest;

  /**
   * Contest Results to be returned when mocking the contest counter.
   */
  private static List<ContestResult> mockedContestResults;

  /**
   * Mocked DoS dashboard, mostly so that we can set the risk limit.
   */
  @Mock
  private DoSDashboard mockedDoSDashboard;

  /**
   * Mocked audit info.
   */
  @Mock
  private AuditInfo mockedAuditInfo;

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(createHibernateProperties(postgres));

    final var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");
    ScriptUtils.runInitScript(containerDelegate, "SQL/simple-assertions.sql");
    // ScriptUtils.runInitScript(containerDelegate, "SQL/adams-partway-through-audit.sql");

    var s = Persistence.openSession();
    s.beginTransaction();

    // Set up the audit info (this is not mocked - the DosDashboard comes from the database).
    DoSDashboard doSD = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    // Set the risk limit to 0.03.
    doSD.updateAuditInfo(new AuditInfo(null, null, null,null, BigDecimal.valueOf(0.04)));

    // Set up mocked contest results
    // Matches county name in simple-assertions.
    County oneNEBAssertionCounty = CountyQueries.fromString("1");
    ContestResult pluralityContestResult = new ContestResult("pluralityContest");
    Contest pluralityContest = new Contest("pluralityContest", oneNEBAssertionCounty, ContestType.PLURALITY.toString(), List.of(alice, bob, chuan), 2,1,12);
    pluralityContestResult.addContests(Set.of(pluralityContest));
    pluralityContestResult.addCounties(Set.of(oneNEBAssertionCounty));
    pluralityContestResult.setDilutedMargin(BigDecimal.valueOf(0.12));
    pluralityContestResult.setBallotCount(10000L);

    // Get the IRV Contest for one NEB AssertionCounty from the database.
    Optional<Contest> irvContest =  ContestQueries.forCounty(oneNEBAssertionCounty).stream().findFirst();
    if(irvContest.isEmpty()) {
      throw new RuntimeException("Database setup seems wrong");
    }

    // matches contest name in simple-assertions.
    ContestResult irvContestResult = new ContestResult(irvContest.get().name());
    irvContestResult.addContests(Set.of(irvContest.get()));
    irvContestResult.addCounties(Set.of(oneNEBAssertionCounty));
    irvContestResult.setDilutedMargin(BigDecimal.valueOf(0.01));
    irvContestResult.setBallotCount(20000L);

    mockedContestResults = List.of(pluralityContestResult, irvContestResult);
  }

  /**
   * Init mocks, for authentication and risk limit.
   */
  @BeforeClass
  public void initMocks() throws IOException {
    testUtils.log(LOGGER, "initMocks");

    // Mock successful auth as a state admin.
    MockitoAnnotations.openMocks(this);
    mockAuth("State test 1", 1L, STATE);

    when(mockedDoSDashboard.auditInfo()).thenReturn(mockedAuditInfo);
    when(mockedAuditInfo.riskLimit()).thenReturn(BigDecimal.valueOf(0.03));
  }

  /**
   * A very basic test of proper csv output format for simple mocked data, both IRV and plurality.
   */
  @Test
  @Transactional
  void basicEstimatedSampleSizesPluralityAndIRV() throws IOException {
    testUtils.log(LOGGER, "basicEstimatedSampleSizesPluralityAndIRV");

    // Mock the main class; mock its auth as the mocked Adams county auth.
    try (MockedStatic<Main> mockedMain = Mockito.mockStatic(Main.class);
         MockedStatic<ContestCounter> mockedCounter = Mockito.mockStatic(ContestCounter.class)) {

      mockedMain.when(Main::authentication).thenReturn(auth);
      mockedCounter.when(ContestCounter::countAllContests).thenReturn(mockedContestResults);

      // We seem to need a dummy request to run before.
      final Request request = new SparkRequestStub("", new HashSet<>());
      endpoint.before(request, response);

      endpoint.endpointBody(request, response);

      // FIXME Ballots cast is zero.
      String csv = response.body();
      assertEquals(csv, String.join("\n", List.of(
          "County,Contest Name,Contest Type,Ballots Cast,Total ballots,Diluted Margin,Sample Size",
          "Adams,pluralityContest,PLURALITY,0,10000,0.12,56",
          "Adams,One NEB Assertion Contest,IRV,0,20000,0.32000000,21"
          )));
    }
  }
}
