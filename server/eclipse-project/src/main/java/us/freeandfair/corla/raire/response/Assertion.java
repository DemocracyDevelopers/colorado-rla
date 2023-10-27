package us.freeandfair.corla.raire.response;

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
}
