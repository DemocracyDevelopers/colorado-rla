/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * 
 * @created Aug 12, 2017
 * 
 * @copyright 2017 Colorado Department of State
 * 
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.util.*;

import javax.persistence.PersistenceException;

import au.org.democracydevelopers.corla.model.GenerateAssertionsSummary;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ComparisonAuditQueries;
import us.freeandfair.corla.query.ContestResultQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

import static au.org.democracydevelopers.corla.endpoint.GetAssertions.pingRaireService;

/**
 * The response generated on a refresh of the DoS dashboard.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings({"URF_UNREAD_FIELD",
    // Justification: Field is read by Gson.
    "SF_SWITCH_NO_DEFAULT"})
// Justification: False positive; there is a default case.

public class DoSDashboardRefreshResponse {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(DoSDashboardRefreshResponse.class);
  /**
   * The ASM state.
   */
  private final ASMState my_asm_state;

  /**
   * A map from audited contests to audit reasons.
   */
  private final SortedMap<Long, AuditReason> my_audited_contests;

  /**
   * A map from audited contests to estimated ballots left to audit.
   */
  private final SortedMap<Long, Integer> my_estimated_ballots_to_audit;

  /**
   * A map from audited contests to optimistic ballots left to audit.
   */
  private final SortedMap<Long, Integer> my_optimistic_ballots_to_audit;

  /**
   * A map from audited contests to discrepancy count maps.
   */
  private final SortedMap<Long, Map<Integer, Integer>> my_discrepancy_count;

  /**
   * A map from county IDs to county status.
   */
  private final SortedMap<Long, CountyDashboardRefreshResponse> my_county_status;

  /**
   * A set of contests selected for full hand count.
   */
  private final List<Long> my_hand_count_contests;

  /**
   * The audit info.
   */
  private final AuditInfo my_audit_info;

  /**
   * The audit reasons for the contests under audit.
   */
  private final SortedMap<Long, AuditReason> my_audit_reasons;

  /**
   * The audit types for the contests under audit.
   */
  private final SortedMap<Long, AuditType> my_audit_types;

  /**
   * The generate assertions summaries, for IRV contests. Keyed by contest name (which is repeated
   * in the GenerateAssertionsSummary).
   */
  private final List<GenerateAssertionsSummaryWithCounty> my_generate_assertions_summaries;

  /**
   * The status of the raire service when ping'd. Either "OK" or "Unreachable".
   */
  private final String my_raire_service_status;

  /**
   * Placeholder string for when a contest crosses multiple counties. Used for IRV assertion-generation
   * summaries.
   */
  private final static String MULTIPLE_COUNTIES = "Multiple";

  /**
   * Constructs a new DosDashboardRefreshResponse.
   *
   * @param the_asm_state                     The ASM state.
   * @param the_audited_contests              The audited contests.
   * @param the_estimated_ballots_to_audit    The estimated ballots to audit, by contest.
   * @param the_optimistic_ballots_to_audit   The optimistic ballots to audit, by contest.
   * @param the_discrepancy_counts            The discrepancy count for each discrepancy
   *                                          type, by contest.
   * @param the_county_status                 The county statuses.
   * @param the_hand_count_contests           The hand count contests.
   * @param the_audit_info                    The election info.
   * @param the_audit_reasons                 The reasons for auditing each contest.
   * @param the_audit_types                   The audit type (usually either COMPARISON or NOT_AUDITABLE)
   * @param the_generate_assertions_summaries The GenerateAssertionsSummaries, for IRV contests, with contest names added.
   * @param the_raire_service_status          The status of the raire service, either "OK" or "Unreachable".
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  protected DoSDashboardRefreshResponse(final ASMState the_asm_state,
                                        final SortedMap<Long, AuditReason> the_audited_contests,
                                        final SortedMap<Long, Integer> the_estimated_ballots_to_audit,
                                        final SortedMap<Long, Integer> the_optimistic_ballots_to_audit,
                                        final SortedMap<Long, Map<Integer, Integer>> the_discrepancy_counts,
                                        final SortedMap<Long, CountyDashboardRefreshResponse> the_county_status,
                                        final List<Long> the_hand_count_contests,
                                        final AuditInfo the_audit_info,
                                        final SortedMap<Long, AuditReason> the_audit_reasons,
                                        final SortedMap<Long, AuditType> the_audit_types,
                                        final List<GenerateAssertionsSummaryWithCounty> the_generate_assertions_summaries,
                                        final String the_raire_service_status) {
    my_asm_state = the_asm_state;
    my_audited_contests = the_audited_contests;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_optimistic_ballots_to_audit = the_optimistic_ballots_to_audit;
    my_discrepancy_count = the_discrepancy_counts;
    my_county_status = the_county_status;
    my_hand_count_contests = the_hand_count_contests;
    my_audit_info = the_audit_info;
    my_audit_reasons = the_audit_reasons;
    my_audit_types = the_audit_types;
    my_generate_assertions_summaries = the_generate_assertions_summaries;
    my_raire_service_status = the_raire_service_status;
  }

  /**
   * Gets the DoSDashboardRefreshResponse for the specified DoS dashboard.
   *
   * @param dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   *              response does not exist.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public static DoSDashboardRefreshResponse createResponse(final DoSDashboard dashboard) {
    // construct the various audit info from the contests to audit in the
    // dashboard
    final SortedMap<Long, AuditReason> audited_contests = new TreeMap<Long, AuditReason>();
    final SortedMap<Long, Integer> estimated_ballots_to_audit = new TreeMap<Long, Integer>();
    final SortedMap<Long, Integer> optimistic_ballots_to_audit = new TreeMap<Long, Integer>();
    final SortedMap<Long, Map<Integer, Integer>> discrepancy_count = new TreeMap<>();
    final List<Long> hand_count_contests = new ArrayList<Long>();
    final SortedMap<Long, AuditReason> audit_reasons = new TreeMap<Long, AuditReason>();
    final SortedMap<Long, AuditType> audit_types = new TreeMap<Long, AuditType>();

    for (final ContestToAudit cta : dashboard.contestsToAudit()) {
      if (cta.audit() != AuditType.NONE) {
        audit_reasons.put(cta.contest().id(), cta.reason());
        audit_types.put(cta.contest().id(), cta.audit());
      }
      switch (cta.audit()) {
        case COMPARISON:
          final Map<Integer, Integer> discrepancy = new HashMap<>();
          int optimistic = 0;
          int estimated = 0;
          audited_contests.put(cta.contest().id(), cta.reason());

          final ComparisonAudit ca = ComparisonAuditQueries.matching(cta.contest().name());
          if (null != ca) {
            optimistic = ca.optimisticRemaining();
            estimated = ca.estimatedRemaining();

            LOGGER.info(String
                .format("[createResponse: optimistic=%d, estimated = %d, ca.optimisticSamplesToAudit()=%d, ca.estimatedSamplesToAudit()=%d, ca.getAuditedSampleCount()=%d]",
                        optimistic, estimated, ca.optimisticSamplesToAudit(),
                        ca.estimatedSamplesToAudit(), ca.getAuditedSampleCount()));

            // possible discrepancy types range from -2 to 2 inclusive,
            // and we provide them all in the refresh response
            for (int i = -2; i <= 2; i++) {
              if (discrepancy.get(i) == null) {
                discrepancy.put(i, 0);
              }
              discrepancy.put(i, discrepancy.get(i) + ca.discrepancyCount(i));
            }
          }

          estimated_ballots_to_audit.put(cta.contest().id(), estimated);
          optimistic_ballots_to_audit.put(cta.contest().id(), optimistic);
          discrepancy_count.put(cta.contest().id(), discrepancy);
          break;

        case HAND_COUNT:
          // we list these separately for some reason

          // FIXME probably should be a set of ContestResult IDs.
          hand_count_contests.add(cta.contest().id());
          break;

        default:
      }
    }

    Collections.sort(hand_count_contests);

    // status
    final DoSDashboardASM asm =
        ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);

    final String raireServiceStatus = pingRaireService();

    return new DoSDashboardRefreshResponse(asm.currentState(), audited_contests,
                                           estimated_ballots_to_audit,
                                           optimistic_ballots_to_audit, discrepancy_count,
                                           countyStatusMap(), hand_count_contests,
                                           dashboard.auditInfo(), audit_reasons, audit_types,
                                           addCountiesToSummaries(), raireServiceStatus);
  }


  /**
   * Gets the GenerateAssertionsSummary list from the database, and makes a corresponding list
   * with the applicable Counties included in each item.
   * @return a list of GenerateAssertionsSummaryWithCounty, which includes the County name if there's
   * a unique on for this contest, or "Multiple" if there is more than one.
   */
  private static List<GenerateAssertionsSummaryWithCounty> addCountiesToSummaries() {
    List<GenerateAssertionsSummaryWithCounty> generateAssertionsSummaries = new ArrayList<>();

    // Load all the Generate Assertions Summaries from the database into the generate_assertions_list.
    final List<GenerateAssertionsSummary> generate_assertions_list
        = Persistence.getAll(GenerateAssertionsSummary.class);

    // Find out which county each contest is in; fill in 'Multiple' if there is more than one.
    for (GenerateAssertionsSummary summary : generate_assertions_list) {
      final Optional<ContestResult> cr = ContestResultQueries.find(summary.getContestName());
      String countyName = "";
      if (cr.isEmpty() || cr.get().getCounties().isEmpty()) {
        // This isn't supposed to happen. Keep the summary, with a blank county name, and continue but warn.
        LOGGER.warn(String.format("%s %s %s.", "[addCountiesToSummaries] ", "Empty ContestResult or County Name for Contest ",
            summary.getContestName()));
      } else {
        Set<County> counties = cr.get().getCounties();
        if (counties.size() == 1) {
          countyName = counties.stream().findFirst().get().name();
        } else {
          // Must be >1 because we already checked for zero.
          countyName = MULTIPLE_COUNTIES;
        }
      }
      // Add the summary in, whether we found a county or not.
      generateAssertionsSummaries.add(new GenerateAssertionsSummaryWithCounty(summary, countyName));
    }
    return generateAssertionsSummaries;
  }

  /**
   * Gets the county statuses for all counties in the database.
   *
   * @return a map from county identifiers to statuses.
   */
  private static SortedMap<Long, CountyDashboardRefreshResponse> countyStatusMap() {
    final SortedMap<Long, CountyDashboardRefreshResponse> status_map =
        new TreeMap<Long, CountyDashboardRefreshResponse>();
    final List<County> counties = Persistence.getAll(County.class);

    for (final County c : counties) {
      final CountyDashboard db = Persistence.getByID(c.id(), CountyDashboard.class);
      if (db == null) {
        throw new PersistenceException("unable to read county dashboard state.");
      } else {
        status_map.put(db.id(), CountyDashboardRefreshResponse.createAbbreviatedResponse(db));
      }
    }

    return status_map;
  }

  /**
   * The same as a GenerateAssertionsSummary, but with the county name attached, or "multiple" if more than one.
   */
  protected record GenerateAssertionsSummaryWithCounty(
      GenerateAssertionsSummary summary,
      String countyName
  ){}
}
