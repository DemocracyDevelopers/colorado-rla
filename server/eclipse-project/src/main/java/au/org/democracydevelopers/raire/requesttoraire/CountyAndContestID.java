package au.org.democracydevelopers.raire.requesttoraire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Just a pair, for uniquely specifying a contest, as part
 * of the request to raire-service to generate assertions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountyAndContestID {
  Long countyID;
  Long contestID;
}
