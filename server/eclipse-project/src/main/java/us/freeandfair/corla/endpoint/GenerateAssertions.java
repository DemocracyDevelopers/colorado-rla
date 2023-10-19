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
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.Assertion;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.NEBAssertion;
import us.freeandfair.corla.model.NENAssertion;
import us.freeandfair.corla.model.raire.request.GenerateAssertionRequestDto;
import us.freeandfair.corla.model.raire.response.AssertionPermutations;
import us.freeandfair.corla.model.raire.response.AssertionResult;
import us.freeandfair.corla.model.raire.response.AuditResponse;
import us.freeandfair.corla.model.raire.response.RaireResponse;
import us.freeandfair.corla.persistence.Persistence;


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
  public EndpointType endpointType() {
    return EndpointType.POST;
  }

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

    // The DoS Dashboard contains a set of contests to audit (not sure if this is how we will know
    // here what contests to generate assertions for).
    final Set<ContestToAudit> cta = dosdb.contestsToAudit();
//    if (cta.isEmpty()) {
//      //TODO we can't proceed further possibly throw exception
//      return "";
//    }
    //Looks like contestIds and Contest Names are 1 to 1 mapping based on the tables.
    final Set<String > contestNames = cta.stream().map(ContestToAudit::contest).map(Contest::name)
        .collect(Collectors.toSet());
    final Set<GenerateAssertionRequestDto> assertionRequest = new LinkedHashSet<>();

    contestNames.forEach(contestId -> assertionRequest.add(
                GenerateAssertionRequestDto.builder()
                    .contestName("IRV for Test County")
                    .timeProvisionForResult(10)
//                    .totalAuditableBallots() //TODO is it possible to get this here?
                    .build()));

    // Following is temporary code until we integrate and get real data for contest names
    //TODO start: remove
    assertionRequest.add(
        GenerateAssertionRequestDto.builder()
            .contestName("IRV for Test County")
            .timeProvisionForResult(10)
//                    .totalAuditableBallots() //TODO is it possible to get this here?
            .build());
    //TODO end: Remove

    final Client client = ClientBuilder.newClient();
    WebTarget webTarget
        = client.target("http://localhost:8080/cvr/audit");

    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

//    jakarta.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), Set.class);
    List generic = invocationBuilder.post(Entity.entity(assertionRequest, MediaType.APPLICATION_JSON), List.class);
    LOGGER.info("Test response");
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

    // NOTE that errors generated by RAIRE service (there are a series of types of errors) will need
    // to be handled appropriately).
    return "";
  }

}
