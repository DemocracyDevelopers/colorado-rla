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
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Generate Assertions endpoint. Takes a GenerateAssertionsRequest, and optional parameters specifying a contest
 * and time limit.
 * If no parameters are specified, it generates assertions for all IRV contests with a default time limit.
 * If a contest is specified, it generates assertions only for that contest.
 * If a time limit is specified, it uses that instead of the default.
 * Returns a list of all responses, which are the contest name together with the winner (if one was returned) and an
 * error (if there was one).
 * For example, hitting /generate-assertions?contest="Boulder Mayoral",timeLimitSeconds=5
 * will, if successful, produce a singleton list with a GenerateAssertionsResponseWithErrors containing "Boulder Mayoral"
 * and the winner.
 * Hitting /generate-assertions with no parameters will produce a list of GenerateAssertionsResponseWithErrors, one for
 * each IRV contest, each containing a nonempty winner or a nonempty error (in some cases, there may be both a winner
 * and an error/warning).
 * If the raire service endpoint returns a 4xx error, this throws a RuntimeException.
 */
public class GenerateAssertions extends AbstractAllIrvEndpoint {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(GenerateAssertions.class);

  /**
   * Query specifier for time limit.
   */
  private static final String TIME_LIMIT = "timeLimitSeconds";

  /**
   * Query specifier for contest name.
   */
  private static final String CONTEST_NAME = "contestName";

  /**
   * Default time limit for assertion generation.
   */
  private static final String DEFAULT_TIME_LIMIT = "5";

  /**
   * Default winner to be used in the case where winner is unknown.
   */
  private static final String UNKNOWN_WINNER = "Unknown";

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/generate-assertions";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    final String prefix = "[endpointBody]";

    final List<GenerateAssertionsResponseWithErrors> responseData;

    final String raireUrl = Main.properties().getProperty(RAIRE_URL, "") + RAIRE_ENDPOINT;

    // If a time limit was specified, use that, otherwise use the default.
    final int timeLimitSeconds = Integer.parseInt(the_request.queryParamOrDefault(TIME_LIMIT, DEFAULT_TIME_LIMIT));

    // If a contest was requested in the query parameter, generate assertions only for that contest.
    // Otherwise, generate them for all IRV contests.
    final String contestName = the_request.queryParamOrDefault(CONTEST_NAME, "");

    // Get all the IRV contest results.
    final List<ContestResult> IRVContestResults = AbstractAllIrvEndpoint.getIRVContestResults();

    // try {
    if (contestName.isBlank()) {
      // No contest was requested - generate for all.
      responseData = generateAllAssertions(IRVContestResults, timeLimitSeconds, raireUrl);
    } else {
      responseData = List.of(generateAssertionsUpdateWinners(IRVContestResults, contestName, timeLimitSeconds, raireUrl));
    }

    the_response.header("Content-Type", "application/json");

    okJSON(the_response, Main.GSON.toJson(responseData));
    return my_endpoint_result.get();

  }

  /**
   * Do the actual work of getting the assertions.
   * - Gather all the IRVContestResults
   * - For each IRV contest, make a request to the raire-service get-assertions endpoint of the right format type
   * - Collate all the results into a zip
   *
   * @param IRVContestResults the collection of all IRV ContestResults.
   * @param timeLimitSeconds  the time limit for raire assertion generation, per contest.
   * @param raireUrl          the url where the raire-service is running.
   */
  protected List<GenerateAssertionsResponseWithErrors> generateAllAssertions(List<ContestResult> IRVContestResults,
                                                                             int timeLimitSeconds, String raireUrl) {
    final String prefix = "[generateAllAssertions]";

    final List<GenerateAssertionsResponseWithErrors> responseData = new ArrayList<>();

    // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
    for (final ContestResult cr : IRVContestResults) {
      GenerateAssertionsResponseWithErrors response = generateAssertionsUpdateWinners(IRVContestResults, cr.getContestName(), timeLimitSeconds, raireUrl);
      responseData.add(response);
    }

    return responseData;
  }

  protected GenerateAssertionsResponseWithErrors generateAssertionsUpdateWinners(List<ContestResult> IRVContestResults,
                                                                                 String contestName, int timeLimitSeconds, String raireUrl) {
    final String prefix = "[generateAssertions]";

    try {
      final ContestResult cr = IRVContestResults.stream()
          .filter(r -> r.getContestName().equalsIgnoreCase(contestName)).findAny().orElseThrow();
      final List<String> candidates = cr.getContests().stream().findAny().orElseThrow().choices().stream()
          .map(Choice::name).toList();

      // Make the request.
      final GenerateAssertionsRequest generateAssertionsRequest = new GenerateAssertionsRequest(
          cr.getContestName(),
          cr.getBallotCount().intValue(),
          timeLimitSeconds,
          candidates
      );

      // Throws URISyntaxException or MalformedURLException if the raireUrl is invalid.
      final HttpPost requestToRaire = new HttpPost(new URL(raireUrl).toURI());
      requestToRaire.addHeader("content-type", "application/json");
      requestToRaire.setEntity(new StringEntity(Main.GSON.toJson(generateAssertionsRequest)));

      // Send it to the RAIRE service.
      final HttpResponse raireResponse = httpClient.execute(requestToRaire);
      LOGGER.debug(String.format("%s %s.", prefix, "Sent Assertion Request to Raire service for "
          + contestName));

      // Interpret the response.
      final int statusCode = raireResponse.getStatusLine().getStatusCode();
      GenerateAssertionsResponse responseFromRaire = Main.GSON.fromJson(EntityUtils.toString(raireResponse.getEntity()),
          GenerateAssertionsResponse.class);

      if (statusCode == HttpStatus.SC_OK) {
        // OK response. Update the stored winner and return it.

        LOGGER.debug(String.format("%s %s.", prefix, "OK response received from RAIRE for "
            + contestName));
        updateWinnersAndLosers(cr, candidates, responseFromRaire.winner);
        return new GenerateAssertionsResponseWithErrors(contestName, responseFromRaire.winner, "");

      } else if (raireResponse.containsHeader(RAIRE_ERROR_CODE)) {
        // Error response about a specific contest, e.g. "NO_ASSERTIONS_PRESENT".
        // Return the error, record it.

        final String code = raireResponse.getFirstHeader(RAIRE_ERROR_CODE).getValue();
        LOGGER.debug(String.format("%s %s %s.", prefix, "Error response " + code,
            "received from RAIRE for " + contestName));

        updateWinnersAndLosers(cr, candidates, UNKNOWN_WINNER);
        return new GenerateAssertionsResponseWithErrors(cr.getContestName(), UNKNOWN_WINNER, code);

      } else {
        // Something went wrong with the connection. Cannot continue.

        final String msg = "Connection failure with Raire service. Http code "
            + statusCode + ". Check the configuration of Raire service url.";
        LOGGER.error(String.format("%s %s", prefix, msg));
        throw new RuntimeException(msg);
      }
    } catch (URISyntaxException | MalformedURLException e) {
      final String msg = "Bad configuration of Raire service url: " + raireUrl + ". Check your config file.";
      LOGGER.error(String.format("%s %s %s", prefix, msg, e.getMessage()));
      throw new RuntimeException(msg);
    } catch (NoSuchElementException | NullPointerException e) {
      final String msg = "Non-existent or non-IRV contest in Generate Assertions request:";
      LOGGER.error(String.format("%s %s %s %s", prefix, msg, contestName, e.getMessage()));
      throw new RuntimeException(msg + contestName);
    } catch (JsonSyntaxException e) {
      final String msg = "Error interpreting Raire response for contest ";
      LOGGER.error(String.format("%s %s %s %s", prefix, msg, contestName, e.getMessage()));
      throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
      final String msg = "Error generating request to Raire for contest ";
      LOGGER.error(String.format("%s %s %s %s", prefix, msg, contestName, e.getMessage()));
      throw new RuntimeException(msg + contestName + e.getMessage());
    } catch (ClientProtocolException e) {
      final String msg = "Error sending request to Raire for contest ";
      LOGGER.error(String.format("%s %s %s %s", prefix, msg, contestName, e.getMessage()));
      throw new RuntimeException(msg + contestName + e.getMessage());
    } catch (IOException e) {
      final String msg = "I/O error during generate assertions attempt for contest ";
      LOGGER.error(String.format("%s %s %s %s", prefix, msg, contestName, e.getMessage()));
      throw new RuntimeException(msg + contestName + e.getMessage());
    }
  }

  /**
   * Update the contestresults in the database according to RAIRE's assessed winners. Set all
   * non-winners to be losers, which means all candidates if the contest is un-auditable.
   *
   * @param cr         the contest result, i.e. aggregaged (possibly cross-county) IRV contest.
   * @param candidates the list of candidate names.
   * @param winner     the winner, as determined by raire.
   * TODO This is currently non-functional - see Issue #136 <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/136">...</a>
   */
  private void updateWinnersAndLosers(ContestResult cr, List<String> candidates, String winner) {
    cr.setWinners(Set.of(winner));
    cr.setLosers(candidates.stream().filter(c -> !c.equalsIgnoreCase(winner)).collect(Collectors.toSet()));
  }
}

