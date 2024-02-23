package au.org.democracydevelopers.query;

import org.hibernate.Session;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestType;
import us.freeandfair.corla.persistence.Persistence;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Queries having to do with Contest entities.
 *
 * @author Michelle Blom
 * @version 1.0.0
 */
public class ContestQueries {
    /**
     * Obtain the list of Contests matching a given contest name.
     *
     * @param contestName The contest name
     * @return the list of Contests with the given contest name, returns an empty list if no such contests exist.
     */
    public static List<Contest> matching(final String contestName) {
        final Session s = Persistence.currentSession();
        final TypedQuery<Contest> q =
                s.createQuery("select co from Contest co "
                        + " where co.my_name = :contestName", Contest.class);
        q.setParameter("contestName", contestName);

        try {
            return q.getResultList();
        } catch (javax.persistence.NoResultException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Obtain the list of IRV Contests.
     *
     * @return the complete list of IRV contests.
     */
    public static List<Contest> getAllIRV() {

        final Session s = Persistence.currentSession();
        final CriteriaBuilder cb = s.getCriteriaBuilder();
        final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
        final Root<Contest> root = cq.from(Contest.class);
        cq.select(root);
        cq.where(cb.equal(root.get("my_description"), ContestType.IRV.toString()));
        final TypedQuery<Contest> q = s.createQuery(cq);

        try {
            return q.getResultList();
        } catch (javax.persistence.NoResultException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Obtain the list of IRV Contest names.
     *
     * @return the complete list of IRV contests, duplicates removed.
     */
    public static List<String> getDistinctIRVNames() {

        final Session s = Persistence.currentSession();
        final CriteriaBuilder cb = s.getCriteriaBuilder();
        final CriteriaQuery<String> cq = cb.createQuery(String.class);
        final Root<Contest> root = cq.from(Contest.class);
        cq.select(root.get("my_name")).distinct(true);
        cq.where(cb.equal(root.get("my_description"), ContestType.IRV.toString()));
        final TypedQuery<String> q = s.createQuery(cq);

        try {
            return q.getResultList();
        } catch (javax.persistence.NoResultException e) {
            return new ArrayList<>();
        }
    }
}
