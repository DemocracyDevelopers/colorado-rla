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

import au.org.democracydevelopers.corla.util.testUtils;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class NENAssertionTests {

  private static final Logger LOGGER = LogManager.getLogger(NENAssertionTests.class);

  // The following tests are designed to verify the NENAssertion all-args constructor. This
  // constructor is only designed to be used in tests for the purpose of creating assertions
  // in order to test their functionality.

  /**
   * Construction of an NEN assertion with non-positive (zero) universe size should throw an
   * IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionZeroUniverseSize(){
    testUtils.log(LOGGER, "createNENAssertionZeroUniverseSize");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 50,
        0, 1.1, List.of("A", "B", "C"));
  }

  /**
   * Construction of an NEN assertion with non-positive (negative) universe size should throw an
   * IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionNegativeUniverseSize(){
    testUtils.log(LOGGER, "createNENAssertionZeroUniverseSize");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 50,
        -1, 1.1, List.of("A", "B", "C"));
  }

  /**
   * Construction of an NEN assertion with a negative margin should throw an IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionNegativeMargin(){
    testUtils.log(LOGGER, "createNENAssertionNegativeMargin");
    Assertion a = new NENAssertion("Test Contest", "A", "B", -50,
        10000, 1.1, List.of("A", "B", "C"));
  }

  /**
   * Construction of an NEN assertion with a margin that is greater than the universe size should
   * throw an IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionExcessiveMargin(){
    testUtils.log(LOGGER, "createNENAssertionExcessiveMargin");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 5000,
        4999, 1.1, List.of("A", "B", "C"));
  }

  /**
   * Construction of an NEN assertion with an equal winner and loser should throw an
   * IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionEqualWinnerLoser(){
    testUtils.log(LOGGER, "createNENAssertionEqualWinnerLoser");
    Assertion a = new NENAssertion("Test Contest", "A", "A", 50,
        4999, 1.1, List.of("A", "B", "C"));
  }

  /**
   * Construction of an NEN assertion in which the winner does not appear in the list of
   * continuing candidates should throw an IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionWinnerNotContinuing(){
    testUtils.log(LOGGER, "createNENAssertionWinnerNotContinuing");
    Assertion a = new NENAssertion("Test Contest", "B", "A", 50,
        4999, 1.1, List.of("A", "C"));
  }

  /**
   * Construction of an NEN assertion in which the loser does not appear in the list of
   * continuing candidates should throw an IllegalArgumentException.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createNENAssertionLoserNotContinuing(){
    testUtils.log(LOGGER, "createNENAssertionLoserNotContinuing");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 50,
        4999, 1.1, List.of("A", "C"));
  }

  /**
   * Tests that the all-args NENAssertion constructor initializes the assertion's diluted margin,
   * and other attributes, correctly.
   */
  @Test()
  public void createNENAssertionTestConstruction(){
    testUtils.log(LOGGER, "createNENAssertionTestConstruction");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 57,
        5025, 1.1, List.of("A", "B", "C"));
    assertEquals(57, a.margin);
    assertEquals("A", a.winner);
    assertEquals("B", a.loser);
    assertEquals("Test Contest", a.contestName);
    assertEquals(Integer.valueOf(0), a.one_vote_over_count);
    assertEquals(Integer.valueOf(0), a.one_vote_under_count);
    assertEquals(Integer.valueOf(0), a.two_vote_over_count);
    assertEquals(Integer.valueOf(0), a.two_vote_under_count);
    assertEquals(Integer.valueOf(0), a.other_count);
    assertEquals(Collections.emptyMap(), a.cvrDiscrepancy);
    assertEquals(List.of("A", "B", "C"), a.assumedContinuing);
    assert(testUtils.doubleComparator.compare(1.0, a.current_risk.doubleValue()) == 0);
    assert(testUtils.doubleComparator.compare(1.1, a.difficulty) == 0);
    assert(testUtils.doubleComparator.compare(57/(double)5025, a.dilutedMargin.doubleValue()) == 0);
  }

  /**
   * Tests that the all-args NENAssertion constructor initializes the assertion's diluted margin
   * correctly, where the raw margin is 0.
   */
  @Test()
  public void createNENAssertionTestDilutedMarginZeroMargin(){
    testUtils.log(LOGGER, "createNENAssertionTestDilutedMarginZeroMargin");
    Assertion a = new NENAssertion("Test Contest", "A", "B", 0,
        5025, 1.1, List.of("A", "B"));
    assert(a.dilutedMargin.compareTo(BigDecimal.ZERO) == 0);
  }

  // The following tests verify NENAssertion functionality that is used for auditing, not just
  // for the construction of tests.

}