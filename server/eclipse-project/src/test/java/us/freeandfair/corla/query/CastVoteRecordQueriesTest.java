package us.freeandfair.corla.query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.*;

import static org.testng.Assert.*;

import java.time.Instant;
import java.util.Properties;

import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;

@Test(groups = {"integration"})
public class CastVoteRecordQueriesTest {

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

  public List<CVRContestInfo> noisyContestSetup(){
    return noisyContestSetup(1L);
  }

  public List<CVRContestInfo> noisyContestSetup(Long countyId){
    County c = new County("test" + countyId.toString(), countyId);

    List<Choice> choices = new ArrayList();
    Choice choice = new Choice("why?",
                               "",
                               false,
                               false);
    choices.add(choice);
    Contest co = new Contest("test",
                             c,
                             "",
                             choices,
                             1,
                             1,
                             1);
    co.setID(1L);

    List<String> votes = new ArrayList();
    votes.add("why?");

    CVRContestInfo ci = new CVRContestInfo(co, null,null, votes);
    List<CVRContestInfo> contest_info = new ArrayList();
    contest_info.add(ci);

    Persistence.save(c);
    Persistence.save(co);
    Persistence.flush();

    return contest_info;
  }

  public CastVoteRecord noisyCVRSetup() {
    return noisyCVRSetup(1);
  }

  public CastVoteRecord noisyCVRSetup(final Integer position) {
    return noisyCVRSetup(position, noisyContestSetup(Long.valueOf(position)));
  }

  public CastVoteRecord noisyCVRSetup(final Integer position, final List<CVRContestInfo> contest_info) {
    CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED,
                                            null,
                                            1L,
                                            position,
                                            1,
                                            1,
                                            "1",
                                            1,
                                            "1",
                                            "a",
                                            contest_info);
    Persistence.save(cvr);

    // Without flushing the persistence context, `deleteAll(1L)` will
    // throw an exception.
    Persistence.flush();
    return cvr;
  }

  @Test()
  public void reauditTest() {
    // let's pretend the current max revision is 2, which we'll inc, and then
    // assertEquals
    Long curMax = 2L;
    CastVoteRecord cvr = noisyCVRSetup(2);
    cvr.setRevision(curMax + 1L);
    cvr.setToReaudited();
    Long result = CastVoteRecordQueries.forceUpdate(cvr);
    assertEquals((long) 1L, (long)result);
    Long maxRev = CastVoteRecordQueries.maxRevision(cvr);
    assertEquals((long)maxRev, (long)curMax + 1L);
  }

  @Test()
  public void deleteAllTest() {
    noisyCVRSetup(1);
    // this is the method under test
    Integer result = CastVoteRecordQueries.deleteAll(1L);

    assertEquals((int) result, (int) 1,
                 "a result of 1 means one thing was deleted");
  }

  @Test()
  public void canonicalChoicesTest() {
    CastVoteRecord cvr = noisyCVRSetup(1);
    // commit the transaction to populate the DB for debugging
    // Persistence.commitTransaction();
    // Persistence.beginTransaction();
    // Note: the weird access to get the contest ID is because somehow it gets changed from 1 to 4 when run with other tests.
    // TODO: THIS SHOULD NOT HAPPEN! WHY IS IT CHANGING FROM 1 TO 4 WHEN RUN WITH OTHER TESTS?!
    int result = CastVoteRecordQueries.updateCVRContestInfos(cvr.countyID(),cvr.contestInfo().get(0).contest().id(), "why?","because.");
    assertEquals(result, 1,
                 "a result of 1 means one choice was changed");

    Persistence.currentSession().refresh(cvr, LockMode.PESSIMISTIC_WRITE);
    assertTrue(cvr.contestInfo().toString().contains("choices=[because.]"));

  }


  @Test()
  public void activityReportTest() {
    CastVoteRecord cvr = noisyCVRSetup(1);
    CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                                             cvr.countyID(), cvr.cvrNumber(), null, cvr.scannerID(),
                                             cvr.batchID(), cvr.recordID(), cvr.imprintedID(),
                                             cvr.ballotType(), cvr.contestInfo());
    acvr.setComment("testing");
    acvr.setAuditBoardIndex(14);
    acvr.setCvrId(cvr.id());
    CVRAuditInfo cai = new CVRAuditInfo(cvr);
    cai.setACVR(acvr);

    Persistence.save(acvr);
    Persistence.save(cai);

    List<CastVoteRecord> acvrs = new ArrayList(){{ add(acvr); }};
    List<Long> contestCVRIds = new ArrayList(){{ add(cvr.id()); }};
    List<CastVoteRecord> result = CastVoteRecordQueries.activityReport(contestCVRIds);
    assertEquals(acvrs, result);
  }

}
