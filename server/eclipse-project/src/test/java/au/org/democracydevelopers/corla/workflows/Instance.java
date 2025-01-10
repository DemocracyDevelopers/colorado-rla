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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Object for storing all the data describing a test workflow, mapping to a workflow instance
 * JSON.
 */
public class Instance {

  /**
   * Name of the workflow instance.
   */
  @JsonProperty("NAME")
  private String name;

  /**
   * Risk limit for the test audit.
   */
  @JsonProperty("RISK_LIMIT")
  private BigDecimal riskLimit;

  /**
   * List of additional SQL files (as path strings) containing extra data with which to
   * initialise the database. County data is automatically added to the database when
   * running workflows.
   */
  @JsonProperty("SQLS")
  private List<String> sqls;

  /**
   * List of CVR csv files (path strings), one for each county.
   */
  @JsonProperty("CVRS")
  private List<String> cvrs;

  /**
   * List of ballot manifest files (path strings), one for each county.
   */
  @JsonProperty("MANIFESTS")
  private List<String> manifests;

  /**
   * Path (as a string) to the canonicalisation file.
   */
  @JsonProperty("CANONICAL_LIST")
  private String canonicalList;

  /**
   * Contests targeted for audit, map between contest name and contest type.
   */
  @JsonProperty("TARGETS")
  private Map<String,String> targets;

  /**
   * Diluted margins for targeted contests.
   */
  @JsonProperty("DILUTED_MARGINS")
  private Map<String,Double> dilutedMargins;

  /**
   * Subset of contests targeted for audit that are IRV contests.
   */
  @JsonProperty("IRV_CONTESTS")
  private List<String> irvContests;

  /**
   * Number of rounds of auditing that we expect will take place.
   */
  @JsonProperty("EXPECTED_ROUNDS")
  private int expectedRounds;

  /**
   * Non-zero round by round results for audited contests.
   */
  @JsonProperty("RESULTS")
  private Map<Integer,Map<String,Map<String,Integer>>> results;

  /**
   * Imprinted CVR ids that should map to phantom ballots.
   */
  @JsonProperty("PHANTOM_BALLOTS")
  private List<String> phantomBallots;

  /**
   * Ballot choices for select CVR Ids and contests. The instance JSON records only the
   * ones we want to be different to what is on the CVR.
   */
  @JsonProperty("CHOICES")
  private Map<String,Map<String,List<String>>> actualChoices;

  /**
   * @return SQL files (as path strings) representing additional data to load
   * into the database for the given workflow instance. Returned as an
   * unmodifiable list.
   */
  public List<String> getSQLs(){
    return Collections.unmodifiableList(sqls);
  }

  /**
   * @return CVR files (as path strings) for each county, in order. Returned as an
   * unmodifiable list.
   */
  public List<String> getCVRs(){
    return Collections.unmodifiableList(cvrs);
  }

  /**
   * @return Manifest files (as path strings) for each county, in order. Returned as an
   * unmodifiable list.
   */
  public List<String> getManifests(){
    return Collections.unmodifiableList(manifests);
  }

  /**
   * @return Risk limit for this test workflow.
   */
  public BigDecimal getRiskLimit(){
    return riskLimit;
  }

  /**
   * @return Path (as string) to the canonicalisation file for this test workflow.
   */
  public String getCanonicalisationFile(){
    return canonicalList;
  }

  /**
   * @return Unmodifiable map of targeted contests (contest name key and contest type value).
   */
  public Map<String,String> getTargetedContests(){
    return Collections.unmodifiableMap(targets);
  }

  /**
   * @return Unmodifiable list of IRV contests among those targeted for audit.
   */
  public List<String> getIRVContests(){
    return Collections.unmodifiableList(irvContests);
  }

  /**
   * @return Unmodifiable mapping between targeted contest name and diluted margin.
   */
  public Map<String,Double> getDilutedMargins(){
    return Collections.unmodifiableMap(dilutedMargins);
  }

  /**
   * @return The number of rounds of auditing we expect will take place.
   */
  public int getExpectedRounds(){
    return expectedRounds;
  }

  /**
   * @param imprintedId Imprinted id of the CVR for which we want to check if the matching paper
   *                    ballot should be treated as a phantom ballot.
   * @return True if the paper ballot matching the given imprinted ID should be treated as a
   * phantom ballot.
   */
  public boolean isPhantomBallot(final String imprintedId){
    return phantomBallots.contains(imprintedId);
  }

  /**
   * Get the expected result of a given round (in terms of a map between status type and expected
   * value) for a given contest. If the round and/or contest cannot be found in our record of
   * results, it means that all status values are expected to be 0 for that round and contest.
   * @param round    Round number
   * @param contest  Contest name
   * @return A mapping between status type (string) and the expected value for that status type
   * for the given round and contest. Returns an empty Optional if no record for that round/contest
   * is present in our record of results.
   */
  public Optional<Map<String,Integer>> getRoundContestResult(final int round, final String contest){
    if(results.containsKey(round)){
      if(results.get(round).containsKey(contest)){
        return Optional.of(results.get(round).get(contest));
      }
    }
    return Optional.empty();
  }

  /**
   * If in our workflow instance record, we want a specific choices list to be used as the
   * ACVR entry for a contest and CVR, this method returns the choices to be used. If we want
   * to simply use what is on the CVR (ie. the raw choices that were on the CVR), then this method
   * will return an empty Optional.
   * @param imprintedId     Imprinted ID of the CVR that we want to create a choice list for.
   * @param contest         Contest of interest on the CVR.
   * @return An Optional list of strings representing the actual choices to be used for a given
   * contest on an ACVR. If the Optional is empty, it indicates that we should use the CVR choices.
   */
  public Optional<List<String>> getActualChoices(final String imprintedId, final String contest){
    if(actualChoices.containsKey(imprintedId)){
      if(actualChoices.get(imprintedId).containsKey(contest)){
        return Optional.of(Collections.unmodifiableList(actualChoices.get(imprintedId).get(contest)));
      }
    }
    return Optional.empty();
  }
}
