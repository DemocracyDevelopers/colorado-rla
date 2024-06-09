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
import static java.lang.Math.log;
import static java.lang.Math.max;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.testng.annotations.DataProvider;

/**
 * This class does not contain any tests, but provides utilities for use by the
 * NEB/NEN Assertion test classes. Although much of the assertion-related functionality is
 * present in the abstract Assertion class, we have chosen to test this functionality
 * twice -- once in the context where the assertion is an NEB and then in the context
 * where the assertion is an NEN. Although, at present, it is the same code that is
 * being tested in both cases, it is possible that future development may alter the
 * assertion subclasses in potentially unforeseen ways. This extra level of testing will
 * reduce the likelihood of future programming errors if this eventuates.
 */
public class AssertionTests {

  /**
   * A 10% risk limit, as a BigDecimal.
   */
  public static final BigDecimal riskLimit10 = BigDecimal.valueOf(0.10);

  /**
   * A 5% risk limit, as a BigDecimal.
   */
  public static final BigDecimal riskLimit5 = BigDecimal.valueOf(0.05);

  /**
   * A small raw margin of 10 votes.
   */
  public static final Integer smallRawMargin = 10;

  /**
   * A small diluted margin of 0.001.
   */
  public static final BigDecimal smallMargin = BigDecimal.valueOf(0.001);

  /**
   * A high assertion difficulty value of 100.
   */
  public static final BigDecimal highDifficulty = BigDecimal.valueOf(100);

  /**
   * A large raw margin of 10000 votes.
   */
  public static final Integer largeRawMargin = 10000;

  /**
   * A large diluted margin of 0.55.
   */
  public static final BigDecimal largeMargin = BigDecimal.valueOf(0.55);

  /**
   * A small assertion difficulty of 1.
   */
  public static final BigDecimal smallDifficulty = BigDecimal.valueOf(1);

  /**
   * List of candidates called "W", "L", and "O".
   */
  public static final List<String> wlo = List.of("W", "L", "O");

  /**
   * Returns a set of varying parameters to supply when constructing assertions to test
   * sample size computations. To test sample size calculation, we consider the following dimensions:
   * -- Risk limit (5% or 10%)
   * -- Assertions with large/small diluted margins.
   * -- Assertions with varying combinations of discrepancies:
   *    -- No discrepancies;
   *    -- 1 of each type of discrepancy, rest 0.
   *    -- 2 of each type of discrepancy, rest 0.
   *    -- Combinations of discrepancies including overstatements. We could have lots of
   *       combinations here, but given that overstatements are of the highest concern, we
   *       consider 1 one vote overstatement plus 1 of each other type (alternating) and
   *       1 two vote overstatement plus 1 of each other type (alternating).
   *    -- Combinations of discrepancies including no overstatements (1 of each type).
   * @return An array of parameter arrays, each containing one combination of assertion attributes
   * that we want to test sample size computation on.
   */
  @DataProvider(name = "SampleParameters")
  public static Object[][] sampleParameters(){
    // Parameter list: risk limit, raw margin, diluted margin, difficulty, cvr discrepancy map,
    // one vote overs, one vote unders, two vote overs, two vote unders, others.
    return new Object[][]{
        // Small margin cases
        // No discrepancies
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        // one 1 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -1), 0, 1, 0, 0, 0},
        // one 1 vote overstatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 2 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -2), 0, 0, 0, 1, 0},
        // one 2 vote overstatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one other discrepancy
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 0), 0, 0, 0, 0, 1},

        // two 1 vote understatements
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -1, 2L, -1), 0, 2, 0, 0, 0},
        // two 1 vote overstatements
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 1), 2, 0, 0, 0, 0},
        // two 2 vote understatements
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -2, 2L, -2), 0, 0, 0, 2, 0},
        // two 2 vote overstatements
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, 2), 0, 0, 2, 0, 0},
        // two other discrepancies
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 0, 2L, 0), 0, 0, 0, 0, 2},

        // one 1 vote overstatement, one 1 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1), 1, 1, 0, 0, 0},
        // one 1 vote overstatement, one 2 vote overstatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        // one 1 vote overstatement, one 2 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -2), 1, 0, 0, 1, 0},
        // one 1 vote overstatement, one other discrepancy
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 0), 1, 0, 0, 0, 1},

        // one 2 vote overstatement, one 1 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, -1), 0, 1, 1, 0, 0},
        // one 2 vote overstatement, one 2 vote understatement
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, -2), 0, 0, 1, 1, 0},
        // one 2 vote overstatement, one other discrepancy
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, 0), 0, 0, 1, 0, 1},

        // one discrepancy of each type
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},

        // one discrepancy of each type (excluding overstatements)
        {riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(2L, -1, 4L, -2,
            5L, 0), 0, 1, 0, 1, 1},

        // (mostly) Large margin cases
        // No discrepancies
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(), 0, 0, 0, 0, 0},
        // one 1 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, -1), 0, 1, 0, 0, 0},
        // one 1 vote overstatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 2 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, -2), 0, 0, 0, 1, 0},
        // one 2 vote overstatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one other discrepancy
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 0), 0, 0, 0, 0, 1},

        // two 1 vote understatements
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, -1, 2L, -1), 0, 2, 0, 0, 0},
        // two 1 vote overstatements
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, 1), 2, 0, 0, 0, 0},
        // two 2 vote understatements
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, -2, 2L, -2), 0, 0, 0, 2, 0},
        // two 2 vote overstatements
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2, 2L, 2), 0, 0, 2, 0, 0},
        // two other discrepancies
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 0, 2L, 0), 0, 0, 0, 0, 2},

        // one 1 vote overstatement, one 1 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -1), 1, 1, 0, 0, 0},
        // one 1 vote overstatement, one 2 vote overstatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        // one 1 vote overstatement, one 2 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -2), 1, 0, 0, 1, 0},
        // one 1 vote overstatement, one other discrepancy
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, 0), 1, 0, 0, 0, 1},

        // one 2 vote overstatement, one 1 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2, 2L, -1), 0, 1, 1, 0, 0},
        // one 2 vote overstatement, one 2 vote understatement
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2, 2L, -2), 0, 0, 1, 1, 0},
        // one 2 vote overstatement, one other discrepancy
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2, 2L, 0), 0, 0, 1, 0, 1},

        // one discrepancy of each type
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},

        // one discrepancy of each type (excluding overstatements)
        {riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(2L, -1, 4L, -2,
            5L, 0), 0, 1, 0, 1, 1},

        // 5% risk limit (selected instances from the above).
        // No discrepancies
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        // one 1 vote understatement
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -1), 0, 1, 0, 0, 0},
        // one 1 vote overstatement
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 1 vote overstatement
        {riskLimit5, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 2 vote understatement
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -2), 0, 0, 0, 1, 0},
        // one 2 vote overstatement
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one 2 vote overstatement
        {riskLimit5, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one other discrepancy
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 0), 0, 0, 0, 0, 1},
        // one discrepancy of each type
        {riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},
        {riskLimit5, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},
        // one discrepancy of each type (excluding overstatements)
        {riskLimit5, largeRawMargin, largeMargin, smallDifficulty, Map.of(2L, -1, 4L, -2,
            5L, 0), 0, 1, 0, 1, 1},
    };
  }

  /**
   * Returns a set of varying parameters to supply when constructing assertions to test
   * estimated sample size computations in the context when varying number of ballots have
   * been sampled thus far. We consider the same dimensions as the "SampleParameters" data set,
   * with the addition of the number of ballots audited thus far.
   *
   * @return An array of parameter arrays, each containing one combination of assertion attributes
   * that we want to test estimated sample size computation on.
   */
  @DataProvider(name = "ParametersVaryingSamples")
  public static Object[][] parametersVaryingSamples(){
    // Parameter list: audited samples thus far, risk limit, raw margin, diluted margin, difficulty,
    // cvr discrepancy map, one vote overs, one vote unders, two vote overs, two vote unders, others.
    return new Object[][]{
        // No discrepancies
        {0, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        {0, riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        {0, riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(), 0, 0, 0, 0, 0},
        {0, riskLimit5, largeRawMargin, largeMargin, smallDifficulty, Map.of(), 0, 0, 0, 0, 0},

        // No discrepancies
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        // one 1 vote understatement
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -1), 0, 1, 0, 0, 0},
        // one 1 vote overstatement
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        {1, riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        {1, riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 2 vote understatement
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -2), 0, 0, 0, 1, 0},
        // one 2 vote overstatement
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        {1, riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        {1, riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one other discrepancy
        {1, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 0), 0, 0, 0, 0, 1},

        // No discrepancies
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(), 0, 0, 0, 0, 0},
        // one 1 vote understatement
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -1), 0, 1, 0, 0, 0},
        // one 1 vote overstatement
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        {101, riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        {101, riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1), 1, 0, 0, 0, 0},
        // one 2 vote understatement
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, -2), 0, 0, 0, 1, 0},
        // one 2 vote overstatement
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        {101, riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        {101, riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 2), 0, 0, 1, 0, 0},
        // one other discrepancy
        {101, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 0), 0, 0, 0, 0, 1},

        // one 1 vote overstatement, one 1 vote understatement
        {37, riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1), 1, 1, 0, 0, 0},
        // one 1 vote overstatement, one 2 vote overstatement
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        {37,riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        {37,riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        // Very large sample count
        {10034,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        {10034,riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        {10034,riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, 2), 1, 0, 1, 0, 0},
        // one 1 vote overstatement, one 2 vote understatement
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -2), 1, 0, 0, 1, 0},
        // one 1 vote overstatement, one other discrepancy
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, 0), 1, 0, 0, 0, 1},

        // one 2 vote overstatement, one 1 vote understatement
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, -1), 0, 1, 1, 0, 0},
        // one 2 vote overstatement, one 2 vote understatement
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, -2), 0, 0, 1, 1, 0},
        // one 2 vote overstatement, one other discrepancy
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 2, 2L, 0), 0, 0, 1, 0, 1},

        // one discrepancy of each type
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},
        {37,riskLimit5, smallRawMargin, smallMargin, highDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},
        {37,riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},
        {10061,riskLimit10, largeRawMargin, largeMargin, smallDifficulty, Map.of(1L, 1, 2L, -1, 3L,
            2, 4L, -2, 5L, 0), 1, 1, 1, 1, 1},

        // one discrepancy of each type (excluding overstatements)
        {37,riskLimit10, smallRawMargin, smallMargin, highDifficulty, Map.of(2L, -1, 4L, -2,
            5L, 0), 0, 1, 0, 1, 1},
    };
  }

  /**
   * Populate the attributes of the given assertion according to the given parameters.
   * @param a Assertion to be configured.
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
   */
  public static void populateAssertion(Assertion a, String winner, String loser, String contestName,
      List<String> continuing, int rawMargin, double dilutedMargin, double difficulty,
      Map<Long,Integer> cvrDiscrepancy, int oneVoteOver, int oneVoteUnder, int twoVoteOver,
      int twoVoteUnder, int other){

    a.winner = winner;
    a.loser  = loser;
    a.contestName = contestName;
    a.assumedContinuing = continuing;
    a.margin = rawMargin;
    a.dilutedMargin = BigDecimal.valueOf(dilutedMargin);
    a.difficulty = difficulty;
    a.cvrDiscrepancy.putAll(cvrDiscrepancy);
    a.oneVoteOverCount = oneVoteOver;
    a.oneVoteUnderCount = oneVoteUnder;
    a.twoVoteOverCount = twoVoteOver;
    a.twoVoteUnderCount = twoVoteUnder;
    a.otherCount = other;
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

  /**
   * Compute the estimated number of ballots to sample for an assertion with the given
   * parameters, and the given risk limit, under the assumption that overstatements will
   * continue at the current rate. This function implements the sample size
   * estimation as described in Section 2.2.1 of the Colorado IRV RLA Implementation Plan.
   * @param riskLimit Risk limit of the audit.
   * @param dilutedMargin Diluted margin of the assertion.
   * @param o1 Number of one vote overstatements.
   * @param o2 Number of two vote overstatements.
   * @param u1 Number of one vote understatements.
   * @param u2 Number of two vote understatements.
   * @param gamma Gamma parameter.
   * @param auditedSamples Number of ballots audited thus far.
   * @return the estimated number of ballots to sample when auditing the assertion with
   * the given characteristics, under the assumption that overstatements will continue at
   * the current rate,
   */
  public static int estimated(BigDecimal riskLimit, double dilutedMargin, int o1, int o2,
      int u1, int u2, BigDecimal gamma, int auditedSamples){

    final int optimistic = optimistic(riskLimit, dilutedMargin, o1, o2, u1, u2, gamma);
    if(auditedSamples == 0){
      return optimistic;
    }
    return (int)ceil(optimistic * (1 + (o1+o2)/(double)auditedSamples));
  }
}
