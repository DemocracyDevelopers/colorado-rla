package au.org.democracydevelopers.raire.responsefromraire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Assertion {
  private String type;
  private Integer winner;
  private Integer loser;

  // Should be empty or null for NEB assertions.
  private Integer[] continuing;
}
