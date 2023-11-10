package us.freeandfair.corla.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParseException;
import org.testng.annotations.*;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.Setup;
import us.freeandfair.corla.util.IRVParsingException;

import java.io.FileReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static us.freeandfair.corla.endpoint.ACVRUpload.buildNewAcvr;


@Test(groups = {"integration"})
public class ACVRUploadTest {

  private ACVRUploadTest() {}


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
  private void createIRVContest(String county, String contest, Map<String,Integer> candidate_votes, int total){
    County cty = CountyQueries.fromString(county);

    // To do: need to add ballot manifest data.
    BallotManifestInfo bmi = new BallotManifestInfo(cty.id(), 1, "1",
            total, "Bin 1", 1L, (long) total);

    Persistence.persist(bmi);

    Set<String> candidates = candidate_votes.keySet();
    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());
    Contest c1 = new Contest(contest, cty, "IRV", choices, 10,
            1, 0);

    Persistence.persist(c1);
    CountyContestResult ctr = new CountyContestResult(cty, c1);

    ctr.updateResults();
    Persistence.persist(ctr);
  }

  /**
   * Creates and persists an example Plurality election for the Boulder county called
   * Board of Parks.
   */
  private void createBoulderChessBoard(){
    createIRVContest("Boulder", "BoulderChessBoard",
            Map.of("Alice", 20, "Bob", 10, "Chuan", 100, "Diego", 40,
            "Alice(1)", 3, "Alice(2)", 4, "Bob(2)", 3,
            "Chuan(3)", 3, "Chuan(4)",0, "Diego(4)", 3), 186);
  }

  /**
   * Creates and persists an example Plurality election for the Broomfield county called
   * Board of Parks.
   */
  private void createBroomfieldChessBoard(){
    createIRVContest("Broomfield", "BroomfieldChessBoard",
            Map.of("Alice", 20, "Bob", 10, "Chuan", 100, "Diego", 40,
            "Alice(1)", 3, "Alice(2)", 4, "Bob(2)", 3,
            "Chuan(3)", 3, "Chuan(4)",0, "Diego(4)", 3), 186);
  }

  /**
   * Test of estimate sample sizes endpoint logic for
   * Plurality contests (one county specific contest in two counties).
   */
  @Test()
  public void testIRVACVRInterpretation() throws IRVParsingException {
      createBoulderChessBoard();
      createBroomfieldChessBoard();

      Contest boulderTestContest = ContestQueries.matching("BoulderChessBoard").get(0);
      Contest broomfieldTestContest = ContestQueries.matching("BroomfieldChessBoard").get(0);

      List <CVRContestInfo> contestInfoList = List.of(
              new CVRContestInfo(boulderTestContest, "", CVRContestInfo.ConsensusValue.YES,
                      List.of("Alice(1)", "Bob(2)", "Chuan(3)", "Diego(4)")),
              new CVRContestInfo(broomfieldTestContest, "", CVRContestInfo.ConsensusValue.YES,
                      List.of("Alice(1)", "Alice(2)", "Bob(2)", "Chuan(4)", "Diego(4)"))
              );

      CastVoteRecord testCVR1 = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED,
              Instant.MIN, 1L, 1, 1, 1, "Batch 1",
              1, "1-1-1", "Audit", contestInfoList );

      SubmittedAuditCVR testSubmittedCVR1 = new SubmittedAuditCVR(1L, testCVR1);

      County testCounty = new County("c1", 1L);
      CountyDashboard cdb = new CountyDashboard(testCounty);
      cdb.startRound(2, 2, 0 , List.of(1L), List.of(1L));

      CastVoteRecord newACVR  = buildNewAcvr( testSubmittedCVR1, testCVR1, cdb);
      assertEquals(newACVR.contestInfo().get(0).choices(), List.of("Alice", "Bob", "Chuan", "Diego"));
      assertEquals(newACVR.contestInfo().get(1).choices(), List.of("Alice", "Bob"));
  }

  /**
   * Creates a cast vote record in a given Plurality contest containing a vote
   * for the given candidate (name).
   *
   * @param name      Name of candidate being voted for in this CVR.
   * @param co        Contest
   * @param position  CVR position (used when creating CVR objects)
   * @return A CastVoteRecord object containing a vote for the given candidate (name).
   */
  private CastVoteRecord createVoteFor(final String name, final Contest co, Integer position){
    // Create CVRContestInfo
    List<String> votes = new ArrayList<>();
    votes.add(name);

    CVRContestInfo ci = new CVRContestInfo(co, null,null, votes);
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

  /*@Test()
  public void testJsonSubmission1(){
      final String json_string = "{\"auditBoardIndex\":0,\"audit_cvr\":{\"ballot_type\":\"Ballot 1 - Type 1\",\"batch_id\":\"9\",\"contest_info\":[{\"choices\":[\"JOHNSTON Eoin(1)\",\"MCCARTHY Steve(2)\",\"WILLIAMS Keith(3)\",\"JOHNSON Jeff(4)\",\"CADWALLADER Sharon(5)\"],\"comment\":\"\",\"consensus\":\"YES\",\"contest\":\"572\"}],\"county_id\":1,\"cvr_number\":637,\"id\":1210,\"imprinted_id\":\"1-9-13\",\"record_id\":13,\"record_type\":\"UPLOADED\",\"scanner_id\":1,\"timestamp\":\"2023-11-09T23:30:51.136Z\"},\"cvr_id\":1210}";
      try{
          final SubmittedAuditCVR submission =
                  Main.GSON.fromJson(json_string, SubmittedAuditCVR.class);
          System.out.println(submission.toString());
      }
      catch(JsonParseException e){
          System.out.println(e.getMessage());
      }
  }*/
}
