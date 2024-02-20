package us.freeandfair.corla.util;

import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ContestType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Collects all the IRV contests together.
 */
public class IRVContestCollector {

    /* Collects all the ContestResults for which all contests are IRV.
     * @return A list of ContestResults for the IRV contests.
     * Throws an exception if any ContestResults have a mix of IRV and plurality.
     */
    public static List<ContestResult> getIRVContestResults() {

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
                throw new RuntimeException("Contest "+cr.getContestName()+" has inconsistent plurality/IRV types.");
            }
        }

        return IRVContestResults;
    }
}
