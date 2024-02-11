/*
 * Sketch of Export Assertions endpoint
 *
 */

package us.freeandfair.corla.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.OutputStream;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.raire.requesttoraire.RequestByContestName;
import us.freeandfair.corla.raire.responsefromraire.GetAssertionResponse;
import us.freeandfair.corla.util.SparkHelper;
import us.freeandfair.corla.util.IRVContestCollector;

/**
 * Finds all IRV contests by name, queries the raire-service and returns the assertions as json.
 */
public class ExportAssertions extends AbstractDoSDashboardEndpoint {
    /**
     * Class-wide logger
     */
    public static final Logger LOGGER = LogManager.getLogger(EstimateSampleSizes.class);

    /**
     * Identify RAIRE service URL from config.
     */
    private static final String RAIRE_URL = "raire_url";

    /**
     * Identify RAIRE service URL from config.
     */
    private static final String RAIRE_ENDPOINT = "/get-assertions";

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
        return "/export-assertions";
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
        try {
            // Temporary/mock up of call to RAIRE service (needs improvement!)
            // TODO: Deal appropriately with the case where no raire_url is set or client fails to init.
            final Client client = ClientBuilder.newClient();
            var raire_url = Main.properties().getProperty(RAIRE_URL, "")+RAIRE_ENDPOINT;
            WebTarget webTarget = client.target(raire_url);

            // Use the DoS Dashboard to get the risk limit.
            final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
            BigDecimal riskLimit = dosdb.auditInfo().riskLimit();

            // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
            // collating the responses.
            List<GetAssertionResponse> raireResponses = new ArrayList<>();
            final List<ContestResult> IRVContestResults = IRVContestCollector.getIRVContestResults();
            IRVContestResults.forEach(cr -> {

                        // Make the request.
                        List<String> candidates = new ArrayList<>(Stream.concat(cr.getWinners().stream(), cr.getLosers().stream()).collect(Collectors.toSet()));
                        String contestName = cr.getContestName();
                        RequestByContestName assertionRequest = new RequestByContestName(
                                contestName,
                                candidates,
                                riskLimit
                        );

                        // Send it to the RAIRE service.
                        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
                        var response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), GetAssertionResponse.class);
                        LOGGER.info("Sent Assertion Request to RAIRE: " + assertionRequest);
                        LOGGER.info(response);

                        // Collect the response.
                        raireResponses.add(response);

                    }
            );

            // Return all the RAIRE responses to the endpoint as a file.
            // This is a little fiddly because we have the RetrievedRaireResponse as a string inside a more
            // complex json structure.
            the_response.header("Content-Type", "application/json");
            the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.json");
            final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            String output = objectMapper.writeValueAsString(raireResponses);
            os.write(output.getBytes(StandardCharsets.UTF_8));
            os.close();
            ok(the_response);

        } catch (Exception e) {
            LOGGER.error("Error in assertion export", e);
            serverError(the_response, "Could not retrieve assertions.");
        }
        return my_endpoint_result.get();
    }
}



