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
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;

import java.math.BigDecimal;
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
   * Compute sample sizes for all contests for which CountyContestResults exist in the database.
   * @return A map between contest name and the estimated sample size required for that contest.
   */
  public Map<String, Integer> estimateSampleSizes(){
    // For estimation of sample sizes for each audit, we need to collect the ContestResult
    // for each contest. For Plurality audits, this will involve tabulating the votes across
    // Counties for that contest. For preliminary sample size estimation, we assign
    // OPPORTUNISTIC_BENEFITS as the reason for each audit. The tabulated vote totals
    // in a ContestResult for an IRV contest will not be used. In the call to ContestCounter
    // (countAllContests), all persisted CountyContestResult's will be accessed from the database,
    // grouped by contest, and accumulated into a single ContestResult.
    final List<ContestResult> countedCRs = ContestCounter.countAllContests().stream().map(cr -> {
      cr.setAuditReason(AuditReason.OPPORTUNISTIC_BENEFITS);
      return cr;
    }).collect(Collectors.toList());

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

    // Call estimatedSamplesToAudit() on each ComparisonAudit. Create a map between contest name
    // and the preliminary sample size. Note that each ContestResult upon which a ComparisonAudit
    // is based will have a set of associated contest IDs.
    return comparisonAudits.stream().collect(Collectors.toMap(
            ComparisonAudit::getContestName, ComparisonAudit::estimatedSamplesToAudit));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    if (my_asm.get().currentState() != COMPLETE_AUDIT_INFO_SET) {
      // We can only compute preliminary sample size estimates once the ASM has
      // reached the COMPLETE_AUDIT_INFO_SET state and assertions have been
      // generated for all IRV contests for which it is possible to form assertions.
      // We may create a new ASM state ASSERTIONS_GENERATED_OK (or similar) to indicate
      // when the system has completed this step, and associated events.

      // For now, require the ASM to be in the COMPLETE_AUDIT_INFO_SET state.
      serverError(the_response, "Complete audit information has not been set");
      return my_endpoint_result.get();
    }

    // Estimate sample sizes
    final Map<String, Integer> samples = estimateSampleSizes();

    // Update response with the sample estimates for each contest.
    try {
      if (samples.isEmpty()) {
        dataNotFound(the_response, "No Comparison Audits found.");
      } else {
        okJSON(the_response, Main.GSON.toJson(samples));
      }
    } catch (final Exception e) {
      // Not sure if this is the right kind of error.
      serverError(the_response, "Could not find any Comparison Audits to estimate.");
    }

    return my_endpoint_result.get();
  }

}
