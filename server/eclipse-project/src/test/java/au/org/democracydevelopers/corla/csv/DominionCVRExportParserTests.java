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
import org.apache.commons.lang3.StringUtils;
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
   * Test county.
   */
  private static County boulder;

  /**
   * Location of the test data.
   */
  public static final String CSV_FILE_PATH = "src/test/resources/CSVs/";

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

    boulder = fromString("Boulder");
  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  @Test
  public void parseSimpleIRVSucceeds() throws IOException {
    Path path = Paths.get(CSV_FILE_PATH + "ThreeCandidatesTenVotes.csv");
    Reader reader = Files.newBufferedReader(path);

    DominionCVRExportParser parser = new DominionCVRExportParser(reader, boulder, new Properties(), true);
    assertTrue(parser.parse().success);

    List<Contest> contests = forCounties(Set.of(boulder));
    assertFalse(contests.isEmpty());
    Contest contest = contests.get(0);

    // Check basic data
    assertEquals(contest.name(), "TinyExample1");
    assertEquals(ContestType.IRV.toString(), contest.description());
    assertEquals(List.of("Alice","Bob","Chuan"), contest.choices().stream().map(Choice::name).collect(Collectors.toList()));

    // Votes allowed should be 3 (because there are 3 ranks), whereas winners=1 always for IRV.
    assertEquals(contest.votesAllowed().intValue(), 3);
    assertEquals(contest.winnersAllowed().intValue(), 1);

    // There are 10 votes:
    // 3 Alice, Bob, Chuan
    // 3 Alice, Chuan, Bob
    // 1 Bob, Alice, Chuan
    // 3 Bob, Chuan, Alice
    List<CastVoteRecord> cvrs = getMatching(boulder.id(),  CastVoteRecord.RecordType.UPLOADED).collect(Collectors.toList());
    List<CVRContestInfo> v1 = cvrs.stream().map(cvr -> cvr.contestInfoForContest(contest)).collect(Collectors.toList());
    assertEquals(cvrs.size(), 10);



}


  @Test(expectedExceptions = IRVParsingException.class,
        expectedExceptionsMessageRegExp = "Couldn't parse.*")
  public void parseBadThrowsException() throws IRVParsingException {

    new IRVPreference("bad");
  }
}
