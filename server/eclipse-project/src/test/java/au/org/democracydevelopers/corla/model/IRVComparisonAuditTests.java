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

package au.org.democracydevelopers.corla.model;

import static au.org.democracydevelopers.corla.util.testUtils.log;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import au.org.democracydevelopers.corla.model.assertion.Assertion;
import au.org.democracydevelopers.corla.model.assertion.AssertionTests;
import au.org.democracydevelopers.corla.model.assertion.NEBAssertion;
import au.org.democracydevelopers.corla.model.assertion.NENAssertion;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import au.org.democracydevelopers.corla.util.testUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditStatus;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.persistence.Persistence;

/**
 * This class contains tests for the functionality present in IRVComparisonAudit.
 */
public class IRVComparisonAuditTests extends TestClassWithDatabase {

  private static final Logger LOGGER = LogManager.getLogger(IRVComparisonAuditTests.class);

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * Mock of a ContestResult for the contest 'One NEB Assertion Contest'.
   */
  @Mock
  private ContestResult oneNEBContestResult;

  /**
   * Mock of a ContestResult for the contest 'One NEN Assertion Contest'.
   */
  @Mock
  private ContestResult oneNENContestResult;

  /**
   * Mock of a ContestResult for the contest 'One NEN NEB Assertion Contest'.
   */
  @Mock
  private ContestResult oneNENNEBContestResult;

  /**
   * Mock of a ContestResult for the contest 'Multi-County Contest 1'.
   */
  @Mock
  private ContestResult multiCountyContestResult;

  /**
   * Mock of a ContestResult for a contest with no assertions.
   */
  @Mock
  private ContestResult doesNotExistContestResult;

  /**
   * Mock of a ContestResult for the contest 'Test Estimation NEB Only'.
   */
  @Mock
  private ContestResult testEstimationNEBOnly;

  /**
   * Mock of a ContestResult for the contest 'Test Estimation NEN Only'.
   */
  @Mock
  private ContestResult testEstimationNENOnly;

  /**
   * Mock of a ContestResult for the contest 'Test Estimation Mixed Assertions'.
   */
  @Mock
  private ContestResult testEstimationMixedAssertions;

  /**
   * Start the test container and establish persistence properties before the first test.
   */
  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(createHibernateProperties(postgres));

    var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(containerDelegate, "SQL/simple-assertions.sql");
  }

  /**
   * Initialise mocked objects prior to the first test. Note that the diluted margin
   * returned by ContestResult's for IRV will not have a sensible value, and it will
   * not be used for IRV computations. For testing purposes, we should set it with
   * varied values and ensure that the audit itself is contructed properly.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);
    when(oneNENContestResult.getContestName()).thenReturn("One NEN Assertion Contest");
    when(oneNENContestResult.getDilutedMargin()).thenReturn(BigDecimal.ZERO);
    when(oneNEBContestResult.getContestName()).thenReturn("One NEB Assertion Contest");
    when(oneNEBContestResult.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.10));
    when(oneNENNEBContestResult.getContestName()).thenReturn("One NEN NEB Assertion Contest");
    when(oneNENNEBContestResult.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.03));
    when(multiCountyContestResult.getContestName()).thenReturn("Multi-County Contest 1");
    when(multiCountyContestResult.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.001));
    when(doesNotExistContestResult.getContestName()).thenReturn("Does Not Exist");
    when(doesNotExistContestResult.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.98));
    when(testEstimationNEBOnly.getContestName()).thenReturn("Test Estimation NEB Only");
    when(testEstimationNEBOnly.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.01));
    when(testEstimationNENOnly.getContestName()).thenReturn("Test Estimation NEN Only");
    when(testEstimationNENOnly.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.01));
    when(testEstimationMixedAssertions.getContestName()).thenReturn("Test Estimation Mixed Assertions");
    when(testEstimationMixedAssertions.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.01));
  }

  /**
   * After all test have run, stop the test container.
   */
  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  /**
   * Create an IRVComparisonAudit for a contest with no assertions in the database.
   */
  @Test
  public void testCreateIRVAuditNoAssertions(){
    testUtils.log(LOGGER, "testCreateIRVAuditNoAssertions");
    IRVComparisonAudit ca = new IRVComparisonAudit(doesNotExistContestResult, AssertionTests.riskLimit3,
        AuditReason.OPPORTUNISTIC_BENEFITS);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.OPPORTUNISTIC_BENEFITS,
        AuditStatus.NOT_AUDITABLE, 0);

    assertEquals(0, ca.optimisticSamplesToAudit().intValue());
    assertEquals(0, ca.estimatedSamplesToAudit().intValue());

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(0, assertions.size());
  }

  /**
   * Create an IRVComparisonAudit for a contest with one NEB assertion stored in the database. This
   * assertion has a diluted margin of 0.32.
   */
  @Test
  public void testCreateIRVAuditOneNEBAssertion(){
    testUtils.log(LOGGER, "testCreateIRVAuditOneNEBAssertion");
    IRVComparisonAudit ca = new IRVComparisonAudit(oneNEBContestResult, AssertionTests.riskLimit3,
        AuditReason.COUNTY_WIDE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.COUNTY_WIDE_CONTEST,
        AuditStatus.NOT_STARTED, 0.32);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(1, assertions.size());

    checkNEBAssertion(assertions.get(0), "Alice", "Bob", 0, 0, 0,
        0, 0, 23, 23, 1, Map.of(), 0.32);
  }



  /**
   * Create an IRVComparisonAudit for a contest with one NEN assertion stored in the database. This
   * assertion has a diluted margin of 0.12.
   */
  @Test
  public void testCreateIRVAuditOneNENAssertion(){
    testUtils.log(LOGGER, "testCreateIRVAuditOneNENAssertion");
    IRVComparisonAudit ca = new IRVComparisonAudit(oneNENContestResult, AssertionTests.riskLimit3,
        AuditReason.GEOGRAPHICAL_SCOPE);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.GEOGRAPHICAL_SCOPE,
        AuditStatus.NOT_STARTED, 0.12);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(1, assertions.size());

    checkNENAssertion(assertions.get(0), "Alice", "Charlie", List.of("Alice","Charlie",
            "Diego","Bob"), 0, 0, 0, 0, 0, 61,
        61, 1, Map.of(), 0.12);
  }

  /**
   * Create an IRVComparisonAudit for a contest with one NEN and one NEB assertion stored in the
   * database. The smallest diluted margin across these assertions is 0.1.
   */
  @Test
  public void testCreateIRVAuditOneNENNEBAssertion(){
    testUtils.log(LOGGER, "testCreateIRVAuditOneNENNEBAssertion");
    IRVComparisonAudit ca = new IRVComparisonAudit(oneNENNEBContestResult, AssertionTests.riskLimit3,
        AuditReason.COUNTY_WIDE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.COUNTY_WIDE_CONTEST,
        AuditStatus.NOT_STARTED, 0.1);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(2, assertions.size());

    checkNEBAssertion(assertions.get(0), "Amanda", "Liesl", 0, 0,
        0, 0, 0, 73, 73, 1, Map.of(), 0.1);

    checkNENAssertion(assertions.get(1), "Amanda", "Wendell", List.of("Liesl","Wendell",
            "Amanda"), 0, 0, 0, 0, 0, 15,
        15, 1, Map.of(), 0.5);
  }

  /**
   * Create an IRVComparisonAudit for a multi-county contest with two NEBs and one NEN stored in the
   * database. The smallest diluted margin across these assertions is 0.1.
   */
  @Test
  public void testCreateIRVAuditMultiCountyContest(){
    testUtils.log(LOGGER, "testCreateIRVAuditMultiCountyContest");
    IRVComparisonAudit ca = new IRVComparisonAudit(multiCountyContestResult,
        AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST,
        AuditStatus.NOT_STARTED, 0.001);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(3, assertions.size());

    checkNEBAssertion(assertions.get(0), "Charlie C. Chaplin", "Alice P. Mangrove",
        0, 0, 0, 0, 0, 729, 729,
        1, Map.of(), 0.01);

    checkNEBAssertion(assertions.get(1), "Alice P. Mangrove", "Al (Bob) Jones",
        0, 0, 0, 0, 0, 105, 105,
        1, Map.of(), 0.07);

    checkNENAssertion(assertions.get(2), "Alice P. Mangrove", "West W. Westerson",
        List.of("West W. Westerson","Alice P. Mangrove"), 0, 0, 0,
        0, 0, 7287, 7287, 1, Map.of(), 0.001);
  }

  /**
   * Test the IRVComparisonAudit::initialSamplesToAudit() method for a fresh IRVComparisonAudit
   * containing only NEB assertions.
   */
  @Test
  public void testInitialOptimisticSampleSizeNEBOnly(){
    log(LOGGER, "testInitialOptimisticSampleSizeNEBOnly");
    IRVComparisonAudit ca = new IRVComparisonAudit(testEstimationNEBOnly,
        AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST,
        AuditStatus.NOT_STARTED, 0.01);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(4, assertions.size());

    checkNEBAssertion(assertions.get(0), "A", "B",
        0, 0, 0, 0, 0, 729, 729,
        1, Map.of(), 0.01);

    checkNEBAssertion(assertions.get(1), "B", "C",
        0, 0, 0, 0, 0, 459, 459,
        1, Map.of(), 0.0159);

    checkNEBAssertion(assertions.get(2), "F", "G",
        0, 0, 0, 1, 0, 48, 48,
        1, Map.of(1L, -2), 0.12345);

    checkNEBAssertion(assertions.get(3), "H", "I",
        1, 1, 0, 0, 0, 47, 47,
        1, Map.of(1L, 1, 2L, 2), 0.33247528);

    assertEquals(729, ca.initialSamplesToAudit());
  }

  /**
   * Test the IRVComparisonAudit::initialSamplesToAudit() method for a fresh IRVComparisonAudit
   * containing only NEN assertions.
   */
  @Test
  public void testInitialOptimisticSampleSizeNENOnly(){
    log(LOGGER, "testInitialOptimisticSampleSizeNENOnly");
    IRVComparisonAudit ca = new IRVComparisonAudit(testEstimationNENOnly,
        AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST,
        AuditStatus.NOT_STARTED, 0.01);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(4, assertions.size());

    checkNENAssertion(assertions.get(0), "A", "B", List.of("A","B"),
        0, 0, 0, 0, 0, 729, 729,
        1, Map.of(), 0.01);

    checkNENAssertion(assertions.get(1), "B", "C", List.of("B","C"),
        0, 0, 0, 0, 0, 459, 459,
        1, Map.of(), 0.0159);

    checkNENAssertion(assertions.get(2), "F", "G", List.of("F","G"),
        0, 0, 0, 1, 0, 48, 48,
        1, Map.of(1L, -2), 0.12345);

    checkNENAssertion(assertions.get(3), "H", "I", List.of("H","I"),
        1, 1, 0, 0, 0, 47, 47,
        1, Map.of(1L, 1, 2L, 2), 0.33247528);

    assertEquals(729, ca.initialSamplesToAudit());
  }

  /**
   * Test the IRVComparisonAudit::initialSamplesToAudit() method for a fresh IRVComparisonAudit
   * containing only NEN assertions.
   */
  @Test
  public void testInitialOptimisticSampleSizeMixedAssertions(){
    log(LOGGER, "testInitialOptimisticSampleSizeMixedAssertions");
    IRVComparisonAudit ca = new IRVComparisonAudit(testEstimationMixedAssertions,
        AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST);

    checkIRVComparisonAudit(ca, AssertionTests.riskLimit3, AuditReason.CLOSE_CONTEST,
        AuditStatus.NOT_STARTED, 0.01);

    final List<Assertion> assertions = ca.getAssertions();
    assertEquals(8, assertions.size());

    checkNEBAssertion(assertions.get(0), "A", "B",
        0, 0, 0, 0, 0, 729, 729,
        1, Map.of(), 0.01);

    checkNEBAssertion(assertions.get(1), "B", "C",
        0, 0, 0, 0, 0, 459, 459,
        1, Map.of(), 0.0159);

    checkNEBAssertion(assertions.get(2), "F", "G",
        0, 0, 0, 1, 0, 48, 48,
        1, Map.of(1L, -2), 0.12345);

    checkNEBAssertion(assertions.get(3), "H", "I",
        1, 1, 0, 0, 0, 47, 47,
        1, Map.of(1L, 1, 2L, 2), 0.33247528);

    checkNENAssertion(assertions.get(4), "A", "B", List.of("A","B"),
        0, 0, 0, 0, 0, 729, 729,
        1, Map.of(), 0.01);

    checkNENAssertion(assertions.get(5), "B", "C", List.of("B","C"),
        0, 0, 0, 0, 0, 459, 459,
        1, Map.of(), 0.0159);

    checkNENAssertion(assertions.get(6), "F", "G", List.of("F","G"),
        0, 0, 0, 1, 0, 48, 48,
        1, Map.of(1L, -2), 0.12345);

    checkNENAssertion(assertions.get(7), "H", "I", List.of("H","I"),
        1, 1, 0, 0, 0, 47, 47,
        1, Map.of(1L, 1, 2L, 2), 0.33247528);

    assertEquals(729, ca.initialSamplesToAudit());
  }

  /**
   * A method to check that the given IRVComparisonAudit has been initialised properly.
   * @param ca IRVComparisonAudit to check.
   * @param risk Risk limit of the audit.
   * @param reason Reason for the audit.
   * @param status Current status of the audit.
   * @param dilutedMargin Diluted margin of the contest being audited.
   */
  private void checkIRVComparisonAudit(final IRVComparisonAudit ca, final BigDecimal risk,
      final AuditReason reason, final AuditStatus status, double dilutedMargin){

    assertEquals(status, ca.auditStatus());
    assertEquals(reason, ca.auditReason());
    assertEquals(0, ca.getAuditedSampleCount().intValue());

    assertEquals(0, ca.disagreementCount());
    assertEquals(0, ca.getOverstatements().intValue());
    assertEquals(0, ca.discrepancyCount(0));
    assertEquals(0, ca.discrepancyCount(1));
    assertEquals(0, ca.discrepancyCount(2));
    assertEquals(0, ca.discrepancyCount(-1));
    assertEquals(0, ca.discrepancyCount(-2));

    assertEquals(0, testUtils.doubleComparator.compare(
        risk.doubleValue(), ca.getRiskLimit().doubleValue()));

    assertEquals(0, testUtils.doubleComparator.compare(
        dilutedMargin, ca.getDilutedMargin().doubleValue()));
  }

  /**
   * Check that the given NEB assertion has the given characteristics.
   * @param a Assertion to check.
   * @param winner Expected winner of the assertion.
   * @param loser Expected loser of the assertion.
   * @param oneOver Expected number of one vote overstatements.
   * @param twoOver Expected number of two vote overstatements.
   * @param oneUnder Expected number of one vote understatements.
   * @param twoUnder Expected number of two vote understatements.
   * @param other Expected number of "other" discrepancies.
   * @param optimistic Expected optimistic sample size.
   * @param estimated Expected estimated sample size.
   * @param risk Expected current risk of the assertion.
   * @param cvrDiscrepancy Expected CVR ID-discrepancy map.
   * @param dilutedMargin Expected diluted margin.
   */
  private void checkNEBAssertion(final Assertion a, final String winner, final String loser,
      int oneOver, int twoOver, int oneUnder, int twoUnder, int other, int optimistic,
      int estimated, double risk, final Map<Long,Integer> cvrDiscrepancy, double dilutedMargin){

    assert(a instanceof NEBAssertion);

    final String expected = String.format("%s NEB %s: oneOver = %d; two Over = %d; oneUnder = %d, " +
            "twoUnder = %d; other = %d; optimistic = %d; estimated = %d; risk %f.", winner, loser,
        oneOver, twoOver, oneUnder, twoUnder, other, optimistic, estimated,
        BigDecimal.valueOf(risk).setScale(Assertion.RISK_DECIMALS, RoundingMode.HALF_UP));

    assertEquals(expected, a.getDescription());
    assertEquals(cvrDiscrepancy, a.getCvrDiscrepancy());

    assertEquals(0, testUtils.doubleComparator.compare(dilutedMargin,
        a.getDilutedMargin().doubleValue()));
  }

  /**
   * Check that the given NEN assertion has the given characteristics.
   * @param a Assertion to check.
   * @param winner Expected winner of the assertion.
   * @param loser Expected loser of the assertion.
   * @param continuing Expected list of assumed continuing candidates.
   * @param oneOver Expected number of one vote overstatements.
   * @param twoOver Expected number of two vote overstatements.
   * @param oneUnder Expected number of one vote understatements.
   * @param twoUnder Expected number of two vote understatements.
   * @param other Expected number of "other" discrepancies.
   * @param optimistic Expected optimistic sample size.
   * @param estimated Expected estimated sample size.
   * @param risk Expected current risk of the assertion.
   * @param cvrDiscrepancy Expected CVR ID-discrepancy map.
   * @param dilutedMargin Expected diluted margin.
   */
  private void checkNENAssertion(final Assertion a, final String winner, final String loser,
      final List<String> continuing, int oneOver, int twoOver, int oneUnder, int twoUnder, int other,
      int optimistic, int estimated, double risk, final Map<Long,Integer> cvrDiscrepancy, double dilutedMargin){

    assert(a instanceof NENAssertion);

    final String expected = String.format("%s NEN %s assuming (%s) are continuing: oneOver = %d; " +
            "two Over = %d; oneUnder = %d, twoUnder = %d; other = %d; optimistic = %d; estimated = %d; " +
            "risk %f.", winner, loser, continuing, oneOver, twoOver, oneUnder, twoUnder, other,
            optimistic, estimated, BigDecimal.valueOf(risk).setScale(Assertion.RISK_DECIMALS,
            RoundingMode.HALF_UP));

    assertEquals(expected, a.getDescription());
    assertEquals(cvrDiscrepancy, a.getCvrDiscrepancy());

    assertEquals(0, testUtils.doubleComparator.compare(dilutedMargin,
        a.getDilutedMargin().doubleValue()));
  }
}
