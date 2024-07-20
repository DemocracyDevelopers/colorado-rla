package au.org.democracydevelopers.corla.model.vote;

import org.apache.commons.lang.StringEscapeUtils;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringListConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A record of the interpretation of invalid IRV ballots. Used for reporting, not for the actual
 * auditing logic. Every time an invalid IRV vote is encountered, whether in the initial upload or
 * during the auditing steps, a record is made of the CVR ID, the county, contest name, raw vote,
 * and our interpretation of that vote as an ordered list of candidate names.
 * This is output as a report called IRVBallotInterpretationReport.
 */
@Entity
@Table(name = "irv_ballot_interpretation")
public class IRVBallotInterpretation implements PersistentEntity, Serializable {

  /**
   * ID for interpretation record (for persistence).
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long version;


  /**
   * The contest to which the interpreted vote belongs.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Contest contest;

  /**
   * The record type, either UPLOADED or AUDITOR_ENTERED.
   */
  @Column(name = "record_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private CastVoteRecord.RecordType recordType;

  /**
   * ID of the CVR being audited, and whose paper ballot is being interpreted.
   * There may be multiple records with the same CVR ID, if one ballot contains multiple IRV contests.
   */
  @Column(name = "cvr_id", nullable = false)
  private int cvrID;

  /**
   * The imprinted ID, generally tabulator_id-batch_id-record_id.
   */
  @Column(name = "imprinted_id", nullable = false)
  @Convert(converter = StringListConverter.class)
  private String imprintedID;

  /**
   * List of candidate names with ranks in parentheses representing a raw vote in an IRV contest.
   * Order is not important.
   */
  @Column(name = "raw_choices", columnDefinition = "character varying (1024)", nullable = false)
  @Convert(converter = StringListConverter.class)
  private List<String> rawChoices = new ArrayList<>();

  /**
   * List of candidates names, in order of rank, representing a valid vote in an IRV contest.
   */
  @Column(name = "valid_choices", columnDefinition = "character varying (1024)", nullable = false)
  @Convert(converter = StringListConverter.class)
  private List<String> validChoices = new ArrayList<>();


  // TODO Actually these headers are all in DominionCVRExportParser. Try to put them in the same place.
  public static String invalidIRVTitle = "Invalid IRV choices.";
  private static String countyHeader = "County";
  private static String contestHeader = "Contest";
  private static String cvrIDHeader = "CVR ID";
  private static String imprintedIDHeader = "Imprinted ID";
  private static String rawChoicesHeader = "Raw choices";
  private static String validChoicesHeader = "Interpreted as";
  public static final List<String> csvHeaders = List.of(countyHeader, contestHeader,
      cvrIDHeader, rawChoicesHeader, validChoicesHeader);

  /**
   * Construct an empty IRVBallotInterpretation (for persistence).
   */
  public IRVBallotInterpretation() {
  }

  /**
   * Create a record of an IRV vote interpretation for a given contest and a given ballot
   * (identified by the CVR ID).
   * @param contest        the Contest
   * @param recordType     the type, expected to be either UPLOADED, AUDITOR_ENTERED, or REAUDITED.
   * @param cvrId          the cvrId.
   * @param imprintedId    the imprinted ID, i.e. tabulator_id-batch_id-record_id.
   * @param rawChoices     the (invalid) raw IRV choices, e.g. [Bob(1),Alice(3),Chuan(4)].
   * @param orderedChoices the way colorado-rla interpreted the raw choices, as an order list of names.
   */
  public IRVBallotInterpretation(Contest contest, CastVoteRecord.RecordType recordType, int cvrId, String imprintedId, List<String> rawChoices, List<String> orderedChoices) {
    this.contest = contest;
    this.recordType = recordType;
    this.cvrID = cvrId;
    this.imprintedID = imprintedId;
    this.rawChoices = rawChoices;
    this.validChoices = orderedChoices;
  }

  /**
   * Output contents as a CSV row, CSV-escaped and comma-delimited, in the same order as csvHeaders.
   * @return the data as a CSV row.
   */
  public String getCSVRow() {
    return String.join(",", (Stream.of(
        contest.county().name(),
        contest.name(),
        String.valueOf(cvrID),
        imprintedID,
        String.join(",", rawChoices),
        "[" + String.join(",", validChoices) + "]"
    ).map(StringEscapeUtils::escapeCsv).toList()));
  }

  /**
   * Output contents as a String appropriate for a log message.
   * @return the data with headers incorporated.
   */
  public String logMessage() {
    return String.join(",", (List.of(
        countyHeader + " " + contest.county().name(),
        contestHeader + " " + contest.name(),
        cvrIDHeader + " " + cvrID,
        imprintedIDHeader + " " + imprintedID,
        rawChoicesHeader + " " + String.join(",", rawChoices),
        validChoicesHeader + " [" + String.join(",", validChoices) + "]"
    ))) + ".";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(Long the_id) {
    id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return version;
  }
}