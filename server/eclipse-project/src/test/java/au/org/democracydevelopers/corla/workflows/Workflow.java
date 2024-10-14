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

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import java.io.File;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import wiremock.net.minidev.json.JSONObject;

/**
 * Base class for an API test workflow designed to run through a sequence of steps involving
 * a sequence of endpoint accesses.
 */
public class Workflow {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Workflow.class);

  @BeforeClass
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;
  }

  protected JSONObject createBody(final Map<String,String> data){
    JSONObject body = new JSONObject();
    body.putAll(data);
    return body;
  }

  /**
   * Authenticate the given user with the given password/second factor challenge answer.
   * @param filter Session filter to maintain same session across API test.
   * @param user Username to authenticate
   * @param pwd Password/second factor challenge answer for user.
   * @param stage Authentication stage.
   */
  protected void authenticate(final SessionFilter filter, final String user, final String pwd, final int stage){
    final JSONObject requestParams = (stage == 1) ?
        createBody(Map.of("username", user, "password", pwd)) :
        createBody(Map.of("username", user, "second_factor", pwd));

    final Response response = given().filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toJSONString())
        .post("/auth-admin");

    final String authStatus = response.getBody().jsonPath().getString("stage");

    LOGGER.debug("Auth status for login "+user+" stage "+stage+" is "+authStatus);
    assertEquals(authStatus, (stage == 1) ? "TRADITIONALLY_AUTHENTICATED" :
        "SECOND_FACTOR_AUTHENTICATED", "Stage "+stage+" auth failed.");
  }

  /**
   * Unauthenticate the given user.
   * @param filter Session filter to maintain same session across API test.
   * @param user Username to unauthenticate.
   */
  protected void logout(final SessionFilter filter, final String user){
    JSONObject requestParams = createBody(Map.of("username", user));

    given()
        .filter(filter)
        .header("Content-Type", "application/json")
        .body(requestParams.toJSONString())
        .post("/unauthenticate");
  }

  /**
   * Upload a file and its corresponding hash on behalf of the given county number.
   * @param number Number of the county uploading the CVR/hash.
   * @param file Path of the file to be uploaded.
   * @param hash Path of the corresponding hash for the CVR file.
   */
  protected void uploadCounty(final int number, final String fileType,
      final String file, final String hash){

    final SessionFilter filter = new SessionFilter();
    final String user = "countyadmin" + number;

    // Login as the county.
    authenticate(filter, user, "", 1);
    authenticate(filter, user, "s d f", 2);

    // GET the county dashboard. This is just to test that the login worked.
    given().filter(filter).get("/county-dashboard");

    // TODO: the following is not the correct code for accessing the upload-file
    // endpoint correctly -- need to debug!
    // Post the CVR file and its hash.
    Response response = given()
        .filter(filter)
        .header("Content-Type", "multipart/form-data")
        .multiPart("file", new File(file), "text/csv")
        .multiPart("hash", new File(hash), "test/csv")
        .post("/upload-file");

    // Request the CVR export to be imported
    given().filter(filter).body(response.body()).post("/import-" + fileType + "-export");

    // Logout.
    logout(filter, user);
  }

}
