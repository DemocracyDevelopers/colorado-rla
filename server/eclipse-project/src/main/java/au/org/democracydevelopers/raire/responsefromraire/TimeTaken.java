package au.org.democracydevelopers.raire.responsefromraire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTaken {
  private Integer work;
  //todo shall we convert this to more readable format?
  private String seconds;
}
