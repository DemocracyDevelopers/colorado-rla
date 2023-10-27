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
  private Long contestId;
  private Integer totalAuditableBallots;
  private Integer timeProvisionForResult;
}
