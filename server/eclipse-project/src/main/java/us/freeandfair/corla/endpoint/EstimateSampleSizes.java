/*
 * Sketch of Sample Size Estimation endpoint
 *
 */

package us.freeandfair.corla.endpoint;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.util.SparkHelper;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

  public static final String[] HEADERS = {
          "County",
          "Contest Name",
          "Contest Type",
          "Ballots Cast",
          "Diluted Margin",
          "Sample Size"
  };


  /**
   * Given a ContestResult and a risk limit, return a ComparisonAudit object appropriate to the contest.
   *
   * @param cr           ContestResult for contest under audit.
   * @param riskLimit    Risk limit for the audit
   * @return A ComparisonAudit object for the contest under audit.
   */
  private ComparisonAudit createAuditForSampleEstimation(final ContestResult cr, final BigDecimal riskLimit){

    // Check type of contest: IRV vs Plurality. If there's a mix of IRV and plurality in one unified contest,
    // that's an error.
    if (cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.IRV.toString()))) {
      return new IRVComparisonAudit(cr, riskLimit, Audit.GAMMA, cr.getAuditReason());
    }
    if (cr.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.PLURALITY.toString()))) {
      return new ComparisonAudit(cr, riskLimit, cr.getDilutedMargin(), Audit.GAMMA, cr.getAuditReason());
    }

    throw new RuntimeException("EstimateSampleSizes: Contest "+cr.getContestName()+" has inconsistent plurality/IRV types.");
  }

  /**
   * Construct a sample size estimate data row for a comparison audit
   * @param ca     Comparison audit whose sample size is being estimated.
   * @return Array of strings defining: county name (or "Multiple if the audit crosses multiple counties);
   * contest name; contest type; ballots cast; diluted margin; and estimated sample size.
   */
  private String[] createSampleEstimateRow(final ComparisonAudit ca){
    // A multi-jurisdictional contest will involve multiple counties.
    final List<String> countyNames = ca.getCounties().stream().map(County::name).collect(Collectors.toList());

    // All contests will have the same contest type (otherwise createAuditForSampleEstimation will have
    // thrown an exception.
    final List<String> contestTypes = ca.contestResult().getContests().stream().map(Contest::description)
            .collect(Collectors.toList());
    final String contestType = contestTypes.get(0);

    final String county = countyNames.size() > 1 ? "Multiple" : countyNames.get(0);

    // The 'ballots cast' datapoint for a contest will be the total number of ballots cast
    // that have that contest on it. This is equal to the sum of my_contest_count in the
    // county contest results for the contest.
    final List<CountyContestResult> ccresults = CountyContestResultQueries.withContestName(ca.getContestName());
    final int contestBallots = ccresults.stream().mapToInt(CountyContestResult::contestBallotCount).sum();

    return new String[]{county, ca.getContestName(), contestType, Integer.toString(contestBallots),
      ca.getDilutedMargin().toString(), ca.estimatedSamplesToAudit().toString()};
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
            cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS)).collect(Collectors.toList());

    // Get the DoS Dashboard (will contain risk limit for audit).
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

    // We now create ComparisonAudit objects for each contest; the sample size estimation
    // methods. ComparisonAuditController.createAudit will create either a Plurality or IRV
    // ComparisonAudit depending on the type of the contest. Note that when ComparisonAudit's
    // are created, they are persisted to the database by ComparisonAuditController. NOTE we
    // could (should)? avoid doing this by directly creating ComparisonAudits without using
    // the ComparisonAuditController for the purpose of preliminary sample size estimation.
    final List<ComparisonAudit> comparisonAudits = countedCRs.stream().map(cr ->
            createAuditForSampleEstimation(cr, dosdb.auditInfo().riskLimit()))
            .collect(Collectors.toList());

    // Call estimatedSamplesToAudit() on each ComparisonAudit. Create a list of rows where
    // each row is an array of strings, containing:
    return comparisonAudits.stream().map(this::createSampleEstimateRow).collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    if (my_asm.get().currentState() != COMPLETE_AUDIT_INFO_SET) {
      // We can only compute preliminary sample size estimates once the ASM has
      // reached the COMPLETE_AUDIT_INFO_SET state (and assertions have been
      // generated for all IRV contests for which it is possible to form assertions.)

      serverError(the_response, "Complete audit information has not been set");
      return my_endpoint_result.get();
    }

    // Estimate sample sizes
    try {
      final List<String[]> samples = estimateSampleSizes();

      if (samples.isEmpty()) {
        dataNotFound(the_response, "No Comparison Audits found.");
      } else {
        the_response.header("Content-Type", "test/csv");
        the_response.header("Content-Disposition", "attachment; filename*=UTF-8''sample_sizes.csv");
        final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
        os.write(((String.join(",",EstimateSampleSizes.HEADERS)) + "\n").getBytes(StandardCharsets.UTF_8));
        for (String[] row : samples) {
          os.write(((String.join(",", row)) + "\n").getBytes(StandardCharsets.UTF_8));
        }
        os.close();
        ok(the_response);
      }
    } catch (final Exception e) {
      // Not sure if this is the right kind of error.
      serverError(the_response, "Could not find any Comparison Audits to estimate, or sample sizes could not be estimated.");
    }

    return my_endpoint_result.get();
  }

}
