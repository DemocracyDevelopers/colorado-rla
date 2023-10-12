/*
 * Sketch of NEBAssertion class (following conventions of other CORLA classes).
 *
 */

package us.freeandfair.corla.model;


import java.util.ArrayList;
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
  public NEBAssertion(String contestName, String winner, String loser, int margin, double dilutedMargin,
                      double difficulty) {
    super(contestName, winner, loser, margin, dilutedMargin, difficulty,  new ArrayList<String>());
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
