package us.freeandfair.corla.raire.requestfromclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/*
 * Information for requesting a contest's data (currently only used for assertions) from the
 * colorado-rla. It's only the contest name that we need.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestByContestNameOnly {
  private String contestName;
}
