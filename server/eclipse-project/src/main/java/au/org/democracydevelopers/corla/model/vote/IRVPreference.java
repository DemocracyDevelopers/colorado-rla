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

/**
 * A single IRV rank-name pair, part of an IRV vote (which may have several rank-name pairs).
 * The constructor parses the name-rank pair in the format expected in CO CVRs, that is,
 * name(rank), where the rank is a positive integer.
 * The comparator compares them by rank, ignoring the name.
 */
public class IRVPreference implements Comparable<IRVPreference> {

  public final Integer rank;
  public final String candidateName;

  /**
   * All args constructor
   * @param r the selected rank (a positive integer)
   * @param name the candidate's name
   */
  public IRVPreference(Integer r, String name) {
    rank = r;
    candidateName = name;
  }

  /**
   * Constructor from a csv string indicating an IRV choice: candidate name with parenthesized rank.
   * @param nameWithRank - a string expected to be of the form "name(rank)".
   * @throws IRVParsingException if the string cannot be parsed in the "name(digits)" pattern.
   */
  public IRVPreference(String nameWithRank) throws IRVParsingException {
    // Look for digits in parentheses at the end of the string.
    String regexp ="\\((\\s*\\d+\\s*\\))\\s*$";
    // Use strip() instead of trim() for unicode awareness.
    String trimmed = nameWithRank.strip();

    try {
      // Split the string into name ([0]) and rank ([1]). Take the first element as candidate name.
      String name = trimmed.split(regexp)[0];
      // Remove exactly the candidate-name substring, from the original trimmed string.
      String rankWithParentheses = trimmed.replace(name, "");
      // Get rid of the parentheses - just take the digits inside.
      String rankString = rankWithParentheses.trim().split("[\\(\\)]")[1];

      candidateName = name.trim();
      rank = Integer.parseInt(rankString.strip());

      // If we got nonsense values, we didn't parse it properly.
      if(candidateName.isBlank() || rank <= 0) {
        throw new IRVParsingException("Couldn't parse candidate-preference: " + nameWithRank);
      }
    } catch (NumberFormatException | IndexOutOfBoundsException e2) {
      throw new IRVParsingException("Couldn't parse candidate-preference: " + nameWithRank);
    }
  }

  // We only care about whether the rank is lower than the other rank, regardless of candidate name.
  @Override
  public int compareTo(IRVPreference preference) {
    return this.rank.compareTo(preference.rank);
  }

  public String toString() {
    return candidateName+"("+rank+")";
  }
}