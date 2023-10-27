package us.freeandfair.corla.raire.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssertionResult {
  private Integer margin;
  private Double difficulty;
  private Assertion assertion;
}
