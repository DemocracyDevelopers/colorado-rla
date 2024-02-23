package au.org.democracydevelopers.raire.responsefromraire;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuditResponse {
  private String contestName;
  private RaireResponse result;
}
