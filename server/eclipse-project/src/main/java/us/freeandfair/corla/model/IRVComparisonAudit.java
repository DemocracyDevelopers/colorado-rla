/*
 * Sketch of IRVComparisonAudit.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;


/**
 * A class representing the state of a single audited IRV contest for
 * across multiple counties
 *
 * Note: will need to determine how the presence of a ComparisonAudit subclass
 * will impact the database storage of ComparisonAudits.
 *
 * Note: superclass attributes are private, will need to access via get/set methods.
 */
@Entity
@Cacheable(true)
@Table(name = "comparison_audit")

public class IRVComparisonAudit extends ComparisonAudit {

  /**
   * List of assertions associated with this audit. These will be all assertions in the database table
   * whose contest matches that in the IRVComparisonAudit's contestResult (attribute in super class).
   */
  private List<Assertion> assertions;

  /**
   * Size of the universe for auditing purposes. I believe this is the same as the total ballot count
   * in the contest.
   * TODO: Check that this is the right universe size.
   */
  private long my_universe_size ;

  /**
   * Constructs a new, empty IRVComparisonAudit (solely for persistence).
   */
  public IRVComparisonAudit() {
    super();
  }

  /**
   * Constructs an IRVComparisonAudit for the given params
   *
   * @param contestResult The contest result.
   * @param riskLimit The risk limit.
   * @param gamma γ
   * @param auditReason The audit reason.
   */
  @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
  public IRVComparisonAudit(final ContestResult contestResult,
                         final BigDecimal riskLimit,
                         final BigDecimal gamma,
                         final AuditReason auditReason) {
    // Grab assertions for this contest, determine the diluted margin,
    // and use this value in place of BigDecimal.ZERO when calling the
    // super class's constructor.

    // Assign a diluted margin to the audit:
    // This is equal to the minimum diluted margin across assertions.

    super(contestResult, riskLimit, BigDecimal.ONE, gamma, auditReason);

    my_universe_size = contestResult().getBallotCount();

    // Ensure assertions list attribute is populated with assertions for given contest.

    // Super constructor at present will call methods to compute initial sample sizes.
    // These will call optimisticSamplesToAudit and estimatedSamplesToAudit which will
    // need to be overriden for IRV.

    // Check if the contest is not auditable, if so set status appropriately.
    // this.setAuditStatus(AuditStatus.NOT_AUDITABLE); The super class will
    // do this if the diluted margin assigned to the contestResult is 0, however
    // this will have been computed according to Plurality rules.

    // NOTE that get methods for some of the private attributes in ComparisonAudit will
    // need to be added to the super class.
  }

  /**
   * Recalculates the overall numbers of ballots to audit, setting this
   * object's `my_optimistic_samples_to_audit` and
   * `my_estimates_samples_to_audit` fields.
   */
  @Override protected void recalculateSamplesToAudit() {
    LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplestoAudit start contestName=%s, "
                    + " optimistic=%d, estimated=%d]",
            contestResult().getContestName(),
            my_optimistic_samples_to_audit, my_estimated_samples_to_audit));

    if (my_optimistic_recalculate_needed) {
      LOGGER.debug("[IRVComparisonAudit::recalculateSamplesToAudit: calling computeOptimisticSamplesToAudit]");

      List<Integer> optimisticSamples = (List<Integer>) assertions.stream().map(a -> a.computeOptimisticSamplesToAudit(getRiskLimit(), my_universe_size));

      my_optimistic_samples_to_audit = Collections.max(optimisticSamples);
      my_optimistic_recalculate_needed = false;
    }

    LOGGER.debug("[IRVComparisonAudit::recalculateSamplesToAudit: calling computeEstimatedSamplesToAudit]");

    List<Integer> estimatedSamples =  (List<Integer>) assertions.stream().map(a -> a.computeEstimatedSamplesToAudit(getRiskLimit(), my_universe_size, getAuditedSampleCount()));

    my_optimistic_samples_to_audit = Collections.max(estimatedSamples);
    my_optimistic_recalculate_needed = false;

    LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplestoAudit end contestName=%s, "
                    + " optimistic=%d, estimated=%d]",
            contestResult().getContestName(),
            my_optimistic_samples_to_audit, my_estimated_samples_to_audit));
    my_estimated_recalculate_needed = false;
  }

  @Override
  public int initialSamplesToAudit(){
    // TBD
    return 0;
  }

  @Override
  public Integer optimisticSamplesToAudit(){
    // TBD
    return 0;
  }

  @Override
  public Integer estimatedSamplesToAudit(){
    // TBD
    return 0;
  }

  /**
   * Updates the audit status based on the current risk limit. If the audit
   * has already been ended or the contest is not auditable, this method has
   * no effect on its status.
   * Fix: RLA-00450
   */
  public void updateAuditStatus() {
    // TBD
    // In ComparisonAudit, this method calls some private methods that are incorrect for
    // IRV (ie. recalculateSamplesToAudit()). This method is private in ComparisonAudit,
    // and thus not overridable.
    // We could adjust recalculateSamplesToAudit() from private to protected, however if
    // we want to minimise changes to the original codebase, we should perhaps just
    // redo all the public methods in ComparisonAudit that call inappropriate private methods.
  }


  /**
   * Computes the over/understatement represented by the specified CVR and ACVR.
   * This method returns an optional int that, if present, indicates a discrepancy.
   * There are 5 possible types of discrepancy: -1 and -2 indicate 1- and 2-vote
   * understatements; 1 and 2 indicate 1- and 2- vote overstatements; and 0
   * indicates a discrepancy that does not count as either an under- or
   * overstatement for the RLA algorithm, but nonetheless indicates a difference
   * between ballot interpretations.
   *
   * @param cvr The CVR that the machine saw
   * @param auditedCVR The ACVR that the human audit board saw
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  @Override
  @SuppressWarnings("checkstyle:magicnumber")
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();

  // TBD

    return result;
  }

  /** risk limit achieved according to math.Audit **/
  @Override
  public BigDecimal riskMeasurement() {
      // To complete as appropriate for IRV audits.
      return BigDecimal.ONE;
  }

  /**
   * Removes the specified over/understatement (the valid range is -2 .. 2:
   * -2 and -1 are understatements, 0 is a discrepancy that doesn't affect the
   * RLA calculations, and 1 and 2 are overstatements). This is typically done
   * when a new interpretation is submitted for a ballot that had already been
   * interpreted.
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @param the_type The type of discrepancy to remove.
   * @exception IllegalArgumentException if an invalid discrepancy type is
   * specified.
   */
  @Override
  @SuppressWarnings("checkstyle:magicnumber")
  public void removeDiscrepancy(final CVRAuditInfo the_record, final int the_type) {
 
  }

}
