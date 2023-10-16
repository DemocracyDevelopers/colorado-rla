/*
 * Sketch of NENAssertion class (following conventions of other CORLA classes).
 *
 */


package us.freeandfair.corla.model;


import javax.persistence.*;
import java.util.OptionalInt;
import java.util.List;

/**
 * Generic assertion for an assertion-based audit.
 *
 */
@Entity
@DiscriminatorValue("NEN")
public class NENAssertion extends Assertion {

  /**
   * Construct an empty NEN assertion (for persistence).
   */
  public NENAssertion(){
    super();
  }

  /**
   * {@inheritDoc}
   */
  public NENAssertion(String contestName, String winner, String loser, int margin, long universeSize,
                      double difficulty, List<String> assumedContinuing) {
    super(contestName, winner, loser, margin, universeSize, difficulty, assumedContinuing);
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
