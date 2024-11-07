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
import static org.testng.Assert.assertEquals;
import static us.freeandfair.corla.Main.main;

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import us.freeandfair.corla.model.Contest;
import wiremock.net.minidev.json.JSONArray;
import wiremock.net.minidev.json.JSONObject;

/**
 * Base class for an API test workflow designed to run through a sequence of steps involving
 * a sequence of endpoint accesses.
 */
public class Workflow extends TestClassWithDatabase {

  /**
   * Number of CO counties
   */
  protected static final int numCounties = 64;

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Workflow.class);

  /**
   * Path for storing temporary config files
   */
  private static final String tempConfigPath = "src/test/workflows/temp/";

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
  protected static void runMain(String testFileName) {
    final String propertiesFile = tempConfigPath +testFileName+"-test.properties";
    try {
      FileOutputStream os = new FileOutputStream(propertiesFile);
      StringWriter sw = new StringWriter();
      config.store(sw, "Ephemeral database config for Demo1");
      os.write(sw.toString().getBytes());
      os.close();
      main(propertiesFile);
    } catch (Exception e) {
      LOGGER.error("Couldn't write Demo1-test.properties", e);
    }
  }

  protected JSONObject createBody(final Map<String, String> data) {
    JSONObject body = new JSONObject();
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
    assertEquals(authStatus, (stage == 1) ? "TRADITIONALLY_AUTHENTICATED" :
        "SECOND_FACTOR_AUTHENTICATED", "Stage " + stage + " auth failed.");
  }

  /**
   * Unauthenticate the given user.
   *
   * @param filter Session filter to maintain same session across API test.
   * @param user   Username to unauthenticate.
   */
  protected void logout(final SessionFilter filter, final String user) {
    JSONObject requestParams = createBody(Map.of("username", user));

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
    SessionFilter filter = doLogin(user);

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
  protected List<EstimateSampleSizes.EstimateData> getSampleSizeEstimates() {
    final String prefix = "[getSampleSizeEstimates]";

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    String data = given()
        .filter(filter)
        .get("/estimate-sample-sizes")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .body()
        .asString();

    List<EstimateSampleSizes.EstimateData> estimates = new ArrayList<>();
    var lines = data.split("\n");
    // Skip the first line (which has headers)
    for(int i = 1 ; i < lines.length ; i++) {

      var line = lines[i].split(",");
      if(line.length < 7) {
        final String msg = prefix + " Invalid sample size estimate data";
        LOGGER.error(msg);
        throw new RuntimeException(msg);
      }

      EstimateSampleSizes.EstimateData estimate = new EstimateSampleSizes.EstimateData(
          line[0],
          line[1],
          line[2],
          Integer.parseInt(line[3]),
          Long.parseLong(line[4]),
          new BigDecimal(line[5]),
          Integer.parseInt(line[6])
      );
      estimates.add(estimate);
    }

    return estimates;
  }

  /**
   * Used by DoS admin to set audit info, including risk limit and canonical list.
   * Sets the election date to an arbitrary date and the public meeting for one week later.
   * @param canonicalListFile the path to the canonical list csv file.
   * @param riskLimit         the risk limit.
   */
  protected void updateAuditInfo(String canonicalListFile, BigDecimal riskLimit) {

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    JSONObject requestParams = new JSONObject();
    requestParams.put("risk_limit", riskLimit);
    requestParams.put("election_date","2024-09-15T05:42:17.796Z");
    requestParams.put("election_type","general");
    requestParams.put("public_meeting_date","2024-09-22T05:42:22.037Z");
    JSONObject canonicalListContents = new JSONObject();
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
   * Generate assertions (for IRV contests)
   * TODO At the moment this expects the raire-service to be running.
   * Set it up so that we run raire-service inside the Docker container and tell main where to find it.
   */
  protected void generateAssertions(double timeLimitSeconds) {
      // Login as state admin.
      final SessionFilter filter = doLogin("stateadmin1");

      given()
          .filter(filter)
          .header("Content-Type", "application/x-www-form-urlencoded")
          .get("/generate-assertions?timeLimitSeconds="+timeLimitSeconds)
          .then()
          .assertThat()
          .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Select contests to target, by name.
   */
  protected void targetContests(Map<String, String> targetedContestsWithReasons) {
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
    JSONArray contestSelections = new JSONArray();

    // Find the IDs of the ones we want to target.
    for(int i=0 ; i < contests.getList("").size() ; i++) {

      final String contestName = contests.getString("[" + i + "].name");
      // If this contest's name is one of the targeted ones...
      String reason = targetedContestsWithReasons.get(contestName);
      if(reason != null) {
        // add it to the selections.
        final JSONObject contestSelection = new JSONObject();

        final Integer contestId = contests.getInt("[" + i + "].id");
        contestSelection.put("audit","COMPARISON");
        contestSelection.put("contest",contestId);
        contestSelection.put("reason", reason);
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
  protected void setSeed(String seed) {
    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");

    JSONObject requestParams = new JSONObject();
    requestParams.put("seed", seed);

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
      Path path = Paths.get(fileName);
      return String.join("\n",Files.readAllLines(path));
      } catch (IOException ex) {
        LOGGER.error(prefix + ex.getMessage());
      return "";
    }
  }

  private SessionFilter doLogin(String username) {
    final SessionFilter filter = new SessionFilter();
    authenticate(filter, username,"",1);
    authenticate(filter, username,"s d f",2);
    return filter;
  }

}
