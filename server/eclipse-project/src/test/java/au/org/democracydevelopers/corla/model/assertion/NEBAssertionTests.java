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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class NEBAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NEBAssertionTests.class);

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
  @Test
  public void testNEBOptimisticSampleNoDiscrepanciesSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneOneVoteOverSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteUnderSmallMargin(){

  }

  @Test
  public void testNEBOptimisticSampleOneTwoVoteOverSmallMargin(){

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
