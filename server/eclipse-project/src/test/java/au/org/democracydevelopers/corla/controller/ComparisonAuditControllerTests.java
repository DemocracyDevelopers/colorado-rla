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

package au.org.democracydevelopers.corla.controller;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.IRVComparisonAudit;
import au.org.democracydevelopers.corla.model.vote.IRVParsingException;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import au.org.democracydevelopers.corla.util.testUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static au.org.democracydevelopers.corla.util.testUtils.*;
import static org.testng.Assert.*;
import static us.freeandfair.corla.query.CastVoteRecordQueries.getMatching;
import static us.freeandfair.corla.query.ContestQueries.forCounties;
import static us.freeandfair.corla.query.CountyQueries.fromString;

/**
 * Test those aspects of the ComparisonAuditController that were altered for IRV:
 * - an all-IRV contest is made into an IRVComparisonAudit,
 * - an all-plurality contest is made into a (plain, plurality) ComparisonAudit,
 * - a mixed-type contest, or a contest that is neither plurality nor IRV, throws an error.
 */
public class ComparisonAuditControllerTests extends TestClassWithDatabase {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(ComparisonAuditControllerTests.class);

  /**
   * Container for the mock-up database.
   */
  static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * Blank properties for submitting to the DominionCVRExportParser instance.
   */
  private static final Properties blank = new Properties();

  /**
   * IRV contest name.
   */
  private static final String tinyIRV = "TinyExample1";

  /**
   * Plurality contest name.
   */
  private static final String tinyPlurality = "PluralityExample1";

  /**
   * Mixed IRV/plurality contest name - should produce errors.
   */
  private static final String tinyMixed = "PluralityExample2";

  @BeforeClass
  public static void beforeAll() {
    postgres.start();
    Persistence.setProperties(createHibernateProperties(postgres));

    var containerDelegate = new JdbcDatabaseDelegate(postgres, "");
    ScriptUtils.runInitScript(containerDelegate,
            "SQL/co-counties.sql");
    ScriptUtils.runInitScript(containerDelegate,
            "SQL/corla-three-candidates-ten-votes-inconsistent-types.sql");

  }

  @AfterClass
  public static void afterAll() {
    postgres.stop();
  }

  /**
   * The consistent IRV contest, tinyIRVExample1, should make an IRVComparisonAudit.
   */
  @Test
  public void IRVContestMakesIRVAudit() {
    testUtils.log(LOGGER, "IRVContestMakesIRVAudit");

    // Set up the contest results from the stored data.
    List<ContestResult> results = ContestCounter.countAllContests();

    // Find all the ContestResults for TinyIRV - there should be one.
    List<ContestResult> tinyIRVResults = results.stream().filter(
            cr -> cr.getContests().stream().anyMatch(co -> co.name().equals(tinyIRV))).toList();
    assertEquals(1, tinyIRVResults.size());
    ContestResult tinyIRV = tinyIRVResults.get(0);

    // Check that it makes an IRVComparisonAudit.
    ComparisonAudit irvAudit = ComparisonAuditController.createAuditOfCorrectType(tinyIRV, BigDecimal.valueOf(0.03));
    assertTrue(irvAudit instanceof IRVComparisonAudit);
  }

  /**
   * The consistent plurality contest, PluralityExample1, should make an IRVComparisonAudit.
   */
  @Test
  public void pluralityContestMakesPluralityAudit() {
    testUtils.log(LOGGER, "pluralityContestMakesPluralityAudit");

    // Set up the contest results from the stored data.
    List<ContestResult> results = ContestCounter.countAllContests();

    // Find all the ContestResults for TinyPlurality - there should be one.
    List<ContestResult> tinyPluralityResults = results.stream().filter(
            cr -> cr.getContests().stream().anyMatch(co -> co.name().equals(tinyPlurality))).toList();
    assertEquals(1, tinyPluralityResults.size());
    ContestResult tinyPlurality = tinyPluralityResults.get(0);

    // Should _not_ be an IRVComparisonAudit.
    ComparisonAudit audit = ComparisonAuditController.createAuditOfCorrectType(tinyPlurality, BigDecimal.valueOf(0.03));
    assertFalse(audit instanceof IRVComparisonAudit);
  }

  /**
   * A contest that mixes plurality and IRV throws an exception when we try to create a ComparisonAudit.
   */
  @Test(expectedExceptions = RuntimeException.class)
  public void inconsistentContestThrowsException() {
    testUtils.log(LOGGER, "inconsistentContestThrowsException");

    // Set up the contest results from the stored data.
    List<ContestResult> results = ContestCounter.countAllContests();

    // Find all the contestResults for TinyMixed - there should be one.
    List<ContestResult> tinyMixedResults = results.stream().filter(
            cr -> cr.getContests().stream().anyMatch(co -> co.name().equals(tinyMixed))).toList();
    assertEquals(1, tinyMixedResults.size());

    ContestResult tinyPlurality = tinyMixedResults.get(0);
    ComparisonAudit audit = ComparisonAuditController.createAuditOfCorrectType(tinyPlurality, BigDecimal.valueOf(0.03));

  }
}
