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

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.hibernate.query.Query;
import org.hibernate.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import us.freeandfair.corla.model.Assertion;


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
   * @param contestName The contest name
   * @return the list of assertions defined for the contest
   */
  public static List<Assertion> matching(final String contestName) {
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
    return Collections.emptyList();
  }


}
