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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.BeforeClass;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import wiremock.net.minidev.json.JSONArray;
import wiremock.net.minidev.json.JSONObject;

/**
 * Base class for an API test workflow designed to run through a sequence of steps involving
 * a sequence of endpoint accesses.
 */
public class Workflow extends TestClassWithDatabase {

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
   * Container for the mock-up database.
   */
  protected final static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * Path for storing temporary config files
   */
  private static final String tempConfigPath = "src/test/workflows/temp/";

  /**
   * Path for all the data files.
   */
  protected static final String dataPath = "src/test/resources/CSVs/";

  /**
   * Strings for colorado-rla JSON structures.
   */
  protected static final String ASM_STATE = "asm_state";
  protected static final String AUDIT = "audit";
  protected static final String AUDIT_INFO = "audit_info";
  protected static final String CONTEST = "contest";
  protected static final String CANONICAL_CHOICES = "canonicalChoices";
  protected static final String CANONICAL_CONTESTS = "canonicalContests";
  protected static final String COUNTY_STATUS = "county_status";
  protected static final String CVR_FILETYPE = "cvr-export";
  protected static final String CVR_JSON = "cvr_export_file";
  protected static final String ESTIMATED_BALLOTS = "estimated_ballots_to_audit";
  protected static final String ELECTION_DATE = "election_date";
  protected static final String ELECTION_TYPE = "election_type";
  protected static final String ID = "id";
  protected static final String MANIFEST_FILETYPE = "ballot-manifest";
  protected static final String MANIFEST_JSON = "ballot_manifest_file";
  protected static final String BALLOTS_REMAINING = "ballots_remaining_in_round";
  protected static final String OPTIMISTIC_BALLOTS = "optimistic_ballots_to_audit";
  protected static final String NAME = "name";
  protected static final String PUBLIC_MEETING_DATE = "public_meeting_date";
  protected static final String REASON = "reason";
  protected static final String RISK_LIMIT_JSON = "risk_limit";
  protected static final String SEED = "seed";
  protected static final String STATUS = "status";

  @BeforeClass
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;
  }

  /**
   * Run main, using the psql container as its database. Main can take (database) properties as a
   * CLI, but only as a file, so we need to make the file and then tell main to read it.
   * @param testFileName the name of the test file - must be different for each test.
   */
  protected static void runMain(final String testFileName) {
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
        .post("/sign-off-audit-round");
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
   * Given a vote in a specific contest, translate the choices on that vote (if an IRV contest)
   * into the form "Candidate(Rank),...,Candidate(Rank)". If the contest is a Plurality contest,
   * return the choices from info.choices(). Otherwise, check whether the vote's original choices
   * were interpreted to remove errors. If so, return the original raw choices from the
   * irv_ballot_interpretation table. Otherwise, add ranks to each choice name and return.
   * @param info Details of the vote for the relevant contest.
   * @param imprintedId Imprinted identifier of the CVR containing the given vote.
   * @return The list of original choices on the pre-interpreted CVR with the given imprinted ID.
   */
  private List<String> translateToRawChoices(final CVRContestInfo info, final String imprintedId){
    final String prefix = "[translateToRawChoices]";

    if(info.contest().description().equals(ContestType.IRV.toString())){
      // The contest is an IRV contest.
      final List<IRVBallotInterpretation> interpretations = TestOnlyQueries.matching(
          info.contest().name(), imprintedId, RecordType.UPLOADED);

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

    final List<Map<String,String>> auditBoard = List.of(
        Map.of("first_name","V", "last_name","T",
            "political_party","Unaffiliated"),
        Map.of("first_name","M", "last_name","B",
            "political_party","Unaffiliated")
    );

    // Sign in the audit board
    final TestAuditSession session = new TestAuditSession(filter, auditBoard, 0, number);
    auditBoardSignIn(session);

    return session;
  }

  /**
   * Return list of CVRs to audit for the given round and the given county (identified by
   * their TestAuditSession).
   * @param round      Audit round.
   * @param session    TestAuditSession capturing the audit session for the relevant county.
   * @return List of CVRs to audit for the county and round.
   */
  protected List<CastVoteRecord> getCvrsToAudit(int round, final TestAuditSession session){
    final SessionFilter filter = session.filter();

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
   * For the given county number, collect the set of CVRs to audit for the given round, and
   * upload the audit CVRs.
   * @param round   Audit round.
   * @param session  TestAuditSession capturing the audit session for a county.
   */
  protected void auditCounty(final int round, final TestAuditSession session){
    final List<CastVoteRecord> cvrsToAudit = getCvrsToAudit(round, session);
    final SessionFilter filter = session.filter();

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

      // Create contest_info for audited cvr.
      // Note that info.choices() has to be converted into "Name(Rank)" form, however
      // info.choices() gives the already interpreted form, and we want to provide
      // the raw choices (which is not stored in the database in the table for CVR contest
      // info). Raw choices for IRV votes that contained errors *are* stored in the
      // irv_ballot_interpretation table. So, we can, for any IRV vote, check whether
      // there is an entry for it in the interpretations table. If so, we take the raw choices
      // from there. Otherwise, we recreate the raw choices from info.choices().
      final List<Map<String,Object>> contest_info = rec.contestInfo().stream().map(info ->
        Map.of("choices", translateToRawChoices(info, rec.imprintedID()),
            "comment", "", "consensus", "YES",
            "contest", info.contest().id())).toList();

      // Create audited cvr as a map
      Map<String, Object> audited_cvr = new HashMap<>();
      audited_cvr.put("ballot_type", rec.ballotType());
      audited_cvr.put("batch_id", rec.batchID());
      audited_cvr.put("contest_info", contest_info);
      audited_cvr.put("county_id", rec.countyID());
      audited_cvr.put("cvr_number", rec.cvrNumber());
      audited_cvr.put("id", rec.id());
      audited_cvr.put("imprinted_id", rec.imprintedID());
      audited_cvr.put("record_id", rec.recordID());
      audited_cvr.put("record_type", rec.recordType());
      audited_cvr.put("scanner_id", rec.scannerID());
      audited_cvr.put("timestamp", rec.timestamp());

      // Create JSON data structure to supply to upload-audit-cvr endpoint
      final JSONObject params = createBody(Map.of(
          "auditBoardIndex", 0,
          "audit_cvr", audited_cvr,
          "cvr_id", rec.id()
      ));

      // Upload discrepancy-free audited CVR
      given().filter(filter)
          .header("Content-Type", "application/json")
          .body(params.toJSONString())
          .post("/upload-audit-cvr");
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
    // DoSDashboardRefreshResponse DoSDasboard = GSON.fromJson(data, DoSDashboardRefreshResponse.class);
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
  protected boolean uploadSuccessfulWithin(int timeAllowedSeconds, final Set<Integer> counties,
      final String fileType) throws InterruptedException {

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
   * Generate assertions (for IRV contests). At the moment, rather than call raire-service, we
   * are mocking that functionality. To do so, we take the path to a file containing SQL insert
   * statements for all assertion related content that would have been created by the raire-service,
   * and inserted into the database, and insert it into the database here.
   * TODO Replace this with a call to the raire-service. Currently problematic because it will be
   * reading the wrong database.
   * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/218">...</a>
   * Set it up so that we run raire-service inside the Docker container and tell main where to find it.
   */
  protected void generateAssertions(final String sqlPath, final double timeLimitSeconds)
  {
      final var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
      ScriptUtils.runInitScript(containerDelegate, sqlPath);

      // Version that connects to raire-service below:
      // Login as state admin.
      //final SessionFilter filter = doLogin("stateadmin1");

      //given()
      //    .filter(filter)
      //    .header("Content-Type", "application/x-www-form-urlencoded")
      //    .get("/generate-assertions?timeLimitSeconds="+timeLimitSeconds)
      //    .then()
      //    .assertThat()
      //    .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Verify that some assertions are present for the given contest, that the minimum diluted
   * margin across these assertions is correct, and that the estimated sample size for the contest
   * is correct.
   * @param contest                   Name of the contest.
   * @param expectedDilutedMargin     Expected diluted margin of the contest.
   * @param actualEstimatedSamples    Actual sample size computation for the contest.
   * @param riskLimit                 Risk limit for the audit.
   */
  protected void verifyAssertions(final String contest, final double expectedDilutedMargin,
      final int actualEstimatedSamples, final BigDecimal riskLimit){
    final List<Assertion> assertions = AssertionQueries.matching(contest);
    assertFalse(assertions.isEmpty());

    final BigDecimal actualDilutedMargin = min(assertions.stream().map(
        Assertion::getDilutedMargin).toList());

    final DoubleComparator comp = new DoubleComparator();
    assertEquals(comp.compare(actualDilutedMargin.doubleValue(), expectedDilutedMargin), 0);

    // Sample size formula is (-2 * gamma * log(risk_limit))/dilutedMargin
    final int samples = (int)(Math.ceil(-2.0 * Audit.GAMMA.doubleValue() *
        Math.log(riskLimit.doubleValue()))/expectedDilutedMargin);
    assertEquals(actualEstimatedSamples, samples);
  }


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
   * Select contests to target, by name.
   */
  protected void targetContests(final Map<String, String> targetedContestsWithReasons) {
    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    // First get the contests.
    // Again, this would be a lot easier if we could use .as(Contest[].class), but serialization is a problem.
    final JsonPath contests = given()
            .filter(filter)
            .header("Content-Type", "text/plain")
            .get("/contest")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .jsonPath();

    // The contests and reasons to be requested.
    final JSONArray contestSelections = new JSONArray();

    // Find the IDs of the ones we want to target.
    for(int i=0 ; i < contests.getList("").size() ; i++) {

      final String contestName = contests.getString("[" + i + "]." + NAME);
      // If this contest's name is one of the targeted ones...
      final String reason = targetedContestsWithReasons.get(contestName);
      if(reason != null) {
        // add it to the selections.
        final JSONObject contestSelection = new JSONObject();

        final Integer contestId = contests.getInt("[" + i + "]." + ID);
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
