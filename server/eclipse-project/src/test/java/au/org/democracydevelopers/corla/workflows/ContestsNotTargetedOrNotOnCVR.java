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

import static io.restassured.RestAssured.given;
import au.org.democracydevelopers.corla.util.testUtils;
import io.restassured.path.json.JsonPath;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testcontainers.ext.ScriptUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.persistence.Persistence;
import wiremock.net.minidev.json.JSONObject;

import java.io.Console;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.*;

/**
 * This class tests proper discrepancy calculations for
 * - a contest that is on the CVR, but is not targeted,
 * - a contest that is on the CVR, and is targeted, but is not the reason this CVR was sampled.
 * In each case, we audit a ballot, claim that there was a discrepancy, and check that the
 * discrepancy count increases as expected.
 * We then reaudit the ballot and enter the 'correct' value (i.e. the same as the CSV), then check
 * that the discrepancy count goes back to zero.
 */
@Test(enabled=true)
public class ContestsNotTargetedOrNotOnCVR extends Workflow {

  /**
   * Class-wide logger
   */
  private static final Logger LOGGER = LogManager.getLogger(ContestsNotTargetedOrNotOnCVR.class);

  /**
   * The number of counties in this test.
   */
  private static final int NUM_COUNTIES_THIS_TEST = 1;

  /**
   * Names relevant to Adams county contests and candidates.
   */
  private static final String ADAMS_COMMISSIONER = "Adams COUNTY COMMISSIONER DISTRICT 3";
  private static final String U_CO_REGENT = "Regent of the University of Colorado - At Large";
  private static final String CLEAR_WINNER = "Clear Winner";
  private static final String DISTANT_LOSER = "Distant Loser";
  private static final String JEFF_BAKER = "Jeff Baker";
  private static final String JANET_LEE_COOK = "Janet Lee Cook";
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

    runMain("ContestsNotTargetedOrNotOnCVR");
  }

  /**
   * This "test" uploads CVRs and ballot manifests for all 64 counties.
   * The uploads match the following http files in the workflows directory:
   * - demo1_loadCVRs, demo1_loadManifests,
   * - Boulder_loadCVRs, Boulder_loadManifest.
   */
  @Test(enabled=true)
  public void runNotCovered() throws InterruptedException {
    testUtils.log(LOGGER, "Demo1");

    // 1. First upload the manifest
    final String manifestFileName = dataPath + "AdamsAndAlamosa/adams-manifest.csv";
    uploadCounty(1, MANIFEST_FILETYPE, manifestFileName, manifestFileName + ".sha256sum");

    // 2. Then upload the CVRs. The order is important because it's an error to try to import a manifest while the CVRs
    // are being read.
    final String cvrFileName = dataPath + "AdamsAndAlamosa/adams-cvrexport.csv";
    uploadCounty(1, CVR_FILETYPE, cvrFileName, cvrFileName + ".sha256sum");

    // Wait while the CVRs (and manifests) are uploaded.
    assertTrue(uploadSuccessfulWithin(600, Set.of(1), CVR_JSON));
    assertTrue(uploadSuccessfulWithin(20, Set.of(1), MANIFEST_JSON));

    // Get the DoSDashboard refresh response; sanity check for initial state.
    JsonPath dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(ASM_STATE), DOS_INITIAL_STATE.toString());
    assertEquals(dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CHOICES).toString(), "{}");
    assertNull(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON));
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));

    // 3. Set the audit info, including the canonical list and the risk limit; check for update.
    final BigDecimal riskLimit = BigDecimal.valueOf(0.03);
    updateAuditInfo(dataPath + "AdamsAndAlamosa/adams-and-alamosa-canonical-list.csv", riskLimit);
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(0, riskLimit
        .compareTo(new BigDecimal(dashboard.get(AUDIT_INFO + "." + RISK_LIMIT_JSON).toString())));
    // There should be two canonical contests, i.e. the two we targeted.
    assertEquals(2, dashboard.getMap(AUDIT_INFO + "." + CANONICAL_CONTESTS).values().size());
    // Check that the seed is still null.
    assertNull(dashboard.get(AUDIT_INFO + "." + SEED));
    assertEquals(dashboard.get(ASM_STATE), PARTIAL_AUDIT_INFO_SET.toString());

    // No need to generate assertions - these are all plurality.

    // 5. Choose targeted contests for audit.

    targetContests(Map.of(ADAMS_COMMISSIONER ,"COUNTY_WIDE_CONTEST",
                          U_CO_REGENT, "COUNTY_WIDE_CONTEST"));

    // 6. Set the seed.
    setSeed(defaultSeed);

    // This should be complete audit info.
    dashboard = getDoSDashBoardRefreshResponse();
    assertEquals(dashboard.get(AUDIT_INFO + "." + SEED), defaultSeed);
    assertEquals(dashboard.get(ASM_STATE), COMPLETE_AUDIT_INFO_SET.toString());

    // 7. Start Audit Round
    startAuditRound();
    dashboard = getDoSDashBoardRefreshResponse();
    TestAuditSession session = countyAuditInitialise(1);
    final String UCORegentID = getContestID(dashboard, U_CO_REGENT);
    final String adamsCommissionerID = getContestID(dashboard, ADAMS_COMMISSIONER);

    final List<CastVoteRecord> cvrsToAudit = getCvrsToAudit(1, session);

    for(final CastVoteRecord rec : cvrsToAudit) {

      // Make the main map from things that won't change.
      // This is the default that gets used if the CVR isn't on the ballot, which
      // should never happen.
      List<Map<String,Object>> contestInfo = rec.contestInfo().stream().map(info ->
          Map.of("choices", info.choices(),
              "comment", "", "consensus", "YES",
              "contest", info.contest().id())).toList();

      // We want to introduce some discrepancies.
      // Find the CVR choices for the U CO Regent contest.
      Optional<List<Choice>> correctChoicesOpt = getChoices(rec, U_CO_REGENT);
      if (correctChoicesOpt.isPresent()) {
        List<String> correctChoices = correctChoicesOpt.get().stream().map(Choice::name).toList();
        LOGGER.info(String.format("Correct values: %s - %s", U_CO_REGENT, String.join(",", correctChoices)));
        // If it's empty or mentions the winner, make it for the loser.
        if (correctChoices.isEmpty() || correctChoices.contains(CLEAR_WINNER)) {
          contestInfo = setOtherCVRChoices(rec, U_CO_REGENT, List.of(DISTANT_LOSER));
          LOGGER.info(String.format("Update: %s - %s", U_CO_REGENT, DISTANT_LOSER));
          // If it doesn't mention the winner, make it for the winner.
        } else {
          contestInfo = setOtherCVRChoices(rec, U_CO_REGENT, List.of(CLEAR_WINNER));
          LOGGER.info(String.format("Update: %s - %s", U_CO_REGENT, CLEAR_WINNER));
        }
      }

      // We want to introduce some discrepancies.
      // Find the CVR choices for the County Commissioner District 3 contest.
      Optional<List<Choice>> correctChoicesCCD3Opt = getChoices(rec, ADAMS_COMMISSIONER);
      if (correctChoicesCCD3Opt.isPresent()) {
        List<String> correctChoicesCCD3 = correctChoicesCCD3Opt.get().stream().map(Choice::name).toList();
        LOGGER.info(String.format("Correct values: %s - %s", ADAMS_COMMISSIONER, String.join(",", correctChoicesCCD3)));
        // If it's empty or mentions the winner, make it for the loser.
        if (correctChoicesCCD3.isEmpty() || correctChoicesCCD3.contains(JEFF_BAKER)) {
          contestInfo = setOtherCVRChoices(rec, ADAMS_COMMISSIONER, List.of(JANET_LEE_COOK));
          LOGGER.info(String.format("Update: %s - %s", ADAMS_COMMISSIONER, JANET_LEE_COOK));
          // If it doesn't mention the winner, make it for the winner.
        } else {
          contestInfo = setOtherCVRChoices(rec, ADAMS_COMMISSIONER, List.of(JEFF_BAKER));
          LOGGER.info(String.format("Update: %s - %s", ADAMS_COMMISSIONER, JEFF_BAKER));
        }
      }


      // Upload the CVR into which we introduced a discrepancy.
      uploadAuditCVR(rec, session, contestInfo, false);
      dashboard = getDoSDashBoardRefreshResponse();
      Map<String,Integer> uCORegentDiscrepancies = dashboard.getMap(DISCREPANCY_COUNT + "." + UCORegentID);
      Map<String,Integer> adamsCommissionerDiscrepancies = dashboard.getMap(DISCREPANCY_COUNT + "." + adamsCommissionerID);

      // We should have introduced at least one discrepancy.
      assertTrue(uCORegentDiscrepancies.get("-2") > 0
                  || uCORegentDiscrepancies.get("-1") > 0
                  || uCORegentDiscrepancies.get("0") > 0
                  || uCORegentDiscrepancies.get("1") > 0
                  || uCORegentDiscrepancies.get("2") > 0
                  || adamsCommissionerDiscrepancies.get("-2") > 0
                  || adamsCommissionerDiscrepancies.get("-1") > 0
                  || adamsCommissionerDiscrepancies.get("0") > 0
                  || adamsCommissionerDiscrepancies.get("1") > 0
                  || adamsCommissionerDiscrepancies.get("2") > 0);

      // Do a reaudit replacing it with the correct (0-discrepancy) value.
      uploadAuditCVR(rec, session, true);
      dashboard = getDoSDashBoardRefreshResponse();
      uCORegentDiscrepancies = dashboard.getMap(DISCREPANCY_COUNT + "." + UCORegentID);
      // Now there should be no discrepancies
      // This should fail for the untargeted contest.
      assertTrue(uCORegentDiscrepancies.get("-2") == 0
          && uCORegentDiscrepancies.get("-1") == 0
          && uCORegentDiscrepancies.get("0") == 0
          && uCORegentDiscrepancies.get("1") == 0
          && uCORegentDiscrepancies.get("2") == 0);

    }

    // Audit board sign off for each county.
    countySignOffLogout(session);

    // Check that there are no more ballots to sample across all counties in first round.
    dashboard = getDoSDashBoardRefreshResponse();
    final Map<String,Map<String,Object>> status = dashboard.get(COUNTY_STATUS);

    for(final Map.Entry<String,Map<String,Object>> entry : status.entrySet()){
      assertEquals(entry.getValue().get(BALLOTS_REMAINING).toString(), "0");
    //  assertEquals(entry.getValue().get(ESTIMATED_BALLOTS).toString(), "0");
    }
    // TODO Sanity check of assertions and sample size estimates.

    LOGGER.debug("Successfully completed ContestsNotTargetedOrNotOnCVR.");
  }

}
