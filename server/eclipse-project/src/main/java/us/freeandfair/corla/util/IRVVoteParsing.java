package us.freeandfair.corla.util;

import org.apache.commons.lang.StringUtils;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;
import us.freeandfair.corla.model.IRVBallots.Preference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IRVVoteParsing {

    private IRVVoteParsing() {}

    /* Takes a list of candidate 'names' which are expected to be real candidate names with an IRV rank
     * in parentheses, and returns the corresponding IRVChoices.  Note that this does _not_ check for
     * validity - the returned IRVChoices may include duplicates, repeated preferences, etc.
     */
    public static IRVChoices parseIRVVote(final List<String> votes) {
       List<Preference> choices = new ArrayList<>();

       for (String v : votes) {
           choices.add(parseIRVpreference(v));
       }

       return new IRVChoices(choices);
    }

    /* Takes a list of candidate 'names' which are expected to be real candidate names with an IRV rank
     * in parentheses, checks whether they are a valid collection of IRV preferences
     * (e.g. no repeated or skipped preferences).
     * If valid, it returns a list of names only, sorted by preference order (highest preference first).
     * If not valid, it throws an exception.
     */
    public static List<String> parseValidIRVVote(final List<String> votes) {
       IRVChoices choices = parseIRVVote(votes);

       if (!choices.IsValid())   {
           throw new RuntimeException("Error parsing IRV vote: "+votes);
       }

       // choices are valid.
       return choices.AsSortedList();
    }

    /* Takes a list of Choices, which include various data, most particularly a choice_name, and
     * - identifies parenthesized ranks
     * - removes all but the first preference of a particular name
     * - removes the parenthesized rank (1) from the first occurence of each name.
     * So for example four choices "Alice(1), Alice(2), Bob(1), Bob(2)" should become
     * the two choices "Alice, Bob".
     * Throws an exception if it encounters preferences higher than 1 for a candidate it hasn't met the
     * first preference for.
     */
    public static void removeParenthesesAndRepeatedNamesFromChoices(final List<Choice> choices) {
        Set<String> candidatesWeKeep = new HashSet<>();
        for (Choice c : choices ) {
            Preference parsedPreference = parseIRVpreference(c.name());
            if (candidatesWeKeep.add(parsedPreference.getCandidateName())) {
                // If add returns true, this is the first time we've encountered that candidate.
                // The candidate has been added to the set we've kept.
                // Update the name in choices.
                c.setName(parsedPreference.getCandidateName());
            } else {
                // We already added this candidate. Remove this choice.
                choices.remove(c);
            }
        }
    }

    /* Parses a string like "Name(n)" where n is a rank, and returns a Preference object with the correct
     * rank.
     * FIXME needs to ensure that only preferences at the end are dealt with - at the moment Fred (jnr) (6)
     * will not parse.
     */
    private static Preference parseIRVpreference(final String nameAndPref) {
        String[] vote = nameAndPref.split("\\(");
        String rank = vote[1].split("\\)")[0];
        if (!StringUtils.isNumeric(rank)) {
            throw new RuntimeException("Couldn't parse candidate-preference: "+nameAndPref);
        }
        return new Preference(Integer.parseInt(rank), vote[0].trim());
    }
}

