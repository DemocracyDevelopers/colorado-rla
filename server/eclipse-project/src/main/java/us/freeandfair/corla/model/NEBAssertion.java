/*
 * Sketch of NEBAssertion class (following conventions of other CORLA classes).
 *
 */

package us.freeandfair.corla.model;


import java.util.OptionalInt;
import java.math.BigDecimal;
import java.util.List;

/**
 * Generic assertion for an assertion-based audit.
 *
 */
public class NEBAssertion extends Assertion  {

  /**
   * {@inheritDoc}
   */
  public NEBAssertion(Long contestID, String winner, String loser, BigDecimal margin,
                      List<String> assumedContinuing){
    super(contestID, winner, loser, margin, assumedContinuing);
  }

  /**
   * {@inheritDoc}
   */
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();
    return result;
  }
}
