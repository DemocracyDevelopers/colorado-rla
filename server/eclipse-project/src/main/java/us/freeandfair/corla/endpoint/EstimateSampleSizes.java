/*
 * Sketch of Sample Size Estimation endpoint
 *
 */

package us.freeandfair.corla.endpoint;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.COMPLETE_AUDIT_INFO_SET;


/**
 *
 */
public class EstimateSampleSizes extends AbstractDoSDashboardEndpoint {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(EstimateSampleSizes.class);

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
    return "/estimate-sample-sizes";
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
    if (my_asm.get().currentState() != COMPLETE_AUDIT_INFO_SET) {
      // We can only compute preliminary sample size estimates once the ASM has
      // reached the COMPLETE_AUDIT_INFO_SET state and assertions have been
      // generated for all IRV contests for which it is possible to form assertions.
      // We may create a new ASM state ASSERTIONS_GENERATED_OK (or similar) to indicate
      // when the system has completed this step, and associated events.

      // For now, require the ASM to be in the COMPLETE_AUDIT_INFO_SET state.
    }

    // We will most likely want to store these preliminary sample size estimates in the
    // DoS dashboard.
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

    // TODO: flesh out required steps.
    return "";
  }

}
