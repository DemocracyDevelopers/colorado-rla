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

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import au.org.democracydevelopers.corla.model.assertion.Assertion;
import au.org.democracydevelopers.corla.model.assertion.AssertionTests;
import au.org.democracydevelopers.corla.model.assertion.NEBAssertion;
import au.org.democracydevelopers.corla.model.assertion.NENAssertion;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import au.org.democracydevelopers.corla.util.testUtils;
import java.math.BigDecimal;
import java.util.List;
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
   * Mock of a ContestResult for a contest with no assertions.
   */
  @Mock
  private ContestResult doesNotExistContestResult;

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
   * varied values and ensure that the audit itself is contrycted properly.
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
    when(doesNotExistContestResult.getContestName()).thenReturn("Does Not Exist");
    when(doesNotExistContestResult.getDilutedMargin()).thenReturn(BigDecimal.valueOf(0.98));
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

    assert(assertions.get(0) instanceof NEBAssertion);
  }

  /**
   * Create an IRVComparisonAudit for a contest with one NEN assertion stored in the database. This
   * assertion has a diluted margin of 0.32.
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

    assert(assertions.get(0) instanceof NENAssertion);
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
}
