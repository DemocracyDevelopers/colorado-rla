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


/**
 *
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
    "PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity", "PMD.NPathComplexity",
    "PMD.ExcessiveImports", "PMD.TooManyStaticImports"})
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

      // TODO: flesh out required steps.
    return "";
  }

}
