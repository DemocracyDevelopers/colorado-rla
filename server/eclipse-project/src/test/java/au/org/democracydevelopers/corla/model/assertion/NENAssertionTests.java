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
import static au.org.democracydevelopers.corla.model.assertion.AssertionTests.checkComputeDiscrepancy;
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
 * A suite of tests to verify the functionality of NENAssertion objects. This includes:
 * -- Testing of optimistic sample size computation.
 * -- Testing of estimated sample size computation.
 * -- Recording of a pre-computed discrepancy.
 * -- Removal of a pre-recorded discrepancy.
 * -- Scoring of NEN assertions.
 * -- Computation of discrepancies.
 * Refer to the Guide to RAIRE for details on how NEN assertions are scored, and how
 * discrepancies are computed (Part 2, Appendix A.)
 */
public class NENAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NENAssertionTests.class);

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
   * Mocked CVRContestInfo representing the vote "D", "A", "B", "C".
   */
  @Mock
  private CVRContestInfo DABC;

  /**
   * Mocked CVRContestInfo representing the vote "B", "A".
   */
  @Mock
  private CVRContestInfo BA;

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
   * Test NEN assertion: Alice NEN Chuan assuming Alice and Chuan remain.
   */
  private final Assertion aliceNENChaun1 = createNENAssertion("Alice", "Chuan",
      "Test Contest", List.of("Alice", "Chuan"), 50, 0.1,
      8, Map.of(), 0, 0, 0, 0, 0);

  /**
   * Test NEN assertion: Alice NEN Chuan assuming Alice, Chuan, and Bob remain.
   */
  private final Assertion aliceNENChaun2 = createNENAssertion("Alice", "Chuan",
      "Test Contest", List.of("Alice", "Chuan", "Bob"), 50, 0.1,
      8, Map.of(), 0, 0, 0, 0, 0);

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);

    when(ABCD.choices()).thenReturn(List.of("A", "B", "C", "D"));
    when(BACD.choices()).thenReturn(List.of("B", "A", "C", "D"));
    when(DABC.choices()).thenReturn(List.of("D", "A", "B", "C"));
    when(BA.choices()).thenReturn(List.of("B", "A"));
    when(blank.choices()).thenReturn(List.of());
    when(A.choices()).thenReturn(List.of("A"));
    when(B.choices()).thenReturn(List.of("B"));

    when(cvr.id()).thenReturn(1L);
  }

  /**
   * This suite of tests verifies the optimistic sample size computation for NEN assertions.
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
  public void testNENOptimistic(BigDecimal riskLimit, Integer rawMargin, BigDecimal dilutedMargin,
      BigDecimal difficulty, Map<Long,Integer> cvrDiscrepancies, Integer oneVoteOver,
      Integer oneVoteUnder, Integer twoVoteOver, Integer twoVoteUnder, Integer other)
  {
    log(LOGGER, String.format("testNENOptimistic[%f;%f;%d;%d:%d;%d;%d]", riskLimit,
        dilutedMargin, oneVoteOver, oneVoteUnder, twoVoteOver, twoVoteUnder, other));

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, rawMargin,
        dilutedMargin.doubleValue(), difficulty.doubleValue(), cvrDiscrepancies, oneVoteOver,
        oneVoteUnder, twoVoteOver, twoVoteUnder, other);

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
  public void testNENEstimatedVaryingSamples(Integer auditedSamples, BigDecimal riskLimit,
      Integer rawMargin, BigDecimal dilutedMargin, BigDecimal difficulty,
      Map<Long,Integer> cvrDiscrepancies, Integer oneVoteOver, Integer oneVoteUnder,
      Integer twoVoteOver, Integer twoVoteUnder, Integer other)
  {
    log(LOGGER, String.format("testNENEstimatedVaryingSamples[%d;%f;%f;%d;%d:%d;%d;%d]",
        auditedSamples, riskLimit, dilutedMargin, oneVoteOver, oneVoteUnder, twoVoteOver,
        twoVoteUnder, other));

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, rawMargin,
        dilutedMargin.doubleValue(), difficulty.doubleValue(), cvrDiscrepancies, oneVoteOver,
        oneVoteUnder, twoVoteOver, twoVoteUnder, other);

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
   * incremented. If it is not there, then the discrepancy counts are not changed.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testNENRecordNoMatch1(){
    log(LOGGER, "testNENRecordNoMatch1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has some discrepancies recorded
   * already. It should not change the assertion's discrepancy counts.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testNENRecordNoMatch2(){
    log(LOGGER, "testNENRecordNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8,  Map.of(2L, -1, 3L, 1), 1,
        1, 0, 0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote overstatement, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testNENRecordOneVoteOverstatement1(){
    log(LOGGER, "testNENRecordOneVoteOverstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 1), 0, 0,
        0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote understatement, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testNENRecordOneVoteUnderstatement1(){
    log(LOGGER, "testNENRecordOneVoteUnderstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, -1), 0, 0,
        0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote overstatement, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testNENRecordTwoVoteOverstatement1(){
    log(LOGGER, "testNENRecordTwoVoteOverstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 2), 0, 0,
        0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testNENRecordTwoVoteUnderstatement1(){
    log(LOGGER, "testNENRecordTwoVoteUnderstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8, Map.of(1L, -2), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testNENRecordOther1(){
    log(LOGGER, "testNENRecordOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 0), 0, 0, 0,
        0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote overstatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRecordOneVoteOverstatement2(){
    log(LOGGER, "testNENRecordOneVoteOverstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 1),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(2, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRecordOneVoteUnderstatement2(){
    log(LOGGER, "testNENRecordOneVoteUnderstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 0, 2L, 1, 3L, -2, 4L, -1),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote overstatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRecordTwoVoteOverstatement2(){
    log(LOGGER, "testNENRecordTwoVoteOverstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC,  AssertionTests.wlo, 50,
        0.1, 8,  Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 2),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRecordTwoVoteUnderstatement2(){
    log(LOGGER, "testNENRecordTwoVoteUnderstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, -2),
        1, 0, 0, 1, 1);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(2, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRecordOther2(){
    log(LOGGER, "testNENRecordOther2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo, 50,
        0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -2, 4L, 0),
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
  public void testNENRemoveNoMatch1(){
    log(LOGGER, "testNENRemoveNoMatch1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    a.removeDiscrepancy(info);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has some discrepancies recorded
   * already. It should not change the assertion's discrepancy counts.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testNENRemoveNoMatch2(){
    log(LOGGER, "testNENRemoveNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(2L, -1, 3L, 1),
        1, 1, 0, 0, 0);

    a.removeDiscrepancy(info);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote overstatement.
   */
  @Test
  public void testNENRemoveOneVoteOverstatement1(){
    log(LOGGER, "testNENRemoveOneVoteOverstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 1), 1,
        0, 0, 0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote understatement.
   */
  @Test
  public void testNENRemoveOneVoteUnderstatement1(){
    log(LOGGER, "testNENRemoveOneVoteUnderstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, -1), 0,
        1, 0, 0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overstatement.
   */
  @Test
  public void testNENRemoveTwoVoteOverstatement1(){
    log(LOGGER, "testNENRemoveTwoVoteOverstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 2), 0,
        0, 1, 0, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote understatement.
   */
  @Test
  public void testNENRemoveTwoVoteUnderstatement1(){
    log(LOGGER, "testNENRemoveTwoVoteUnderstatement1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, -2), 0,
        0, 0, 1, 0);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote understatement.
   */
  @Test
  public void testNENRemoveOther1(){
    log(LOGGER, "testNENRemoveOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 0), 0,
        0, 0, 0, 1);

    a.removeDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote overstatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRemoveOneVoteOverstatement2(){
    log(LOGGER, "testNENRemoveOneVoteOverstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -2,
            4L, 1), 2, 0, 0, 1, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRemoveOneVoteUnderstatement2(){
    log(LOGGER, "testNENRemoveOneVoteUnderstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -1,
            4L, -1), 1, 2, 0, 0, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -1), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overstatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRemoveTwoVoteOverstatement2(){
    log(LOGGER, "testNENRemoveTwoVoteOverstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 2, 2L, 1, 3L, -2,
            4L, 2), 1, 0, 2, 1, 0);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());

    assertEquals(Map.of(1L, 2, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRemoveTwoVoteUnderstatement2(){
    log(LOGGER, "testNENRemoveTwoVoteUnderstatement2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -2,
            4L, -2), 1, 0, 0, 2, 1);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }

  /**
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote understatement, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testNENRemoveOther2(){
    log(LOGGER, "testNENRemoveOther2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", TC, AssertionTests.wlo,
        50, 0.1, 8, Map.of(1L, 0, 2L, 1, 3L, -2,
            4L, 0), 1, 0, 0, 1, 2);

    a.removeDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(1, a.otherCount.intValue());

    assertEquals(Map.of(1L, 0, 2L, 1, 3L, -2), a.cvrDiscrepancy);
  }


  /**
   * Test NEN assertion scoring: zero score.
   */
  @Test
  public void testNENScoreZero1() {
    log(LOGGER, "testNENScoreZero1");
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Diego"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: zero score.
   */
  @Test
  public void testNENScoreZero2() {
    log(LOGGER, "testNENScoreZero2");
    when(cvrInfo.choices()).thenReturn(List.of());
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of zero.
   */
  @Test
  public void testNENScoreZero3() {
    log(LOGGER, "testNENScoreZero3");
    when(cvrInfo.choices()).thenReturn(List.of("Diego"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of zero.
   */
  @Test
  public void testNENScoreZero4() {
    log(LOGGER, "testNENScoreZero4");
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Alice", "Diego", "Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testNENScoreOne1() {
    log(LOGGER, "testNENScoreOne1");
    when(cvrInfo.choices()).thenReturn(List.of("Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testNENScoreOne2() {
    log(LOGGER, "testNENScoreOne2");
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testNENScoreOne3() {
    log(LOGGER, "testNENScoreOne3");
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Bob", "Chuan"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testNENScoreOne4() {
    log(LOGGER, "testNENScoreOne4");
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Alice", "Diego", "Chuan"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testNENScoreMinusOne1() {
    log(LOGGER, "testNENScoreMinusOne1");
    when(cvrInfo.choices()).thenReturn(List.of("Diego", "Chuan", "Bob", "Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testNENScoreMinusOne2() {
    log(LOGGER, "testNENScoreMinusOne2");
    when(cvrInfo.choices()).thenReturn(List.of("Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testNENScoreMinusOne3() {
    log(LOGGER, "testNENScoreMinusOne3");
    when(cvrInfo.choices()).thenReturn(List.of("Chuan", "Alice"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testNENScoreMinusOne4() {
    log(LOGGER, "testNENScoreMinusOne4");
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Chuan", "Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Two CastVoteRecord's with a blank vote will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNone1(RecordType auditedType){
    testNENComputeDiscrepancyNone(blank, auditedType);
  }

  /**
   * Two CastVoteRecord's with a single vote for "A" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNone2(RecordType auditedType){
    testNENComputeDiscrepancyNone(A, auditedType);
  }

  /**
   * Two CastVoteRecord's with a single vote for "B" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNone3(RecordType auditedType){
    testNENComputeDiscrepancyNone(B, auditedType);
  }

  /**
   * Two CastVoteRecord's with a vote for "A", "B", "C", "D" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNone4(RecordType auditedType){
    testNENComputeDiscrepancyNone(ABCD, auditedType);
  }

  /**
   * Two CastVoteRecord's with a vote for "B", "A", "C", "D" will not trigger a discrepancy.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNone5(RecordType auditedType){
    testNENComputeDiscrepancyNone(BACD, auditedType);
  }

  /**
   * Check that a series of NEN assertions will recognise when there is no discrepancy
   * between a CVR and audited ballot. The given vote configuration is used as the CVRContestInfo
   * field in the CVR and audited ballot CastVoteRecords.
   * @param info A vote configuration.
   */
  public void testNENComputeDiscrepancyNone(CVRContestInfo info, RecordType auditedType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNone[%s;%s]", info.choices(), auditedType));
    resetMocks(info, info, RecordType.UPLOADED, ConsensusValue.YES, auditedType);

    // Create a series of NEN assertions and check that this cvr/audited ballot are never
    // identified as having a discrepancy.
    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    Assertion a2 = createNENAssertion("D", "C", TC, List.of("C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    Assertion a3 = createNENAssertion("E", "F", TC, List.of("E", "F"), 50,
        0.1, 8, Map.of(2L, 2), 0, 0, 1,
        0, 0);

    Assertion a4 = createNENAssertion("B", "F", TC, List.of("A", "B", "D", "F"),
        50, 0.1, 8, Map.of(2L, 1), 1,
        0, 0, 0, 0);

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
   * check that the right discrepancy is computed for the assertion  A NEN C assuming "A", "B",
   * "C" are continuing candidates. (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENomputeDiscrepancyOneOver1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneOver1[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C",  TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with vote "B", check that the right discrepancy
   * is computed for the assertions A NEN C assuming "A" and "C" are continuing.
   * (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneOver2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneOver2[%s]", recordType));
    resetMocks(A, B, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "B"), 50,
        0.1, 8, Map.of(2L, -1, 4L, 2), 0,
        1, 1, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 1, Map.of(1L, 1,2L,
            -1, 4L, 2), 0, 1, 1, 0, 0);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with a blank vote, check that the right discrepancy
   * is computed for the assertions A NEN B assuming "A", "B" and "C" are continuing.
   * (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneOver3(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneOver3[%s]", recordType));
    resetMocks(A, blank, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC, List.of("A", "B", "C"),50,
        0.1, 8, Map.of(2L, -1), 0, 1,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 1, Map.of(1L, 1, 2L, -1),
        0, 0, 1, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with vote "D", "A", "B", "C",
   * check that the right discrepancy is computed for the assertion B NEN C assuming "B", "C", and
   * "D" are continuing candidates. (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneOver4(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneOver4[%s]", recordType));
    resetMocks(ABCD, DABC, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("B", "C",  TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A" and audited ballot with a vote of "B", check that the right discrepancy
   * is computed for the assertions A NEN B given "A" and "B", and A NEN B given "A", "B", and "C"
   * are continuing. (In this case, a two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyTwoOver1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyTwoOver1[%s]", recordType));
    resetMocks(A, B, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC,  List.of("A", "B"), 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);
    Assertion a2 = createNENAssertion("A", "B", TC,  List.of("A", "B", "C"), 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with a vote of "D", "A", "B", "C",
   * check that the right discrepancy is computed for the assertion C NEN D assuming "C",
   * and "D" are continuing. (In this case, a two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyTwoOver2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyTwoOver2[%s]", recordType));
    resetMocks(ABCD, DABC, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("C", "D", TC,  List.of("C", "D"),
        50, 0.1, 8, Map.of(3L, 2), 0,
        0, 1, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 2,
        Map.of(1L, 2, 3L, 2), 0, 1, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertions A NEN B assuming "A", "B", "C",
   * and "D" are continuing, and A NEN B assuming "A", and "B" are continuing.
   * (In this case, a two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyTwoOver3(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyTwoOver2[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC,  List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(3L, 2), 0,
        0, 1, 0, 0);
    Assertion a2 = createNENAssertion("A", "B", TC,  List.of("A", "B"),
        50, 0.1, 8, Map.of(3L, 2), 0,
        0, 1, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 2,
        Map.of(1L, 2, 3L, 2), 0, 1, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion B NEN C, assuming "A", "B", "C"
   * and "D" are continuing. (In this case, a one vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneUnder1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneUnder1[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("B", "C", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), -1, Map.of(1L, -1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a blank vote and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertion A NEN C given that "A", and "C"
   * are continuing. (In this case, a one vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneUnder2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneUnder2[%s]", recordType));
    resetMocks(blank, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "C"), 50,
        0.1, 8, Map.of(2L, 0), 0, 0,
        0, 0, 1);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), -1,
        Map.of(1L, -1, 2L, 0), 0, 0, 0, 0, 1);
  }

  /**
   * Given a CVR with a vote "B" and audited ballot with a vote of "A", check that the right
   * discrepancy is computed for the assertion A NEN C assuming "A", "B", and "C" are continuing.
   * (In this case, a one vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneUnder3(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneUnder3[%s]", recordType));
    resetMocks(B, A, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(2L, 1), 1,
        0, 0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), -1,
        Map.of(1L, -1, 2L, 1), 1, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a vote "B" and audited ballot with a vote of "B", "A", "C", "D", check that
   * the right discrepancy is computed for the assertion A NEN C assuming "A", and "C" are continuing.
   * (In this case, a one vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOneUnder4(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOneUnder4[%s]", recordType));
    resetMocks(B, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "C"),
        50, 0.1, 8, Map.of(2L, 1), 1,
        0, 0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), -1,
        Map.of(1L, -1, 2L, 1), 1, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a vote "B" and audited ballot with a vote of "A", check that the right
   * discrepancy is computed for the assertion A NEN B assuming "A" and "B" are continuing.
   * (In this case, a two vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyTwoUnder1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyTwoUnder1[%s]", recordType));
    resetMocks(B, A, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC, List.of("A", "B"), 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), -2, Map.of(1L, -2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertions B NEN A assuming "A", "B", "C",
   * and "D" are continuing, and  B NEN A assuming "A" and "B" are continuing.
   * (In this case, a two vote understatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyTwoUnder2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyTwoUnder2[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("B", "A", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(2L, -1), 0,
        1, 0, 0, 0);
    Assertion a2 = createNENAssertion("B", "A", TC, List.of("A", "B"),
        50, 0.1, 8, Map.of(2L, -1), 0,
        1, 0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), -2,
        Map.of(1L, -2, 2L, -1), 0, 0, 1, 0, 0);
  }

  /**
   * Given a CVR with a vote "A", "B", "C", "D" and audited ballot with a vote of "B", "A", "C", "D",
   * check that the right discrepancy is computed for the assertions D NEB C assuming "B", "C",
   * and "D" are continuing, and D NEN E assuming "C", "D", and "E" are continuing.
   * (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOther1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOther1[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("D", "C", TC, List.of("B", "C", "D"),
        50, 0.1,  8, Map.of(2L, -1), 0,
        1, 0, 0, 0);
    Assertion a2 = createNENAssertion("D", "E", TC, List.of("C", "D", "E"),
        50, 0.1,  8, Map.of(2L, -1), 0,
        1, 0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 0,
        Map.of(1L, 0, 2L, -1), 0, 0, 1, 0, 0);
  }

  /**
   * Given a CVR with a vote "A" and audited ballot with a vote of "B", check that the right
   * discrepancy is computed for the assertion C NEN D assuming "C" and "D" are continuing.
   * (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOther2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOther2[%s]", recordType));
    resetMocks(A, B, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("C", "D", TC, List.of("C", "D"), 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a vote "A" and audited ballot with a blank vote, check that the right
   * discrepancy is computed for the assertion C NEN D assuming "A", "B", "C", and "D" are
   * continuing. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOther3(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOther3[%s]", recordType));
    resetMocks(A, blank, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("C", "D", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(2L, 0), 0,
        0, 0, 0, 1);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 0, Map.of(1L, 0, 2L, 0),
        0, 0, 0, 0, 1);
  }

  /**
   * Given a CVR with a vote "A" and audited ballot with a vote of "B", "A", "C", "D", check that
   * the right discrepancy is computed for the assertion A NEN C assuming "A", "C", and "D" are
   * continuing. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOther4(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOther4[%s]", recordType));
    resetMocks(A, BACD, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "C", "D"), 50,
        0.1, 8, Map.of(2L, 2), 0, 0, 1,
        0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 0, Map.of(1L, 0, 2L, 2),
        0, 1, 0, 0, 0);
  }

  /**
   * Given a CVR with a blank vote and audited ballot with vote "A", and "B", check that the
   * right discrepancy is computed for the assertion C NEN D assuming "C", and "D" are
   * continuing, and C NEN D assuming "A", "B", "C", and "D" are continuing.
   * (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyOther5(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyOther5[%s]", recordType));
    resetMocks(blank, BA, RecordType.UPLOADED, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("C", "D", TC, List.of("C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("C", "D", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a normal ballot with a blank vote, check that the right
   * discrepancy is computed for the assertions A NEN B given A and B are continuing, and
   * C NEN A given A, B, C, and D are continuing. (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordOneOver1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordOneOver1[%s]", recordType));
    resetMocks(blank, blank, RecordType.PHANTOM_RECORD, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC, List.of("A", "B"), 50,
        0.1, 8, Map.of(), 0, 0, 0,
        0, 0);
    Assertion a2 = createNENAssertion("C", "A", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertions F NEN G given "B", "D", "F", and "G" are continuing,
   * and C NEN D given "B", "C", and "D" are continuing.
   * (In this case, a one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordOneOver2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordOneOver2[%s]", recordType));
    resetMocks(blank, ABCD, RecordType.PHANTOM_RECORD, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("F", "G", TC, List.of("B", "D", "F", "G"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("C", "D", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }


  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertions A NEN B given "A", "B", and "C" are continuing,
   * A NEN B given "A" and "B" are continuing, and B NEN D given "B", "C" and "D" are
   * continuing. (In this case, an "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordOther1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordOther1[%s]", recordType));
    resetMocks(blank, ABCD, RecordType.PHANTOM_RECORD, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("A", "B", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("A", "B", TC, List.of("A", "B"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("B", "D", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a normal ballot with vote "A", "B", "C", "D", check that the right
   * discrepancy is computed for the assertions B NEN A assuming "A", "B" and "D" are continuing,
   * and D NEN C assuming only "C" and "D" are continuing. (In this case, a two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordTwoOver1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordTwoOver1[%s]", recordType));
    resetMocks(blank, ABCD, RecordType.PHANTOM_RECORD, ConsensusValue.YES, recordType);

    Assertion a1 = createNENAssertion("B", "A", TC, List.of("A", "B", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("D", "C", TC, List.of("C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a ballot with vote "A", "B", "C", "D", but no consensus, check that
   * the right discrepancy is computed for any NEN assertion. (A two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordNoConsensus1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordNoConsensus1[%s]", recordType));
    resetMocks(blank, ABCD, RecordType.PHANTOM_RECORD, ConsensusValue.NO, recordType);

    List<Assertion> assertions = getSetAnyNEN();
    checkComputeDiscrepancy(cvr, auditedCvr, assertions, 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a ballot with a blank vote, but no consensus, check that
   * the right discrepancy is computed for any NEN assertion. (A two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomRecordNoConsensus2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomRecordNoConsensus2[%s]", recordType));
    resetMocks(blank, blank, RecordType.PHANTOM_RECORD, ConsensusValue.NO, recordType);

    List<Assertion> assertions = getSetAnyNEN();
    checkComputeDiscrepancy(cvr, auditedCvr, assertions, 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a phantom CVR, and a phantom ballot, check that the right discrepancy is computed for
   * any NEN assertion. (A two vote overstatement).
   */
  @Test
  public void testNENComputeDiscrepancyPhantomRecordPhantomBallot(){
    log(LOGGER, "testNENComputeDiscrepancyPhantomRecordPhantomBallot");
    resetMocks(blank, ABCD, RecordType.PHANTOM_RECORD, ConsensusValue.YES, RecordType.PHANTOM_BALLOT);

    List<Assertion> assertions = getSetAnyNEN();
    checkComputeDiscrepancy(cvr, auditedCvr, assertions, 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions A NEN F given "A", "C", and "F" are
   * continuing, B NEN C given "B", "C", and "D" are continuing, and C NEN F given "C" and "F"
   * are continuing. (A two vote overstatement).
   */
  @Test
  public void testNENComputeDiscrepancyPhantomBallotNormalCVR1(){
    log(LOGGER, "testNENComputeDiscrepancyPhantomRecordNormalCVR1");
    resetMocks(ABCD, blank, RecordType.UPLOADED, ConsensusValue.YES, RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNENAssertion("A", "F", TC, List.of("A", "C", "F"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("B", "C", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("C", "F", TC, List.of("C", "F"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions F NEN A given "A", "B", "C", and "D" are
   * continuing, D NEN B given "B" and "D" are continuing, and D NEN C given "C", and "D" are
   * continuing. (An "other" discrepancy).
   */
  @Test
  public void testNENComputeDiscrepancyPhantomBallotNormalCVR2(){
    log(LOGGER, "testNENComputeDiscrepancyPhantomRecordNormalCVR2");
    resetMocks(ABCD, blank, RecordType.UPLOADED, ConsensusValue.YES, RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNENAssertion("F", "A", TC, List.of("A", "B", "C", "D"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("D", "B", TC, List.of("B", "D"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("D", "C", TC, List.of("C", "D"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a phantom ballot, check that
   * the right discrepancy is computed for assertions B NEN C given "A", "B", and "C" are
   * continuing, D NEN C given "B", "C" and "D" are continuing. (A one vote overstatement).
   */
  @Test
  public void testNENComputeDiscrepancyPhantomBallotNormalCVR3(){
    log(LOGGER, "testNENComputeDiscrepancyPhantomRecordNormalCVR3");
    resetMocks(ABCD, blank, RecordType.UPLOADED, ConsensusValue.YES, RecordType.PHANTOM_BALLOT);

    Assertion a1 = createNENAssertion("B", "C", TC, List.of("A", "B", "C"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("D", "C", TC, List.of("B", "C", "D"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a blank vote, and a phantom ballot, check that the right discrepancy is computed for
   * any NEN assertion. (A one vote overstatement).
   */
  @Test
  public void testNENComputeDiscrepancyPhantomBallotNormalCVR4(){
    log(LOGGER, "testNENComputeDiscrepancyPhantomRecordNormalCVR4");
    resetMocks(blank, blank, RecordType.UPLOADED, ConsensusValue.YES, RecordType.PHANTOM_BALLOT);

    List<Assertion> assertions = getSetAnyNEN();
    checkComputeDiscrepancy(cvr, auditedCvr, assertions, 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }


  /**
   * Given a CVR with vote "A", "B", "C", "D", and a ballot with no consensus, check that
   * the right discrepancy is computed for assertions A NEN F given that "A", "C", and "F" are
   * continuing, B NEB C given "B", and "C" are continuing, and D NEN F given "D" and "F" are
   * continuing. (A two vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNoConsensusNormalCVR1(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNoConsensusNormalCVR1[%s]", recordType));
    resetMocks(ABCD, BACD, RecordType.UPLOADED, ConsensusValue.NO, recordType);

    Assertion a1 = createNENAssertion("A", "F", TC, List.of("A", "C", "F"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("B", "C", TC, List.of("B", "C"),50,
        0.1,8, Map.of(), 0, 0, 0,
        0, 0);
    Assertion a3 = createNENAssertion("D", "F", TC, List.of("D", "F"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 2, Map.of(1L, 2),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", and a ballot with no consensus, check that the right discrepancy
   * is computed for assertion F NEN A given "A" and "F" are continuing. (An "other" discrepancy).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNoConsensusNormalCVR2(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNoConsensusNormalCVR2[%s]", recordType));
    resetMocks(ABCD, A, RecordType.UPLOADED, ConsensusValue.NO, recordType);

    Assertion a1 = createNENAssertion("F", "A", TC, List.of("A", "F"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with vote "A", "B", "C", "D", and a ballot with no consensus, check that
   * the right discrepancy is computed for assertions B NEN C given "A", "B" and "C" are
   * continuing, and C NEN D given "B", "C", and "D" are continuing. (A one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNoConsensusNormalCVR3(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNoConsensusNormalCVR3[%s]", recordType));
    resetMocks(ABCD, blank, RecordType.UPLOADED, ConsensusValue.NO, recordType);

    Assertion a1 = createNENAssertion("B", "C", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("C", "D", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR with a blank vote, and a ballot with no consensus, check that the right
   * discrepancy is computed for any NEN assertion. (A one vote overstatement).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNoConsensusNormalCVR4(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNoConsensusNormalCVR4[%s]", recordType));
    resetMocks(blank, ABCD, RecordType.UPLOADED, ConsensusValue.NO, recordType);

    List<Assertion> assertions = getSetAnyNEN();
    checkComputeDiscrepancy(cvr, auditedCvr, assertions, 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR and audited ballot where the assertion's contest is not on either, check that
   * no discrepancy results for any NEN assertion.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENNoContestOnCVRAuditedBallot(RecordType recordType){
    log(LOGGER, String.format("testNENNoContestOnCVRAuditedBallot[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());

    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    List<Assertion> assertions = getSetAnyNEN();

    for(Assertion a : assertions){
      OptionalInt d = a.computeDiscrepancy(cvr, auditedCvr);
      assert(d.isEmpty());
      assert(countsEqual(a, 0, 0, 0, 0, 0));
      assertEquals(Map.of(), a.cvrDiscrepancy);
    }
  }

  /**
   * Given a CVR that is _not_ a phantom, but does _not_ have the assertion's contest on it, and
   * an audited ballot that _is_ a phantom, check that a discrepancy of 1 (a one vote overstatement)
   * results (for any NEN assertion).
   */
  @Test
  public void testNENComputeDiscrepancyCVRNoContestPhantomBallot(){
    log(LOGGER, "testNENComputeDiscrepancyCVRNoContestPhantomBallot");
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(RecordType.PHANTOM_BALLOT);

    List<Assertion> assertions = getSetAnyNEN();

    for(Assertion a : assertions){
      OptionalInt d = a.computeDiscrepancy(cvr, auditedCvr);
      assert(d.isPresent() && d.getAsInt() == 1);
      assert(countsEqual(a, 0, 0, 0, 0, 0));
      assertEquals(Map.of(1L, 1), a.cvrDiscrepancy);
    }
  }

  /**
   * Given a CVR that _is_ a phantom and audited ballot that _is not_ a phantom, but does not
   * contain the assertion's contest on it, check that a discrepancy of 1 (a one vote overstatement)
   * results (for any NEN assertion).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyPhantomCVRBallotNoContest(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyPhantomCVRBallotNoContest[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(blank));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());

    when(cvr.recordType()).thenReturn(RecordType.PHANTOM_RECORD);
    when(auditedCvr.recordType()).thenReturn(recordType);

    List<Assertion> assertions = getSetAnyNEN();

    for(Assertion a : assertions){
      OptionalInt d = a.computeDiscrepancy(cvr, auditedCvr);
      assert(d.isPresent() && d.getAsInt() == 1);
      assert(countsEqual(a, 0, 0, 0, 0, 0));
      assertEquals(Map.of(1L, 1), a.cvrDiscrepancy);
    }
  }

  /**
   * Given a CVR that is _not_ a phantom, but does _not_ have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, but has no consensus, check that a discrepancy of
   * 1 (a one vote overstatement) results (for any NEN assertion).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyCVRNoContestBallotNoConsensus(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyCVRNoContestBallotNoConsensus[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.NO);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    List<Assertion> assertions = getSetAnyNEN();

    for(Assertion a : assertions){
      OptionalInt d = a.computeDiscrepancy(cvr, auditedCvr);
      assert(d.isPresent() && d.getAsInt() == 1);
      assert(countsEqual(a, 0, 0, 0, 0, 0));
      assertEquals(Map.of(1L, 1), a.cvrDiscrepancy);
    }
  }

  /**
   * Given a CVR that is _not_ a phantom, but does _not_ have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, and has consensus, check that a discrepancy equal
   * to the audited ballot's score results. For this test, the resylt is a 0 (other discrepancy)
   * for all the tested assertions.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyCVRNoContestBallotScoreOfZero(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyCVRNoContestBallotScoreOfZero[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(A.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("B", "C", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("C", "D", TC, List.of("B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("D", "C", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR that is _not_ a phantom, but does _not_ have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, and has consensus, check that a discrepancy equal
   * to the audited ballot's score results. For this test, the resylt is a 1 (one vote overstatement)
   * for all the tested assertions. (Note, in this case, the discrepancy is equal to the negation
   * of the audited ballot score).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyCVRNoContestBallotScoreOfMinusOne(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyCVRNoContestBallotScoreOfMinusOne[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("B", "A", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("C", "B", TC, List.of("B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("D", "B", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR that is _not_ a phantom, but does _not_ have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, and has consensus, check that a discrepancy equal
   * to the audited ballot's score results. For this test, the resylt is a -1 (one vote understatement)
   * for all the tested assertions. (Note, in this case, the discrepancy is equal to the negation
   * of the audited ballot score).
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyCVRNoContestBallotScoreOfOne(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyCVRNoContestBallotScoreOfOne[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("A", "B", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("B", "F", TC, List.of("B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("B", "D", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), -1, Map.of(1L, -1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR that is _not_ a phantom, and does have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, but does not have the contest on it, check that a
   * discrepancy equal to the CVR's score results. For this test, the resylt is a 0 (an other
   * discrepancy) for all the tested assertions.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNormalCVRBallotNoContestZero(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNormalCVRBallotNoContestZero[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(A));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("B", "C", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("A", "C", TC, List.of("B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("B", "A", TC, List.of("B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 0, Map.of(1L, 0),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR that is _not_ a phantom, and does have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, but does not have the contest on it, check that a
   * discrepancy equal to the CVR's score results. For this test, the resylt is a 1 (a one vote
   * overstatement) for all the tested assertions.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNormalCVRBallotNoContestOne(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNormalCVRBallotNoContestOne[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("A", "B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("B", "C", TC, List.of("B", "C"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("D", "A", TC, List.of("D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), 1, Map.of(1L, 1),
        0, 0, 0, 0, 0);
  }

  /**
   * Given a CVR that is _not_ a phantom, and does have the assertion's contest on it, and
   * an audited ballot that _is not_ a phantom, but does not have the contest on it, check that a
   * discrepancy equal to the CVR's score results. For this test, the resylt is a -1 (a one vote
   * understatement) for all the tested assertions.
   */
  @Test(dataProvider = "AuditedRecordTypes", dataProviderClass = AssertionTests.class)
  public void testNENComputeDiscrepancyNormalCVRBallotNoContestMinusOne(RecordType recordType){
    log(LOGGER, String.format("testNENComputeDiscrepancyNormalCVRBallotNoContestMinusOne[%s]", recordType));
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(ABCD));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.empty());

    when(ABCD.consensus()).thenReturn(ConsensusValue.YES);
    when(cvr.recordType()).thenReturn(RecordType.UPLOADED);
    when(auditedCvr.recordType()).thenReturn(recordType);

    Assertion a1 = createNENAssertion("A", "C", TC, List.of("C", "D", "F"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("B", "A", TC, List.of("A", "B"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a3 = createNENAssertion("A", "D", TC, List.of("D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);

    checkComputeDiscrepancy(cvr, auditedCvr, List.of(a1, a2, a3), -1, Map.of(1L, -1),
        0, 0, 0, 0, 0);
  }

  /**
   * Create an NEN assertion with the given parameters.
   * @param winner Winner of the assertion.
   * @param loser Loser of the assertion.
   * @param contestName Name of the contest to which the assertion belongs.
   * @param continuing List of candidates assumed to be continuing.
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
  private static Assertion createNENAssertion(String winner, String loser, String contestName,
      List<String> continuing, int rawMargin, double dilutedMargin, double difficulty,
      Map<Long,Integer> cvrDiscrepancy, int oneVoteOver, int oneVoteUnder, int twoVoteOver,
      int twoVoteUnder, int other){

    Assertion a = new NENAssertion();
    AssertionTests.populateAssertion(a, winner, loser, contestName, continuing, rawMargin,
        dilutedMargin, difficulty, cvrDiscrepancy, oneVoteOver, oneVoteUnder, twoVoteOver,
        twoVoteUnder, other);

    return a;
  }

  /**
   * Reset the CVR and audited CVR mock objects with the given parameters.
   * @param cvrInfo CVRContestInfo for the CVR.
   * @param acvrInfo CVRContestInfo for the audited ballot.
   * @param cvrRecType Record type for the CVR.
   * @param acvrConsensus Consensys value for the audited ballot.
   * @param acvrRecType Record type for the audited ballot.
   */
  private void resetMocks(CVRContestInfo cvrInfo, CVRContestInfo acvrInfo, RecordType cvrRecType,
      ConsensusValue acvrConsensus, RecordType acvrRecType){
    when(cvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(cvrInfo));
    when(auditedCvr.contestInfoForContestResult(TC)).thenReturn(Optional.of(acvrInfo));

    when(acvrInfo.consensus()).thenReturn(acvrConsensus);
    when(cvr.recordType()).thenReturn(cvrRecType);
    when(auditedCvr.recordType()).thenReturn(acvrRecType);
  }

  /**
   * Return a set of NEN assertions for use when testing discrepancy computation on arbitrary
   * NENs.
   * @return A set of arbitrary NEN assertions.
   */
  private static List<Assertion> getSetAnyNEN(){
    Assertion a1 = createNENAssertion("B", "A", TC, List.of("A", "B", "C", "D"),
        50, 0.1, 8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a2 = createNENAssertion("A", "B", TC, List.of("A", "B"),50,
        0.1,8, Map.of(), 0, 0, 0,
        0, 0);
    Assertion a3 = createNENAssertion("F", "G", TC, List.of("B", "D", "F", "G"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);
    Assertion a4 = createNENAssertion("F", "G", TC, List.of("F", "G"),
        50, 0.1,8, Map.of(), 0, 0,
        0, 0, 0);
    return List.of(a1, a2, a3, a4);
  }
}