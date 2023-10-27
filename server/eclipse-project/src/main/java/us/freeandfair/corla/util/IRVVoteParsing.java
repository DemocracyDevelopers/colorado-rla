package us.freeandfair.corla.util;

import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.IRVChoices;
import us.freeandfair.corla.model.IRVPreference;

import java.util.*;
import java.util.stream.Collectors;

public class IRVVoteParsing {

    private IRVVoteParsing() {}

    /* Takes a list of candidate 'names' which are expected to be real candidate names with an IRV rank
     * in parentheses, and returns the corresponding IRVChoices.  Note that this does _not_ check for
     * validity - the returned IRVChoices may include duplicates, repeated preferences, etc.
     */
    public static IRVChoices parseIRVVote(final List<String> votes) throws IRVParsingException {
       List<IRVPreference> choices = new ArrayList<>();

       for (String v : votes) {
           choices.add(parseIRVPreference(v));
       }

       return new IRVChoices(choices);
    }

    /* Takes a list of candidate 'names' which are expected to be real candidate names with an IRV rank
     * in parentheses, checks whether they are a valid collection of IRV preferences
     * (e.g. no repeated or skipped preferences).
     * If valid, it returns a list of names only, sorted by preference order (highest preference first).
     * If not valid, it throws an exception.
     */
    public static List<String> parseValidIRVVote(final List<String> votes) throws IRVParsingException {
       IRVChoices choices = parseIRVVote(votes);

       if (!choices.IsValid())   {
           throw new IRVParsingException("Error parsing IRV vote: "+votes);
       }

       // choices are valid.
       return choices.AsSortedList();
    }

    /* Takes a list of Choices, which include various data, most particularly a choice_name, and
     * - identifies parenthesized ranks
     * - removes all but the first preference of a particular name
     * - removes the parenthesized rank (1) from the first occurence of each name.
     * - returns a list of Choices, in which each real candidate is represented once, without parentheses
     * So for example four choices "Alice(1), Alice(2), Bob(1), Bob(2)" should become
     * the two choices "Alice, Bob" with the data matching whatever was in "Alice (1)", "Bob (1)" respectively.
     *
     * This is intended for choices (i.e. Candidate names), not for votes, because it ignores
     * rank information.
     */
    public static List<Choice> parseIRVHeadersExtractChoiceNames(final List<Choice> choices) throws IRVParsingException {
        Set<Choice> distinctChoices = new HashSet<>();

        // Add each choice into the set of distinct choices after removing parentheses after name.
        for (Choice c : choices)  {
            IRVPreference parsedPreference = parseIRVPreference(c.name());
            Choice newChoice = new Choice(parsedPreference.getCandidateName(), c.description(), c.qualifiedWriteIn(), c.fictitious());
            distinctChoices.add(newChoice);
        }

        List<Choice> distinctChoicesList = new ArrayList<>(distinctChoices);
        return distinctChoicesList;
    }

    /**
     * Scans through a list of Choices. For each first preference, removes the (1), leaving only the
     * candidate's name.
     * Returns the list of new (preference-less) Choices.
     */
    public static List<Choice> removeFirstPreferenceParenthesesFromChoiceNames(final List<Choice> choices) throws IRVParsingException {
        List<Choice> choicesWithPlainNames = new ArrayList<>();

        // Add each choice into the set of distinct choices after removing parentheses after name.
        for (Choice c : choices)  {
            IRVPreference parsedPreference = parseIRVPreference(c.name());
            if (parsedPreference.getRank() == 1) {
                c.setName(parsedPreference.getCandidateName());
                choicesWithPlainNames.add(c);
            }
        }

        return choicesWithPlainNames;
    }

    public static List<Choice> removeParenthesesAndRepeatedNames(final List<Choice> choices) throws IRVParsingException {
        Set<Choice> distinctChoices = new HashSet<>();

        // Add each choice into the set of distinct choices after removing parentheses after name.
        for (Choice c : choices)  {
            IRVPreference parsedPreference = parseIRVPreference(c.name());
            c.setName(parsedPreference.getCandidateName());
            distinctChoices.add(c);
        }

        return new ArrayList<Choice>(distinctChoices);
    }

    /* Checks that the list of choices is sorted according to preference. Throws an exception if the list is
     * not a valid preference list.
     * TODO It wouldn't be too hard to sort them, but I think that case never arises anyway.
     */
    public static void checkSortIRVPreferences(final List<Choice> choices) throws IRVParsingException {
        if (choices.isEmpty()) {
            return;
        }

        IRVPreference p_current = parseIRVPreference(choices.get(0).name());
        for (int i =0 ; i < choices.size()-2 ; i++) {
            IRVPreference p_next = parseIRVPreference(choices.get(i+1).name());
            if (p_current.getRank() >= p_next.getRank()) {
                throw new IRVParsingException("Error parsing IRV CSV: "+choices);
            }
            p_current = p_next;
        }
    }

    /** This assumes the oldChoices include explicit parenthesized ranks, while newChoices
     *  contains the same names but without the parenthesized ranks. It first checks
     *  that they are properly sorted, then 'removes' the explicit parentheses by replacing
     *  everything of that form with its corresponding plain name.
     *  Throws an exception if either the ranks aren't sorted, or there is a name in the
     *  old choices that doesn't have a corresponding name in the new choices.
     */
    public static void updateIRVPreferencesWithNewChoices(List<Choice> oldChoices, List<Choice> newChoices) throws IRVParsingException {
       checkSortIRVPreferences(oldChoices);

       for (int i =0 ; i < oldChoices.size() ; i++)  {
           IRVPreference pref = parseIRVPreference(oldChoices.get(i).name());
           List<Choice> newChoice = newChoices.stream().filter(c -> c.name().equals(pref.getCandidateName())).collect(Collectors.toList());
           if (newChoice.size() != 1) {
               throw new IRVParsingException("Error parsing CSV"+oldChoices);
           }
           oldChoices.set(i, newChoice.get(0));
       }
    }

    /** Tests whether a string is an IRV write-in, i.e. "Write-in (n)" for some n.
     */
    public static boolean IsIRVWriteIn (String choice) {

        try {
            IRVPreference pref = parseIRVPreference(choice);
            return pref.getCandidateName().equalsIgnoreCase("Write-in");
        } catch (IRVParsingException e) {
            // It's OK to have an IRVParsing exception here - it just means that it's not an IRV choice,
            // and hence definitely not an IRV write-in.
            return true;
        }
    }

    /* Parses a string like "Name(n)" where n is a rank, and returns a Preference object with the correct
     * rank.
     * Throws an exception if the input doesn't fit into the name(digits) pattern.
     */
    public static IRVPreference parseIRVPreference(final String nameAndPref) throws IRVParsingException {

        // Look for digits in parentheses at the end of the string.
        String regexp ="\\((\\d+\\))$";

        try {
            String vote = nameAndPref.split(regexp)[0];
            String rank1 = nameAndPref.replace(vote, "");
                    String rank2 = rank1.trim().split("[\\(\\)]")[1];

            return new IRVPreference(Integer.parseInt(rank2), vote.trim());

        } catch (NumberFormatException | IndexOutOfBoundsException e2) {
            throw new IRVParsingException("Couldn't parse candidate-preference: " + nameAndPref);
        }
    }


}

