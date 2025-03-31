package us.freeandfair.corla.model;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.*;

import us.freeandfair.corla.model.CastVoteRecord.RecordType;

import static org.testng.Assert.*;

public class CastVoteRecordTest {
    private CastVoteRecord cvr1;
    private CastVoteRecord cvr2;
    private CastVoteRecord cvr3;
    private CastVoteRecord cvr4;
    private Instant now;

    @BeforeClass
    public void CastVoteRecordTest() {
        now = Instant.now();
        cvr1 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch1", 1, "1-Batch1-1", null, null);
        cvr2 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch2", 1, "1-Batch2-1", null, null);
        cvr3 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch11", 1, "1-Batch11-1", null, null);
        cvr4 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch2", 1, "1-Batch2-1", null, null);

    }

    @Test()
    public void comparatorTest() {
        assertEquals(cvr1.compareTo(cvr2), -1);
        assertEquals(cvr2.compareTo(cvr4), 0);
        assertEquals(cvr3.compareTo(cvr2), 1);
    }

    /**
     * Begin new tests
     */

    private static final Clock fixedClock = Clock.fixed(Instant.now(), Clock.systemDefaultZone().getZone());
    private static final RecordType the_record_type = RecordType.UPLOADED;
    private static final Instant the_timestamp = fixedClock.instant();
    private static final Long the_county_id = 1L;
    private static final Integer the_cvr_number = 1;
    private static final Integer the_sequence_number = 3;
    private static final Integer the_scanner_id = 1;
    private static final String the_batch_id = "Batch 1";
    private static final Integer the_record_id = 37;
    private static final String the_imprinted_id = "0123456789"; //TODO find a realistic one here
    private static final String the_ballot_type = "Absentee";
    private static final List<CVRContestInfo> the_contest_info = new ArrayList();

    private static final CastVoteRecord cvr = new CastVoteRecord(the_record_type,
            the_timestamp,
            the_county_id,
            the_cvr_number,
            the_sequence_number,
            the_scanner_id,
            the_batch_id,
            the_record_id,
            the_imprinted_id,
            the_ballot_type,
            the_contest_info);
    @Test
    public static void testGettersAndSetters() {
        // we need to commit to the DB to make this work .
        assertEquals(cvr.id(), (Long)1L);
    }
}
