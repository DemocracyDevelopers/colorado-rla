package au.org.democracydevelopers.model;

import org.testng.annotations.*;

import us.freeandfair.corla.persistence.Persistence;
import au.org.democracydevelopers.query.AssertionQueries;
import us.freeandfair.corla.query.Setup;


import java.util.*;

import static org.junit.Assert.assertEquals;


public class AssertionTest {

  private AssertionTest() {};


  @BeforeMethod()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

  }

  @AfterMethod()
  public void tearDown() {
    try {
      Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  @Test()
  public void testCreateNEB() {
    NEBAssertion neb = new NEBAssertion("Board of Tax and Estimation", "Alice", "Bob",
            50, 5000, 100);

    Persistence.save(neb);
    Persistence.flushAndClear();

    List<Assertion> assertions = AssertionQueries.matching("Board of Tax and Estimation");

    Assertion neb_act = assertions.get(0);
    assertEquals("Alice", neb_act.getWinner());
    assertEquals("Bob", neb_act.getLoser());
    assertEquals("Board of Tax and Estimation", neb_act.getContestName());
  }

  @Test()
  public void testCreateNEN() {
    NENAssertion nen = new NENAssertion("Board of Play", "Chuan", "Alice",
            30, 4000, 133.3, Arrays.asList("Chuan", "Alice", "Diego"));

    Persistence.save(nen);
    Persistence.flushAndClear();

    List<Assertion> assertions = AssertionQueries.matching("Board of Play");

    Assertion nen_act = assertions.get(0);
    assertEquals("Chuan", nen_act.getWinner());
    assertEquals("Alice", nen_act.getLoser());
    assertEquals("Board of Play", nen_act.getContestName());
    assertEquals(Arrays.asList("Chuan", "Alice", "Diego"), nen_act.getContinuing());
  }





}
