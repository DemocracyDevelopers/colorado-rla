/*
 * Sketch of Assertion Generation endpoint
 *
 */

package au.org.democracydevelopers.endpoint;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;
import us.freeandfair.corla.model.*;
import au.org.democracydevelopers.raire.requesttoraire.CountyAndContestID;
import au.org.democracydevelopers.raire.requesttoraire.GenerateAssertionsRequest;

import au.org.democracydevelopers.raire.responsefromraire.GenerateAssertionsResponse;
import au.org.democracydevelopers.util.IRVContestCollector;

/**
 * Generates assertions by:
 *  - collecting the set of contests (by ID) for which assertions should be generated,
 *  - gathering their (countyID, contestID) pairs and other relevant data,
 *  - calling the RAIRE service to form assertions for those contests,
 *  - returning RAIRE's summary responses.
 */
public class GenerateAssertions extends AbstractDoSDashboardEndpoint {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(GenerateAssertions.class);

  /**
   * Identify RAIRE service URL from config.
   */
  private static final String RAIRE_URL = "raire_url";

  /**
   * Identify RAIRE service URL from config.
   */

  private static final String RAIRE_ENDPOINT = "/raire/generate-assertions";
  /**
   * The time given to RAIRE for timeouts.
   */
  private static final int COMPUTE_TIME = 10;

  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() { return EndpointType.GET; }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/generate-assertions";
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
      String raire_url = Main.properties().getProperty(RAIRE_URL, "")+RAIRE_ENDPOINT;
      WebTarget webTarget = client.target(raire_url);

      final List<ContestResult> IRVContestResults = IRVContestCollector.getIRVContestResults();

      List<GenerateAssertionsResponse> raireResponses = new ArrayList<>();

      // Build the requests to RAIRE.
      // cr.getBallotCount() is the correct universe size here, because it represents the total number of ballots
      // (cards) cast in all counties that include this contest.
      //
      // Note: We considered making this a parallel stream because the database access is by far the slowest part.
      // However, the database did not seem to respond well to multiple read attempts.
      List<GenerateAssertionsRequest> assertionRequests
        = IRVContestResults.stream().map(cr ->

                // build the RAIRE request for this IRV contest.
                        GenerateAssertionsRequest.builder()
                                .contestName(cr.getContestName())
                                .timeProvisionForResult(COMPUTE_TIME)
                                .candidates(getCandidates(cr))
                                .totalAuditableBallots(Math.toIntExact(cr.getBallotCount()))
                                .countyAndContestIDs(buildCountyAndContestIDList(cr))
                                .build()
              ).collect(Collectors.toList());

      for(GenerateAssertionsRequest assertionRequest : assertionRequests ) {
        // Send it to the RAIRE service.
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        var response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON),
                GenerateAssertionsResponse.class);
        LOGGER.info("Sent Assertion Request to RAIRE: " + assertionRequest);
        LOGGER.info("Received response: "+response);

        raireResponses.add(response);

        // deserialize and store the assertions in the database.

      }

      // Return all the RAIRE responses to the endpoint.
      okJSON(the_response, Main.GSON.toJson(raireResponses));
    }
    catch(Exception e){
      LOGGER.error("Error in assertion generation", e);
      serverError(the_response, "Could not generate assertions.");
    }
    return my_endpoint_result.get();
  }

  /**
   *
   * @param cr The Contest Result
   * @return   Its list of (countyID, contestID) pairs in a CountyAndContestID structure.
   */
  private List<CountyAndContestID> buildCountyAndContestIDList(ContestResult cr) {
    return cr.getContests().stream().map(contest ->
            new CountyAndContestID(contest.county().id(), contest.id())).collect(Collectors.toList());
  }

  /* Get all the choice names (i.e. candidate names) for a given contest from the database.
   * @param cr  The ContestResult for the contest
   * @return the list of choices (i.e. candidate names) for the contest.
   */
  private List<String> getCandidates(ContestResult cr) {
    return cr.getContests().stream().flatMap(c -> c.choices().stream().map(Choice::name)).distinct().collect(Collectors.toList());
  }
}
