/*
 * Sketch of NEBAssertion class (following conventions of other CORLA classes).
 *
 */

package au.org.democracydevelopers.model;

import us.freeandfair.corla.model.CVRContestInfo;

import javax.persistence.*;
import java.util.*;


/**
 * Generic assertion for an assertion-based audit.
 *
 */
@Entity
@DiscriminatorValue("NEB")
public class NEBAssertion extends Assertion  {

  /**
   * Construct an empty NEB assertion (for persistence).
   */
  public NEBAssertion(){
    super();
  }

  /**
   * {@inheritDoc}
   */
  public NEBAssertion(String contestName, String winner, String loser, int margin, long universeSize,
                      double difficulty) {
    super(contestName, winner, loser, margin, universeSize, difficulty,  new ArrayList<>());
  }

  /**
   * {@inheritDoc}
   */
  protected int score(final CVRContestInfo info){
    // Get index of winner and loser in this CVR/ballot's ranking
    int winner_index = info.choices().indexOf(winner);
    int loser_index = info.choices().indexOf(loser);

    // If our winner is the first ranked candidate, we return a score of 1.
    if (winner_index == 0){
      return 1;
    }

    // If our winner is not mentioned on the ballot, but the loser is, we return a score of -1.
    // We also return a score of -1 if our loser is ranked higher than our winner.
    if(loser_index != -1){
      if(winner_index == -1 || loser_index < winner_index){
        return -1;
      }
    }

    // Otherwise, we return a score of 0.
    return 0;
  }


}
