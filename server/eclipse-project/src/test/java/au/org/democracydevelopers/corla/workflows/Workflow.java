/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire
service (https://github.com/DemocracyDevelopers/raire-service), in order to
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.workflows;

import static io.restassured.RestAssured.given;
import static java.util.Collections.min;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.Main.main;
import static us.freeandfair.corla.auth.AuthenticationInterface.USERNAME;
import static us.freeandfair.corla.auth.AuthenticationStage.SECOND_FACTOR_AUTHENTICATED;
import static us.freeandfair.corla.auth.AuthenticationStage.TRADITIONALLY_AUTHENTICATED;
import static us.freeandfair.corla.model.AuditType.COMPARISON;

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.assertion.Assertion;
import au.org.democracydevelopers.corla.model.vote.IRVBallotInterpretation;
import au.org.democracydevelopers.corla.query.AssertionQueries;
import au.org.democracydevelopers.corla.util.DoubleComparator;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import au.org.democracydevelopers.corla.util.TestOnlyQueries;
import au.org.democracydevelopers.corla.workflows.Instance.ReAuditDetails;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.BeforeClass;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.ContestResultQueries;
import wiremock.net.minidev.json.JSONArray;
import wiremock.net.minidev.json.JSONObject;

/**
 * Base class for an API test workflow designed to run through a sequence of steps involving
 * a sequence of endpoint accesses.
 */
public abstract class Workflow  {

  /**
   * A collection of data representing an audit session for a county.
   * @param filter        SessionFilter used to keep track of the session.
   * @param auditBoard    Identities of the auditors on the index'th audit board.
   * @param index         Index of the audit board.
   * @param county        County number.
   */
  public record TestAuditSession(SessionFilter filter, List<Map<String,String>> auditBoard,
                                 int index, int county){}

  /**
   * Number of CO counties
   */
  protected static final int numCounties = 64;

  /**
   * Set of all CO counties, by number.
   */
  protected static final Set<Integer> allCounties
      = IntStream.rangeClosed(1,numCounties).boxed().collect(Collectors.toSet());

  /**
   * Default PRNG seed.
   */
  protected static final String defaultSeed = "24098249082409821390482049098";

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Workflow.class);

  /**
   * Path for storing temporary config files
   */
  protected static final String tempConfigPath = "src/test/resources/workflows/temp/";

  /**
   * Path for all the data files.
   */
  protected static final String dataPath = "src/test/resources/CSVs/";

  /**
   * Default audit board names.
   */
  private final List<Map<String,String>> auditBoard = List.of(
      Map.of("first_name","V", "last_name","T",
          "political_party","Unaffiliated"),
      Map.of("first_name","M", "last_name","B",
          "political_party","Unaffiliated")
  );

  /**
   * Strings for colorado-rla JSON structures.
   */
  protected static final String ASM_STATE = "asm_state";
  protected static final String AUDIT = "audit";
  protected static final String AUDIT_BOARD_ASM_STATE = "audit_board_asm_state";
  protected static final String AUDIT_INFO = "audit_info";
  protected static final String CONTEST = "contest";
  protected static final String CONTESTID = "contestId";
  protected static final String CANONICAL_CHOICES = "canonicalChoices";
  protected static final String CANONICAL_CONTESTS = "canonicalContests";
  protected static final String COUNTY_STATUS = "county_status";
  protected static final String COUNTYID = "countyId";
  protected static final String CHOICES = "choices";
  protected static final String OLDNAME = "oldName";
  protected static final String NEWNAME = "newName";
  protected static final String CVR_FILETYPE = "cvr-export";
  protected static final String CVR_JSON = "cvr_export_file";
  protected static final String ESTIMATED_BALLOTS = "estimated_ballots_to_audit";
  protected static final String OPTIMISTIC_BALLOTS = "optimistic_ballots_to_audit";
  protected static final String ELECTION_DATE = "election_date";
  protected static final String ELECTION_TYPE = "election_type";
  protected static final String FILETYPE = "fileType";
  protected static final String ID = "id";
  protected static final String IGNORE_MANIFESTS = "ignoreManifests";
  protected static final String MANIFEST_FILETYPE = "ballot-manifest";
  protected static final String MANIFEST_JSON = "ballot_manifest_file";
  protected static final String NAME = "name";
  protected static final String PUBLIC_MEETING_DATE = "public_meeting_date";
  protected static final String REASON = "reason";
  protected static final String RISK_LIMIT_JSON = "risk_limit";
  protected static final String RISK_LIMIT_ACHIEVED = "risk_limit_achieved";
  protected static final String SEED = "seed";
  protected static final String STATUS = "status";
  protected static final String BALLOTS_REMAINING = "ballots_remaining_in_round";
  protected static final String DISCREPANCY_COUNT = "discrepancy_count";
  protected static final String ONE_OVER_COUNT = "one_over_count";
  protected static final String ONE_UNDER_COUNT = "one_under_count";
  protected static final String TWO_OVER_COUNT = "two_over_count";
  protected static final String TWO_UNDER_COUNT = "two_under_count";
  protected static final String OTHER_COUNT = "other_count";
  protected static final String DISAGREEMENTS = "disagreements";
  protected static final String ONE_OVER = "1";
  protected static final String ONE_UNDER = "-1";
  protected static final String OTHER = "0";
  protected static final String TWO_OVER = "2";
  protected static final String TWO_UNDER = "-2";
  protected static final String BALLOT_TYPE = "ballot_type";
  protected static final String BATCH_ID = "batch_id";
  protected static final String COUNTY_ID = "county_id";
  protected static final String CVR_NUMBER = "cvr_number";
  protected static final String IMPRINTED_ID = "imprinted_id";
  protected static final String RECORD_ID = "record_id";
  protected static final String RECORD_TYPE = "record_type";
  protected static final String SCANNER_ID = "scanner_id";
  protected static final String TIMESTAMP = "timestamp";


  @BeforeClass
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;
  }


  /**
   * Abstract to allow for either a simulated setup in a container, or a setup that makes http calls
   * to raire.
   * @param testName the name of the test.
   * @param postgres the container where the test DB is found.
   *                 FIXME get testName to be the full path of the config file.
   */
  protected abstract void runMainAndInitializeDB(final String testName, final Optional<PostgreSQLContainer<?>> postgres);

  /**
   * Create properties files for use when running main, and then runs main. Main can take (database)
   * properties as a CLI, but only as a file, so we need to make the file and then tell main to read it.
   * @param testFileName the name of the test file - must be different for each test.
   *                     FIXME put this into the workflow runner, together with the temp config path. Possibly it should
   *                     be joined into runMainAndInitializeDB.
   */
  protected static void runMain(final Properties config, final String testFileName) {
    final String propertiesFile = tempConfigPath + testFileName + "-test.properties";
    try {
      FileOutputStream os = new FileOutputStream(propertiesFile);
      final StringWriter sw = new StringWriter();
      config.store(sw, "Ephemeral database config for "+testFileName);
      os.write(sw.toString().getBytes());
      os.close();
    } catch (Exception e) {
      LOGGER.error("Couldn't write " + testFileName + "-test.properties. "+e.getMessage(), e);
    }
    main(propertiesFile);
  }


  /**
   * Transform a simple map of strings into a JSONObject.
   * @param data Data to be transformed into JSONObject.
   * @return The given data as a JSONObject.
   */
  protected JSONObject createBody(final Map<String, Object> data) {
    final JSONObject body = new JSONObject();
    body.putAll(data);
    return body;
  }

  /**
   * Authenticate the given user with the given password/second factor challenge answer.
   *
   * @param filter Session filter to maintain same session across API test.
   * @param user   Username to authenticate
   * @param pwd    Password/second factor challenge answer for user.
   * @param stage  Authentication stage.
   */
  protected void authenticate(final SessionFilter filter, final String user, final String pwd, final int stage) {
    final JSONObject requestParams = (stage == 1) ?
        createBody(Map.of("username", user, "password", pwd)) :
        createBody(Map.of("username", user, "second_factor", pwd));

    final Response response = given().filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toJSONString())
        .post("/auth-admin");

    final String authStatus = response.getBody().jsonPath().getString("stage");

    LOGGER.debug("Auth status for login " + user + " stage " + stage + " is " + authStatus);
    assertEquals(authStatus, (stage == 1) ? TRADITIONALLY_AUTHENTICATED.toString()
        : SECOND_FACTOR_AUTHENTICATED.toString(), "Stage " + stage + " auth failed.");
  }

  /**
   * Unauthenticate the given user.
   *
   * @param filter Session filter to maintain same session across API test.
   * @param user   Username to unauthenticate.
   */
  protected void logout(final SessionFilter filter, final String user) {
    final JSONObject requestParams = createBody(Map.of(USERNAME, user));

    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toJSONString())
        .post("/unauthenticate");
  }

  /**
   * Upload a file and its corresponding hash on behalf of the given county number.
   *
   * @param number   Number of the county uploading the CVR/hash.
   * @param file     Path of the file to be uploaded.
   * @param hashFile Path of the corresponding hash for the CVR file.
   */
  protected void uploadCounty(final int number, final String fileType,
                              final String file, final String hashFile) {
    final String prefix = "[uploadCounty]";

    final String user = "countyadmin" + number;
    final SessionFilter filter = doLogin(user);

    // GET the county dashboard. This is just to test that the login worked.
    given().filter(filter).get("/county-dashboard");

    // The hash has to be sent directly as a string, unlike the CSVs which are sent as files.
    final String hash = readFromFile(hashFile);

    // Post the CVR/manifest file and its hash.
    Response response = given()
        .filter(filter)
        .header("Content-Type", "multipart/form-data")
        .multiPart("file", new File(file), "text/csv")
        .multiPart("hash", hash)
        .post("/upload-file");

    // Request the CVR/manifest to be imported
    given().filter(filter)
        .header("Content-Type", "application/json")
        .body(response.then().extract().asString())
        .post("/import-" + fileType);

    LOGGER.debug(String.format("%s %s %s %s.", prefix, "Successful file upload - ", user, fileType));

    // Logout.
    logout(filter, user);
  }

  /**
   * Sign in the given audit board for a given county.
   * @param session TestAuditSession capturing an audit session for a given county.
   */
  private void auditBoardSignIn(final TestAuditSession session){

    final JSONObject params = createBody(Map.of("audit_board",
        session.auditBoard, "index", session.index()));

      given().filter(session.filter())
          .header("Content-Type", "application/json")
          .body(params.toJSONString())
          .post("/audit-board-sign-in");
      // FIXME This sometimes doesn't work, possibly because of contention.
      //     .then()
      //    .assertThat()
      //    .statusCode(HttpStatus.SC_OK);
  }

  /**
   * The given audit board signs off on their current audit round.
   * @param session TestAuditSession capturing an audit session for a given county.
   *
   */
  private void auditBoardSignOff(final TestAuditSession session){

    final JSONObject params = createBody(Map.of("audit_board",
        session.auditBoard(), "index", session.index()));

    given().filter(session.filter())
        .header("Content-Type", "application/json")
        .body(params.toJSONString())
        .post("/sign-off-audit-round")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Intended for audit boards that have to do nothing other than sign off. Log in, sign off on the
   * (empty) audit, then sign out.
   */
  protected void countyLogInSignOffLogout(final int countyID) {
    final String user = "countyadmin" + countyID;
    final SessionFilter filter = doLogin(user);

    TestAuditSession session = new TestAuditSession(filter, auditBoard, 0, countyID);
    countySignOffLogout(session);
  }
  /**
   * Sign off the current audit round for the given county, and logout of the session.
   * @param session TestAuditSession capturing an audit session for a given county.
   */
  protected void countySignOffLogout(final TestAuditSession session){
    final String user = "countyadmin" + session.county();

    // Sign off audit round
    auditBoardSignOff(session);

    // Logout.
    logout(session.filter(), user);
  }

  /**
   * Given a vote in a specific contest, return the raw choices to be used in creating the
   * ACVR containing the vote.
   * @param countyID ID of the county to which the CVR belongs, as a string.
   * @param round The round (starts from 1).
   * @param cvrNumber CVR number for the CVR containing the given vote.
   * @param info Details of the vote for the relevant contest.
   * @param imprintedId Imprinted identifier of the CVR containing the given vote.
   * @param instance Details of the workflow instance being run.
   * @return The list of original choices on the pre-interpreted CVR with the given imprinted ID.
   */
  private List<String> translateToRawChoices(final String countyID, final int round, final int cvrNumber,
      final CVRContestInfo info, final String imprintedId, final Instance instance){

    // Check if the CVR's imprinted Id is present in the workflow instance
    final Optional<List<String>> choices = instance.getActualChoices(countyID, round, imprintedId,
        info.contest().name());

    if(choices.isPresent()){
      LOGGER.info("Alternative choices specified for CVR " + imprintedId +
          " from county " + countyID + ".");
    }

    return choices.orElseGet(() -> translateToRawChoices(cvrNumber, info, imprintedId));
  }

  /**
   * Given a vote in a specific contest (on a CVR that is being reaudited), return the raw choices
   * to be used in creating the ACVR containing the vote.
   * @param cvrNumber CVR number for the CVR containing the given vote.
   * @param info Details of the vote for the relevant contest.
   * @param imprintedId Imprinted identifier of the CVR containing the given vote.
   * @param entry A mapping between contest name and reaudit details for that contest. Note that
   *              this map may not contain the relevant contest, in which case we know that we
   *              should return the choices as they are defined on the CVR.
   * @return The list of original choices on the pre-interpreted CVR with the given imprinted ID.
   */
  private List<String> translateToRawChoicesReAudit(final int cvrNumber, final CVRContestInfo info,
      final String imprintedId, final Map<String,ReAuditDetails> entry){

    final String contestName = info.contest().name();

    if(entry.containsKey(contestName)){
      return entry.get(contestName).choices();
    }

    return translateToRawChoices(cvrNumber, info, imprintedId);
  }

  /**
   * Given a vote in a specific contest, translate the choices on that vote (if an IRV contest)
   * into the form "Candidate(Rank),...,Candidate(Rank)", returning the simplest pre-interpretation
   * set of choices. If the contest is a Plurality contest, return the choices from info.choices().
   * Otherwise, check whether the vote's original choices were interpreted to remove errors. If so,
   * return the original raw choices from the irv_ballot_interpretation table. Otherwise, add ranks
   * to each choice name and return.
   * @param cvrNumber Number of the CVR containing the given vote.
   * @param info Details of the vote for the relevant contest.
   * @param imprintedId Imprinted identifier of the CVR containing the given vote.
   * @return The list of original choices on the pre-interpreted CVR with the given imprinted ID.
   */
  private List<String> translateToRawChoices(final int cvrNumber, final CVRContestInfo info,
      final String imprintedId){
    final String prefix = "[translateToRawChoices]";

    if(info.contest().description().equals(ContestType.IRV.toString())){
      // The contest is an IRV contest.
      final List<IRVBallotInterpretation> interpretations = TestOnlyQueries.matching(
          cvrNumber, info.contest().id(), imprintedId, RecordType.UPLOADED);

      if(interpretations.isEmpty()){
        List<String> rawChoices = new ArrayList<>();
        for(int i = 0; i < info.choices().size(); ++i){
          rawChoices.add(info.choices().get(i) + "("+ (i+1) + ")");
        }
        return rawChoices;
      }
      else{
        if(interpretations.size() > 1){
          final String msg = prefix + " there are multiple interpretations of the CVR " +
              imprintedId + " with record type UPLOADED.";
          LOGGER.error(msg);
          throw new RuntimeException(msg);
        }
        return interpretations.get(0).getRawChoices();
      }
    }
    else{
      return info.choices();
    }
  }

  /**
   * For the given county number, authenticate, tell corla there is one audit board, and
   * sign in as the audit board.
   * @param number County number
   * @return Session to use for this county in later testing.
   */
  protected TestAuditSession countyAuditInitialise(final int number){
    final String user = "countyadmin" + number;
    final SessionFilter filter = doLogin(user);

    // GET the county dashboard. This is just to test that the login worked.
    given().filter(filter).get("/county-dashboard");

    // Tell corla there is only one audit board
    given().filter(filter)
        .header("Content-Type", "application/json")
        .body(createBody(Map.of("count", 1)).toJSONString())
        .post("/set-audit-board-count");

    // Get the audit board ASM state
    final JsonPath auditBoardASMState = given().filter(filter)
        .header("Content-Type", "application/json")
        .get("/audit-board-asm-state")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .jsonPath();

    LOGGER.info(String.format("Audit board ASM state for County %d: %s.",
        number, auditBoardASMState.get("current_state")));

    // Sign in the default audit board
    final TestAuditSession session = new TestAuditSession(filter, auditBoard, 0, number);
    auditBoardSignIn(session);

    return session;
  }

  /**
   * Return list of CVRs to audit for the given round.
   * @param round      Audit round.
   * @param filter     Session filter for use when calling thr cvr-to-audit-list endpoint.
   * @return List of CVRs to audit for given round.
   */
  public List<CastVoteRecord> getCvrsToAudit(int round, SessionFilter filter){
    // Collect CVRs to audit
    final JsonPath cvrs = given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .get("cvr-to-audit-list?round=" + round)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .jsonPath();

    // Each CVR in the returned JSON structure will look something like this:
    //{
    //  "audit_sequence_number": 1996,
    //    "scanner_id": 102,
    //    "batch_id": "151",
    //    "record_id": 11,
    //    "imprinted_id": "102-151-11",
    //    "cvr_number": 78992,
    //    "db_id": 161428,
    //    "ballot_type": "DS-07",
    //    "storage_location": "",
    //    "audit_board_index": 0,
    //    "audited": false,
    //    "previously_audited": false
    //}
    final List<HashMap<String,Object>> cvrData = cvrs.getList("");

    // In each HashMap, the "db_id" field will be an Integer, which we will need to convert
    // to Long to create the CVR id list to pass to CastVoteRecordQueries::get.
    final List<Long> cvrIds = cvrData.stream().map(m -> Long.valueOf((int) m.get("db_id"))).toList();

    // Get CastVoteRecords for each of the CVRs to audit.
    return CastVoteRecordQueries.get(cvrIds);
  }

  /**
   * Given a cast vote record, initialise a map to be used when uploading the audited ballot
   * corresponding to that CVR. This map defines attributes like ballot type, batch id, and so on.
   * @param rec Cast vote record being audited.
   * @return A map defining the attributes of the audited ballot, for use when calling the endpoint
   * to upload an audited ballot.
   */
  private Map<String,Object> initialiseAuditedCVR(final CastVoteRecord rec){
    Map<String, Object> audited_cvr = new HashMap<>();
    audited_cvr.put(BALLOT_TYPE, rec.ballotType());
    audited_cvr.put(BATCH_ID, rec.batchID());
    audited_cvr.put(COUNTY_ID, rec.countyID());
    audited_cvr.put(CVR_NUMBER, rec.cvrNumber());
    audited_cvr.put(ID, rec.id());
    audited_cvr.put(IMPRINTED_ID, rec.imprintedID());
    audited_cvr.put(RECORD_ID, rec.recordID());
    audited_cvr.put(RECORD_TYPE, rec.recordType());
    audited_cvr.put(SCANNER_ID, rec.scannerID());
    audited_cvr.put(TIMESTAMP, rec.timestamp());
    return audited_cvr;
  }

  /**
   * With the given payload -- audited CVR attributes for the CVR with the given ID -- call the
   * endpoint for uploading an audited CVR.
   * @param audited_cvr  Attributes of the CVR being audited.
   * @param cvrID        ID of the CVR being audited.
   * @param filter       Session filter to use when calling the upload-audit-cvr endpoint.
   */
  private void callUploadCVREndpoint(final Map<String,Object> audited_cvr, final long cvrID,
      final SessionFilter filter){

    // Create JSON data structure to supply to upload-audit-cvr endpoint
    final JSONObject params = createBody(Map.of(
        "auditBoardIndex", 0,
        "audit_cvr", audited_cvr,
        "cvr_id", cvrID
    ));

    // Upload audited CVR
    given().filter(filter)
        .header("Content-Type", "application/json")
        .body(params.toJSONString())
        .post("/upload-audit-cvr");
  }

  /**
   * With the given payload -- CVR ID -- call the ballot not found endpoint.
   * @param cvrID        ID of the CVR being audited.
   * @param filter       Session filter to use when calling the upload-audit-cvr endpoint.
   */
  private void callBallotNotFoundEndpoint(final long cvrID, final SessionFilter filter){

    // Create JSON data structure to supply to upload-audit-cvr endpoint
    final JSONObject params = createBody(Map.of(
        "auditBoardIndex", 0,
        "id", cvrID
    ));

    // Upload audited CVR
    given().filter(filter)
        .header("Content-Type", "application/json")
        .body(params.toJSONString())
        .post("/ballot-not-found");
  }

  /**
   * For the given county number, collect the set of CVRs to audit for the given round, and
   * upload the audit CVRs.
   * @param round   Audit round.
   * @param session TestAuditSession capturing the audit session for a county.
   */
  protected void auditCounty(final int round, final TestAuditSession session, final Instance instance){

    final SessionFilter filter = session.filter();
    final List<CastVoteRecord> cvrsToAudit = getCvrsToAudit(round, filter);

    LOGGER.info("CVRS FOR AUDIT IN ROUND " + round);
    for(final CastVoteRecord rec : cvrsToAudit){
      LOGGER.info("County ID," + rec.countyID() + ",Imprinted ID," + rec.imprintedID());
    }

    // Upload audit CVRs
    for(final CastVoteRecord rec : cvrsToAudit){
      // We need to post a JSON structure of the following form:
      //{
      //  "auditBoardIndex":0,
      //  "audit_cvr":{
      //      "ballot_type": ###,
      //      "batch_id": ###,
      //      "contest_info":[{
      //          "choices":["ANDERSON John(1)","COOREY Cate(2)","HUNTER Alan(4)", ...],
      //          "comment":"",
      //          "consensus":"YES",
      //          "contest": ###
      //      }],
      //      "county_id": ###,
      //      "cvr_number": ###,
      //      "id": ###,
      //      "imprinted_id": ###,
      //      "record_id": ###,
      //      "record_type":"UPLOADED",
      //      "scanner_id": ###,
      //      "timestamp": ###
      //   },
      //  "cvr_id": ###
      //}
      // Create audited cvr as a map
      Map<String, Object> audited_cvr = initialiseAuditedCVR(rec);

      // Check if this CVR should map to a phantom ballot for the purposes of this workflow
      final boolean isPhantom = instance.isPhantomBallot(rec.imprintedID(), rec.countyID());
      if(isPhantom){
        callBallotNotFoundEndpoint(rec.id(), filter);
        continue;
      }

      // Create contest_info for audited cvr.
      // Note that info.choices() has to be converted into "Name(Rank)" form, however
      // info.choices() gives the already interpreted form, and we want to provide
      // the raw choices (which is not stored in the database in the table for CVR contest
      // info). Raw choices for IRV votes that contained errors *are* stored in the
      // irv_ballot_interpretation table. So, we can, for any IRV vote, check whether
      // there is an entry for it in the interpretations table. If so, we take the raw choices
      // from there. Otherwise, we recreate the raw choices from info.choices().
      final List<String> disagreements = instance.getDisagreements(rec.imprintedID(), rec.countyID());
      final List<Map<String, Object>> contest_info = rec.contestInfo().stream().map(info ->
          Map.of("choices", translateToRawChoices(rec.countyID().toString(), round, rec.cvrNumber(),
                  info, rec.imprintedID(), instance), "comment", "", "consensus",
              disagreements.contains(info.contest().name()) ? "NO" : "YES",
              "contest", info.contest().id())).toList();

      audited_cvr.put("contest_info", contest_info);

      callUploadCVREndpoint(audited_cvr, rec.id(), filter);

      // Check whether we want to reaudit this ballot, and if so, process the reaudits.
      final Optional<List<Map<String, ReAuditDetails>>> reaudits = instance.getReAudits(
          rec.countyID().toString(), round, rec.imprintedID());

      if (reaudits.isPresent()) {
        for (final Map<String, ReAuditDetails> entry : reaudits.get()) {
          Map<String, Object> reaudited_cvr = initialiseAuditedCVR(rec);
          reaudited_cvr.put("reaudit", true);

          final List<Map<String, Object>> new_contest_info = rec.contestInfo().stream().map(info ->
              Map.of("choices", translateToRawChoicesReAudit(rec.cvrNumber(), info,
                      rec.imprintedID(), entry), "comment", "", "consensus",
                  entry.containsKey(info.contest().name()) ? entry.get(info.contest().name()).consensus() :
                      "YES", "contest", info.contest().id())).toList();

          reaudited_cvr.put("contest_info", new_contest_info);

          callUploadCVREndpoint(reaudited_cvr, rec.id(), filter);
        }
      }
    }
  }

  /**
   * Get the DoSDashboardRefreshResponse, as a JSONPath object, which contains basically everything
   * about the current status of the audit.
   * Also tests that the http response is OK.
   * @return the DosDashboardRefreshResponse.
   */
  protected JsonPath getDoSDashBoardRefreshResponse() {
    // Note: this would be a lot simpler if it just returned a DoSDashBoardRefreshResponse via
    // DoSDashboardRefreshResponse DoSDashboard = GSON.fromJson(data, DoSDashboardRefreshResponse.class);
    // but that throws errors relating to parsing of enums. Not sure exactly why.
    // Similarly, so does getting the response and then calling
    // .as(DoSDashboardRefreshResponse.class);
    // So I've left it as a JsonPath, from which you can collect the fields by name.

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    return given()
            .filter(filter)
            .header("Content-Type", "application/json")
            .get("/dos-dashboard")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .jsonPath();
  }

  /**
   * Get the sample size estimates CSV and return the parsed data.
   * @return The sample size estimate data as a list of EstimateData structures.
   */
  protected Map<String,EstimateSampleSizes.EstimateData> getSampleSizeEstimates() {
    final String prefix = "[getSampleSizeEstimates]";

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    final String data = given()
        .filter(filter)
        .get("/estimate-sample-sizes")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .asString();

    final Map<String,EstimateSampleSizes.EstimateData> estimates = new HashMap<>();
    final String[] lines = data.split("\n");
    // Skip the first line (which has headers)
    for(int i = 1 ; i < lines.length ; i++) {

      final String[] line = lines[i].split(",");
      if(line.length < 7) {
        final String msg = prefix + " Invalid sample size estimate data";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
      }

      final EstimateSampleSizes.EstimateData estimate = new EstimateSampleSizes.EstimateData(
          line[0],
          line[1],
          line[2],
          Integer.parseInt(line[3]),
          Long.parseLong(line[4]),
          new BigDecimal(line[5]),
          Integer.parseInt(line[6])
      );
      estimates.put(estimate.contestName(), estimate);
    }

    return estimates;
  }

  /**
   * Get a CSV report. Note this has only been tested with the irv ballot interpretation report.
   * @param report the name of the report. See list of possibilities at src/main/resources/sql.
   * @return each line of the CSV as a string, including the header.
   */
  protected List<String> getReportAsCSV(String report) {
    final String prefix = "[getReportAsCSV]";

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    // Get the IRV ballot interpretation report.

    final byte[] zip = given()
        .filter(filter)
        .get("/download-audit-report?reports="+report)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .response().asByteArray();

    ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zip));
    // There should only be one file in the .zip, since we only requested one.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      zipStream.getNextEntry();
      zipStream.transferTo(baos);
    } catch (IOException | NullPointerException e) {
      LOGGER.error(String.format("%s Error retrieving report %s: %s", prefix, report, e.getMessage()));
    }
    return Arrays.stream(baos.toString().split("\n")).toList();

  }

  /**
   * Used by DoS admin to set audit info, including risk limit and canonical list.
   * Sets the election date to an arbitrary date and the public meeting for one week later.
   * @param canonicalListFile the path to the canonical list csv file.
   * @param riskLimit         the risk limit.
   */
  protected void updateAuditInfo(final String canonicalListFile, final BigDecimal riskLimit) {

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    final JSONObject requestParams = new JSONObject();
    requestParams.put(RISK_LIMIT_JSON, riskLimit);
    requestParams.put(ELECTION_DATE,"2024-09-15T05:42:17.796Z");
    requestParams.put(ELECTION_TYPE,"general");
    requestParams.put(PUBLIC_MEETING_DATE,"2024-09-22T05:42:22.037Z");
    final JSONObject canonicalListContents = new JSONObject();
    canonicalListContents.put("contents",readFromFile(canonicalListFile));
    requestParams.put("upload_file", List.of(canonicalListContents));

    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toString())
        .post("/update-audit-info")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Checks that all the given counties have completed their CVR upload within the timeout.
   * @param counties       the counties to wait for, by county ID.
   * @param timeAllowedSeconds the maximum time, in seconds, to wait for.
   * @return true if all uploads were successful within timeoutSeconds, false if any failed or the timeout was reached.
   * Timing is imprecise and assumes all the calls take no time.
   */
  protected boolean uploadSuccessfulWithin(int timeAllowedSeconds, final Set<Integer> counties, final String fileType) throws InterruptedException {

    JsonPath dashboard = getDoSDashBoardRefreshResponse();
      while (timeAllowedSeconds-- > 0) {
        List<Integer> succeededCounties = new ArrayList<>();
        for (int c : counties) {
          final String status = dashboard.getString(COUNTY_STATUS + "." + c + "." + fileType + "."
              + STATUS);
            // Upload failed (e.g. a hash mismatch).
          if (status != null && status.equals(UploadedFile.FileStatus.FAILED.toString())) {
            return false;
            // This county succeeded.
          } else if (status != null && status.equals(UploadedFile.FileStatus.IMPORTED.toString())) {
            succeededCounties.add(c);
          }
        }
        if (succeededCounties.size() == counties.size()) {
          return true;
        } else {
          Thread.sleep(1000);
          dashboard = getDoSDashBoardRefreshResponse();
        }
    }

    // Timeout.
    return false;
  }

  /**
   * This abstract version allows descendents to make the assertions in their own way, either by
   * calling raire or by retrieving assertions and related data from an sql file.
   */
  protected abstract void makeAssertionData(final Optional<PostgreSQLContainer<?>> postgres, final List<String> SQLfiles);

  protected void generateAssertions(final PostgreSQLContainer<?> postgres, final String sqlPath, final double timeLimitSeconds)
  {
    TestClassWithDatabase.runSQLSetupScript(postgres, sqlPath);
  }

  /**
   * Verify that some assertions are present for the given contest, that the minimum diluted
   * margin across these assertions is correct, and that the estimated sample size for the contest
   * is correct.
   * @param contest                   Name of the contest.
   * @param expectedDilutedMargin     Expected diluted margin of the contest.
   * @param actualEstimatedSamples    Actual sample size computation for the contest.
   * @param expectedEstimatedSamples  Expected sample size for the contest.
   * @param isIRV                     True if the contest is an IRV contest.
   */
  protected void verifySampleSize(final String contest, final double expectedDilutedMargin,
      final int actualEstimatedSamples, final int expectedEstimatedSamples, final boolean isIRV){

    if(isIRV) {
      final List<Assertion> assertions = AssertionQueries.matching(contest);
      assertFalse(assertions.isEmpty());

      final BigDecimal actualDilutedMargin = min(assertions.stream().map(
          Assertion::getDilutedMargin).toList());

      final DoubleComparator comp = new DoubleComparator();
      assertEquals(comp.compare(actualDilutedMargin.doubleValue(), expectedDilutedMargin), 0);
    }

    assertEquals(actualEstimatedSamples, expectedEstimatedSamples);
  }

  /**
   * Login as the state admin and hit the start audit round endpoint. Check that an
   * appropriate HTTP status is returned.
   */
  protected void startAuditRound() {
    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    given()
        .filter(filter)
        .post("/start-audit-round")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Delete a csv or manifest file, for the logged in county.
   *
   * @param countyId the ID number of the county (1 to 64)
   * @param type     either "cvr" or "bmi" (for the manifest)
   */
  protected void deleteFile(int countyId, String type) {
    final String prefix = "[deleteFile]";
    final SessionFilter filter = doLogin("stateadmin" + countyId);

    // add it to the selections.
    final JSONObject fileSelection = new JSONObject();
    fileSelection.put(FILETYPE, type);
    fileSelection.put(COUNTYID, countyId);

    if(!type.equals("bmi") &&  !type.equals("cvr")) {
      LOGGER.error(String.format("%s Delete file needs type 'bmi' or 'cvr'.", prefix));
    }

    // Post the delete-file request
    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(fileSelection.toString())
        .post("/delete-file")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Select contests to target, by name.
   * @param targetedContestsWithReasons Contests to target alongside the reasons for targeting them.
   * @return A mapping between contest name (all contests, not just those targeted) and its
   * database record ID (as a string).
   */
  protected Map<String,String> targetContests(final Map<String, String> targetedContestsWithReasons) {
    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    // First get the contests.
    // Again, this would be a lot easier if we could use .as(Contest[].class), but serialization is a problem.
    final JsonPath contests = getContests(false);

    // The contests and reasons to be requested.
    final JSONArray contestSelections = new JSONArray();

    Map<String,String> contestToDBID = new HashMap<>();

    // Find the IDs of the ones we want to target.
    for(int i=0 ; i < contests.getList("").size() ; i++) {
      final String contestName = contests.getString("[" + i + "]." + NAME);

      final Integer contestId = contests.getInt("[" + i + "]." + ID);
      contestToDBID.put(contestName, contestId.toString());

      // If this contest's name is one of the targeted ones...
      if(targetedContestsWithReasons.containsKey(contestName)) {
        // add it to the selections.
        final String reason = targetedContestsWithReasons.get(contestName);
        final JSONObject contestSelection = new JSONObject();

        contestSelection.put(AUDIT, COMPARISON.toString());
        contestSelection.put(CONTEST, contestId);
        contestSelection.put(REASON, reason);
        contestSelections.add(contestSelection);
      }
    }

    // Post the select-contests request
    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(contestSelections.toString())
        .post("/select-contests")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);

    return contestToDBID;
  }

  /**
   * Hit the /contests endpoint, to learn all the contests.
   * @param ignoreManifests
   * @return the response, as a JsonPath.
   */
  protected JsonPath getContests(boolean ignoreManifests) {

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    final String query = ignoreManifests ? "?" + IGNORE_MANIFESTS +"=true" : "";

    // First get the contests.
    // Again, this would be a lot easier if we could use .as(Contest[].class), but serialization is a problem.
    return given()
        .filter(filter)
        .header("Content-Type", "text/plain")
        .get("/contest"+query)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .jsonPath();
  }

  /**
   * Given a workflow instance, perform cononicalisation of candidate and contest names.
   * @param instance           Workflow instance
   * @param ignoreManifests
   */
  protected void canonicalise(final Instance instance, boolean ignoreManifests){
    final JsonPath contests = getContests(ignoreManifests);

    final Map<String,String> contestNameChanges = instance.getContestNameChanges();
    final Map<String, Map<String,String>> candNameChanges = instance.getCandidateNameChanges();

    final JSONArray canonicaliseContests = new JSONArray();
    final JSONArray canonicaliseCandidates = new JSONArray();

    // Find the IDs of the ones we want to target.
    for(int i=0 ; i < contests.getList("").size() ; i++) {
      final String contestName = contests.getString("[" + i + "]." + NAME);
      final Integer contestId = contests.getInt("[" + i + "]." + ID);
      final Integer countyId = contests.getInt("[" + i + "]." + COUNTY_ID);

      final String newName = contestNameChanges.getOrDefault(contestName, contestName);

      if(contestNameChanges.containsKey(contestName)){
        canonicaliseContests.add(Map.of(CONTESTID, contestId, COUNTYID,
            countyId, NAME, contestNameChanges.get(contestName)));
      }

      if(candNameChanges.containsKey(newName)){
        JSONArray candChanges = new JSONArray();
        for(Map.Entry<String,String> change : candNameChanges.get(newName).entrySet()){
          candChanges.add(createBody(Map.of(OLDNAME, change.getKey(),
              NEWNAME, change.getValue())));
        }
        canonicaliseCandidates.add(createBody(Map.of(CONTESTID, contestId,
            COUNTYID, countyId, CHOICES, candChanges)));
      }
    }

    final SessionFilter filter = doLogin("stateadmin1");

    // Post the set contest names request (canonicalise contest names)
    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(canonicaliseContests.toString())
        .post("/set-contest-names")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);

    // Post the set contest names request (canonicalise candidate names)
    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(canonicaliseCandidates.toString())
        .post("/set-contest-names")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Set the seed for the audit.
   */
  protected void setSeed(final String seed) {
    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    final JSONObject requestParams = new JSONObject();
    requestParams.put(SEED, seed);

    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toString())
        .post("/random-seed")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Verify that the "seed" report, when downloaded as a CSV, contains the correct data for
   * the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkSeedReport(final List<String> lines, final Instance instance){
    assertEquals(lines.size(), 2);
    assertEquals(lines.get(1).strip(), instance.getSeed());
  }

  /**
   * Verify that the "contest" report, when downloaded as a CSV, contains the correct data for
   * the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   * @param lastDiscrepancyCounts What we expect the final discrepancy counts to be for the contest.
   */
  protected void checkContestReport(final List<String> lines, final Instance instance,
      final Map<String,Map<String,Integer>> lastDiscrepancyCounts) {

    final int numContests = ContestResultQueries.count();
    assertEquals(1 + numContests, lines.size());
    for (int i = 1; i < lines.size(); ++i) {
      final List<String> tokens = Arrays.stream(lines.get(i).split(",")).toList();

      // There are at least 20 columns in this report
      assertTrue(tokens.size() >= 20);

      final String contestName = tokens.get(0);

      // For the given contest, check: the audit reason; the winner; that the risk limit was
      // achieved; the minimum margin; discrepancy counts; overstatements count; and final
      // optimistic number of samples to audit.
      final String targetReason = tokens.get(1);
      final String auditStatus = tokens.get(2);
      final int winnersAllowed = Integer.parseInt(tokens.get(3));
      List<String> winners = new ArrayList<>();
      for(int j = 6; j < 6 + winnersAllowed; ++j){
        winners.add(tokens.get(j));
      }
      final String winner = StringUtils.join(winners, ",").replace("\"","");
      final Integer minMargin = Integer.parseInt(tokens.get(6+winnersAllowed));
      final Integer twoVoteOverCount = Integer.parseInt(tokens.get(9+winnersAllowed));
      final Integer oneVoteOverCount = Integer.parseInt(tokens.get(10+winnersAllowed));
      final Integer zeroDiscrepancyCount = Integer.parseInt(tokens.get(11+winnersAllowed));
      final Integer oneVoteUnderCount = Integer.parseInt(tokens.get(12+winnersAllowed));
      final Integer twoVoteUnderCount = Integer.parseInt(tokens.get(13+winnersAllowed));
      final Integer disagreementCount = Integer.parseInt(tokens.get(14+winnersAllowed));

      final int overstatementCount = Integer.parseInt(tokens.get(16+winnersAllowed));

      Map<String, Integer> countMap = Map.of(ONE_OVER, 0, TWO_OVER, 0,
          ONE_UNDER, 0, TWO_UNDER, 0, OTHER, 0, DISAGREEMENTS, 0);

      final Optional<Integer> expectedRawMargin = instance.getRawMargin(contestName);
      expectedRawMargin.ifPresent(m -> assertEquals(minMargin, m));

      final Optional<String> expectedWinner = instance.getWinner(contestName);
      expectedWinner.ifPresent(s -> assertEquals(winner, s));
      final Optional<String> expectedReason = instance.getTargetedContestReason(contestName);
      expectedReason.ifPresent(s -> assertEquals(targetReason.toLowerCase(), s.toLowerCase()));

      if (instance.getTargetedContests().containsKey(contestName)) {
        // Audit status should be risk limit achieved for all successful workflows
        assertEquals(auditStatus.toLowerCase(), Workflow.RISK_LIMIT_ACHIEVED.toLowerCase());

        countMap = lastDiscrepancyCounts.get(contestName);
      }

      // Verify expected discrepancy counts
      final int expectedOverstatements = countMap.get(TWO_OVER) + countMap.get(ONE_OVER);

      assertEquals(twoVoteOverCount, countMap.get(TWO_OVER));
      assertEquals(oneVoteOverCount, countMap.get(ONE_OVER));
      assertEquals(twoVoteUnderCount, countMap.get(TWO_UNDER));
      assertEquals(oneVoteUnderCount, countMap.get(ONE_UNDER));
      assertEquals(zeroDiscrepancyCount, countMap.get(OTHER));
      assertEquals(disagreementCount, countMap.get(DISAGREEMENTS));

      assertEquals(overstatementCount, expectedOverstatements);

      final int optimisticSamples = Integer.parseInt(tokens.get(17+winnersAllowed));
      final int estimatedSamples = Integer.parseInt(tokens.get(18+winnersAllowed));

      final Optional<Integer> expectedFinalOptimistic = instance.getExpectedOptimisticSamples(contestName);
      expectedFinalOptimistic.ifPresent(m -> assertTrue(m <= optimisticSamples));
      final Optional<Integer> expectedFinalEstimated = instance.getExpectedEstimatedSamples(contestName);
      expectedFinalEstimated.ifPresent(m -> assertTrue(m <= estimatedSamples));
    }
  }

  /**
   * Verify that the "contest_selection" report, when downloaded as a CSV, contains the correct
   * data for the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkContestSelectionReport(final List<String> lines, final Instance instance){
    final int numContests = ContestResultQueries.count();
    assertEquals(1 + numContests, lines.size());
    for(int i = 1; i < lines.size(); ++i) {
      final List<String> tokens = Arrays.stream(lines.get(i).split(",")).toList();

      // There are at least 3 columns in this report
      assertTrue(tokens.size() >= 3);

      final String contestName = tokens.get(1);
      final int minMargin = Integer.parseInt(tokens.get(0));

      final Optional<Integer> expectedAudited = instance.getExpectedAuditedBallots(contestName);
      expectedAudited.ifPresent(s -> assertTrue(s <= tokens.size() - 2));
      final Optional<Integer> expectedRawMargin = instance.getRawMargin(contestName);
      expectedRawMargin.ifPresent(m -> assertEquals(m.intValue(), minMargin));
    }
  }

  /**
   * Verify that the "contests_by_county" report, when downloaded as a CSV, contains the correct
   * data for the given instance. Note, the instance may only specify the contests for a
   * selected subset of counties (rather than all of them).
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkContestsByCountyReport(final List<String> lines, final Instance instance){
    final Map<String,List<String>> contestsByCounty = instance.getContestsByCounty();

    for(int i = 1; i < lines.size(); ++i) {
      final List<String> tokens = Arrays.stream(lines.get(i).split(",")).toList();

      assertTrue(tokens.size() >= 3);
      final String countyName = tokens.get(0);
      final String contestName = tokens.get(1);

      if(contestsByCounty.containsKey(countyName)){
        assertTrue(contestsByCounty.get(countyName).contains(contestName));
      }
    }
  }

  /**
   * Verify that the "tabulate_plurality" report, when downloaded as a CSV, contains the correct
   * data for the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkTabulatePluralityReport(final List<String> lines, final Instance instance){
    // TODO
  }

  /**
   * Verify that the "tabulate_county_plurality" report, when downloaded as a CSV, contains the correct
   * data for the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkTabulateCountyPluralityReport(final List<String> lines, final Instance instance){
    // TODO
  }

  /**
   * Verify that the "summarize_IRV" report, when downloaded as a CSV, contains the correct
   * data for the given instance.
   * @param lines      Lines of the CSV report, as Strings.
   * @param instance   Workload instance being run.
   */
  protected void checkSummarizeIRVReport(final List<String> lines, final Instance instance){
    for(int i = 1; i < lines.size(); ++i) {
      final List<String> tokens = Arrays.stream(lines.get(i).split(",")).toList();

      // There are more columns in this report, but we are going to verify the first three
      // for each entry: contest_name; target_reason; winner.
      assertTrue(tokens.size() >= 3);

      final String contestName = tokens.get(0);
      final String targetReason = tokens.get(1);
      final String winner = tokens.get(2);

      // Check that the contest *is* an IRV contest, as specified in the instance.
      assertTrue(instance.getIRVContests().contains(contestName));

      final Optional<String> expectedWinner = instance.getWinner(contestName);
      expectedWinner.ifPresent(s -> assertEquals(s, winner));
      final Optional<String> expectedReason = instance.getTargetedContestReason(contestName);
      expectedReason.ifPresent(s -> assertEquals(s, targetReason));
    }
  }

  /**
   * Read a string from a file.
   * Catches the IO exception and returns "" if file can't be opened.
   * @param fileName the path of the file
   * @return the file contents as a single string, with '\n' added between lines.
   */
  private String readFromFile(final String fileName) {
    final String prefix = "[readFromFile]";
    try {
      final Path path = Paths.get(fileName);
      return String.join("\n",Files.readAllLines(path));
      } catch (final IOException ex) {
        LOGGER.error(prefix + ex.getMessage());
      return "";
    }
  }

  /**
   * For the given user, perform first and second round authentication.
   * @param username Username for the user to authenticate
   * @return The user's session, to be interacted with in later testing.
   */
  protected SessionFilter doLogin(final String username) {
    final SessionFilter filter = new SessionFilter();
    authenticate(filter, username,"",1);
    authenticate(filter, username,"s d f",2);
    return filter;
  }
}