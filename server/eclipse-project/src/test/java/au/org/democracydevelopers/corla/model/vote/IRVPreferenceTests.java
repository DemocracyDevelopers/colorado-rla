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

import static org.testng.AssertJUnit.assertEquals;

/**
 * Tests for proper parsing of IRV choices - these are supposed to be of the form name(rank).
 * Tests include both valid complex tests (e.g. candidate names with parentheses, whitespace
 * inside parentheses), and invalid tests to check an exception is thrown.
 */
public class IRVPreferenceTests {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(IRVPreference.class);

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void parseTwoDigitPreferences() throws IRVParsingException {
    testUtils.log(LOGGER, "parseTwoDigitPreferences");
    String choice = "Alice(10)";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Alice", p.candidateName);
  }

  @Test
  public void parseTwoDigitPreferences2() throws IRVParsingException {
    testUtils.log(LOGGER, "parseTwoDigitPreferences2");

    String choice = "Candidate 1(10)";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10,(int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseTwoDigitPreferences2WithSpace() throws IRVParsingException {
    testUtils.log(LOGGER, "parseTwoDigitPreferences2WithSpace");

    String choice = "Candidate 1(10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10,(int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseTwoDigitPreferences2WithOtherSpace() throws IRVParsingException {
    testUtils.log(LOGGER, "parseTwoDigitPreferences2WithOtherSpace");

    String choice = "Candidate 1  (10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseCandidateNameWithParentheses() throws IRVParsingException {
    testUtils.log(LOGGER, "parseCandidateNameWithParentheses");

    String choice = "  Henry(8) (10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Henry(8)", p.candidateName);
  }

  @Test
  public void parseWhiteSpaceInsideParentheses() throws IRVParsingException {
    testUtils.log(LOGGER, "parseWhiteSpaceInsideParentheses");

    String choice = "  Henry (  10  ) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Henry", p.candidateName);
  }

  @Test
  public void parseChoiceWithEmptyParenthesesThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithEmptyParenthesesThrowsException");


    String choice = "CandidateWithNoPreference()";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }


  @Test
  public void parseChoiceWithNestedParenthesesThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithNestedParenthesesThrowsException");

    String choice = "CandidateWithNestedParentheses((42))";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithZeroPreferenceThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithZeroPreferenceThrowsException");

    String choice = "CandidateWithZeroPreference(0)";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithNegativePreferenceThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithNegativePreferenceThrowsException");

    String choice = "CandidateWithZeroPreference(-10)";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithFractionalPreferenceThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithFractionalPreferenceThrowsException");

    String choice = "CandidateWithFractionalPreference(2.5)";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithStringPreferenceThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithStringPreferenceThrowsException");

    String choice = "CandidateWithFractionalPreference(pref)";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithNoParenthesesThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithNoParenthesesThrowsException");

    String choice = "CandidateWithNoPreference";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }

  @Test
  public void parseChoiceWithWhiteSpaceNameThrowsException() throws IRVParsingException {
    testUtils.log(LOGGER, "parseChoiceWithWhiteSpaceNameThrowsException");

    String choice = "    (23)";

    exception.expect(IRVParsingException.class);
    new IRVPreference(choice);
  }
}
