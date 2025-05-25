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

package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.communication.requestToRaire.GenerateAssertionsRequest;
import au.org.democracydevelopers.corla.communication.responseFromRaire.GenerateAssertionsResponse;
import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import au.org.democracydevelopers.corla.util.testUtils;
import au.org.democracydevelopers.corla.workflows.Workflow;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

import static au.org.democracydevelopers.corla.endpoint.AbstractAllIrvEndpoint.RAIRE_URL;
import static au.org.democracydevelopers.corla.endpoint.GenerateAssertions.CONTEST_NAME;
import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;
import static au.org.democracydevelopers.corla.util.TestClassWithDatabase.generateAssertionsPortNumberString;
import static au.org.democracydevelopers.corla.util.TestClassWithDatabase.runSQLSetupScript;
import static au.org.democracydevelopers.corla.util.testUtils.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.endpoint.Endpoint.AuthorizationType.STATE;

/**
 * Test the GetAssertions endpoint via the API.
 * This currently tests that the assertion generation request is accepted and blocked in the right
 * circumstances. It doesn't exhaustively test _all_ possible states, but it tests that the transition
 * from allowed to blocked is made correctly.
 * This is only the most trivial kind of workflow, but it helps to inherit from Workflow to get access
 * to utility methods for auth etc.
 * TODO This really isn't a completely comprehensive set of tests yet.
 * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/125">...</a>
 */
public class GenerateAssertionsAPITests extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(GenerateAssertionsAPITests.class);

  /**
   * Mock response for tinyExample1 contest, which succeeded and is not worth retrying.
   */
  private final static GenerateAssertionsResponse tinyIRVResponse
      = new GenerateAssertionsResponse(tinyIRV, true, false);

  /**
   * Request for tinyExample1 contest, intended as a boring request with normal parameters.
   */
  private final static GenerateAssertionsRequest tinyIRVRequest
      = new GenerateAssertionsRequest(tinyIRV, tinyIRVCount, 5,
      tinyIRVCandidates.stream().map(Choice::name).toList());

  /**
   * Raire endpoint for getting assertions.
   */
  private final String raireGenerateAssertionsEndpoint = "/raire/generate-assertions";

  /**
   * Wiremock server for mocking the raire service.
   */
  private WireMockServer wireMockRaireServer;

  /**
   * GSON for json interpretation.
   */
  private final static Gson gson = new Gson();

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocksAndMainAndDB() {

    config = loadProperties();
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;

    // Set up default raire server on the port defined in test.properties.
    // You can instead run the real raire service and set baseUrl accordingly,
    // though some tests may fail, depending on whether you have
    // appropriate contests in the database.
    final int rairePort = Integer.parseInt(config.getProperty(generateAssertionsPortNumberString, ""));
    wireMockRaireServer = new WireMockServer(rairePort);
    wireMockRaireServer.start();

    String baseUrl = wireMockRaireServer.baseUrl();
    configureFor("localhost", wireMockRaireServer.port());

    // Set the above-initialized URL for the RAIRE_URL property in Main.
    // This config is used in runMainAndInitializeDBIfNeeded.
    config.setProperty(RAIRE_URL, baseUrl);
    final PostgreSQLContainer<?> postgres = TestClassWithDatabase.createTestContainer();
    runMainAndInitializeDBIfNeeded("GenerateAssertionsAPITests", Optional.of(postgres));

    // Load some IRV contests into the database.
    TestClassWithDatabase.runSQLSetupScript(postgres, "SQL/corla-three-candidates-ten-votes-plus-plurality.sql");

    // Mock a proper response to the IRV TinyExample1 contest.
    stubFor(post(urlEqualTo(raireGenerateAssertionsEndpoint))
        .withRequestBody(containing(tinyIRV))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withHeader("Content-Type", "application/json")
            .withBody(gson.toJson(tinyIRVResponse))));
  }

  @AfterClass
  public void closeMocks() {
    wireMockRaireServer.stop();
  }

  /**
   * Successful assertion generation for an IRV contest for which we've mocked a RAIRE response.
   * This tests both the case when the tinyIRV example is specifically requested, and also the case
   * where no contest is specified - these should produce the same result because there is only that
   * one IRV contest in the database.
   */
  @Test
  @Transactional
  void assertionGenerationSucceedsOnRaireMockedIRV() {

    SessionFilter session = doLogin("stateadmin1");

    Response response = generateAssertionsCorla(session, Optional.of(tinyIRV), Optional.empty());
    assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
    GenerateAssertionsResponse[] gr1 = response.getBody().as(GenerateAssertionsResponse[].class);
    assertEquals(gr1.length, 1);
    assertTrue(gr1[0].succeeded);

    response = generateAssertionsCorla(session, Optional.empty(), Optional.empty());
    assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
    GenerateAssertionsResponse[] gr2 = response.getBody().as(GenerateAssertionsResponse[].class);
    // Should be the same result as if we'd requested the one contest.
    assertEquals(gson.toJson(gr2[0]), gson.toJson(gr1[0]));

    logout(session, "stateadmin1");
  }

  /**
   * An error response is returned for a plurality contest.
   */
  @Test
  void assertionGenerationErrorOnPluralityExample() {

    SessionFilter session = doLogin("stateadmin1");

    Response response = generateAssertionsCorla(session, Optional.of("PluralityExample1"), Optional.empty());
    assertEquals(response.getStatusCode(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
    String s = response.getBody().asString();
    assertTrue(StringUtils.containsIgnoreCase(s, "Non-existent or non-IRV"));

    logout(session, "stateadmin1");

  }

  /**
   * An error response is returned for a non-existent contest.
   */
  @Test
  void assertionGenerationErrorOnNonExistentExample() {

    SessionFilter session = doLogin("stateadmin1");

    Response response = generateAssertionsCorla(session, Optional.of("badContest"), Optional.empty());
    assertEquals(response.getStatusCode(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
    String s = response.getBody().asString();
    assertTrue(StringUtils.containsIgnoreCase(s, "Non-existent or non-IRV"));

    logout(session, "stateadmin1");
  }

  /**
   * Simple test that the assertion generation request is made to raire when in
   * ASM initial state and ASM PARTIAL_AUDIT_INFO_SET states, and not in later states.
   * This does not exhaustively test all later states, just the two after the allowed states.
   */
  @Test
  @Transactional
  void assertionGenerationBlockedWhenInWrongASMState() {
    testUtils.log(LOGGER, "assertionGenerationBlockedWhenInWrongASMState");

    SessionFilter session = doLogin("stateadmin1");

    // First test: check that the GenerateAssertions endpoint works when in the initial state
    // (which is set up initially in the database).

    // There should be no error when the ASM is in the initial state.
    Response response = generateAssertionsCorla(session, Optional.empty(), Optional.empty());
    int status = response.getStatusCode();
    assertEquals(status, HttpStatus.SC_OK);

    // Now set all the audit info - transition to PARTIAL_AUDIT_INFO_SET
    updateAuditInfo("src/test/resources/CSVs/AdamsAndAlamosa/adams-and-alamosa-canonical-list.csv",
        BigDecimal.valueOf(0.03));

    // There should be still be no error when the ASM is in the PARTIAL_AUDIT_INFO_SET state.
    response = generateAssertionsCorla(session, Optional.empty(), Optional.empty());
    status = response.getStatusCode();
    assertEquals(status, HttpStatus.SC_OK);

    // Now transition to COMPLETE_AUDIT_INFO_SET and other, subsequent, states, in which
    // assertion generation is expected to throw an error. Check that it does.
    setSeed("123412341234123412341234");

    // There should now be an error.
    response = generateAssertionsCorla(session, Optional.empty(), Optional.empty());
    status = response.getStatusCode();
    assertEquals(status, HttpStatus.SC_FORBIDDEN);

    // Start the audit round, try again; should be another error.
    response = generateAssertionsCorla(session, Optional.empty(), Optional.empty());
    status = response.getStatusCode();
    assertEquals(status, HttpStatus.SC_FORBIDDEN);

  }
}