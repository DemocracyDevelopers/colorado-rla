/*
 * Sketch of Assertion Generation endpoint
 *
 */

package us.freeandfair.corla.endpoint;

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
import us.freeandfair.corla.raire.response.AssertionPermutations;
import us.freeandfair.corla.raire.response.AssertionResult;
import us.freeandfair.corla.raire.response.AuditResponse;
import us.freeandfair.corla.raire.response.RaireResponse;
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
   * Class-wide logger
   */
  public static final String RAIRE_URL = "raire_url";

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
      // final Set<GenerateAssertionRequestDto> assertionRequest = new LinkedHashSet<>();
      List<RaireSolution> raireResponses = new ArrayList<>();

      // Build the request to RAIRE.
      // cr.getBallotCount() is the correct universe size here, because it represents the total number of ballots
      // (cards) cast in all counties that include this contest.
      IRVContestResults.values().forEach(cr -> {

        // build the RAIRE request for this IRV contest.
        GenerateAssertionRequestDto assertionRequest =
                GenerateAssertionRequestDto.builder()
                        .contestName(cr.getContestName())
                        .timeProvisionForResult(10)
                        .candidates(getCandidates(cr))
                        .votes(getVotes(cr))
                        .totalAuditableBallots(Math.toIntExact(cr.getBallotCount()))
                        .build();

        // Send it to the RAIRE service.
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        List generic = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), List.class);
        LOGGER.info("Sent Assertion Request to RAIRE: " + assertionRequest);
        LOGGER.info(generic);

        // Read the response and add it to the list of responses.
        RaireSolution raireResponse = objectMapper.convertValue(generic, new TypeReference<RaireSolution>() {
        });
        raireResponses.add(raireResponse);

    });

      // Iterate through all the responses and store the assertions in the database.
      raireResponses.forEach(auditResponse -> {
        RaireResponse result = auditResponse.getResult();
        Map<String, AssertionPermutations> solution = result.getSolution();
        AssertionPermutations assertionPermutations = solution.get("Ok");
        List<AssertionResult> assertions = assertionPermutations.getAssertions();
        List<String> candidates = auditResponse.getResult().getMetadata().getCandidates();
        Long universeSize = IRVContestResults.get(auditResponse.getContestName()).getBallotCount();
        assertions.forEach(assertionResult -> {
          Assertion assertion = assertionJSONToJava(assertionResult, candidates, auditResponse.getContestName(), universeSize);
          Persistence.save(assertion);
        });

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


  private Assertion assertionJSONToJava(AssertionResult assertionResult, List<String> candidates, String contestName, Long universeSize) {
    String winner = candidates.get(assertionResult.getAssertion().getWinner());
    String loser = candidates.get(assertionResult.getAssertion().getLoser());
    Integer[] continuing = assertionResult.getAssertion().getContinuing();
    Integer margin = assertionResult.getMargin();
    double difficulty = assertionResult.getDifficulty();

    Assertion assertion;

    // NEB assertions. There should be no 'assumed continuing' candidates.
    if (StringUtils.equalsIgnoreCase("NEB", assertionResult.getAssertion().getType()) &&
            (continuing == null || continuing.length == 0))  {
      assertion = new NEBAssertion(contestName, winner, loser, margin, universeSize, difficulty);
    }
    // NEN assertion. 'assumed continuing' should be non-null. Empty is fine.
    else if (StringUtils.equalsIgnoreCase("NEN", assertionResult.getAssertion().getType()) &&
            continuing != null) {
      List<String> continuingByName = Arrays.stream(continuing).map(candidates::get).collect(Collectors.toList());
      assertion = new NENAssertion(contestName, winner, loser, margin, universeSize, difficulty, continuingByName);
    } else {
      throw new IllegalStateException("Illegal Assertion: "+assertionResult.getAssertion());
    }
    return assertion;

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
