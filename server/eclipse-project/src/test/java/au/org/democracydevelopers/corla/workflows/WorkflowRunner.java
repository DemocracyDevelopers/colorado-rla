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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.PARTIAL_AUDIT_INFO_SET;

import au.org.democracydevelopers.corla.util.TestClassWithDatabase;
import io.restassured.path.json.JsonPath;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(enabled=true)
public class WorkflowRunner extends Workflow {

  /**
   * Class-wide logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(WorkflowRunner.class);

  /**
   * Directory in which instance JSON files are stored.
   */
  private static final String pathToInstances = "src/test/workflows/instances";

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAllThisClass() {

    // Used to initialize the database, e.g. to set the ASM state to the DOS_INITIAL_STATE
    // and to insert counties and administrator test logins.
    runSQLSetupScript("SQL/co-counties.sql");

    runMain("Workflows");
  }


  @Test(enabled=true)
  public void runWorkflows() throws InterruptedException {
    final String prefix = "[runWorkflows]";

    // Run every workflow instance present in the pathToInstances directory.
    // Instances are JSON files.
    List<Path> pathList;

    try (Stream<Path> stream = Files.walk(Paths.get(pathToInstances))) {
      pathList = stream.map(Path::normalize)
          .filter(Files::isRegularFile)
          .filter(p -> isJSON(p.toString()))
          .collect(Collectors.toList());
    }
    catch(IOException e) {
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }

    pathList.forEach(p -> runInstance(p));
  }

  private void runInstance(final Path pathToInstance) throws InterruptedException {
    final String prefix = "[runWorkflows] " + pathToInstance;

    try {
      ObjectMapper toJson = new ObjectMapper();
      final Instance instance = toJson.readValue(pathToInstance.toFile(), Instance.class);

      // Upload all the manifests and CVRs
      final List<String> cvrs = instance.getCVRs();
      final List<String> manifests = instance.getManifests();

      assertEquals(manifests.size(), numCounties);
      assertEquals(cvrs.size(), numCounties);

      for(int i = 0; i < manifests.size(); ++i){
        uploadCounty(i+1, MANIFEST_FILETYPE, manifests.get(i), manifests.get(i) + ".sha256sum");
      }

      // Upload all the CVRs. The order is important because it's an error to try to import a
      // manifest while the CVRs are being read.
      for(int i = 0; i < cvrs.size()  ; ++i){
        uploadCounty(i+1, CVR_FILETYPE, cvrs.get(i), cvrs.get(i-1) + ".sha256sum");
      }

      // Wait while the CVRs (and manifests) are uploaded.
      assertTrue(uploadSuccessfulWithin(600, allCounties, CVR_JSON));
      assertTrue(uploadSuccessfulWithin(20, allCounties, MANIFEST_JSON));

      // Get the DoSDashboard refresh response; sanity check for initial state.
      JsonPath dashboard = getDoSDashBoardRefreshResponse();
      assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());
      assertEquals(dashboard.getMap(AUDIT_INFO+"."+CANONICAL_CHOICES).toString(), "{}");
      assertNull(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON));
      assertNull(dashboard.get(AUDIT_INFO + "." + SEED));

      updateAuditInfo(instance.getCanonicalisationFile(), instance.getRiskLimit());
      dashboard = getDoSDashBoardRefreshResponse();
      assertEquals(0, instance.getRiskLimit()
          .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())));

      // There should be canonical contests for each county.
      assertEquals(numCounties,
          dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());

      // Check that the seed is still null.
      assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
      assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

      // Load additional SQL data (this is data that we want to add after we have
      // CVRs, manifests, etc loaded for each county).
      instance.getSQLs().forEach(TestClassWithDatabase::runSQLSetupScript);


    }
    catch(IOException e){
      final String msg = prefix + " " + e.getMessage();
      LOGGER.error(msg);
      throw new RuntimeException(msg);
    }
  }


  /**
   * Utility that returns true if the given file path represents a JSON file.
   * @param filePath Path to the file (as a string).
   * @return true if the given file is a JSON file.
   */
  private static boolean isJSON(final String filePath){
    return filePath.toLowerCase().endsWith(".json");
  }
}
