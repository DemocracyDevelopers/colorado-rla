package us.freeandfair.corla.model;

import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import org.testng.annotations.Test;

import java.time.Instant;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.*;
import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableHashCode;

public class AuditInvestigationReportInfoTest extends TestClassWithDatabase {

    private static final String name = "name";
    private static final String report = "report";
    private static final Instant timestamp = Instant.now();
    private static final int hashcode = nullableHashCode(timestamp);

    private static final AuditInvestigationReportInfo empty = new AuditInvestigationReportInfo();
    private static final AuditInvestigationReportInfo nonempty = new AuditInvestigationReportInfo(timestamp, name, report);


    /**
     * A test for constructors and getters/setters.
     * These tests are rarely that informative, but they're needed for coverage.
     */
    @Test
    public static void testGettersAndSetters() {
        assertNull(empty.timestamp());
        assertNull(empty.report());
        assertNull(empty.name());

        assertEquals("AuditInvestigationReport [timestamp=null, name=null, report=null]", empty.toString());

        assertEquals(timestamp, nonempty.timestamp());
        assertEquals(name, nonempty.name());
        assertEquals(report, nonempty.report());
        assertEquals(hashcode, nonempty.hashCode());

    }

    /**
     * Ensure that equality functions as expected
     */
    @Test
    public static void testEquality() {
        assertNotEquals(empty, nonempty);
        assertNotEquals(nonempty, empty);
        assertNotEquals("", empty);
        assertNotEquals("", nonempty);
        assert(empty.equals(empty));
        assert(nonempty.equals(nonempty));

    }
}
