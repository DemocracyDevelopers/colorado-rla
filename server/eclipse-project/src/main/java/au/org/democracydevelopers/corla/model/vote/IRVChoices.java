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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


/**
 * A ballot is a list of (rank, candidate name) pairs. This data structure allows ballots to be
 * invalid, that is, with repeated candidate names or repeated preferences.
 * The constructor will parse a ballot that comes as a comma-separated string of the form
 * "name1(p1), name2(p2), ..." where p1, p2... are (positive integer) preferences.
 * It stores them sorted by preference, with skips or duplicates allowed.
 * The main algorithms in this class implement valid ballot interpretation for Colorado's IRV rules
 * (<a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>)
 * which describe how to deal with repeated candidates, or skipped or repeated preferences.
 * The order of application of the rules follows Colorado's Guide to Voter Intent -
 * Determination of Voter Intent for Colorado Elections, 2023 addendum for Instant Runoff Voting
 * (IRV), available at
 * <a href="https://assets.bouldercounty.gov/wp-content/uploads/2023/11/Voter-Intent-Guide-IRV-Addendum-2023.pdf">...</a>
 * Main methods:
 * - IsValid(): checks whether the choices are a valid IRV vote (without skipped or repeated
 * preferences, or repeated candidates).
 * - ValidInterpretation(): applies Colorado's IRV rules and returns the valid interpretation, as
 * an ordered list of candidate names with the highest preference first.
 */
public class IRVChoices {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(IRVChoices.class);

  /**
   * choices is always sorted by preference, from highest preference (i.e. lowest number) to lowest
   * preference (i.e. highest number). Repeated candidates and repeated or skipped preferences are
   * allowed. choices are final and immutable.
   */
  private final List<IRVPreference> choices;

  public int getLength() {
    return choices.size();
  }

  /**
   * Constructor - takes a list of IRVPreferences.
   *
   * @param mutableChoices a list of IRVPreferences, which need not be valid - repeats or skipped
   *                       preferences are allowed.
   */
  public IRVChoices(List<IRVPreference> mutableChoices) {
    final String prefix = "[IRVChoices constructor]";
    LOGGER.debug(String.format("%s interpreting IRV Preferences %s", prefix, mutableChoices));

    mutableChoices.sort(IRVPreference::compareTo);
    choices = Collections.unmodifiableList(mutableChoices);
  }

  /**
   * Constructor - takes IRV preferences as a string of the kind stored in the corla database,
   * of the form "name1(p1), name2(p2), ..."
   * Parses each individual preference into an IRVPreference and then calls the previous
   * constructor.
   *
   * @param sanitizedChoices the IRV preferences as a string, which need not be a valid IRV vote -
   *                         repeats or skipped preferences are allowed.
   */
  public IRVChoices(String sanitizedChoices) throws IRVParsingException {
    this(parseChoices(sanitizedChoices));
  }

  /**
   * Check whether the ballot is a valid list of preferences without repeats of candidate names
   * or ranks and without skipped or invalid preferences.
   */
  public boolean IsValid() {
    final String prefix = "[IsValid]";
    LOGGER.debug(String.format("%s interpreting validity for vote %s.", prefix,
        choices.toString()));

    // Test for a perfect integer sequence of length choices.size(), by filling in the i-th element
    // when i is read. preferencesInOrder[i-1] is true if we've encountered preference i - if
    // we encounter it again, or encounter an out-of-bounds preference, return false.
    boolean[] preferencesInOrder = new boolean[choices.size()];
    for (IRVPreference p : choices) {
      // If this preference is out of bounds or already used, the preference list is invalid
      if (p.rank < 1 || p.rank > choices.size() || preferencesInOrder[p.rank - 1]) {
        LOGGER.debug(String.format("%s vote %s is not valid.", prefix, choices));
        return false;
      }
      // Otherwise, it's fine so far. Remember that we've seen this preference.
      preferencesInOrder[p.rank - 1] = true;
    }

    // Now check for repeat candidate names.
    Set<String> candidateNamesSet = new HashSet<>();
    List<String> candidateNames = choices.stream().map(c -> c.candidateName).collect(Collectors.toList());

    // Set::add returns false if the item is already present, in which case this returns false.
    // (Repeated candidate names are invalid.)
    boolean isValid = candidateNames.stream().map(candidateNamesSet::add)
        .reduce(true, Boolean::logicalAnd);
    LOGGER.debug(String.format("%s vote %s is%s valid.", prefix, choices, isValid ? "" : " not"));
    return isValid;
  }

  /**
   * Applies validation rules in the order specified in CO Election Rules [8 CCR 1505-1]
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * to produce a valid IRV vote, and returns that valid IRV vote as an ordered list of candidate
   * names, with the highest-preference candidate first.
   *
   * @return the implied valid IRV preferences, as an ordered list of candidate names with the
   * most-preferred first.
   */
  public List<String> GetValidIntent() {
    final String prefix = "[GetValidIntent]";
    LOGGER.debug(String.format("%s getting valid interpretation of vote %s.", prefix,
        choices.toString()));

    IRVChoices noDuplicates = this.ApplyRule3();
    IRVChoices noOvervotes = noDuplicates.ApplyRule1();
    List<String> valid = noOvervotes.ApplyRule2().choices.stream().map(c -> c.candidateName)
        .collect(Collectors.toList());

    LOGGER.debug(String.format("%s valid interpretation is %s.", prefix, valid));
    return valid;
  }

  /**
   * Applies Rule 26.7.1 from
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * Returns new updated ballot.
   * This is the rule that identifies overvotes (i.e. repeated ranks) and removes all
   * subsequent preferences.
   */
  public IRVChoices ApplyRule1() {

    assert IsSorted(choices) : "Error: Trying to apply Rule 1 to unsorted preferences.";

    List<IRVPreference> mutableChoices = new ArrayList<>(choices);
    for (int i = 0; i < mutableChoices.size() - 1; i++) {
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
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * This removes everything from the skipped preference onwards.
   * Returns new updated ballot.
   */
  public IRVChoices ApplyRule2() {

    assert IsSorted(choices) : "Error: Trying to apply Rule 2 to unsorted preferences.";

    // If the first element is not rank 1, the ballot is effectively blank.
    if (!choices.isEmpty() && choices.get(0).rank != 1) {
      return new IRVChoices(new ArrayList<>());
    }

    List<IRVPreference> mutableChoices = new ArrayList<>(choices);

    for (int i = 0; i < mutableChoices.size() - 1; i++) {
      if ((mutableChoices.get(i + 1)).rank > mutableChoices.get(i).rank + 1) {
        // We found a skipped rank. Skip from the _next_ item.
        mutableChoices.subList(i + 1, mutableChoices.size()).clear();
        break;
      }
    }

    return new IRVChoices(mutableChoices);
  }

  /**
   * Applies Rule 26.7.3 from
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * This removes duplicated preferences for the same candidates.
   * Returns new updated ballot.
   */
  public IRVChoices ApplyRule3() {

    assert IsSorted(choices) : "Error: Trying to apply Rule 3 to unsorted preferences.";

    HashSet<String> candidates = new HashSet<>();
    List<IRVPreference> mutableChoices = new ArrayList<>();

    for (IRVPreference choice : choices) {
      if (!candidates.contains(choice.candidateName)) {
        // This candidate hasn't been mentioned before. Add it to both the vote and the set of mentioned candidates.
        mutableChoices.add(choice);
        candidates.add(choice.candidateName);
      }
    }

    return new IRVChoices(mutableChoices);
  }

  /**
   * Print as a human-readable string. This will include explicit preferences in parentheses.
   * Intended for votes that may not be valid, e.g. "Alice(1),Bob(1),Chuan(3)".
   */
  public String toString() {
    return choices.stream().map(IRVPreference::toString).collect(Collectors.joining(","));
  }

  /**
   * Parse a string (from the database) into a list of IRV preferences. This checks each individual
   * preference for validity (a nonempty string, followed by a positive integer in parentheses) but
   * does _not_ check whether the vote is valid - repeated and skipped preferences are allowed.
   *
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
   *
   * @param choices a list of IRV preferences
   * @return true if the preferences are in ascending order (highest preference / lowest number
   * first)
   */
  private boolean IsSorted(List<IRVPreference> choices) {
    for (int i = 0; i < choices.size() - 1; i++) {
      if ((choices.get(i)).compareTo(choices.get(i + 1)) > 0) {
        return false;
      }
    }
    return true;
  }
}
