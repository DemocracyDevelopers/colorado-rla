package us.freeandfair.corla.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static us.freeandfair.corla.util.IRVVoteParsing.*;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestType;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;

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
    public void parseValidIRVVoteTest() {
        List<String> inputVote = Arrays.asList("Alice(2)", "Chuan(1)","Bob(3)");
        List<String> expectedOutputVote = Arrays.asList("Chuan","Alice","Bob");

        assertTrue(listsEqual(parseValidIRVVote(inputVote), expectedOutputVote));
    }

    // Check that an effort to convert an invalid vote into a sorted list throws an exception.
    @Test(expectedExceptions = RuntimeException.class)
    void whenGivenInvalidVote_parseValidIRVVote_throwsException() {
        List<String> invalidInputVote = Arrays.asList("Alice(1)", "Chuan(1)","Bob(3)");

        parseValidIRVVote(invalidInputVote);
    }

    @Test
    void parseValidEmptyIRVVoteTest() {
        List<String> invalidInputVote = new ArrayList<>();

        List<String> output = parseValidIRVVote(invalidInputVote);
        assertTrue(output.isEmpty());
    }

    @Test
    void parseIRVVoteTest() {
        List<String> inputVote = Arrays.asList("Alice(2)", "Chuan(1)","Bob(3)");
        IRVChoices irvChoices = parseIRVVote(inputVote);

        assertTrue(irvChoices.toString().equals("Chuan(1),Alice(2),Bob(3)"));
    }

    @Test
    void parseInvalidIRVVoteTest() {
        List<String> inputVote = Arrays.asList("Alice(1)", "Chuan(1)","Bob(3)");
        IRVChoices irvChoices = parseIRVVote(inputVote);

        boolean atLeastOneEqual = irvChoices.toString().equals("Chuan(1),Alice(1),Bob(3)")
                || irvChoices.toString().equals("Alice(1),Chuan(1),Bob(3)");

        assertTrue(atLeastOneEqual);
    }

    @Test
    void parseBlankIRVVoteTest() {
       List<String>  inputVote = new ArrayList<>();
       IRVChoices irvChoices = parseIRVVote(inputVote);

       assertTrue(irvChoices.toString().equals(""));
    }

    @Test
    void tidyIRVBallotChoices() {
        Choice c1 = new Choice("Alice(1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice(2)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Bob(1)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Bob(2)", ContestType.IRV.toString(), false, false);
        List<Choice> choices = new ArrayList<>();
        Collections.addAll(choices, c1, c2, c3, c4);

        removeParenthesesAndRepeatedNamesFromChoices(choices);

        Set<String> names = choices.stream().map(Choice::name).collect(Collectors.toSet());

        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));
        assertEquals(2, choices.size());
    }

    @Test
    void tidyBlankIRVBallotChoices() {
        List<Choice> choices  = new ArrayList<>();
        removeParenthesesAndRepeatedNamesFromChoices(choices);
        assertEquals(0, choices.size());
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
