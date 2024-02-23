package au.org.democracydevelopers.raire.requesttoraire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/*
 * Information for requesting a contest's data (currently only used for assertions) from the
 * raire-service.
 *
 * This class should exactly match the class of the same name in raireservice.request.
 * The main useful field is the contest name.
 * Candidates is used to check for consistency with the stored assertions.
 * The other data is not really necessary but is returned as metadata for display with the assertions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestByContestName {
  private String contestName;
  private List<String> candidates;
  private BigDecimal riskLimit;
}
