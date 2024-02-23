package au.org.democracydevelopers.model;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringListConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The version (for optimistic locking).
     */
    @Version
    private Long version;

    /**
     * ID of the CVR being audited, and whose paper ballot is being interpreted.
     */
    @Column(name = "cvr_id")
    private Long cvrID;

    /**
     * Name of the contest to which the interpreted vote belongs.
     */
    @Column(name = "contest_name")
    private String contestName;

    /**
     * List of candidate names with ranks in parentheses representing a raw vote in an IRV contest as entered
     * by auditors.
     */
    @Column(name = "raw_choices", columnDefinition = "character varying (1024)")
    @Convert(converter = StringListConverter.class)
    private List<String> rawChoices = new ArrayList<>();

    /**
     * List of candidates names, in order of preference, representing a valid vote in an IRV contest.
     */
    @Column(name = "valid_choices", columnDefinition = "character varying (1024)")
    @Convert(converter = StringListConverter.class)
    private List<String> validChoices = new ArrayList<>();

    /**
     * Construct an empty IRVBallotInterpretation (for persistence).
     */
    public IRVBallotInterpretation(){}

    /**
     * Create a record of an IRV vote interpretation for a given contest and a given ballot (identified by the
     * CVR ID).
     * @param theCVRID          Ballot identifier
     * @param theContestName    Name of the contest whose vote is being interpreted.
     * @param theRawChoices     Raw choices on the ballot for the contest, as identified by an auditor.
     * @param theValidChoices   Valid interpretation of the vote on the ballot.
     */
    public IRVBallotInterpretation(final Long theCVRID, final String theContestName, final List<String> theRawChoices,
                                   final List<String> theValidChoices){
        cvrID = theCVRID;
        contestName = theContestName;
        rawChoices = theRawChoices;
        validChoices = theValidChoices;
    }


    public static IRVChoices InterpretValidIntent(final IRVChoices b) {
        IRVChoices i3 = b.ApplyRule3();
        IRVChoices i1 = i3.ApplyRule1();
        return i1.ApplyRule2();
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