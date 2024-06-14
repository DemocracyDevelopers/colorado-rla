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

import us.freeandfair.corla.model.*;

import java.math.BigDecimal;
import java.util.OptionalInt;

import static us.freeandfair.corla.math.Audit.GAMMA;

/**
 * Stub of the IRVComparisonAudit class, which currently does nothing.
 */
public class IRVComparisonAudit extends ComparisonAudit {

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
   * @param auditReason The audit reason.
   *
   */
  @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod"})
  public IRVComparisonAudit(final ContestResult contestResult, final BigDecimal riskLimit,
                            final AuditReason auditReason) {
    super(contestResult, riskLimit, BigDecimal.ONE, GAMMA, auditReason);
  }

  // Does nothing - TODO.
  @Override
  protected void recalculateSamplesToAudit() {

  }

  // Does nothing - TODO.
  @Override
  public int initialSamplesToAudit() {
    return 0;
  }

  // Does nothing - TODO.
  @Override
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr, final CastVoteRecord auditedCVR) {
      return OptionalInt.of(0);
  }

  // Does nothing - TODO.
  @Override
  public BigDecimal riskMeasurement() {
    return BigDecimal.ONE;
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
