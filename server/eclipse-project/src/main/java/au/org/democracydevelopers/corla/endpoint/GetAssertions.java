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
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.org.democracydevelopers.corla.raire.requestToRaire.GetAssertionsRequest;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
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
 */
public class GetAssertions extends AbstractDoSDashboardEndpoint {

    /**
     * Class-wide logger
     */
    public static final Logger LOGGER = LogManager.getLogger(GetAssertions.class);

    /**
     * Identify RAIRE service URL from config.
     */
    private static final String RAIRE_URL = "raire_url";

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
     * @return STATE authorization is necessary for this endpoint.
     */
    public AuthorizationType requiredAuthorization() {
        return AuthorizationType.STATE;
    }

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

        final Client client = ClientBuilder.newClient();
        final String raireUrl = Main.properties().getProperty(RAIRE_URL, "") + RAIRE_ENDPOINT;
        String suffix;

        // If csv was requested in the query parameter, hit the get-assertions-csv endpoint; default to json.
        String format = the_request.queryParamOrDefault(FORMAT_PARAM, JSON_SUFFIX);
        if (CSV_SUFFIX.equalsIgnoreCase(format)) {
            suffix = CSV_SUFFIX;
        } else {
            suffix = JSON_SUFFIX;
        }
        WebTarget webTarget = client.target(raireUrl + "-" + suffix);

        // Use the DoS Dashboard to get the risk limit.
        final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        final OutputStream os;
        try {
            os = SparkHelper.getRaw(the_response).getOutputStream();
            final ZipOutputStream zos = new ZipOutputStream(os);

            // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
            // collating the responses.
            final List<ContestResult> IRVContestResults = IRVContestCollector.getIRVContestResults();
            for (ContestResult cr : IRVContestResults) {
                //  IRVContestResults.forEach(cr -> {

                // Find the winner - there should only be one.
                // TODO At the moment, the winner isn't yet set properly - will be set in the GenerateAssertions Endpoint.
                // For now, tolerate > 1; later, check.
                String winner = cr.getWinners().stream().findAny().get();
                List<String> candidates = cr.getContests().stream().findAny().orElseThrow().choices().stream().map(Choice::name).toList();
                // Make the request.
                GetAssertionsRequest getAssertionsRequest = new GetAssertionsRequest(
                        cr.getContestName(),
                        cr.getBallotCount().intValue(),
                        candidates,
                        winner,
                        dosdb.auditInfo().riskLimit()
                );

                // Send it to the RAIRE service.
                Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
                var response = invocationBuilder.post(Entity.entity(getAssertionsRequest, MediaType.APPLICATION_JSON)
                        , String.class);
                LOGGER.info(String.format("%s %s", prefix, "Sent Assertion Request to RAIRE: " + getAssertionsRequest));
                LOGGER.info(response);

                // Put the response into the .zip.
                zos.putNextEntry(new ZipEntry(cr.getContestName() + "_assertions." + suffix));
                zos.write(response.getBytes());
                zos.closeEntry();

            }

            // Return all the RAIRE responses to the endpoint as a zip file.
            zos.close();
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        the_response.header("Content-Type", "application/zip");
        the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.zip");
        ok(the_response);
        return my_endpoint_result.get();
    }
}
