package us.freeandfair.corla.model;

import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AdministratorQueries;
import us.freeandfair.corla.util.TestClassWithDatabase;

import static org.testng.AssertJUnit.*;

@Test
public class AdministratorTest extends TestClassWithDatabase {

    @Test
    public static void testGettersAndSetters() {
        String expectedUsername = "testname";
        Administrator.AdministratorType expectedType = Administrator.AdministratorType.STATE;
        String expectedFullname = "fulltestname";
        Administrator admin = new Administrator(expectedUsername,
                expectedType,
                expectedFullname,
                null);

        Persistence.saveOrUpdate(admin);

        // this is a database constraint
        assertNotNull(admin.id());

        Long expectedID = 42L;
        admin.setID(expectedID);
        assertEquals(expectedID, admin.id());

        assertEquals(expectedUsername, admin.username());
        assertEquals(expectedFullname, admin.fullName());
        assertEquals(expectedType, admin.type());
        assertNull(admin.county());

    }
}
