/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire 
service (https://github.com/DemocracyDevelopers/raire-service), in order to 
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.endpoint;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.util.SparkHelper;


public class GetAssertions extends AbstractDoSDashboardEndpoint {

    /**
     * Class-wide logger
     */
    public static final Logger LOGGER = LogManager.getLogger(GetAssertions.class);

    /**
     * Identify RAIRE service URL from config.
     */
    private static final String RAIRE_URL = "raire_url";

    /**
     * RAIRE service endpoint name.
     */
    private static final String RAIRE_ENDPOINT = "/raire/get-assertions";

    /**
     * RAIRE service suffix for csv.
     */
    private static final String CSV_SUFFIX = "csv";

    /**
     * RAIRE service suffix for json.
     */
    private static final String JSON_SUFFIX = "json";

    /**
     * String for "format" query parameter (value will be csv or json).
     */
    private static final String FORMAT_PARAM = "format";

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
        return "/get-assertions";
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

        final Client client = ClientBuilder.newClient();
        final String raireUrl = Main.properties().getProperty(RAIRE_URL, "") + RAIRE_ENDPOINT;
        WebTarget webTarget;

        // If csv was requested in the query parameter, hit the get-assertions-csv endpoint; default to json.
        String format = the_request.queryParamOrDefault(FORMAT_PARAM, JSON_SUFFIX);
        if (CSV_SUFFIX.equalsIgnoreCase(format)) {
            webTarget = client.target(raireUrl + "-" + CSV_SUFFIX);
        } else {
            webTarget = client.target(raireUrl + "-" + JSON_SUFFIX);
        }

        // Use the DoS Dashboard to get the risk limit.
        final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        BigDecimal riskLimit = dosdb.auditInfo().riskLimit();

        // Iterate through all IRV Contests, sending a request to the raire-service for each one's assertions and
        // collating the responses.
        List<byte[]> raireResponses = new ArrayList<>();

        // Do stuff.


        // Return all the RAIRE responses to the endpoint as a zip file.
        // This is a little fiddly because we have the RetrievedRaireResponse as a string inside a more
        // complex json structure.
        the_response.header("Content-Type", "application/zip");
        the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.zip");
        final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();

        final ZipOutputStream zos = new ZipOutputStream(os);
        for (byte[] raireResponse : raireResponses) {
            zos.putNextEntry(new ZipEntry("GETTHECONTESTNAME AND SET THE SUFFIX" + "_assertions.json"));
            zos.write(raireResponse);
            zos.closeEntry();
        }

        zos.close();
        the_response.header("Content-Type", "application/zip");
        the_response.header("Content-Disposition", "attachment; filename*=UTF-8''assertions.zip");
        ok(the_response);
        return my_endpoint_result.get();
    }
}
