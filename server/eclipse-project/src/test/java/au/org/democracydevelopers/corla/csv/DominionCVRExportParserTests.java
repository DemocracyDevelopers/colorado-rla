/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire 
service (https://github.com/DemocracyDevelopers/raire-service), in order to 
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.csv;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.vote.IRVParsingException;
import au.org.democracydevelopers.corla.testUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.*;

import static au.org.democracydevelopers.corla.testUtils.*;
import static org.testng.Assert.*;

import static us.freeandfair.corla.query.CastVoteRecordQueries.getMatching;
import static us.freeandfair.corla.query.ContestQueries.forCounties;
import static us.freeandfair.corla.query.CountyQueries.fromString;

/**
 * Test a variety of IRV-related parsing issues for CVRs, including:
 * - some basic small test cases that can be verified by eye,
 * - a constructed test with invalid IRV ballots to check they are accepted and properly
 *   interpreted.
 * - a real example from Boulder '23, with a mix of IRV and plurality contests
 * - some examples with invalid headers, to ensure they are rejected. These match the applicable
 *   examples from IRVPreferenceTests.java, plus a bad plurality "Vote For= " with a non-integer.
 * - some examples to test the broader class of Write In strings.
 */
public class DominionCVRExportParserTests {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(DominionCVRExportParserTests.class);

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres
      = new PostgreSQLContainer<>("postgres:15-alpine")
      // None of these actually have to be the same as the real database (except its name), but this
      // makes it easy to match the setup scripts.
      .withDatabaseName("corla")
      .withUsername("corlaadmin")
      .withPassword("corlasecret")
      .withInitScript("corlaInit.sql");

  /**
   * Error message to match.
   */
  private static final String badNumsRegexp = "Unexpected or uninterpretable numbers in header:.*";

  /**
   * Blank properties for submitting to the DominionCVRExportParser instance.
   */
  private static final Properties blank = new Properties();

  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Properties hibernateProperties = new Properties();
    hibernateProperties.setProperty("hibernate.driver", "org.postgresql.Driver");
    hibernateProperties.setProperty("hibernate.url", postgres.getJdbcUrl());
    hibernateProperties.setProperty("hibernate.user", postgres.getUsername());
    hibernateProperties.setProperty("hibernate.pass", postgres.getPassword());
    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
    Persistence.setProperties(hibernateProperties);
    Persistence.beginTransaction();

  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  /**
   * Simple test of successful parsing of a tiny IRV test example, ThreeCandidatesTenVotes.csv.
   * Tests that all the metadata and all the votes are correct.
   * @throws IOException never.
   */
  @Test
  public void parseThreeCandidatesTenVotesSucceeds() throws IOException {
    testUtils.log(LOGGER, "parseThreeCandidatesTenVotesSucceeds");
    Path path = Paths.get(TINY_CSV_PATH + "ThreeCandidatesTenVotes.csv");
    Reader reader = Files.newBufferedReader(path);
    County saguache = fromString("Saguache");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, saguache, blank, true);
    assertTrue(parser.parse().success);

    // There should be one contest, the one we just read in.
    List<Contest> contests = forCounties(Set.of(saguache));
    assertEquals(1, contests.size());
    Contest contest = contests.get(0);

    // Check basic data
    assertEquals(contest.name(), "TinyExample1");
    assertEquals(contest.description(), ContestType.IRV.toString());
    assertEquals(contest.choices().stream().map(Choice::name).collect(Collectors.toList()),
        List.of("Alice","Bob","Chuan"));

    // Votes allowed should be 3 (because there are 3 ranks), whereas winners=1 always for IRV.
    assertEquals(contest.votesAllowed().intValue(), 3);
    assertEquals(contest.winnersAllowed().intValue(), 1);

    // There are 10 votes:
    // 3 Alice, Bob, Chuan
    // 3 Alice, Chuan, Bob
    // 1 Bob, Alice, Chuan
    // 3 Bob, Chuan, Alice
    List<CVRContestInfo> cvrs = getMatching(saguache.id(),  CastVoteRecord.RecordType.UPLOADED)
        .map(cvr -> cvr.contestInfoForContest(contest)).collect(Collectors.toList());
    assertEquals(10, cvrs.size());
    assertEquals(List.of("Alice","Bob","Chuan"), cvrs.get(0).choices());
    assertEquals(List.of("Alice","Bob","Chuan"), cvrs.get(1).choices());
    assertEquals(List.of("Alice","Bob","Chuan"), cvrs.get(2).choices());
    assertEquals(List.of("Alice","Chuan","Bob"), cvrs.get(3).choices());
    assertEquals(List.of("Alice","Chuan","Bob"), cvrs.get(4).choices());
    assertEquals(List.of("Alice","Chuan","Bob"), cvrs.get(5).choices());
    assertEquals(List.of("Bob","Alice","Chuan"), cvrs.get(6).choices());
    assertEquals(List.of("Chuan","Alice","Bob"), cvrs.get(7).choices());
    assertEquals(List.of("Chuan","Alice","Bob"), cvrs.get(8).choices());
    assertEquals(List.of("Chuan","Alice","Bob"), cvrs.get(9).choices());
  }

  /**
   * Second simple test of successful parsing of a tiny IRV test example, GuideToRAIREExample3.csv.
   * Test that all the metadata are correct, then spot-check some of the votes.
   * @throws IOException never.
   */
  @Test
  public void parseGuideToRaireExample3() throws IOException {
    testUtils.log(LOGGER, "parseGuideToRaireExample3");
    Path path = Paths.get(TINY_CSV_PATH + "GuideToRAIREExample3.csv");
    Reader reader = Files.newBufferedReader(path);
    County montezuma = fromString("Montezuma");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, montezuma, blank, true);
    assertTrue(parser.parse().success);

    // There should be one contest, the one we just read in.
    List<Contest> contests = forCounties(Set.of(montezuma));
    assertEquals(1, contests.size());
    Contest contest = contests.get(0);

    // Check basic data
    assertEquals(contest.name(), "Example3");
    assertEquals(contest.description(), ContestType.IRV.toString());
    assertEquals(contest.choices().stream().map(Choice::name).collect(Collectors.toList()),
        List.of("A", "B", "C", "D"));

    // Votes allowed should be 3 (because there are 3 ranks), whereas winners=1 always for IRV.
    assertEquals(contest.votesAllowed().intValue(), 4);
    assertEquals(contest.winnersAllowed().intValue(), 1);

    // There are 225 votes. Spot-check some of them.
    List<CVRContestInfo> cvrs = getMatching(montezuma.id(), CastVoteRecord.RecordType.UPLOADED)
        .map(cvr -> cvr.contestInfoForContest(contest)).collect(Collectors.toList());
    assertEquals(225, cvrs.size());
    assertEquals(List.of("C", "D"), cvrs.get(0).choices());
    assertEquals(List.of("C", "D"), cvrs.get(44).choices());
    assertEquals(List.of("C", "B", "D"), cvrs.get(45).choices());
    assertEquals(List.of("C", "B", "D"), cvrs.get(84).choices());
    assertEquals(List.of("A", "B", "C", "D"), cvrs.get(85).choices());
    assertEquals(List.of("A", "B", "C", "D"), cvrs.get(184).choices());
    assertEquals(List.of("B", "D", "C"), cvrs.get(185).choices());
    assertEquals(List.of("B", "D", "C"), cvrs.get(185).choices());
    assertEquals(List.of("B", "D", "C"), cvrs.get(224).choices());
  }


  /**
   * Test of successful parsing of a file with valid headers but invalid IRV votes - it includes
   * duplicate candidates and skipped or repeated ranks.
   * TODO Clarify whether we should reject or interpret invalid IRV CVR uploads.
   * See Issue <a href="https://github.com/DemocracyDevelopers/colorado-rla/issues/106">...</a>
   * It's possible that this should reject, rather than interpret, these invalid IRV ballots.
   * At the moment, this checks for correct metadata and correct valid interpretation of all votes.
   * @throws IOException if there are file I/O issues.
   */
  @Test
  public void parseThreeCandidatesTenInvalidVotesSucceeds() throws IOException {
    testUtils.log(LOGGER, "parseThreeCandidatesTenInvalidVotesSucceeds");
    Path path = Paths.get(TINY_CSV_PATH + "ThreeCandidatesTenInvalidVotes.csv");
    Reader reader = Files.newBufferedReader(path);
    County gilpin = fromString("Gilpin");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, gilpin, blank, true);
    assertTrue(parser.parse().success);

    // There should be one contest, the one we just read in.
    List<Contest> contests = forCounties(Set.of(gilpin));
    assertEquals(1, contests.size());
    Contest contest = contests.get(0);

    // Check basic data
    assertEquals(contest.name(), "TinyInvalidExample1");
    assertEquals(contest.description(), ContestType.IRV.toString());
    assertEquals(contest.choices().stream().map(Choice::name).collect(Collectors.toList()),
        List.of("Alice", "Bob", "Chuan"));

    // Votes allowed should be 3 (because there are 3 ranks), whereas winners=1 always for IRV.
    assertEquals(contest.votesAllowed().intValue(), 3);
    assertEquals(contest.winnersAllowed().intValue(), 1);

    // There are 10 votes, with respective valid interpretations as below:
    List<CVRContestInfo> cvrs = getMatching(gilpin.id(), CastVoteRecord.RecordType.UPLOADED)
        .map(cvr -> cvr.contestInfoForContest(contest)).collect(Collectors.toList());
    assertEquals(10, cvrs.size());

    // Raw: "Alice(1),Alice(2),Bob(2),Chuan(3)
    assertEquals(List.of("Alice", "Bob", "Chuan"), cvrs.get(0).choices());
    // Raw: "Alice(1),Bob(2),Alice(3),Chuan(3)
    assertEquals(List.of("Alice", "Bob", "Chuan"), cvrs.get(1).choices());
    // Raw: "Alice(1),Alice(2),Bob(2),Alice(3),Chuan(3)
    assertEquals(List.of("Alice", "Bob", "Chuan"), cvrs.get(2).choices());
    // Raw: "Alice(1),Alice(2),Bob(2),Bob(3),Chuan(3)
    assertEquals(List.of("Alice", "Bob", "Chuan"), cvrs.get(3).choices());
    // Raw: "Alice(1),Bob(2),Chuan(2),Bob(3)
    assertEquals(List.of("Alice"), cvrs.get(4).choices());
    // Raw:  "Alice(1),Alice(2),Bob(2),Chuan(2),Bob(3)
    assertEquals(List.of("Alice"), cvrs.get(5).choices());
    // Raw:  "Alice(1),Chuan(3)"
    assertEquals(List.of("Alice"), cvrs.get(6).choices());
    // Raw:  "Alice(1),Alice(2), Bob(3)"
    assertEquals(List.of("Alice"), cvrs.get(7).choices());
    // Raw: "Alice(1),Chuan(1),Alice(2),Bob(3)
    assertTrue(cvrs.get(8).choices().isEmpty());
    // Raw: "Bob(1),Chuan(1),Alice(2),Bob(3)
    assertTrue(cvrs.get(9).choices().isEmpty());
  }

  /**
   * Test of successful parsing of data from Boulder '23, which contains a mix of IRV and plurality
   * contests. Check all their metadata.
   * Check correct parsing of the first vote.
   */
  @Test
  public void parseBoulder23Succeeds() throws IOException {
    testUtils.log(LOGGER, "parseBoulder23Succeeds");
    Path path = Paths.get(BOULDER_CSV_PATH + "Boulder-2023-Coordinated-CVR-Redactions-removed.csv");
    Reader reader = Files.newBufferedReader(path);
    County boulder = fromString("Boulder");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, boulder, blank, true);
    assertTrue(parser.parse().success);

    // There should be 38 contests. Check their metadata.
    List<Contest> contests = forCounties(Set.of(boulder));
    assertEquals(38, contests.size());
    Contest boulderMayoral = contests.get(0);
    assertEquals(boulderMayoral.name(), "City of Boulder Mayoral Candidates");
    Contest boulderCouncil = contests.get(1);
    assertEquals(boulderCouncil.name(), "City of Boulder Council Candidates");
    Contest lafayetteCouncil = contests.get(2);
    assertEquals(lafayetteCouncil.name(), "City of Lafayette City Council Candidates");
    Contest longmontMayor = contests.get(3);
    assertEquals(longmontMayor.name(),"City of Longmont - Mayor");
    Contest longmontCouncillorAtLarge = contests.get(4);
    assertEquals(longmontCouncillorAtLarge.name(),
        "City of Longmont - City Council Member At-Large");
    Contest longmontCouncillorWard1 = contests.get(5);
    assertEquals(longmontCouncillorWard1.name(),"City of Longmont - Council Member Ward 1");
    Contest longmontCouncillorWard3 = contests.get(6);
    assertEquals(longmontCouncillorWard3.name(),"City of Longmont - Council Member Ward 3");
    Contest louisvilleMayor = contests.get(7);
    assertEquals(louisvilleMayor.name(),"City of Louisville Mayor At-Large (4 Year Term)");
    Contest louisvilleCouncilWard1 = contests.get(8);
    assertEquals(louisvilleCouncilWard1.name(),
        "City of Louisville City Council Ward 1 (4-year term)");
    Contest louisvilleCouncilWard2 = contests.get(9);
    assertEquals(louisvilleCouncilWard2.name(),
        "City of Louisville City Council Ward 2 (4-year term)");
    Contest louisvilleCouncilWard3 = contests.get(10);
    assertEquals( louisvilleCouncilWard3.name(),
        "City of Louisville City Council Ward 3");
    Contest boulderValleySchoolDirectorA = contests.get(11);
    assertEquals( boulderValleySchoolDirectorA.name(),
        "Boulder Valley School District RE-2 Director District A (4 Years)");
    Contest boulderValleySchoolDirectorC = contests.get(12);
    assertEquals(boulderValleySchoolDirectorC.name(),
        "Boulder Valley School District RE-2 Director District C (4 Years)");
    Contest boulderValleySchoolDirectorD = contests.get(13);
    assertEquals(boulderValleySchoolDirectorD.name(),
        "Boulder Valley School District RE-2 Director District D (4 Years)");
    Contest boulderValleySchoolDirectorG = contests.get(14);
    assertEquals(boulderValleySchoolDirectorG.name(),
        "Boulder Valley School District RE-2 Director District G (4 Years)");
    Contest estesParkSchoolDirectorAtLarge = contests.get(15);
    assertEquals(estesParkSchoolDirectorAtLarge.name(),
        "Estes Park School District R-3 School Board Director At Large (4 Year)");
    Contest thompsonSchoolDirectorA = contests.get(16);
    assertEquals(thompsonSchoolDirectorA.name(),
        "Thompson R2-J School District Board of Education Director District A (4 Year Term)");
    Contest thompsonSchoolDirectorC = contests.get(17);
    assertEquals(thompsonSchoolDirectorC.name(),
        "Thompson R2-J School District Board of Education Director District C (4 Year Term)");
    Contest thompsonSchoolDirectorD = contests.get(18);
    assertEquals( thompsonSchoolDirectorD.name(),
        "Thompson R2-J School District Board of Education Director District D (4 Year Term)");
    Contest thompsonSchoolDirectorG = contests.get(19);
    assertEquals(thompsonSchoolDirectorG.name(),
        "Thompson R2-J School District Board of Education Director District G (4 Year Term)");
    Contest cityOfLongmontJudge = contests.get(20);
    assertEquals(cityOfLongmontJudge.name(), "City of Longmont Municipal Court Judge - Frick");
    Contest propHH = contests.get(21);
    assertEquals(propHH.name(),"Proposition HH (Statutory)");
    Contest propII = contests.get(22);
    assertEquals(propII.name(),"Proposition II (Statutory)");
    Contest boulder1A = contests.get(23);
    assertEquals(boulder1A.name(),"Boulder County Ballot Issue 1A");
    Contest boulder1B = contests.get(24);
    assertEquals(boulder1B.name(),"Boulder County Ballot Issue 1B");
    Contest boulder2A = contests.get(25);
    assertEquals(boulder2A.name(), "City of Boulder Ballot Issue 2A");
    Contest boulder2B = contests.get(26);
    assertEquals(boulder2B.name(), "City of Boulder Ballot Question 2B");
    Contest boulder302 = contests.get(27);
    assertEquals(boulder302.name(), "City of Boulder Ballot Question 302");
    Contest erie3A = contests.get(28);
    assertEquals(erie3A.name(), "Town of Erie Ballot Question 3A");
    Contest erie3B = contests.get(29);
    assertEquals(erie3B.name(), "Town of Erie Ballot Question 3B");
    Contest longmont3C = contests.get(30);
    assertEquals(longmont3C.name(), "City of Longmont Ballot Issue 3C");
    Contest longmont3D = contests.get(31);
    assertEquals(longmont3D.name(), "City of Longmont Ballot Issue 3D");
    Contest longmont3E = contests.get(32);
    assertEquals(longmont3E.name(), "City of Longmont Ballot Issue 3E");
    Contest louisville2C = contests.get(33);
    assertEquals(louisville2C.name(), "City of Louisville Ballot Issue 2C");
    Contest superior301 = contests.get(34);
    assertEquals(superior301.name(), "Town of Superior Ballot Question 301");
    Contest superiorHomeRule = contests.get(35);
    assertEquals(superiorHomeRule.name(), "Town of Superior - Home Rule Charter Commission");
    Contest nederlandEcoPass6A = contests.get(36);
    assertEquals(nederlandEcoPass6A.name(),
        "Nederland Eco Pass Public Improvement District Ballot Issue 6A");
    Contest northMetroFire7A = contests.get(37);
    assertEquals(northMetroFire7A.name(), "North Metro Fire Rescue District Ballot Issue 7A");

  }

  /**
   * A plurality cvr with a "Vote for=2.0". This is an error.
   * Note different behaviour from earlier parsing, which simply assumed 1.
   * @throws RuntimeException always,
   */
  @Test(expectedExceptions = RuntimeException.class,
      expectedExceptionsMessageRegExp = badNumsRegexp)
  public void parseBadVoteForPluralityError() throws IOException {
    testUtils.log(LOGGER, "parseBadVoteForPluralityError");
    Path path = Paths.get(BAD_CSV_PATH + "badVoteForPlurality.csv");
    Reader reader = Files.newBufferedReader(path);
    County sedgwick = fromString("Sedgwick");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, sedgwick, blank, true);
    assertTrue(parser.parse().success);
  }

  /**
   * Test for correct interpretation of a variety of different write-in strings, including
   * "WRITEIN", "WRITE-in" etc.
   * The regexp should match any whitespace, followed by any capitalization of "write", followed
   * by -, _, space or no space, followed by any capitalization of "in", followed by any whitespace.
   */
  @Test
  public void parseWriteIns() throws IOException {
    testUtils.log(LOGGER, "parseWriteIns");
    Path path = Paths.get(WRITEIN_CSV_PATH + "WriteIns.csv");
    Reader reader = Files.newBufferedReader(path);
    County lasAnimas = fromString("Las Animas");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, lasAnimas, blank, true);
    assertTrue(parser.parse().success);

    // There should be one contest, the one we just read in.
    List<Contest> contests = forCounties(Set.of(lasAnimas));
    assertEquals(1, contests.size());
    Contest contest = contests.get(0);

    // Check basic data
    assertEquals(contest.name(), "Test Write-ins");
    assertEquals(contest.description(), ContestType.PLURALITY.toString());
    assertEquals(contest.choices().size(), 2);
    assertFalse(contest.choices().get(0).qualifiedWriteIn());
    assertTrue(contest.choices().get(1).qualifiedWriteIn());
  }
}
