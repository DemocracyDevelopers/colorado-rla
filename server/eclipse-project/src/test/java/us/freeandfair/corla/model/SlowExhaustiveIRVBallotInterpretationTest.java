package us.freeandfair.corla.model;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import us.freeandfair.corla.model.IRVBallots.BallotInterpretationDuplicatesBeforeOvervotes;
import us.freeandfair.corla.model.IRVBallots.IRVChoices;
import us.freeandfair.corla.model.IRVBallots.Preference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/* This test class runs an exhaustive test on all possible ballots of a given size
 * (currently set to 5) and applies the BallotInterpretationDuplicatesBeforeOvervotes
 * interpretation function to all of them. This verifies that the official ballot
 * interpretation function always produces valid IRV ballots as output, at least up
 * to inputs of length 5.
 */
public class SlowExhaustiveIRVBallotInterpretationTest {

    private final List<IRVChoices> testVoteList1 = new ArrayList<IRVChoices>();

    private SlowExhaustiveIRVBallotInterpretationTest() {};

    @BeforeTest()
    public void setUp() {
        testVoteList1.add(new IRVChoices("Alice(1)"));
        testVoteList1.add(new IRVChoices("Alice(1),Bob(2)"));
        testVoteList1.add(new IRVChoices("Alice(1),Alice(1),Bob(2)"));
        testVoteList1.add(new IRVChoices("Alice(1),Alice(2),Bob(3)"));
        testVoteList1.add(new IRVChoices("Alice(1),Alice(2),Bob(2)"));
    }

    @AfterTest()
    public void tearDown() {
    }

    @Test
    void testValidityOfDuplicatesBeforeOvervotes() {
        assertTrue(ballotListValidityTest(BallotInterpretationDuplicatesBeforeOvervotes::InterpretValidIntent, testVoteList1));
    }

    @Test
    void testValidityOfDuplicatesBeforeOvervotesExhaustively() {
        assertTrue(testValidityExhaustively(5,
                BallotInterpretationDuplicatesBeforeOvervotes::InterpretValidIntent));
    }

    // Runs the ballot interpretation function on all the ballots in the input
    private static boolean ballotListValidityTest(Function<IRVChoices, IRVChoices> ballotInterpretationFunction, List<IRVChoices> votes)  {

        return votes.stream().allMatch(v -> ballotValidityTest(ballotInterpretationFunction, v));
    }

    private static boolean ballotValidityTest(Function <IRVChoices, IRVChoices> ballotInterpretationFunction, IRVChoices vote) {

        return ballotInterpretationFunction.apply(vote).IsValid();
    }


    /* Exhaustively enumerate all possible ballots, test that the ballotInterpretationFunction returns
     * a valid ballot.
     * Warning: This function is quadratic-exponential in numCandidates, i.e., its running time is 2^(numCandidates^2).
     * numCandidates = 5 takes about half a minute. 6 or 7 candidates will take a long time.
     *
     * Example with 3 candidates: consider each possible (possibly invalid) preference list as a subset of
     * C(3), C(2), C(1),
     * B(3), B(2), B(1),
     * A(3), A(2), A(1)
     * and similarly for larger values of n.
     *
     * Iterate though all values of a pad up to 2^(n*n), in which the bits are numbered starting with 0 for the lsb.
     * Use the standard trick of interpreting the (long) int as a bitmap into the set with the lsb at the bottom
     * right of the matrix. For i = 0 .. n-1 and j = 0 .. n-1, the (n-i)th row and (n-j)th column is included
     * in the vote if the (n*j + i)th bit of the pad is 1.
     */
    private static boolean testValidityExhaustively(int numCandidates, Function<IRVChoices, IRVChoices> ballotInterpretationFunction)  {

        // 49 bits fit comfortably in a long.
        assert numCandidates <= 7 : "Error: testing validity exhaustively does not work for more than 7 candidates.";

        long pad;
        long stoppingPoint =  1L << (numCandidates*numCandidates) ;

        for(pad = 0 ; pad < stoppingPoint ; pad++) {
            IRVChoices vote = generateVoteFromPad(pad, numCandidates);
            if(! ballotValidityTest(ballotInterpretationFunction, vote)) {
                return false;
            }
        }

        return true;
    }

    private static IRVChoices generateVoteFromPad(long pad, int numCandidates) {

        String[] candidateNameArray = {"A", "B", "C", "D", "E", "F", "G"};
        List<Preference> choices = new ArrayList<Preference>();
        long localPad = pad;

        for(int i=0 ; i < numCandidates ; i++) {
            String name = candidateNameArray[i];
            // Run from lsb to numCandidates-th bit, adding a preference of that value+1 for that candidate
            // if the bit is 1.
            for(int j = 0 ; j < numCandidates ; j++) {
                if(localPad % 2 == 1) {
                    choices.add(new Preference(j+1, name));
                }
                localPad = localPad >> 1;
            }
        }
        return new IRVChoices(choices);
    }
}
