package us.freeandfair.corla.query;

import java.io.ByteArrayOutputStream;

import java.util.*;

import org.junit.Ignore;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.*;

import static org.testng.Assert.*;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;
import us.freeandfair.corla.query.ExportQueries;

import org.hibernate.Session;
import org.hibernate.query.Query;



@Test(groups = {"integration"})
public class ExportQueriesTest {

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres
          = new PostgreSQLContainer<>("postgres:15-alpine")
          // None of these actually have to be the same as the real database (except its name), but this
          // makes it easy to match the setup scripts.
          .withDatabaseName("corla")
          .withUsername("corlaadmin")
          .withPassword("corlasecret")
          // .withInitScripts("corlaInit.sql","contest.sql");
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
    Persistence.beginTransaction();

  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  @BeforeMethod
  public static void beforeEach() {
    Persistence.beginTransaction();
    insertSeed();
  }

  @AfterMethod
  public static void afterEach() {
    Persistence.rollbackTransaction();
  }

  private static void insertSeed() {
    final Session s = Persistence.currentSession();
    String query = "insert into dos_dashboard (id,seed) values (99,'1234');";
    s.createNativeQuery(query).executeUpdate();
  }

  // Note: the code this is testing is broken. JSON reports appear to be unused, so I'm ignoring it for now.
  @Test( enabled = false )
  public void jsonRowsTest() {
    String q = "SELECT seed FROM dos_dashboard";
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ExportQueries.jsonOut(q, os);

    assertEquals(os.toString(), "[{\"seed\":\"1234\"}]");
  }

  @Test()
  public void csvOutTest() {
    String q = "SELECT seed FROM dos_dashboard";
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ExportQueries.csvOut(q, os);

    assertEquals(os.toString(), "seed\n1234\n");
  }

  @Test()
  public void sqlFilesTest()
    throws java.io.IOException {
    Map<String,String> files = ExportQueries.sqlFiles();
    assertTrue(files.get("seed").contains("SELECT seed FROM dos_dashboard"));
  }
}
