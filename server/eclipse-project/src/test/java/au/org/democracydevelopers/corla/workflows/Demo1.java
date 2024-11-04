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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.persistence.Persistence;

import static org.testng.Assert.assertEquals;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.DOS_INITIAL_STATE;

/**
 * A demonstration workflow that uploads CVRs and ballot manifests for all 64 counties.
 * This assumes that main is running.
 */
@Test(enabled=true)
public class Demo1 extends Workflow {

  /**
   * Path for all the data files.
   */
  private static final String dataPath = "src/test/resources/CSVs/";

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Demo1.class);

  /**
   * Container for the mock-up database.
   */
  private final static PostgreSQLContainer<?> postgres = createTestContainer();

  /**
   * Database init.
   */
  @BeforeClass
  public static void beforeAll() {

    final var containerDelegate = setupContainerStartPostgres(postgres);

    var s = Persistence.openSession();
    s.beginTransaction();

    // Used to initialize the database, e.g. to set the ASM state to the DOS_INITIAL_STATE
    // and to insert counties and administrator test logins.
    ScriptUtils.runInitScript(containerDelegate, "SQL/co-counties.sql");

    runMain("Demo1");
  }

  /**
   * This "test" uploads CVRs and ballot manifests for all 64 counties.
   * The uploads match the following http files in the workflows directory:
   * - demo1_loadCVRs, demo1_loadManifests,
   * - Boulder_loadCVRs, Boulder_loadManifest.
   */
  @Test(enabled = true)
  public void runDemo1(){


    List<String> CVRS = new ArrayList<>();

    CVRS.add(dataPath + "Demo1/1-adams-cvrexport-plusByron-1.csv");
    CVRS.add(dataPath + "Demo1/2-alamosa-cvrexport-plusByron-2.csv");
    CVRS.add(dataPath + "Demo1/3-arapahoe-Byron-3-plus-tied-irv.csv");
    CVRS.add(dataPath + "Demo1/4-archuleta-kempsey-plusByron-4.csv");
    CVRS.add(dataPath + "split-Byron/Byron-5.csv");
    CVRS.add(dataPath + "split-Byron/Byron-5.csv");
    CVRS.add(dataPath + "split-Byron/Byron-6.csv");
    CVRS.add(dataPath + "Demo1/7-boulder-2023-plusByron-7.csv");

    List<String> MANIFESTS = new ArrayList<>();

    MANIFESTS.add(dataPath + "Demo1/1-adams-plusByron-1-manifest.csv");
    MANIFESTS.add(dataPath + "Demo1/2-alamosa-plusByron-2-manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-3-manifest.csv");
    MANIFESTS.add(dataPath + "NewSouthWales21/Kempsey_Mayoral.manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-5-manifest.csv");
    MANIFESTS.add(dataPath + "split-Byron/Byron-6-manifest.csv");
    MANIFESTS.add(dataPath + "Boulder23/Boulder-IRV-Manifest.csv");

    for(int i = 8; i < 65; ++i){
      CVRS.add(dataPath + "split-Byron/Byron-" + i + ".csv");
      MANIFESTS.add(dataPath + "split-Byron/Byron-" + i + "-manifest.csv");
    }

    for(int i = 1; i < 65; ++i){
      uploadCounty(i, "cvr-export", CVRS.get(i-1), CVRS.get(i-1) + ".sha256sum");
      uploadCounty(i, "ballot-manifest", MANIFESTS.get(i-1), MANIFESTS.get(i-1) + ".sha256sum");
    }

    // Get the DoSDashboard refresh response; sanity check.
    JsonPath response = getDoSDashBoardRefreshResponse();
    assertEquals(response.get("asm_state"), DOS_INITIAL_STATE.toString());

    // Set the audit info, including the canonical list and the risk limit.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.03);
    updateAuditInfo(dataPath + "Demo1/" + "demo1-canonical-list.csv", riskLimit);

    JsonPath response2 = getDoSDashBoardRefreshResponse();
    assertEquals(0, riskLimit
        .compareTo(new BigDecimal(response2.getMap("audit_info").get("risk_limit").toString())));
  }

}
