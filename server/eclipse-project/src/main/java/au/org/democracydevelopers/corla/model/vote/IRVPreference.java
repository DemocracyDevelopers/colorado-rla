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

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.model.Choice;

import java.util.ArrayList;
import java.util.List;

/**
 * A single IRV name-rank pair, part of an IRV vote (which may have several name-rank pairs).
 * The constructor parses the name-rank pair in the format expected in CO CVRs, that is,
 * name(rank), where the rank is a positive integer.
 * The comparator compares them by rank, ignoring the name.
 */
public class IRVPreference implements Comparable<IRVPreference> {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(IRVPreference.class);

  public final int rank;
  public final String candidateName;

  /**
   * All args constructor
   * Constructs an IRVPreference with the given rank and candidate name.
   * @param r the selected rank (a positive integer)
   * @param name the candidate's name
   */
  public IRVPreference(int r, String name) {
    rank = r;
    candidateName = name;
  }

  /**
   * Constructor from a csv string indicating an IRV choice: candidate name with parenthesized rank.
   * Extracts the name and rank of an IRV preference, provided as a string, and constructs an
   * IRVPreference. The given string should contain a candidate name followed by a parenthesized
   * rank.
   * @param nameWithRank - a string expected to be of the form "name(rank)".
   * @throws IRVParsingException if the string cannot be parsed in the "name(digits)" pattern.
   */
  public IRVPreference(String nameWithRank) throws IRVParsingException {
    // Use strip() instead of trim() for unicode awareness.
    String trimmed = nameWithRank.strip();

    try {
      // Look for digits in parentheses at the end of the string.
      // Split the string into name ([0]) and rank ([1]). Take the first element as candidate name.
      String name = trimmed.split("\\((\\s*\\d+\\s*\\))\\s*$")[0];
      // Remove exactly the candidate-name substring, from the original trimmed string.
      String rankWithParentheses = trimmed.replace(name, "");
      // Get rid of the parentheses - just take the digits inside.
      String rankString = rankWithParentheses.trim().split("[()]")[1];

      candidateName = name.trim();
      rank = Integer.parseInt(rankString.strip());

      // If we got nonsense values, we didn't parse it properly.
      if(candidateName.isBlank() || rank <= 0) {
        throw new IRVParsingException();
      }
    } catch (NumberFormatException | IndexOutOfBoundsException | IRVParsingException e) {
      final String prefix = "[IRVChoices constructor]";
      final String errorMessage = "Couldn't parse candidate-preference: ";
      LOGGER.error(String.format("%s %s %s", prefix, errorMessage, nameWithRank), e);
      throw new IRVParsingException(errorMessage + nameWithRank);
    }
  }

  /**
   * Checks whether a CSVRecord contains a sequence of choices as expected for IRV - it should be
   * a sequence of name(rank) choices, starting with preference 1 and going up to irvVotesAllowed,
   * and for each preference, it should contain all the same choices. For example,
   * Alice(1), Bob(1), Chuan(1), Alice(2), Bob(2), Chuan(2), Alice(3), Bob(3), Chuan(3) is valid,
   * but any rearrangement of those items, or anything that omits some rank or candidate included
   * elsewhere, is invalid.
   * @param theLine the CSV line (basically a list of Strings)
   * @param startIndex the first index for this contest's choices.
   * @param numChoices the number of choices (candidates).
   * @param maxRank The number of ranks allowed (which matches the number expected in the
   *                       csv).
   * @throws IRVParsingException if either an individual choice can't be parsed as name(rank), or
   *                             the overall collection of choices doesn't fit the pattern above.
   */
  public static void validateIRVPreferenceHeaders(CSVRecord theLine, int startIndex, int numChoices,
                                                  int maxRank) throws IRVParsingException {
    final String prefix = "[valicateIRVChoiceHeaders] ";

    // Iterate through all consecutive pairs of ranks, checking that they have the expected choice
    // counts and correct ranks.
    for (int rank =1; rank < maxRank ; rank++) {
      checkNamesForNextPreference(theLine, startIndex, numChoices, 1);
    }
  }

  /**
   * Get the candidate names as an ordered list, for a given rank.
   * Check that the line has names of the form name_1(rank), name_2(rank), ... name_numChoices(rank)
   * from the given startIndex, for the given rank.
   * FIXME fix up comment.
   * @param theLine the CSV line (an indexed list of strings).
   * @param startIndex the first index where we expect a name(rank) entry of interest.
   * @param numChoices the number of candidates.
   * @param startRank the starting rank - these choices will be compared with startRank+1.
   * @return the candidate names as a list of Strings, in the order they appeared, if they fit
   * the expected pattern with the right rank and length..
   */
  private static void checkNamesForNextPreference(CSVRecord theLine, int startIndex,
                                             int numChoices, int startRank) throws IRVParsingException {

    final String prefix = "[getNamesForPreference] ";

    // irv1 iterates along all the choices of a given rank (startRank), while
    // irv2 iterates along all the choices of the next rank (startRank+1).
    // Check whether they have the same candidate name and the expected rank.
    for (int i = 0 ; i < numChoices ; i++) {
      IRVPreference irv1 = new IRVPreference(theLine.get(startIndex+i));
      IRVPreference irv2 = new IRVPreference(theLine.get(startIndex+i+numChoices));
      if(irv1.rank != startRank || irv2.rank != startRank + 1 || !irv1.candidateName.equals(irv2.candidateName)) {
        final String msg = "Invalid IRV choices header: ";
        LOGGER.error(String.format("%s %s", prefix, msg + theLine));
        throw new IRVParsingException(msg + theLine);
      }
    }
  }

  /**
   * This is the almost-inverse of validateIRVPreferenceHeaders. Given a list of (plain-name)
   * choices, and a number of allowed ranks, it generates the complete list of all name(rank)
   * strings, by iterating through ranks from 1 to maxRank and, within each rank, iterating through
   * all names.
   * @param choices the list of candidate names.
   * @param maxRank the maximum allowed rank. (Minimum is always 1.)
   * @return a list of IRV choices in the expected pattern, e.g.
   * Alice(1), Bob(1), Chuan(1), Alice(2), Bob(2), Chuan(2), Alice(3), Bob(3), Chuan(3) is valid,
   */
  public static List<Choice> generateAllIRVPreferences(List<Choice> choices, int maxRank) {
    List<Choice> allPrefs = new ArrayList<>();
    for(int rank = 1 ; rank <= maxRank ; rank++) {
      for (Choice c : choices) {
        allPrefs.add(new Choice(
            c.name()+"("+rank+")",
            c.description(),
            c.qualifiedWriteIn(),
            c.fictitious()
        ));
      }
    }
    return allPrefs;
  }

  /**
   * Compares this, and the given IRVPreference 'preference', in terms of rank. This comparison
   * does not consider the candidate names of the two preferences, only their integer ranks.
   */
  @Override
  public int compareTo(IRVPreference preference) {
    return Integer.compare(rank, preference.rank);
  }

  /**
   * @return the IRVPreference as a human-readable string with the candidate name followed by the
   * rank in parentheses, e.g. "Diego(1)".
   */
  public String toString() {
    return candidateName+"("+rank+")";
  }
}