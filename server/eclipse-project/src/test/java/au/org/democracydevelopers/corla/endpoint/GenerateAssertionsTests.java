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
import au.org.democracydevelopers.corla.communication.responseToColoradoRla.GenerateAssertionsResponseWithErrors;
import static au.org.democracydevelopers.corla.util.testUtils.*;
import au.org.democracydevelopers.corla.util.testUtils;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.*;

import java.util.List;
import java.util.Set;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the GetAssertions endpoint, both CSV and JSON versions. The response is supposed to be a zip file containing
 * the assertions.
 * TODO This really isn't a completely comprehensive set of tests yet. We also need:
 * - API testing
 * - Testing that the service throws appropriate exceptions if the raire service connection isn't set up properly.
 * - More thorough tests of assertion generation for known cases, e.g. examples from NSW and the
 *   Guide to Raire.
 * - Testing of input validity, particularly non-negative time limits, which is done by the endpoint and hence
 *   not included in these tests.
 * See <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/125">...</a>
 */
public class GenerateAssertionsTests {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(GenerateAssertionsTests.class);

  /**
   * Mock response for Boulder Mayoral '23
   */
  private final static GenerateAssertionsResponse boulderResponse
      = new GenerateAssertionsResponse(boulderMayoral, "Aaron Brockett");

  /**
   * Mock response for tinyExample1 contest
   */
  private final static GenerateAssertionsResponse tinyIRVResponse
      = new GenerateAssertionsResponse(tinyIRV, "Alice");

  /**
   * Request for Boulder Mayoral '23
   */
  private final static GenerateAssertionsRequest boulderRequest
      = new GenerateAssertionsRequest(boulderMayoral, bouldMayoralCount, 5,
          boulderMayoralCandidates.stream().map(Choice::name).toList());

  /**
   * Request for tinyExample1 contest
   */
  private final static GenerateAssertionsRequest tinyIRVRequest
      = new GenerateAssertionsRequest(tinyIRV, tinyIRVCount, 5,
      tinyIRVCandidates.stream().map(Choice::name).toList());

  /**
   * Corla endpoint to be tested.
   */
  private final GenerateAssertions endpoint = new GenerateAssertions();

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
   * Bad url, for testing we deal appropriately with the resulting error.
   */
  String badEndpoint = "/badUrl";

  /**
   * An endpoint that produces nonsense responses, i.e. valid json but not a valid
   * GenerateAssertionsResponse, for testing that we deal appropriately with the resulting error.
   */
  String nonsenseResponseEndpoint = "/raire/nonsense-generating-url";

  /**
   * An endpoint that produces nonsense/uninterpretable responses, i.e. not valid json, for testing
   * that we deal appropriately with the resulting error.
   */
  String invalidResponseEndpoint = "/raire/invalid-json-generating-url";

  /**
   * GSON for json interpretation.
   */
  private final static Gson gson = new Gson();

  /**
   * Initialise mocked objects prior to the first test.
   */
  @BeforeClass
  public void initMocks() {
    MockitoAnnotations.openMocks(this);

    boulderIRVContestResult.setAuditReason(AuditReason.COUNTY_WIDE_CONTEST);
    boulderIRVContestResult.setBallotCount((long) bouldMayoralCount);
    boulderIRVContestResult.setWinners(Set.of("Aaron Brockett"));
    boulderIRVContestResult.addContests(Set.of(boulderMayoralContest));

    tinyIRVContestResult.setAuditReason(AuditReason.COUNTY_WIDE_CONTEST);
    tinyIRVContestResult.setBallotCount((long) tinyIRVCount);
    tinyIRVContestResult.setWinners(Set.of("Alice"));
    tinyIRVContestResult.addContests(Set.of(tinyIRVExample));

    // Default raire server. You can instead run the real raire service and set baseUrl accordingly,
    // though the tests of invalid/uninterpretable data will fail.
    wireMockRaireServer.start();
    baseUrl = wireMockRaireServer.baseUrl();
    String badUrl = baseUrl + badEndpoint;
    configureFor("localhost", wireMockRaireServer.port());
    // Mock a proper response to the Boulder Mayoral '23 contest.
    stubFor(post(urlEqualTo(raireGenerateAssertionsEndpoint))
        .withRequestBody(equalToJson(gson.toJson(boulderRequest)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withHeader("Content-Type", "application/json")
            .withBody(gson.toJson(boulderResponse))));
    // Mock a proper response to the IRV TinyExample1 contest.
    stubFor(post(urlEqualTo(raireGenerateAssertionsEndpoint))
                .withRequestBody(equalToJson(gson.toJson(tinyIRVRequest)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withHeader("Content-Type", "application/json")
            .withBody(gson.toJson(tinyIRVResponse))));
    // Mock a 404 for badUrl.
    stubFor(post(urlEqualTo(badUrl))
        .withRequestBody(equalToJson(gson.toJson(tinyIRVRequest)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_NOT_FOUND)));
    // Mock an OK but invalid response from the nonsense endpoint.
    // This is just a list of candidates, which should not make sense as a response.
    stubFor(post(urlEqualTo(nonsenseResponseEndpoint))
        .withRequestBody(equalToJson(gson.toJson(tinyIRVRequest)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withHeader("Content-Type", "application/json")
            .withBody(gson.toJson(tinyIRVCandidates))));
    // Mock an OK response with invalid json.
    stubFor(post(urlEqualTo(invalidResponseEndpoint))
        .withRequestBody(equalToJson(gson.toJson(tinyIRVRequest)))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SC_OK)
            .withHeader("Content-Type", "application/json")
            .withBody("This isn't valid json")));
  }

  @AfterClass
  public void closeMocks() {
    wireMockRaireServer.stop();
  }

  /**
   * Calls the single-contest version of the endpoint for the boulder Mayor '23 example , checks
   * for the right winner.
   */
  @Test
  public void rightBoulderIRVWinner() {
    testUtils.log(LOGGER, "rightBoulderIRVWinner");

    GenerateAssertionsResponseWithErrors result = endpoint.generateAssertionsUpdateWinners(
        mockedIRVContestResults, boulderRequest.contestName, boulderRequest.timeLimitSeconds,
        baseUrl + raireGenerateAssertionsEndpoint);

    assertEquals(result.contestName, boulderMayoral);
    assertEquals(result.winner, "Aaron Brockett");
  }

  /**
   * Calls the generateAssertions main endpoint function and checks that the right winners are
   * returned, for the two example contests (Boulder and TinyIRV).
   */
  @Test
  public void rightWinners() {
    testUtils.log(LOGGER, "rightWinners");

    List<GenerateAssertionsResponseWithErrors> results = endpoint.generateAllAssertions(mockedIRVContestResults, 5,
        baseUrl + raireGenerateAssertionsEndpoint);

    assertEquals(results.size(), 2);
    assertEquals(results.get(0).contestName, boulderMayoral);
    assertEquals(results.get(0).winner, "Aaron Brockett");
    assertEquals(results.get(1).contestName, tinyIRV);
    assertEquals(results.get(1).winner, "Alice");
  }

  /**
   * A nonexistent contest causes an appropriate error message.
   * (The requested contest does not appear in the mockedIRVContestResults.)
   */
  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = ".*Non-existent or non-IRV contest.*")
    public void nonExistentContestThrowsRuntimeException() {
    testUtils.log(LOGGER, "nonExistentContestThrowsRuntimeException");
    
    endpoint.generateAssertionsUpdateWinners(mockedIRVContestResults, nonExistentContest, 5,
      baseUrl + raireGenerateAssertionsEndpoint);
  }

  /**
   * When raire sends an uninterpretable response, an appropriate error message appears.
   * This tests a response that is not valid json.
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ".*Error interpreting Raire response for contest.*")
  public void uninterpretableRaireResponseThrowsRuntimeException() {
    testUtils.log(LOGGER, "uninterpretableRaireResponseThrowsRuntimeException");

    endpoint.generateAssertionsUpdateWinners(mockedIRVContestResults, tinyIRV, 5,
        baseUrl + invalidResponseEndpoint);
  }

  /**
   * When raire sends an unexpected response, an appropriate error message appears.
   * This tests a response that is valid json, but not the json we were expecting.
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ".*Error interpreting Raire response for contest.*")
  public void unexpectedRaireResponseThrowsRuntimeException() {
    testUtils.log(LOGGER, "unexpectedRaireResponseThrowsRuntimeException");

    endpoint.generateAssertionsUpdateWinners(mockedIRVContestResults, tinyIRV, 5,
        baseUrl + nonsenseResponseEndpoint);
  }


  /**
   * When given a bad endpoint, a runtime exception is thrown with an appropriate error message.
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ".*Connection failure.*Raire service url.*")
  public void badEndpointThrowsRuntimeException() {
    testUtils.log(LOGGER, "badEndpointThrowsRuntimeException");

    String badUrl = baseUrl + badEndpoint;
    endpoint.generateAllAssertions(mockedIRVContestResults, 5, badUrl);
  }

  /**
   * When given a bad url, an appropriate url-parsing error appears.
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ".*Bad configuration of Raire service url.*")
  public void badUrlThrowsUrlException() {
    testUtils.log(LOGGER, "badUrlThrowsUrlException");

    String url = "completelyNotAUrl" + "/badUrl";

    endpoint.generateAllAssertions(mockedIRVContestResults, 5, url);
  }

  /**
   * When given a bad url, an appropriate url-parsing error appears.
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = ".*Bad configuration of Raire service url.*")
  public void nonExistentContestThrowsException() {
    testUtils.log(LOGGER, "badUrlThrowsUrlException");

    String url = "completelyNotAUrl" + "/badUrl";
    endpoint.generateAllAssertions(mockedIRVContestResults, 5, url);
  }


}