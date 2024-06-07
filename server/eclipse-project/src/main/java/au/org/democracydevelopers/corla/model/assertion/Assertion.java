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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import us.freeandfair.corla.math.Audit;
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
   * colorado-rla code base, and the implementation of the methods provided in Audit, we are
   * using a BigDecimal).
   */
  @Column(name = "diluted_margin", updatable = false, nullable = false)
  protected BigDecimal dilutedMargin = BigDecimal.valueOf(0);

  /**
   * Assertion difficulty, as estimated by raire-java.
   */
  @Column(name = "difficulty", updatable = false, nullable = false)
  protected double difficulty = 0;

  /**
   * List of candidates that the assertion assumes are 'continuing' in the
   * assertion's context.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_context", joinColumns = @JoinColumn(name = "id"))
  @Column(name = "assumed_continuing", updatable = false, nullable = false)
  protected List<String> assumedContinuing = new ArrayList<>();

  // The attributes above are established by raire-service when the assertion is created and
  // stored in the database. The attributes below are updatable by colorado-rla during an audit.

  /**
   * Map between CVR ID and the discrepancy calculated for it (and its A-CVR) in the context
   * of this assertion, based on the last call to computeDiscrepancy(). Calls to computeDiscrepancy()
   * will update this map. This data is stored in the table "assertion_cvr_discrepancy" which
   * has columns "id,crv_id,discrepancy", where "id" corresponds to this Assertion's ID, "cvr_id"
   * to the ID of the CVR that is involved in the discrepancy, and "discrepancy" the value of the
   * discrepancy from -2 to 2.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "assertion_discrepancies", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "cvr_id")
  @Column(name = "discrepancy", nullable = false)
  protected Map<Long,Integer> cvrDiscrepancy = new HashMap<>();

  /**
   * The estimated number of samples we expect to need to audit this assertion, assuming no
   * further overstatements arise.
   */
  @Column(name = "optimistic_samples_to_audit", nullable = false)
  protected Integer optimistic_samples_to_audit = 0;

  /**
   * The estimated number of samples we expect to need to audit this assertion,
   * assuming overstatement continue at the current rate.
   */
  @Column(name = "estimated_samples_to_audit", nullable = false)
  protected Integer estimated_samples_to_audit = 0;

  /**
   * The number of two-vote understatements recorded against this assertion so far.
   */
  @Column(name = "two_vote_under_count", nullable = false)
  protected Integer two_vote_under_count = 0;

  /**
   * The number of one-vote understatements recorded against this assertion so far.
   */
  @Column(name = "one_vote_under_count", nullable = false)
  protected Integer one_vote_under_count = 0;

  /**
   * The number of one-vote overstatements recorded against this assertion so far.
   */
  @Column(name = "one_vote_over_count", nullable = false)
  protected Integer one_vote_over_count = 0;

  /**
   * The number of two-vote overstatements recorded against this assertion so far.
   */
  @Column(name = "two_vote_over_count", nullable = false)
  protected Integer two_vote_over_count = 0;

  /**
   * The number of discrepancies recorded so far, against this assertion, that are neither
   * understatements nor overstatements.
   */
  @Column(name = "other_count", nullable = false)
  protected Integer other_count = 0;

  /**
   * Current risk of the assertion. We initialize this risk to 1, as when we have no information
   * we assume maximum risk.
   */
  @Column(name = "current_risk", nullable = false)
  protected BigDecimal current_risk = BigDecimal.valueOf(1);

  /**
   * Construct an empty assertion (required for persistence). Note that creation and storage
   * of assertions is the responsibility of raire-service.
   */
  public Assertion(){}

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
   * For the given risk limit, compute the expected (optimistic) number of samples to audit for this
   * assertion. This calculation assumes that no further overstatements will arise. This method
   * updates the Assertion::optimistic_samples_to_audit attribute with a call to Audit.optimistic()
   * and then returns the new optimistic_samples_to_audit value.
   * @param riskLimit The risk limit of the audit.
   * @return The (optimistic) number of samples we expect we will need to sample to audit this
   * assertion.
   */
  public Integer computeOptimisticSamplesToAudit(BigDecimal riskLimit) {
    final String prefix = "[computeOptimisticSamplesToAudit]";

    LOGGER.debug(String.format("%s Calling Audit::optimistic() with parameters: risk limit " +
        "%f; diluted margin %f; gamma %f; two vote under count %d; one vote under count %d; " +
        "one vote over count %d; two vote over count %d.", prefix, riskLimit, dilutedMargin,
        Audit.GAMMA, two_vote_under_count, one_vote_under_count, one_vote_over_count,
        two_vote_over_count));

    // Call the colorado-rla audit math; update optimistic_samples_to_audit and return new value.
    optimistic_samples_to_audit = Audit.optimistic(riskLimit, dilutedMargin,
        Audit.GAMMA, two_vote_under_count, one_vote_under_count, one_vote_over_count,
        two_vote_over_count).intValue();

    LOGGER.debug(String.format("%s Computed optimistic samples to audit for Assertion %d" +
        " of %s ballots.", prefix, id, optimistic_samples_to_audit));

    return optimistic_samples_to_audit;
  }
}
