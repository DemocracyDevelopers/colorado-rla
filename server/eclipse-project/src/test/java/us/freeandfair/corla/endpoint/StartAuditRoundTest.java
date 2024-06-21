package us.freeandfair.corla.endpoint;

import static org.testng.Assert.assertEquals;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.*;

import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;

import java.util.Properties;

@Test(groups = {"integration"})
public class StartAuditRoundTest {

  private StartAuditRoundTest() {};
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
  }

  @AfterMethod
  public static void afterEach() {
    Persistence.rollbackTransaction();
  }

  // this test doesn't do much yet
  @Test()
  public void testReadyToStartFalse() {
    StartAuditRound sar = new StartAuditRound();
    County county = new County("c1", 1L);
    CountyDashboard cdb = new CountyDashboard(county);
    CountyDashboardASM cdbAsm = new CountyDashboardASM(cdb.id().toString());

    assertEquals(false, (boolean)sar.isReadyToStartAudit(cdb));
  }

}
