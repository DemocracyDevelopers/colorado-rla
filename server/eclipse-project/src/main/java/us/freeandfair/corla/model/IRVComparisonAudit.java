/*
 * Sketch of IRVComparisonAudit.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.OptionalInt;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.math.Audit;

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

    super(contestResult, riskLimit, BigDecimal.ZERO, gamma, auditReason);

    // Ensure assertions list attribute is populated with assertions for given contest.

    // Super constructor at present will call methods to compute initial sample sizes.
    // These will call computeOptimisticSamplesToAudit() with varying arguments.

    // Check if the contest is not auditable, if so set status appropriately.
    //  this.setAuditStatus(AuditStatus.NOT_AUDITABLE); The super class will
    // do this if the diluted margin assigned to the contestResult is 0, however
    // this will have been computed according to Plurality rules.

  }

  /**
   * Computes the expected number of ballots to audit overall given the
   * specified numbers of over- and understatements.
   *
   * @param twoUnder The two-vote understatements.
   * @param oneUnder The one-vote understatements.
   * @param oneOver The one-vote overstatements.
   * @param twoOver The two-vote overstatements.
   *
   * @return the expected number of ballots remaining to audit.
   * This is the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  private BigDecimal computeOptimisticSamplesToAudit(final int twoUnder,
                                                     final int oneUnder,
                                                     final int oneOver,
                                                     final int twoOver) {
    return Audit.optimistic(getRiskLimit(), getDilutedMargin(), getGamma(),
                            twoUnder, oneUnder, oneOver, twoOver) ;
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
  @SuppressWarnings("checkstyle:magicnumber")
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();



    return result;
  }

  /** risk limit achieved according to math.Audit **/
  public BigDecimal riskMeasurement() {
      // To complete as appropriate for IRV audits.
      return BigDecimal.ONE;
  }

  /**
   * Computes the discrepancy between two ballots. This method returns an optional
   * int that, if present, indicates a discrepancy. There are 5 possible types of
   * discrepancy: -1 and -2 indicate 1- and 2-vote understatements; 1 and 2 indicate
   * 1- and 2- vote overstatements; and 0 indicates a discrepancy that does not
   * count as either an under- or overstatement for the RLA algorithm, but
   * nonetheless indicates a difference between ballot interpretations.
   *
   * @param the_cvr_info The CVR info.
   * @param the_acvr_info The ACVR info.
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  private OptionalInt computeAuditedBallotDiscrepancy(final CVRContestInfo the_cvr_info,
                                                      final CVRContestInfo the_acvr_info) {

    final OptionalInt result = OptionalInt.empty();

    // To be fleshed out.

    return result;
  }

  /**
   * Computes the discrepancy between a phantom ballot and the specified
   * CVRContestInfo.
   * @return The number of discrepancies
   */
  private Integer computePhantomBallotDiscrepancy(final CVRContestInfo cvrInfo,
                                                  final ContestResult contestResult) {
    int result = 2;

    // To be fleshed out.

    return result;
  }

}
