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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.org.democracydevelopers.corla.communication.requestToRaire.GetAssertionsRequest;
import au.org.democracydevelopers.corla.communication.responseFromRaire.RaireServiceErrors;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.util.SparkHelper;

import static au.org.democracydevelopers.corla.endpoint.GenerateAssertions.UNKNOWN_WINNER;

/**
 * The Get Assertions endpoint. Takes a GetAssertionsRequest, and an optional format parameter specifying CSV or JSON,
 * defaulting to json. Returns a zip of all assertions for all IRV contests, in the requested format.
 * For example, hitting /get-assertions?format=csv will produce a zip of all the assertions exported in csv format;
 * omitting the query parameter, or requesting anything other than csv, will produce a zip of all the assertions in
 * json format.
 * If the raire service returns a specific error for a particular contest, e.g. NO_ASSERTIONS_PRESENT, a file is made
 * for that contest containing the error string, which is then included in the zip.
 * If the raire service endpoint returns a 4xx error, this throws a RuntimeException.
 */
public class GetAssertions extends AbstractAllIrvEndpoint {

    /**
     * Class-wide logger
     */
    private static final Logger LOGGER = LogManager.getLogger(GetAssertions.class);

    /**
     * RAIRE service get assertions endpoint.
     */
    protected static final String RAIRE_ENDPOINT = "/raire/get-assertions";

    /**
     * RAIRE service suffix for csv.
     */
    private static final String CSV_SUFFIX = "csv";

    /**
     * RAIRE service suffix for json.
     */
    private static final String JSON_SUFFIX = "json";

    /**
     * String for "format" query parameter (value will be csv or json).
     */
    private static final String FORMAT_PARAM = "format";

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
        return "/get-assertions";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String endpointBody(final Request the_request, final Response the_response) {
        final String prefix = "[endpointBody]";

        final String raireUrl = Main.properties().getProperty(RAIRE_URL, "") + RAIRE_ENDPOINT;
        String suffix;

        // If csv was requested in the query parameter, hit the get-assertions-csv endpoint; default to json.
        String format = the_request.queryParamOrDefault(FORMAT_PARAM, JSON_SUFFIX);
        if (CSV_SUFFIX.equalsIgnoreCase(format)) {
            suffix = CSV_SUFFIX;
        } else {
            suffix = JSON_SUFFIX;
        }

        // Use the DoS Dashboard to get the risk limit; default to 0 if none is specified.
        // This is a safe default because the true risk limit cannot be smaller.
        BigDecimal riskLimit = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class).auditInfo().riskLimit();
        riskLimit = riskLimit == null ? BigDecimal.ZERO : riskLimit;

        try {

            final ZipOutputStream os = new ZipOutputStream(SparkHelper.getRaw(the_response).getOutputStream());
            getAssertions(os, riskLimit, raireUrl, suffix);

            the_response.header("Content-Type", "application/zip");
            the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.zip");

            ok(the_response);

        } catch (URISyntaxException | MalformedURLException e) {
            final String msg = "Bad configuration of raire-service url: " + raireUrl + ". Fix the config file.";
            LOGGER.error(String.format("%s %s %s", prefix, msg, e.getMessage()));
            serverError(the_response, msg);
        } catch (IOException e) {
            final String msg = "Error creating zip file.";
            LOGGER.error(String.format("%s %s %s", prefix, msg, e.getMessage()));
            serverError(the_response, msg);
        }

        return my_endpoint_result.get();
    }

    /**
     * Do the actual work of getting the assertions.
     * - Gather all the IRVContestResults
     * - For each IRV contest, make a request to the raire-service get-assertions endpoint of the right format type
     * - Collate all the results into a zip
     * @param zos an output stream (to become a zip file)
     * @param riskLimit the risk limit
     * @param raireUrl the url where the raire-service is running
     * @param suffix requested file type: "csv" or "json"
     */
    public void getAssertions(final ZipOutputStream zos, final BigDecimal riskLimit, String raireUrl, String suffix)
            throws IOException, URISyntaxException {
        final String prefix = "[getAssertions]";

        // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
        // collating the responses.
        final List<ContestResult> IRVContestResults = AbstractAllIrvEndpoint.getIRVContestResults();
        final List<ContestResult> correctWinnersIRVContestResults = IRVContestResults.stream().map(AbstractAllIrvEndpoint::updateIRVContestWinner).toList();
        for (final ContestResult cr : correctWinnersIRVContestResults) {

            // Find the winner (there should only be one), candidates and contest name.
            final String winner = cr.getWinners().stream().findAny().orElse(UNKNOWN_WINNER);
            final List<String> candidates = cr.getContests().stream().findAny().orElseThrow().choices().stream()
                    .map(Choice::name).toList();

            // Remove non-word characters for saving into .zip file; set up the zip next entry.
            final String sanitizedContestName = cr.getContestName().replaceAll("[\\W]", "");
            zos.putNextEntry(new ZipEntry(sanitizedContestName + "_assertions." + suffix));

            // Make the request.
            final GetAssertionsRequest getAssertionsRequest = new GetAssertionsRequest(
                    cr.getContestName(),
                    cr.getBallotCount().intValue(),
                    candidates,
                    winner,
                    riskLimit
            );

            // Throws URISyntaxException or MalformedURLException if the raireUrl is invalid.
            final HttpPost requestToRaire = new HttpPost(new URL(raireUrl + "-" + suffix).toURI());
            requestToRaire.addHeader("content-type", "application/json");
            requestToRaire.setEntity(new StringEntity(gson.toJson(getAssertionsRequest)));

            // Send it to the RAIRE service.
            final HttpResponse raireResponse = httpClient.execute(requestToRaire);
            LOGGER.debug(String.format("%s %s.", prefix, "Sent Assertion Request to Raire service for "
                    + getAssertionsRequest.contestName));

            final int statusCode = raireResponse.getStatusLine().getStatusCode();
            if(statusCode == HttpStatus.SC_OK) {
                // OK response. Put the file name and data into the .zip.

                LOGGER.debug(String.format("%s %s.", prefix, "OK response received from RAIRE for "
                        + getAssertionsRequest.contestName));
                IOUtils.copy(raireResponse.getEntity().getContent(), zos);

            } else if(raireResponse.containsHeader(RaireServiceErrors.ERROR_CODE_KEY)) {
                // Error response about a specific contest, e.g. "NO_ASSERTIONS_PRESENT".
                // Write the error into the zip file and continue.

                final String code = raireResponse.getFirstHeader(RaireServiceErrors.ERROR_CODE_KEY).getValue();
                LOGGER.debug(String.format("%s %s %s.", prefix, "Error response " + code, "received from RAIRE for "
                        + getAssertionsRequest.contestName));
                zos.write(code.getBytes(StandardCharsets.UTF_8));

            } else {
                // Something went wrong with the connection. Cannot continue.

                final String msg = "Bad response from Raire service for contest " + getAssertionsRequest.contestName
                        + ":" + statusCode + " " + raireResponse.getStatusLine().getReasonPhrase();
                LOGGER.error(String.format("%s %s", prefix, msg));
                throw new RuntimeException(msg);
            }

            zos.closeEntry();
        }

        // Return all the RAIRE responses to the endpoint as a zip file.
        zos.close();
    }
}
