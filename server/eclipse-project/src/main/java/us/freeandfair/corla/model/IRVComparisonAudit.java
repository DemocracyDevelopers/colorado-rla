/*
 * Sketch of IRVComparisonAudit.
 */

package us.freeandfair.corla.model;

import us.freeandfair.corla.query.AssertionQueries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.persistence.*;
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
@DiscriminatorValue("IRV")
public class IRVComparisonAudit extends ComparisonAudit {

  /**
   * List of assertions associated with this audit. These will be all assertions in the database table
   * whose contest matches that in the IRVComparisonAudit's contestResult (attribute in super class).
   */
  @ManyToMany()
  @JoinTable(name = "audit_to_assertions",
          joinColumns = { @JoinColumn(name = "id") })
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

    // Ensure assertions list attribute is populated with assertions for given contest.

    // Super constructor at present will call methods to compute initial sample sizes.
    // These will call optimisticSamplesToAudit and estimatedSamplesToAudit which will
    // need to be overridden for IRV. However, we need assertions to be set prior
    // to that. So, we will either need to move those method outside of the super class
    // constructor (which is suggested in commenting in order to remove warnings), or
    // run the methods again in our constructor once assertions have been set.
    super(contestResult, riskLimit, BigDecimal.ONE, gamma, auditReason);

    // set to the sum of all ballot counts in all counties.
    my_universe_size = contestResult().getBallotCount();

    // Grab assertions for this contest, and store them. Set them up with the
    // right universe size.
    assertions = AssertionQueries.matching(contestResult.getContestName());

    // If there are no assertions for this audit, then the contest is not
    // auditable. Otherwise, it is auditable.
    // The super class will label the audit as NOT_AUDITABLE if the diluted margin
    // assigned to the contestResult is 0, however this will have been computed
    // according to Plurality rules and has no bearing on the auditability of
    // an IRV contest.
    if (assertions.isEmpty()){
      diluted_margin = BigDecimal.ZERO;
      setAuditStatus(AuditStatus.NOT_AUDITABLE);
    }
    else{
      // Assign a diluted margin to the audit
      diluted_margin = BigDecimal.valueOf(Collections.min(assertions.stream().
              map(Assertion::getDilutedMargin).collect(Collectors.toList())));
      setAuditStatus(AuditStatus.NOT_STARTED);
    }

    // As the super class invokes sample size estimation methods in its constructor,
    // we need to invoke them here after the audit's assertions have been populated.
    optimisticSamplesToAudit();
    estimatedSamplesToAudit();
  }

  /**
   * Recalculates the overall numbers of ballots to audit, setting this
   * object's `my_optimistic_samples_to_audit` and
   * `my_estimates_samples_to_audit` fields.
   */
  @Override
  protected void recalculateSamplesToAudit() {
    if(assertions == null){
      // We have not yet populated our assertions list
      return;
    }

    LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplestoAudit start contestName=%s, "
                    + " optimistic=%d, estimated=%d]",
            contestResult().getContestName(),
            my_optimistic_samples_to_audit, my_estimated_samples_to_audit));

    if(assertions.isEmpty()){
      LOGGER.debug("[IRVComparisonAudit::recalculateSamplesToAudit: no assertions in audit]");
      my_optimistic_samples_to_audit = 0;
      my_estimated_samples_to_audit = 0;
      my_optimistic_recalculate_needed = false;
      my_estimated_recalculate_needed = false;
    }
    else {
      if (my_optimistic_recalculate_needed) {
        LOGGER.debug("[IRVComparisonAudit::recalculateSamplesToAudit: calling computeOptimisticSamplesToAudit]");

        List<Integer> optimisticSamples = assertions.stream().map(a ->
                a.computeOptimisticSamplesToAudit(getRiskLimit())).collect(Collectors.toList());

        my_optimistic_samples_to_audit = Collections.max(optimisticSamples);
        my_optimistic_recalculate_needed = false;
      }

      LOGGER.debug("[IRVComparisonAudit::recalculateSamplesToAudit: calling computeEstimatedSamplesToAudit]");

      List<Integer> estimatedSamples = assertions.stream().map(a -> a.computeEstimatedSamplesToAudit(getRiskLimit(),
              getAuditedSampleCount())).collect(Collectors.toList());

      my_estimated_samples_to_audit = Collections.max(estimatedSamples);
      my_estimated_recalculate_needed = false;
    }

    LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplestoAudit end contestName=%s, "
                    + " optimistic=%d, estimated=%d]",
            contestResult().getContestName(),
            my_optimistic_samples_to_audit, my_estimated_samples_to_audit));
  }

  /*
   * MB: This method is not used in the super class, however if it starts getting used, it needs to
   * be overridden here.
   */
  @Override
  public int initialSamplesToAudit() {
    if(assertions == null){
      // We have not yet populated our assertions list
      return 0;
    }
    LOGGER.debug("[IRVComparisonAudit::initialSamplesToAudit: calling computeOptimisticSamplesToAudit]");

    if(assertions.isEmpty()){
      return 0;
    }
    else{
      List<Integer> optimisticSamples = assertions.stream().map(a ->
              a.computeOptimisticSamplesToAudit(getRiskLimit())).collect(Collectors.toList());

      return Collections.max(optimisticSamples);
    }
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
   * @param cvr         The CVR that the machine saw
   * @param auditedCVR  The ACVR that the human audit board saw
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  @Override
  @SuppressWarnings("checkstyle:magicnumber")
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();

  // TBD
    // VT: Suggest this should just iterate though all the assertions and call
    // a.computeDiscrepancy, then take a form of max which is:
    // nothing (i.e. no optional int) if all the calls to a.computeDiscrepancy return nothing.
    // If any of the calls to a.computeDiscrepancy return something, a something with the max
    // of the returned values.

    return result;
  }

  /** risk limit achieved according to math.Audit **/
  @Override
  public BigDecimal riskMeasurement() {
    if (assertions.isEmpty()) {
      // In this case, no assertions were formed for this contest, likely
      // because the contest was not auditable due to ties or assertion
      // generation was too time consuming.
      return BigDecimal.ONE;
    }

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
    // Iterate over the assertions for this audit, and remove 'the_count' instances of this
    // discrepancy from their tallies (if the discrepancy is relevant to the assertion).
    for(Assertion a : assertions){
      a.removeDiscrepancy(the_record);
    }
  }

  /**
   * Records the specified discrepancy. If the discrepancy is for this Contest
   * but from a CVR/ballot that was not selected for this Contest (selected for
   * another Contest), is does not contribute to the counts and calculations. It
   * is still recorded, though, for informational purposes. The valid range is
   * -2 .. 2: -2 and -1 are understatements, 0 is a discrepancy that doesn't
   * affect the RLA calculations, and 1 and 2 are overstatements).
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @param the_type The type of discrepancy to add.
   * @exception IllegalArgumentException if an invalid discrepancy type is
   * specified.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public void recordDiscrepancy(final CVRAuditInfo the_record,
                                final int the_type){
    // Iterate over the assertions for this audit, and record 'the_count' instances of this
    // discrepancy toward their tallies (if the discrepancy is relevant to the assertion).
    for(Assertion a : assertions){
      a.recordDiscrepancy(the_record);
    }
  }

}
