package us.freeandfair.corla.util;

import org.apache.commons.lang.StringUtils;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;
import us.freeandfair.corla.model.IRVBallots.Preference;

import java.util.ArrayList;
import java.util.List;

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

