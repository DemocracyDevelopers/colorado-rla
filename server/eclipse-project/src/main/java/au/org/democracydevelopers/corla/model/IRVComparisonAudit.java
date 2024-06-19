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

package au.org.democracydevelopers.corla.model;

import au.org.democracydevelopers.corla.query.AssertionQueries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.*;

import java.math.BigDecimal;
import java.util.OptionalInt;

import au.org.democracydevelopers.corla.model.assertion.Assertion;

import static java.util.Collections.max;

/**
 * A class representing the state of a single audited IRV contest across one or more counties.
 */
@Entity
@DiscriminatorValue("IRV")
public class IRVComparisonAudit extends ComparisonAudit {

  /**
   * List of assertions associated with this audit -- the set of all assertions in the database
   * table 'assertion' whose contest name matches that of this ComparisonAudit (located in the
   * base classes' contest result attribute, 'my_contest_resylt').
   */
  @ManyToMany()
  @JoinTable(name = "audit_to_assertions", joinColumns = { @JoinColumn(name = "id") })
  private List<Assertion> assertions;

  /**
   * Constructs a new, empty IRVComparisonAudit (solely for persistence).
   */
  public IRVComparisonAudit() {
      super();
  }

  /**
   * Constructs an IRVComparisonAudit for the contest described in the given ContestResult,
   * with the given risk limit, and audit reason. This constructor calls the base class
   * constructor, and then populates this IRV audit's list of assertions by collecting all
   * assertions from the database associated with the given contest. It then calculates and
   * sets the audit's diluted margin (stored in the base class) and calls methods for
   * establishing the optimistic and estimated sample size for the audit. This follows the
   * logic of the original ComparisonAudit class for Plurality contests. A RuntimeException is
   * thrown if an unexpected error arises when retrieving assertions from the database, or in the
   * computation of optimistic and estimated sample sizes.
   *
   * @param contestResult The contest result (identifies the contest under audit).
   * @param riskLimit The risk limit.
   * @param auditReason The audit reason.
   */
  @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
  public IRVComparisonAudit(final ContestResult contestResult, final BigDecimal riskLimit,
                            final AuditReason auditReason)  {
    super(contestResult, riskLimit, BigDecimal.ONE, Audit.GAMMA, auditReason);

    final String prefix = "[IRVComparisonAudit all args constructor]";
    final String contestName = contestResult.getContestName();

    LOGGER.debug(String.format("%s called for contest %s risk limit %f and audit reason %s.",
        prefix, contestName, riskLimit, auditReason));

    // Populate the list of assertions belonging to this audit (retrieve all assertions from
    // the database whose contest name matches that in the given contestResult). If an
    // unexpected error arises in retrieving this data from the database, matching() will throw
    // a RunTimeException.
    assertions = AssertionQueries.matching(contestResult.getContestName());

    LOGGER.debug(String.format("%s retrieved %d assertions for contest %s.",
        prefix, assertions.size(), contestName));

    try {
      // If there are no assertions for this audit, then the contest is not auditable.
      if (assertions.isEmpty()) {
        // Setting the diluted margin to 0, as the contest is not auditable.
        diluted_margin = BigDecimal.ZERO;
        setAuditStatus(AuditStatus.NOT_AUDITABLE);
        LOGGER.debug(String.format("%s contest %s is not auditable, setting diluted margin to 0," +
            "and status to NOT_AUDITABLE.", prefix, contestName));
      } else {
        // Assign a diluted margin to the audit. This is equal to the smallest diluted margin of
        // any assertion in 'assertions' (ie. the hardest to audit assertion).
        diluted_margin = Collections.min(assertions.stream().map(Assertion::getDilutedMargin).toList());

        // Initialise the status of the audit.
        setAuditStatus(AuditStatus.NOT_STARTED);

        // As the super class invokes sample size estimation methods in its constructor,
        // we need to invoke them here after the audit's assertions have been populated. Otherwise,
        // they will not have been computed correctly for IRV.
        optimisticSamplesToAudit();
        estimatedSamplesToAudit();
      }
    } catch(Exception e){
      final String msg = String.format("%s An unexpected error arose during diluted margin " +
          "and initial sample size computation when instantiating an IRVComparison audit " +
          "for contest %s: %s", prefix, contestName, e.getMessage());
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   *  Recalculates the estimated number of ballots to audit, and updates the audit's current level
   *  of risk, setting the base class' `my_optimistic_samples_to_audit` and
   *  `my_estimated_samples_to_audit` attributes in the process. Each assertion in this audit is
   *  updated with its current risk. The method will check whether the optimistic sample size
   *  of the audit (the number of ballots we expect to need to sample assuming no further
   *  overstatements arise) needs to be recalculated. If so, each assertion's optimistic sample
   *  size will be computed, and we will take the maximum of these as the optimistic sample size
   *  for the audit as a whole. We then call each assertion's estimated sample size computation
   *  method, and take the maximum of these as the estimated sample size of the audit as a whole.
   * A RuntimeException is thrown when an unexpected error arises during sample size computation. This
   * calculation involves both newly added code and existing code within various parts of colorado-rla.
   */
  @Override
  protected void recalculateSamplesToAudit() {
    if(assertions == null){
      // We have not associated assertions with this audit yet. When an IRVComparisonAudit
      // constructed, we call the base class constructor first. The ComparisonAudit constructor
      // calls sample size computation methods. So, this method may end up being called before
      // the IRVComparisonAudit constructor has completed its setup (retrieving assertions from
      // the database and so on). In which case, we simply ignore the call.
      return;
    }

    final String prefix = "[recalculateSamplesToAudit]";
    final String contestName = getContestName();

    try {
      if (assertions.isEmpty()) {
        // This contest is not auditable.
        // There are no assertions in this audit (Note: this is distinct from the above case where
        // we have not yet *retrieved* assertions and initialised the 'assertions' attribute).
        my_optimistic_samples_to_audit = 0;
        my_estimated_samples_to_audit = 0;
        my_optimistic_recalculate_needed = false;
        my_estimated_recalculate_needed = false;

        LOGGER.debug(String.format("%s No assertions for contest %s; setting optimistic and " +
                "estimate sample sizes to 0; and optimistic and estimated recalculate needed to false.",
            prefix, getContestName()));
      } else {
        // Sample size computation works by (1) computing the optimistic sample size if its
        // current value is out of date, and then (2) computing estimated sample size (which uses
        // the optimistic sample size as a starting point). We first check whether we need to update
        // the optimistic sample size stored in the base ComparisonAudit class.
        if (my_optimistic_recalculate_needed) {
          // We compute the optimistic sample size for each of the IRVComparisonAudit's
          // assertions and take the largest of these as the optimistic sample size of the
          // audit as a whole.
          LOGGER.debug(String.format("%s calling computeOptimisticSamplesToAudit() for each " +
              "assertion in contest %s given risk limit %f.", prefix, contestName, getRiskLimit()));
          my_optimistic_samples_to_audit = max(assertions.stream().map(a ->
              a.computeOptimisticSamplesToAudit(getRiskLimit())).toList());
          my_optimistic_recalculate_needed = false;
          LOGGER.debug(String.format("%s optimistic sample size of %d computed for contest %s.",
              prefix, my_optimistic_samples_to_audit, contestName));
        }

        // We now compute the estimated sample size for each assertion, and take the maximum as
        // the overall estimated sample sizes of the audit.
        LOGGER.debug(String.format("%s calling computeEstimatedSamplesToAudit() for each " +
                "assertion in contest %s given current audited sample count of %d.", prefix,
            contestName, getAuditedSampleCount()));
        my_estimated_samples_to_audit = max(assertions.stream().map(a ->
            a.computeEstimatedSamplesToAudit(getAuditedSampleCount())).toList());
        my_optimistic_recalculate_needed = false;

        // Tell each assertion to update its risk calculation, return maximum risk across
        // assertions. This will record an updated risk in each assertion's currentRisk attribute.
        final BigDecimal risk = max(assertions.stream().map(a ->
            a.riskMeasurement(getAuditedSampleCount())).toList());

        LOGGER.debug(String.format("%s estimated sample size of %d computed for contest %s; risk %f.",
            prefix, my_estimated_samples_to_audit, contestName, risk));
      }
    } catch(Exception e){
      // An unexpected error has arisen during sample size computation.
      final String msg = String.format("%s An unexpected error arose in sample size " +
          "computation for the IRVComparisonAudit of contest %s: %s.", prefix, contestName,
          e.getMessage());
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * This method is currently not being used in the base class. However, if it starts being used
   * in the future, we need an IRV version for IRV audits. Consequently, we provide the IRV
   * version here. This method computes and returns the initial (optimistic) sample size
   * for the audit. This is computed by calling the optimistic sample size computation method
   * of each of the audit's assertions, and returning the maximum of these. A RuntimeException is
   * thrown if an unexpected error arises in computing optimistic sample sizes for each assertion.
   * @return the optimistic sample size for the audit.
   */
  @Override
  public int initialSamplesToAudit() {
    if(assertions == null){
      // We have not associated assertions with this audit yet.
      // In this case, we simply ignore the call.
      return 0;
    }

    final String prefix = "[initialSamplesToAudit]";
    final String contestName = getContestName();

    try {
      LOGGER.debug(String.format("%s computing the initial samples to audit for the IRV contest %s.",
        prefix, contestName));

      if (assertions.isEmpty()) {
        LOGGER.debug(String.format("%s No assertions for contest %s; returning an initial sample " +
            "size of 0.", prefix, contestName));
        return 0;
      } else {
        LOGGER.debug(String.format("%s calling computeOptimisticSamplesToAudit() for each " +
            "assertion in contest %s given risk limit %f.", prefix, contestName, getRiskLimit()));
        final int samples = max(assertions.stream().map(a ->
            a.computeOptimisticSamplesToAudit(getRiskLimit())).toList());
        LOGGER.debug(String.format("%s optimistic sample size of %d computed for contest %s.",
            prefix, samples, contestName));
        return samples;
      }
    } catch(Exception e){
      // An unexpected error has arisen during sample size computation.
      final String msg = String.format("%s An unexpected error arose in sample size " +
              "computation for the IRVComparisonAudit of contest %s: %s.", prefix, contestName,
              e.getMessage());
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Computes the discrepancy (if any) between the specified CVR and matching audited ballot (ACVR),
   * with respect to each assertion in this audit and the audit's contest. This method returns an
   * optional int that, if present, indicates that a discrepancy exists.
   * There are 5 possible types of discrepancy: -1 indicates a one vote understatement; a -2
   * indicates a two vote understatement; a 1 indicates a one vote overstatement; a 2 a two vote
   * overstatement; and a 0 an "other" discrepancy (not an understatement or overstatement but
   * a difference in the vote recorded for this audit's contest).
   * Discrepancies are computed with respect to individual assertions. This method will consider
   * each assertion, and compute the discrepancy (if one exists) between the CVR and ACVR for that
   * assertion. If no discrepancy exists for any of the audit's assertions, then an empty
   * optional int will be returned. Otherwise, the method will return the maximum discrepancy
   * arising from this CVR-ACVR pair across all assertions.
   * This method has side effects: calling each assertion's computeDiscrepancy() method will store
   * the CVR ID and discrepancy value of the CVR-ACVR pair *if* a discrepancy was found. If the
   * recordDiscrepancy() method is called with this CVR and ACVR pair, the previously computed
   * discrepancies will be recorded in each assertion's discrepancy counts. This logic follows
   * how discrepancy computation and recording works for Plurality audits. A RuntimeException
   * will be thrown in the following circumstances: the provided CVR/ACVR is null; the
   * IRVComparisonAudit's assertions list is null (not yet set); a RuntimeException has been
   * thrown by an assertion's computeDiscrepancy() method; or an unexpected error has been caught.
   *
   * @param cvr         The CVR that the machine saw.
   * @param auditedCVR  The ACVR that the human audit board saw.
   * @return an optional int that is present if there is a discrepancy between the CVR and audited
   * ballot, for at least one assertion, and absent otherwise.
   */
  @Override
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr, final CastVoteRecord auditedCVR) {
    final String prefix = "[computeDiscrepancy]";
    final String contestName = getContestName();

    if(cvr == null || auditedCVR == null){
      // This is not valid input.
      final String msg = String.format("%s Calling computeDiscrepancy() with null CVR/ACVR.", prefix);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    LOGGER.debug(String.format("%s Computing discrepancy between CVR ID %d and audited ballot, " +
        "contest %s.", prefix, cvr.id(), contestName));

    if(assertions == null){
      // This should not happen; if it does, something has gone wrong.
      final String msg = String.format("%s IRVComparison audit for contest %s has not set its " +
          "assertions list yet (it is null).", prefix, contestName);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    LOGGER.debug(String.format("%s Contest %s: Calling computeDiscrepancy() for each assertion.",
        prefix, contestName));

    // Compute discrepancies with respect to each assertion for this CVR/ACVR pair. If a
    // discrepancy exists for an assertion, add its value to a list.
    try {
      List<Integer> discrepancies = new ArrayList<>();
      for (Assertion a : assertions) {
        final OptionalInt result = a.computeDiscrepancy(cvr, auditedCVR);

        if (result.isPresent()) {
          discrepancies.add(result.getAsInt());
        }
      }

      // If we've found no discrepancies across the assertion set, then none exists for this contest
      // and this CVR/ACVR pair. Otherwise, return the maximum discrepancy found across all assertions.
      if (discrepancies.isEmpty()) {
        LOGGER.debug(String.format("%s Contest %s: No discrepancies found for CVR ID %d.", prefix,
            contestName, cvr.id()));
        return OptionalInt.empty();
      } else {
        final Integer maxDiscrepancy = max(discrepancies);
        LOGGER.debug(String.format("%s Contest %s: Maximum discrepancy of %d found for CVR ID %d.",
            prefix, contestName, maxDiscrepancy, cvr.id()));
        return OptionalInt.of(maxDiscrepancy);
      }

    } catch(RuntimeException e){
      final String msg = String.format("%s Contest %s, RuntimeException: %s", prefix,
          contestName, e.getMessage());
      LOGGER.error(msg);
      throw new RuntimeException(msg);

    } catch(Exception e){
      // Unexpected exception: log and throw as a RuntimeException.
      final String msg = String.format("%s Contest %s, Exception: %s", prefix, contestName,
          e.getMessage());
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Returns the maximum assertion risk across this audit's set of assertions. This method
   * will call each assertion's riskMeasurement() method to update their risk, and return the
   * largest of these computed risks. This method will throw a RuntimeException if called
   * and the IRVComparisonAudit's assertions list is null, or an unexpected error arises during
   * risk computation.
   * @return The largest current level of risk attached to an assertion in this audit.
   */
  @Override
  public BigDecimal riskMeasurement() {
    final String prefix = "[riskMeasurement]";
    final String contestName = getContestName();

    if(assertions == null){
      // This should never happen, and indicates an error.
      final String msg = String.format("%s IRVComparisonAudit ID %d for contest %s, null " +
          "assertions list.", prefix, id(), contestName);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    BigDecimal risk = BigDecimal.ONE;
    if(!assertions.isEmpty()){
      LOGGER.debug(String.format("%s IRVComparisonAudit ID %d for contest %s, computing risks.",
          prefix, id(), contestName));
      try {
        risk = max(assertions.stream().map(a -> a.riskMeasurement(getAuditedSampleCount())).toList());
      }
      catch(Exception e){
        final String msg = String.format("%s IRVComparisonAudit ID %d for contest %s, error " +
            "in assertion risk measurement: %s", prefix, id(), contestName, e.getMessage());
        LOGGER.error(msg);
        throw new RuntimeException(msg);
      }
    }

    LOGGER.debug(String.format("%s IRVComparisonAudit ID %d for contest %s, risk %f.", prefix,
        id(), contestName, risk));
    return risk;
  }

  // Does nothing - TODO.
  @Override
  public void removeDiscrepancy(final CVRAuditInfo the_record, final int the_type) {
  }

  // Does nothing - TODO.
  @Override
  public void recordDiscrepancy(final CVRAuditInfo the_record, final int the_type) {
  }
}
