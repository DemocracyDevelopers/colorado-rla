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

package au.org.democracydevelopers.corla.model.assertion;

import static au.org.democracydevelopers.corla.model.assertion.AssertionTests.TC;
import static au.org.democracydevelopers.corla.model.assertion.AssertionTests.countsEqual;
import static au.org.democracydevelopers.corla.util.testUtils.log;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;


/**
 * A suite of tests to verify the functionality of NEBAssertion objects. This includes:
 * -- Testing of optimistic sample size.
 * -- Testing of estimated sample size.
 * -- Recording of a pre-computed discrepancy.
 * -- Removal of a pre-recorded discrepancy.
 * -- Scoring of NEB assertions.
 * -- Computation of discrepancies for NEB assertions.
 * Refer to the Guide to RAIRE for details on how NEB assertions are scored, and how
 * discrepancies are computed.
 */
public class NEBAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NEBAssertionTests.class);

  /**
   * Establish a mocked CVRContestInfo for use in testing Assertion scoring.
   */
  @Mock
  private CVRContestInfo cvrInfo;

  /**
   * Mocked CVRContestInfo representing the vote "A", "B", "C", "D".
   */
  @Mock
  private CVRContestInfo ABCD;

  /**
   * Mocked CVRContestInfo representing the vote "B", "A", "C", "D".
   */
  @Mock
  private CVRContestInfo BACD;

  /**
   * Mocked CVRContestInfo representing a blank vote.
   */
  @Mock
  private CVRContestInfo blank;

  /**
   * Mocked CVRContestInfo representing the vote "A".
   */
  @Mock
  private CVRContestInfo A;

  /**
   * Mocked CVRContestInfo representing the vote "B".
   */
  @Mock
  private CVRContestInfo B;

  /**
   * Mocked CastVoteRecord to represent a CVR.
   */
  @Mock
  private CastVoteRecord cvr;

  /**
   * Mocked CastVoteRecord to represent an audited CVR.
   */
  @Mock
  private CastVoteRecord auditedCvr;

  /**
   * Test NEB assertion: Alice NEB Chuan
   */
  private final Assertion aliceNEBChaun = createNEBAssertion("Alice", "Chuan",
      TC, 50, 0.1, 8, Map.of(),
      0, 0, 0, 0, 0);

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);

    when(ABCD.choices()).thenReturn(List.of("A", "B", "C", "D"));
    when(BACD.choices()).thenReturn(List.of("B", "A", "C", "D"));
    when(blank.choices()).thenReturn(List.of());
    when(A.choices()).thenReturn(List.of("A"));
    when(B.choices()).thenReturn(List.of("B"));

    when(cvr.id()).thenReturn(1L);
  }

  /**
   * Create an NEB assertion with the given parameters.
   * @param winner Winner of the assertion.
   * @param loser Loser of the assertion.
   * @param contestName Name of the contest to which the assertion belongs.
   * @param rawMargin Raw margin of the assertion.
   * @param dilutedMargin Diluted margin of the assertion.
   * @param difficulty Difficulty of the assertion.
   * @param cvrDiscrepancy Map between CVR ID and discrepancy type.
   * @param oneVoteOver Number of one vote overstatements to associate with the assertion.
   * @param oneVoteUnder Number of one vote understatements to associate with the assertion.
   * @param twoVoteOver Number of two vote overstatements to associate with the assertion.
   * @param twoVoteUnder Number of two vote understatements to associate with the assertion.
   * @param other Number of other discrepancies to associate with the assertion.
   * @return an NEB assertion with the given specification.
   */
  public static Assertion createNEBAssertion(String winner, String loser, String contestName,
      int rawMargin, double dilutedMargin, double difficulty, Map<Long,Integer> cvrDiscrepancy,
      int oneVoteOver, int oneVoteUnder, int twoVoteOver, int twoVoteUnder, int other){

    Assertion a = new NEBAssertion();
    AssertionTests.populateAssertion(a, winner, loser, contestName, List.of(), rawMargin,
      dilutedMargin, difficulty, cvrDiscrepancy, oneVoteOver, oneVoteUnder, twoVoteOver,
      twoVoteUnder, other);

    return a;
  }


  /**
   * This suite of tests verifies the optimistic sample size computation for NEB assertions.
   * @param riskLimit Risk limit of the audit.
   * @param rawMargin Raw margin of the assertion.
   * @param dilutedMargin Diluted margin of the assertion.
   * @param difficulty Raire-computed difficulty of the assertion.
   * @param cvrDiscrepancies Map between CVR id and associated discrepancy for the assertion.
   * @param oneVoteOver Number of one vote overstatements related to the assertion.
   * @param oneVoteUnder Number of one vote understatements related to the assertion.
   * @param twoVoteOver Number of two vote overstatements related to the assertion.
   * @param twoVoteUnder Number of two vote understatements related to the assertion.
   * @param other Number of other discrepancies related to the assertion.
   */
  @Test(dataProvider = "SampleParameters", dataProviderClass = AssertionTests.class)
  public void testNEBOptimistic(BigDecimal riskLimit, Integer rawMargin, BigDecimal dilutedMargin,
      BigDecimal difficulty, Map<Long,Integer> cvrDiscrepancies, Integer oneVoteOver, Integer oneVoteUnder,
      Integer twoVoteOver, Integer twoVoteUnder, Integer other){

    log(LOGGER, String.format("testNEBOptimistic[%f;%f;%d;%d:%d;%d;%d]", riskLimit, dilutedMargin,
        oneVoteOver, oneVoteUnder, twoVoteOver, twoVoteUnder, other));

    Assertion a = createNEBAssertion("W", "L", TC, rawMargin, dilutedMargin.doubleValue(),
        difficulty.doubleValue(), cvrDiscrepancies, oneVoteOver, oneVoteUnder, twoVoteOver,
        twoVoteUnder, other);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit);
    final int expected = AssertionTests.optimistic(riskLimit, dilutedMargin.doubleValue(),
        oneVoteOver, twoVoteOver, oneVoteUnder, twoVoteUnder, Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  /**
   * Test estimated sample size computation in context where none or some ballots have been sampled,
   * with various combinations of risk limit, diluted margin, and discrepancies.
   * @param auditedSamples Number of ballots that have been sampled thus far.
   * @param riskLimit Risk limit of the audit.
   * @param rawMargin Raw margin of the assertion.
   * @param dilutedMargin Diluted margin of the assertion.
   * @param difficulty Raire-computed difficulty of the assertion.
   * @param cvrDiscrepancies Map between CVR id and associated discrepancy for the assertion.
   * @param oneVoteOver Number of one vote overstatements related to the assertion.
   * @param oneVoteUnder Number of one vote understatements related to the assertion.
   * @param twoVoteOver Number of two vote overstatements related to the assertion.
   * @param twoVoteUnder Number of two vote understatements related to the assertion.
   * @param other Number of other discrepancies related to the assertion.
   */
  @Test(dataProvider = "ParametersVaryingSamples", dataProviderClass = AssertionTests.class)
  public void testNEBEstimatedVaryingSamples(Integer auditedSamples, BigDecimal riskLimit,
      Integer rawMargin, BigDecimal dilutedMargin, BigDecimal difficulty,
      Map<Long,Integer> cvrDiscrepancies, Integer oneVoteOver, Integer oneVoteUnder,
      Integer twoVoteOver, Integer twoVoteUnder, Integer other)
  {
    log(LOGGER, String.format("testNEBEstimatedVaryingSamples[%d;%f;%f;%d;%d:%d;%d;%d]",
        auditedSamples, riskLimit, dilutedMargin, oneVoteOver, oneVoteUnder, twoVoteOver,
        twoVoteUnder, other));

    Assertion a = createNEBAssertion("W", "L", TC, rawMargin, dilutedMargin.doubleValue(),
        difficulty.doubleValue(), cvrDiscrepancies, oneVoteOver, oneVoteUnder, twoVoteOver,
        twoVoteUnder, other);

    // Note that the way optimistic/estimated sample computation is performed is that
    // there is an assumption that the optimistic calculation has occurred prior to
    // 'computeEstimatedSamplesToAudit' being called. This fits with the existing design
    // of how plurality sample size computation is done.
    a.computeOptimisticSamplesToAudit(riskLimit);
    final int result = a.computeEstimatedSamplesToAudit(auditedSamples);
    final int expected = AssertionTests.estimated(riskLimit, dilutedMargin.doubleValue(),
        oneVoteOver, twoVoteOver, oneVoteUnder, twoVoteUnder, Audit.GAMMA, auditedSamples);

    assertEquals(result, expected);
    assertEquals(a.estimatedSamplesToAudit.intValue(), expected);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has no prior recorded
   * discrepancies. It should not change the assertion's discrepancy counts. What
   * recordDiscrepancy() does is look for the CVRAuditInfo's ID in its cvrDiscrepancy map. If it is
   * there, the value matching the ID key is retrieved and the associated discrepancy type
   * incremented. If it is not there, then a runtime exception is thrown.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testRecordNoMatch1(){
    log(LOGGER, "testRecordNoMatch1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has some discrepancies recorded
   * already. A runtime exception should be thrown.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testRecordNoMatch2(){
    log(LOGGER, "testRecordNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(2L, -1, 3L, 1), 1, 1,
        0, 0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote overvote.
   */
  @Test
  public void testRecordOneVoteOvervote1(){
    log(LOGGER, "testRecordOneVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 1), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote undervote.
   */
  @Test
  public void testRecordOneVoteUndervote1(){
    log(LOGGER, "testRecordOneVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, -1), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote overvote.
   */
  @Test
  public void testRecordTwoVoteOvervote1(){
    log(LOGGER, "testRecordTwoVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 2), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRecordTwoVoteUndervote1(){
    log(LOGGER, "testRecordTwoVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, -2), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRecordOther1(){
    log(LOGGER, "testRecordOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRecordOneVoteOvervote2(){
    log(LOGGER, "testRecordOneVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 1),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(2, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRecordOneVoteUndervote2(){
    log(LOGGER, "testRecordOneVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, -1),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRecordTwoVoteOvervote2(){
    log(LOGGER, "testRecordTwoVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 2),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRecordTwoVoteUndervote2(){
    log(LOGGER, "testRecordTwoVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, -2),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(2, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRecordOther2(){
    log(LOGGER, "testRecordOther2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 0),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(2, a.otherCount.intValue());
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been recorded for the given CVR-ACVR pair, and the assertion has no prior recorded
   * discrepancies. It should not change the assertion's discrepancy counts. What
   * removeDiscrepancy() does is look for the CVRAuditInfo's ID in its cvrDiscrepancy map. If it is
   * there, the value matching the ID key is retrieved, the associated discrepancy type
   * decremented, and the ID removed from the map. If it is not there, then the discrepancy counts
   * and the map are not changed.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testRemoveNoMatch1(){
    log(LOGGER, "testRemoveNoMatch1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    a.removeDiscrepancy(info);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has some discrepancies recorded
   * already. It should not change the assertion's discrepancy counts.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testRemoveNoMatch2(){
    log(LOGGER, "testRemoveNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(2L, -1, 3L, 1), 1, 1,
        0, 0, 0);

    a.removeDiscrepancy(info);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote overvote.
   */
  @Test
  public void testRemoveOneVoteOvervote1(){
    log(LOGGER, "testRemoveOneVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 1), 1, 0, 0,
        0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote undervote.
   */
  @Test
  public void testRemoveOneVoteUndervote1(){
    log(LOGGER, "testRemoveOneVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, -1), 0, 1, 0,
        0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overvote.
   */
  @Test
  public void testRemoveTwoVoteOvervote1(){
    log(LOGGER, "testRemoveTwoVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, 2), 0, 0, 1,
        0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRemoveTwoVoteUndervote1(){
    log(LOGGER, "testRemoveTwoVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, -2), 0, 0, 0,
        1, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRemoveOther1(){
    log(LOGGER, "testRemoveOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0), 0, 0, 0,
        0, 1);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOneVoteOvervote2(){
    log(LOGGER, "testRemoveOneVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 1),
        2, 0, 0, 1, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOneVoteUndervote2(){
    log(LOGGER, "testRemoveOneVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -1, 4L, -1),
        1, 2, 0, 0, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -1), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveTwoVoteOvervote2(){
    log(LOGGER, "testRemoveTwoVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 2, 2L, 1, 3L, -2, 4L, 2),
        1, 0, 2, 1, 0);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(1L, 2, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveTwoVoteUndervote2(){
    log(LOGGER, "testRemoveTwoVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC,50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, -2),
        1, 0, 0, 2, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOther2(){
    log(LOGGER, "testRemoveOther2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNEBAssertion("W", "L", TC, 50, 0.1,
        8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 0),
        1, 0, 0, 1, 2);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test NEB assertion scoring: zero score.
   */
  @Test
  public void testScoreZero1() {
    log(LOGGER, "testScoreZero1");
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Diego"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEB assertion scoring: zero score.
   */
  @Test
  public void testScoreZero2() {
    log(LOGGER, "testScoreZero2");
    when(cvrInfo.choices()).thenReturn(List.of());
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEB assertion scoring: score of zero.
   */
  @Test
  public void testScoreZero3() {
    log(LOGGER, "testScoreZero3");
    when(cvrInfo.choices()).thenReturn(List.of("Diego", "Alice"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEB assertion scoring: score of one.
   */
  @Test
  public void testScoreOne1() {
    log(LOGGER, "testScoreOne1");
    when(cvrInfo.choices()).thenReturn(List.of("Alice"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEB assertion scoring: score of one.
   */
  @Test
  public void testScoreOne2() {
    log(LOGGER, "testScoreOne2");
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Chuan"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEB assertion scoring: score of one.
   */
  @Test
  public void testScoreOne3() {
    log(LOGGER, "testScoreOne3");
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Bob", "Chuan"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEB assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne1() {
    log(LOGGER, "testScoreMinusOne1");
    when(cvrInfo.choices()).thenReturn(List.of("Diego", "Chuan", "Bob", "Alice"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEB assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne2() {
    log(LOGGER, "testScoreMinusOne2");
    when(cvrInfo.choices()).thenReturn(List.of("Chuan"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEB assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne3() {
    log(LOGGER, "testScoreMinusOne3");
    when(cvrInfo.choices()).thenReturn(List.of("Chuan", "Alice"));
    final int score = aliceNEBChaun.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Two CastVoteRecord's with a blank vote will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNone1(RecordType auditedType){
    testComputeDiscrepancyNone(blank, auditedType);
  }

  /**
   * Two CastVoteRecord's with a single vote for "A" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNone2(RecordType auditedType){
    testComputeDiscrepancyNone(A, auditedType);
  }

  /**
   * Two CastVoteRecord's with a single vote for "B" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNone3(RecordType auditedType){
    testComputeDiscrepancyNone(B, auditedType);
  }

  /**
   * Two CastVoteRecord's with a vote for "A", "B", "C", "D" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNone4(RecordType auditedType){
    testComputeDiscrepancyNone(ABCD, auditedType);
  }

  /**
   * Two CastVoteRecord's with a vote for "B", "A", "C", "D" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNone5(RecordType auditedType){
    testComputeDiscrepancyNone(BACD, auditedType);
  }

  /**
   * Check that a series of NEB assertions will recognise when there is no discrepancy
   * between a CVR and audited ballot. The given vote configuration is used as the CVRContestInfo
   * field in the CVR and audited ballot CastVoteRecords.
   * @param info A vote configuration.
   */
  public void testComputeDiscrepancyNone(CVRContestInfo info, RecordType auditedType){
    log(LOGGER, String.format("testComputeDiscrepancyNone[%s;%s]", info.choices(), auditedType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(info));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(info));

    when(info.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(auditedType);

    // Create a series of NEB assertions and check that this cvr/audited ballot are never
    // identified as having a discrepancy.
    Assertion a1 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    Assertion a2 = createNEBAssertion("D", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    Assertion a3 = createNEBAssertion("E", "F", TC, 50, 0.1,
        8, Map.of(2L, 2), 0, 0, 1,
        0, 0);

    Assertion a4 = createNEBAssertion("B", "F", TC, 50, 0.1,
        8, Map.of(2L, 1), 1, 0, 0,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d4 = a4.computeDiscrepancy(cvr, auditedCvr);

    // None of the above calls to computeDiscrepancy should have produced a discrepancy.
    assert(d1.isEmpty() && d2.isEmpty() && d3.isEmpty() && d4.isEmpty());

    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 1, 0, 0, 0));
    assertEquals(Map.of(2L, 2), a3.cvrDiscrepancy);
    assert(countsEqual(a4, 1, 0, 0, 0, 0));
    assertEquals(Map.of(2L, 1), a4.cvrDiscrepancy);
  }


  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with vote "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion  A NEB C. (In this case, a one
   * vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneOver1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneOver1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);

    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with vote "B", check that the right discrepancy
   * is computed for the assertions A NEB C. (In this case, a one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneOver2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneOver2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(B));

    when(B.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(2L, -1, 4L, 2), 0, 1,
        1, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 1, 1, 0, 0));
    assertEquals(Map.of(1L, 1,2L, -1, 4L, 2), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with a blank vote, check that the right discrepancy
   * is computed for the assertions A NEB B. (In this case, a one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneOver3(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneOver3[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));

    when(blank.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(2L, -1), 0, 1, 0,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 1, 0, 0));
    assertEquals(Map.of(1L, 1, 2L, -1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with a vote of "B", check that the right discrepancy
   * is computed for the assertions A NEB B. (In this case, a two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyTwoOver1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyTwoOver1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(B));

    when(B.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertions A NEB B. (In this case, a
   * two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyTwoOver2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyTwoOver2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(3L, 2), 0, 0, 1,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 1, 0, 0, 0));
    assertEquals(Map.of(1L, 2, 3L, 2), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion B NEB C. (In this case, a
   * one vote undervote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneUnder1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneUnder1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == -1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, -1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a blank vote and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion B NEB C. (In this case, a
   * one vote undervote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneUnder2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneUnder2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "C", TC, 50, 0.1,
        8, Map.of(2L, 0), 0, 0, 0,
        0, 1);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == -1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 1));
    assertEquals(Map.of(1L, -1, 2L, 0), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "B" and audited ballot with a vote of "A", check that the right
   * discrepancy is computed for the assertion A NEB C. (In this case, a one vote undervote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOneUnder3(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOneUnder3[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(B));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));

    when(A.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(2L, 1), 1, 0, 0,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == -1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 1, 0, 0, 0, 0));
    assertEquals(Map.of(1L, -1, 2L, 1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "B" and audited ballot with a vote of "A", check that the right
   * discrepancy is computed for the assertion A NEB B. (In this case, a two vote undervote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyTwoUnder1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyTwoUnder1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(B));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));

    when(A.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == -2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, -2), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion B NEB A. (In this case, a two
   * vote undervote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyTwoUnder2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyTwoUnder2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(2L, -1), 0, 1, 0,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == -2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 1, 0, 0));
    assertEquals(Map.of(1L, -2, 2L, -1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion D NEB C. (In this case, an
   * "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOther1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOther1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("D", "C", TC, 50, 0.1,
        8, Map.of(2L, -1), 0, 1, 0,
        0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 1, 0, 0));
    assertEquals(Map.of(1L, 0, 2L, -1), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "A" and audited ballot with a vote of "B", check that the right
   * discrepancy is computed for the assertion C NEB D. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOther2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOther2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(B));

    when(B.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("C", "D", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a1.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a vote "A" and audited ballot with a blank vote, check that the right
   * discrepancy is computed for the assertion C NEB D. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyOther3(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyOther3[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));

    when(blank.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("C", "D", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a1.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a normal ballot with a blank vote, check that the right
   * discrepancy is computed for the assertion A NEB B. (In this case, a one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordOneOver1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordOneOver1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));

    when(blank.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertion F NEB G. (In this case, a one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordOneOver2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordOneOver2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertion A NEB B. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordOther1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordOther1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a1.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertion B NEB A. (In this case, a two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordTwoOver1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordTwoOver1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a ballot with vote "A", "B", "C", "D", but no consensus, check that
   * the right discrepancy is computed for any NEB assertion. (A two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordNoConsensus1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordNoConsensus1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a3 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);
    assert(d2.isPresent() && d2.getAsInt() == 2);
    assert(d3.isPresent() && d3.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a3.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a ballot with a blank vote, but no consensus, check that
   * the right discrepancy is computed for any NEB assertion. (A two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyPhantomRecordNoConsensus2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyPhantomRecordNoConsensus2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));

    when(blank.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a3 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);
    assert(d2.isPresent() && d2.getAsInt() == 2);
    assert(d3.isPresent() && d3.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a3.cvrDiscrepancy);
  }

  /**
   * Given a phantom CVR, and a phantom ballot, check that the right discrepancy is computed for
   * any NEB assertion. (A two vote overvote).
   */
  @Test
  public void testComputeDiscrepancyPhantomRecordPhantomBallot(){
    log(LOGGER, "testComputeDiscrepancyPhantomRecordPhantomBallot");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD)); // Choices do not matter

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES); // Setting does not matter
    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a3 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);
    assert(d2.isPresent() && d2.getAsInt() == 2);
    assert(d3.isPresent() && d3.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a3.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions A NEB F, and A NEB C. (A two vote overvote).
   */
  @Test
  public void testComputeDiscrepancyPhantomBallotNormalCVR1(){
    log(LOGGER, "testComputeDiscrepancyPhantomRecordNormalCVR1");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter

    when(blank.consensus()).thenReturn(ConsensusValue.YES); // Setting does not matter
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNEBAssertion("A", "F", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);
    assert(d2.isPresent() && d2.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions F NEB A, and D NEB B. (An "other" discrepancy).
   */
  @Test
  public void testComputeDiscrepancyPhantomBallotNormalCVR2(){
    log(LOGGER, "testComputeDiscrepancyPhantomRecordNormalCVR2");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter

    when(blank.consensus()).thenReturn(ConsensusValue.YES); // Setting does not matter
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNEBAssertion("F", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("D", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);
    assert(d2.isPresent() && d2.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions B NEB C, and B NEB D. (A one vote overvote).
   */
  @Test
  public void testComputeDiscrepancyPhantomBallotNormalCVR3(){
    log(LOGGER, "testComputeDiscrepancyPhantomRecordNormalCVR3");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter

    when(blank.consensus()).thenReturn(ConsensusValue.YES); // Setting does not matter
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNEBAssertion("B", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("B", "D", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);
    assert(d2.isPresent() && d2.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a blank vote, and a phantom ballot, check that the right discrepancy is computed for
   * any NEB assertion. (A one vote overvote).
   */
  @Test
  public void testComputeDiscrepancyPhantomBallotNormalCVR4(){
    log(LOGGER, "testComputeDiscrepancyPhantomRecordNormalCVR4");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank)); // Choices do not matter

    when(blank.consensus()).thenReturn(ConsensusValue.YES); // Setting does not matter
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a3 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);
    assert(d2.isPresent() && d2.getAsInt() == 1);
    assert(d3.isPresent() && d3.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a3.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a ballot with no consensus, check that
   * the right discrepancy is computed for assertions A NEB F, and A NEB C. (A two vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNoConsensusNormalCVR1(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyNoConsensusNormalCVR1[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(BACD));

    when(BACD.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("A", "F", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 2);
    assert(d2.isPresent() && d2.getAsInt() == 2);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 2), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a ballot with no consensus, check that
   * the right discrepancy is computed for assertions F NEB A, and D NEB B. (An "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNoConsensusNormalCVR2(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyNoConsensusNormalCVR2[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("F", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("D", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 0);
    assert(d2.isPresent() && d2.getAsInt() == 0);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 0), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a ballot with no consensus, check that
   * the right discrepancy is computed for assertions B NEB C, and B NEB D. (A one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNoConsensusNormalCVR3(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyNoConsensusNormalCVR3[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));

    when(blank.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "C", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("B", "D", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);
    assert(d2.isPresent() && d2.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a2.cvrDiscrepancy);
  }

  /**
   * Given a CVR with a blank vote, and a ballot with no consensus, check that the right
   * discrepancy is computed for any NEB assertion. (A one vote overvote).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testComputeDiscrepancyNoConsensusNormalCVR4(RecordType recordType){
    log(LOGGER, String.format("testComputeDiscrepancyNoConsensusNormalCVR4[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNEBAssertion("B", "A", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a2 = createNEBAssertion("A", "B", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);
    Assertion a3 = createNEBAssertion("F", "G", TC, 50, 0.1,
        8, Map.of(), 0, 0, 0, 0, 0);

    OptionalInt d1 = a1.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d2 = a2.computeDiscrepancy(cvr, auditedCvr);
    OptionalInt d3 = a3.computeDiscrepancy(cvr, auditedCvr);

    assert(d1.isPresent() && d1.getAsInt() == 1);
    assert(d2.isPresent() && d2.getAsInt() == 1);
    assert(d3.isPresent() && d3.getAsInt() == 1);

    // Note that discrepancy counts don't change until recordDiscrepancy() is called.
    assert(countsEqual(a1, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a1.cvrDiscrepancy);
    assert(countsEqual(a2, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a2.cvrDiscrepancy);
    assert(countsEqual(a3, 0, 0, 0, 0, 0));
    assertEquals(Map.of(1L, 1), a3.cvrDiscrepancy);
  }
}
