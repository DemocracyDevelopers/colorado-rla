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
import java.util.Collections;
import java.util.List;

/**
 * Object for storing all the data describing a test workflow, mapping to a workflow instance
 * JSON.
 */
public class Instance {

  /**
   * Name of the workflow instance.
   */
  private String NAME;

  /**
   * Risk limit for the test audit.
   */
  private BigDecimal RISKLIMIT;

  /**
   * List of additional SQL files (as path strings) containing extra data with which to
   * initialise the database. County data is automatically added to the database when
   * running workflows.
   */
  private List<String> SQL;

  /**
   * List of CVR csv files (path strings), one for each county.
   */
  private List<String> CVRS;

  /**
   * List of ballot manifest files (path strings), one for each county.
   */
  private List<String> MANIFESTS;

  /**
   * Path (as a string) to the canonicalisation file.
   */
  private String CANONICALLIST;

  /**
   * @return SQL files (as path strings) representing additional data to load
   * into the database for the given workflow instance. Returned as an
   * unmodifiable list.
   */
  public List<String> getSQLs(){
    return Collections.unmodifiableList(SQL);
  }

  /**
   * @return CVR files (as path strings) for each county, in order. Returned as an
   * unmodifiable list.
   */
  public List<String> getCVRs(){
    return Collections.unmodifiableList(CVRS);
  }

  /**
   * @return Manifest files (as path strings) for each county, in order. Returned as an
   * unmodifiable list.
   */
  public List<String> getManifests(){
    return Collections.unmodifiableList(MANIFESTS);
  }

  /**
   * @return Risk limit for this test workflow.
   */
  public BigDecimal getRiskLimit(){
    return RISKLIMIT;
  }

  /**
   * @return Path (as string) to the canonicalisation file for this test workflow.
   */
  public String getCanonicalisationFile(){
    return CANONICALLIST;
  }
}
