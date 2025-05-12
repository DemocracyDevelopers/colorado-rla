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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import static org.testng.Assert.*;

/**
 * This workflow runner is designed to run in a UAT environment in which the raire service, colorado-rla
 * server and corla database are all set up and running.
 * 1. You will need a copy of the colorado-rla code, which you can either clone with
 * `git clone https://github.com/DemocracyDevelopers/colorado-rla.git`
 * `git clone git@github.com:DemocracyDevelopers/colorado-rla.git`
 * or download as a zip from
 * https://github.com/DemocracyDevelopers/colorado-rla/archive/refs/heads/main.zip
 * 2. Use src/test/resources/test.properties to specify the raire url, database login credentials and
 * url/port of the main colorado-rla server.
 * 3. Ensure that maven and java are installed.
 * 4. Ensure that the database contains correct setup data such as administrator credentials,
 * which can be loaded from colorado-rla/test/corla-test.credentials.psql, and the list of Colorado
 * counties. Note that once these are in the database, you may execute the command in Step 6
 * repeatedly without resetting the database. At the start of running a workflow (with RAIRE), the
 * database is reset.
 * 5. Ensure that the colorado-rla server and raire-service are running.
 * 6. From the eclipse-project directory, to run a workflow json file via the command line, enter
 * `mvn test -Dtest="*WorkflowRunnerWithRaire" -DworkflowFile="[Path to workflow file]"`
 * For example, to run the AllPluralityTwoVoteOverstatementTwoRounds workflow, enter
 * `mvn test -Dtest="*WorkflowRunnerWithRaire" -DworkflowFile="src/test/resources/workflows/instances/AllPluralityTwoVoteOverstatementTwoRounds.json"`
 * This test is skipped when the tests are run with empty parameters, i.e. during normal testing.
 */
@Test(enabled=true)
public class WorkflowRunnerWithRaire extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(WorkflowRunnerWithRaire.class);


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
   * @param workflowFile Path to JSON workflow instance to execute.
   * @throws InterruptedException
   */
  @Parameters("workflowFile")
  @Test
  public void runInstance(final String workflowFile) throws InterruptedException {
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

      // Do the workflow. Reset the database first.
      resetDatabase("stateadmin1");

      doWorkflow(pathToInstance, Optional.empty());

    } catch(IOException e){
      final String msg = prefix + " " + e.getMessage() + ". Check that the path and filename are correct.";
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Run everything with the database and raire url set up in src/test/resources/test.properties
   * This assumes the database is in an initial state, with administrator logins and counties but
   * without other data.
   * TODO possibly get testName to be the full (relative) path of the config file. Then this can
   * be used for src/main/resources/us/freeandfair/corla/default.properties
   * @param testName not used.
   * @param postgres not used.
   */
  @Override
  protected void runMainAndInitializeDB(final String testName, final Optional<PostgreSQLContainer<?>> postgres) {
    assertTrue(postgres.isEmpty());
    testUtils.log(LOGGER, "[runMainAndInitializeDB] running workflow " + testName + ".");
    // Don't need to start main because we assume it's already running.
    // main("src/test/resources/test.properties");
    Persistence.setProperties(config);
    Persistence.beginTransaction();
  }
}
