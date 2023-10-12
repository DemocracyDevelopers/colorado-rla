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
  public NENAssertion(ContestResult contestResult, String winner, String loser, int margin, double difficulty,
                      List<String> assumedContinuing) {
    super(contestResult, winner, loser, margin, difficulty, assumedContinuing);
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
