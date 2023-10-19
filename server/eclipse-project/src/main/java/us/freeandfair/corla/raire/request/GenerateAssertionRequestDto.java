package us.freeandfair.corla.raire.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateAssertionRequestDto {
  private String contestName;
  private Integer totalAuditableBallots;
  private Integer timeProvisionForResult;
}
