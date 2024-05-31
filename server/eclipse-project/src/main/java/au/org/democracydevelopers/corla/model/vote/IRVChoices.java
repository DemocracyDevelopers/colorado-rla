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
 * This class represents one voter's choices in one IRV contest, that is, one IRV vote.
 * Each IRV choice has both a candidate name and a rank (preference) - this is represented as
 * an IRVPreference object. So a vote (a single IRVChoices object) is a list of such IRVPreferences.
 * This data structure allows votes to be invalid, that is, with repeated candidate names or
 * skipped or repeated preferences.
 * The constructor will parse a vote that comes as a comma-separated string of the form
 * "name1(p1),name2(p2), ..." where p1, p2... are (positive integer) ranks. It stores them sorted
 * by rank, with skips or duplicates allowed.
 * An IRVChoices object may represent an invalid vote, but it implies a unique valid interpretation
 * as specified in Colorado's IRV rules
 * (<a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>)
 * which describe how to deal with repeated candidates, or skipped or repeated preferences.
 * The main algorithms in this class implement valid vote interpretation for Colorado's IRV rules.
 * The order of application of the rules follows Colorado's Guide to Voter Intent -
 * Determination of Voter Intent for Colorado Elections, 2023 addendum for Instant Runoff Voting
 * (IRV), available at
 * <a href="https://assets.bouldercounty.gov/wp-content/uploads/2023/11/Voter-Intent-Guide-IRV-Addendum-2023.pdf">...</a>
 * TODO This may be updated soon - updated ref to CDOS rather than Boulder when official CDOS
 * version becomes available.
 * Main methods:
 * - isValid(): checks whether the choices are a valid IRV vote (without skipped or repeated
 *   preferences, or repeated candidates).
 * - rule_26_7_1_Overvotes(): applies Colorado's IRV rule 26.7.1 - removing overvotes (i.e.
 *   repeated ranks) and all subsequent choices.
 * - rule_26_7_2_Skips(): applies Colorado's IRV rule 26.7.2 - removing all choices after a
 *   skipped rank.
 * - rule_26_7_3_Duplicates(): applies Colorado's IRV rule 26.7.3 - removing all but the highest
 *   rank for a given candidate, if that candidate is mentioned with duplicate ranks.
 * - getValidIntentAsOrderedList(): applies Colorado's IRV rules and returns the valid
 *   interpretation, as an ordered list of candidate names with the highest preference first. In
 *   particular, it applies Rule_26_7_3_Duplicates() before Rule_26_7_1_Overvotes() as required.
 */
public class IRVChoices {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(IRVChoices.class);

  /**
   * Choices are always sorted by preference, from highest preference (i.e. lowest number) to lowest
   * preference (i.e. highest number). Repeated candidates and repeated or skipped preferences are
   * allowed. Choices are final and immutable.
   */
  private final List<IRVPreference> choices;

  /**
   * The number of IRV choices, as given to the constructor, before any duplicates, skipped
   * preferences or overvotes are removed.
   *
   * @return the number of choices in the raw IRV vote.
   */
  public int getRawChoicesCount() {
    return choices.size();
  }

  /**
   * Constructor - takes a list of IRVPreferences (ranked candidates) representing a vote, sorts
   * this list by rank (most to least preferred), then stores in an unmodifiable list.
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
   * of the form "name1(p1),name2(p2), ..."
   * Parses each individual preference into an IRVPreference to make a list of IRVPreferences, and
   * then calls the other constructor, which sorts the list by rank (most to least preferred), then
   * stores in an unmodifiable list.
   *
   * @param sanitizedChoices the IRV preferences as a string, which need not be a valid IRV vote -
   *                         repeats or skipped preferences are allowed.
   * @throws IRVParsingException if one of the comma-separated substrings cannot be parsed as a
   *                             name(rank). This could happen for example if called on a
   *                             plurality vote.
   */
  public IRVChoices(String sanitizedChoices) throws IRVParsingException {
    this(parseChoices(sanitizedChoices));
  }

  /**
   * Check whether the vote is a valid list of preferences without repeats of candidate names
   * or ranks and without skipped or invalid preferences.
   * This first checks that the preference list contains no repeats and no items outside the range
   * of [1,...,choices.size]. The preference list must therefore be a valid permutation of all the
   * numbers from 1 to choices.size.
   * The second part checks that no candidate names have been repeated. (Note that skipping
   * candidates is fine.) This function does _not_ check that the candidate names correspond to the
   * available choices for the contest - that will be caught by a different validity check on
   * upload.
   */
  public boolean isValid() {
    final String prefix = "[IsValid]";
    LOGGER.debug(String.format("%s interpreting validity for vote %s.", prefix, choices.toString()));

    // This method should be applied only to sorted choices.
    if (choicesAreUnsorted()) {
      final String msg = String.format("%s IsValid called on unsorted choices: %s.", prefix, choices);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    // Test for a perfect sorted integer sequence of length choices.size(), by checking whether
    // each element matches the expected value.
    Set<String> candidateNamesSet = new HashSet<>();
    for (int i=0 ; i < choices.size() ; i++) {
      if (choices.get(i).rank != i + 1 || !candidateNamesSet.add(choices.get(i).candidateName))  {
        LOGGER.debug(String.format("%s vote %s is not valid.", prefix, choices));
        return false;
      }
    }

    LOGGER.debug(String.format("%s vote %s is valid.", prefix, choices));
    return true;
  }

  /**
   * Applies validation rules in the order specified in CO Election Rules [8 CCR 1505-1]
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * to produce a valid IRV vote, and returns that valid IRV vote as an ordered list of candidate
   * names, with the highest-preference candidate first.
   * The order of application of the rules follows Colorado's Guide to Voter Intent -
   * Determination of Voter Intent for Colorado Elections, 2023 addendum for Instant Runoff Voting
   * (IRV), available at
   * <a href="https://assets.bouldercounty.gov/wp-content/uploads/2023/11/Voter-Intent-Guide-IRV-Addendum-2023.pdf">...</a>
   * TODO This may be updated soon - updated ref to CDOS rather than Boulder when official CDOS
   * version becomes available.
   * This specifies that candidate duplicates (Rule 26.7.3) should be removed before overvotes
   * (Rule 26.7.1). The order of skipped preferences (Rule 26.7.2) does not matter.
   * If the IRV vote is already valid, it will be returned as an ordered list of candidate names
   * in preference order (highest preference first).
   *
   * @return the implied valid IRV preferences, as an ordered list of candidate names with the
   * most-preferred first.
   */
  public List<String> getValidIntentAsOrderedList() {
    final String prefix = "[GetValidIntentAsOrderedList]";
    LOGGER.debug(String.format("%s getting valid interpretation of vote %s.", prefix,
        choices.toString()));

    List<String> valid = this.rule_26_7_3_Duplicates().rule_26_7_1_Overvotes().rule_26_7_2_Skips()
        .choices.stream().map(c -> c.candidateName).collect(Collectors.toList());
    LOGGER.debug(String.format("%s valid interpretation is %s.", prefix, valid));
    return valid;
  }

  /**
   * Return this vote as a human-readable string. This will include explicit preferences in
   * parentheses. The method can be used irrespective of whether the vote is valid or invalid.
   */
  public String toString() {
    return choices.stream().map(IRVPreference::toString).collect(Collectors.joining(","));
  }

  /**
   * Applies Rule 26.7.1 from
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * This is the rule that identifies overvotes (i.e. repeated ranks) and removes all
   * subsequent preferences.
   * For example, "Alice(1),"Bob(2)","Chuan(2)" will convert to "Alice(1)".
   *
   * @return new updated vote, with overvotes and all subsequent choices removed.
   */
  private IRVChoices rule_26_7_1_Overvotes() {
    final String prefix = "[Rule_26_7_1_Overvotes]";

    // These rules should be applied only to sorted choices.
    if (choicesAreUnsorted()) {
      final String msg = String.format("%s Rule 26.7.1 (overvote removal) called on unsorted " +
          "choices: %s.", prefix, choices);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    // Iterate through the list - at any time, if the current rank is equal to the next rank, break
    // and drop those choices and all subsequent ones.
    // We use the explicit control over array index to deal appropriately with the comparison
    // between adjacent items and the need to remove both if they have equal rank.
    List<IRVPreference> nonRepeatedChoices = new ArrayList<>(choices);
    for (int i = 0; i < nonRepeatedChoices.size() - 1; i++) {
      if ((nonRepeatedChoices.get(i)).compareTo(nonRepeatedChoices.get(i + 1)) == 0) {

        // We found an overvote. Skip from this item.
        nonRepeatedChoices.subList(i, nonRepeatedChoices.size()).clear();
        break;
      }
    }

    return new IRVChoices(nonRepeatedChoices);
  }

  /**
   * Applies Rule 26.7.2 from
   * <a href="https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf">...</a>
   * This removes everything from the skipped preference onwards.
   * Note: I was tempted to optimize this to check whether choices[i] > i+1 for any i. Although that
   * would certainly flag any invalid vote, it would not immediately flag all votes that had skipped
   * preferences. For example, Alice(1),Bob(2),Chuan(2),Diego(4) skips a preference (3) but does
   * not at any point have an index for which the preference exceeds the position in the list. I
   * believe that under CO's current rules it wouldn't matter - the distinction is relevant only
   * when a repeated preference precedes the skipped one, and CO treats both of those cases the
   * same anyway. However, I have left it as it is, looking for actual skipped preference numbers
   * regardless of array position, in case anyone chooses to adapt this code to other IRV rules.
   * In particular, this implementation will remove Diego(4) in the example above, but will _not_
   * remove Bob(2),Chuan(2).
   *
   * @return new updated vote, with every choice following a skipped rank removed.
   */
  private IRVChoices rule_26_7_2_Skips() {
    final String prefix = "[Rule_26_7_2_Skips]";

    // These rules should be applied only to sorted choices.
    if (choicesAreUnsorted()) {
      final String msg = String.format("%s Rule 26.7.2 (skipped rank removal) called on unsorted " +
          "choices: %s.", prefix, choices);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    // If the choices are blank, return blank. If the first element is not rank 1, the vote is
    // effectively blank.
    if (choices.isEmpty() || choices.get(0).rank != 1) {
      return new IRVChoices(new ArrayList<>());
    }

    List<IRVPreference> mutableChoices = new ArrayList<>(choices);

    // Iterate through the list - at any time, if we find a rank that is more than one greater than
    // the rank beforehand, break and drop all subsequent choices.
    // We use the explicit control over array index to deal appropriately with the comparison
    // between adjacent items and the need to remove the second if there is a skipped rank.
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
   * This removes repeat mentions of the same candidate, leaving only the most-preferred rank
   * (i.e. the lowest number) for each candidate.
   * For example, "Alice(1),Alice(2),Bob(2)" will convert to "Alice(1),Bob(2)".
   * @return new updated vote, with all but the highest-rank mention of each candidate removed.
   */
  private IRVChoices rule_26_7_3_Duplicates() {
    final String prefix = "[Rule_26_7_3_Duplicates]";

    // These rules should be applied only to sorted choices.
    if (choicesAreUnsorted()) {
      final String msg = String.format("%s Rule 26.7.3 (duplicate candidate removal) called on " +
          "unsorted choices: %s.", prefix, choices);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    HashSet<String> candidates = new HashSet<>();
    List<IRVPreference> mutableChoices = new ArrayList<>();

    // Iterate through the choices in increasing order of preference, maintaining a set of
    // candidates who have been encountered already. A candidate encountered for the first time is
    // added to the encountered-candidates set, and also that mention of them is added to the vote.
    // Subsequent mentions of an already-encountered candidate are ignored.
    for (IRVPreference choice : choices) {
      if (!candidates.contains(choice.candidateName)) {
        // This candidate hasn't been mentioned before. Add it to both the vote and the set of
        // mentioned candidates.
        mutableChoices.add(choice);
        candidates.add(choice.candidateName);
      }
    }

    return new IRVChoices(mutableChoices);
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
   * Check whether the preferences are sorted in non-descending order. It's debatable whether this
   * function is needed, because the choices are immutable and are sorted in the constructor. It is
   * here only to protect against subsequent development errors, since the application of ballot
   * validity rules is critically dependent on the assumption that the choices are sorted.
   * @return true if the preferences are in non-descending order (highest preference / lowest number
   * first)
   */
  private boolean choicesAreUnsorted() {
    for (int i = 0; i < choices.size() - 1; i++) {
      if ((choices.get(i)).compareTo(choices.get(i + 1)) > 0) {
        return true;
      }
    }
    return false;
  }
}
