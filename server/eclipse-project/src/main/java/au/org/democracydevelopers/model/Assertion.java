/*
 * Sketch of abstract Assertion class (following conventions of other CORLA classes).
 *
 * Will need to implement PersistentEntity and Serializable interfaces, as shown.
 *
 * JPA hibernate annotations are speculative.
 */

package au.org.democracydevelopers.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.LongIntegerMapConverter;
import us.freeandfair.corla.persistence.PersistentEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;

@Entity
@Table(name = "assertion")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "assertion_type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NEBAssertion.class, name = "NEB"),
        @JsonSubTypes.Type(value = NENAssertion.class, name = "NEN")
})
public abstract class Assertion implements PersistentEntity, Serializable {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
          LogManager.getLogger(Assertion.class);

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

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
  @Column(name = "contest_name", nullable = false)
  protected String contestName;

  /**
   * Winner of the assertion (a candidate in the contest).
   */
  @Column(name = "winner", nullable = false)
  protected String winner;

  /**
   * Loser of the assertion (a candidate in the contest).
   */
  @Column(name = "loser", nullable = false)
  protected String loser;

  /**
   * Assertion margin.
   */
  @Column(name = "margin", nullable = false)
  protected int margin;

  /**
   * Diluted margin for the assertion.
   */
  @Column(name = "diluted_margin", nullable = false)
  protected double dilutedMargin = 0;

  /**
   * Assertion difficulty, as estimated by RAIRE.
   */
  @Column(name = "difficulty", nullable = false)
  protected double difficulty = 0;

  /**
   * List of candidates that the assertion assumes are `continuing' in the
   * assertion's context.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_context",
          joinColumns = @JoinColumn(name = "id"))
  @Column(name = "assumed_continuing", nullable = false)
  protected List<String> assumedContinuing = new ArrayList<>();

  /**
   * The number of samples to audit overall assuming no further overstatements.
   */
  @Column(nullable = false)
  private Integer my_optimistic_samples_to_audit = 0;

  /**
   * Map between CVR ID and the discrepancy calculated for it (and its A-CVR) in the context
   * of this assertion, based on the last call to computeDiscrepancy(). Calls to computeDiscrepancy()
   * will update this map. Two options for how we persist this data. We can use existing
   * functionality in colorado-rla (the LongIntegerMapConverter class). This will add a column to
   * the assertion table. The second option is to create a new table: "assertion_cvr_discrepancy" which
   * will have columns "id,crv_id,discrepancy", where "id" corresponds to this Assertion's ID, "cvr_id"
   * to the ID of the CVR that is involved in the discrepancy, and "discrepancy" the value of the
   * discrepancy from -2 to 2.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_discrepancies", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "cvr_id")
  @Column(name = "discrepancy", nullable = false)
  protected Map<Long,Integer> cvrDiscrepancy = new HashMap<>();

  /**
   * The expected number of samples to audit overall assuming overstatements
   * continue at the current rate.
   */
  @Column(nullable = false)
  private Integer my_estimated_samples_to_audit = 0;

  /**
   * The two-vote understatements recorded so far.
   */
  @Column(nullable = false)
  protected Integer my_two_vote_under_count = 0;

  /**
   * The one-vote understatements recorded so far.
   */
  @Column(nullable = false)
  protected Integer my_one_vote_under_count = 0;

  /**
   * The one-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  protected Integer my_one_vote_over_count = 0;

  /**
   * The two-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  protected Integer my_two_vote_over_count = 0;

  /**
   * Discrepancies recorded so far that are neither understatements nor overstatements.
   */
  @Column(nullable = false)
  protected Integer my_other_count = 0;

  /**
   * Current risk measurement
   * Initialize at 1 because, when we have no audit info, we assume maximum risk.
   */
  @Column(nullable = false)
  private BigDecimal my_current_risk = BigDecimal.valueOf(1);

  /**
   * Creates an Assertion for a specific contest. The assertion has a given winner, loser,
   * margin, and list of candidates that are assumed to be continuing in the assertion's
   * context.
   *
   * @param contestName       Assertion has been created for this contest.
   * @param winner            Winning candidate (from contest contestID) of the assertion.
   * @param loser             Losing candidate (from contest contestID) of the assertion.
   * @param margin            Margin of the assertion.
   * @param universeSize      Size of the universe for this audit, i.e. overall ballot count.
   * @param difficulty        Estimated difficulty of assertion.
   * @param assumedContinuing List of candidates that assertion assumes are continuing.
   */
  public Assertion(String contestName, String winner, String loser, int margin,
                   long universeSize, double difficulty, List<String> assumedContinuing) {
    this.contestName = contestName;
    this.winner = winner;
    this.loser = loser;
    this.margin = margin;

    assert universeSize != 0 : "Assertion constructor: can't work with zero universeSize.";
    this.dilutedMargin = margin / (double) universeSize;

    this.difficulty = difficulty;
    this.assumedContinuing = assumedContinuing;
  }

  /**
   * Construct an empty assertion (for persistence).
   */
  public Assertion(){}

  public void setContestName(String contestName){
    this.contestName = contestName;
  }

  public void setWinner(String winner){
    this.winner = winner;
  }

  public void setLoser(String loser){
    this.loser = loser;
  }

  public void setContinuing(List<String> continuing){
    this.assumedContinuing = continuing;
  }

  public String getContestName(){
    return this.contestName;
  }

  public String getWinner(){
    return this.winner;
  }

  public String getLoser(){
    return this.loser;
  }

  public List<String> getContinuing(){
    return this.assumedContinuing;
  }

  public double getDilutedMargin() { return this.dilutedMargin; }

  public Integer computeOptimisticSamplesToAudit (BigDecimal riskLimit) {

    my_optimistic_samples_to_audit = Audit.optimistic(riskLimit, BigDecimal.valueOf(dilutedMargin), Audit.GAMMA,
            my_two_vote_under_count, my_one_vote_under_count, my_one_vote_over_count, my_two_vote_over_count).intValue();

    return my_optimistic_samples_to_audit;
  }

  /* Important thing to think about: possibly we want to re-call computeOptimisticSamplesToAudit first,
   * to make sure that is updated, just in case someone calls Estimated without having called Optimistic
   * immediately prior. OTOH perhaps that's bad because it means that calling computeEstimatedSamplesToAudit
   * will have the side-effect of changing my_optimistic_samples_to_audit. I have therefore left it as it is.
   */
  public Integer computeEstimatedSamplesToAudit (BigDecimal riskLimit, int auditedSampleCount) {

    var scalingFac = scalingFactor(auditedSampleCount, my_one_vote_over_count, my_two_vote_over_count);

    if (my_one_vote_over_count + my_two_vote_over_count == 0) {
      LOGGER.debug("[computeEstimatedSamplesToAudit: zero overcounts]");
      my_estimated_samples_to_audit = my_optimistic_samples_to_audit;
    } else {
      LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplesToAudit: non-zero overcounts, " +
              "using scaling factor %s]", scalingFac));
      my_estimated_samples_to_audit =
              BigDecimal.valueOf(my_optimistic_samples_to_audit)
                      .multiply(scalingFac)
                      .setScale(0, RoundingMode.CEILING)
                      .intValue();
    }

    return my_estimated_samples_to_audit;
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
  public void setID(final Long the_id) {
    id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return version;
  }


  /**
   * For a given CVRAuditInfo capturing a discrepancy between a CVR and ACVR, check if the
   * discrepancy is relevant for this assertion (if it is present in its cvrDiscrepancy map). If so,
   * increment the counters for its discrepancy type.
   *
   * @param the_record   CVRAuditInfo representing the CVR-ACVR pair that has resulted in a discrepancy.
   * @exception IllegalArgumentException if the discrepancy associated with this CVR-ACVR pair is not valid.
   */
  public void recordDiscrepancy(final CVRAuditInfo the_record) {
    if(cvrDiscrepancy.containsKey(the_record.id())){
      int theType = cvrDiscrepancy.get(the_record.id());
      switch (theType) {
        case -2:
          my_two_vote_under_count += 1;
          break;

        case -1:
          my_one_vote_under_count += 1;
          break;

        case 0:
          my_other_count += 1;
          break;

        case 1:
          my_one_vote_over_count += 1;
          break;

        case 2:
          my_two_vote_over_count += 1;
          break;

        default:
          throw new IllegalArgumentException("Invalid discrepancy type " + theType +
                  " stored in assertion for contest " + contestName);
      }
    }
  }



  /**
   * Removes discrepancies relating to a given CVR-ACVR comparison.
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @exception IllegalArgumentException if an invalid discrepancy type has been stored in this assertion.
   */
  public void removeDiscrepancy(final CVRAuditInfo the_record){
    // Check if this CVR-ACVR pair produced a discrepancy with respect to this assertion.
    // (Note the CVRAuditInfo ID is always the CVR ID).
    if(cvrDiscrepancy.containsKey(the_record.id())){
      int theType = cvrDiscrepancy.get(the_record.id());
      switch (theType) {
        case -2:
          my_two_vote_under_count -= 1;
          break;

        case -1:
          my_one_vote_under_count -= 1;
          break;

        case 0:
          my_other_count -= 1;
          break;

        case 1:
          my_one_vote_over_count -= 1;
          break;

        case 2:
          my_two_vote_over_count -= 1;
          break;

        default:
          throw new IllegalArgumentException("Invalid discrepancy type " + theType +
                  " stored in assertion for contest " + contestName);
      }
    }

  }

  /**
   * A scaling factor for the estimate, from 1 (when no samples have
   * been audited) upward.
   * The scaling factor grows as the ratio of
   * overstatements to samples increases.
   *
   * VT: I really don't understand this function - I think it's just an ad hoc way
   * of bumping up the sample size in case we see a few more errors than we did
   * in the last round.
   *
   * In any case, it should be a static util function (maybe in the Audit class) that
   * takes its data (auditedSamples, overstatements) and returns the scaling factor.
   * That would avoid the need to have one copy here and another identical copy in ComparisonAudit.
   * Also no idea why we need all these BigDecimals when we're just going to round to int anyway.
   * TODO check whether the use of BigDecimals is (a) correct, (b) necessary.
   */
  private BigDecimal scalingFactor(int auditedSamplesInt, Integer one_vote_overstatements, Integer two_vote_overstatements) {
    final BigDecimal auditedSamples = BigDecimal.valueOf(auditedSamplesInt);
    final int overstatements = one_vote_overstatements + two_vote_overstatements;
    if (auditedSamples.equals(BigDecimal.ZERO)) {
      return BigDecimal.ONE;
    } else {
      return BigDecimal.ONE.add(BigDecimal.valueOf(overstatements).divide(auditedSamples, MathContext.DECIMAL128));
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
   * If a discrepancy is found, it will be recorded in the cvrDiscrepancy map
   * for this Assertion. It will not be added to the Assertion's discrepancy
   * counters until the recordDiscrepancy() method is called.
   *
   * @param cvr        The CVR that the machine saw
   * @param auditedCVR The ACVR that the human audit board saw
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();

    // Get CVRContestInfo matching this assertion's contest from the CVR and audited ballot.
    final Optional<CVRContestInfo> cvrInfo = cvr.contestInfoForContestResult(this.contestName);
    final Optional<CVRContestInfo> acvrInfo = auditedCVR.contestInfoForContestResult(this.contestName);

    if (auditedCVR.recordType() == CastVoteRecord.RecordType.PHANTOM_BALLOT) {
        // There is no matching audited ballot for this CVR (the audited ballot is a phantom),
        // and the relevant contest for this assertion is on the CVR.
        // If the contest has not been recorded as being on this CVR, the worst-case discrepancy in
        // this instance is a 1-vote overstatement (we treat the CVR score as being 0 in this case,
        // and if the ACVR score is -1, we get a 1-vote overstatement).
        if (cvr.recordType() == CastVoteRecord.RecordType.PHANTOM_RECORD){
          result = OptionalInt.of(2);
        }
        else {
          result = cvrInfo.map(this::computeDiscrepancyPhantomBallot).orElseGet(() -> OptionalInt.of(1));
        }
    } else if (cvr.recordType() == CastVoteRecord.RecordType.PHANTOM_RECORD){
      // Similar to the phantom ballot, we use the worst case scenario.
      result = OptionalInt.of(2);

      if(acvrInfo.isPresent() && acvrInfo.get().consensus() == CVRContestInfo.ConsensusValue.YES){
        // Compute ballot score for this assertion.
        int acvrScore = score(acvrInfo.get());

        // Based on the ballot score, what is the maximum discrepancy we could have for the
        // phantom CVR and given ballot?
        result = OptionalInt.of(1 - acvrScore);
      }
    } else if (cvrInfo.isPresent() && acvrInfo.isPresent()) {
      if (acvrInfo.get().consensus() == CVRContestInfo.ConsensusValue.NO) {
        // A lack of consensus for this contest between auditors is treated as if the ballot is a
        // phantom ballot.
        result = computeDiscrepancyPhantomBallot(cvrInfo.get());

      } else {
        // First, determine whether there is a difference in the votes on the CVR vs the ballot.
        // If there is no difference, there is no discrepancy.
        boolean recordsSame = cvrInfo.get().choices().equals(acvrInfo.get().choices());
        if(recordsSame){
          result = OptionalInt.empty();
        }
        else {
          // There is a difference, compute the discrepancy for this assertion (if any).
          int cvrScore = score(cvrInfo.get());
          int acvrScore = score(acvrInfo.get());
          result = OptionalInt.of(cvrScore - acvrScore);
        }
      }
    }
    // If we have determined there is a discrepancy, record the type in cvrDiscrepancies. If
    // we have found there is no discrepancy, ensure that any discrepancy record from a prior call to
    // computeDiscrepancy() for this CVR is removed. Note that if you
    // try to access CVR ID vis getCvrId() on the cvr, it will be null
    // for some inexplicable reason. So, to get the CVR ID for this
    // audited ballot, we need to call the method on the ACVR.
    if(result.isPresent()){
      LOGGER.info(String.format("[Assertion::computeDiscrepancy CVR ID %d discrepancy %d]",
              cvr.id(), result.getAsInt()));
      cvrDiscrepancy.put(cvr.id(), result.getAsInt());
    }
    else {
      cvrDiscrepancy.remove(cvr.id());
    }
    return result;
  }

  /**
   * Updates the local value of the estimated risk for this assertion.
   * @param auditedSampleCount The number of ballots sampled so far.
   * @return the new risk estimate.
   */
  public BigDecimal updateRiskMeasurement(final Integer auditedSampleCount) {
    my_current_risk = riskMeasurement(auditedSampleCount, Audit.GAMMA);
    return my_current_risk;
  }

  /**
   * Returns the worst case discrepancy possible for this assertion given that we have a
   * CVR with the relevant contest on it, and no matching ballot.
   * @param cvrInfo   Contest information on the given CVR.
   * @return The worst case discrepancy for this assertion given a CVR and no matching ballot.
   */
  private OptionalInt computeDiscrepancyPhantomBallot(final CVRContestInfo cvrInfo){
    // Compute the score for the CVR (in the context of this assertion).
    final int score = score(cvrInfo);

    // The maximum discrepancy we can have is 1 + the score assigned to the CVR. If
    // the CVR gives the vote to the assertion's winner, then we could have a 2-vote
    // overstatement if the ballot gave the vote to the loser.
    return OptionalInt.of(score + 1);
  }

  /**
   * Compute the current level of risk achieved for this assertion given a specified
   * audited sample count and gamma value. Audit.pValueApproximation is used to compute
   * this risk using the diluted margin of the assertion, and the recorded number of
   * one/two vote understatements and one/two vote overstatements.
   * @param auditedSampleCount  Number of ballots audited.
   * @param gamma               Gamma value to use in risk computation.
   * @return Level of risk achieved for the given assertion.
   */
  public BigDecimal riskMeasurement(final Integer auditedSampleCount, final BigDecimal gamma){
    if (auditedSampleCount > 0 && dilutedMargin > 0) {
      final BigDecimal result =  Audit.pValueApproximation(auditedSampleCount,
              BigDecimal.valueOf(dilutedMargin), gamma, my_one_vote_under_count,
              my_two_vote_under_count, my_one_vote_over_count, my_two_vote_over_count);

      return result.setScale(3, BigDecimal.ROUND_HALF_UP);

    } else {
      // Full risk (100%) when nothing is known
      return BigDecimal.ONE;
    }
  }

  /**
   * Computes the Score for the given vote in the context of this assertion. For details on
   * how votes are scored for assertions, refer to the Guide to RAIRE.
   * @param info   Contest information containing the vote to be scored.
   * @return Vote score (either -1, 0, or 1).
   */
  protected abstract int score(final CVRContestInfo info);
}
