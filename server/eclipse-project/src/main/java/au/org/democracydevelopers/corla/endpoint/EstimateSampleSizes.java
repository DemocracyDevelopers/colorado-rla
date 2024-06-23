package au.org.democracydevelopers.corla.endpoint;

import spark.Request;
import spark.Response;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Non-functional stub.
 */
public class EstimateSampleSizes extends AbstractDoSDashboardEndpoint {

    /**
     * Class-wide logger
     */
    private static final Logger LOGGER = LogManager.getLogger(EstimateSampleSizes.class);

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
        return my_endpoint_result.get();
    }

}
