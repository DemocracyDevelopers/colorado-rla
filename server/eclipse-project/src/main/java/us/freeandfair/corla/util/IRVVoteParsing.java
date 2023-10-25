package us.freeandfair.corla.util;

import org.apache.commons.lang.StringUtils;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestType;
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
     *
     * This is intended for choices (i.e. Candidate names), not for votes, because it ignores
     * rank information.
     */
    public static List<Choice> removeParenthesesAndRepeatedNames(final List<Choice> choices) {
        Set<Choice> distinctChoices = new HashSet<>();

        // Add each choice into the set of distinct choices after removing parentheses after name.
        for (Choice c : choices)  {
            Preference parsedPreference = parseIRVpreference(c.name());
            c.setName(parsedPreference.getCandidateName());
            distinctChoices.add(c);
        }

        return new ArrayList<Choice>(distinctChoices);
    }

    /* Parses a string like "Name(n)" where n is a rank, and returns a Preference object with the correct
     * rank.
     * Throws an exception if the input doesn't fit into the name(digits) pattern.
     */
    private static Preference parseIRVpreference(final String nameAndPref) {

        // Look for digits in parentheses at the end of the string.
        String regexp ="\\((\\d+\\))$";

        try {
            String vote = nameAndPref.split(regexp)[0];
            String rank1 = nameAndPref.replace(vote, "");
                    String rank2 = rank1.trim().split("[\\(\\)]")[1];

            return new Preference(Integer.parseInt(rank2), vote.trim());

        } catch (NumberFormatException | IndexOutOfBoundsException e2) {
            throw new RuntimeException("Couldn't parse candidate-preference: " + nameAndPref);
        }
    }
}

