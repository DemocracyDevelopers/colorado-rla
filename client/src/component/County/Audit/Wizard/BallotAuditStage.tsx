import * as React from 'react';

import * as _ from 'lodash';

import { Button, Checkbox, Dialog, EditableText, Intent } from '@blueprintjs/core';

import BackButton from './BackButton';
import WaitingForNextBallot from './WaitingForNextBallot';

import CommentIcon from '../../../CommentIcon';

import ballotNotFound from 'corla/action/county/ballotNotFound';
import IrvChoiceForm from 'corla/component/County/Audit/Wizard/IrvChoiceForm';

interface NotFoundProps {
    notFound: OnClick;
    currentBallot: CVR;
}

const BallotNotFoundForm = (props: NotFoundProps) => {

    const { notFound, currentBallot } = props;

    const onClick = () => {
        if (confirm('By continuing, this ballot will be recorded as “not found”'
            + ' and you will move on to the next ballot.')) {
            notFound();
        }
    };

    return (

        <div>

            <div className='not-found-header'>Are you looking at the right ballot?</div>
            <div className='not-found-copy'>
                Before making any selections below, first make sure the paper ballot you are examining
                matches the current ballot information displayed on the left. If you make selections based
                on the wrong ballot, you may have to audit more ballots later.
            </div>
            <button className='pt-button pt-large pt-intent-danger' onClick={onClick} >
                Ballot not found - move to next ballot
            </button>
        </div>
    );
};

interface AuditInstructionsProps {
    countyState: County.AppState;
    currentBallot: CVR;
}

const AuditInstructions = (props: AuditInstructionsProps) => {
    const {
        countyState,
        currentBallot,
    } = props;

    const isCurrentCvr = (cvr: JSON.CVR) => cvr.db_id === currentBallot.id;
    const fullCvr = _.find(countyState.cvrsToAudit, isCurrentCvr);
    const storageBin = fullCvr ? fullCvr.storage_location : '—';

    return (
        <div>
            <div className='current-ballot-info'>
                <h2 className='sidebar-heading'>Current ballot:</h2>
                <ul className='current-ballot-stats pt-list-unstyled'>
                    <li>
                        <span className='current-ballot-stats-label'>Storage bin</span>
                        <span className='current-ballot-stats-value'>{storageBin}</span>
                    </li>
                    <li>
                        <span className='current-ballot-stats-label'>Tabulator</span>
                        <span className='current-ballot-stats-value'>{currentBallot.scannerId}</span>
                    </li>
                    <li>
                        <span className='current-ballot-stats-label'>Batch</span>
                        <span className='current-ballot-stats-value'>{currentBallot.batchId}</span>
                    </li>
                    <li>
                        <span className='current-ballot-stats-label'>Ballot position</span>
                        <span className='current-ballot-stats-value'>{currentBallot.recordId}</span>
                    </li>
                    <li>
                        <span className='current-ballot-stats-label'>Ballot type</span>
                        <span className='current-ballot-stats-value'>{currentBallot.ballotType}</span>
                    </li>
                </ul>
            </div>
        </div>
    );
};

interface ContestInfoProps {
    contest: Contest;
}

const ContestInfo = ({ contest }: ContestInfoProps) => {
    const { name, description, choices } = contest;

    return (
        <div className='contest-info'>
            <div className='contest-name'>{name}</div>
            <div>{description}</div>
        </div>
    );
};

const ContestChoices = (props: ChoicesProps) => {
    const {
        choices,
        marks,
        noConsensus,
        updateBallotMarks,
        description,
    } = props;

    function updateChoiceByName(name: string) {
        function updateChoice(e: React.ChangeEvent<HTMLInputElement>) {
            const checkbox = e.target;

            updateBallotMarks({ choices: { [name]: checkbox.checked } });
        }

        return updateChoice;
    }

    const pluralityChoiceForms = _.map(choices, choice => {
        const checked = marks.choices[choice.name];

        return (
            <div className='contest-choice-selection'>
                <Checkbox
                    className='rla-contest-choice'
                    key={choice.name}
                    disabled={noConsensus}
                    checked={checked || false}
                    onChange={updateChoiceByName(choice.name)} >
                    <span className='choice-name'>{choice.name}</span></Checkbox>
            </div>
        );
    });

    function isPlurality() {
        return description === 'PLURALITY';
    }

    return (
        <div className={isPlurality() ? 'plurality-contest-choice-grid' : ''}>
            {isPlurality() ? pluralityChoiceForms : IrvChoiceForm(props)}
        </div>
    );
};

interface CommentsProps {
    comments: string;
    onChange: OnClick;
}

const ContestComments = (props: CommentsProps) => {
    const { comments, onChange } = props;

    return (
        <div className='comment-box'>
            <label className='comment-box-label'>
                <CommentIcon />
                <EditableText className='comment-field'
                    multiline
                    onChange={onChange}
                    placeholder='Add comment'
                    value={comments || ''} />
            </label>
        </div>
    );
};

interface MarkFormProps {
    contest: Contest;
    countyState: County.AppState;
    currentBallot: CVR;
    updateBallotMarks: OnClick;
}

const BallotContestMarkForm = (props: MarkFormProps) => {
    const { contest, countyState, currentBallot, updateBallotMarks } = props;
    const { name, description, choices } = contest;

    const acvr = countyState.acvrs![currentBallot.id];
    const contestMarks = acvr[contest.id];

    const updateComments = (comments: string) => {
        updateBallotMarks({ comments });
    };

    const updateConsensus = (e: React.ChangeEvent<any>) => {
        const isChecked: boolean = !!e.target.checked;

        // If unchecked, explicit permission granted
        let hasPermission: boolean = !isChecked;

        if (isChecked) {
            hasPermission = confirm(
                'By continuing, you acknowledge that the audit board could not reach consensus on an interpretation'
                + ' of voter intent for this ballot.',
            );
        }

        if (hasPermission) {
            updateBallotMarks({ noConsensus: isChecked });
        }
    };

    const updateNoMark = (e: React.ChangeEvent<any>) => {
        updateBallotMarks({ noMark: !!e.target.checked });
    };

    return (
        <div className='contest-row'>
            <ContestInfo contest={contest} />
            <ContestChoices
                key={contest.id}
                choices={choices}
                marks={contestMarks}
                noConsensus={!!contestMarks.noConsensus}
                updateBallotMarks={updateBallotMarks}
                description={description}
            />

            <div className='contest-choice-grid'>
                <div className='contest-choice-selection'>
                    <Checkbox
                        checked={!!contestMarks.noConsensus}
                        onChange={updateConsensus}>
                        <span className='choice-name no-choice'>No audit board consensus</span></Checkbox>
                </div>

                <div className='contest-choice-selection'>
                    <Checkbox
                        checked={!!contestMarks.noMark}
                        onChange={updateNoMark}>
                        <span className='choice-name no-choice'>Blank vote - no mark</span></Checkbox>
                </div>
            </div>

            <ContestComments comments={contestMarks.comments} onChange={updateComments} />
        </div>
    );
};

interface AuditFormProps {
    countyState: County.AppState;
    currentBallot: CVR;
    updateBallotMarks: OnClick;
}

const BallotAuditForm = (props: AuditFormProps) => {
    const { countyState, currentBallot } = props;

    const contestForms = _.map(currentBallot.contestInfo, info => {
        const contest = countyState.contestDefs![info.contest];

        const updateBallotMarks = (data: any) => props.updateBallotMarks({
            ballotId: currentBallot.id,
            contestId: contest.id,
            ...data,
        });

        return (
            <BallotContestMarkForm
                key={contest.id}
                contest={contest}
                countyState={countyState}
                currentBallot={currentBallot}
                updateBallotMarks={updateBallotMarks} />
        );
    });

    return <div>{contestForms}</div>;
};

interface StageProps {
    auditBoardIndex: number;
    comment?: string;
    countyState: County.AppState;
    currentBallot?: County.CurrentBallot;
    currentBallotNumber?: number;
    isReAuditing: boolean;
    nextStage: OnClick;
    prevStage: OnClick;
    totalBallotsForBoard?: number;
    updateBallotMarks: (data: any) => any;
}

interface BallotAuditStageState {
    showDialog: boolean;
}

class BallotAuditStage extends React.Component<StageProps, BallotAuditStageState> {

    constructor(props: StageProps) {
        super(props);
        this.state = { showDialog: true };
    }

    private closeDialog = () => {
        this.setState({ showDialog: !this.state.showDialog });
     }

       public render() {

        const {
            auditBoardIndex,
            comment,
            countyState,
            currentBallot,
            currentBallotNumber,
            isReAuditing,
            nextStage,
            prevStage,
            totalBallotsForBoard,
            updateBallotMarks,
          } = this.props;

        if (currentBallot == null) {
            return <WaitingForNextBallot />;
        }

        if (currentBallotNumber == null) {
            return <WaitingForNextBallot />;
        }

        const notFound = () => {
            if (isReAuditing) {
                ballotNotFound(currentBallot.id, auditBoardIndex, true, comment);
            } else {
                ballotNotFound(currentBallot.id, auditBoardIndex);
            }
        };

        const validateAcvr = () => {
            const acvr = countyState.acvrs![currentBallot.id];

            const validateContest = (contest: any) => {
                return (_.size(contest.choices) > 0 && _.some(contest.choices))
                    || contest.noConsensus
                    || contest.noMark;
            };

            return _.every(acvr, validateContest);
        };

        const validatingAcvr = (handler: OnClick) => {
            return (e: any) => {
                if (!validateAcvr()) {
                    alert('You must record an interpretation of voter intent for each contest.'
                        + ' Double-check that all contests have an option selected.');

                    return false;
                }

                return handler(e);
            };
        };

        const { currentRound } = countyState;

        if (currentBallot.submitted) {
            return <WaitingForNextBallot />;
        }

        return (
            <div className='rla-page'>
                <div>
                    <Dialog
                        isOpen={this.state.showDialog}
                    >
                        <div className='pt-dialog-body'>
                            <p>
                                Tabulator: {currentBallot.scannerId}<br/>
                                Batch: {currentBallot.batchId}<br/>
                                Ballot position: {currentBallot.recordId}<br/>
                                Ballot type: {currentBallot.ballotType}<br/><br/>
                           Please confirm that the ballot you’re examining matches the ballot information displayed on the screen.
                           </p>                         </div>
                        <div className='pt-dialog-footer'>
                            <div className='pt-dialog-footer-actions'>
                                <Button intent={Intent.PRIMARY}
                                    onClick={() => this.closeDialog()}
                                    text='Continue' />
                            </div>
                        </div>
                    </Dialog>
                </div>

                <div className='audit-page-container'>
                    <div className='audit-page-header'>
                        <h1 className='audit-page-title'>
                            Audit Board {`${auditBoardIndex + 1}`}: Ballot Card Verification
                        </h1>
                        <div className='audit-page-subtitle'>Enter ballot information</div>
                        <div className='ballot-number'>
                            Auditing ballot card {currentBallotNumber} of {totalBallotsForBoard}
                        </div>
                    </div>
                    <div className='col-layout row1'>
                        <div className='col1'>
                            <AuditInstructions
                                countyState={countyState}
                                currentBallot={currentBallot}
                            />
                        </div>
                        <div className='col2'>
                            <BallotNotFoundForm
                                notFound={notFound}
                                currentBallot={currentBallot}
                            />
                        </div>
                    </div>

                    <div className='col-layout'>
                        <div className='col1'>
                            <div className='sidebar-instructions'>
                                <h3 className='sidebar-heading'>How to match selections with ballot</h3>

                                <div className='sidebar-accordion'>
                                    <input type='checkbox' id='item1' name='accordion' className='accordion-item' />
                                    <label htmlFor='item1' className='accordion-item-label'>Overvote</label>
                                    <div id='content1' className='accordion-item-content'>
                                        <p>
                                            Even if you think that the voter voted for too many
                                            choices, select all of the choices exactly as they
                                            appear on the ballot in the RLA Software.
                                            If there is an overvote with an uncertified write-in,
                                            select the “Blank vote – no mark” button;
                                            the software will not let you select any other choices.
                                            If you need more specific guidance, consult the Voter Intent Guide.
                                        </p>
                                    </div>

                                    <input type='checkbox' id='item2' name='accordion' className='accordion-item' />
                                    <label htmlFor='item2' className='accordion-item-label'>Undervote</label>
                                    <div id='content2' className='accordion-item-content'>
                                        <p>
                                            Even if you think that the voter did not select enough
                                            choices, select all of the choices exactly as they appear
                                            on the ballot. If the voter did not select any choice, mark
                                            the “Blank vote – no mark” button. If you need more specific
                                            guidance, consult the Voter Intent Guide.
                                        </p>
                                    </div>

                                    <input type='checkbox' id='item3' name='accordion' className='accordion-item' />
                                    <label htmlFor='item3' className='accordion-item-label'>Blank vote</label>
                                    <div id='content3' className='accordion-item-content'>
                                        <p>
                                            If you examine a paper ballot and determine
                                            the voter did not intend to vote for any choice in a ballot contest,
                                            select “Blank vote – no mark”
                                        </p>
                                    </div>

                                    <input type='checkbox' id='item4' name='accordion' className='accordion-item' />
                                    <label htmlFor='item4' className='accordion-item-label'>Write-in</label>
                                    <div id='content4' className='accordion-item-content'>
                                        <p>
                                            If the voter wrote in a candidate on the “Write-in” line,
                                            select the candidate the voter wrote in in the RLA software.
                                            If the candidate does not appear in the RLA software,
                                            select the “Blank vote – no mark” button and add a comment with what
                                            is written on the Write-in line. If there is an overvote with an uncertified
                                            write-in, select the “Blank vote – no mark” button; the software will not let
                                            you select any other choices. If you need more specific guidance, consult the
                                            Voter Intent Guide.
                                        </p>
                                    </div>

                                    <input type='checkbox' id='item5' name='accordion' className='accordion-item' />
                                    <label htmlFor='item5' className='accordion-item-label'>We can't agree</label>
                                    <div id='content5' className='accordion-item-content'>
                                        <p>
                                            If the members of the audit board can’t agree on what the voter intended
                                            when they marked their ballot, select the “No consensus” button for that
                                            contest. Before moving on, take a break and then try again to reach a consensus
                                            using the Voter Intent Guide. If you select “No consensus” you may have to audit
                                            more ballots later.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className='col2'>
                            <div className='main-col-instructions'>
                                <div className='not-found-header'>For each ballot contest:</div>
                                <p>
                                    Select exactly the same voting choices as the voter marked on the paper ballot you are
                                    examining.
                                </p>
                                <p>
                                    Example 1: If the voter marked three candidates on their ballot in this contest, select
                                    the exact same three candidates below.
                                </p>
                                <p>
                                    Example 2: If the voter did not vote for any of the candidates or choices in this
                                    contest, select “Blank vote – no mark”
                                </p>
                            </div>
                            <BallotAuditForm
                                countyState={countyState}
                                currentBallot={currentBallot}
                                updateBallotMarks={updateBallotMarks} />
                        </div>
                    </div>
                    <div className='button-container'>
                        <BackButton back={prevStage} />

                        <Button className='ml-default blackButtonText'
                            intent={Intent.SUCCESS}
                            large
                            onClick={validatingAcvr(nextStage)}>
                            Review
                        </Button>
                    </div>
                </div>
            </div>
        );
    }
};

export default BallotAuditStage;
