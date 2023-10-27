package us.freeandfair.corla.raire.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaireResponse {
  private JsonNode metadata;
  public Map<String, AssertionPermutations> solution;




}
