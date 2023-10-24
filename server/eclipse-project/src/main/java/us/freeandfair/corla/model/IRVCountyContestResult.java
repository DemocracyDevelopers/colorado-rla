/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 19, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringSetConverter;
import us.freeandfair.corla.util.SuppressFBWarnings;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.Map.Entry;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;
import static us.freeandfair.corla.util.IRVVoteParsing.removeParenthesesAndRepeatedNames;

/**
 * A class representing the results for a single contest for a single county.
 * Updated for IRV contests.
 * @author Vanessa Teague
 * @version 1.0.0
 */


@Entity
@Cacheable(true)
@DiscriminatorValue("IRV")
public class IRVCountyContestResult extends CountyContestResult {

  /**
   * The "my_id" string.
   */
 // private static final String MY_ID = "my_id";

  /**
   * The "result_id" string.
   */
  // private static final String RESULT_ID = "result_id";

  /**
   * The serialVersionUID.
   */
  // private static final long serialVersionUID = 1L;

  /**
   * The ID number.
   */
  // @Id
  // @Column(updatable = false, nullable = false)
  // @GeneratedValue(strategy = GenerationType.SEQUENCE)
  // private Long my_id;

  /**
   * The version (for optimistic locking).
   */
  // @Version
  // private Long my_version;

  /**
   * The county to which this contest result set belongs.
   */
  // TODO protected?
  // @ManyToOne(optional = false, fetch = FetchType.LAZY)
  // @JoinColumn
  // private County my_county;

  /**
   * The contest.
   */
  // TODO protected?
  // @ManyToOne(optional = false, fetch = FetchType.LAZY)
  // @JoinColumn
  // private Contest my_contest;

  /**
   * The winners allowed.
   */
  // @Column(updatable = false, nullable = false)
  // private Integer my_winners_allowed;

  /**
   * The set of contest winners.
   */
  // TODO protected? Could be reset after assertion generation.
  // @Column(name = "winners", columnDefinition = "text")
  // @Convert(converter = StringSetConverter.class)
  // private Set<String> my_winners = new HashSet<>();

  /**
   * The set of contest losers.
   */
  // TODO protected? Could be reset after assertion generation.
  // @Column(name = "losers", columnDefinition = "text")
  // @Convert(converter = StringSetConverter.class)
  // private Set<String> my_losers = new HashSet<>();

  /**
   * A map from choices to vote totals.
   */
  // TODO protected? Could be reset after assertion generation.
  // @ElementCollection(fetch = FetchType.EAGER)
  // @CollectionTable(name = "county_contest_vote_total",
  //                  joinColumns = @JoinColumn(name = RESULT_ID,
  //                                            referencedColumnName = MY_ID))
  // @MapKeyColumn(name = "choice")
  // @Column(name = "vote_total")
  // private Map<String, Integer> my_vote_totals = new HashMap<>();

  /**
   * The minimum pairwise margin between a winner and a loser.
   */
  // TODO protected? Could be reset after assertion generation.
  // private Integer my_min_margin;

  /**
   * The maximum pairwise margin between a winner and a loser.
   */
  // TODO protected? Could be set to -1 so nobody gets confused? Not relevant to IRV.
  // private Integer my_max_margin;

  /**
   * The total number of ballots cast in this county.
   */
  // private Integer my_county_ballot_count = 0;

  /**
   * The total number of ballots cast in this county that contain this contest.
   */
  // private Integer my_contest_ballot_count = 0;

  /**
   * Constructs a new empty CountyContestResult (solely for persistence).
   */
  public IRVCountyContestResult() {
    super();
  }

  /**
   * Constructs a new CountyContestResult for the specified county ID and
   * contest.
   *
   * @param the_county The county.
   * @param the_contest The contest.
   */
  public IRVCountyContestResult(final County the_county, final Contest the_contest) {
    super(the_county, the_contest);
  }

  /**
   * {@inheritDoc}
   */
  // @Override
  // public Long id() {
  //   return my_id;
  // }

  /**
   * {@inheritDoc}
   */
  // @Override
  // public void setID(final Long the_id) {
  //   my_id = the_id;
  // }
  
  /**
   * {@inheritDoc}
   */
  /*
  @Override
  public Long version() {
    return my_version;
  } */
  
  /**
   * @return the county for this CountyContestResult.
   */
  /*
  public County county() {
    return my_county;
  }
  */
  
  /**
   * @return the contest for this CountyContestResult.
   */
  /*
  public Contest contest() {
    return my_contest;
  }
  */

  /**
   * @return the winners for thie CountyContestResult.
   */
  // TODO redo after assertion generation to return IRV winner?
  // even then, we probably just need to override my_winners, not this function.
  /*
  public Set<String> winners() {
    return Collections.unmodifiableSet(my_winners);
  }
  */

  /**
   * @return the losers for this CountyContestResult.
   */
  // TODO - same comment as winners.
  /*
  public Set<String> losers() {
    return Collections.unmodifiableSet(my_losers);
  }
   */
  
  /**
   * @return a map from choices to vote totals.
   */
  /* TODO Not clear what to do here. Maybe first preference totals in case anyone wants to sanity check them?
  public Map<String, Integer> voteTotals() {
    return Collections.unmodifiableMap(my_vote_totals);
  }
  */

  /**
   * @return a list of the choices in descending order by number of votes
   * received.
   */
  // Deleted.
  // public List<String> rankedChoices() {

  /**
   * Change a choice name as part of Canonicalization.
   */
  // public void updateChoiceName(final String oldName,

  /**
   * Reset choice names as part of parsing. Initially, IRV choice names look like
   * "Alice(1)" "Alice(2)" "Bob(1)" "Bob(2)"
   * This will remove parenthesized ranks and take one instance of each name.
   */
  public void removeParenthesesFromChoiceNames() {
    my_vote_totals.clear();
    List<Choice> updatedChoices = removeParenthesesAndRepeatedNames(my_contest.getChoices());
    my_contest.setChoices(updatedChoices);
    my_vote_totals.clear();
    for (final Choice c : my_contest.choices()) {
      if (!c.fictitious()) {
        my_vote_totals.put(c.name(), 0);
      }
    }
  }
  /**
   * Compute the pairwise margin between the specified choices.
   * If the first choice has more votes than the second, the
   * result will be positive; if the second choie has more 
   * votes than the first, the result will be negative; if they
   * have the same number of votes, the result will be 0.
   * 
   * @param the_first_choice The first choice.
   * @param the_second_choice The second choice.
   * @return the pairwise margin between the two choices, as
   * an OptionalInt (empty if the margin cannot be calculated).
   */
  // Deleted.
  // public OptionalInt pairwiseMargin(final String the_first_choice,

  /**
   * Computes the margin between the specified choice and the next choice. 
   * If the specified choice is the last choice, or is not a valid choice,
   * the margin is empty. 
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  // Deleted.
  // public OptionalInt marginToNearestLoser(final String the_choice) {

  /**
   * Computes the diluted margin between the specified choice and the nearest
   * loser. If the specified choice is the last choice or is not a valid 
   * choice, or the margin is undefined, the result is null.
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  // TODO Only for reporting. Not clear whether we need to override for IRV.
  // public BigDecimal countyDilutedMarginToNearestLoser(final String the_choice) {

  /**
   * Computes the diluted margin between the specified choice and the nearest
   * loser. If the specified choice is the last choice or is not a valid 
   * choice, or the margin is undefined, the result is null.
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  // Unused.
  // public BigDecimal contestDilutedMarginToNearestLoser(final String the_choice) {

  /**
   * @return the number of winners allowed in this contest.
   */
  /*
  public Integer winnersAllowed() {
    return my_winners_allowed;
  }
  */
  /**
   * @return the number of ballots cast in this county that include this contest.
   */
  /*
  public Integer contestBallotCount() {
    return my_contest_ballot_count;
  }
   */
  
  /**
   * @return the number of ballots cast in this county.
   */
  /*
  public Integer countyBallotCount() {
    return my_county_ballot_count;
  }

   */
  
  /**
   * @return the maximum margin between a winner and a loser.
   */
  /*
  public Integer maxMargin() {
    return my_max_margin;
  }

   */
  
  /**
   * @return the minimum margin between a winner and a loser.
   */
  /*
  public Integer minMargin() {
    return my_min_margin;
  }

   */
  
  /**
   * @return the county diluted margin for this contest, defined as the
   * minimum margin divided by the number of ballots cast in the county.
   * @exception IllegalStateException if no ballots have been counted.
   */
  // Unused.
  // public BigDecimal countyDilutedMargin() {

  /**
   * @return the diluted margin for this contest, defined as the
   * minimum margin divided by the number of ballots cast in this county
   * that contain this contest.
   * @exception IllegalStateException if no ballots have been counted.
   */
  // Unused.
  // public BigDecimal contestDilutedMargin() {

  /**
   * Reset the vote totals and all related data in this CountyContestResult.
   */
  /*
  public void reset() {
    my_winners.clear();
    my_losers.clear();
    for (final String s : my_vote_totals.keySet()) {
      my_vote_totals.put(s, 0);
    }
    updateResults();
  }

   */
  
  /**
   * Update the vote totals using the data from the specified CVR.
   * 
   * @param the_cvr The CVR.
   */
 //  public void addCVR(final CastVoteRecord the_cvr) {

  /**
   * Updates the stored results.
   */
  // TODO probably needs to be overriden for IRV, if we're storing IRV winner info here after getting
  // assertions. Indeed, this would be the right thing to call after generating the assertions - use the
  // assertion data to find winner etc.
  // public void updateResults() {

  /**
   * Calculates all the pairwise margins using the vote totals.
   */

 //  private void calculateMargins() {

  /**
   * @return a String representation of this contest.
   */
  /*
  @Override
  public String toString() {
    return "CountyContestResult [id=" + id() + "]";
  }
   */

  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  // @Override
  // public boolean equals(final Object the_other) {

  /**
   * @return a hash code for this object.
   */
  // @Override
  // public int hashCode() {

  /**
   * A reverse integer comparator, for sorting lists of integers in reverse.
   */
}
