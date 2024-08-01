package au.org.democracydevelopers.corla.endpoint;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.IRVComparisonAudit;
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
import us.freeandfair.corla.util.SparkHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeCsv;
import static us.freeandfair.corla.controller.ComparisonAuditController.createAuditOfCorrectType;

/**
 * Estimate Sample sizes.
 * This uses ComparisonAudit::estimatedSampleSize() to get the estimated samples for each contest.
 * It can be used either before or during an audit because it makes transient ComparisonAudits (not
 * stored in the database).
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
    final String prefix = "[endpointBody]";
    LOGGER.debug(String.format("%s %s", prefix, "Started Estimate Sample sizes."));

    try {
        final String data = estimateSampleSizes();

        if (data.isEmpty()) {
          final String msg = "Could not find data for sample size estimation.";

          LOGGER.debug(String.format("%s %s.", prefix, msg));
          dataNotFound(the_response, msg);
        } else {
          the_response.header("Content-Type", "test/csv");
          the_response.header("Content-Disposition", "attachment; filename*=UTF-8''sample_sizes.csv");
          final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
          os.write(((String.join(",", HEADERS)) + "\n" + data).getBytes(StandardCharsets.UTF_8));
          os.close();

          LOGGER.debug(String.format("%s %s.", prefix, "Completed sample size estimation"));
          ok(the_response);
        }

    } catch (final IOException e) {
      // Something went wrong with getting the Spark response or writing to the output stream.

      final String msg = "I/O Error in sample size estimation.";
      LOGGER.error(msg, e);
      serverError(the_response, msg);
    }

    return my_endpoint_result.get();
  }

  /**
   * Compute sample sizes for all contests for which CountyContestResults exist in the database.
   *
   * @return A list of string arrays containing rows with the following data: county name,
   * contest name, contest type, single or multi-jurisdictional, ballots cast, diluted margin,
   * and estimated sample size.
   */
  public String estimateSampleSizes() {
    List<estimateData> dataRows = new ArrayList<>();
    BigDecimal riskLimit;

    // For estimation of sample sizes for each audit, we need to collect the ContestResult
    // for each contest. For Plurality audits, this will involve tabulating the votes across
    // Counties for that contest. For preliminary sample size estimation, we assign
    // OPPORTUNISTIC_BENEFITS as the reason for each audit. The tabulated vote totals
    // in a ContestResult for an IRV contest will not be used. In the call to ContestCounter
    // (countAllContests), all persisted CountyContestResults will be accessed from the database,
    // grouped by contest, and accumulated into a single ContestResult.
    final List<ContestResult> countedCRs = ContestCounter.countAllContests().stream().peek(cr ->
        cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS)).toList();

    // Try to get the DoS Dashboard, which may contain the risk limit for the audit.
    riskLimit = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class).auditInfo().riskLimit();
    if (riskLimit == null) {
      // If the risk limit is not initialized, set the printed risk limit to zero. This is a
      // safe approximation because a zero risk limit cannot be met.
      // FIXME - throw an error.
      riskLimit = BigDecimal.ZERO;
    }

    // Iterate over all the contest results, getting relevant data into an estimateData record.
    for (ContestResult cr : countedCRs) {

      final ComparisonAudit ca = createAuditOfCorrectType(cr, riskLimit);
      final List<String> countyNames = ca.getCounties().stream().map(County::name).toList();

      // The total number of ballots cast that have that contest on it. This is equal to the sum
      // of my_contest_count in the county contest results for the contest.
      final int contestBallots = CountyContestResultQueries.withContestName(ca.getContestName())
          .stream().mapToInt(CountyContestResult::contestBallotCount).sum();

      dataRows.add(new estimateData(countyNames, ca, contestBallots, cr.getBallotCount()));
    }

    return String.join("\n", dataRows.stream().map(estimateData::toString).toList());
  }

  /**
   * A record to store the data we need in the report. Stores relevant data (in some cases after
   * retrieving it from more complex structures) and outputs it as a CSV row (toString()).
   * @param countyName            The name of the county.
   * @param contestName           The name of the contest.
   * @param contestType           The type, as a string, IRV or plurality.
   * @param contestBallots        The number of ballots that actually contained the contest.
   * @param totalAuditableBallots The total universe of ballots for this contest's audit.
   * @param dilutedMargin         The margin divided by the total Auditable ballots.
   * @param estimatedSamples      The estimated samples to audit.
   */
  private record estimateData(String countyName, String contestName, String contestType,
                              int contestBallots, long totalAuditableBallots, BigDecimal dilutedMargin,
                              int estimatedSamples) {

    // Construct the record by pulling relevant data out of the ComparisonAudit data structure.
    // If the county name is unique, we print it; if not, we just print "Multiple."
    public estimateData(List<String> countyNames, ComparisonAudit ca, int contestBallots, long totalBallots) {
      this(countyNames.size() == 1 ? countyNames.get(0) : "Multiple",
          ca.getContestName(),
          ca instanceof IRVComparisonAudit ? ContestType.IRV.toString() : ContestType.PLURALITY.toString(),
          contestBallots,
          totalBallots,
          ca.getDilutedMargin(),
          ca.estimatedSamplesToAudit()
      );
    }

    // Print the record as a csv row.
    // Headers:
    // "County", "Contest Name", "Contest Type", "Ballots Cast", "Total ballots", "Diluted Margin", "Sample Size"
    @Override
    public String toString() {
      return String.join(",",
          List.of(
              escapeCsv(countyName),
              escapeCsv(contestName),
              contestType,
              "" + contestBallots,
              "" + totalAuditableBallots,
              dilutedMargin.toString(),
              "" + estimatedSamples
          ));
    }
  }
}
