/*
 * Sketch of abstract Assertion class (following conventions of other CORLA classes).
 *
 * Will need to implement PersistentEntity and Serializable interfaces, as shown.
 *
 * JPA hibernate annotations are speculative.
 */

package us.freeandfair.corla.model;

import com.google.gson.annotations.JsonAdapter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Immutable;
import us.freeandfair.corla.json.ContestJsonAdapter;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.persistence.PersistentEntity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.OptionalInt;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Immutable
@Table(name = "assertion")
@JsonAdapter(ContestJsonAdapter.class)
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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
  protected double dilutedMargin;

  /**
   * Assertion difficulty, as estimated by RAIRE.
   */
  @Column(name = "difficulty", nullable = false)
  protected double difficulty;

  /**
   * List of candidates that the assertion assumes are `continuing' in the
   * assertion's context.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @OrderColumn(name = "index")
  @CollectionTable(name = "assertion_context",
          uniqueConstraints= @UniqueConstraint(columnNames={"assertion_id","name"}),
          joinColumns = @JoinColumn(name = "assertion_id",
                  referencedColumnName = "id"))
  protected List<String> assumedContinuing;

  /**
   * The number of samples to audit overall assuming no further overstatements.
   */
  @Column(nullable = false)
  private Integer my_optimistic_samples_to_audit = 0;

  /**
   * The expected number of samples to audit overall assuming overstatements
   * continue at the current rate.
   */
  @Column(nullable = false)
  private Integer my_estimated_samples_to_audit = 0;

  /**
   * The number of two-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_under_count = 0;

  /**
   * The number of one-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_under_count = 0;

  /**
   * The number of one-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_over_count = 0;

  /**
   * The number of two-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_over_count = 0;

  /**
   * The number of discrepancies recorded so far that are neither
   * understatements nor overstatements.
   */
  @Column(nullable = false)
  private Integer my_other_count = 0;

  /**
   * Creates an Assertion for a specific contest. The assertion has a given winner, loser,
   * margin, and list of candidates that are assumed to be continuing in the assertion's
   * context.
   *
   * @param contestName       Assertion has been created for this contest.
   * @param winner            Winning candidate (from contest contestID) of the assertion.
   * @param loser             Losing candidate (from contest contestID) of the assertion.
   * @param margin            Margin of the assertion.
   * @param dilutedMargin     Diluted margin of the assertion.
   * @param difficulty        Estimated difficulty of assertion.
   * @param assumedContinuing List of candidates that assertion assumes are continuing.
   */
  public Assertion(String contestName, String winner, String loser, int margin,
                   double dilutedMargin, double difficulty, List<String> assumedContinuing) {
    this.contestName = contestName;
    this.winner = winner;
    this.loser = loser;
    this.margin = margin;
    this.dilutedMargin = dilutedMargin;
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

  public void setDilutedMargin(double dilutedMargin) { this.dilutedMargin = dilutedMargin; }

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
  public Integer computeOptimisticSamplesToAudit (BigDecimal riskLimit, long universe_size) {

    // VT: We could also have a private field that stores the diluted margin.
    BigDecimal dilutedMargin = BigDecimal.valueOf(margin / (double) universe_size);

    my_optimistic_samples_to_audit = Audit.optimistic(riskLimit, dilutedMargin, Audit.GAMMA,
            my_two_vote_under_count, my_one_vote_under_count, my_one_vote_over_count, my_two_vote_over_count).intValue();

    return my_optimistic_samples_to_audit;
  }

  /* Important thing to think about: possibly we want to re-call computeOptimisticSamplesToAudit first,
   * to make sure that is updated, just in case someone calls Estimated without having called Optimistic
   * immediately prior. OTOH perhaps that's bad because it means that calling computeEstimatedSamplesToAudit
   * will have the side-effect of changing my_optimistic_samples_to_audit. I have therefore left it as it is.
   */
  public Integer computeEstimatedSamplesToAudit (BigDecimal riskLimit, long universe_size, int auditedSampleCount) {

    var scalingFac = scalingFactor(auditedSampleCount, my_one_vote_over_count, my_two_vote_over_count);

    if (my_one_vote_over_count + my_two_vote_over_count == 0) {
      LOGGER.debug("[computeEstimatedSamplesToAudit: zero overcounts]");
      my_estimated_samples_to_audit = my_optimistic_samples_to_audit;
    } else {
      LOGGER.debug(String.format("[IRVComparisonAudit::recalculateSamplesToAudit: non-zero overcounts, using scaling factor %s]", scalingFac));
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
   * Computes the over/understatement represented by the specified CVR and ACVR.
   * This method returns an optional int that, if present, indicates a discrepancy.
   * There are 5 possible types of discrepancy: -1 and -2 indicate 1- and 2-vote
   * understatements; 1 and 2 indicate 1- and 2- vote overstatements; and 0
   * indicates a discrepancy that does not count as either an under- or
   * overstatement for the RLA algorithm, but nonetheless indicates a difference
   * between ballot interpretations.
   *
   * @param cvr        The CVR that the machine saw
   * @param auditedCVR The ACVR that the human audit board saw
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  public abstract OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                                 final CastVoteRecord auditedCVR);

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
    final Integer overstatements = one_vote_overstatements + two_vote_overstatements;
    if (auditedSamples.equals(BigDecimal.ZERO)) {
      return BigDecimal.ONE;
    } else {
      return BigDecimal.ONE.add(BigDecimal.valueOf(overstatements).divide(auditedSamples, MathContext.DECIMAL128));
    }
  }

}
