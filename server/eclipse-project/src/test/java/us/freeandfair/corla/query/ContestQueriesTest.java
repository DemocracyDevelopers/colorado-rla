package us.freeandfair.corla.query;

import org.hibernate.Session;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


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

    List<Contest> contests = ContestQueries.matching("Board of Parks");
    assertEquals("Board of Parks", contests.get(0).name());
    assertEquals(choices, contests.get(0).choices());
  }

}
