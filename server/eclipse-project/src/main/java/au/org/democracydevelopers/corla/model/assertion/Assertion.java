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

package au.org.democracydevelopers.corla.model.assertion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import javax.persistence.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.PersistentEntity;

@Entity
@Table(name = "assertion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "assertion_type")
public abstract class Assertion implements PersistentEntity {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(Assertion.class);

  /**
   * Assertion ID.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long version;

  /**
   * Name of contest for which this Assertion was generated.
   */
  @Column(name = "contest_name", updatable = false, nullable = false)
  protected String contestName;

  /**
   * Winner of the assertion (a candidate in the contest).
   */
  @Column(name = "winner", updatable = false, nullable = false)
  protected String winner;

  /**
   * Loser of the assertion (a candidate in the contest).
   */
  @Column(name = "loser", updatable = false, nullable = false)
  protected String loser;

  /**
   * Raw margin of the assertion.
   */
  @Column(name = "margin", updatable = false, nullable = false)
  protected int margin;

  /**
   * Diluted margin of the assertion. (This could be a double. For consistency with the existing
   * colorado-rla code base, and the implementation of the methods provided in Audit, we are using a
   * BigDecimal).
   */
  @Column(name = "diluted_margin", updatable = false, nullable = false)
  protected BigDecimal dilutedMargin = BigDecimal.valueOf(0);

  /**
   * Assertion difficulty, as estimated by raire-java.
   */
  @Column(name = "difficulty", updatable = false, nullable = false)
  protected double difficulty = 0;

  /**
   * List of candidates that the assertion assumes are 'continuing' in the assertion's context.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_context", joinColumns = @JoinColumn(name = "id"))
  @Column(name = "assumed_continuing", updatable = false, nullable = false)
  protected List<String> assumedContinuing = new ArrayList<>();

  // The attributes above are established by raire-service when the assertion is created and
  // stored in the database. The attributes below are updatable by colorado-rla during an audit.

  /**
   * Map between CVR ID and the discrepancy calculated for it (and its A-CVR) in the context of this
   * assertion, based on the last call to computeDiscrepancy(). Calls to computeDiscrepancy() will
   * update this map. This data is stored in the table "assertion_cvr_discrepancy" which has columns
   * "id,crv_id,discrepancy", where "id" corresponds to this Assertion's ID, "cvr_id" to the ID of
   * the CVR that is involved in the discrepancy, and "discrepancy" the value of the discrepancy
   * from -2 to 2.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_discrepancies", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "cvr_id")
  @Column(name = "discrepancy", nullable = false)
  protected Map<Long, Integer> cvrDiscrepancy = new HashMap<>();

  /**
   * The estimated number of samples we expect to need to audit this assertion, assuming no further
   * overstatements arise.
   */
  @Column(name = "optimistic_samples_to_audit", nullable = false)
  protected Integer optimisticSamplesToAudit = 0;

  /**
   * The estimated number of samples we expect to need to audit this assertion, assuming
   * overstatement continue at the current rate.
   */
  @Column(name = "estimated_samples_to_audit", nullable = false)
  protected Integer estimatedSamplesToAudit = 0;

  /**
   * The number of two-vote understatements recorded against this assertion so far.
   */
  @Column(name = "two_vote_under_count", nullable = false)
  protected Integer twoVoteUnderCount = 0;

  /**
   * The number of one-vote understatements recorded against this assertion so far.
   */
  @Column(name = "one_vote_under_count", nullable = false)
  protected Integer oneVoteUnderCount = 0;

  /**
   * The number of one-vote overstatements recorded against this assertion so far.
   */
  @Column(name = "one_vote_over_count", nullable = false)
  protected Integer oneVoteOverCount = 0;

  /**
   * The number of two-vote overstatements recorded against this assertion so far.
   */
  @Column(name = "two_vote_over_count", nullable = false)
  protected Integer twoVoteOverCount = 0;

  /**
   * The number of discrepancies recorded so far, against this assertion, that are neither
   * understatements nor overstatements.
   */
  @Column(name = "other_count", nullable = false)
  protected Integer otherCount = 0;

  /**
   * Current risk of the assertion. We initialize this risk to 1, as when we have no information we
   * assume maximum risk.
   */
  @Column(name = "current_risk", nullable = false)
  protected BigDecimal currentRisk = BigDecimal.valueOf(1);

  /**
   * Construct an empty assertion (required for persistence). Note that creation and storage of
   * assertions is the responsibility of raire-service.
   */
  public Assertion() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long theId) {
    id = theId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return version;
  }


  /**
   * Given a number of audited samples, and the total number of overstatements experienced thus far,
   * increase the sample count by a scaling factor. The scaling factor grows as the ratio of
   * overstatements to samples increases. This method could be directly moved to
   * us.freeandfair.corla.math.Audit, can called within this class. The method is almost a direct
   * copy of a private scalingFactor() method in us.freeandfair.corla.ComparisonAudit. If this
   * scalingFactor method is moved to math.Audit, then that private method in ComparisonAudit can be
   * deleted, with calls replaced with calls to the public static method in math.Audit.
   *
   * @return the given number of audited samples increased by a scaling factor.
   */
  private static BigDecimal scalingFactor(BigDecimal auditedSamples, BigDecimal overstatements) {
    if (auditedSamples.equals(BigDecimal.ZERO)) {
      return BigDecimal.ONE;
    } else {
      return BigDecimal.ONE.add(overstatements.divide(auditedSamples, MathContext.DECIMAL128));
    }
  }

  /**
   * For the given risk limit, compute the expected (optimistic) number of samples to audit for this
   * assertion. This calculation assumes that no further overstatements will arise. This method
   * updates the Assertion::optimistic_samples_to_audit attribute with a call to Audit.optimistic()
   * and then returns the new optimistic_samples_to_audit value.
   *
   * @param riskLimit The risk limit of the audit.
   * @return The (optimistic) number of samples we expect we will need to sample to audit this
   * assertion.
   */
  public Integer computeOptimisticSamplesToAudit(BigDecimal riskLimit) {
    final String prefix = "[computeOptimisticSamplesToAudit]";

    LOGGER.debug(String.format("%s Calling Audit::optimistic() with parameters: risk limit " +
            "%f; diluted margin %f; gamma %f; two vote under count %d; one vote under count %d; " +
            "one vote over count %d; two vote over count %d.", prefix, riskLimit, dilutedMargin,
            Audit.GAMMA, twoVoteUnderCount, oneVoteUnderCount, oneVoteOverCount, twoVoteOverCount));

    // Call the colorado-rla audit math; update optimistic_samples_to_audit and return new value.
    optimisticSamplesToAudit = Audit.optimistic(riskLimit, dilutedMargin, Audit.GAMMA,
        twoVoteUnderCount, oneVoteUnderCount, oneVoteOverCount, twoVoteOverCount).intValue();

    LOGGER.debug(String.format("%s Computed optimistic samples to audit for Assertion %d" +
        " of %s ballots.", prefix, id, optimisticSamplesToAudit));

    return optimisticSamplesToAudit;
  }

  /**
   * Compute and return the estimated number of samples to audit for this assertion, given the
   * number of ballots audited thus far and number of observed overstatements. This method takes
   * the current optimistic number of ballots to sample for the assertion, and applies a scaling
   * factor to it. This scaling factor is computed based on the total ballots audited thus far,
   * and the number of observed overstatements. This method updates the assertion's internal record
   * of estimated samples to audit.
   * @param auditedSampleCount Number of ballots audited thus far.
   * @return The estimated number of ballots to audit for this assertion assuming overstatements
   * continue at the current observed rate.
   */
  public Integer computeEstimatedSamplesToAudit(int auditedSampleCount) {
    final String prefix = "[computeEstimatedSamplesToAudit]";
    final int totalOverstatements = oneVoteOverCount + twoVoteOverCount;

    if (totalOverstatements == 0) {
      estimatedSamplesToAudit = optimisticSamplesToAudit;

      LOGGER.debug(String.format("%s No overstatements thus far; estimated ballot samples (%d) " +
              "equals optimistic samples (%d). Assertion ID %d, contest %s.", prefix,
              estimatedSamplesToAudit, optimisticSamplesToAudit, id, contestName));
    }
    else {
      // Compute scaling factor (based on rate of observed overstatements to audited ballots).
      final BigDecimal scalingFac = scalingFactor(BigDecimal.valueOf(auditedSampleCount),
          BigDecimal.valueOf(totalOverstatements));

      estimatedSamplesToAudit = BigDecimal.valueOf(optimisticSamplesToAudit)
              .multiply(scalingFac).setScale(0, RoundingMode.CEILING).intValue();

      LOGGER.debug(String.format("%s %d overstatements thus far; scaling factor of %f applied to " +
              "optimistic sample count of %d; estimate sample count is %d ballots " +
              "(Assertion ID %d, contest %s).", prefix, totalOverstatements, scalingFac,
              optimisticSamplesToAudit, estimatedSamplesToAudit, id, contestName));
    }

    return estimatedSamplesToAudit;
  }

  /**
   * Computes the over/understatement represented by the specified CVR and ACVR. This method returns
   * an optional int that, if present, indicates a discrepancy. There are 5 possible types of
   * discrepancy: -1 and -2 indicate 1- and 2-vote understatements; 1 and 2 indicate 1- and 2- vote
   * overstatements; and 0 indicates a discrepancy that does not count as either an under- or
   * overstatement for the RLA algorithm, but nonetheless indicates a difference between ballot
   * interpretations. If a discrepancy is found, it will be recorded in the cvrDiscrepancy map for
   * this Assertion. It will not be added to the Assertion's discrepancy counters until the
   * recordDiscrepancy() method is called. This design choice is influenced by how the audit logic
   * process interacts with ComparisonAudit's.
   * @param cvr        The CVR that the machine saw.
   * @param auditedCVR The ACVR that the human audit board saw.
   * @return an optional int that is present if there is a discrepancy and absent otherwise.
   */
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr, final CastVoteRecord auditedCVR) {
    final String prefix = "[computeDiscrepancy]";
    OptionalInt result = OptionalInt.empty();

    LOGGER.debug(String.format("%s Computing discrepancy for CVR ID %d, Assertion ID %d, " +
        "contest %s.", prefix, cvr.id(), id, contestName));

    // Get CVRContestInfo matching this assertion's contest from the CVR and audited ballot.
    final Optional<CVRContestInfo> cvrInfo = cvr.contestInfoForContestResult(contestName);
    final Optional<CVRContestInfo> acvrInfo = auditedCVR.contestInfoForContestResult(contestName);

    if (auditedCVR.recordType() == CastVoteRecord.RecordType.PHANTOM_BALLOT) {
      // There is no matching ballot for this CVR (the audited ballot is a phantom), and the
      // relevant contest for this assertion is on the CVR. If the contest has not been recorded
      // as being on this CVR, the worst-case discrepancy in this instance is a 1-vote
      // overstatement (we treat the CVR score as being 0 in this case, and if the ACVR score
      // is -1, we get a 1-vote overstatement).
      if (cvr.recordType() == CastVoteRecord.RecordType.PHANTOM_RECORD) {
        LOGGER.debug(String.format("%s Phantom ballot, phantom record.", prefix));
        result = OptionalInt.of(2);
      }
      else {
        LOGGER.debug(String.format("%s Phantom ballot.", prefix));
        result = cvrInfo.map(this::computeDiscrepancyPhantomBallot)
            .orElseGet(() -> OptionalInt.of(1));
      }
    }
    else if (cvr.recordType() == CastVoteRecord.RecordType.PHANTOM_RECORD) {
      LOGGER.debug(String.format("%s Phantom record.", prefix));
      // Similar to the phantom ballot, we use the worst case scenario.
      result = OptionalInt.of(2);

      if (acvrInfo.isPresent() && acvrInfo.get().consensus() == CVRContestInfo.ConsensusValue.YES) {
        // Compute ballot score for this assertion.
        final int acvrScore = score(acvrInfo.get());

        // Based on the ballot score, what is the maximum discrepancy we could have for the
        // phantom CVR and given ballot?
        result = OptionalInt.of(1 - acvrScore);
      }
    }
    else if (cvrInfo.isPresent() && acvrInfo.isPresent()) {
      if (acvrInfo.get().consensus() == CVRContestInfo.ConsensusValue.NO) {
        LOGGER.debug(String.format("%s Lack of consensus, treat as phantom ballot.", prefix));
        // A lack of consensus for this contest between auditors is treated as if the ballot is a
        // phantom ballot.
        result = computeDiscrepancyPhantomBallot(cvrInfo.get());
      }
      else {
        // First, determine whether there is a difference in the votes on the CVR vs the ballot.
        // If there is no difference, there is no discrepancy.
        final boolean recordsSame = cvrInfo.get().choices().equals(acvrInfo.get().choices());
        if (recordsSame) {
          result = OptionalInt.empty();
        }
        else {
          LOGGER.debug(String.format("%s CVR and ACVR differ.", prefix));
          // There is a difference, compute the discrepancy for this assertion (if any).
          final int cvrScore = score(cvrInfo.get());
          final int acvrScore = score(acvrInfo.get());
          result = OptionalInt.of(cvrScore - acvrScore);
        }
      }
    }
    // If we have determined there is a discrepancy, record the type in cvrDiscrepancies. If
    // we have found there is no discrepancy, ensure that any discrepancy record from a prior call to
    // computeDiscrepancy() for this CVR is removed. Note that if you try to access CVR ID via
    // getCvrId() on the cvr, it will be null.
    if (result.isPresent()) {
      LOGGER.info(String.format("%s CVR ID %d, Assertion ID %d, contest %s, discrepancy %d.",
          prefix, cvr.id(), id, contestName, result.getAsInt()));
      cvrDiscrepancy.put(cvr.id(), result.getAsInt());
    }
    else {
      LOGGER.info(String.format("%s CVR ID %d, Assertion ID %d, contest %s, no discrepancy.",
          prefix, cvr.id(), id, contestName));
      cvrDiscrepancy.remove(cvr.id());
    }
    return result;
  }

  /**
   * For a given CVRAuditInfo capturing a discrepancy between a CVR and ACVR, check if the
   * discrepancy is relevant for this assertion (if it is present in its cvrDiscrepancy map).
   * If so, increment the counters for its discrepancy type.
   * @param the_record   CVRAuditInfo representing the CVR-ACVR pair that has resulted in a discrepancy.
   * @exception RuntimeException if the discrepancy type associated with this CVR-ACVR pair
   * is not valid (i.e., not one of the defined types).
   */
  public void recordDiscrepancy(final CVRAuditInfo the_record) throws RuntimeException {
    final String prefix = "[recordDiscrepancy]";
    if(cvrDiscrepancy.containsKey(the_record.id())){
      final int theType = cvrDiscrepancy.get(the_record.id());
      switch (theType) {
        case -2 -> twoVoteUnderCount += 1;
        case -1 -> oneVoteUnderCount += 1;
        case 0 -> otherCount += 1;
        case 1 -> oneVoteOverCount += 1;
        case 2 -> twoVoteOverCount += 1;
        default -> {
          final String msg = String.format("%s Invalid discrepancy type %d stored in " +
              "discrepancy map for Assertion ID %d, contest %s.", prefix, theType, id, contestName);
          LOGGER.error(msg);
          throw new RuntimeException(msg);
        }
      }

      LOGGER.debug(String.format("%s Discrepancy of type %d added to Assertion ID %d,"+
          "contest %s, CVR ID %d. New totals: 1 vote understatements %d; 1 vote overstatements %d; " +
          "2 vote understatements %d; 2 vote overstatements %d; other %d.", prefix, theType, id,
          contestName, the_record.id(), oneVoteUnderCount, oneVoteOverCount, twoVoteUnderCount,
          twoVoteOverCount, otherCount));
    }
    else{
      final String msg = String.format("%s Attempt to record a discrepancy in Assertion ID %d " +
              "contest %s, CVR ID %s, but no record of a pre-computed discrepancy associated with " +
              "that CVR exists. No increase to discrepancy totals: 1 vote understatements %d; " +
              "1 vote overstatements %d; 2 vote understatements %d; 2 vote overstatements %d; other %d.",
              prefix, id, contestName, the_record.id(), oneVoteUnderCount, oneVoteOverCount,
              twoVoteUnderCount, twoVoteOverCount, otherCount);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Removes discrepancies relating to a given CVR-ACVR comparison. (This is relevant when
   * ballots are 'un-audited' to be subsequently re-audited).
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @exception RuntimeException if an invalid discrepancy type has been stored in this assertion.
   */
  public void removeDiscrepancy(final CVRAuditInfo the_record) throws RuntimeException {
    final String prefix = "[removeDiscrepancy]";
    // Check if this CVR-ACVR pair produced a discrepancy with respect to this assertion.
    // (Note the CVRAuditInfo ID is always the CVR ID).
    if(cvrDiscrepancy.containsKey(the_record.id())){
      final int theType = cvrDiscrepancy.get(the_record.id());
      switch (theType) {
        case -2 -> twoVoteUnderCount -= 1;
        case -1 -> oneVoteUnderCount -= 1;
        case 0 -> otherCount -= 1;
        case 1 -> oneVoteOverCount -= 1;
        case 2 -> twoVoteOverCount -= 1;
        default -> {
          final String msg = String.format("%s Invalid discrepancy type %d stored in " +
              "discrepancy map for Assertion ID %d, contest %s.", prefix, theType, id, contestName);
          LOGGER.error(msg);
          throw new RuntimeException(msg);
        }
      }
      cvrDiscrepancy.remove(the_record.id());
      LOGGER.debug(String.format("%s Discrepancy of type %d removed from Assertion ID %d,"+
              "contest %s, CVR ID %d. New totals: 1 vote understatements %d; 1 vote overstatements %d; " +
              "2 vote understatements %d; 2 vote overstatements %d; other %d.", prefix, theType, id,
              contestName, the_record.id(), oneVoteUnderCount, oneVoteOverCount, twoVoteUnderCount,
              twoVoteOverCount, otherCount));
    }
    else{
      final String msg = String.format("%s Attempt to remove a discrepancy in Assertion ID %d " +
              "contest %s, CVR ID %s, but no record of a pre-computed discrepancy associated with " +
              "that CVR exists. No increase to discrepancy totals: 1 vote understatements %d; " +
              "1 vote overstatements %d; 2 vote understatements %d; 2 vote overstatements %d; other %d.",
              prefix, id, contestName, the_record.id(), oneVoteUnderCount, oneVoteOverCount,
              twoVoteUnderCount, twoVoteOverCount, otherCount);
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    if(oneVoteOverCount < 0 || oneVoteUnderCount < 0 || twoVoteUnderCount < 0 || otherCount < 0 ||
        twoVoteOverCount < 0) {
      final String msg = String.format("%s Negative discrepancy counts in Assertion ID %d, " +
          "contest %s when removing discrepancy for CVR %d.", prefix, id, contestName, the_record.id());
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Computes the Score for the given vote in the context of this assertion. For details on how
   * votes are scored for assertions, refer to the Guide to RAIRE.
   *
   * @param info Contest information containing the vote to be scored.
   * @return Vote score (either -1, 0, or 1).
   */
  protected abstract int score(final CVRContestInfo info);

  /**
   * Returns the worst case discrepancy possible for this assertion given that we have a CVR with
   * the relevant contest on it, and no matching ballot.
   *
   * @param cvrInfo Contest information on the given CVR.
   * @return The worst case discrepancy for this assertion given a CVR and no matching ballot.
   */
  private OptionalInt computeDiscrepancyPhantomBallot(final CVRContestInfo cvrInfo) {
    // Compute the score for the CVR (in the context of this assertion).
    final int score = score(cvrInfo);

    // The maximum discrepancy we can have is 1 + the score assigned to the CVR. If
    // the CVR gives the vote to the assertion's winner, then we could have a 2-vote
    // overstatement if the ballot gave the vote to the loser.
    return OptionalInt.of(score + 1);
  }
}