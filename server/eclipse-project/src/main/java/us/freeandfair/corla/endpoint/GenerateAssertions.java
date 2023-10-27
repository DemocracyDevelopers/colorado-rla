/*
 * Sketch of Assertion Generation endpoint
 *
 */

package us.freeandfair.corla.endpoint;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.ServerASMResponse;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.model.raire.request.GenerateAssertionRequestDto;
import us.freeandfair.corla.model.raire.response.AssertionPermutations;
import us.freeandfair.corla.model.raire.response.AssertionResult;
import us.freeandfair.corla.model.raire.response.AuditResponse;
import us.freeandfair.corla.model.raire.response.RaireResponse;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;


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
  /*
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
//    if (my_asm.get().currentState() != COMPLETE_AUDIT_INFO_SET) {
      // We can only generate assertions when CVRs have been provided for all the contests
      // that we want to generate assertions for.

      // For now, require the ASM to be in the COMPLETE_AUDIT_INFO_SET state.

      // Assertions can not yet be generated when the system is in this state.
//    }

    // Note that assertions may already exist in the database for various contests. In this case,
    // if we are generating a new set of assertions for a contests, the old ones should be replaced.

    // For a specified set of contest IDs, call the RAIRE service to generate assertions.
    // Assume we extract ArrayList<Long> contestIds = .... from request.

    // Task #48: Add example call to raire connector service here with contest identifiers as input.
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

    // The DoS Dashboard contains a set of contests to audit.
    // It's possible that this is the right way to decide which contests to audit for the audit step;
    // it's also possible that we should just generate the assertions for all contests anyway.
    // final Set<ContestToAudit> cta = dosdb.contestsToAudit();

    List<ContestResult> IRVContestResults = getIRVContestResults();

    final Set<GenerateAssertionRequestDto> assertionRequest = new LinkedHashSet<>();

    // Build the request to RAIRE.
    // cr.getBallotCount() is the correct universe size here, because it represents the total number of ballots
    // (cards) cast in all counties that include this contest.
    IRVContestResults.forEach(cr -> assertionRequest.add(
                GenerateAssertionRequestDto.builder()
                    .contestName(cr.getContestName())
                    .timeProvisionForResult(10)
                    .totalAuditableBallots(Math.toIntExact(cr.getBallotCount()))
                    .build()));


    final Client client = ClientBuilder.newClient();
    WebTarget webTarget
        = client.target("http://localhost:8080/cvr/audit");

    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

//    jakarta.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), Set.class);
    List generic = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), List.class);
    LOGGER.info("Sent Assertion Request to RAIRE: "+assertionRequest);
    LOGGER.info(generic);

    ObjectMapper objectMapper = new ObjectMapper();
    List<AuditResponse> raireResponse = objectMapper.convertValue(generic, new TypeReference<List<AuditResponse>>() { });

    raireResponse.stream().forEach(auditResponse -> {
      RaireResponse result = auditResponse.getResult();
      Map<String, AssertionPermutations> solution = result.getSolution();
      AssertionPermutations assertionPermutations = solution.get("Ok");
      List<AssertionResult> assertions = assertionPermutations.getAssertions();
      assertions.forEach(assertionResult -> {
        JsonNode candidates = auditResponse.getResult().getMetadata().get("candidates");
        Assertion assertion;
        if(StringUtils.equalsIgnoreCase("NEB", assertionResult.getAssertion().getType())) {
          assertion =  new NEBAssertion(auditResponse.getContestName(), candidates.get(assertionResult.getAssertion().getWinner()).asText(),
              candidates.get(assertionResult.getAssertion().getLoser()).asText(),
              assertionResult.getMargin(), 10, assertionResult.getDifficulty()); //TODO what is universeSize
          assertion.setContinuing(List.of("MOCK"));
        } else if(StringUtils.equalsIgnoreCase("NEN", assertionResult.getAssertion().getType())) {
          assertion = new NENAssertion();
        } else {
          throw new IllegalStateException("The audit resulted in an error state");
        }
        Persistence.save(assertion);
      });

    });
    Persistence.flushAndClear();

    // NOTE that for a single contest that involves multiple counties, there will be multiple
    // entries in the list 'cta' for that contest. Contests really need to be identified by their
    // name.

    // Create NEBAssertion and NENAssertion objects for the set of assertions identified by RAIRE.
    // (Compute diluted margins for all assertions to form that input to constructors).

    // Persist these assertion objects in the database.

    // NOTE that we will need to expand the ASM states/events to incorporate the state
    // in which assertions have been generated.
*/
    // NOTE that errors generated by RAIRE service (there are a series of types of errors) will need
    // to be handled appropriately).

  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    NENAssertion testAssertion = new NENAssertion("TestContest","Winner", "Loser", 5, 250, 0.123, List.of("Winner","Loser"));
    okJSON(the_response, Main.GSON.toJson(testAssertion));
    return my_endpoint_result.get();
  }

  // Collects all the ContestResults for which all contests are IRV. Throws an exception if any ContestResults
  // have a mix of IRV and plurality.
  private List<ContestResult> getIRVContestResults() {

    // Get the ContestResults grouped by Contest name - this will give us accurate universe sizes.
    final List<ContestResult> countedCRs = ContestCounter.countAllContests().stream().peek(cr ->
            cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS)).collect(Collectors.toList());

    final List<ContestResult> IRVContestResults = new ArrayList<>();

    for (ContestResult cr : countedCRs) {

      // If it's all IRV, keep it.
      if (cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.IRV.toString()))) {
        IRVContestResults.add(cr);
        // It's not all IRV and not all plurality.
      } else if (! cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.PLURALITY.toString()))) {
        throw new RuntimeException("EstimateSampleSizes: Contest "+cr.getContestName()+" has inconsistent plurality/IRV types.");
      }
    }

    return IRVContestResults;
  }
}
