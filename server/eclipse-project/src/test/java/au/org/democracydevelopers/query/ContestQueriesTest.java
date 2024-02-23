package au.org.democracydevelopers.query;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.Setup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;


public class ContestQueriesTest {

  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();

  }

  @AfterTest()
  public void tearDown() {
    try {
    Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }


  @Test()
  public void testContestMatching() {
    County cty = CountyQueries.fromString("Boulder");

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Board of Parks", cty, "PLURALITY", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);
    Persistence.flushAndClear();

    List<Contest> contests = au.org.democracydevelopers.query.ContestQueries.matching("Board of Parks");
    assertEquals("Board of Parks", contests.get(0).name());
    assertEquals(choices, contests.get(0).choices());
  }

  /*
   * Test that a plurality contest is NOT included in the IRV list.
   */
  @Test()
  public void testGetIRVNoPlurality() {
    County cty = CountyQueries.fromString("Boulder");

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Board of Works", cty, "PLURALITY", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);
    Persistence.flushAndClear();

    List<Contest> contests = au.org.democracydevelopers.query.ContestQueries.getAllIRV();
    List<String> contestNames = contests.stream().map(Contest::name).collect(Collectors.toList());
    assert(!contestNames.contains(c1.name()));

    List<String> contestNamesFromDBQuery = au.org.democracydevelopers.query.ContestQueries.getDistinctIRVNames();
    assert(!contestNamesFromDBQuery.contains(c1.name()));
  }

  /*
   * Test that an IRV contest is included in the IRV list.
   */
  @Test()
  public void testGetIRVIncludesIRV() {
    County cty = CountyQueries.fromString("Boulder");

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Tree committee", cty, "IRV", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);
    Persistence.flushAndClear();

    List<Contest> contests = au.org.democracydevelopers.query.ContestQueries.getAllIRV();
    List<String> contestNames = contests.stream().map(Contest::name).collect(Collectors.toList());
    assert(contestNames.contains(c1.name()));

    List<String> contestNamesFromDBQuery = au.org.democracydevelopers.query.ContestQueries.getDistinctIRVNames();
    assert(contestNamesFromDBQuery.contains(c1.name()));
  }

  /*
   * Test that duplicate IRV contest names are removed when all IRV contests are queried.
   */
  @Test()
  public void testGetIRVRemovesDuplicates() {
    County cty = CountyQueries.fromString("Boulder");
    County cty2 = CountyQueries.fromString("Arapahoe");

    List<String> candidates = Arrays.asList("Alice", "Bob", "Chuan", "Diego");

    List<Choice> choices = candidates.stream().map(c -> { return new Choice(c,
            "", false, false);}).collect(Collectors.toList());

    Contest c1 = new Contest("Tree committee", cty, "IRV", choices, 1,
            1, 0);
    Contest c2 = new Contest("Tree committee", cty2, "IRV", choices, 1,
            1, 0);

    Persistence.saveOrUpdate(c1);
    Persistence.saveOrUpdate(c2);
    Persistence.flushAndClear();

    List<Contest> contests = au.org.democracydevelopers.query.ContestQueries.getAllIRV();
    List<String> contestNames = contests.stream().map(Contest::name).collect(Collectors.toList());
    assert(contestNames.contains(c1.name()));

    List<String> contestNamesFromDBQuery = au.org.democracydevelopers.query.ContestQueries.getDistinctIRVNames();
    List<String> treeCommitteeMatches = contestNamesFromDBQuery.stream().filter(n -> n.equals("Tree committee")).collect(Collectors.toList());
    assert(treeCommitteeMatches.size() == 1);
  }
}
