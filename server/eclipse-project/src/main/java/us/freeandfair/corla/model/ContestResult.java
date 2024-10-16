package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringSetConverter;

/**
 * A class representing the results for a contest across counties.
 * A roll-up  of CountyContestResults
 * Many of these values, including the tallies, do not make sense for IRV, or are not correctly
 * initialized for IRV. These are generally not used unless set after assertion generation.
 */
@Entity
@Cacheable(true)
@Table(name = "contest_result",
       uniqueConstraints = {
         @UniqueConstraint(columnNames = {"contest_name"}) },
       indexes = { @Index(name = "idx_cr_contest",
                          columnList = "contest_name",
                          unique = true)})
@SuppressWarnings({"PMD.ExcessiveImports"}) // you complain if we import x.y.z.*, so....
public class ContestResult implements PersistentEntity, Serializable {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(ContestResult.class);

  /**
   * text
   */
  private static final String TEXT = "text";

  /**
   * The "id" string.
   */
  private static final String ID = "id";

  /**
   * The "result_id" string.
   */
  private static final String RESULT_ID = "result_id";

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier.
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
   * The winners allowed.
   */
  @Column(name = "winners_allowed")
  private Integer winnersAllowed;

  /**
   * The set of contest winners. Not correctly initialized for IRV unless externally
   * set after assertion generation.
   */
  @Column(name = "winners", columnDefinition = TEXT)
  @Convert(converter = StringSetConverter.class)
  private final Set<String> winners = new HashSet<>();

  /**
   * The set of contest losers. Not correctly initialized for IRV unless externally
   * set after assertion generation.
   */
  @Column(name = "losers", columnDefinition = TEXT)
  @Convert(converter = StringSetConverter.class)
  private final Set<String> losers = new HashSet<>();

  /**
   * A map from choices to vote totals. Not meaningful for IRV.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "contest_vote_total",
                   joinColumns = @JoinColumn(name = RESULT_ID,
                                             referencedColumnName = ID))
  @MapKeyColumn(name = "choice")
  @Column(name = "vote_total")
  private  Map<String, Integer> vote_totals = new HashMap<>();

  /**
   * A ContestResult has many counties - supporting auditing multi-county
   * contests. Counties have many ContestResults (and many Contests)
   **/
  @ManyToMany()
  @JoinTable(name = "counties_to_contest_results",
             joinColumns = { @JoinColumn(name = "contest_result_id") },
             inverseJoinColumns = { @JoinColumn(name = "county_id") })
  private final Set<County> counties = new HashSet<>();


  /**
   *  A ContestResult has many Contests through "contests_to_contest_results"
   *  Contests are many in the db because each county has their own, just 'cause
   */
  @OneToMany()
  @JoinTable(name = "contests_to_contest_results",
             joinColumns = { @JoinColumn(name = "contest_result_id") },
             inverseJoinColumns = { @JoinColumn(name = "contest_id") })
  private final Set<Contest> contests = new HashSet<>();

  /**
   * The contest name.
   */
  @Column(name = "contest_name", nullable = false)
  private String contestName;

  /**
   * The margin divided by total number of ballots cast. Not correct, and not used, for IRV.
   */
  @Column(name = "diluted_margin")
  private BigDecimal dilutedMargin;

  /**
   * The smallest margin between any winner and loser of the contest. Not correct, and not used,
   * for IRV.
   */
  @Column(name = "min_margin")
  private Integer minMargin;

  /**
   * The largest margin between any winner and loser of the contest. Not correct, and not used,
   * for IRV.
   */
  @Column(name = "max_margin")
  private Integer maxMargin;

  /**
   * The number of ballots cast for this contest
   */
  @Column(name = "ballot_count")
  private Long ballotCount;

  /**
   * AuditReason
   */
  @Column(name = "audit_reason")
  private AuditReason auditReason;

  /**
   * Constructs a new empty ContestResult (solely for persistence).
   */
  public ContestResult() {
    super();
  }

  /**
   * Constructs a new ContestResult with the specified contestName. The
   * contestName is what links Contests together (along with one
   * ContestResult).
   *
   * @param contestName The contest.
   */
  public ContestResult(final String contestName) {
    super();
    this.contestName = contestName;
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
    this.id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return version;
  }

  /**
   * @return the contest name.
   */
  public String getContestName() {
    return this.contestName;
  }

  /**
   * @return the AuditReason.
   */
  public AuditReason getAuditReason() {
    return this.auditReason;
  }

  /** set it **/
  public void setAuditReason(final AuditReason auditReason) {
    this.auditReason = auditReason;
  }

  /**
   * set the set of counties
   */
  public boolean addCounties(final Set<County> cs) {
    return this.counties.addAll(cs);
  }

  /**
   * @return the counties related to this contestresult.
   */
  public Set<County> getCounties() {
    return Collections.unmodifiableSet(this.counties);
  }

  /**
   * set the set of contests
   */
  public boolean addContests(final Set<Contest> cs) {
    return this.contests.addAll(cs);
  }

  /**
   * @return the contests related to this ContestResult
   * (should be contests that have the same name as this.contestName)
   **/
  public Set<Contest> getContests() {
    return Collections.unmodifiableSet(this.contests);
  }

  /**
   * getter
   */
  public int winnersAllowed() {
    return this.winnersAllowed;
  }

  /**
   * setter
   */
  public void setWinnersAllowed(final int n) {
    this.winnersAllowed = n;
  }

  /**
   * @param county the county owning the contest you want
   * @return the contest belonging to county
   */
  public Contest contestFor(final County county) {
    final Optional<Contest> contestMaybe = getContests().stream()
      .filter(c -> c.county().id().equals(county.id()))
      .findFirst(); // should only be one?

    if (contestMaybe.isPresent()) {
      return contestMaybe.get();
    } else {
      return null;
    }
  }

  /**
   * @param winners a set of the choices that won the contest
   */
  public void setWinners(final Set<String> winners) {
    this.winners.clear();
    this.winners.addAll(winners);
  }

  /**
   * @return the winners for this ContestResult.
   */
  public Set<String> getWinners() {
    return Collections.unmodifiableSet(this.winners);
  }

  /**
   * @param losers a set of the choices that did not win the contest
   */
  public void setLosers(final Set<String> losers) {
    this.losers.clear();
    this.losers.addAll(losers);
  }

  /**
   * @return the losers for this ContestResult.
   */
  public Set<String> getLosers() {
    return Collections.unmodifiableSet(this.losers);
  }

  /**
   * @return a map from choices to vote totals. Not relevant or correct for IRV.
   */
  public Map<String, Integer> getVoteTotals() {
    return Collections.unmodifiableMap(this.vote_totals);
  }

  /** data access helper **/
  public Integer totalVotes() {
    return getVoteTotals().values().stream().reduce(0, Integer::sum);
  }

  /**
   * @param voteTotals a map from choices to vote totals. Not relevant or correct for IRV.
   */
  public void setVoteTotals(final Map<String, Integer> voteTotals) {
    this.vote_totals = voteTotals;
  }

  /**
   * set dilutedMargin.
   */
  public void setDilutedMargin(final BigDecimal dilutedMargin) {
    this.dilutedMargin = dilutedMargin;
  }

  /**
   * The diluted margin (μ) of this ContestResult.  Not relevant or correct for IRV unless it has
   * been externally set after assertion generation.
   */
  public BigDecimal getDilutedMargin() {
    return this.dilutedMargin;
  }

  /**
   * set minMargin.
   */
  public void setMinMargin(final Integer minMargin) {
    this.minMargin = minMargin;
  }

  /**
   * The smallest margin between any winner and loser of the contest. Not relevant or correct for
   * IRV unless it has been externally set after assertion generation.
   */
  public Integer getMinMargin() {
    return this.minMargin;
  }

  /**
   * set maxMargin. Not relevant or correct for IRV.
   */
  public void setMaxMargin(final Integer maxMargin) {
    this.maxMargin = maxMargin;
  }

  /**
   * The largest margin between any winner and loser of the contest. Not relevant or correct for
   * IRV.
   */
  public Integer getMaxMargin() {
    return this.maxMargin;
  }

  /**
   * set ballotCount
   */
  public void setBallotCount(final Long n) {
    this.ballotCount = n;
  }

  /**
   * what is the ballotCount?
   */
  public Long getBallotCount() {
    return this.ballotCount;
  }

  /**
   * The set of county ids related to this ContestResult
   */
  public Set<Long> countyIDs() {
    return this.getCounties().stream()
      .map(x -> x.id())
      .collect(Collectors.toSet());
  }

  /**
   * The set of contest ids related to this ContestResult
   */
  public Set<Long> contestIDs() {
    return this.getContests().stream()
      .map(x -> x.id())
      .collect(Collectors.toSet());
  }

  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "ContestResult [id=" + id() + " contestName=" + getContestName() + "]";
  }

  /**
   * Compare this object with another for equivalence.
   *
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof ContestResult) {
      final ContestResult other_result = (ContestResult) the_other;
      // compare by database ID, since that is the only
      // context in which they can reasonably be compared
      result &= nullableEquals(other_result.id(), id());
    } else {
      result = false;
    }
    return result;
  }

  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return id().hashCode();
  }
}
