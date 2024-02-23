package au.org.democracydevelopers.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static au.org.democracydevelopers.util.IRVVoteParsing.*;

import au.org.democracydevelopers.util.IRVParsingException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestType;
import au.org.democracydevelopers.model.IRVChoices;
import au.org.democracydevelopers.model.IRVPreference;

import java.util.*;
import java.util.stream.Collectors;

public class IRVVoteParsingTest {
    @BeforeTest()
    public void setUp() {
    }

    @AfterTest()
    public void tearDown() {
    }

    // Check that a valid vote is parsed properly.
    @Test
    public void parseValidIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = Arrays.asList("Alice(2)", "Chuan(1)","Bob(3)");
        List<String> expectedOutputVote = Arrays.asList("Chuan","Alice","Bob");

        assertTrue(listsEqual(parseValidIRVVote(inputVote), expectedOutputVote));
        assertTrue(listsEqual(IRVVoteToValidInterpretationAsSortedList(inputVote), expectedOutputVote));
    }

    // Check that an effort to convert an invalid vote into a sorted list throws an exception.
    @Test(expectedExceptions = IRVParsingException.class)
    void whenGivenInvalidVote_parseValidIRVVote_throwsException() throws IRVParsingException {
        List<String> invalidInputVote = Arrays.asList("Alice(1)", "Chuan(1)","Bob(3)");

        parseValidIRVVote(invalidInputVote);
    }

    @Test
    void whenGivenInvalidVote_IRVVoteToValidInterpretation() throws IRVParsingException {
        List<String> invalidInputVote = Arrays.asList("Alice(1)", "Chuan(1)","Bob(3)");

        assertTrue(IRVVoteToValidInterpretationAsSortedList(invalidInputVote).isEmpty());
    }

    @Test
    void parseValidEmptyIRVVoteTest() throws IRVParsingException {
        List<String> invalidInputVote = new ArrayList<>();

        List<String> output = parseValidIRVVote(invalidInputVote);
        assertTrue(output.isEmpty());

        assertTrue(IRVVoteToValidInterpretationAsSortedList(invalidInputVote).isEmpty());
    }

    @Test
    void parseIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = Arrays.asList("Alice(2)", "Chuan(1)","Bob(3)");
        IRVChoices irvChoices = parseIRVVote(inputVote);

        assertTrue(irvChoices.toString().equals("Chuan(1),Alice(2),Bob(3)"));

        List<String> expectedOutput = List.of("Chuan", "Alice", "Bob");
        assertTrue(listsEqual(IRVVoteToValidInterpretationAsSortedList(inputVote), expectedOutput));
    }

    @Test
    void parseInvalidIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = Arrays.asList("Alice(1)", "Chuan(1)","Bob(3)");
        IRVChoices irvChoices = parseIRVVote(inputVote);

        boolean atLeastOneEqual = irvChoices.toString().equals("Chuan(1),Alice(1),Bob(3)")
                || irvChoices.toString().equals("Alice(1),Chuan(1),Bob(3)");

        assertTrue(atLeastOneEqual);

        assertTrue(IRVVoteToValidInterpretationAsSortedList(inputVote).isEmpty());
    }

    @Test
    void parseExampleIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = List.of("Candidate 5(1)", "Candidate 6(2)", "Candidate 7(3)", "Candidate 8(4)",
                "Candidate 9(5)", "Candidate 10(6)", "Candidate 11(7)", "Candidate 12(8)", "Candidate 2(9)","Candidate 1(10) ");
        IRVChoices irvChoices = parseIRVVote(inputVote);
    }

    @Test
    void parseAndInterpretExampleIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = List.of("Candidate 5(1)", "Candidate 6(2)", "Candidate 7(3)", "Candidate 8(4)", "Candidate 9(5)", "Candidate 10(6)", "Candidate 11(7)", "Candidate 12(8)", "Candidate 2(9)","Candidate 1(10) ");
        List<String> expectedOutput = List.of("Candidate 5", "Candidate 6", "Candidate 7", "Candidate 8", "Candidate 9", "Candidate 10", "Candidate 11", "Candidate 12", "Candidate 2","Candidate 1");

        assertTrue(listsEqual(IRVVoteToValidInterpretationAsSortedList(inputVote), expectedOutput));
    }

    @Test
    void parseExampleIRVVoteTestCandidate10() throws IRVParsingException {
        List<String> inputVote = List.of("Candidate 1(10) ");

        assertTrue(IRVVoteToValidInterpretationAsSortedList(inputVote).isEmpty());
    }

    @Test
    void parseAndInterpretExampleIRVVoteTestCandidate10() throws IRVParsingException {
        List<String> inputVote = List.of("Candidate 5(1)", "Candidate 6(2)", "Candidate 7(3)", "Candidate 8(4)", "Candidate 9(5)", "Candidate 10(6)", "Candidate 11(7)", "Candidate 12(8)", "Candidate 2(9)","Candidate 1(10) ");
        List<String> expectedOutput = List.of("Candidate 5", "Candidate 6", "Candidate 7", "Candidate 8", "Candidate 9", "Candidate 10", "Candidate 11", "Candidate 12", "Candidate 2","Candidate 1");

        assertTrue(listsEqual(IRVVoteToValidInterpretationAsSortedList(inputVote), expectedOutput));
    }

    // This example is missing a 9th preference.
    @Test
    void parseBadExampleIRVVoteTest() throws IRVParsingException {
        List<String> inputVote = List.of("Candidate 5(1)", "Candidate 6(2)", "Candidate 7(3)", "Candidate 8(4)", "Candidate 9(5)", "Candidate 10(6)", "Candidate 11(7)", "Candidate 12(8)", "Candidate 1(10) ");
        IRVChoices irvChoices = parseIRVVote(inputVote);
    }

    @Test
    void parseBlankIRVVoteTest() throws IRVParsingException {
       List<String>  inputVote = new ArrayList<>();
       IRVChoices irvChoices = parseIRVVote(inputVote);

       assertTrue(irvChoices.toString().equals(""));
    }

    @Test
    void IRVHeadersParenthesisRemoval() throws IRVParsingException {
        Choice c1 = new Choice("Alice(1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice(2)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Bob(1)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Bob(2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4);

        List<Choice> updatedChoices = parseIRVHeadersExtractChoiceNames(choices);

        assertTrue(updatedChoices.stream().map(Choice::name).collect(Collectors.toList()).contains("Alice"));
        assertTrue(updatedChoices.stream().map(Choice::name).collect(Collectors.toList()).contains("Bob"));
        assertEquals(2, updatedChoices.size());
    }

    @Test
    void tidyReturnBlankIRVBallotChoices() throws IRVParsingException {
        List<Choice> choices  = new ArrayList<>();
        List<Choice> updatedChoices = parseIRVHeadersExtractChoiceNames(choices);
        assertEquals(0, updatedChoices.size());
    }

    @Test (expectedExceptions = IRVParsingException.class)
    void extractPlainChoicesThrowsExceptionWithInvalidChoices() throws IRVParsingException {
        Choice c1 = new Choice("Alice (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("InvalidChoice", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Alice (Not Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry (8) (1)", ContestType.IRV.toString(), false, false);
        Choice c5 = new Choice("Henry (8) (2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4,c5);

        List<Choice> updatedChoices = parseIRVHeadersExtractChoiceNames(choices);
    }

    @Test
    void trickyIRVBallotChoicesToPlainChoices() throws IRVParsingException {
        Choice c1 = new Choice("Alice (Zahra) (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (Zahra)(3)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Alice (Not Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry (8) (1)", ContestType.IRV.toString(), false, false);
        Choice c5 = new Choice("Henry (8) (2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4,c5);

        List<Choice> updatedChoices = parseIRVHeadersExtractChoiceNames(choices);

        List<Choice> aliceNotZahrasPref = updatedChoices.stream().filter(c -> c.name().equals("Alice (Not Zahra)")).collect(Collectors.toList());
        Set<String> names = updatedChoices.stream().map(Choice::name).collect(Collectors.toSet());

        assertEquals(1, aliceNotZahrasPref.size());
        assertTrue(names.contains("Alice (Zahra)"));
        assertTrue(names.contains("Alice (Not Zahra)"));
        assertTrue(names.contains("Henry (8)"));
        assertEquals(3, updatedChoices.size());
    }

    @Test
    void tidyIRVBallotChoices() throws IRVParsingException {
        Choice c1 = new Choice("Alice(1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice(2)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Bob(1)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Bob(2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4);

        List<Choice> updatedChoices = removeParenthesesAndRepeatedNames(choices);

        Set<String> names = updatedChoices.stream().map(Choice::name).collect(Collectors.toSet());

        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));
        assertEquals(2, updatedChoices.size());
    }

    @Test
    void tidyBlankIRVBallotChoices() throws IRVParsingException {
        List<Choice> choices  = new ArrayList<>();
        List<Choice> updatedChoices = removeParenthesesAndRepeatedNames(choices);
        assertEquals(0, updatedChoices.size());
    }

    @Test
    void trickyIRVBallotChoices() throws IRVParsingException {
        Choice c1 = new Choice("Alice (Zahra) (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (Zahra)(3)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Alice (Not Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry (8) (1)", ContestType.IRV.toString(), false, false);
        Choice c5 = new Choice("Henry (8) (2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4,c5);

        List<Choice> updatedChoices = removeParenthesesAndRepeatedNames(choices);

        List<Choice> aliceNotZahrasPref = updatedChoices.stream().filter(c -> c.name().equals("Alice (Not Zahra)")).collect(Collectors.toList());
        Set<String> names = updatedChoices.stream().map(Choice::name).collect(Collectors.toSet());

        assertEquals(1, aliceNotZahrasPref.size());
        assertTrue(names.contains("Alice (Zahra)"));
        assertTrue(names.contains("Alice (Not Zahra)"));
        assertTrue(names.contains("Henry (8)"));
        assertEquals(3, updatedChoices.size());
    }

    @Test
    void removeFirstPreferencesTest() throws IRVParsingException {
        Choice c1 = new Choice("Alice (Zahra) (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (Zahra)(3)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Alice (Not Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry (8) (1)", ContestType.IRV.toString(), false, false);
        Choice c5 = new Choice("Henry (8) (2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4,c5);

        List<Choice> correctedChoices = removeFirstPreferenceParenthesesFromChoiceNames(choices);

        assertEquals(2, correctedChoices.size());
        assertEquals("Alice (Zahra)", c1.name());
        assertEquals("Alice (Zahra)(3)" , c2.name());
        assertEquals("Alice (Not Zahra) (2)", c3.name());
        assertEquals("Henry (8)",c4.name());
        assertEquals("Henry (8) (2)",c5.name());

    }

    @Test
    public void testCheckSortIRVPreferencesTest() throws IRVParsingException {

        Choice c1 = new Choice("Alice (Zahra) (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (Not Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Henry (8) (3)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry jnr (4)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4);

        checkSortIRVPreferences(choices);
        assertTrue(true);
    }

    @Test(expectedExceptions = IRVParsingException.class)
    public void testCheckUnsortedThrowsException() throws IRVParsingException {

        Choice c1 = new Choice("Alice (Zahra) (2)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (Not Zahra) (1)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Henry (8) (3)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Henry jnr (4)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4);

        checkSortIRVPreferences(choices);

    }

    @Test
    public void parseTwoDigitPreferences() throws IRVParsingException {
        String choice = "Alice(10)";

        IRVPreference p = parseIRVPreference(choice);

        assertEquals(10,p.getRank());
        assertEquals("Alice", p.getCandidateName());
    }
    @Test
    public void parseTwoDigitPreferences2() throws IRVParsingException {
        String choice = "Candidate 1(10)";

        IRVPreference p = parseIRVPreference(choice);

        assertEquals(10,p.getRank());
        assertEquals("Candidate 1", p.getCandidateName());
    }

    @Test
    public void parseTwoDigitPreferences2WithSpace() throws IRVParsingException {
        String choice = "Candidate 1(10) ";

        IRVPreference p = parseIRVPreference(choice);

        assertEquals(10,p.getRank());
        assertEquals("Candidate 1", p.getCandidateName());
    }

    @Test
    public void parseTwoDigitPreferences2WithOtherSpace() throws IRVParsingException {
        String choice = "Candidate 1  (10) ";

        IRVPreference p = parseIRVPreference(choice);

        assertEquals(10,p.getRank());
        assertEquals("Candidate 1", p.getCandidateName());
    }

    @Test
    private boolean listsEqual(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for(int i = 0 ; i < list1.size() ; i++ ) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }
}
