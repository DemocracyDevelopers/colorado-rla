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
import au.org.democracydevelopers.corla.model.vote.IRVPreference;
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
 *   examples from IRVPreferenceTests.java
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
   * Location of the test data.
   */
  public static final String TINY_IRV_PATH = "src/test/resources/CSVs/Tiny-IRV-Examples/";

  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Properties properties = new Properties();
    properties.setProperty("hibernate.driver", "org.postgresql.Driver");
    properties.setProperty("hibernate.url", postgres.getJdbcUrl());
    properties.setProperty("hibernate.user", postgres.getUsername());
    properties.setProperty("hibernate.pass", postgres.getPassword());
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
    Persistence.setProperties(properties);
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
    Path path = Paths.get(TINY_IRV_PATH + "ThreeCandidatesTenVotes.csv");
    Reader reader = Files.newBufferedReader(path);
    County boulder = fromString("Boulder");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, boulder, new Properties(), true);
    assertTrue(parser.parse().success);

    // There should be one contest, the one we just read in.
    List<Contest> contests = forCounties(Set.of(boulder));
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
    List<CVRContestInfo> cvrs = getMatching(boulder.id(),  CastVoteRecord.RecordType.UPLOADED)
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
    Path path = Paths.get(TINY_IRV_PATH + "GuideToRAIREExample3.csv");
    Reader reader = Files.newBufferedReader(path);
    County montezuma = fromString("Montezuma");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, montezuma, new Properties(), true);
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
    Path path = Paths.get(TINY_IRV_PATH + "ThreeCandidatesTenInvalidVotes.csv");
    Reader reader = Files.newBufferedReader(path);
    County gilpin = fromString("Gilpin");

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, gilpin, new Properties(), true);
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

  @Test(expectedExceptions = IRVParsingException.class,
        expectedExceptionsMessageRegExp = "Couldn't parse.*")
  public void parseBadThrowsException() throws IRVParsingException {

    new IRVPreference("bad");
  }
}
