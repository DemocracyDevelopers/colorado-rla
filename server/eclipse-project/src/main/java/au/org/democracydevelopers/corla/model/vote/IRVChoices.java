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

import org.apache.commons.lang3.StringUtils;
import au.org.democracydevelopers.corla.model.vote.IRVParsingException;

import java.util.*;
import java.util.stream.Collectors;


/**
 * A ballot is a list of (rank, candidate name) pairs. This data structure allows ballots to be
 * invalid, that is, with repeated candidate names or repeated preferences.
 * The constructor will parse a ballot that comes as a comma-separated string of the form
 * "name1(p1), name2(p2), ..." where p1, p2... are (positive integer) preferences.
 * It stores them sorted by preference, with skips or duplicates allowed.
 * The main algorithms in this class implement valid ballot interpretation for Colorado's IRV rules
 * (https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf)
 * which describe how to deal with repeated candidates, or skipped or repeated preferences.
 * Main methods:
 * - IsValid(): checks whether the choices are a valid IRV vote (without skipped or repeated
 * preferences, or repeated candidates).
 * - ValidInterpretation(): applies Colorado's IRV rules and returns the valid interpretation, as
 * an ordered list of candidate names with the highest preference first.
 */
public class IRVChoices {

  // Always sorted by preference, from highest preference (i.e. lowest number) to lowest
  // preference (i.e. highest number). Repeated or skipped preferences are allowed.
  private List<IRVPreference> choices;

  public int getLength() {
    return choices.size();
  }

  /**
   * Constructor - takes a list of IRVPreferences.
   * @param mutableChoices a list of IRVPreferences, which need not be valid - repeats or skipped
   *                       preferences are allowed.
   */
  public IRVChoices(List<IRVPreference> mutableChoices) {
    mutableChoices.sort(IRVPreference::compareTo);
    choices = Collections.unmodifiableList(mutableChoices);
  }

  /**
   * Constructor - takes IRV preferences as a string of the kind stored in the corla database,
   * of the form "name1(p1), name2(p2), ..."
   * Parses each individual preference into an IRVPreference and then calls the previous
   * constructor.
   * @param sanitizedChoices the IRV preferences as a string, which need not be a valid IRV vote -
   *                         repeats or skipped preferences are allowed.
   */
  public IRVChoices(String sanitizedChoices) throws IRVParsingException {
    this(parseChoices(sanitizedChoices));
  }

  /** Check whether the ballot is a valid list of preferences without repeats of candidate names
   * or ranks and without skipped or invalid preferences.
   */
  public boolean IsValid() {

    // Test for a perfect integer sequence of length choices.size(), by filling in the i-th element
    // when i is read. preferencesInOrder[i-1] is true if we've encountered preference i - if
    // we encounter it again, or encounter an out-of-bounds preference, return false.
    boolean[] preferencesInOrder = new boolean[choices.size()];
    for(IRVPreference p : choices) {
      // If this preference is out of bounds or already used, the preference list is invalid
      if (p.rank < 1 || p.rank > choices.size() || preferencesInOrder[p.rank-1]) {
        return false;
      }
      // Otherwise, it's fine so far. Remember that we've seen this preference.
      preferencesInOrder[p.rank-1] = true;
    }

    // Now check for repeat candidate names.
    Set<String> candidateNamesSet = new HashSet<>();
    List<String> candidateNames = choices.stream().map(c -> c.candidateName).collect(Collectors.toList());

    // Set::add returns false if the item is already present, in which case this returns false.
    // (Repeated candidate names are invalid.)
    return candidateNames.stream().map(candidateNamesSet::add).reduce(true, Boolean::logicalAnd);
  }

  /**
   * Applies validation rules in the order specified in CO Election Rules [8 CCR 1505-1]
   * https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf
   * to produce a valid IRV vote, and returns that valid IRV vote as an ordered list of candidate
   * names, with the highest-preference candidate first.
   * @return the implied valid IRV preferences, as an ordered list of candidate names with the
   * most-preferred first.
   */
  public List<String> GetValidIntent() {
    IRVChoices i3 = this.ApplyRule3();
    IRVChoices i1 = i3.ApplyRule1();
    return i1.ApplyRule2().choices.stream().map(c -> c.candidateName).collect(Collectors.toList());
  }

  /**
   * Applies Rule 26.7.1 from
   * https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf
   * Returns new updated ballot.
   * This is the rule that identifies overvotes (i.e. repeated ranks) and removes all
   * subsequent preferences.
   */
  public IRVChoices ApplyRule1() {

    assert IsSorted(choices) : "Error: Trying to apply Rule 1 to unsorted preferences.";

    List<IRVPreference> mutableChoices = new ArrayList<IRVPreference>(choices);
    for (int i=0 ; i < mutableChoices.size()-1 ; i++) {
      if ((mutableChoices.get(i)).compareTo(mutableChoices.get(i + 1)) == 0) {
        // We found an overvote. Skip from this item.
        mutableChoices.subList(i, mutableChoices.size()).clear();
        break;
    }
    }

    return new IRVChoices(mutableChoices);
  }


    /**
     * Applies Rule 26.7.2 from
     * https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf
     * This removes everything from the skipped preference onwards.
     * Returns new updated ballot.
     */
    public IRVChoices ApplyRule2() {

      assert IsSorted(choices) : "Error: Trying to apply Rule 2 to unsorted preferences.";

      // If the first element is not rank 1, the ballot is effectively blank.
      if(!choices.isEmpty() && choices.get(0).rank != 1) {
        return new IRVChoices(new ArrayList<IRVPreference>());
      }

      List<IRVPreference> mutableChoices = new ArrayList<IRVPreference>(choices);

      for (int i=0 ; i < mutableChoices.size()-1 ; i++) {
        if ((mutableChoices.get(i+1)).rank > mutableChoices.get(i).rank + 1) {
          // We found a skipped rank. Skip from the _next_ item.
          mutableChoices.subList(i+1, mutableChoices.size()).clear();
          break;
        }
      }

      return new IRVChoices(mutableChoices);
    }

    /* Applies Rule 26.7.3 from
     * https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf
     * This removes duplicated preferences for the same candidates.
     * Returns new updated ballot.
     */
    public IRVChoices ApplyRule3() {

      assert IsSorted(choices) : "Error: Trying to apply Rule 3 to unsorted preferences.";

      HashSet<String> candidates = new HashSet<>();
      List<IRVPreference> mutableChoices = new ArrayList<IRVPreference>();

      for (IRVPreference choice : choices) {
        if (!candidates.contains(choice.candidateName)) {
          // This candidate hasn't been mentioned before. Add it to both the vote and the set of mentioned candidates.
          mutableChoices.add(choice);
          candidates.add(choice.candidateName);
        }
      }

      return new IRVChoices(mutableChoices);
    }

    /* Returns this IRV vote as a sorted list of candidate names, starting with the first preference
     * and ending with the lowest.
     * Throws an exception if called on an invalid vote.
     */
    public List<String> AsSortedList() throws IRVParsingException {
      // Neither of these calls should ever be made.
      if ( !IsValid() ) {
        throw new IRVParsingException("Attempt to call AsSortedList on invalid vote: "+choices);
      }
      if (! IsSorted(choices)) {
        throw new IRVParsingException("Attempt to call AsSortedList on unsorted choices.");
      }

      return choices.stream().map(p -> p.candidateName).collect(Collectors.toUnmodifiableList());
    }

    /** Print as a human-readable string.
     *  This will include explicit preferences in parentheses.
     * Intended for votes that may not be valid,
     * e.g. "Alice(1),Bob(1),Chuan(3)".
     */
    public String toString() {
      return choices.stream().map(IRVPreference::toString).collect(Collectors.joining(","));
    }

  /**
   * Parse a string (from the database) into a list of IRV preferences. This checks each individual
   * preference for validity (a nonempty string, followed by a positive integer in parentheses) but
   * does _not_ check whether the vote is valid - repeated and skipped preferences are allowed.
   * @param sanitizedChoices a string describing the IRV vote, in the form "name1(p1),name2(p2),..."
   * @return the same information as a list of IRVPreference objects.
   * @throws IRVParsingException if any of the strings cannot be parsed as an IRVPreference.
   */
  private static List<IRVPreference> parseChoices(String sanitizedChoices) throws IRVParsingException {
    ArrayList<IRVPreference> mutableChoices = new ArrayList<>();

    String[] preferences = sanitizedChoices.trim().split(",");

    for (String preference : preferences) {
      mutableChoices.add(new IRVPreference(preference));
    }

    return mutableChoices;
  }

  /**
   * Check whether the preferences are sorted.
   * @param choices a list of IRV preferences
   * @return true if the preferences are in ascending order (highest preference / lowest number
   * first)
   */
  private boolean IsSorted(List<IRVPreference> choices) {
    for (int i=0 ; i < choices.size()-1 ; i++) {
      if ((choices.get(i)).compareTo(choices.get(i + 1)) > 0) {
        return false;
      }
    }
    return true;
  }

}
