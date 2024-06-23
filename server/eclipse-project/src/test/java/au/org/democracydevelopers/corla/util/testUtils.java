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

package au.org.democracydevelopers.corla.util;
import org.apache.log4j.Logger;

/**
 * This class contains utilities for use in testing.
 */
public class testUtils {

  /**
   * Comparator for doubles within a specific tolerance.
   */
  public static final DoubleComparator doubleComparator = new DoubleComparator();

  /*
   * Location of the tiny examples intended to be human-tallyable.
   */
  public static final String TINY_CSV_PATH = "src/test/resources/CSVs/Tiny-IRV-Examples/";

  /**
   * Location of the (redacted) data from Boulder '23.
   */
  public static final String BOULDER_CSV_PATH = "src/test/resources/CSVs/Boulder23/";

  /**
   * Location of the data derived from New South Wales '21 elections.
   */
  public static final String NSW_CSV_PATH = "src/test/resources/CSVs/NewSouthWales21/";

  /**
   * Location of examples that are expected to fail to parse.
   */
  public static final String BAD_CSV_PATH = "src/test/resources/CSVs/badExamples/";

  /**
   * Location of examples that test expanded definition of Write-In string.
   */
  public static final String WRITEIN_CSV_PATH = "src/test/resources/CSVs/WriteIns/";

  /**
   * Print log statement indicating that a specific test has started running.
   */
  public static void log(Logger logger, String test){
    logger.debug(String.format("RUNNING TEST: %s.",test));
  }
}
