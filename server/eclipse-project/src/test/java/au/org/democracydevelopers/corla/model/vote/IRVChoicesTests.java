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

package au.org.democracydevelopers.corla.model.vote;

import au.org.democracydevelopers.corla.testUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.*;

/**
 * Tests for proper interpretation of IRV choices, including proper interpretation of invalid
 * choices involving repeated candidate names and repeated or skipped preferences.
 * The implementation follows the rules specified in Colorado's Guide to Voter Intent -
 * Determination of Voter Intent for Colorado Elections, 2023 addendum for Instant Runoff Voting
 * (IRV), available at
 * <a href="https://assets.bouldercounty.gov/wp-content/uploads/2023/11/Voter-Intent-Guide-IRV-Addendum-2023.pdf">...</a>
 * Examples are taken directly from the Guide.
 */
public class IRVChoicesTests {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(IRVPreference.class);

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  /**
   * Using the DuplicatesBeforeOvervotes function and testing against the
   * specific examples in the Guide.
   * This has a repeated first preference and should hence have empty valid interpretation.
   */
  @Test
  public void Example1OvervotesNoValidRankings() throws IRVParsingException {
    testUtils.log(LOGGER, "Example1OvervotesNoValidRankings");

    IRVChoices b = new IRVChoices("Candidate A(1),Candidate B(1),Candidate C(1),Candidate C(2),Candidate B(3)");
    List<String> expected = new ArrayList<>();
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Second preference is duplicated, so only the first is valid.
   * @throws IRVParsingException never
   */
  @Test
  public void Example2OvervoteWithValidRankings() throws IRVParsingException {
    testUtils.log(LOGGER,"Example2OvervoteWithValidRankings");

    IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate C(2),Candidate C(3)");
    List<String> expected = List.of("Candidate B");
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Preference 2 is skipped, so everything afterwards is ignored.
   * @throws IRVParsingException never
   */
  @Test
    public void Example1SkippedRankings() throws IRVParsingException {
    testUtils.log(LOGGER,"Example1SkippedRankings");

    IRVChoices b = new IRVChoices("Candidate A(1),Candidate B(3)");
    List<String> expected = List.of("Candidate A");
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Candidate A has duplicate preferences, so only the first counts. This causes preference 2 to
   * be skipped, so everything later is ignored.
   */
  @Test
  public void Example1DuplicateRankings() throws IRVParsingException {
    testUtils.log(LOGGER,"Example1DuplicateRankings");

    IRVChoices b = new IRVChoices("Candidate A(1),Candidate A(2),Candidate B(3)");
    List<String> expected = List.of("Candidate A");
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Preference 2 is overvoted, so only the first preference counts.
   * @throws IRVParsingException never
   */
  @Test
  public void Example1DuplicatesAndOvervotes() throws IRVParsingException {
    testUtils.log(LOGGER,"Example1DuplicatesAndOvervotes");

    IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate C(2),Candidate C(3)");
    List<String> expected = List.of("Candidate B");
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Duplicates are removed before overvotes, so the preference for CandidateB(2) is removed
   * (it duplicates B(1)), hence leaving no overvote for preference 2.
   * @throws IRVParsingException never
   */
  @Test
  public void Example2DuplicatesAndOvervotes() throws IRVParsingException {
    testUtils.log(LOGGER,"Example2DuplicatesAndOvervotes");

    IRVChoices b = new IRVChoices("Candidate B(1),Candidate A(2),Candidate B(2),Candidate C(3)");
    List<String> expected = List.of("Candidate B", "Candidate A", "Candidate C");
    List<String> validInterpretation = b.GetValidIntent();
    assertEqualListsOfStrings(expected, validInterpretation);
  }

  /**
   * Other general validity tests for ballot interpretation,
   * including applying single rules and checking the validity
   * of the output.
   * Valid choices.
   * @throws IRVParsingException never
   */
  @Test
  public void testValidBallots() throws IRVParsingException {
    testUtils.log(LOGGER,"testValidBallots");

    IRVChoices b = new IRVChoices("Alice(1)");
    assertTrue(b.IsValid());

    b = new IRVChoices("Alice(1),Bob(2)");
    assertTrue(b.IsValid());

    b = new IRVChoices("Alice(1),Chuan(3),Bob(2)");
    assertTrue(b.IsValid());

    b = new IRVChoices("Diego(4),Alice(1),Chuan(3),Bob(2)");
    assertTrue(b.IsValid());
  }

  /**
   * Invalid choices. Check that they're correctly identified as not valid.
   * @throws IRVParsingException never.
   */
  @Test
  public void testInvalidBallot() throws IRVParsingException {
    testUtils.log(LOGGER,"testInvalidBallot");

    IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
    assertFalse(b.IsValid());

    b = new IRVChoices("Alice(1),Alice(2)");
    assertFalse(b.IsValid());

    b = new IRVChoices("Alice(1),Bob(1)");
    assertFalse(b.IsValid());

    b = new IRVChoices("Alice(1),Bob(3)");
    assertFalse(b.IsValid());

    b = new IRVChoices("Alice(2),Bob(2)");
    assertFalse(b.IsValid());

    b = new IRVChoices("Alice(2),Bob(3)");
    assertFalse(b.IsValid());
    }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * Not applicable.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test1");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(2, i.getLength());
  }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * Removes everything.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test2() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test2");

    IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(0, i.getLength());
  }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * No change (though this vote is invalid for other reasons).
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test3() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test3");

    IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(2, i.getLength());
  }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * Removes repeat 2nd preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test4() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test4");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(1, i.getLength());
    assertEquals("Alice",i.GetValidIntent().get(0));
  }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * Removes repeat 2nd preference and the subsequent preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test5() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test5");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(3)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(1, i.getLength());
    assertEquals("Alice",i.GetValidIntent().get(0));
  }

  /**
   * Test rule 1 - removing overvotes (repeated preferences).
   * Removes repeat 3rd preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1Test6() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1Test6");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3),Diego(3)");
    IRVChoices i = b.ApplyRule1();
    assertEquals(2, i.getLength());
    assertEquals("Alice",i.GetValidIntent().get(0));
    assertEquals("Bob",i.GetValidIntent().get(1));
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Not applicable.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test1");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(2, i2.getLength());
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Not applicable (though this vote is invalid for other reasons).
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test2() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test2");

    IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(2, i2.getLength());
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Not applicable (though this vote is invalid for other reasons).
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test3() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test3");

    IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(2, i2.getLength());
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Not applicable.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test4() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test4");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(3, i2.getLength());
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Removes all but the first preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test5() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test5");

    IRVChoices b = new IRVChoices("Alice(1),Bob(3)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(1, i2.getLength());
    assertEquals("Alice",i2.GetValidIntent().get(0));
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * First rank skipped - remove all.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test6() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test6");

    IRVChoices b = new IRVChoices("Bob(2),Chuan(3)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(0, i2.getLength());
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * Third rank skipped - remove the 4th.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test7() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test7");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(4)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(2, i2.getLength());
    assertEquals("Alice",i2.GetValidIntent().get(0));
    assertEquals("Bob",i2.GetValidIntent().get(1));
  }

  /**
   * Test rule 2 - removing skipped rankings.
   * First rank skipped - remove all.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule2Test8() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2Test8");

    IRVChoices b = new IRVChoices("Alice(3),Bob(2),Chuan(4)");
    IRVChoices i2 = b.ApplyRule2();
    assertEquals(0, i2.getLength());
  }

  /**
   * Apply rule 1 and then rule 2.
   * The overvoted second rank is removed, then everything afterwards.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1AndRule2Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1AndRule2Test1");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(4)");
    IRVChoices i = b.ApplyRule1();
    IRVChoices i2 = i.ApplyRule2();
    assertEquals(1, i2.getLength());
    assertEquals("Alice",i2.GetValidIntent().get(0));
  }

  /**
   * Apply rule 2 and then rule 1. Same result.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule1AndRule2Test2() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1AndRule2Test2");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(2),Diego(4)");
    IRVChoices i2 = b.ApplyRule2();
    IRVChoices i1 = i2.ApplyRule1();
    assertEquals(1, i1.getLength());
    assertEquals("Alice",i2.GetValidIntent().get(0));
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * Not applicable.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test1");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(2, i3.getLength());
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * Result is a single valid 1st preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test2() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test2");

    IRVChoices b = new IRVChoices("Alice(1),Alice(1)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(1, i3.getLength());
    assertEquals("Alice",i3.GetValidIntent().get(0));
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * Result is a single valid 1st preference.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test3() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test3");

    IRVChoices b = new IRVChoices("Alice(2),Alice(1)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(1, i3.getLength());
    assertEquals("Alice",i3.GetValidIntent().get(0));
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * Not applicable.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test4() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test4");

    IRVChoices b = new IRVChoices("Alice(1),Bob(2),Chuan(3)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(3, i3.getLength());
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * Not applicable (though the vote is invalid for other reasons).
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test5() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test5");

    IRVChoices b = new IRVChoices("Alice(1),Bob(3)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(2, i3.getLength());
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * The second preference for Alice is removed, leaving two valid preferences.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test6() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test6");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(2, i3.getLength());
    assertEquals("Alice",i3.GetValidIntent().get(0));
    assertEquals("Bob",i3.GetValidIntent().get(1));
  }

  /**
   * Apply rule 3 - removing all but the highest expressed preference for a given candidate.
   * The second preference for Alice and third preference for Bob are removed, leaving a vote
   * that is invalid for other reasons.
   * @throws IRVParsingException never
   */
  @Test
  public void applyRule3Test7() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test7");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2),Chuan(4),Bob(3)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(3, i3.getLength());
    assertEquals("Alice",i3.GetValidIntent().get(0));
    assertEquals("Bob",i3.GetValidIntent().get(1));

    exception.expect(IndexOutOfBoundsException.class);
    i3.GetValidIntent().get(2);
  }

  @Test
  public void applyRule3Test8() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3Test8");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Alice(4)");
    IRVChoices i3 = b.ApplyRule3();
    assertEquals(1, i3.getLength());
  }

  @Test
  public void applyRule3AndRule2Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3AndRule2Test1");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
    IRVChoices i3 = b.ApplyRule3();
    IRVChoices i2 = i3.ApplyRule2();
    assertEquals(2, i2.getLength());
    assertTrue(i2.IsValid());
  }

  @Test
  public void applyRule3AndRule2Test3() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3AndRule2Test3");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(3)");
    IRVChoices i3 = b.ApplyRule3();
    IRVChoices i2 = i3.ApplyRule2();
    assertEquals(1, i2.getLength());
    assertTrue(i2.IsValid());
  }

  @Test
  public void applyRule2AndRule3Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2AndRule3Test1");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
    IRVChoices i2 = b.ApplyRule2();
    IRVChoices i3 = i2.ApplyRule3();
    assertEquals(2, i3.getLength());
    assertTrue(i3.IsValid());
  }

  @Test
  public void applyRule2AndRule3Test3() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule2AndRule3Test3");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(3)");
    IRVChoices i2 = b.ApplyRule2();
    IRVChoices i3 = i2.ApplyRule3();
    assertEquals(2, i3.getLength());
    assertFalse(i3.IsValid());
  }

  @Test
  public void applyRule1AndRule3Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule1AndRule3Test1");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
    IRVChoices i1 = b.ApplyRule1();
    IRVChoices i3 = i1.ApplyRule3();
    assertEquals(1, i3.getLength());
    assertTrue(i3.IsValid());
  }

  @Test
  public void applyRule3AndRule1Test1() throws IRVParsingException {
    testUtils.log(LOGGER,"applyRule3AndRule1Test1");

    IRVChoices b = new IRVChoices("Alice(1),Alice(2),Bob(2)");
    IRVChoices i3 = b.ApplyRule3();
    IRVChoices i1 = i3.ApplyRule1();
    assertEquals(2, i1.getLength());
    assertTrue(i1.IsValid());
  }

  /**
   * Check that two lists of strings are identical.
   *
   * @param l1 a list of strings
   * @param l2 another list of strings
   */
  private void assertEqualListsOfStrings(List<String> l1, List<String> l2) {

    assertEquals(l1.size(), l2.size());

    for (int i = 0; i < l1.size(); i++) {
      assertEquals(l1.get(i), l2.get(i));
    }
  }
}
