/*
 * Sketch of abstract Assertion class (following conventions of other CORLA classes).
 *
 * Will need to implement PersistentEntity and Serializable interfaces, as shown.
 *
 * JPA hibernate annotations are speculative.
 */

package us.freeandfair.corla.model;

import com.google.gson.annotations.JsonAdapter;
import org.hibernate.annotations.Immutable;
import us.freeandfair.corla.json.ContestJsonAdapter;
import us.freeandfair.corla.persistence.PersistentEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.OptionalInt;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Immutable
@Table(name = "assertion")
@JsonAdapter(ContestJsonAdapter.class)
public abstract class Assertion implements PersistentEntity, Serializable {
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
   * Creates an Assertion for a specific contest. The assertion has a given winner, loser,
   * margin, and list of candidates that are assumed to be continuing in the assertion's
   * context.
   *
   * @param contestResult     Assertion has been created for this ContestResult.
   * @param winner            Winning candidate (from contest contestID) of the assertion.
   * @param loser             Losing candidate (from contest contestID) of the assertion.
   * @param margin            Margin of the assertion.
   * @param difficulty        Estimated difficulty of assertion.
   * @param assumedContinuing List of candidates that assertion assumes are continuing.
   */
  public Assertion(ContestResult contestResult, String winner, String loser, int margin,
                   double difficulty, List<String> assumedContinuing) {
    this.contestName = contestResult.getContestName();
    this.winner = winner;
    this.loser = loser;
    this.margin = margin;
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
}
