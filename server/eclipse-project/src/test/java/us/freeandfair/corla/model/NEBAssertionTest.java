package us.freeandfair.corla.model;


import org.testng.annotations.Test;
import us.freeandfair.corla.math.Audit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import static java.lang.Math.pow;
import static org.junit.Assert.*;

public class NEBAssertionTest {

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
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        assertEquals(1, neb.score(info));
    }

    @Test()
    public void testScore2(){
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob", "Alice"));

        assertEquals(-1, neb.score(info));
    }

    @Test()
    public void testScore3(){
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice", "Bob"));

        assertEquals(0, neb.score(info));
    }

    @Test()
    public void testScore4(){
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, new ArrayList<String>());

        assertEquals(0, neb.score(info));
    }
    @Test()
    public void testScore5(){
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, List.of("Bob"));

        assertEquals(-1, neb.score(info));
    }

    @Test()
    public void testScore6(){
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo info = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, List.of("Alice"));

        assertEquals(1, neb.score(info));
    }


    @Test()
    public void testComputeDiscrepancyOneOver() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(1, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(1, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyNone() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertFalse(discrepancy.isPresent());
    }

    @Test()
    public void testComputeDiscrepancyTwoOver1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob", "Alice"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyTwoOver2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyZero1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(0, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(0, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyZero2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice", "Bob"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(0, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(0, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyZero3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob", "Alice"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, List.of("Chuan", "Bob", "Alice"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(0, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(0, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyOneUnder() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Alice"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(-1, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(-1, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyTwoUnder() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, List.of("Alice"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(-2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(-2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomBallot1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_BALLOT, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(0, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(0, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomBallot2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_BALLOT, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(1, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(1, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomBallot3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_BALLOT, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyNoConsensus() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Chuan"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.NO, Arrays.asList("Alice", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomRecord1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_RECORD, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(0, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(0, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomRecord2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_RECORD, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyPhantomRecord3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.NO, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_RECORD, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, null);

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyUpdateDiscrepancy1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        neb.cvrDiscrepancy.put(cvr.getCvrId(), 0);

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        assertEquals(1, neb.cvrDiscrepancy.size());
        assertEquals(2, discrepancy.getAsInt());
        assertTrue(neb.cvrDiscrepancy.containsKey(cvr.getCvrId()));
        assertEquals(2, neb.cvrDiscrepancy.get(cvr.getCvrId()).intValue());
    }

    @Test()
    public void testComputeDiscrepancyUpdateDiscrepancy2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                64L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        neb.cvrDiscrepancy.put(cvr.getCvrId(), 2);

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertFalse(discrepancy.isPresent());
        assertEquals(0, neb.cvrDiscrepancy.size());
    }

    @Test()
    public void testRecordDiscrepancy1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Bob", "Alice"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(1), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRecordDiscrepancy2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(1), neb.my_other_count);
    }

    @Test()
    public void testRecordDiscrepancy3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Wendy", "Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(1), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRecordDiscrepancy4() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Chuan", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(1), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRecordDiscrepancy5() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(1), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRecordDiscrepancy6() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertFalse(discrepancy.isPresent());
        neb.recordDiscrepancy(new CVRAuditInfo(cvr));
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(1), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(1), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Bob", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Chuan", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(1), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy4() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Chuan", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Chuan"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(1), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy5() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Chuan", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertTrue(discrepancy.isPresent());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(1), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRemoveDiscrepancy6() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        final CVRContestInfo cvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CVRContestInfo acvrInfo = new CVRContestInfo(contest, "",
                CVRContestInfo.ConsensusValue.YES, Arrays.asList("Alice", "Wendy"));

        final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(cvrInfo));

        final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                16L, 1, 1, 1, "Batch1",
                1, "1-Batch1-1", null, List.of(acvrInfo));

        final OptionalInt discrepancy = neb.computeDiscrepancy(cvr, acvr);
        assertFalse(discrepancy.isPresent());
        assertEquals(0, neb.cvrDiscrepancy.size());

        final CVRAuditInfo cvr_info = new CVRAuditInfo(cvr);
        neb.recordDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);

        neb.removeDiscrepancy(cvr_info);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_over_count);
        assertEquals(Integer.valueOf(0), neb.my_one_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_two_vote_under_count);
        assertEquals(Integer.valueOf(0), neb.my_other_count);
    }

    @Test()
    public void testRiskComputation1() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 1;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), 0) * pow(1 - 1/gamma, -1) *
                pow(1 + 1/(2*gamma), 0) * pow(1 + 1/gamma, 0);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation2() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 1;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), -1) * pow(1 - 1/gamma, 0) *
                pow(1 + 1/(2*gamma), 0) * pow(1 + 1/gamma, 0);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation3() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 1;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), 0) * pow(1 - 1/gamma, 0) *
                pow(1 + 1/(2*gamma), -1) * pow(1 + 1/gamma, 0);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation4() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 1;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), 0) * pow(1 - 1/gamma, 0) *
                pow(1 + 1/(2*gamma), 0) * pow(1 + 1/gamma, -1);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation5() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 1;
        neb.my_two_vote_over_count = 1;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), -1) * pow(1 - 1/gamma, -1) *
                pow(1 + 1/(2*gamma), 0) * pow(1 + 1/gamma, 0);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation6() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 1;
        neb.my_two_vote_under_count = 1;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), 0) * pow(1 - 1/gamma, 0) *
                pow(1 + 1/(2*gamma), -1) * pow(1 + 1/gamma, -1);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation7() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 1;
        neb.my_two_vote_over_count = 1;
        neb.my_one_vote_under_count = 1;
        neb.my_two_vote_under_count = 1;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 1000;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), -1) * pow(1 - 1/gamma, -1) *
                pow(1 + 1/(2*gamma), -1) * pow(1 + 1/gamma, -1);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation8() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final double dilutedMargin = 50.0/5000.0;
        final double U = (2*gamma)/dilutedMargin;
        final int auditedSample = 10;
        double risk = pow(1 - 1/U, auditedSample) * pow(1 - 1/(2*gamma), 0) * pow(1 - 1/gamma, 0) *
                pow(1 + 1/(2*gamma), 0) * pow(1 + 1/gamma, 0);

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.valueOf(risk).setScale(3, BigDecimal.ROUND_HALF_UP), computedRisk);
    }

    @Test()
    public void testRiskComputation9() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 0;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final int auditedSample = 0;

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.ONE, computedRisk);
    }

    @Test()
    public void testRiskComputation10() {
        NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
                50, 5000, 100);

        neb.my_one_vote_over_count = 1;
        neb.my_two_vote_over_count = 0;
        neb.my_one_vote_under_count = 0;
        neb.my_two_vote_under_count = 0;

        final double gamma = 1.03905;
        final int auditedSample = 0;

        BigDecimal computedRisk = neb.riskMeasurement(auditedSample, BigDecimal.valueOf(gamma));
        assertEquals(BigDecimal.ONE, computedRisk);
    }
}
