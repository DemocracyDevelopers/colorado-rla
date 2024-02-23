package au.org.democracydevelopers.raire.requesttoraire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The request data structure for Raire's generate-assertion endpoint.
 * Matches the data in raire-service::ContestRequestByIDs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateAssertionRequest {
  private String contestName;
  private int totalAuditableBallots;
  private Integer timeProvisionForResult;
  private List<String> candidates;
  private List<CountyAndContestID> countyAndContestIDs;
}
