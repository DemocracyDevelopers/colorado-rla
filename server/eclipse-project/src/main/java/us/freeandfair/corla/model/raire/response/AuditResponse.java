package us.freeandfair.corla.model.raire.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuditResponse {
  private Integer contestId;
  private RaireResponse result;
}