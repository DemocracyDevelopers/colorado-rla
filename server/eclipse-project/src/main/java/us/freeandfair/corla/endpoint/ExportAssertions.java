/*
 * Sketch of Export Assertions endpoint
 *
 */

package us.freeandfair.corla.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.query.AssertionQueries;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.util.SparkHelper;

/**
 *
 */
public class ExportAssertions extends AbstractDoSDashboardEndpoint {
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
            final List<Assertion> assertions = AssertionQueries.allAssertions();

            // Form a map with contest name as the key, and List<Assertion> as the value. The
            // map will split the assertions in the above list into their contests.
            final Map<String,List<Assertion>> contestAssertions = assertions.stream()
                    .collect(Collectors.groupingBy(Assertion::getContestName));

            the_response.header("Content-Type", "application/json");
            the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.json");
            final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
            os.write(Main.GSON.toJson(contestAssertions).getBytes(StandardCharsets.UTF_8));
            os.close();
            ok(the_response);

        } catch (final Exception e) {
            LOGGER.error("Error in assertion export", e);
            serverError(the_response, "Could not export assertions.");
        }

        return my_endpoint_result.get();
    }

}
