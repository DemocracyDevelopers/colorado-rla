package us.freeandfair.corla.query;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.*;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

import java.util.Properties;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@Test
public class AdministratorQueriesTest {

    /**
     * Container for the mock-up database.
     */
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("corla")
            .withUsername("corlaadmin")
            .withPassword("corlasecret")
            .withInitScript("SQL/corlaInitEmpty.sql");

    @BeforeClass
    public static void beforeAll() {
        postgres.start();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.driver", "org.postgresql.Driver");
        hibernateProperties.setProperty("hibernate.url", postgres.getJdbcUrl());
        hibernateProperties.setProperty("hibernate.user", postgres.getUsername());
        hibernateProperties.setProperty("hibernate.pass", postgres.getPassword());
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        Persistence.setProperties(hibernateProperties);
    }
    @BeforeMethod
    public static void beforeEach() {
        Persistence.beginTransaction();
    }

    @AfterMethod
    public static void afterEach() {
        Persistence.rollbackTransaction();
    }

    @AfterClass
    public static void afterall() {
        postgres.stop();
    }

    @Test
    public void testByUsernameState() {

        Administrator admin = new Administrator("testname",
                                                Administrator.AdministratorType.STATE,
                                                "fulltestname",
                                                null);

        Persistence.saveOrUpdate(admin);
        assertEquals(AdministratorQueries.byUsername("testname"), admin);
    }


    @Test
    public void testByUsernameCounty() {

        County county = new County("test", 1L);
        Persistence.saveOrUpdate(county);
        Administrator admin = new Administrator("testname",
                Administrator.AdministratorType.COUNTY,
                "fulltestname",
                county
                );

        Persistence.saveOrUpdate(admin);
        assertEquals(AdministratorQueries.byUsername("testname"), admin);
    }

    @Test
    public void testByUsernameStateAndCounty() {
        County county = new County("test", 1L);
        Persistence.saveOrUpdate(county);
        Administrator countyadmin = new Administrator("county",
                Administrator.AdministratorType.COUNTY,
                "countyfull",
                county);

        Persistence.saveOrUpdate(countyadmin);

        Administrator stateadmin = new Administrator("state",
                Administrator.AdministratorType.STATE,
                "statefull",
                null);

        Persistence.saveOrUpdate(stateadmin);

        assertEquals(AdministratorQueries.byUsername("county"),countyadmin);
        assertEquals(AdministratorQueries.byUsername("state"), stateadmin);
    }

    @Test
    public void testSameName() {

        Administrator first = new Administrator("state",
                Administrator.AdministratorType.STATE,
                "first",
                null);

        Administrator second = new Administrator("state",
                Administrator.AdministratorType.STATE,
                "second",
                null);
        Persistence.saveOrUpdate(first);
        Persistence.saveOrUpdate(second);

        assertNull(AdministratorQueries.byUsername("state"));
    }

    @Test
    public void testDBError() {
        // Close the DB
        Persistence.commitTransaction();
        assertNull(AdministratorQueries.byUsername("username"));
        Persistence.beginTransaction();
    }
}
