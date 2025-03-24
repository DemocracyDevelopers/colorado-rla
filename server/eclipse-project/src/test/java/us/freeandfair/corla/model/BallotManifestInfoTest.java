package us.freeandfair.corla.model;

import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.AssertJUnit.*;

public class BallotManifestInfoTest extends TestClassWithDatabase {

    private static final Long the_county_id = 1L;
    private static final Integer the_scanner_id = 1;
    private static final String the_batch_id = "Batch 1";
    private static final Integer the_batch_size = 10;
    private static final String the_storage_location = "Warehouse";
    private static final Long the_sequence_start = 1L;
    private static final Long the_sequence_end = 10L;
    private static final Long the_id = 2L;
    private static final Integer rand = 12345;
    private static final Long start = 23L;
    private static final String uri = "bmi:1:1-Batch 1";

    private static final BallotManifestInfo bmi = new BallotManifestInfo(the_county_id,
            the_scanner_id,
            the_batch_id,
            the_batch_size,
            the_storage_location,
            the_sequence_start,
            the_sequence_end);



    @Test
    public static void testGettersAndSetters() {
        assertNull(bmi.id());

        // This seemingly never used?
        assertNull(bmi.version());

        bmi.setID(the_id);
        assertEquals(the_id, bmi.id());
        assertEquals(the_scanner_id, bmi.scannerID());
        assertEquals(the_batch_id, bmi.batchID());
        assertEquals(the_batch_size, bmi.batchSize());
        assertEquals(the_storage_location, bmi.storageLocation());
        assertEquals(the_sequence_start, bmi.sequenceStart());
        assertEquals(the_sequence_end, bmi.sequenceEnd());
        assertEquals(uri, bmi.getUri());

        bmi.setUltimate(start);
        assertEquals(start, bmi.sequenceStart());
        // For some reason I had to cast here?
        assertEquals((Long)(the_sequence_end - the_sequence_start), bmi.rangeSize());
        assertEquals((Long)(bmi.rangeSize() + start), bmi.ultimateSequenceEnd);
    }

    @Test
    public static void testBMILogic() {

        bmi.setUltimate(start);
        assertEquals((Integer)(rand - bmi.ultimateSequenceStart.intValue()+ 1), bmi.sequencePosition(rand));
        assertEquals(bmi.sequencePosition(rand), bmi.translateRand(rand));


        assertFalse(bmi.isHolding(0L));
        assertFalse(bmi.isHolding(100000L));
        assertTrue(bmi.isHolding(bmi.ultimateSequenceStart));
        assertTrue(bmi.isHolding(bmi.ultimateSequenceEnd));
        assertTrue(bmi.isHolding(25L));

        assertEquals(bmi.ballotPosition(rand.intValue()), (Integer)12323);
        // Why are these types so inconsistent?
        //assertEquals(bmi.imprintedID(new Long((long)rand), "1-Batch 1-12323");





    }
}
