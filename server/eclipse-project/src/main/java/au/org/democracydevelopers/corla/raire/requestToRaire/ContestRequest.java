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

package au.org.democracydevelopers.corla.raire.requestToRaire;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Request (expected to be json) identifying a contest by name and listing other data:
 * - the candidates (by name),
 * - the total auditable ballots in the universe (used to calculate difficulty in raire),
 * - the time limit allowed to raire.
 * Identical to the ContestRequest in raire-service.
 * This is used directly for requesting assertion generation.
 * The get assertions request type inherits from this class and adds some other fields.
 */
public class ContestRequest {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(ContestRequest.class);

  /**
   * The name of the contest
   */
  public final String contestName;

  /**
   * The total number of ballots in the universe under audit.
   * This may not be the same as the number of ballots or CVRs in the contest, if the contest
   * is available only to a subset of voters in the universe.
   */
  public final int totalAuditableBallots;

  /**
   * List of candidate names.
   */
  public final List<String> candidates;

  /**
   * The elapsed time allowed to raire to generate the assertions, in seconds.
   * Ignored for GetAssertionsRequests.
   */
  public final double timeLimitSeconds;

  /**
   * The field name of timeLimitSeconds, used to exclude it from serialization in
   * GetAssertionsRequest. It's important that this matches the actual field name above.
   */
  protected final static String TIME_LIMIT_SECONDS = "timeLimitSeconds";

  /**
   * All args constructor.
   * @param contestName the name of the contest
   * @param totalAuditableBallots the total auditable ballots in the universe under audit.
   * @param timeLimitSeconds the elapsed time allowed for RAIRE to generate assertions, in seconds.
   * @param candidates the list of candidates by name
   */
  @ConstructorProperties({"contestName", "totalAuditableBallots", "timeLimitSeconds","candidates"})
  public ContestRequest(String contestName, int totalAuditableBallots, double timeLimitSeconds,
                        List<String> candidates) {
    final String prefix = "[ContestRequest constructor]";
    LOGGER.debug(String.format("%s Making ContestRequest for contest %s", prefix, contestName));

    this.contestName = contestName;
    this.totalAuditableBallots = totalAuditableBallots;
    this.timeLimitSeconds = timeLimitSeconds;
    this.candidates = candidates;
  }
}
