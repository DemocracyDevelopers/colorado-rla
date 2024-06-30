package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.ContestType;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.List;

/**
 * Utility class for collecting all the ContestResults for IRV contests - used by both get-assertions and
 * generate-assertions endpoints.
 */
public class IRVContestCollector {
    /**
     * Class-wide logger
     */
    private static final Logger LOGGER = LogManager.getLogger(IRVContestCollector.class);

    /**
     * Get all the ContestResults whose contests are consistently IRV.
     *
     * @return A list of all ContestResults for IRV contests.
     * @throws RuntimeException if it encounters contests with a mix of IRV and any other contest type.
     */
    public static List<ContestResult> getIRVContestResults() {
        final String prefix = "[getIRVContestResults]";
        final String msg = "Inconsistent contest types:";

        // Find all the ContestResults with any that match IRV.
        List<ContestResult> results = ContestCounter.countAllContests().stream()
                .peek(cr -> cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS))
                .filter(cr -> cr.getContests().stream().map(Contest::description)
                        .anyMatch(d -> d.equalsIgnoreCase(ContestType.IRV.toString()))).toList();

        // The above should be sufficient, but just in case, check that each contest we found _all_ matches IRV, and
        // throw a RuntimeException if not.
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
