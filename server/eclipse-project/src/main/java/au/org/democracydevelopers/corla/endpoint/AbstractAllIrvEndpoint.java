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

import au.org.democracydevelopers.corla.model.ContestType;
import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;
import us.freeandfair.corla.model.*;

import java.net.http.HttpClient;
import java.util.List;

/**
 * An abstract endpoint for communicating with raire. Includes all the information for collecting IRV contests
 * and making a request to raire, including the location of the raire service.
 * Used by GetAssertions and GenerateAssertions.
 */
public abstract class AbstractAllIrvEndpoint extends AbstractDoSDashboardEndpoint {

    /**
     * Class-wide logger
     */
    private static final Logger LOGGER = LogManager.getLogger(AbstractAllIrvEndpoint.class);

    /**
     * GSON, for serialising requests.
     */
    protected final static Gson gson = new Gson();

    /**
     * Identify RAIRE service URL from config.
     */
    protected static final String RAIRE_URL = "raire_url";

    /**
     * The httpClient used for making requests to the raire-service.
     */
    protected final static HttpClient httpClient = HttpClient.newHttpClient();


    /**
     * The event to return for this endpoint.
     */
    protected final ThreadLocal<ASMEvent> my_event = new ThreadLocal<>();

    /**
     * @return State admin authorization is necessary for this endpoint.
     */
    public AuthorizationType requiredAuthorization() { return AuthorizationType.STATE; }

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
     * Get all the ContestResults whose contests are consistently IRV.
     * @return A list of all ContestResults for IRV contests.
     * @throws RuntimeException if it encounters contests with a mix of IRV and any other contest type.
     */
    protected static List<ContestResult> getIRVContestResults() {
        final String prefix = "[getIRVContestResults]";
        final String msg = "Inconsistent contest types:";

        // Find all the ContestResults with any that match IRV.
        List<ContestResult> results = ContestCounter.countAllContests(true).stream()
                .filter(cr -> cr.getContests().stream().map(Contest::description)
                        .anyMatch(d -> d.equalsIgnoreCase(ContestType.IRV.toString()))).toList();

        // The above should be sufficient, but just in case, check that each contest we found _all_
        // matches IRV, and throw a RuntimeException if not.
        for (final ContestResult cr : results) {
            if (cr.getContests().stream().map(Contest::description)
                .anyMatch(d -> !d.equalsIgnoreCase(ContestType.IRV.toString()))) {
                LOGGER.error(String.format("%s %s %s", prefix, msg, cr.getContestName()));
                throw new RuntimeException(msg + cr.getContestName());
            }
        }

        return results;
    }
}
