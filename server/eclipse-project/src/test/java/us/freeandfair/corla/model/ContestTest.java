package us.freeandfair.corla.model;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.junit.Assert.*;

import us.freeandfair.corla.model.IRVBallots.BallotInterpretationDuplicatesBeforeOvervotes;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;

import java.util.List;

// Testing IRV-specific additions to the contest class.
public class ContestTest {
    private ContestTest() {}

    private Contest my_contest;
    @BeforeTest()
    public void setUp() {
        Choice c1 = new Choice("Alice (1)", ContestType.IRV.toString(), false, false);
        Choice c2 = new Choice("Alice (2)", ContestType.IRV.toString(), false, false);
        Choice c3 = new Choice("Bob (1)", ContestType.IRV.toString(), false, false);
        Choice c4 = new Choice("Bob (2)", ContestType.IRV.toString(), false, false);
        List<Choice> my_choices = List.of(c1,c2,c3,c4);
        my_contest = new Contest("testName", new County(), ContestType.IRV.toString(), my_choices, 10, 1, 0) ;
    }

    @AfterTest()
    public void tearDown() {
    }

    @Test
    public void validIRVChoiceNamesTest() {
       assertTrue(my_contest.isValidIRVChoiceName("Alice"));
       assertTrue(my_contest.isValidIRVChoiceName("Bob"));
       assertFalse(my_contest.isValidIRVChoiceName("Diego"));
    }

}
