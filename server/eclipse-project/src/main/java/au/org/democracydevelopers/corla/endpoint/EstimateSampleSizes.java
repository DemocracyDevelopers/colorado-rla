package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.ContestType;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.endpoint.AbstractDoSDashboardEndpoint;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;

import java.math.BigDecimal;
import java.util.List;

import static us.freeandfair.corla.controller.ComparisonAuditController.createAuditOfCorrectType;

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
   * Headers for the CSV file. "Ballots cast" are the number that had the contest on them;
   * "total ballots" is the total universe size (which includes all the ballots in any county that
   * ran the contest).
   */
  private static final String[] HEADERS = {
          "County",
          "Contest Name",
          "Contest Type",
          "Ballots Cast",
          "Total ballots",
          "Diluted Margin",
          "Sample Size"
  };

    /**
     * {@inheritDoc}
     */
    @Override
    public String endpointBody(final Request the_request, final Response the_response) {
        return my_endpoint_result.get();
    }

     /**
   * Compute sample sizes for all contests for which CountyContestResults exist in the database.
   * @return A list of string arrays containing rows with the following data: county name,
   * contest name, contest type, single or multi-jurisdictional, ballots cast, diluted margin,
   * and estimated sample size.
   */
  public List<String[]> estimateSampleSizes(){
    // For estimation of sample sizes for each audit, we need to collect the ContestResult
    // for each contest. For Plurality audits, this will involve tabulating the votes across
    // Counties for that contest. For preliminary sample size estimation, we assign
    // OPPORTUNISTIC_BENEFITS as the reason for each audit. The tabulated vote totals
    // in a ContestResult for an IRV contest will not be used. In the call to ContestCounter
    // (countAllContests), all persisted CountyContestResult's will be accessed from the database,
    // grouped by contest, and accumulated into a single ContestResult.
    final List<ContestResult> countedCRs = ContestCounter.countAllContests().stream().peek(cr ->
            cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS)).toList();

    // Get the DoS Dashboard (will contain risk limit for audit).
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    final BigDecimal riskLimit = dosdb.auditInfo().riskLimit();

    for(ContestResult cr : countedCRs) {

        // TODO is this right?
        ComparisonAudit ca = createAuditOfCorrectType(cr, riskLimit);

        // The 'ballots cast' datapoint for a contest will be the total number of ballots cast
    // that have that contest on it. This is equal to the sum of my_contest_count in the
    // county contest results for the contest. VT: Is there a better way to do this - do we have the CountyContestResults already somewhere?
    final List<CountyContestResult> ccresults = CountyContestResultQueries.withContestName(ca.getContestName());
    final int contestBallots = ccresults.stream().mapToInt(CountyContestResult::contestBallotCount).sum();


        String countyName = "test";
        // TODO Throw something.
        estimateData dataRow = new estimateData(countyName, ca.getContestName(), cr.getContests().stream().findFirst().orElseThrow().description(), contestBallots, cr.getBallotCount(), ca.getDilutedMargin(), ca.estimatedSamplesToAudit() );
    }

    return String.join(",",EstimateSampleSizes.HEADERS) + "\n" +
           String.join("\n", dataRows.stream().map(estimateData::toString).toList());

    }

  private record estimateData(String countyName, String contestName, String contestType,
      int contestBallots, long totalAuditableBallots, BigDecimal dilutedMargin, int estimatedSamples) {
      @Override
            public String toString() {
         return "Testing"      ;
      }
  }
}
