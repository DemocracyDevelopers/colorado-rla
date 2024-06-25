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

package au.org.democracydevelopers.corla.communication.responseToColoradoRla;

import java.beans.ConstructorProperties;
import java.util.Objects;

/**
 * The response to be sent to colorado-rla after a GenerateAssertionsRequest.
 * These are identified by contest name.
 * The success response is simply a winner and an empty error string.
 * Error responses have some non-empty error and usually a winner set to "UNKNOWN".
 * Note that errors may sometimes have a real winner, though they usually don't. For example, if trimming assertions
 * times out, there will be both a statement of that error and a real winner.
 */
public final class GenerateAssertionsResponseWithErrors {
    private final String contestName;
    private final String winner;
    private final String raireError;

    /**
     * @param contestName The name of the contest.
     * @param winner      The winner of the contest, as calculated by raire.
     * @param raireError  The error message returned from raire. Empty if there was no error.
     */
    @ConstructorProperties({"contestName", "winner", "raireError"})
    public GenerateAssertionsResponseWithErrors(String contestName, String winner, String raireError) {
        this.contestName = contestName;
        this.winner = winner;
        this.raireError = raireError;
    }
}