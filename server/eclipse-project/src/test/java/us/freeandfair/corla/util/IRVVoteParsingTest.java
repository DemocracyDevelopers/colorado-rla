package us.freeandfair.corla.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static us.freeandfair.corla.util.IRVVoteParsing.parseIRVVote;
import static us.freeandfair.corla.util.IRVVoteParsing.parseValidIRVVote;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
