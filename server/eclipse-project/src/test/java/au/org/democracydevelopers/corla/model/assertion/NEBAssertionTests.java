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

import static java.lang.Math.ceil;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import us.freeandfair.corla.math.Audit;

public class NEBAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NEBAssertionTests.class);

  private final BigDecimal riskLimit10 = BigDecimal.valueOf(0.10);

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
      int rawMargin, double dilutedMargin, double difficulty,
      Map<Long,Integer> cvrDiscrepancy, int oneVoteOver, int oneVoteUnder,
      int twoVoteOver, int twoVoteUnder, int other){

    Assertion a = new NEBAssertion();
    a.winner = winner;
    a.loser  = loser;
    a.contestName = contestName;
    a.margin = rawMargin;
    a.dilutedMargin = BigDecimal.valueOf(dilutedMargin);
    a.difficulty = difficulty;
    a.cvrDiscrepancy = cvrDiscrepancy;
    a.oneVoteOverCount = oneVoteOver;
    a.oneVoteUnderCount = oneVoteUnder;
    a.twoVoteOverCount = twoVoteOver;
    a.twoVoteUnderCount = twoVoteUnder;
    a.otherCount = other;

    return a;
  }

  /**
   * Compute the optimistic number of ballots to sample for an assertion with the given
   * parameters, and the given risk limit. This function implements the sample size
   * estimation as described in Section 2.2.1 of the Colorado IRV RLA Implementation Plan.
   * @param riskLimit Risk limit of the audit.
   * @param dilutedMargin Diluted margin of the assertion.
   * @param o1 Number of one vote overstatements.
   * @param o2 Number of two vote overstatements.
   * @param u1 Number of one vote understatements.
   * @param u2 Number of two vote understatements.
   * @param gamma Gamma parameter.
   * @return the optimistic number of ballots to sample when auditing the assertion with
   * the given characteristics.
   */
  public static int optimistic(BigDecimal riskLimit, double dilutedMargin, int o1, int o2,
      int u1, int u2, BigDecimal gamma){
    final double dgamma = gamma.doubleValue();
    final double oneOnOneGamma = 1/dgamma;
    final double oneOnTwoGamma = 1/(2*dgamma);
    final double factor = (-2 * dgamma)/dilutedMargin;

    final int totalDiscrepancies = o1 + o2 + u1 + u2;

    return max(totalDiscrepancies, (int)ceil(factor * (log(riskLimit.doubleValue()) +
        u1 * log(1 + oneOnTwoGamma) + u2 * log(1 + oneOnOneGamma) + o1 * log(1 - oneOnTwoGamma) +
        o2 * log(1 - oneOnOneGamma))));
  }

  // To test sample size calculation, we consider the following dimensions:
  // -- Assertions with large/small diluted margins.
  // -- Assertions with varying combinations of discrepancies:
  //      -- No discrepancies;
  //      -- 1 of each type of discrepancy, rest 0.
  //      -- 2 of each type of discrepancy, rest 0.
  //      -- Combinations of discrepancies including overstatements. We could have lots of
  //         combinations here, but given that overstatements are of the highest concern, we
  //         consider 1 one vote overstatement plus 1 of each other type (alternating) and
  //         1 two vote overstatement plus 1 of each other type (alternating).
  //      -- Combinations of discrepancies including no overstatements (1 of each type).

  // Dimension: small diluted margin

  /**
   * Check that the correct optimistic sample size is computed for an NEB assertion
   * with a small diluted margin and no discrepancies.
   */
  @Test
  public void testNEBOptimisticSampleNoDiscrepanciesSmallMargin(){
    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        10, 0.001, 100, Map.of(), 0,
        0, 0, 0, 0);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit10);
    final int expected = optimistic(riskLimit10, 0.001, 0, 0, 0, 0,
        Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  /**
   * Check that the correct optimistic sample size is computed for an NEB assertion
   * with a small diluted margin and one 1-vote understatement.
   */
  @Test
  public void testNEBOptimisticSampleOneOneVoteUnderSmallMargin(){
    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        10, 0.001, 100, Map.of(1L, -1), 0,
        1, 0, 0, 0);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit10);
    final int expected = optimistic(riskLimit10, 0.001, 0, 0, 1, 0,
        Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  /**
   * Check that the correct optimistic sample size is computed for an NEB assertion
   * with a small diluted margin and one 1-vote overstatement.
   */
  @Test
  public void testNEBOptimisticSampleOneOneVoteOverSmallMargin(){
    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        10, 0.001, 100, Map.of(1L, 1), 1,
        0, 0, 0, 0);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit10);
    final int expected = optimistic(riskLimit10, 0.001, 1, 0, 0, 0,
        Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  /**
   * Check that the correct optimistic sample size is computed for an NEB assertion
   * with a small diluted margin and one 2-vote understatement.
   */
  @Test
  public void testNEBOptimisticSampleOneTwoVoteUnderSmallMargin(){
    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        10, 0.001, 100, Map.of(1L, -2), 0,
        0, 0, 1, 0);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit10);
    final int expected = optimistic(riskLimit10, 0.001, 0, 0, 0, 1,
        Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  /**
   * Check that the correct optimistic sample size is computed for an NEB assertion
   * with a small diluted margin and one 2-vote overstatement.
   */
  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverSmallMargin(){
    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        10, 0.001, 100, Map.of(1L, 2), 0,
        0, 1, 0, 0);

    final int result = a.computeOptimisticSamplesToAudit(riskLimit10);
    final int expected = optimistic(riskLimit10, 0.001, 0, 1, 0, 0,
        Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

  @Test
  public void testNEBOptimisticSampleOneOtherSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOneVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOneVoteOverSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoTwoVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoTwoVoteOverSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOtherSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneTwoVoteOverSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneOneVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneTwoVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOtherSmallMargin(){

  }


  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOneOneVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOneTwoVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOtherSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOfEachTypeNoOverstatementsSmallMargin(){

  }

  // Dimension: large diluted margin
  @Test
  public void testNEBOptimisticSampleNoDiscrepanciesLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOtherLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOneVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOneVoteOverLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoTwoVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoTwoVoteOverLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleTwoOtherLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneTwoVoteOverLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneOneVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOneTwoVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverOtherLargeMargin(){

  }


  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOneOneVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOneTwoVoteUnderLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverOtherLargeMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOfEachTypeNoOverstatementsLargeMargin(){

  }
}
