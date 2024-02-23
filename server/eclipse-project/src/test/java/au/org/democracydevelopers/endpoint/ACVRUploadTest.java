package au.org.democracydevelopers.endpoint;

import org.testng.annotations.*;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import au.org.democracydevelopers.query.ContestQueries;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.Setup;
import au.org.democracydevelopers.util.IRVParsingException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


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
   * Create an IRV contest for the given county, contest name, and candidates.
   *
   * @param county            County to which the contest belongs (name).
   * @param contest           Name of the contest.
   * @param candidates        Candidates in the contest.
   * @param total             Total number of CVRs with the contest on it.
   */
  private void createIRVContest(String county, String contest, List<String> candidates, int total){
    County cty = CountyQueries.fromString(county);

    // To do: need to add ballot manifest data.
    BallotManifestInfo bmi = new BallotManifestInfo(cty.id(), 1, "1",
            total, "Bin 1", 1L, (long) total);

    Persistence.persist(bmi);

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());
    Contest c1 = new Contest(contest, cty, "IRV", choices, 10,
            1, 0);
    Persistence.persist(c1);

    CountyContestResult ctr = new CountyContestResult(cty, c1);
    Persistence.persist(ctr);
  }

  /**
   * Creates and persists an example IRV election for the Boulder Chess Board.
   */
  private void createBoulderChessBoard(){
    createIRVContest("Boulder", "BoulderChessBoard", Arrays.asList("Alice",
                    "Bob", "Chuan", "Diego"), 186);
  }

  /**
   * Creates and persists an example IRV election for the Broomfield Chess Board.
   */
  private void createBroomfieldChessBoard(){
      createIRVContest("Broomfield", "BroomfieldChessBoard", Arrays.asList("Alice",
              "Bob", "Chuan", "Diego"), 186);
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


      final String cinfo1 = "{\"auditBoardIndex\":0,\"audit_cvr\":{\"ballot_type\":\"Ballot 1 - Type 1\"," +
              "\"batch_id\":\"9\",\"contest_info\":[{\"choices\":[\"Alice(1)\",\"Bob(2)\",\"Chuan(3)\",\"Diego(4)\"]," +
              "\"comment\":\"\",\"consensus\":\"YES\",\"contest\":\"" + boulderTestContest.id() + "\"}],\"county_id\":" +
              boulderTestContest.county().id() + ",\"cvr_number\":1,\"id\":1,\"imprinted_id\":\"1-1-1\"," +
              "\"record_id\":1,\"record_type\":\"UPLOADED\",\"scanner_id\":1,\"timestamp\":\"2023-11-09T23:30:51.136Z\"}," +
              "\"cvr_id\":1}";


      final SubmittedAuditCVR submission1 = Main.GSON.fromJson(cinfo1, SubmittedAuditCVR.class);

      assertEquals(submission1.auditCVR().contestInfo().get(0).choices(), List.of("Alice", "Bob", "Chuan", "Diego"));

      final String cinfo2 = "{\"auditBoardIndex\":0,\"audit_cvr\":{\"ballot_type\":\"Ballot 1 - Type 1\"," +
              "\"batch_id\":\"9\",\"contest_info\":[{\"choices\":[\"Alice(1)\",\"Alice(2)\",\"Bob(2)\",\"Chuan(3)\",\"Diego(4)\"]," +
              "\"comment\":\"\",\"consensus\":\"YES\",\"contest\":\"" + broomfieldTestContest.id() + "\"}],\"county_id\":" +
              broomfieldTestContest.county().id() + ",\"cvr_number\":1,\"id\":1,\"imprinted_id\":\"1-1-1\"," +
              "\"record_id\":1,\"record_type\":\"UPLOADED\",\"scanner_id\":1,\"timestamp\":\"2023-11-09T23:30:51.136Z\"}," +
              "\"cvr_id\":1}";
      final SubmittedAuditCVR submission2 = Main.GSON.fromJson(cinfo2, SubmittedAuditCVR.class);
      assertEquals(submission2.auditCVR().contestInfo().get(0).choices(), List.of("Alice", "Bob", "Chuan", "Diego"));
  }

}
