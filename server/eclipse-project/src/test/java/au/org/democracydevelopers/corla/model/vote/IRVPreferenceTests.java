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

package au.org.democracydevelopers.model.vote;

import au.org.democracydevelopers.corla.model.vote.IRVParsingException;
import au.org.democracydevelopers.corla.model.vote.IRVPreference;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class IRVPreferenceTests {
  @Test
  public void parseTwoDigitPreferences() throws IRVParsingException {
    String choice = "Alice(10)";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Alice", p.candidateName);
  }
  @Test
  public void parseTwoDigitPreferences2() throws IRVParsingException {
    String choice = "Candidate 1(10)";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10,(int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseTwoDigitPreferences2WithSpace() throws IRVParsingException {
    String choice = "Candidate 1(10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10,(int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseTwoDigitPreferences2WithOtherSpace() throws IRVParsingException {
    String choice = "Candidate 1  (10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Candidate 1", p.candidateName);
  }

  @Test
  public void parseCandidateNameWithParentheses() throws IRVParsingException {
    String choice = "  Henry(8) (10) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Henry(8)", p.candidateName);
  }

  @Test
  public void parseWhiteSpaceInsideParentheses() throws IRVParsingException {
    String choice = "  Henry (  10  ) ";

    IRVPreference p = new IRVPreference(choice);

    assertEquals(10, (int) p.rank);
    assertEquals("Henry", p.candidateName);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithEmptyParenthesesThrowsException() throws IRVParsingException {
    String choice = "CandidateWithNoPreference()";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithNestedParenthesesThrowsException() throws IRVParsingException {
    String choice = "CandidateWithNestedParentheses((42))";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithZeroPreferenceThrowsException() throws IRVParsingException {
    String choice = "CandidateWithZeroPreference(0)";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithNegativePreferenceThrowsException() throws IRVParsingException {
    String choice = "CandidateWithZeroPreference(-10)";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithFractionalPreferenceThrowsException() throws IRVParsingException {
    String choice = "CandidateWithFractionalPreference(2.5)";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithStringPreferenceThrowsException() throws IRVParsingException {
    String choice = "CandidateWithFractionalPreference(pref)";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithNoParenthesesThrowsException() throws IRVParsingException {
    String choice = "CandidateWithNoPreference";

    IRVPreference p = new IRVPreference(choice);
  }

  @Test(expectedExceptions = IRVParsingException.class)
  public void parseChoiceWithWhiteSpaceNameThrowsException() throws IRVParsingException {
    String choice = "    (23)";

    IRVPreference p = new IRVPreference(choice);
  }
}
