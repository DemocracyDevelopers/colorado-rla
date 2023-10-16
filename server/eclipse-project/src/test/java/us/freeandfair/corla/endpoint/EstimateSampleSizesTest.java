package us.freeandfair.corla.endpoint;

import org.hibernate.Session;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;


public class EstimateSampleSizesTest {

  private EstimateSampleSizesTest() {};


  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

    // Create DoSDashboard with some audit information.
    DoSDashboard dosb = new DoSDashboard();
    dosb.updateAuditInfo(new AuditInfo("general", Instant.now(), Instant.now(),
            "12856782643571354365", BigDecimal.valueOf(5)));
    Persistence.saveOrUpdate(dosb);
  }

  @AfterTest()
  public void tearDown() {
    try {
      Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  @Test
  public void testEstimateSampleSizePlurality(){

    // Add data for county contest results to the database
    final Session s = Persistence.currentSession();

    String query = "insert into contest (id,description,name,sequence_number,votes_allowed,winners_allowed,county_id) values "
            + " (0,'PLURALITY','Board of Parks',0,1,1,48), (1,'PLURALITY','Board of Tax and Estimation',1,1,1,49)";
    s.createNativeQuery(query).executeUpdate();

    query = "insert into contest_choice (contest_id,fictitious,name,qualified_write_in,index) values "
            + " (0,false,'Alice',false,0), (0,false,'Bob',false,1), (0,false,'Chuan',false,2), (0,false,'Diego',false,3), "
            + " (1,false,'Alice',false,0), (1,false,'Bob',false,1), (1,false,'Chuan',false,2), (1,false,'Diego',false,3) ";
    s.createNativeQuery(query).executeUpdate();

    query = " insert into county_contest_result "
            + "(id,contest_ballot_count,county_ballot_count,losers,max_margin,min_margin,winners,winners_allowed,contest_id,county_id) "
            + "values (0,170,170,'Alice,Bob,Diego',90,10,'Chuan',1,0,48), (1,225,225,'Alice,Bob,Chuan',145,15,'Diego',1,1,49)";
    s.createNativeQuery(query).executeUpdate();

    query = "insert into county_contest_vote_total (result_id,vote_total,choice) values "
            + " (0,20,'Alice'), (0,10,'Bob'), (0,100,'Chuan'), (0,40,'Diego'), "
            + " (1,50,'Alice'), (1,5,'Bob'), (1,20,'Chuan'), (1,150,'Diego') ";
    s.createNativeQuery(query).executeUpdate();

    EstimateSampleSizes esr = new EstimateSampleSizes();
    Map<String,Integer> samples = esr.estimateSampleSizes();
    System.out.println(samples);
  }

  /*
  @Test()
  public void testEstimateSampleSizesSimplePlurality() {
    // For testing sample size estimation endpoint, we need a series of CountyContestResult's
    // in the database, their associated Counties and Contests, a DoSDashboard with audit info.

    County cty = CountyQueries.fromString("Boulder");

    Persistence.saveOrUpdate(cty);

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");
    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Board of Parks", cty, "PLURALITY", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);

    CountyContestResult ctr = new CountyContestResult(cty, c1);

    int cntr = 0;
    for(int i = 0; i < 20; ++i) {
      ctr.addCVR(createVoteFor("Alice", c1, cty, cntr));
      ++cntr;
    }
    for(int i = 0; i < 10; ++i) {
      ctr.addCVR(createVoteFor("Bob", c1, cty, cntr));
      ++cntr;
    }
    for(int i = 0; i < 100; ++i) {
      ctr.addCVR(createVoteFor("Chuan", c1, cty, cntr));
      ++cntr;
    }
    for(int i = 0; i < 40; ++i) {
      ctr.addCVR(createVoteFor("Diego", c1, cty, cntr));
      ++cntr;
    }

    ctr.updateResults();

    Persistence.saveOrUpdate(ctr);

    List<CountyContestResult> contestResults = Persistence.getAll(CountyContestResult.class);

    System.out.println(contestResults.size());

  }

  private CastVoteRecord createVoteFor(final String name, final Contest co, final County cty, Integer position){
    // Create CVRContestInfo
    List<String> votes = new ArrayList();
    votes.add(name);

    CVRContestInfo ci = new CVRContestInfo(co, null,null, votes);
    List<CVRContestInfo> contest_info = new ArrayList();
    contest_info.add(ci);

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
    return cvr;
  }*/

}
