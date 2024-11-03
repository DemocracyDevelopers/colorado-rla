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

import au.org.democracydevelopers.corla.endpoint.EstimateSampleSizes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * A demonstration workflow that tests sample size estimation with and without manifests, comparing
 * the implications and ensuring that manifests are either properly ignored or properly incorporated
 * throughout both sample size estimation and subsequent auditing. For example, when the manifest
 * exactly matches the CVR count, the samples should be the same; extras in the manifest should
 * increase the sample size, and a smaller manifest than CVR list should throw an error.
 * All the tests here a the same plain plurality contest - we just vary the absence of presence of
 * the manifest, and (if present) the number of ballots it states.
 * This assumes that main is running.
 */
@Test(enabled=true)
public class EstimateSampleSizesVaryingManifests extends Workflow {

  /**
   * Path for all the data files.
   */
  private static final String dataPath = "src/test/resources/CSVs/";

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(EstimateSampleSizesVaryingManifests.class);

  /**
   * This "test" uploads CVRs and ballot manifests.
   */
  @Test(enabled = false)
  public void runManifestVaryingDemo(){

    List<String> CVRS = new ArrayList<>();
    CVRS.add(dataPath + "PluralityOnly/Plurality1And2.csv");

    List<String> MANIFESTS = new ArrayList<>();
    MANIFESTS.add(dataPath + "PluralityOnly/ThreeCandidatesTenVotes_Manifest.csv");

    for(int i = 1; i < 2; ++i){
      uploadCounty(i, "cvr-export", CVRS.get(i-1), CVRS.get(i-1) + ".sha256sum");
      uploadCounty(i, "ballot-manifest", MANIFESTS.get(i-1), MANIFESTS.get(i-1) + ".sha256sum");
    }

    // Now do the sample size estimate
    List<EstimateSampleSizes.EstimateData> estimateData = getSampleSizeEstimates();


}
