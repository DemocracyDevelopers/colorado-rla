package au.org.democracydevelopers.model;

import au.org.democracydevelopers.model.NENAssertion;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NENAssertionTest {

    private final County county = new County("Denver", 16L);

    private final List<Choice> choice_list = Arrays.asList(
            new Choice("Alice", "", false, false),
            new Choice("Bob", "", false, false),
            new Choice("Wendy", "", false, false),
            new Choice("Chuan", "", false, false)
    );

    private final Contest contest = new Contest("Board of Tax and Estimation", county, "IRV",
            choice_list, 4, 1, 1);

    @Test()
    public void testScore1(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, Arrays.asList("Alice", "Bob", "Chuan"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice", "Bob"));

        assertEquals(1, nen.score(info));
    }

    @Test()
    public void testScore2(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, Arrays.asList("Alice", "Bob"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, List.of("Alice"));

        assertEquals(1, nen.score(info));
    }

    @Test()
    public void testScore3(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, Arrays.asList("Alice", "Bob"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, new ArrayList<String>());

        assertEquals(0, nen.score(info));
    }

    @Test()
    public void testScore4(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, new ArrayList<String>());

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Chuan"));

        assertEquals(0, nen.score(info));
    }

    @Test()
    public void testScore5(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, Arrays.asList("Bob", "Alice"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        assertEquals(-1, nen.score(info));
    }

    @Test()
    public void testScore6(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, Arrays.asList("Bob", "Alice"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        assertEquals(-1, nen.score(info));
    }

    @Test()
    public void testScore7(){
        NENAssertion nen = new NENAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100, List.of("Bob"));

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice"));

        assertEquals(0, nen.score(info));
    }

}
