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

import io.restassured.filter.session.SessionFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

/**
 * A demonstration workflow that uploads CVRs and ballot manifests for all 64 counties.
 */
@Test(enabled=false)
public class Demo1 extends Workflow {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(Demo1.class);

  /**
   * This "test" uploads CVRs and ballot manifests for all 64 counties.
   */
  @Test
  public void runDemo1(){
    List<String> CVRS = new ArrayList<>();

    CVRS.add("CSVs/Demo1/1-adams-cvrexport-plusByron-1.csv");
    CVRS.add("CSVs/Demo1/2-alamosa-cvrexport-plusByron-2.csv");
    CVRS.add("CSVs/Demo1/3-arapahoe-Byron-3-plus-tied-irv.csv");
    CVRS.add("CSVs/Demo1/4-archuleta-kempsey-plusByron-4.csv");
    CVRS.add("CSVs/split-Byron/Byron-5.csv");
    CVRS.add("CSVs/split-Byron/Byron-5.csv");
    CVRS.add("CSVs/split-Byron/Byron-6.csv");
    CVRS.add("CSVs/Demo1/7-boulder-2023-plusByron-7.csv");

    // List of sessions for each county (we will want to perform a sequence
    // of requests of behalf of each county, and must do so in the same session).
    List<SessionFilter> filters = new ArrayList<>();
    for(int i = 8; i < 65; ++i){
      CVRS.add("CSVs/split-Byron/Byron-" + i + ".csv");
      filters.add(new SessionFilter());
    }

    List<String> MANIFESTS = new ArrayList<>();

    MANIFESTS.add("CSVs/Demo1/1-adams-cvrexport-plusByron-1-manifest.csv");
    MANIFESTS.add("CSVs/Demo1/2-alamosa-cvrexport-plusByron-2-manifest.csv");
    MANIFESTS.add("CSVs/Demo1/3-arapahoe-Byron-3-plus-tied-irv-manifest.csv");
    MANIFESTS.add("CSVs/Demo1/4-archuleta-kempsey-plusByron-4-manifest.csv");
    MANIFESTS.add("CSVs/split-Byron/Byron-5-manifest.csv");
    MANIFESTS.add("CSVs/split-Byron/Byron-5-manifest.csv");
    MANIFESTS.add("CSVs/split-Byron/Byron-6-manifest.csv");
    MANIFESTS.add("CSVs/Boulder23/Boulder-IRV-Manifest.csv");

    for(int i = 8; i < 65; ++i){
      CVRS.add("CSVs/split-Byron/Byron-" + i + ".csv");
      MANIFESTS.add("CSVs/split-Byron/Byron-" + i + "-manifest.csv");
    }

    for(int i = 1; i < 65; ++i){
      uploadCounty(i, "cvr", CVRS.get(i), CVRS.get(i) + ".sha256sum");
      uploadCounty(i, "manifest", MANIFESTS.get(i), MANIFESTS.get(i) + ".sha256sum");
    }
  }

}
