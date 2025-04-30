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

package au.org.democracydevelopers.corla.workflows;

import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;
import static java.lang.Math.max;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.WAITING_FOR_ROUND_SIGN_OFF;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.COUNTY_AUDIT_COMPLETE;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.COUNTY_AUDIT_UNDERWAY;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.COMPLETE_AUDIT_INFO_SET;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.PARTIAL_AUDIT_INFO_SET;

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

/**
 * This workflow runner is designed to execute all JSON workflows present in a specified
 * directory (defined in the "pathToInstances" member). These JSON workflows define a complete
 * audit to undertake, along with expected results. The workflow runner will execute all stages of
 * the audit: CVR and manifest uploads; defining the audit; selecting contests to target;
 * uploading audited ballots; reauditing ballots; and executing rounds until there are no further
 * ballots to sample. The workflow ends when the audit ends. Reporting is not tested in these workflows.
 */
@Test(enabled = true)
public class WorkflowRunner extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(WorkflowRunner.class);

  /**
   * Directory in which instance JSON files are stored.
   */
  private static final String pathToInstances = "src/test/resources/workflows/instances";

  @BeforeClass
  public void setup() {
    config = loadProperties();
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;
  }

  /**
   * Returns a list of parameter lists to supply to the runWorkflow test method in this class.
   * Each parameter list contains one element -- a path to the workflow JSON instance to run.
   *
   * @return A list of test parameter lists as a 2D array of objects.
   */
  @DataProvider(name = "workflow-provider")
  public Object[][] supplyWorkflowPaths() {
    final String prefix = "[supplyWorkflowPaths]";
    List<Path> pathList;

    try (Stream<Path> stream = Files.walk(Paths.get(pathToInstances))) {
      pathList = stream.map(Path::normalize)
          .filter(Files::isRegularFile)
          .filter(p -> isJSON(p.toString()))
          .toList();
    } catch (IOException e) {
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    // Convert list of paths into an array of parameter arrays
    Object[][] params = new Object[pathList.size()][1];
    for (int i = 0; i < pathList.size(); ++i) {
      params[i][0] = pathList.get(i);
    }
    return params;
  }

  /**
   * Returns a singleton list containing the .json file given as the argument.
   * Useful for running a single workflow during testing of the testing.
   */
  @DataProvider(name = "single-workflow-provider")
  public Object[][] supplySingleWorkflowPath() {

    String filename = "StateAndCountyUnbalanced.json";
    Path path = Paths.get(pathToInstances, filename);
    Path normalizedPath = path.normalize();
    assertTrue(Files.isRegularFile(normalizedPath));
    assertTrue(isJSON(normalizedPath.toString()));
    Object[][] params = new Object[1][1];
    params[0][0] = normalizedPath;
    return params;
  }

  /**
   * Given a JSON file defining an audit workflow (CVRs, Manifests, which CVRs to replace with
   * phantoms, which ballots to treat as phantoms, expected diluted margins and sample sizes,
   * which ballots to simulate discrepancies for, and expected end of round states ...), run
   * the test audit and verify that the expected outcomes arise.
   *
   * @param pathToInstance Path to the JSON workflow instance defining the test.
   * @throws InterruptedException
   */
  // @Test(dataProvider = "workflow-provider")
  @Test(dataProvider = "single-workflow-provider")
  public void runInstance(final Path pathToInstance) throws InterruptedException {
    final String prefix = "[runInstance] " + pathToInstance;

    try {
      // PostgreSQLContainer<?> postgres = setupDatabaseAndRunMain(pathToInstance.getFileName().toString());
      final PostgreSQLContainer<?> postgres = TestClassWithDatabase.createTestContainer();
      runMainAndInitializeDB(pathToInstance.getFileName().toString(), Optional.of(postgres));

      // Do the workflow.
      doWorkflow(pathToInstance, Optional.of(postgres));

      postgres.stop();

    } catch(IOException e){
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }


  /**
   * Load additional SQL data (this is data that we want to add after we have
   * CVRs, manifests, etc loaded for each county). This will mostly be used to load
   * assertion data into the database, simulating a call to the raire-service.
   */
  protected void makeAssertionData(final Optional<PostgreSQLContainer<?>> postgresOpt, final List<String> SQLfiles) {
    assertTrue(postgresOpt.isPresent());
    for (final String s : SQLfiles) {
      TestClassWithDatabase.runSQLSetupScript(postgresOpt.get(), s);
    }
  }

  /**
   * Set up main's configuration file to match the given postgres container, then run main and
   * load the colorado-rla init script into the database.
   * This loads in the properties in resources/test.properties, then overwrites the database
   * location with the one in the newly-created test container.
   * @param testName Name of test instance.
   * @param postgresOpt The PostgreSQL container to use.
   */
  @Override
  protected void runMainAndInitializeDB(String testName, Optional<PostgreSQLContainer<?>> postgresOpt) {
    assertTrue(postgresOpt.isPresent());
    final PostgreSQLContainer<?> postgres = postgresOpt.get();

    postgres.start();
    config.setProperty("hibernate.url", postgres.getJdbcUrl());
    Persistence.setProperties(config);
    TestClassWithDatabase.runSQLSetupScript(postgres, "SQL/co-counties.sql");

    runMain(config, testName);

    Persistence.beginTransaction();
  }

  /**
   * Utility that returns true if the given file path represents a JSON file.
   * @param filePath Path to the file (as a string).
   * @return true if the given file is a JSON file.
   */
  private static boolean isJSON(final String filePath) {
    return filePath.toLowerCase().endsWith(".json");
  }
}
