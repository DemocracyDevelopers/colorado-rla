package us.freeandfair.corla.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/* A ballot is a set of (rank, candidate name) pairs. This data structure allows them to be
 * invalid, that is, with repeated candidate names or repeated preferences.
 */
public class IRVChoices {

    // Always sorted by preference, from highest preference (i.e. lowest number) to lowest preference (i.e. highest number)
    // Repeated or skipped preferences are allowed.
    private List<IRVPreference> choices;

    public int getLength() {
        return choices.size();
    }

    public IRVChoices(List<IRVPreference> mutableChoices) {
        mutableChoices.sort(IRVPreference::compareTo);
        choices = Collections.unmodifiableList(mutableChoices);
    }


    // Build a new ballot from the string of CandidateName(rank) strings in the colorado-rla database.
    // FIXME probably almost everywhere that calls this now, will instead want to be called on an ordered list
    // of choices.
    public IRVChoices(String sanitizedChoices) {
        ArrayList<IRVPreference> mutableChoices = new ArrayList<>();

        String[] preferences = sanitizedChoices.trim().split(",");
        if (preferences.length == 0) {
            return;
        }
        // FIXME This needs to deal with candidate names that have parentheses.
        for (String preference : preferences) {
            String[] vote = preference.split("\\(");
            String rank = vote[1].split("\\)")[0];
            if (!StringUtils.isNumeric(rank)) {
                throw new RuntimeException("Couldn't parse ballot");
                //Invalid rank. Ballot is not usable. IF ballot is invalid, this safeguards us against number format exception
            }
            mutableChoices.add(new IRVPreference(Integer.parseInt(rank), vote[0].trim()));
        }

        mutableChoices.sort(IRVPreference::compareTo);
        choices = Collections.unmodifiableList(mutableChoices);
    }

    /* Check whether the ballot is a valid list of preferences without repeats of candidate names or ranks and
     * without skipped or invalid preferences.
     */
    public boolean IsValid() {

        Set<String> candidateNamesSet = new HashSet<>();
        // add to set returns false if the item is already present.
        boolean allNamesDistinct = choices.stream().map(c -> candidateNamesSet.add(c.getCandidateName())).reduce(true, (b1,b2) -> b1 && b2);
        if (!allNamesDistinct) {
            return false;
        }

        // Test for a perfect integer sequence of length choices.size(), by filling in the i-th element when i is read.
        boolean[] preferencesInOrder = new boolean[choices.size()];
        for(IRVPreference p : choices) {
            // If this preference is out of bounds or already used, the pref list is invalid
            if (p.getRank() < 1 || p.getRank() > choices.size() || preferencesInOrder[p.getRank()-1]) {
                return false;
            }
            preferencesInOrder[p.getRank()-1] = true;
        }

        return true;
    }

    /* Applies Rule 26.7.1 from
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


    /* Applies Rule 26.7.2 from
     * https://www.sos.state.co.us/pubs/rule_making/CurrentRules/8CCR1505-1/Rule26.pdf
     * This removes everything from the skipped preference onwards.
     * Returns new updated ballot.
     */
    public IRVChoices ApplyRule2() {

        assert IsSorted(choices) : "Error: Trying to apply Rule 2 to unsorted preferences.";


        // If the first element is not rank 1, the ballot is effectively blank.
        if(!choices.isEmpty() && choices.get(0).getRank() != 1) {
            return new IRVChoices(new ArrayList<IRVPreference>());
        }

        List<IRVPreference> mutableChoices = new ArrayList<IRVPreference>(choices);

        for (int i=0 ; i < mutableChoices.size()-1 ; i++) {
            if ((mutableChoices.get(i+1)).getRank() > mutableChoices.get(i).getRank() + 1) {
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
            if (!candidates.contains(choice.getCandidateName())) {
                // This candidate hasn't been mentioned before. Add it to both the vote and the set of mentioned candidates.
                mutableChoices.add(choice);
                candidates.add(choice.getCandidateName());
            }
        }

        return new IRVChoices(mutableChoices);
    }

    /* Returns this IRV vote as a sorted list of candidate names, starting with the first preference
     * and ending with the lowest.
     * Throws an exception if called on an invalid vote.
     */
    public List<String> AsSortedList() {
        // Neither of these calls should ever be made.
        if ( !IsValid() ) {
            throw new RuntimeException("Attempt to call AsSortedList on invalid vote: "+choices);
        }
        if (! IsSorted(choices)) {
            throw new RuntimeException("Attempt to call AsSortedList on unsorted choices.");
        }

        return choices.stream().map(IRVPreference::getCandidateName).collect(Collectors.toUnmodifiableList());
    }

    /* This will include explicit preferences in parentheses. Intended for votes that may not be valid,
     * e.g. "Alice(1),Bob(1),Chuan(3)".
     */
    public String toString() {
        return choices.stream().map(IRVPreference::toString).collect(Collectors.joining(","));
    }

    private boolean IsSorted(List<IRVPreference> choices) {
        for (int i=0 ; i < choices.size()-1 ; i++) {
            if ((choices.get(i)).compareTo(choices.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

}