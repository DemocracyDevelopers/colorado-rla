/*
 * Sketch of NENAssertion class (following conventions of other CORLA classes).
 *
 */


package us.freeandfair.corla.model;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

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
  @Override
  protected int score(final CVRContestInfo info){
    // Reduce the list of choices in 'info' to those that are assumed to be continuing.
    List<String> choices_left = info.choices().stream().filter(c ->
            assumedContinuing.contains(c)).collect(Collectors.toList());

    if (choices_left.isEmpty()){
      return 0;
    }

    // If our winner is the first candidate in 'choices_left' our score is 1.
    if(choices_left.get(0).equals(winner)){
      return 1;
    }

    // If our loser is the first candidate in 'choices_left' our score is -1.
    if(choices_left.get(0).equals(loser)){
      return -1;
    }

    // Otherwise, our score is 0.
    return 0;
  }


}
