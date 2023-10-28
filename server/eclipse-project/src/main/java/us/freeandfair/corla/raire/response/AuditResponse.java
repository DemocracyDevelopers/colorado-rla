package us.freeandfair.corla.raire.response;

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
