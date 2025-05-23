/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

import javax.persistence.PersistenceException;

import au.org.democracydevelopers.corla.model.ContestType;
import au.org.democracydevelopers.corla.model.vote.IRVChoices;
import au.org.democracydevelopers.corla.model.vote.IRVParsingException;
import au.org.democracydevelopers.corla.model.vote.IRVPreference;
import au.org.democracydevelopers.corla.model.vote.IRVBallotInterpretation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.util.DBExceptionUtil;
import us.freeandfair.corla.util.ExponentialBackoffHelper;

import static au.org.democracydevelopers.corla.csv.IRVHeadersParser.generateAllIRVPreferences;
import static au.org.democracydevelopers.corla.csv.IRVHeadersParser.validateIRVPreferenceHeaders;

/**
 * Parser for Dominion CVR export files.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 * Additions by Vanessa Teague for IRV and STV parsing.
 * This parser can cope with 3 different kinds of contests, defined by their headers.
 * - plurality single- and multi- winner contests, which are parsed directly and saved,
 * - IRV (ranked single-winner) contests, which are parsed with some extra complexity for dealing with
 *   their special candidate headers (names and ranks), and saved,
 * - STV (ranked multi-winner) contests, which by request are accepted but then dropped completely.
 *   These are ephemerally parsed into the Contest data structures, which are then used to parse each
 *   CVR, but no CountyContestResult is added to my_results, no Contest is persisted, and none of the
 *   choices are added into the CVRContestInfo structures attached to the CVR. This processing is
 *   intended to replicate the situation in which the STV contest had simply been omitted from the
 *   CSV file. However, parsing errors (invalid headers, missing headers, etc) will still cause an
 *   error message.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class DominionCVRExportParser {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(DominionCVRExportParser.class);

  /**
   * The name of the transaction size property.
   */
  public static final String TRANSACTION_SIZE_PROPERTY = "cvr_import_transaction_size";

  /**
   * The name of the batch size property.
   */
  public static final String BATCH_SIZE_PROPERTY = "cvr_import_batch_size";

  /**
   * The number of times to retry a county dashboard update operation.
   */
  private static final int UPDATE_RETRIES = 15;

  /**
   * The number of milliseconds to sleep between transaction retries.
   */
  private static final long TRANSACTION_SLEEP_MSEC = 10;

  /**
   * The interval at which to log progress.
   */
  private static final int PROGRESS_INTERVAL = 500;

  /**
   * The default size of a batch of CVRs to be flushed to the database.
   */
  private static final int DEFAULT_BATCH_SIZE = 80;

  /**
   * The default size of a batch of CVRs to be committed as a transaction.
   */
  private static final int DEFAULT_TRANSACTION_SIZE = 400;

  /**
   * The column containing the CVR number in a Dominion export file.
   */
  private static final String CVR_NUMBER_HEADER = "CvrNumber";

  /**
   * The column containing the tabulator number in a Dominion export file.
   */
  private static final String TABULATOR_NUMBER_HEADER = "TabulatorNum";

  /**
   * The column containing the batch ID in a Dominion export file.
   */
  private static final String BATCH_ID_HEADER = "BatchId";

  /**
   * The column containing the record ID in a Dominion export file.
   */
  private static final String RECORD_ID_HEADER = "RecordId";

  /**
   * The column containing the imprinted ID in a Dominion export file.
   */
  private static final String IMPRINTED_ID_HEADER = "ImprintedId";

  /**
   * The column containing the counting group in a Dominion export file.
   */
  private static final String COUNTING_GROUP_HEADER = "CountingGroup";

  /**
   * The column containing the precinct portion in a Dominion export file.
   */
  @SuppressWarnings({"PMD.UnusedPrivateField", "unused"})
  private static final String PRECINCT_PORTION_HEADER = "PrecinctPortion";

  /**
   * The column containing the ballot type in a Dominion export file.
   */
  private static final String BALLOT_TYPE_HEADER = "BallotType";

  /**
   * The prohibited headers.
   */
  private static final String[] PROHIBITED_HEADERS = {COUNTING_GROUP_HEADER};

  /**
   * The required headers.
   */
  private static final String[] REQUIRED_HEADERS = {
      CVR_NUMBER_HEADER, TABULATOR_NUMBER_HEADER, BATCH_ID_HEADER,
      RECORD_ID_HEADER, IMPRINTED_ID_HEADER, BALLOT_TYPE_HEADER
      };

  /**
   * The string indicating how many votes are permitted, for plurality.
   */
  private static final String PLURALITY_VOTE_FOR = "(Vote For=";

  /**
   * The string indicating how many votes are permitted, for IRV.
   */
  private static final String IRV_VOTE_FOR = "Number of ranks=";

  /**
   * The string indicating how many winners are being selected, for IRV.
   * We only audit when there is one winner.
   */
  private static final String IRV_WINNERS_ALLOWED = "(Number of positions=";

  /**
   * Indicator (-1) to show that there are no votes allowed. This is used to identify STV contests.
   */
  private static final int STV_NO_VOTES = -1;

  /**
   * The parser to be used.
   */
  private final CSVParser my_parser;

  /**
   * The map from column names to column numbers.
   */
  private final Map<String, Integer> my_columns = new HashMap<String, Integer>();

  /**
   * The index of the first choice/contest column.
   */
  private int my_first_contest_column;

  /**
   * The list of contests parsed from the supplied data export.
   */
  private final List<Contest> my_contests = new ArrayList<Contest>();

  /**
   * The list of county contest results we build from the supplied
   * data export.
   */
  private final List<CountyContestResult> my_results =
      new ArrayList<CountyContestResult>();

  /**
   * The county whose CVRs we are parsing.
   */
  private final County my_county;

  /**
   * The number of parsed CVRs.
   */
  private int my_record_count = -1;

  /**
   * The set of parsed CVRs that haven't yet been flushed to the database.
   */
  private final Set<CastVoteRecord> my_parsed_cvrs = new HashSet<>();

  /**
   * The size of a batch of CVRs to be flushed to the database.
   */
  private final int my_batch_size;

  /**
   * The size of a batch of CVRs to be committed as a transaction.
   */
  private final int my_transaction_size;

  /**
   * A flag that indicates whether the parse is processed as multiple
   * transactions.
   */
  private final boolean my_multi_transaction;

  /**
   * Construct a new Dominion CVR export parser using the specified Reader,
   * for CVRs provided by the specified county.
   *
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @param the_properties The properties from which to read any overrides to the
   * default transaction and batch sizes.
   * @param the_multi_transaction true to commit the CVRs in multiple transactions,
   * false otherwise. If this is true, the parser assumes that a transaction is
   * in progress when invoked, and periodically commits that transaction and
   * starts a new one to continue parsing, leaving a transaction open at completion.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final Reader the_reader, final County the_county,
                                 final Properties the_properties,
                                 final boolean the_multi_transaction)
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_county = the_county;
    my_multi_transaction = the_multi_transaction;
    my_batch_size = parseProperty(the_properties, BATCH_SIZE_PROPERTY,
                                  DEFAULT_BATCH_SIZE);
    my_transaction_size = parseProperty(the_properties, TRANSACTION_SIZE_PROPERTY,
                                        DEFAULT_TRANSACTION_SIZE);
  }

  /**
   * Construct a new Dominion CVR export parser to parse the specified
   * CSV string, for CVRs provided by the specified county.
   *
   * @param the_string The CSV string to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final String the_string, final County the_county)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county = the_county;
    my_multi_transaction = false;
    my_batch_size = DEFAULT_BATCH_SIZE;
    my_transaction_size = DEFAULT_TRANSACTION_SIZE;
  }

  /**
   * Parse an integer value from the specified property, returning the specified
   * default if the property doesn't exist or is not an integer.
   *
   * @param the_properties The properties to use.
   * @param the_property_name The name of the property to parse.
   * @param the_default_value The default value.
   */
  private int parseProperty(final Properties the_properties,
                            final String the_property_name,
                            final int the_default_value) {
    int result;

    try {
      result = Integer.parseInt(the_properties.getProperty(the_property_name,
                                                           String.valueOf(the_default_value)));
    } catch (final NumberFormatException e) {
      result = the_default_value;
    }

    return result;
  }

  /**
   * Strip the '="..."' from a column.
   *
   * @param the_value The value to strip.
   * @return the stripped value, as a String, or the original String if it
   * does not have the '="..."' form.
   */
  private String stripEqualQuotes(final String the_value) {
    String result = the_value;
    if (the_value.startsWith("=\"") && the_value.endsWith("\"")) {
      result = the_value.substring(0, the_value.length() - 1).replaceFirst("=\"", "");
    }
    return result;
  }

  /**
   * Updates the contest names, max selections, and choice counts structures.
   *
   * @param the_line The CSV line containing the contest information.
   * @param the_names The contest names.
   * @param the_votes_allowed The votes allowed table.
   * @param the_choice_counts The choice counts table.
   */
  private void updateContestStructures(final CSVRecord the_line,
                                       final List<String> the_names,
                                       final Map<String, Integer> the_votes_allowed,
                                       final Map<String, Integer> the_choice_counts,
                                       final Map<String, ContestType> the_contest_types) {
    final String prefix = "[updateContestStructures] ";

    int index = my_first_contest_column;
    do {
      final String c = the_line.get(index);
      int count = 0;
      while (index < the_line.size() &&
             c.equals(the_line.get(index))) {
        index = index + 1;
        count = count + 1;
      }

      try {
        final int pluralityVotesAllowed = extractPositiveInteger(c, PLURALITY_VOTE_FOR, ")");
        final int irvVotesAllowed = extractPositiveInteger(c, IRV_VOTE_FOR, ")");
        final int irvWinners = extractPositiveInteger(c, IRV_WINNERS_ALLOWED, ",");
        // If winners and allowed votes are as expected for plurality, this is a plurality contest.
        final boolean isPlurality = pluralityVotesAllowed > 0 && irvVotesAllowed == -1 && irvWinners == -1;
        final boolean isIRV = pluralityVotesAllowed == -1 && irvVotesAllowed > 0 && irvWinners == 1;
        // If it looks like IRV but has more than one winner, it's an STV contest.
        final boolean isSTV = pluralityVotesAllowed == -1 && irvVotesAllowed > 0 && irvWinners > 1;

        // Counterintuitively, we also code the STV contest as a 'plurality' contest, which works
        // fine with candidate names of the form Alice(1), Alice(2), etc.
        // STV_NO_VOTES = -1 in the_votes_allowed to encode that it's an STV contest.
        if(isPlurality || isSTV) {
          final String contestName
              = c.substring(0, c.indexOf(isPlurality ? PLURALITY_VOTE_FOR : IRV_VOTE_FOR)).strip();
          the_names.add(contestName);
          the_contest_types.put(contestName, ContestType.PLURALITY);
          the_choice_counts.put(contestName, count);
          the_votes_allowed.put(contestName, isPlurality ? pluralityVotesAllowed : STV_NO_VOTES);

        // If winners and allowed votes are as expected for IRV, this is an IRV contest.
        // We expect the count to be the real number of choices times the number of ranks.
        } else if(isIRV && count % irvVotesAllowed == 0) {
          final String contestName = c.substring(0, c.indexOf(IRV_WINNERS_ALLOWED)).strip();
          the_names.add(contestName);
          the_contest_types.put(contestName, ContestType.IRV);
          // Each real choice (i.e. candidate name) is repeated 'irvVotesAllowed' times, e.g.
          // Alice(1), Alice(2), etc. We just want to record the number of real choices.
          the_choice_counts.put(contestName, count / irvVotesAllowed);
          the_votes_allowed.put(contestName, irvVotesAllowed);

        } else {
          // The header didn't have the keywords we expected.
          final String msg = "Could not parse header: ";
          LOGGER.error(String.format("%s %s", prefix, msg+c));
          throw new RuntimeException(msg+c);
        }
      } catch (NumberFormatException e) {
        // We parsed the header OK, but the values were not as expected.
        final String msg = "Unexpected or uninterpretable numbers in header: ";
        LOGGER.error(String.format("%s %s", prefix, msg+c));
        throw new RuntimeException(msg+c);
      }

    } while (index < the_line.size());

    LOGGER.debug(String.format("%s %s", prefix, "Successfully parsed headers: "+the_line));
  }

  /**
   * Parses and returns a positive integer that occurs in the whole string, between indicatorString
   * and endString. For example, if wholeString is "[contestname] (Vote For=3)", a call to
   * getIntegerAfter(wholestring, "VoteFor=", ")") will return 3.
   * @param wholeString The string from which to extract the value.
   * @param indicator The substring that indicates the value is next.
   * @param endString The string that should follow the value.
   * @return the positive integer between indicatorString and endString, or
   *         -1 if indicatorString is not present in wholeString.
   * @throws NumberFormatException if indicatorString appears in wholeString, but what follows
   *         between it and endString cannot be parsed as a positive integer.
   */
  private int extractPositiveInteger(String wholeString, String indicator, String endString)
      throws NumberFormatException {
    final String prefix = "[extractPositiveInteger] ";

    // If the indicator is not present, return -1.
    if(!wholeString.contains(indicator)) {
      return -1;
    }

    // The index in wholeString where we expect the integer to start.
    int intIndex = wholeString.indexOf(indicator)+indicator.length();
    String intString = wholeString.substring(intIndex, wholeString.indexOf(endString, intIndex));
    final int val = Integer.parseInt(intString.strip());
    if(val > 0) {
      return val;
    }

    String msg = "Could not parse header: ";
    LOGGER.error(String.format("%s %s", prefix, msg+wholeString));
    throw new NumberFormatException(String.format("%s %s", prefix, msg+wholeString));
  }

  /**
   * Create contest and result objects for use later in parsing.
   *
   * @param choiceLine      The CSV line containing the choice information.
   * @param explanationLine The CSV line containing the choice explanations.
   * @param contestNames    The list of contest names.
   * @param votesAllowed    The table of votes allowed values.
   * @param choiceCounts    The table of contest choice counts.
   * @param contestTypes    The table of contest types (PLURALITY or IRV)
   */
  private Result addContests(final CSVRecord choiceLine,
                             final CSVRecord explanationLine,
                             final List<String> contestNames,
                             final Map<String, Integer> votesAllowed,
                             final Map<String, Integer> choiceCounts,
                             final Map<String, ContestType> contestTypes) {
    final Result result = new Result();
    int index = my_first_contest_column;
    int contest_count = 0;

    for (final String contestName : contestNames) {
      final List<Choice> choices = new ArrayList<Choice>();
      final int end = index + choiceCounts.get(contestName);
      boolean isWriteIn = false;
      final boolean isIRV = contestTypes.get(contestName).equals(ContestType.IRV);

      try {
        if (isIRV) {
          // Ensure that the IRV choices have the expected form, iterating through all ranks and
          // all candidates for each rank.
          validateIRVPreferenceHeaders(choiceLine, index, choiceCounts.get(contestName),
              votesAllowed.get(contestName));
        }
        while (index < end) {
          String choice;
          if (isIRV) {
            // If it is IRV, ignore the rank in parentheses and extract the candidate name.
            choice = new IRVPreference(choiceLine.get(index)).candidateName;
          } else {
            // Plurality - just use the candidate/choice name as is.
            // Also for STV - it will just keep the names followed by rank.
            choice = choiceLine.get(index).strip();
          }
          final String explanation = explanationLine.get(index).trim();
          // "Write-in" is a fictitious candidate that denotes the beginning of
          // the list of qualified write-in candidates
          final boolean isFictitious = choice.toUpperCase().matches("WRITE[-_ \t]*IN");
          choices.add(new Choice(choice, explanation, isWriteIn, isFictitious));
          if (isFictitious) {
            // consider all subsequent choices in this contest to be qualified
            // write-in candidates
            isWriteIn = true;
          }
          index = index + 1;
        }

        // Winners allowed is always 1 for IRV, but is assumed to be equal to votesAllowed for
        // plurality, because the Dominion format doesn't give us that separately.
        // For STV, it doesn't matter because the contest will be dropped.
        final int winnersAllowed = isIRV ? 1 : votesAllowed.get(contestName);

        final Contest c = new Contest(contestName, my_county, contestTypes.get(contestName).toString(),
            choices, votesAllowed.get(contestName), winnersAllowed, contest_count);

        LOGGER.debug(String.format("[addContests: county=%s, contest=%s", my_county.name(), c));

        // If we've just finished a plurality or STV contest header, index is already at the right
        // place for the next contest.
        // If we've just finished the 1st-preference IRV choices, e.g. "Alice(1), Bob(1), Chuan(1)",
        // index will now be pointed at the beginning of the IRV 2nd preferences, e.g.
        // "Alice(2), Bob(2), Chuan(2)", so we have to advance it to the next contest. There is a
        // complete list of choices for each of the ranks allowed other than 1, so add
        // (number of choices)*(ranks allowed -1).
        index = index + (isIRV ? choices.size() * (votesAllowed.get(contestName) - 1) : 0);
        contest_count = contest_count + 1;
        try {
          // Don't make a CountyContestResult for, and don't persist, an STV contest.
          if(votesAllowed.get(contestName) != STV_NO_VOTES) {
            Persistence.saveOrUpdate(c);
            final CountyContestResult r = CountyContestResultQueries.matching(my_county, c);
            my_results.add(r);
          }
          my_contests.add(c);
        } catch (PersistenceException pe) {
          result.success = false;
          result.errorMessage = StringUtils.abbreviate(DBExceptionUtil.getConstraintFailureReason(pe), 250);
          result.errorRowContent = StringUtils.abbreviate("Error adding " + c.shortToString(), 250);
          return result;
        }
      } catch (IRVParsingException e) {
        result.success = false;
        result.errorMessage = e.getMessage();
        result.errorRowContent = "Error doing IRV parsing for "+choiceLine.get(index);
        return result;
      }
    }
    result.success = true ;
    return result ;
  }

  /**
   * Checks to see if the set of parsed CVRs needs flushing, and does so
   * if necessary.
   */
  private void checkForFlush() {
    if (my_multi_transaction && my_record_count % my_transaction_size == 0) {
      commitCVRsAndUpdateCountyDashboard();
    }

    if (my_record_count % my_batch_size == 0) {
      Persistence.flush();
      for (final CastVoteRecord cvr : my_parsed_cvrs) {
        Persistence.evict(cvr);
      }
      my_parsed_cvrs.clear();
    }
  }

  /**
   * Commits the currently outstanding CVRs and updates the county dashboard
   * accordingly.
   */
  private void commitCVRsAndUpdateCountyDashboard() {
    // commit all the CVR records and contest tracking data
    Persistence.commitTransaction();

    boolean success = false;
    int retries = 0;
    while (!success && retries < UPDATE_RETRIES) {
      try {
        retries = retries + 1;
        LOGGER.debug("updating county " + my_county.id() + " dashboard, attempt " +
                          retries);
        Persistence.beginTransaction();
        final CountyDashboard cdb =
            Persistence.getByID(my_county.id(), CountyDashboard.class);
        // if we can't get a reference to the county dashboard, we've got problems -
        // but we'll deal with them elsewhere
        if (cdb == null) {
          Persistence.rollbackTransaction();
        } else {
          cdb.setCVRsImported(my_record_count);
          Persistence.saveOrUpdate(cdb);
          Persistence.commitTransaction();
          success = true;
        }
      } catch (final PersistenceException e) {
        // something went wrong, let's try again
        if (Persistence.canTransactionRollback()) {
          try {
            Persistence.rollbackTransaction();
          } catch (final PersistenceException ex) {
            // not much we can do about it
          }
        }
        // let's give other transactions time to breathe
        try {
          final long delay =
              ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
          LOGGER.info("retrying county " + my_county.id() +
                           " dashboard update in " + delay + "ms");
          Thread.sleep(delay);
        } catch (final InterruptedException ex) {
          // it's OK to be interrupted
        }
      }
    }
    // we always need a running transaction
    Persistence.beginTransaction();
    if (success && retries > 1) {
      LOGGER.info("updated state machine for county " + my_county.id() +
                       " in " + retries + " tries");
    } else if (!success) {
      throw new PersistenceException("could not update state machine for county " +
                                     my_county.id() + " after " + retries + " tries");
    }
  }

  /**
   * Extract a CVR from a line of the file.
   *
   * @param the_line The line representing the CVR.
   * @return the resulting CVR.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private CastVoteRecord extractCVR(final CSVRecord the_line) {
    final int cvr_id =
      Integer.parseInt(
                       stripEqualQuotes(the_line.get(my_columns.get(CVR_NUMBER_HEADER))));
    final int tabulator_id =
      Integer.parseInt(
                       stripEqualQuotes(
                                        the_line.get(my_columns.get(TABULATOR_NUMBER_HEADER))));
    final String batch_id =
      stripEqualQuotes(the_line.get(my_columns.get(BATCH_ID_HEADER)));
    final int record_id =
      Integer.parseInt(
                       stripEqualQuotes(the_line.get(my_columns.get(RECORD_ID_HEADER))));
    final String imprinted_id =
      stripEqualQuotes(the_line.get(my_columns.get(IMPRINTED_ID_HEADER)));
    final String ballot_type =
      stripEqualQuotes(the_line.get(my_columns.get(BALLOT_TYPE_HEADER)));
    final List<CVRContestInfo> contest_info = new ArrayList<CVRContestInfo>();
    final String prefix = "[extractCVR]";

    // for each contest, see if choices exist on the CVR; "0" or "1" are
    // votes or absences of votes; "" means that the contest is not in this style
    int index = my_first_contest_column;
    for (final Contest co : my_contests) {
      boolean present = false;
      final List<String> votes = new ArrayList<String>();
      final boolean isIRV = co.description().equals(ContestType.IRV.toString());

      List<Choice> choices;
      if(isIRV) {
        choices = generateAllIRVPreferences(co.choices(), co.votesAllowed());
      } else {
        choices = co.choices();
      }

      for (final Choice ch : choices) {
        final String mark_string = the_line.get(index);
        final boolean p = !mark_string.isEmpty();
        final boolean mark = "1".equals(mark_string);
        present |= p;
        if (!ch.fictitious() && p && mark) {
          votes.add(ch.name());
        }
        index = index + 1;
      }
      // if this contest was on the ballot, add it to the votes
      try {
      if (present) {
        if(isIRV) {
            // If it is IRV, convert it into an ordered list of names (without parentheses), then
            // store.
            final IRVChoices irvVotes = new IRVChoices(votes);
            final List<String> orderedChoices = irvVotes.getValidIntentAsOrderedList();
            if(!irvVotes.isValid()) {
              // IRV preferences were invalid. Store a record of the raw votes for debugging/record-
              // keeping purposes, but use the valid interpretation as the choices in the audit.
              final IRVBallotInterpretation irvInterpretation = new IRVBallotInterpretation(co,
                  RecordType.UPLOADED, cvr_id, imprinted_id, votes, orderedChoices);
              Persistence.save(irvInterpretation);
              final String msg = "Interpretation of invalid IRV choices.";
              LOGGER.warn(String.format("%s %s %s.", prefix, msg,
                  irvInterpretation.logMessage(CVR_NUMBER_HEADER, IMPRINTED_ID_HEADER)));
            }
            contest_info.add(new CVRContestInfo(co, null, null, orderedChoices));

        } else if(co.votesAllowed() != STV_NO_VOTES) {
          // Don't store an STV contest (indicated by STV_NO_VOTES in votesAllowed).
          // Store plurality vote.
          contest_info.add(new CVRContestInfo(co, null, null, votes));
        }
      }
      } catch (IRVParsingException e) {
        throw new RuntimeException(e);
      }
    }

    // we don't need to look for an existing CVR with this data because,
    // by definition, there cannot be one unless the same line appears
    // twice in the CVR export file... and if it does, we need it to
    // appear twice here too.
    final CastVoteRecord new_cvr =
      new CastVoteRecord(RecordType.UPLOADED, null, my_county.id(),
                         cvr_id, my_record_count, tabulator_id,
                         batch_id, record_id, imprinted_id,
                         ballot_type, contest_info);
    Persistence.saveOrUpdate(new_cvr);
    my_parsed_cvrs.add(new_cvr);

    // add the CVR to all of our results
    for (final CountyContestResult r : my_results) {
      r.addCVR(new_cvr);
    }
    LOGGER.debug("parsed CVR: " + new_cvr);
    return new_cvr;
  }

  /**
   * Processes the headers from the specified CSV record. This includes checking
   * for the use of forbidden headers, and that all required headers are
   * present.
   *
   * @return true if the headers are OK, false otherwise; this method also
   * sets the error message if necessary.
   */
  @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDeeplyNestedIfStmts",
      "PMD.ModifiedCyclomaticComplexity", "PMD.CyclomaticComplexity",
      "PMD.StdCyclomaticComplexity", "PMD.NPathComplexity"})
  private Result processHeaders(final CSVRecord the_line) {
    final Result result = new Result();

    // the explanations line includes the column names for the non-contest/choice
    // columns, so let's get those
    for (int i = 0; i < my_first_contest_column; i++) {
      my_columns.put(the_line.get(i), i);
    }

    // let's make sure none of our prohibited headers are present
    final List<String> prohibited_headers = new ArrayList<>();
    for (final String h : PROHIBITED_HEADERS) {
      if (my_columns.get(h) != null) {
        result.success = false;
        prohibited_headers.add(h);
      }
    }

    // let's make sure no required headers are missing
    final Set<String> required_headers =
        new HashSet<>(Arrays.asList(REQUIRED_HEADERS));
    for (final String header : REQUIRED_HEADERS) {
      if (my_columns.get(header) != null) {
        required_headers.remove(header);
      }
    }

    result.success = prohibited_headers.isEmpty() && required_headers.isEmpty();

    if (!result.success) {
      final StringBuilder sb = new StringBuilder();
      sb.append("malformed CVR file: ");

      if (!prohibited_headers.isEmpty()) {
        sb.append("prohibited header");
        if (prohibited_headers.size() > 1) {
          sb.append('s');
        }
        sb.append(' ');
        sb.append(stringList(prohibited_headers));
        sb.append(" present");
        if (!required_headers.isEmpty()) {
          sb.append(", ");
        }
      }

      if (!required_headers.isEmpty()) {
        sb.append("required header");
        if (required_headers.size() > 1) {
          sb.append('s');
        }
        sb.append(' ');
        sb.append(stringList(required_headers));
        sb.append(" missing");
      }

      result.errorMessage = sb.toString();
      result.errorRowNum = Long.valueOf( the_line.getRecordNumber()).intValue();
      List<String> values = new ArrayList<>();
      the_line.iterator().forEachRemaining(values::add);
      result.errorRowContent = String.join(",", values);
    }

    return result;
  }

  /**
   * Makes a comma-separated string of the specified collection of
   * strings.
   *
   * @param the_strings The list.
   * @return the comma-separated string.
   */
  private String stringList(final Collection<String> the_strings) {
    final List<String> strings = new ArrayList<>(the_strings);
    final StringBuilder sb = new StringBuilder();

    Collections.sort(strings);
    sb.append(strings.get(0));
    for (int i = 1; i < strings.size(); i++) {
      sb.append(", ");
      sb.append(strings.get(i));
    }

    return sb.toString();
  }

  /**
   * Parse the supplied data export. If it has already been parsed, this
   * method returns immediately.
   *
   * @return true if the parse was successful, false otherwise
   */
  public Result parse() {
    final Result result = new Result();

    LOGGER.info("parsing CVR export for county " + my_county.id() +
                ", batch_size=" + my_batch_size +
                ", transaction_size=" + my_transaction_size);

    final Iterator<CSVRecord> records = my_parser.iterator();

    my_record_count = 0;


    // 1) we expect the first line to be the election name, which we currently discard
    final CSVRecord electionName;

    // 2) for the second line, we count the number of empty strings to find the first
    // contest/choice column
    final CSVRecord contest_line;

    // 3) we expect the third line to be a list of contest choices
    final CSVRecord choice_line;

    // 4) a list of explanations of those choices (such as party affiliations)
    final CSVRecord expl_line;

    // the combination of line 2-5
    final Result headerResult;

    // tracker for errors
    int lineNum = 1;

    try {
      // electionName
      records.next();
      lineNum++;
      contest_line = records.next();
      lineNum++;
      choice_line = records.next();
      lineNum++;
      expl_line = records.next();
    } catch (final Exception e) {
      LOGGER.error(e.getClass());
      LOGGER.error(e.getMessage());
      result.success = false;
      result.errorMessage = "Not a valid CSV";
      result.errorRowNum = lineNum;
      result.errorRowContent = "? (could not parse)";
      return result;
    }

    my_first_contest_column = 0;
    while ("".equals(contest_line.get(my_first_contest_column))) {
      my_first_contest_column = my_first_contest_column + 1;
    }
    // find all the contest names, how many choices each has,
    // and how many choices can be made in each
    final List<String> contest_names = new ArrayList<String>();
    final Map<String, Integer> contest_votes_allowed = new HashMap<String, Integer>();
    final Map<String, Integer> contest_choice_counts = new HashMap<String, Integer>();
    final Map<String, ContestType> contest_types = new HashMap<>();

    // we expect the second line to be a list of contest names, each appearing once
    // for each choice in the contest

    updateContestStructures(contest_line, contest_names, contest_votes_allowed,
                            contest_choice_counts, contest_types);


    headerResult = processHeaders(expl_line);

    if (headerResult.success == false) {
      return headerResult;
    } else {
      Result addContestResult = addContests(choice_line, expl_line, contest_names,
          contest_votes_allowed, contest_choice_counts, contest_types);
      if (!addContestResult.success) {
        return addContestResult ;
      }
        

      // subsequent lines contain cast vote records
      while (records.hasNext()) {
        final CSVRecord cvr_line = records.next();
        try {
          extractCVR(cvr_line);
        } catch (final Exception e) {
          LOGGER.error(e.getClass());
          LOGGER.error(e.getMessage());
          result.success = false;
          // we don't know what went wrong
          result.errorMessage = e.getClass().toString() + " - "+ e.getMessage();
          result.errorRowNum = Long.valueOf( cvr_line.getRecordNumber()).intValue();
          List<String> values = new ArrayList<>();
          cvr_line.iterator().forEachRemaining(values::add);
          result.errorRowContent = String.join(",", values);
          // get out of here now! break and return
          return result;
        }

        my_record_count = my_record_count + 1;
        if (my_record_count % PROGRESS_INTERVAL == 0) {
          LOGGER.info("parsed " + my_record_count +
                      " CVRs for county " + my_county.id());
        }
        checkForFlush();
      }

      for (final CountyContestResult r : my_results) {
        r.updateResults();
        Persistence.saveOrUpdate(r);
      }

      // commit any uncommitted records

      commitCVRsAndUpdateCountyDashboard();
    }

    result.success = true; // we made it through, yay!
    result.importedCount = my_record_count;

    return result;
  }
}
