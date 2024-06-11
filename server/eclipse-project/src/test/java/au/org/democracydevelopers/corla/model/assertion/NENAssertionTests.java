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

import static au.org.democracydevelopers.corla.util.testUtils.log;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;

/**
 * A suite of tests to verify the functionality of NENAssertion objects. This includes:
 * -- Testing of optimistic sample size.
 * -- Testing of estimated sample size.
 * -- Recording of a pre-computed discrepancy.
 * -- Removal of a pre-recorded discrepancy.
 * -- Scoring of NEN assertions.
 */
public class NENAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NENAssertionTests.class);

  /**
   * Establish a mocked CVRContestInfo for use in testing Assertion scoring.
   */
  @Mock
  private CVRContestInfo cvrInfo;

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
  public static Assertion createNENAssertion(String winner, String loser, String contestName,
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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, rawMargin, dilutedMargin.doubleValue(), difficulty.doubleValue(),
        cvrDiscrepancies, oneVoteOver, oneVoteUnder, twoVoteOver, twoVoteUnder, other);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, rawMargin, dilutedMargin.doubleValue(), difficulty.doubleValue(),
        cvrDiscrepancies, oneVoteOver, oneVoteUnder, twoVoteOver, twoVoteUnder, other);

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
  public void testRecordNoMatch1(){
    log(LOGGER, "testRecordNoMatch1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8, Map.of(), 0,
        0, 0, 0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) in the context where no discrepancy has
   * been computed for the given CVR-ACVR pair, and the assertion has some discrepancies recorded
   * already. It should not change the assertion's discrepancy counts.
   */
  @Test(expectedExceptions = {RuntimeException.class})
  public void testRecordNoMatch2(){
    log(LOGGER, "testRecordNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(2L, -1,
            3L, 1), 1, 1, 0, 0, 0);

    a.recordDiscrepancy(info);
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote overvote, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testRecordOneVoteOvervote1(){
    log(LOGGER, "testRecordOneVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 1),
        0, 0, 0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(1, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a one vote undervote, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testRecordOneVoteUndervote1(){
    log(LOGGER, "testRecordOneVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, -1),
        0, 0, 0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(1, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote overvote, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testRecordTwoVoteOvervote1(){
    log(LOGGER, "testRecordTwoVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 2),
        0, 0, 0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(1, a.twoVoteOverCount.intValue());
    assertEquals(0, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testRecordTwoVoteUndervote1(){
    log(LOGGER, "testRecordTwoVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8, Map.of(1L, -2),
        0, 0, 0, 0, 0);

    a.recordDiscrepancy(info);

    assertEquals(0, a.oneVoteOverCount.intValue());
    assertEquals(0, a.oneVoteUnderCount.intValue());
    assertEquals(0, a.twoVoteOverCount.intValue());
    assertEquals(1, a.twoVoteUnderCount.intValue());
    assertEquals(0, a.otherCount.intValue());
  }

  /**
   * Test Assertion::recordDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has no recorded discrepancies.
   */
  @Test
  public void testRecordOther1(){
    log(LOGGER, "testRecordOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 0),
        0, 0, 0, 0, 0);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 0, 2L,
            1, 3L, -2, 4L, 1), 1, 0, 0,
            1, 1);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 0, 2L,
            1, 3L, -2, 4L, -1), 1, 0, 0,
            1, 1);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8,  Map.of(1L, 0,
            2L, 1, 3L, -2, 4L, 2), 1, 0,
            0, 1, 1);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8, Map.of(1L, 0, 2L,
            1, 3L, -2, 4L, -2), 1, 0, 0,
            1, 1);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest",
        AssertionTests.wlo, 50, 0.1, 8, Map.of(1L, 0,
            2L, 1, 3L, -2, 4L, 0), 1, 0,
        0, 1, 1);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
  public void testRemoveNoMatch2(){
    log(LOGGER, "testRemoveNoMatch2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
        50, 0.1, 8, Map.of(2L, -1, 3L, 1),
        1, 1, 0, 0, 0);

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

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote undervote.
   */
  @Test
  public void testRemoveOneVoteUndervote1(){
    log(LOGGER, "testRemoveOneVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overvote.
   */
  @Test
  public void testRemoveTwoVoteOvervote1(){
    log(LOGGER, "testRemoveTwoVoteOvervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRemoveTwoVoteUndervote1(){
    log(LOGGER, "testRemoveTwoVoteUndervote1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote.
   */
  @Test
  public void testRemoveOther1(){
    log(LOGGER, "testRemoveOther1");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(1L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOneVoteOvervote2(){
    log(LOGGER, "testRemoveOneVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a one vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOneVoteUndervote2(){
    log(LOGGER, "testRemoveOneVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote overvote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveTwoVoteOvervote2(){
    log(LOGGER, "testRemoveTwoVoteOvervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveTwoVoteUndervote2(){
    log(LOGGER, "testRemoveTwoVoteUndervote2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
   * Test Assertion::removeDiscrepancy(CVRAuditInfo) for a two vote undervote, in the context
   * where the assertion has already recorded discrepancies.
   */
  @Test
  public void testRemoveOther2(){
    log(LOGGER, "testRemoveOther2");
    CVRAuditInfo info = new CVRAuditInfo();
    info.setID(4L);

    Assertion a = createNENAssertion("W", "L", "Test Contest", AssertionTests.wlo,
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
  public void testScoreZero1() {
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Diego"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: zero score.
   */
  @Test
  public void testScoreZero2() {
    when(cvrInfo.choices()).thenReturn(List.of());
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of zero.
   */
  @Test
  public void testScoreZero3() {
    when(cvrInfo.choices()).thenReturn(List.of("Diego"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of zero.
   */
  @Test
  public void testScoreZero4() {
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Alice", "Diego", "Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(0, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testScoreOne1() {
    when(cvrInfo.choices()).thenReturn(List.of("Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testScoreOne2() {
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testScoreOne3() {
    when(cvrInfo.choices()).thenReturn(List.of("Alice", "Bob", "Chuan"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of one.
   */
  @Test
  public void testScoreOne4() {
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Alice", "Diego", "Chuan"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne1() {
    when(cvrInfo.choices()).thenReturn(List.of("Diego", "Chuan", "Bob", "Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne2() {
    when(cvrInfo.choices()).thenReturn(List.of("Chuan"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne3() {
    when(cvrInfo.choices()).thenReturn(List.of("Chuan", "Alice"));
    final int score = aliceNENChaun2.score(cvrInfo);
    assertEquals(-1, score);
  }

  /**
   * Test NEN assertion scoring: score of minus one.
   */
  @Test
  public void testScoreMinusOne4() {
    when(cvrInfo.choices()).thenReturn(List.of("Bob", "Chuan", "Alice"));
    final int score = aliceNENChaun1.score(cvrInfo);
    assertEquals(-1, score);
  }
}