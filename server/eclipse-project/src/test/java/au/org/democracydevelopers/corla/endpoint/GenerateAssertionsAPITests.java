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
import au.org.democracydevelopers.corla.util.SparkRequestStub;
import au.org.democracydevelopers.corla.util.TestClassWithAuth;
import au.org.democracydevelopers.corla.util.testUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.HaltException;
import spark.Request;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.*;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.persistence.Persistence;

import javax.transaction.Transactional;
import java.util.*;

import static au.org.democracydevelopers.corla.endpoint.AbstractAllIrvEndpoint.RAIRE_URL;
import static au.org.democracydevelopers.corla.endpoint.GenerateAssertions.CONTEST_NAME;
import static au.org.democracydevelopers.corla.util.testUtils.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.endpoint.Endpoint.AuthorizationType.STATE;

/**
 * Test the GetAssertions endpoint via the API.
 * This currently tests that the assertion generation request is accepted and blocked in the right
 * circumstances.
 * TODO This really isn't a completely comprehensive set of tests yet.
 * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/125">...</a>
 */
public class GenerateAssertionsAPITests extends TestClassWithAuth {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(GenerateAssertionsAPITests.class);

  /**
   * Container for the mock-up database.
   */
  private final static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * The Generate Assertions endpoint.
   */
  private final GenerateAssertions endpoint = new GenerateAssertions();

  /**
   * Mock response for tinyExample1 contest
   */
  private final static GenerateAssertionsResponse tinyIRVResponse
      = new GenerateAssertionsResponse(tinyIRV, true, false);

  /**
   * Request for tinyExample1 contest
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
   * (Note the default of 8080 clashes with the raire-service default, so this is different.)
   */
  private final WireMockServer wireMockRaireServer = new WireMockServer(8110);

  /**
   * Base url - this is set up to use the wiremock server, but could be set here to wherever you have the
   * raire-service running to test with that directly.
   */
  private static String baseUrl;

  /**
   * The Properties that will be mocked in Main, specifically for the RAIRE_URL.
   */
  private static final Properties mockProperties = new Properties();

  /**
   * GSON for json interpretation.
   */
  private final static Gson gson = new Gson();

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(config);

    var s = Persistence.openSession();
    s.beginTransaction();

    final var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    // Used to initialize the database, particularly to set the ASM state to the DOS_INITIAL_STATE.
    ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");
  }

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocks() {

    // Mock successful auth as a state admin.
    MockitoAnnotations.openMocks(this);
    mockAuth("State test 1", 1L, STATE);

    tinyIRVContestResult.setAuditReason(AuditReason.COUNTY_WIDE_CONTEST);
    tinyIRVContestResult.setBallotCount((long) tinyIRVCount);
    tinyIRVContestResult.setWinners(Set.of("Alice"));
    tinyIRVContestResult.addContests(Set.of(tinyIRVExample));

    // Default raire server. You can instead run the real raire service and set baseUrl accordingly.
    // Of course you have to have appropriate contests in the database.
    wireMockRaireServer.start();
    baseUrl = wireMockRaireServer.baseUrl();
    configureFor("localhost", wireMockRaireServer.port());

    // Mock the above-initialized URL for the RAIRE_URL property in Main.
    mockProperties.setProperty(RAIRE_URL, baseUrl);

    // Mock a proper response to the IRV TinyExample1 contest.
    stubFor(post(urlEqualTo(raireGenerateAssertionsEndpoint))
        .withRequestBody(equalToJson(gson.toJson(tinyIRVRequest)))
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
   * Simple test that the assertion generation request is made when in
   * ASM initial state and ASM PARTIAL_AUDIT_INFO_SET states, and not in later states.
   */
  @Test
  @Transactional
  void assertionGenerationBlockedWhenInWrongASMState() {
    testUtils.log(LOGGER, "assertionGenerationBlockedWhenInWrongASMState");

    // Mock the main class; mock its auth as the mocked state admin auth.
    try (MockedStatic<Main> mockedMain = Mockito.mockStatic(Main.class);
         MockedStatic<ContestCounter> mockedCounter = Mockito.mockStatic(ContestCounter.class)) {

      // Mock auth.
      mockedMain.when(Main::authentication).thenReturn(auth);

      // Mock properties, particularly the RAIRE URL.
      mockedMain.when(Main::properties).thenReturn(mockProperties);

      // Mock non-empty contest response (one IRV contest).
      List<ContestResult> mockedContestResults = List.of(tinyIRVContestResult);
      mockedCounter.when(ContestCounter::countAllContests).thenReturn(mockedContestResults);

      // We seem to need a dummy request to run before.
      final Request request = new SparkRequestStub("", Map.of(CONTEST_NAME, tinyIRV));
      endpoint.before(request, response);

      // First test: check that the GenerateAssertions endpoint works when in the initial state
      // (which is set up initially in the database).

      DoSDashboardASM doSDashboardASM = ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);
      assertTrue(doSDashboardASM.isInInitialState());

      String errorBody = "";
      try {
        endpoint.endpointBody(request, response);
        endpoint.after(request, response);
      } catch (HaltException e) {
        errorBody = "Error: " + e.body();
      }
      // There should be no error when the ASM is in the initial state.
      assertEquals(errorBody, "");

      // Now transition to PARTIAL_AUDIT_INFO_SET
      doSDashboardASM.stepEvent(PARTIAL_AUDIT_INFO_EVENT);
      ASMUtilities.save(doSDashboardASM);

      errorBody = "";
      try {
        endpoint.endpointBody(request, response);
        endpoint.after(request, response);
      } catch (HaltException e) {
        errorBody = "Error: " + e.body();
      }
      // There should be still be no error when the ASM is in the PARTIAL_AUDIT_INFO_SET state.
      assertEquals(errorBody, "");

      final String expectedError = "Assertion generation not allowed in current state.";

      // Now transition to COMPLETE_AUDIT_INFO_SET and other, subsequent, states, in which
      // assertion generation is expected to throw an error. Check that it does.
      for (ASMEvent.DoSDashboardEvent event : List.of(
          COMPLETE_AUDIT_INFO_EVENT,
          DOS_START_ROUND_EVENT,
          AUDIT_EVENT,
          DOS_ROUND_COMPLETE_EVENT,
          // DoS can start another round after the earlier round is complete.
          DOS_START_ROUND_EVENT,
          DOS_COUNTY_AUDIT_COMPLETE_EVENT,
          DOS_AUDIT_COMPLETE_EVENT,
          PUBLISH_AUDIT_REPORT_EVENT
      )) {
        doSDashboardASM.stepEvent(event);
        ASMUtilities.save(doSDashboardASM);

        // In this state, assertion generation attempts should throw an error.
        errorBody = "";
        try {
          endpoint.endpointBody(request, response);
          endpoint.after(request, response);
        } catch (HaltException e) {
          errorBody = "Error: " + e.body();
        }
        assertTrue(errorBody.contains(expectedError));
      }
    }
  }
}