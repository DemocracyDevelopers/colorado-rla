/*
 * Sketch of Export Assertions endpoint
 *
 */

package us.freeandfair.corla.endpoint;

import au.org.democracydevelopers.raire.RaireSolution;
import au.org.democracydevelopers.raire.assertions.AssertionAndDifficulty;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.*;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.raire.requesttoraire.RequestByContestName;
import us.freeandfair.corla.raire.requestfromclient.RequestByContestNameOnly;
import us.freeandfair.corla.raire.responsefromraire.GetAssertionResponse;

/**
 *
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
        /*
        try {
            // Temporary/mock up of call to RAIRE service (needs improvement!)
            // TODO: Deal appropriately with the case where no raire_url is set or client fails to init.
            final Client client = ClientBuilder.newClient();
            var raire_url = Main.properties().getProperty(RAIRE_URL, "");
            WebTarget webTarget = client.target(raire_url);

            // Build the requests to RAIRE, using each IRV contest name as retrieved from the database.
            // FIXME Just a single test one for now.
            final RequestByContestNameOnly request =
                    Main.GSON.fromJson(the_request.body(), RequestByContestNameOnly.class);
            List<String> candidates = new ArrayList<>();
            BigDecimal riskLimit = BigDecimal.valueOf(0.03); // FIXME
            RequestByContestName assertionRequest = new RequestByContestName(
                    request.getContestName(),
                    candidates,
                    riskLimit
            );

            // Send it to the RAIRE service.
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            var response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), GetAssertionResponse.class);
            LOGGER.info("Sent Assertion Request to RAIRE: " + assertionRequest);
            LOGGER.info(response);


                // deserialize and store the assertions in the database.
                if(response.solution.Err == null) {
                    assert response.solution.Ok != null;
                    AssertionAndDifficulty[] assertionsWithDifficulty = response.solution.Ok.assertions;
                    for(var assertionWD : assertionsWithDifficulty) {
                        Assertion assertion = makeStoreable(assertionWD, assertionRequest.getContestName(),
                                assertionRequest.getTotalAuditableBallots(), assertionRequest.getCandidates()) ;
                        Persistence.save(assertion);
                    }
                } else {
                    // TODO : Discuss  how to show errors to the user.

                    LOGGER.info("Received error from RAIRE: " + response.solution.Err);
                }

            }

            Persistence.flushAndClear();

            // Return all the RAIRE responses to the endpoint.
            okJSON(the_response, Main.GSON.toJson(raireResponses));
        }
        catch(Exception e){
            LOGGER.error("Error in assertion generation", e);
            serverError(the_response, "Could not generate assertions.");
        }
         */
        return my_endpoint_result.get();
    }
}



