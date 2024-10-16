/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.IRVComparisonAudit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.*;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;

/**
 * Controller methods relevant to comparison audits.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public final class ComparisonAuditController {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ComparisonAuditController.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditController() {
    // empty
  }

  /**
   * Gets all CVRs to audit in the specified round for the specified county
   * dashboard. This returns a list in audit random sequence order.
   *
   * @param the_dashboard The dashboard.
   * @param the_round_number The round number (indexed from 1).
   * @return the CVRs to audit in the specified round.
   * @exception IllegalArgumentException if the specified round doesn't exist.
   */
  public static List<CVRAuditInfo> cvrsToAuditInRound(final CountyDashboard the_cdb,
                                                      final int the_round_number) {
    if (the_round_number < 1 || the_cdb.rounds().size() < the_round_number) {
      throw new IllegalArgumentException("invalid round specified");
    }
    final Round round = the_cdb.rounds().get(the_round_number - 1);
    final Set<Long> id_set = new HashSet<>();
    final List<CVRAuditInfo> result = new ArrayList<>();

    for (final Long cvr_id : round.auditSubsequence()) {
      if (!id_set.contains(cvr_id)) {
        id_set.add(cvr_id);
        result.add(Persistence.getByID(cvr_id, CVRAuditInfo.class));
      }
    }

    return result;
  }

  /**
   * @return the CVR IDs remaining to audit in the current round, or an empty
   * list if there are no CVRs remaining to audit or if no round is in progress.
   */
  public static List<Long> cvrIDsRemainingInCurrentRound(final CountyDashboard the_cdb) {
    final List<Long> result = new ArrayList<Long>();
    final Round round = the_cdb.currentRound();
    if (round != null) {
      for (int i = 0;
           i + round.actualAuditedPrefixLength() < round.expectedAuditedPrefixLength();
           i++) {
        result.add(round.auditSubsequence().get(i + round.actualAuditedPrefixLength()));
      }
    }
    return result;
  }

  /**
   * Return the ballot cards to audit for a particular county and round.
   *
   * The returned list will not have duplicates and is in an undefined order.
   *
   * @param countyDashboard county dashboard owning the rounds
   * @param roundNumber 1-based round number
   *
   * @return the list of ballot cards for audit. If the query does not result in
   *         any ballot cards, for instance when the round number is invalid,
   *         the returned list is empty.
   */
  public static List<CastVoteRecord>
      ballotsToAudit(final CountyDashboard countyDashboard,
                     final int roundNumber) {
    final List<Round> rounds = countyDashboard.rounds();
    Round round;

    try {
      // roundNumber is 1-based
      round = rounds.get(roundNumber - 1);
    } catch (IndexOutOfBoundsException e) {
      return new ArrayList<CastVoteRecord>();
    }

    LOGGER.debug(
        String.format(
            "Ballot cards to audit: "
            + "[round=%s, round.ballotSequence.size()=%d, round.ballotSequence()=%s]",
            round,
            round.ballotSequence().size(),
            round.ballotSequence()
        )
    );

    // Get all ballot cards for the target round
    final List<CastVoteRecord> cvrs = CastVoteRecordQueries.get(round.ballotSequence());

    // Fetch the CVRs from previous rounds in order to set a flag determining
    // whether they had been audited previously.
    final Set<CastVoteRecord> previousCvrs = new HashSet<CastVoteRecord>();
    for (int i = 1; i < roundNumber; i++) {
      // i is 1-based
      final Round r = rounds.get(i - 1);
      previousCvrs.addAll(CastVoteRecordQueries.get(r.ballotSequence()));
    }

    // PERF TODO: We may be able to replace calls to `audited` with a query that
    // determines the audit status of all the CVRs when they are fetched.
    for (final CastVoteRecord cvr : cvrs) {
      cvr.setAuditFlag(audited(countyDashboard, cvr));
      cvr.setPreviouslyAudited(previousCvrs.contains(cvr));
    }

    return cvrs;
  }

  /**
   * Creates a ComparisonAudit (of the appropriate type - either IRV or plurality) for the given contest and risk limit.
   * No data is persisted. Used both for auditing and sample size estimation.
   *
   * @param contestResult   Contest result for the contest.
   * @param riskLimit       Risk limit for the audit.
   * @return ComparisonAudit of the appropriate type for the given contest.
   */
  public static ComparisonAudit createAuditOfCorrectType(final ContestResult contestResult,
                                                   final BigDecimal riskLimit) {
    final String prefix = "[createAuditOfCorrectType]";

    // If it is all plurality, make a (plurality) ComparisonAudit. This will also be true if the contestResult has no
    // contests.
    if (contestResult.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.PLURALITY.toString()))) {
      return new ComparisonAudit(contestResult, riskLimit, contestResult.getDilutedMargin(), Audit.GAMMA, contestResult.getAuditReason());
    }

    // If it is all IRV, make an IRVComparisonAudit.
    if (contestResult.getContests().stream().map(Contest::description).allMatch(d -> d.equals(ContestType.IRV.toString()))) {
      return new IRVComparisonAudit(contestResult, riskLimit, contestResult.getAuditReason());
    }

    // If it is a mix of different types of contests, or a contest type that we don't recognize, that is an error.
    final String msg = String.format("%s Contest %s has inconsistent or unrecognized contest types.", prefix,
            contestResult.getContestName());
    LOGGER.error(msg);
    throw new RuntimeException(msg);
  }

  /**
   * Creates a ComparisonAudit object for the given contest, and persists the object in the database.
   *
   * @param contestResult  Contest result for the contest.
   * @param riskLimit      Risk limit for the audit.
   * @return ComparisonAudit for the contest.
   */
  public static ComparisonAudit createAudit(final ContestResult contestResult,
                                            final BigDecimal riskLimit) {
    final ComparisonAudit ca = createAuditOfCorrectType(contestResult, riskLimit);

    Persistence.save(ca);
    LOGGER.debug(String.format("[createAudit: contestResult=%s, ComparisonAudit=%s]", contestResult, ca));

    return ca;
  }

  /**
   * Do the part of setup for a county dashboard to start their round.
   * - updateRound
   * - updateCVRUnderAudit
   */
  public static boolean startRound(final CountyDashboard cdb,
                                   final Set<ComparisonAudit> audits,
                                   final List<Long> auditSequence,
                                   final List<Long> ballotSequence) {
    LOGGER.info(String.format("Starting a round for %s, drivingContests=%s",
                              cdb.county(), cdb.drivingContestNames()));
    cdb.startRound(ballotSequence.size(), auditSequence.size(),
                   0, ballotSequence, auditSequence);
    // FIXME it appears these two must happen in this order.
    updateRound(cdb, cdb.currentRound());
    updateCVRUnderAudit(cdb);

    // if the round was started there will be ballots to count
    return cdb.ballotsRemainingInCurrentRound() > 0;
  }


  /** unaudit and audit a submitted ACVR **/
  public static boolean reaudit(final CountyDashboard cdb,
                                final CastVoteRecord cvr,
                                final CastVoteRecord newAcvr,
                                final String comment) {

    LOGGER.info("[reaudit] cvr: " + cvr.toString());

    // DemocracyDevelopers: If this cvr has not been audited before, cai will be null.
    final CVRAuditInfo cai = Persistence.getByID(cvr.id(), CVRAuditInfo.class);
    if (cai == null || cai.acvr() == null) {
      LOGGER.error("can't reaudit a cvr that hasn't been audited");
      return false;
    }
    final CastVoteRecord oldAcvr = cai.acvr();

    final Integer former_count = unaudit(cdb, cai);
    LOGGER.debug("[reaudit] former_count: " + former_count.toString());


    Long revision = CastVoteRecordQueries.maxRevision(cvr);
    // sets revision to 1 if this is the original(revision is zero)
    if (0L == revision) {
      revision = 1L;
      oldAcvr.setRevision(revision);
    }
    oldAcvr.setToReaudited();
    CastVoteRecordQueries.forceUpdate(oldAcvr);

    // the original will not have a re-audit comment
    newAcvr.setComment(comment);

    // sets revision to 2 if this is the first revision(revision is zero)
    newAcvr.setRevision(revision + 1L);
    cai.setACVR(newAcvr);
    Persistence.save(newAcvr);
    Persistence.save(cai);

    final Integer new_count = audit(cdb, cai, true);
    LOGGER.debug("[reaudit] new_count: " + new_count.toString());
    cdb.updateAuditStatus();

    return true;
  }


  /**
   * Submit an audit CVR for a CVR under audit to the specified county dashboard.
   *
   * @param cdb The dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The corresponding audit CVR.
   * @return true if the audit CVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit, or the specified CVR under audit was
   * not in fact under audit.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
  public static boolean submitAuditCVR(final CountyDashboard cdb,
                                       final CastVoteRecord the_cvr_under_audit,
                                       final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;

    final CVRAuditInfo info =
        Persistence.getByID(the_cvr_under_audit.id(), CVRAuditInfo.class);

    if (info == null) {
      LOGGER.warn("attempt to submit ACVR for county " +
                  cdb.id() + ", cvr " +
                  the_cvr_under_audit.id() + " not under audit");
    } else if (checkACVRSanity(the_cvr_under_audit, the_audit_cvr)) {
      LOGGER.trace("[submitAuditCVR: ACVR seems sane]");
      // if the record is the current CVR under audit, or if it hasn't been
      // audited yet, we can just process it
      if (info.acvr() == null) {
        // this audits all instances of the ballot in our current sequence;
        // they might be out of order, but that's OK because we have strong
        // requirements about finishing rounds before looking at results as
        // final and valid
        LOGGER.trace("[submitAuditCVR: ACVR is null, creating]");
        info.setACVR(the_audit_cvr);
        final int new_count = audit(cdb, info, true);
        cdb.addAuditedBallot();
        // there could be a problem here, maybe the cdb counts for all contests
        // and that is good enough??
        cdb.setAuditedSampleCount(cdb.auditedSampleCount() + new_count);
      } else {
        // the record has been audited before, so we need to "unaudit" it
        LOGGER.trace("[submitAuditCVR: ACVR is seen, un/reauditing]");
        final int former_count = unaudit(cdb, info);
        info.setACVR(the_audit_cvr);
        final int new_count = audit(cdb, info, true);
        cdb.setAuditedSampleCount(cdb.auditedSampleCount() - former_count + new_count);
      }
      result = true;
    }  else {
      LOGGER.warn("attempt to submit non-corresponding ACVR " +
                  the_audit_cvr.id() + " for county " + cdb.id() +
                  ", cvr " + the_cvr_under_audit.id());
    }
    Persistence.flush();

    LOGGER.trace(String.format("[Before recalc: auditedSampleCount=%d, estimatedSamples=%d, optimisticSamples=%d",
                              cdb.auditedSampleCount(),
                              cdb.estimatedSamplesToAudit(),
                              cdb.optimisticSamplesToAudit()));
    updateCVRUnderAudit(cdb);
    LOGGER.trace(String.format("[After recalc: auditedSampleCount=%d, estimatedSamples=%d, optimisticSamples=%d",
                              cdb.auditedSampleCount(),
                              cdb.estimatedSamplesToAudit(),
                              cdb.optimisticSamplesToAudit()));
    cdb.updateAuditStatus();
    return result;
  }

  /**
   * Computes the estimated total number of samples to audit on the specified
   * county dashboard. This uses the minimum samples to audit calculation,
   * increased by the percentage of discrepancies seen in the audited ballots
   * so far.
   *
   * @param cdb The dashboard.
   */
  public static int estimatedSamplesToAudit(final CountyDashboard cdb) {
    int to_audit = Integer.MIN_VALUE;
    final Set<String> drivingContests = cdb.drivingContestNames();

    // FIXME might look better as a stream().filter().
    for (final ComparisonAudit ca : cdb.comparisonAudits()) { // to_audit = cdb.comparisonAudits.stream()
      final String contestName = ca.contestResult().getContestName(); // strike
      if (drivingContests.contains(contestName)) { // .filter(ca -> drivingContests.contains(ca.contestResult().getContestName()))
        final int bta = ca.estimatedSamplesToAudit(); // .map(ComparisonAudit::estimatedSamplesToAudit)
        to_audit = Math.max(to_audit, bta);           // .max() gets the biggest of all driving contest estimated samples
        LOGGER.debug(String.format("[estimatedSamplesToAudit: "
                                   + "driving contest=%s, bta=%d, to_audit=%d]",
                                   ca.contestResult().getContestName(), bta, to_audit));
      }
    }
    return Math.max(0, to_audit);
  }

  /**
   * Checks to see if the specified CVR has been audited on the specified county
   * dashboard. This check sets the audit flag on the CVR record in memory,
   * so its result can be accessed later without an expensive database hit.
   *
   * @param the_cdb The county dashboard.
   * @param the_cvr The CVR.
   * @return true if the specified CVR has been audited, false otherwise.
   */
  public static boolean audited(final CountyDashboard the_cdb,
                                final CastVoteRecord the_cvr) {
    final CVRAuditInfo info = Persistence.getByID(the_cvr.id(), CVRAuditInfo.class);
    final boolean result;
    if (info == null || info.acvr() == null) {
      result = false;
    } else {
      result = true;
    }
    return result;
  }

  /**
   * Updates a round object with the disagreements and discrepancies
   * that already exist for CVRs in its audit subsequence, creates
   * any CVRAuditInfo objects that don't exist but need to, and
   * increases the multiplicity of any CVRAuditInfo objects that already
   * exist and are duplicated in this round.
   *
   * @param cdb The county dashboard to update.
   * @param round The round to update.
   */
  private static void updateRound(final CountyDashboard cdb,
                                  final Round round) {
    for (final Long cvrID : new HashSet<>(round.auditSubsequence())) {
      final Map<String, AuditReason> auditReasons = new HashMap<>();
      final Set<AuditReason> discrepancies = new HashSet<>();
      final Set<AuditReason> disagreements = new HashSet<>();

      CVRAuditInfo cvrai = Persistence.getByID(cvrID, CVRAuditInfo.class);
      if (cvrai == null) {
        cvrai = new CVRAuditInfo(Persistence.getByID(cvrID, CastVoteRecord.class));
      }

      if (cvrai.acvr() != null) {
        // do the thing
        // update the round statistics as necessary
        for (final ComparisonAudit ca : cdb.comparisonAudits()) {
          final String contestName = ca.contestResult().getContestName();
          AuditReason auditReason = ca.auditReason();

          if (ca.isCovering(cvrID) && auditReason.isTargeted()) {
            // If this CVR is interesting to this audit, the discrepancy
            // should be in the audited contests part of the dashboard.
            LOGGER.debug(String.format("[updateRound: CVR %d is covered in a targeted audit."
                                       + " contestName=%s, auditReason=%s]",
                                       cvrID, contestName, auditReason));
            auditReasons.put(contestName, auditReason);
          } else {
            // Otherwise, let's put it in the unaudited contest bucket.
            auditReason = AuditReason.OPPORTUNISTIC_BENEFITS;
            LOGGER.debug(String.format("[updateRound: CVR %d has a discrepancy; not covered by"
                                       + " contestName=%s, auditReason=%s]",
                                       cvrID, contestName, auditReason));
            auditReasons.put(contestName, auditReason);
          }

          final OptionalInt discrepancy = ca.computeDiscrepancy(cvrai.cvr(), cvrai.acvr());
          if (!discrepancies.contains(auditReason) && discrepancy.isPresent()) {
            discrepancies.add(auditReason);
          }


          final int multiplicity = ca.multiplicity(cvrID);
          for (int i = 0; i < multiplicity; i++) {
            round.addDiscrepancy(discrepancies);
            round.addDisagreement(disagreements);
          }

          cvrai.setMultiplicityByContest(ca.id(), multiplicity);
        }

        for (final CVRContestInfo ci : cvrai.acvr().contestInfo()) {
          final AuditReason reason = auditReasons.get(ci.contest().name());
          if (ci.consensus() == ConsensusValue.NO) {
            // TODO check to see if we have disagreement problems. this
            // is being added in the other loop.
            disagreements.add(reason);
          }
        }
      }

      Persistence.saveOrUpdate(cvrai);
    }
  }

  /**
   * Audits a CVR/ACVR pair by adding it to all the audits in progress.
   * This also updates the local audit counters, as appropriate.
   *
   * @param cdb The dashboard.
   * @param auditInfo The CVRAuditInfo to audit.
   * @param updateCounters true to update the county dashboard
   * counters, false otherwise; false is used when this ballot
   * has already been audited once.
   * @return the number of times the record was audited.
   */
  @SuppressWarnings({"PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
      "PMD.NPathComplexity"})
  private static int audit(final CountyDashboard cdb,
                           final CVRAuditInfo auditInfo,
                           final boolean updateCounters) {
    final Set<String> contestDisagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvrUnderAudit = auditInfo.cvr();
    final Long cvrID = cvrUnderAudit.id();
    final CastVoteRecord auditCvr = auditInfo.acvr();
    int totalCount = 0;

    // discrepancies
    for (final ComparisonAudit ca : cdb.comparisonAudits()) {
      AuditReason auditReason = ca.auditReason();
      final String contestName = ca.contestResult().getContestName();

      // how many times does this cvr appear in the audit samples; how many dups?
      final int multiplicity = ca.multiplicity(cvrID);

      // how many times does a discrepancy need to be recorded, while counting
      // each sample(or occurance) only once - across rounds
      final int auditCount = multiplicity - auditInfo.getCountByContest(ca.id());

      // to report something to the caller
      totalCount += auditCount;

      auditInfo.setMultiplicityByContest(ca.id(), multiplicity);
      auditInfo.setCountByContest(ca.id(), multiplicity);

      final OptionalInt discrepancy = ca.computeDiscrepancy(cvrUnderAudit, auditCvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < auditCount; i++) {
          ca.recordDiscrepancy(auditInfo, discrepancy.getAsInt());
        }

        if (ca.isCovering(cvrID) && auditReason.isTargeted()) {
          LOGGER.debug(String.format("[audit: CVR %d is covered in a targeted audit."
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          discrepancies.add(auditReason);
        } else {
          auditReason = AuditReason.OPPORTUNISTIC_BENEFITS;
          LOGGER.debug(String.format("[audit: CVR %d has a discrepancy, but isn't covered by"
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          discrepancies.add(auditReason);
        }
      }

      // disagreements
      for (final CVRContestInfo ci : auditCvr.contestInfo()) {
        if (ci.consensus() == ConsensusValue.NO) {
          contestDisagreements.add(ci.contest().name());
        }
      }

      // NOTE: this may or may not be correct, we're not sure
      if (contestDisagreements.contains(contestName)) {
        for (int i = 0; i < auditCount; i++) {
          ca.recordDisagreement(auditInfo);
        }
        if (ca.isCovering(cvrID) && auditReason.isTargeted()) {
          LOGGER.debug(String.format("[audit: CVR %d is covered in a targeted audit."
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          disagreements.add(auditReason);
        } else {
          auditReason = AuditReason.OPPORTUNISTIC_BENEFITS;
          LOGGER.debug(String.format("[audit: CVR %d has a disagreement, but isn't covered by"
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          disagreements.add(auditReason);
        }
      }

      ca.signalSampleAudited(auditCount, cvrID);
      Persistence.saveOrUpdate(ca);
    }

    // todo does this need to be in the loop?
    auditInfo.setDiscrepancy(discrepancies);
    auditInfo.setDisagreement(disagreements);
    Persistence.saveOrUpdate(auditInfo);

    if (updateCounters) {
      cdb.addDiscrepancy(discrepancies);
      cdb.addDisagreement(disagreements);
      LOGGER.debug(String.format("[audit: %s County discrepancies=%s, disagreements=%s]",
                                 cdb.county().name(), discrepancies, disagreements));
    }

    return totalCount;
  }

  /**
   * "Unaudits" a CVR/ACVR pair by removing it from all the audits in
   * progress in the specified county dashboard. This also updates the
   * dashboard's counters as appropriate.
   *
   * @param the_cdb The county dashboard.
   * @param the_info The CVRAuditInfo to unaudit.
   */
  @SuppressWarnings("PMD.NPathComplexity")
  private static int unaudit(final CountyDashboard the_cdb, final CVRAuditInfo the_info) {
    final Set<String> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.cvr();
    final Long cvrID = cvr_under_audit.id();
    final CastVoteRecord audit_cvr = the_info.acvr();
    int totalCount = 0;

    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest().name());
      }
    }

    for (final ComparisonAudit ca : the_cdb.comparisonAudits()) {
      AuditReason auditReason = ca.auditReason();
      final String contestName = ca.contestResult().getContestName();

      // how many times does this cvr appear in the audit samples; how many dups?
      final int multiplicity = ca.multiplicity(cvr_under_audit.id());

      // if the cvr has been audited, which is must have been to be here, then
      final int auditCount = multiplicity;

      // to report something to the caller
      totalCount += auditCount;

      final OptionalInt discrepancy =
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < auditCount; i++) {
          ca.removeDiscrepancy(the_info, discrepancy.getAsInt());
        }

        if (ca.isCovering(cvrID) && auditReason.isTargeted()) {
          LOGGER.debug(String.format("[audit: CVR %d is covered in a targeted audit."
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          discrepancies.add(auditReason);
        } else {
          auditReason = AuditReason.OPPORTUNISTIC_BENEFITS;
          LOGGER.debug(String.format("[audit: CVR %d has a discrepancy, but isn't covered by"
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          discrepancies.add(auditReason);
        }
      }
      if (contest_disagreements.contains(ca.contestResult().getContestName())) {
        for (int i = 0; i < auditCount; i++) {
          ca.removeDisagreement(the_info);
        }
        if (ca.isCovering(cvrID) && auditReason.isTargeted()) {
          LOGGER.debug(String.format("[audit: CVR %d is covered in a targeted audit."
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          disagreements.add(auditReason);
        } else {
          auditReason = AuditReason.OPPORTUNISTIC_BENEFITS;
          LOGGER.debug(String.format("[audit: CVR %d has a disagreement, but isn't covered by"
                                     + " contestName=%s, auditReason=%s]",
                                     cvrID, contestName, auditReason));
          disagreements.add(auditReason);
        }
      }
      ca.signalSampleUnaudited(auditCount, cvr_under_audit.id());
      Persistence.saveOrUpdate(ca);
    }

    the_info.setDisagreement(null);
    the_info.setDiscrepancy(null);
    the_info.resetCounted();
    Persistence.saveOrUpdate(the_info);

    the_cdb.removeDiscrepancy(discrepancies);
    the_cdb.removeDisagreement(disagreements);

    return totalCount;
  }

  /**
   * Updates the current CVR to audit index of the specified county
   * dashboard to the first CVR after the current CVR under audit that
   * lacks an ACVR. This "audits" all the CVR/ACVR pairs it finds
   * in between, and extends the sequence of ballots to audit if it
   * reaches the end and the audit is not concluded.
   *
   * @param cdb The dashboard.
   */
  public static void updateCVRUnderAudit(final CountyDashboard cdb) {
    // start from where we are in the current round
    final Round round = cdb.currentRound();

    if (round != null) {
      final Set<Long> checked_ids = new HashSet<>();
      int index = round.actualAuditedPrefixLength() - round.startAuditedPrefixLength();

      while (index < round.auditSubsequence().size()) {
        final Long cvr_id = round.auditSubsequence().get(index);
        if (!checked_ids.contains(cvr_id)) {
          checked_ids.add(cvr_id);

          final CVRAuditInfo cai = Persistence.getByID(cvr_id, CVRAuditInfo.class);

          if (cai == null || cai.acvr() == null) {
            break;              // ok, so this hasn't been audited yet.
          } else {
            final int audit_count = audit(cdb, cai, false);
            cdb.setAuditedSampleCount(cdb.auditedSampleCount() + audit_count);
          }
        }
        index = index + 1;
      }
      // FIXME audited prefix length might not mean the same things that
      // it once meant.
      cdb.setAuditedPrefixLength(index + round.startAuditedPrefixLength());
      cdb.updateAuditStatus();
    }
  }

  /**
   * Checks that the specified CVR and ACVR are an audit pair, and that
   * the specified ACVR is auditor generated.
   *
   * @param the_cvr The CVR.
   * @param the_acvr The ACVR.
   */
  private static boolean checkACVRSanity(final CastVoteRecord the_cvr,
                                         final CastVoteRecord the_acvr) {
    return the_cvr.isAuditPairWith(the_acvr) &&
      (the_acvr.recordType().isAuditorGenerated()
       || the_acvr.recordType().isSystemGenerated())
      ;
  }
}
