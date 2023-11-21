package us.freeandfair.corla.raire.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateAssertionRequestDto {
  private String contestName;
  private List<List<String>> votes;
  private Integer totalAuditableBallots;
  private Integer timeProvisionForResult;
}
