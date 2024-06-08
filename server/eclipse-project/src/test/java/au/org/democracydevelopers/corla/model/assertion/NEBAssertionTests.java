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

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import us.freeandfair.corla.math.Audit;

/**
 * A suite of tests to verify the functionality of NEBAssertion objects. This includes:
 * -- Testing of optimistic sample size.
 */
public class NEBAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NEBAssertionTests.class);

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
  public void testNEBOptimistic(Integer rawMargin, BigDecimal dilutedMargin, BigDecimal difficulty,
      Map<Long,Integer> cvrDiscrepancies, Integer oneVoteOver, Integer oneVoteUnder,
      Integer twoVoteOver, Integer twoVoteUnder, Integer other){

    Assertion a = createNEBAssertion("W", "L", "Test Contest",
        rawMargin, dilutedMargin.doubleValue(), difficulty.doubleValue(), cvrDiscrepancies,
        oneVoteOver, oneVoteUnder, twoVoteOver, twoVoteUnder, other);

    final int result = a.computeOptimisticSamplesToAudit(AssertionTests.riskLimit10);
    final int expected = AssertionTests.optimistic(AssertionTests.riskLimit10,
        dilutedMargin.doubleValue(), oneVoteOver, twoVoteOver, oneVoteUnder, twoVoteUnder, Audit.GAMMA);

    assertEquals(result, expected);
    assertEquals(a.optimisticSamplesToAudit.intValue(), expected);
  }

}
