/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import us.freeandfair.corla.model.Assertion;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.NEBAssertion;
import us.freeandfair.corla.model.NENAssertion;


/**
 * Queries related to Assertion entities.
 */
public final class AssertionQueries {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(AssertionQueries.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private AssertionQueries() {
    // do nothing
  }

  /**
   * Obtain the Assertions for the specified contest name.
   *
   * @param contestName      The contest name
   * @param my_universe_size The total number of ballots in the universe for this audit.
   *                         Note that this is _not_ always the same as the ballot count
   *                         for this contest - for example, if multiple audits of different
   *                         contests are being conducted in the same county. ContestResult.getBallotCount()
   *                         has the correct value.
   * @return the list of assertions defined for the contest
   */
  public static List<Assertion> matching(final String contestName, long my_universe_size) {
    // Following is an example for grabbing a single ComparisonAudit from the database
    /*final Session s = Persistence.currentSession();
    final Query q =
      s.createQuery("select ca from ComparisonAudit ca "
                    + " join ContestResult cr "
                    + "   on ca.my_contest_result = cr "
                    + " where cr.contestName = :contestName");

    q.setParameter("contestName", contestName);

    try {
      return (ComparisonAudit) q.getSingleResult();
    } catch (javax.persistence.NoResultException e ) {
      return null;
    }*/
    List<String> assumedContinuing =  Arrays.asList("TestCandidate1", "TestCandidate2", "TestWinner", "TestLoser");
    Assertion myTestNENAssertion = new NENAssertion("TestContestName", "TestWinner", "TestLoser", 100,
            10000, 42, assumedContinuing);
    Assertion myTestNEBAssertion = new NEBAssertion("TestContestName", "TestWinner", "TestLoser", 100,
    10000, 42);

    return List.of(myTestNEBAssertion, myTestNENAssertion);
  }


}
