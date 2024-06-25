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

package au.org.democracydevelopers.corla.communication.responseFromRaire;

/**
 * The success response when a ContestRequest is sent to raire's generate-assertions endpoint. This
 * simply returns the winner, as calculated by raire, along with the name of the contest for which
 * the initial request was made.
 * This record is identical to the record of the same name in raire-service. Used for
 * deserialization.
 *
 * @param contestName The name of the contest.
 * @param winner      The winner of the contest, as calculated by raire.
 */
public record GenerateAssertionsResponse(String contestName, String winner) {
}