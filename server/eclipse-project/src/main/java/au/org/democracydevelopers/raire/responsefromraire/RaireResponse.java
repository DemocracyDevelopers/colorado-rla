package au.org.democracydevelopers.raire.responsefromraire;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaireResponse {
  private Metadata metadata;

  // TODO Note this might not work in the case that the String is "Err" rather than OK.
  public Map<String, AssertionPermutations> solution;
}
