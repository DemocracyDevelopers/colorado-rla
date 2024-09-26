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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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

  /**
   * Authenticate the given user with the given password and check that the given status is
   * returned in the response.
   * @param user Username of the administrator.
   * @param pwd Password of the administrator.
   * @param stage Authentication stage (1 or 2).
   * @param statusExpected Expected status to be returned after authentication.
   */
  protected void authenticate(final String user, final String pwd, final int stage, final String statusExpected){
    JSONObject requestParams = new JSONObject();
    requestParams.put("username", user);
    if(stage == 1) {
      requestParams.put("password", pwd);
    }
    else{
      requestParams.put("second_factor", pwd);
    }

    RequestSpecification request = given();
    request.header("Content-Type", "application/json");
    request.body(requestParams.toJSONString());

    Response response = request.post("/auth-admin");
    JsonPath path = new JsonPath(response.getBody().asString());

    final String authStatus = path.getString("stage");
    LOGGER.debug("Auth status for login "+user+" is "+authStatus);
    assertEquals(authStatus, statusExpected, "Stage " + stage + "auth failed.");
  }

  /**
   * Unauthenticate the given user.
   * @param user Username to unauthenticate.
   */
  protected void logout(final String user){
    JSONObject requestParams = new JSONObject();
    requestParams.put("username", user);

    given()
        .header("Content-Type", "application/json")
        .body(requestParams.toJSONString())
        .post("/unauthenticate");
  }

  /**
   * Upload a CVR file and its corresponding hash on behalf of the given county number.
   * @param number Number of the county uploading the CVR/hash.
   * @param cvrFile Path of the CVR file to be uploaded.
   * @param hashFile Path of the corresponding hash for the CVR file.
   */
  protected void cvrUploadCounty(final int number, final String cvrFile, final String hashFile){
    final String user = "countyadmin" + number;

    // Login as the county.
    authenticate(user, "", 1, "TRADITIONALLY_AUTHENTICATED");
    authenticate(user, "s d f", 2, "SECOND_FACTOR_AUTHENTICATED");

    // GET the county dashboard. This is just to test that the login worked.
    given().get("/county-dashboard");

    // Post the CVR file and its hash.
    Response response = given()
        .header("Content-Type", "multipart/form-data")
        .multiPart("file", cvrFile, "text/csv")
        .multiPart("hash", hashFile, "test/csv")
        .when()
        .post("/upload-file");

    // Request the CVR export to be imported
    given().body(response.body()).post("/import-cvr-export");

    // Logout.
    logout(user);
  }

  /**
   * Upload a ballot manifest file and its corresponding hash on behalf of the given county number.
   * @param number Number of the county uploading the ballot manifest/hash.
   * @param manifestFile Path of the ballot manifest file to be uploaded.
   * @param hashFile Path of the corresponding hash for the CVR file.
   */
  protected void manifestUploadCounty(final int number, final String manifestFile, final String hashFile){
    final String user = "countyadmin" + number;

    // Login as the county.
    authenticate(user, "", 1, "TRADITIONALLY_AUTHENTICATED");
    authenticate(user, "s d f", 2, "SECOND_FACTOR_AUTHENTICATED");

    // GET the county dashboard. This is just to test that the login worked.
    given().get("/county-dashboard");

    // Post the CVR file and its hash.
    Response response = given()
        .header("Content-Type", "multipart/form-data")
        .multiPart("file", manifestFile, "text/csv")
        .multiPart("hash", hashFile, "test/csv")
        .when()
        .post("/upload-file");

    // Request the CVR export to be imported
    given().body(response.body()).post("/import-ballot-manifest");

    // Logout.
    logout(user);
  }
}
