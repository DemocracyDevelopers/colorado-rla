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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.org.democracydevelopers.corla.raire.requestToRaire.GetAssertionsRequest;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.util.SparkHelper;

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
public class GetAssertions extends AbstractDoSDashboardEndpoint {

    /**
     * Class-wide logger
     */
    private static final Logger LOGGER = LogManager.getLogger(GetAssertions.class);

    /**
     * GSON, for serialising requests.
     */
    Gson gson = new Gson();

    /**
     * Identify RAIRE service URL from config.
     */
    private static final String RAIRE_URL = "raire_url";

    /**
     * RAIRE error code key.
     */
    private static final String RAIRE_ERROR_CODE = "error_code";

    /**
     * RAIRE service endpoint name.
     */
    private static final String RAIRE_ENDPOINT = "/raire/get-assertions";

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
     * The httpClient used for making requests to the raire-service.
     */
    CloseableHttpClient httpClient = HttpClients.createDefault();


    /**
     * The event to return for this endpoint.
     */
    private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();

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
     * @return No authorization is necessary for this endpoint.
     */
    // TODO: Clarify whether this should be STATE or NONE.
    public AuthorizationType requiredAuthorization() { return AuthorizationType.NONE; }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ASMEvent endpointEvent() {
        return my_event.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        my_event.set(null);
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
            return my_endpoint_result.get();

        } catch (IOException e) {
            final String msg = "Error creating zip file.";
            LOGGER.error(String.format("%s %s", prefix, msg));
            throw new RuntimeException(e);
        }
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
    public void getAssertions(final ZipOutputStream zos, final BigDecimal riskLimit, String raireUrl, String suffix) throws IOException {
        final String prefix = "[getAssertions]";

        // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
        // collating the responses.
        final List<ContestResult> IRVContestResults = IRVContestCollector.getIRVContestResults();
        for (ContestResult cr : IRVContestResults) {
            //  IRVContestResults.forEach(cr -> {

            // Find the winner (there should only be one), candidates and contest name.
            // TODO At the moment, the winner isn't yet set properly - will be set in the GenerateAssertions Endpoint.
            // See https://github.com/DemocracyDevelopers/colorado-rla/issues/73
            // For now, tolerate > 1; later, check.
            String winner = cr.getWinners().stream().findAny().orElse("UNKNOWN");
            List<String> candidates = cr.getContests().stream().findAny().orElseThrow().choices().stream()
                    .map(Choice::name).toList();

            // Remove non-word characters for saving into .zip file; set up the zip next entry.
            String sanitizedContestName = cr.getContestName().replaceAll("[\\W]", "");
            zos.putNextEntry(new ZipEntry(sanitizedContestName + "_assertions." + suffix));

            // Make the request.
            GetAssertionsRequest getAssertionsRequest = new GetAssertionsRequest(
                    cr.getContestName(),
                    cr.getBallotCount().intValue(),
                    candidates,
                    winner,
                    riskLimit
            );
            // TODO possibly we should be checking whether a valid URL has been set before we call it.
            // Need to make sure that the exceptions we catch match the ones this will throw if that is wrong.
            // See https://github.com/orgs/DemocracyDevelopers/projects/1/views/1?pane=issue&itemId=68081868
            HttpPost requestToRaire = new HttpPost(raireUrl + "-" + suffix);
            requestToRaire.addHeader("content-type", "application/json");
            requestToRaire.setEntity(new StringEntity(gson.toJson(getAssertionsRequest)));

            // Send it to the RAIRE service.
            HttpResponse raireResponse = httpClient.execute(requestToRaire);
            LOGGER.debug(String.format("%s %s.", prefix, "Sent Assertion Request to Raire service for "
                    + getAssertionsRequest.contestName));

            int statusCode = raireResponse.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                // OK response. Put the file name and data into the .zip.

                LOGGER.debug(String.format("%s %s.", prefix, "OK response received from RAIRE for "
                        + getAssertionsRequest.contestName));
                IOUtils.copy(raireResponse.getEntity().getContent(), zos);

            } else if(raireResponse.containsHeader(RAIRE_ERROR_CODE)) {
                // Error response about a specific contest, e.g. "NO_ASSERTIONS_PRESENT".
                // Write the error into the zip file and continue.

                String code = raireResponse.getFirstHeader(RAIRE_ERROR_CODE).getValue();
                LOGGER.debug(String.format("%s %s %s.", prefix, "Error response " + code, "received from RAIRE for "
                        + getAssertionsRequest.contestName));
                zos.write(code.getBytes(StandardCharsets.UTF_8));

            } else {
                // Something went wrong with the connection. Cannot continue.

                final String msg = ("Bad response from Raire service: " + statusCode + ": "
                        +raireResponse.getStatusLine().getReasonPhrase());
                LOGGER.error(String.format("%s %s", prefix, msg));
                throw new RuntimeException(msg);
            }

            zos.closeEntry();
        }

        // Return all the RAIRE responses to the endpoint as a zip file.
        zos.close();
    }
}
