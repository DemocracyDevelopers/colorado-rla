/*
 * Sketch of NENAssertion class (following conventions of other CORLA classes).
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
public class NENAssertion extends Assertion {


  /**
   * {@inheritDoc}
   */
  public NENAssertion(String contestName, String winner, String loser, int margin, double dilutedMargin,
                      double difficulty, List<String> assumedContinuing) {
    super(contestName, winner, loser, margin, dilutedMargin, difficulty, assumedContinuing);
  }

  /**
   * {@inheritDoc}
   */
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR){
    OptionalInt result = OptionalInt.empty();
    return result;
  }
}
