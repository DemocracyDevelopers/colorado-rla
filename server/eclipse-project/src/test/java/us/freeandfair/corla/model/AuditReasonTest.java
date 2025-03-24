package us.freeandfair.corla.model;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class AuditReasonTest {

    /**
     * Tests that the expected audit selection is returned based on the type of Audit Reason, and
     */
    @Test
    public static void testAuditReasonLogic() {

        // Tied and opportunistic contests are never considered "audited"
        final AuditReason tied = AuditReason.TIED_CONTEST;
        final AuditReason opportunistic = AuditReason.OPPORTUNISTIC_BENEFITS;

        assertEquals("Tied Contest", tied.prettyString());
        assertEquals(AuditSelection.UNAUDITED_CONTEST, tied.selection());
        assertEquals(AuditSelection.UNAUDITED_CONTEST, opportunistic.selection());

        assertFalse(tied.isTargeted());

        // Everything else is
        final AuditReason statewide = AuditReason.STATE_WIDE_CONTEST;
        assertEquals(AuditSelection.AUDITED_CONTEST, statewide.selection());

        assert(statewide.isTargeted());

    }
}
