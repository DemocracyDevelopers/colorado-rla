/*
 * Sketch of Assertion Generation endpoint
 *
 */

package us.freeandfair.corla.endpoint;

import au.org.democracydevelopers.raire.assertions.AssertionAndDifficulty;
import au.org.democracydevelopers.raire.assertions.NotEliminatedBefore;
import au.org.democracydevelopers.raire.assertions.NotEliminatedNext;
import au.org.democracydevelopers.raire.assertions.RaireAssertion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.raire.request.GenerateAssertionRequestDto;
// import us.freeandfair.corla.raire.response.AssertionPermutations;
//import us.freeandfair.corla.raire.response.AssertionResult;
// import us.freeandfair.corla.raire.response.RaireResponse;
import us.freeandfair.corla.persistence.Persistence;

import static us.freeandfair.corla.query.CastVoteRecordQueries.getMatching;

import au.org.democracydevelopers.raire.RaireSolution;

/**
 * Generates assertions by: collecting the set of contests (by ID) for which assertions should ; be
 * generated; calling the RAIRE service to form assertions for those contests; storing the generated
 * assertions (returned from the RAIRE service in JSON) into the database.
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
      // TODO: Deal appropriately with the case where no raire_url is set.
      final Client client = ClientBuilder.newClient();
      var raire_url = Main.properties().getProperty(RAIRE_URL, "");
      WebTarget webTarget = client.target(raire_url);

      ObjectMapper objectMapper = new ObjectMapper();

      final Map<String, ContestResult> IRVContestResults = getIRVContestResults();

      List<RaireSolution> raireResponses = new ArrayList<>();

      // Build the request to RAIRE.
      // cr.getBallotCount() is the correct universe size here, because it represents the total number of ballots
      // (cards) cast in all counties that include this contest.
      IRVContestResults.values().forEach(cr -> {

        // build the RAIRE request for this IRV contest.
        var candidatesList = getCandidates(cr);
        GenerateAssertionRequestDto assertionRequest =
                GenerateAssertionRequestDto.builder()
                        .contestName(cr.getContestName())
                        .timeProvisionForResult(COMPUTE_TIME)
                        .candidates(candidatesList)
                        .votes(getVotes(cr))
                        .totalAuditableBallots(Math.toIntExact(cr.getBallotCount()))
                        .build();

        // Send it to the RAIRE service.
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        var response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), RaireSolution.class);
        LOGGER.info("Sent Assertion Request to RAIRE: " + assertionRequest);
        LOGGER.info(response);

        // Get the response, add it to the list for download.
        RaireSolution raireResponse = objectMapper.convertValue(response, new TypeReference<RaireSolution>() {});
        raireResponses.add(raireResponse);

        // deserialize and store the assertions in the database.
        if(raireResponse.solution.Err == null) {
            assert raireResponse.solution.Ok != null;
            AssertionAndDifficulty[] assertionsWithDifficulty = raireResponse.solution.Ok.assertions;
            for(var assertionWD : assertionsWithDifficulty) {
              Assertion assertion = makeStoreable(assertionWD, cr, candidatesList) ;
              Persistence.save(assertion);
          }
        } else {
          // TODO : Figure out how to show errors to the user.

          LOGGER.info("Received error from RAIRE: " + raireResponse.solution.Err);
        }

      });

      Persistence.flushAndClear();

      // Return all the RAIRE responses to the endpoint.
      okJSON(the_response, Main.GSON.toJson(raireResponses));
    }
    catch(Exception e){
      LOGGER.error("Error in assertion generation", e);
      serverError(the_response, "Could not generate assertions.");
    }
    return my_endpoint_result.get();
  }


  /* Gets all the choice names (i.e. candidate names) for a given contest from the database.
   *
   */
  private List<String> getCandidates(ContestResult cr) {
    return cr.getContests().stream().flatMap(c -> c.choices().stream().map(Choice::name)).distinct().collect(Collectors.toList());
  }

  /* Gets all the votes for a given contest from the database, in a form that the RAIRE service can understand.
   *
   */
  private List<List<String>> getVotes(ContestResult c) {

    Set<Long> countyIDs = c.countyIDs();
    Stream<CastVoteRecord> CVRs = countyIDs.stream()
            .map(countyID -> getMatching(countyID, CastVoteRecord.RecordType.UPLOADED))
            .flatMap(s ->s);

    Stream<Optional<CVRContestInfo>> contestInfos = CVRs.map(cvr -> cvr.contestInfoForContestResult(c));

    // Only use the ones that are present for this contest.
    return contestInfos.flatMap(Optional::stream).map(CVRContestInfo::choices).collect(Collectors.toList());
  }

  // Convert the type of assertion received from RAIRE into the form colorado-rla needs to store in the database.
  private Assertion makeStoreable(AssertionAndDifficulty assertionWD, ContestResult cr, List<String> candidates) {
    String contestName = cr.getContestName();
    RaireAssertion assertion = assertionWD.assertion;

    if(assertionWD.assertion.getClass() == NotEliminatedBefore.class) {
      NotEliminatedBefore nebAssertion = (NotEliminatedBefore) assertion;
      String winnerName = candidates.get(nebAssertion.winner);
      String loserName = candidates.get(nebAssertion.loser);
      return new NEBAssertion(contestName, winnerName, loserName, assertionWD.margin, cr.getBallotCount(), assertionWD.difficulty);
    } else if (assertionWD.assertion.getClass() == NotEliminatedNext.class) {
      NotEliminatedNext nenAssertion = (NotEliminatedNext) assertion;
      String winnerName = candidates.get(nenAssertion.winner);
      String loserName = candidates.get(nenAssertion.loser);
      List<String> continuingByName = Arrays.stream(nenAssertion.continuing).mapToObj(candidates::get).collect(Collectors.toList());
      return new NENAssertion(contestName, winnerName, loserName, assertionWD.margin, cr.getBallotCount(),
              assertionWD.difficulty, continuingByName);
    } else {
      throw new IllegalStateException("Illegal Assertion: "+assertion);
    }
  }

  // Collects all the ContestResults for which all contests are IRV. Throws an exception if any ContestResults
  // have a mix of IRV and plurality.
  private Map<String, ContestResult> getIRVContestResults() {

    // Get the ContestResults grouped by Contest name - this will give us accurate universe sizes.
    final List<ContestResult> countedCRs = ContestCounter.countAllContests().stream().peek(cr ->
            cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS)).collect(Collectors.toList());

    final Map<String,ContestResult> IRVContestResults = new HashMap<>();

    for (ContestResult cr : countedCRs) {

      // If it's all IRV, keep it.
      if (cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.IRV.toString()))) {
        IRVContestResults.put(cr.getContestName(), cr);
        // It's not all IRV and not all plurality.
      } else if (! cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.PLURALITY.toString()))) {
        throw new RuntimeException("Contest "+cr.getContestName()+" has inconsistent plurality/IRV types.");
      }
    }

    return IRVContestResults;
  }
}
