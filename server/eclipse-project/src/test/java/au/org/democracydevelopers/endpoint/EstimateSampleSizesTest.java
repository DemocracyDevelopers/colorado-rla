package au.org.democracydevelopers.endpoint;

import au.org.democracydevelopers.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.model.NEBAssertion;
import au.org.democracydevelopers.model.NENAssertion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.testng.annotations.*;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.Setup;

import java.io.FileReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Test(groups = {"integration"})
public class EstimateSampleSizesTest {

  private EstimateSampleSizesTest() {}


  @BeforeMethod
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

    // Create DoSDashboard with some audit information.
    DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    dosdb.updateAuditInfo(new AuditInfo("general", Instant.now(), Instant.now(),
            "12856782643571354365", BigDecimal.valueOf(0.05)));
    Persistence.persist(dosdb);
  }

  @AfterMethod
  public void tearDown() {
    try {
      Persistence.rollbackTransaction();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Create a Plurality contest for the given county, contest name, and with the given
   * distribution of votes.
   *
   * @param county            County to which the contest belongs (name).
   * @param contest           Name of the contest.
   * @param candidate_votes   Map between candidate name and their vote total.
   * @param total             Total number of CVRs with the contest on it.
   */
  private void createPluralityContest(String county, String contest, Map<String,Integer> candidate_votes, int total){
    County cty = CountyQueries.fromString(county);

    // To do: need to add ballot manifest data.
    BallotManifestInfo bmi = new BallotManifestInfo(cty.id(), 1, "1",
            total, "Bin 1", 1L, (long) total);

    Persistence.persist(bmi);

    Set<String> candidates = candidate_votes.keySet();
    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());
    Contest c1 = new Contest(contest, cty, "PLURALITY", choices, 1,
            1, 0);

    Persistence.persist(c1);
    CountyContestResult ctr = new CountyContestResult(cty, c1);

    int cntr = 0;
    for(Map.Entry<String,Integer> entry : candidate_votes.entrySet()){
      for(int i = 0; i < entry.getValue(); ++i){
        ctr.addCVR(createVoteFor(List.of(entry.getKey()), c1, cntr));
        ++cntr;
      }
    }

    ctr.updateResults();
    Persistence.persist(ctr);
  }

  /**
   * Creates and persists an example Plurality election for the Boulder county called
   * Board of Parks.
   */
  private void createBoulderBoardOfParks(){
    createPluralityContest("Boulder", "Board of Parks", Map.of("Alice", 20, "Bob", 10,
            "Chuan", 100, "Diego", 40), 170);
  }

  /**
   * Creates and persists an example Plurality election for the Broomfield county called
   * Board of Parks.
   */
  private void createBroomfieldBoardOfParks(){
    createPluralityContest("Broomfield", "Board of Parks", Map.of("Alice", 150, "Bob", 55,
            "Chuan", 310, "Diego", 440), 955);
  }

  /**
   * Creates and persists an example Plurality election for the Boulder county called
   * Board of Museums.
   */
  private void createBoulderBoardOfMuseums(){
    createPluralityContest("Boulder", "Board of Museums", Map.of("Alice", 45, "Bob", 5,
            "Chuan", 120, "Diego", 200), 370);
  }

  /**
   * Creates and persists an example Plurality election for the Broomfield county called
   * Board of Transport.
   */
  private void createBroomfieldBoardOfTransport(){
    createPluralityContest("Broomfield", "Board of Transport", Map.of("Wendy", 150, "Kara", 130,
            "Raoul", 350, "Chao", 320), 950);
  }

  /**
   * Test of estimate sample sizes endpoint logic for
   * Plurality contests (one county specific contest in two counties).
   */
  @Test()
  public void testEstimateSampleSizesSimplePlurality() {
    try {
      createBoulderBoardOfParks();
      createBroomfieldBoardOfTransport();

      Map<String,String[]> samples = computeSampleSizes();
      assertEquals(2, samples.size());

      // Check that sample estimates have been created for both our contests.
      assertEquals(new HashSet<>(Arrays.asList("Board of Parks", "Board of Transport")), samples.keySet());

      checkSampleEstimateRow(samples.get("Board of Parks"), "Board of Parks", "Boulder",
              "PLURALITY", "170", 0.3529, 0.35295, "18");

      checkSampleEstimateRow(samples.get("Board of Transport"), "Board of Transport", "Broomfield",
              "PLURALITY", "950", 0.03156, 0.03158, "198");

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Test of estimate sample sizes endpoint logic for the case where a
   * county has two Plurality Contests.
   */
  @Test()
  public void testEstimateSampleSizesTwoPluralityPerCounty() {
    try {
      createBoulderBoardOfParks();
      createBoulderBoardOfMuseums();

      Map<String,String[]> samples = computeSampleSizes();
      assertEquals(2, samples.size());

      // Check that sample estimates have been created for both our contests.
      assertEquals(new HashSet<>(Arrays.asList("Board of Parks", "Board of Museums")), samples.keySet());

      checkSampleEstimateRow(samples.get("Board of Parks"), "Board of Parks", "Boulder",
              "PLURALITY", "170", 0.162161, 0.162163, "39");

      checkSampleEstimateRow(samples.get("Board of Museums"), "Board of Museums", "Boulder",
              "PLURALITY", "370", 0.216215, 0.216217, "29");

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Test of estimate sample sizes endpoint logic for the case where a
   * contest runs across multiple counties.
   */
  @Test()
  public void testEstimateSampleSizesContestAcrossCounties() {
    try {
      createBoulderBoardOfParks();
      createBroomfieldBoardOfParks();

      Map<String,String[]> samples = computeSampleSizes();
      assertEquals(1, samples.size());

      // Check that sample estimates have been created for both our contests.
      assertEquals(new HashSet<>(List.of("Board of Parks")), samples.keySet());

      checkSampleEstimateRow(samples.get("Board of Parks"), "Board of Parks", "Multiple",
              "PLURALITY", "1125", 0.0622221, 0.06222223, "101");

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Demonstration test of estimate sample size endpoint logic for a series of
   * IRV contests (33 Local government Mayoral elections in NSW 2021).
   */
  @Test()
  public void testEstimateSampleSizesIRVMayorals() {
    try {
      loadIRVContestConfiguration("assertions/irv_estimation_test_case1.json");

      Map<String,String[]> samples = computeSampleSizes();
      assertEquals(3, samples.size());

      assertEquals(new HashSet<>(Arrays.asList("2021 NSW Local Government election for Ballina Mayoral",
              "2021 NSW Local Government election for Bellingen Mayoral",
              "2021 NSW Local Government election for Burwood Mayoral")), samples.keySet());

      checkNSWIRVContests(samples);

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }



  /**
   * Demonstration test of estimate sample size endpoint logic for a series of
   * IRV AND Plurality contests.
   */
  @Test()
  public void testEstimateSampleSizesIRVMayoralsAndPlurality() {
    try {
      loadIRVContestConfiguration("assertions/irv_estimation_test_case1.json");
      createBroomfieldBoardOfTransport();
      createBoulderBoardOfParks();

      Map<String,String[]> samples = computeSampleSizes();
      assertEquals(5, samples.size());

      assertEquals(new HashSet<>(Arrays.asList("Board of Transport",
              "Board of Parks",
              "2021 NSW Local Government election for Ballina Mayoral",
              "2021 NSW Local Government election for Bellingen Mayoral",
              "2021 NSW Local Government election for Burwood Mayoral")), samples.keySet());

      checkSampleEstimateRow(samples.get("Board of Parks"), "Board of Parks", "Boulder",
              "PLURALITY", "170", 0.3529, 0.35295, "18");

      checkSampleEstimateRow(samples.get("Board of Transport"), "Board of Transport", "Broomfield",
              "PLURALITY", "950", 0.03156, 0.03158, "198");

      checkNSWIRVContests(samples);

    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Check the data points generated through sample size estimation for the 33 NSW Local Government
   * Mayoral elections.
   * @param samples   Map between contest name and data point row (formed by sample size estimation logic)
   *                  for that contest.
   */
  private void checkNSWIRVContests(final Map<String,String[]> samples){
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Ballina Mayoral"),
            "2021 NSW Local Government election for Ballina Mayoral", "Ballina",
            "IRV", "27853", 0.13261, 0.13263, "47");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Bellingen Mayoral"),
            "2021 NSW Local Government election for Bellingen Mayoral", "Bellingen",
            "IRV", "8609", 0.29177, 0.291788, "22");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Burwood Mayoral"),
            "2021 NSW Local Government election for Burwood Mayoral", "Burwood",
            "IRV", "18232", 0.38628, 0.386299, "17");
    /*checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Byron Mayoral"),
            "2021 NSW Local Government election for Byron Mayoral", "Byron",
            "IRV", "18732", 0.05657, 0.056588, "111");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Canada Bay Mayoral"),
            "2021 NSW Local Government election for Canada Bay Mayoral", "Canada Bay",
            "IRV", "49682", 0.12001, 0.12003, "52");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Broken Hill Mayoral"),
            "2021 NSW Local Government election for City of Broken Hill Mayoral", "City of Broken Hill",
            "IRV", "11084", 0.29762, 0.297637, "21");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Cessnock Mayoral"),
            "2021 NSW Local Government election for City of Cessnock Mayoral", "City of Cessnock",
            "IRV", "37944", 0.1487454, 0.1487456, "42");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Coffs Harbour Mayoral"),
            "2021 NSW Local Government election for City of Coffs Harbour Mayoral", "City of Coffs Harbour",
            "IRV", "46929", 0.112253, 0.112255, "56");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Griffith Mayoral"),
            "2021 NSW Local Government election for City of Griffith Mayoral", "City of Griffith",
            "IRV", "14804", 0.243581, 0.243583, "26");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Lake Macquarie Mayoral"),
            "2021 NSW Local Government election for City of Lake Macquarie Mayoral", "City of Lake Macquarie",
            "IRV", "135601", 0.30891, 0.308922, "21");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Lismore Mayoral"),
            "2021 NSW Local Government election for City of Lismore Mayoral", "City of Lismore",
            "IRV", "27044", 0.334121, 0.334123, "19");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Liverpool Mayoral"),
            "2021 NSW Local Government election for City of Liverpool Mayoral", "City of Liverpool",
            "IRV", "120656", 0.021017, 0.0210185, "297");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Maitland Mayoral"),
            "2021 NSW Local Government election for City of Maitland Mayoral", "City of Maitland",
            "IRV", "56056", 0.020532, 0.0205331, "304");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Newcastle Mayoral"),
            "2021 NSW Local Government election for City of Newcastle Mayoral", "City of Newcastle",
            "IRV", "103043", 0.1645622, 0.1645624, "38");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Orange Mayoral"),
            "2021 NSW Local Government election for City of Orange Mayoral", "City of Orange",
            "IRV", "25409", 0.0191663, 0.0191665, "325");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Shellharbour Mayoral"),
            "2021 NSW Local Government election for City of Shellharbour Mayoral", "City of Shellharbour",
            "IRV", "47759", 0.054334, 0.0543354, "115");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Shoalhaven Mayoral"),
            "2021 NSW Local Government election for City of Shoalhaven Mayoral", "City of Shoalhaven",
            "IRV", "70130", 0.0230143, 0.0230145, "271");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Sydney Mayoral"),
            "2021 NSW Local Government election for City of Sydney Mayoral", "City of Sydney",
            "IRV", "120186", 0.2674187, 0.2674189, "24");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Willoughby Mayoral"),
            "2021 NSW Local Government election for City of Willoughby Mayoral", "City of Willoughby",
            "IRV", "39453", 0.0641521, 0.0641523, "98");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for City of Wollongong Mayoral"),
            "2021 NSW Local Government election for City of Wollongong Mayoral", "City of Wollongong",
            "IRV", "131740", 0.020235, 0.0202369, "308");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Eurobodalla Mayoral"),
            "2021 NSW Local Government election for Eurobodalla Mayoral", "Eurobodalla",
            "IRV", "26604", 0.0415725, 0.0415727, "150");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Hornsby Mayoral"),
            "2021 NSW Local Government election for Hornsby Mayoral", "Hornsby",
            "IRV", "87792", 0.1420857, 0.1420859, "44");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Hunter's Hill Mayoral"),
            "2021 NSW Local Government election for Hunter's Hill Mayoral", "Hunter's Hill",
            "IRV", "8572", 0.0254315, 0.0254317, "245");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Kempsey Mayoral"),
            "2021 NSW Local Government election for Kempsey Mayoral", "Kempsey",
            "IRV", "18471", 0.0209516, 0.0209518, "298");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Mosman Mayoral"),
            "2021 NSW Local Government election for Mosman Mayoral", "Mosman",
            "IRV", "16926", 0.215702, 0.215704, "29");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Nambucca Mayoral"),
            "2021 NSW Local Government election for Nambucca Mayoral", "Nambucca",
            "IRV", "12816", 0.3559611, 0.3559613, "18");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Port Macquarie-Hastings Mayoral"),
            "2021 NSW Local Government election for Port Macquarie-Hastings Mayoral", "Port Macquarie-Hastings",
            "IRV", "56827", 0.336212, 0.336214, "19");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Port Stephens Mayoral"),
            "2021 NSW Local Government election for Port Stephens Mayoral", "Port Stephens",
            "IRV", "49528", 0.011447, 0.0114481, "544");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Richmond Mayoral"),
            "2021 NSW Local Government election for Richmond Mayoral", "Richmond",
            "IRV", "14064", 0.413892, 0.413894, "16");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Singleton Mayoral"),
            "2021 NSW Local Government election for Singleton Mayoral", "Singleton",
            "IRV", "14337", 0.0791656, 0.0791658, "79");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for The Hills Shire Mayoral"),
            "2021 NSW Local Government election for The Hills Shire Mayoral", "The Hills Shire",
            "IRV", "108399", 0.264171, 0.264173, "24");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Uralla Mayoral"),
            "2021 NSW Local Government election for Uralla Mayoral", "Uralla",
            "IRV", "3906", 0.593957, 0.5939581, "11");
    checkSampleEstimateRow(samples.get("2021 NSW Local Government election for Wollondilly Mayoral"),
            "2021 NSW Local Government election for Wollondilly Mayoral", "Wollondilly",
            "IRV", "33005", 0.0389333, 0.0389335, "160");*/
  }

  /**
   * Creates a cast vote record in a given Plurality contest containing a vote
   * for the given candidate (name).
   *
   * @param vote      List of names representing ranked choices (most preferred to least)
   * @param co        Contest
   * @param position  CVR position (used when creating CVR objects)
   * @return A CastVoteRecord object containing a vote for the given candidate (name).
   */
  private CastVoteRecord createVoteFor(final List<String> vote, final Contest co, Integer position){
    // Create CVRContestInfo
    CVRContestInfo ci = new CVRContestInfo(co, null,null, vote);
    List<CVRContestInfo> contest_info = new ArrayList<>();
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
    Persistence.persist(cvr);
    return cvr;
  }


  /**
   * Computes sample sizes for all contests in the database. Returns
   * these samples sizes, and prints them to stdout.
   */
  private Map<String,String[]>  computeSampleSizes(){
    // Call core logic of the EstimateSampleSizes endpoint.
    EstimateSampleSizes esr = new EstimateSampleSizes();

    // Get a map between contest name and the initial sample size expected
    // for that contest.
    List<String[]> samples = esr.estimateSampleSizes();

    // Build map between contest name and samples row (so we can check that each
    // data point is correct for that contest). In doing so, check that each
    // row has 6 data points.
    Map<String,String[]> contest2row = new HashMap<>();
    for (String[] s: samples){
      assertEquals(6, s.length);
      contest2row.put(s[1], s);
    }

    return contest2row;
  }

  /**
   * Compare row of sample size estimate data returned by EstimateSampleSizes::estimateSampleSizes()
   * against expected values.
   * @param row           Sample size estimate data points for a single contest.
   * @param contestName   Name of contest.
   * @param countyName    Name of county.
   * @param contestType   Type of contest.
   * @param ballots       Ballots in the auditing universe of the contest.
   * @param lb_dmargin    Lower bound on diluted margin for the contest.
   * @param ub_dmargin    Upper bound on diluted margin for the contest.
   * @param estimate      Sample size estimate for the contest.
   */
  private void checkSampleEstimateRow(final String[] row, final String contestName, final String countyName,
                                      final String contestType, final String ballots, final double lb_dmargin,
                                      final double ub_dmargin, final String estimate){
    assertEquals(countyName, row[0]);
    assertEquals(contestName, row[1]);
    assertEquals(contestType, row[2]);
    assertEquals(ballots, row[3]);
    assertEquals(estimate, row[5]);
    double dilutedMargin = Double.parseDouble(row[4]);
    assertTrue(lb_dmargin <= dilutedMargin && dilutedMargin <= ub_dmargin);
  }

  /**
   * Given a JSON configuration file defining a series of IRV contests with
   * assertions, create and persist County, Contest, CountyContestResult,
   * and Assertion objects.
   *
   * @param config_file   JSON configuration file defining a series of IRV contests.
   */
  private void loadIRVContestConfiguration(String config_file){
    ClassLoader classLoader = getClass().getClassLoader();
    try {
      // Grab test configuration: a series of IRV contests with attached assertions
      // in JSON format.
      FileReader file = new FileReader(classLoader.getResource(config_file).getFile());
      JsonElement json = Main.GSON.fromJson(file, JsonElement.class);
      JsonObject jobj = json.getAsJsonObject();

      // Parse each contest in the configuration file.
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

        // Parse candidates: form a list of Choice
        List<Choice> choices = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for(JsonElement c : candidates){
          choices.add(new Choice(c.getAsString(), "", false,
                  false));
          names.add(c.getAsString());
        }

        // Create, and persist, the County, Contest, and CountyContestResult
        County cty = new County(county, cty_cntr);
        Contest co = new Contest(name, cty, ContestType.IRV.toString(), choices, 1,
                1, seq_cntr);
        CountyContestResult ctr = new CountyContestResult(cty, co);

        // Add CVRs to contest (needed to establish contest ballot count in CountyContestResult).
        // It doesn't matter what the content of the vote is for the purposes of these tests.
        for (int i = 0; i < universe; ++i){
          ctr.addCVR(createVoteFor(names, co, i));
        }

        BallotManifestInfo bmi = new BallotManifestInfo(cty.id(), 1, "1",
                universe, "Bin 1", 1L, (long) universe);

        Persistence.persist(bmi);
        Persistence.persist(cty);
        Persistence.persist(co);
        Persistence.persist(ctr);

        // Create and persist the assertions for the contest.
        loadAssertions(assertions, name, choices, universe);

        ++cty_cntr;
        ++seq_cntr;
      }
    } catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Create assertions for the given contest from the JsonArray of assertions provided as input.
   * These assertions will be persisted.
   *
   * @param assertions   A JsonArray containing assertion descriptions in JSON format.
   * @param contest      Name of the contest to which the assertions belong.
   * @param choices      List of candidates in the contest (as a list of Choice).
   * @param universe     Number of ballots in the universe to which this contest belongs.
   */
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
        NENAssertion nen = new NENAssertion(contest, choices.get(winner).name(),
                choices.get(loser).name(), margin, universe, difficulty, continuing);
        Persistence.persist(nen);
      }
      else{
        NEBAssertion neb = new NEBAssertion(contest, choices.get(winner).name(),
                choices.get(loser).name(), margin, universe, difficulty);
        Persistence.persist(neb);
      }
    }
  }
}
