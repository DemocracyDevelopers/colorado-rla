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


import org.hibernate.Session;
import us.freeandfair.corla.model.Assertion;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.NEBAssertion;
import us.freeandfair.corla.model.NENAssertion;
import us.freeandfair.corla.persistence.Persistence;

import javax.persistence.Query;
import javax.persistence.TypedQuery;


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
   * @return the list of assertions defined for the contest
   */
  public static List<Assertion> matching(final String contestName) {
    final Session s = Persistence.currentSession();
    final TypedQuery<Assertion> q =
      s.createQuery("select ca from Assertion ca "
                    + " where ca.contestName = :contestName", Assertion.class);
    q.setParameter("contestName", contestName);

    try {
      return q.getResultList();
    } catch (javax.persistence.NoResultException e ) {
      return new ArrayList<>();
    }
  }



}
