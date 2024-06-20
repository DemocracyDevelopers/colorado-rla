package us.freeandfair.corla.query;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.*;
import org.testng.Assert;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;

@Test(groups = {"integration"})
public class ContestResultQueriesTest {

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
  public static void afterEach() { Persistence.rollbackTransaction(); }


  @Test()
  public void findOrCreateTest() {
    County county = new County("abc", 321L);
    Persistence.saveOrUpdate(county);
    Assert.assertEquals((Long)county.version(),(Long) 0L);
    Contest contest = new Contest("def",
                                  county,
                                  "desc",
                                  new ArrayList<>(),
                                  1,
                                  1,
                                  0);
    Persistence.saveOrUpdate(contest);
    ContestResult crSetup = new ContestResult(contest.name());
    Persistence.saveOrUpdate(crSetup);
    ContestResult cr = ContestResultQueries.findOrCreate("def");
    Assert.assertEquals(cr.getContestName(), "def");

    // contructed from database
    Assert.assertEquals(cr.id(), crSetup.id());
    ContestResult cr2 = ContestResultQueries.findOrCreate("ghi");
    // newly contructed and persisted!
    Assert.assertNotEquals(cr2.id(), cr.id());

    //ride along to avoid state mgmt
    Integer result = ContestResultQueries.count();
    Assert.assertEquals((int)result, (int) 2);
  }

  // @Test()
  // public void testCount() {
  //   County county = new County("abc", 321L);
  //   Persistence.saveOrUpdate(county);
  //   Contest contest = new Contest("def",
  //                                 county,
  //                                 "desc",
  //                                 new ArrayList<>(),
  //                                 1,
  //                                 1,
  //                                 0);

  //   Persistence.saveOrUpdate(contest);
  //   Integer result = ContestResultQueries.count();
  //   Assert.assertEquals((int)result, (int) 1);
  // }

}
