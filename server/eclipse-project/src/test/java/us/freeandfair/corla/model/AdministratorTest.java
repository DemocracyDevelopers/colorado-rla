package us.freeandfair.corla.model;

import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AdministratorQueries;
import us.freeandfair.corla.util.TestClassWithDatabase;

import java.time.Clock;
import java.time.Instant;

import static org.testng.AssertJUnit.*;

@Test
public class AdministratorTest extends TestClassWithDatabase {

    @Test
    public static void testGettersAndSetters() {
        String expectedUsername = "testname";
        Administrator.AdministratorType expectedType = Administrator.AdministratorType.STATE;
        String expectedFullname = "fulltestname";

        // Make the clock stuck
        Clock testClock = Clock.fixed(Instant.now(), Clock.systemDefaultZone().getZone());
        Administrator admin = new Administrator(expectedUsername,
                expectedType,
                expectedFullname,
                null,
                testClock);

        assertNull(admin.id());
        Persistence.saveOrUpdate(admin);

        // this is a database constraint
        assertNotNull(admin.id());

        Long expectedID = 42L;
        admin.setID(expectedID);
        assertEquals(expectedID, admin.id());

        assertEquals(expectedUsername, admin.username());
        assertEquals(expectedFullname, admin.fullName());
        assertEquals(expectedType, admin.type());
        assertNull(admin.version());
        assertNull(admin.county());

        // Because we're using a stopped clock, this time will be the same for both
        Instant expectedTime = testClock.instant();
        assertNull(admin.lastLoginTime());
        admin.updateLastLoginTime();
        assertEquals(expectedTime, admin.lastLoginTime());

        assertNull(admin.lastLogoutTime());
        admin.updateLastLogoutTime();
        assertEquals(expectedTime, admin.lastLogoutTime());

        String expected_string = "Administrator [username=" + expectedUsername + ", type=" +
                expectedType+ ", full_name=" + expectedFullname+ ", county=" +
                null + ", last_login_time=" + expectedTime +
                ", last_logout_time=" + expectedTime + "]";

    }
}
