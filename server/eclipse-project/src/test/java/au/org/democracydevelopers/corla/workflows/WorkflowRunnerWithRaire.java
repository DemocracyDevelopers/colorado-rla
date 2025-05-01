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

import au.org.democracydevelopers.corla.util.testUtils;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import us.freeandfair.corla.persistence.Persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static au.org.democracydevelopers.corla.util.PropertiesLoader.loadProperties;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * This workflow runner is designed to run in a UAT environment in which the raire service, colorado-rla
 * server and corla database are all set up and running.
 * Use src/test/resources/test.properties to specify the raire url, database login credentials and
 * url/port of the main colorado-rla server.
 * Ensure that maven and java are installed, and that the database is in the initial state - empty
 * except for setup data such as administrator credentials (which can be loaded from corla-test.credentials.psql,
 * available from the github repository).
 * From the eclipse-project directory, to run a workflow json file via the command line, enter
 * `mvn -Dtest="*WorkflowRunnerWithRaire" -DworkflowFile="[Path to workflow file]" test`
 * For example, to run the AllPluralityTwoVoteOverstatementTwoRounds workflow, enter
 * `mvn -Dtest="*WorkflowRunnerWithRaire" -DworkflowFile="src/test/resources/workflows/instances/AllPluralityTwoVoteOverstatementTwoRounds.json" test`
 * This test is skipped when the tests are run with empty parameters, i.e. during normal testing.
 */
@Test(enabled=true)
public class WorkflowRunnerWithRaire extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(WorkflowRunnerWithRaire.class);

  /**
   * Default time limit for raire call.
   */
  private static final int TIME_LIMIT_DEFAULT = 5;

  @BeforeClass
  public void setup() {
    config = loadProperties();
    RestAssured.baseURI = config.getProperty("corla_url");
    RestAssured.port = Integer.parseInt(config.getProperty("corla_http_port"));
  }

  /**
   * Given a JSON file defining an audit workflow (CVRs, Manifests, which CVRs to replace with
   * phantoms, which ballots to treat as phantoms, expected diluted margins and sample sizes,
   * which ballots to simulate discrepancies for, and expected end of round states ...), run
   * the test audit and verify that the expected outcomes arise.
   * @throws InterruptedException
   */
  @Parameters("workflowFile")
  @Test
  public void runInstance(String workflowFile) throws InterruptedException {
    final String prefix = "[runInstance] ";

    if(workflowFile == null || workflowFile.isEmpty()) {
      System.out.println("Usage example, from eclipse-project directory: " +
          "mvn -Dtest=\"*WorkflowRunnerWithRaire\" " +
          "-DworkflowFile=\"src/test/resources/workflows/instances/AllPluralityTwoVoteOverstatementTwoRounds.json\" test");
      // Return without running the test. This also means that when included in automated workflows
      // without command-line arguments, this test always passes.
      return;
    }

    try {
      final Path pathToInstance = Paths.get(workflowFile);
      LOGGER.info(String.format("%s %s %s.", prefix, "running workflow", pathToInstance));
      runMainAndInitializeDB("Workflow with raire", Optional.empty());

      // Do the workflow.
      doWorkflow(pathToInstance, Optional.empty());

    } catch(IOException e){
      final String msg = prefix + " " + e.getMessage() + ". Check that the path and filename are correct.";
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Connect to the raire service and tell it to generate the assertions.
   * @param postgres The database (expected to be the same one raire uses).
   * @param SQLfiles Not used.
   */
  protected void makeAssertionData(final Optional<PostgreSQLContainer<?>> postgres, final List<String> SQLfiles) {
    // This should not be called with 'useRaire' true in this workflow.
    assertTrue(postgres.isEmpty());

    // Login as state admin.
    final SessionFilter filter = doLogin("stateadmin1");
    given()
        .filter(filter)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .get("/generate-assertions?timeLimitSeconds="+TIME_LIMIT_DEFAULT)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  /**
   * Run everything with the database and raire url set up in test.properties.
   * This loads in the properties in resources/test.properties.
   * FIXME get testName to be the full (relative) path of the config file. Then this can be used
   * for either src/test/resources/test.properties or src/main/resources/us/freeandfair/corla/default.properties
   * @param testName not used.
   * @param postgres not used.
   */
  @Override
  protected void runMainAndInitializeDB(String testName, Optional<PostgreSQLContainer<?>> postgres) {
    assertTrue(postgres.isEmpty());
    testUtils.log(LOGGER, "[runMainAndInitializeDB] running workflow " + testName + ".");
    // Don't need to start main because we assume it's already running.
    // main("src/test/resources/test.properties");
    Properties config = loadProperties();
    Persistence.setProperties(config);
    Persistence.beginTransaction();
  }

}
