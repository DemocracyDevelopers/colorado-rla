package us.freeandfair.corla.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.poi.util.ArrayUtil;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.IntegerListConverter;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.Setup;


import java.io.FileReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;



import static org.junit.Assert.assertEquals;



public class EstimateSampleSizesTest {

  private EstimateSampleSizesTest() {};


  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

    // Create DoSDashboard with some audit information.
    DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    dosdb.updateAuditInfo(new AuditInfo("general", Instant.now(), Instant.now(),
            "12856782643571354365", BigDecimal.valueOf(0.05)));
    Persistence.saveOrUpdate(dosdb);
    Persistence.flushAndClear();
  }

  @AfterTest()
  public void tearDown() {
    try {
      Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  @Test()
  public void testEstimateSampleSizesSimplePlurality() {
    // For testing sample size estimation endpoint, we need a series of CountyContestResult's
    // in the database, their associated Counties and Contests, a DoSDashboard with audit info.

    County cty = CountyQueries.fromString("Boulder");

    // To do: need to add ballot manifest data.
    BallotManifestInfo bmi = new BallotManifestInfo(cty.id(), 1, "1",
            170, "Bin 1", 0L, 169L);

    Persistence.save(bmi);
    Persistence.flushAndClear();

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Board of Parks", cty, "PLURALITY", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);
    Persistence.flushAndClear();

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
    Persistence.flushAndClear();

    List<CountyContestResult> contestResults = Persistence.getAll(CountyContestResult.class);

    assertEquals(contestResults.size(), 1);

    EstimateSampleSizes esr = new EstimateSampleSizes();
    Map<String,Integer> samples = esr.estimateSampleSizes();
    Map<String,Integer> expected = Map.of("Board of Parks", 18);

    assertEquals(samples, expected);
  }

  private CastVoteRecord createVoteFor(final String name, final Contest co, final County cty, Integer position){
    // Create CVRContestInfo
    List<String> votes = new ArrayList<String>();
    votes.add(name);

    CVRContestInfo ci = new CVRContestInfo(co, null,null, votes);
    List<CVRContestInfo> contest_info = new ArrayList<CVRContestInfo>();
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
    Persistence.flushAndClear();
    return cvr;
  }

  @Test()
  public void testEstimateSampleSizesIRVMayorals() {
    ClassLoader classLoader = getClass().getClassLoader();
    try {
      FileReader file = new FileReader(classLoader.getResource("assertions/irv_estimation_test_case1.json").getFile());
      JsonElement json = Main.GSON.fromJson(file, JsonElement.class);
      JsonObject jobj = json.getAsJsonObject();

      JsonArray contests = (JsonArray) jobj.get("contests");
      long cty_cntr = 5000;
      int seq_cntr = 0;
      for(JsonElement element : contests){
        JsonObject cobject = element.getAsJsonObject();
        String name = cobject.get("name").getAsString();
        String county = cobject.get("county").getAsString();
        int universe = cobject.get("universe").getAsInt();
        JsonArray assertions = cobject.get("assertions").getAsJsonArray();
        JsonArray candidates = cobject.get("candidates").getAsJsonArray();

        List<Choice> choices = new ArrayList<>();
        for(JsonElement c : candidates){
          choices.add(new Choice(c.getAsString(), "", false,
                  false));
        }

        County cty = new County(county, cty_cntr);
        Contest co = new Contest(name, cty, ContestType.IRV.toString(), choices, 1,
                1, seq_cntr);
        CountyContestResult ctr = new CountyContestResult(cty, co);

        Persistence.saveOrUpdate(cty);
        Persistence.save(co);
        Persistence.save(ctr);
        Persistence.flushAndClear();

        loadAssertions(assertions, name, choices, universe);

        ++cty_cntr;
        ++seq_cntr;
      }

      EstimateSampleSizes esr = new EstimateSampleSizes();
      Map<String, Integer> samples = esr.estimateSampleSizes();

      for (Map.Entry<String, Integer> entry : samples.entrySet()) {
        System.out.println(entry.getKey() + ": " + entry.getValue());
      }

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  private void loadAssertions(JsonArray assertions, String contest, List<Choice> choices, int universe){
    for(JsonElement a : assertions){
      JsonObject o = a.getAsJsonObject();

      JsonObject atype = o.get("assertion").getAsJsonObject();
      boolean isnen = atype.get("type").getAsString().equals("NEN");
      int winner = atype.get("winner").getAsInt();
      int loser = atype.get("loser").getAsInt();

      int margin = o.get("margin").getAsInt();
      double difficulty = o.get("difficulty").getAsDouble();

      List<String> continuing = new ArrayList<>();
      if(isnen){
        for(JsonElement c : atype.get("continuing").getAsJsonArray()){
          continuing.add(choices.get(c.getAsInt()).name());
        }
      }

      if(isnen){
        NENAssertion nen = new NENAssertion(contest, choices.get(winner).name(), choices.get(loser).name(),
                margin, universe, difficulty, continuing);
        Persistence.saveOrUpdate(nen);
        Persistence.flushAndClear();
      }
      else{
        NEBAssertion neb = new NEBAssertion(contest, choices.get(winner).name(), choices.get(loser).name(),
                margin, universe, difficulty);
        Persistence.saveOrUpdate(neb);
        Persistence.flushAndClear();
      }
    }
  }
}
