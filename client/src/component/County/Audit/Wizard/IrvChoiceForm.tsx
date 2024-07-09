import * as _ from 'lodash';
import * as React from 'react';

import {Checkbox} from '@blueprintjs/core';

interface IrvChoiceFormRanking {
    name: string;
    rank: number;
    value: string;
    label: string;
}

const IrvChoiceForm = (props: ChoicesProps) => {
    const {
        choices,
        marks,
        noConsensus,
        updateBallotMarks,
        description,
    } = props;

    const numberOfRankChoices = choices.length;
    const noRankSuffix: string = '(none)';
    const noRankRanking: number = -1;

    function getNoRankButton(candidateName: string) {
        const noRankValue = `${candidateName}${noRankSuffix}`;
        const noRank: IrvChoiceFormRanking = {label: 'No Rank', name: candidateName, rank: noRankRanking,
            value: noRankValue};

        const checked = marks.choices[noRankValue];

        return (
            <div className='contest-choice-selection'>
                <Checkbox
                    className='rla-contest-choice'
                    key={noRank.value}
                    value={noRank.value}
                    disabled={noConsensus}
                    checked={checked || false}
                    onChange={updateChoiceByRanking(noRank)}>
                    <span className='choice-name'>{noRank.label}</span>
                </Checkbox>
            </div>
        );
    }

    function updateChoiceByRanking(irvChoice: IrvChoiceFormRanking) {
        function updateChoice(e: React.ChangeEvent<HTMLInputElement>) {
            const checkbox = e.target;

            // Special conditions exist for 'No Rank'
            if (checkbox.checked) {
                handleNoRankButton(irvChoice);
            }

            updateBallotMarks({ choices: { [irvChoice.value]: checkbox.checked } });
        }

        // tslint:disable-next-line:no-console
        console.log(marks);

        return updateChoice;
    }

    function handleNoRankButton(irvChoice: IrvChoiceFormRanking) {
        // 'No Rank' Checked
        if (irvChoice.rank === noRankRanking) {
            // Uncheck any other rankings in that candidate's row
            for (let i = 0; i < numberOfRankChoices; i++) {
                marks.choices[irvChoice.name + `(${i + 1})`] = false;
            }
        // Another ranking checked
        } else if (marks.choices[irvChoice.name + noRankSuffix]) {
            // Uncheck 'No Rank' for that row if it was previously checked
            marks.choices[irvChoice.name + noRankSuffix] = false;
        }
    }

    function getRankings(candidateName: string, candidateDescription: string) {
        const rankingOptions = [];

        rankingOptions.push(
            <div className='rla-contest-choice-name'>
                <b>{candidateName}</b>
                {candidateDescription ? <br /> : null}
                {candidateDescription}
            </div>,
        );

        for (let i = 0; i < numberOfRankChoices; i++) {
            const rank = i + 1;
            const ranking: IrvChoiceFormRanking = {label: (rank).toString(), name: candidateName, rank,
                value: candidateName + `(${rank})`};
            const checked = marks.choices[ranking.value];

            rankingOptions.push(
                <div className='contest-choice-selection'>
                    <Checkbox
                        className='rla-contest-choice'
                        key={ranking.value}
                        value={ranking.value}
                        disabled={noConsensus}
                        checked={checked || false}
                        onChange={updateChoiceByRanking(ranking)}>
                        <span className='choice-name'>{ranking.label}</span>
                    </Checkbox>
                </div>,
            );
        }

        rankingOptions.push(getNoRankButton(candidateName));

        return rankingOptions;
    }

    return (
        <div className='irv-contest-choice-grid'
             style={{gridTemplateColumns: `repeat(${numberOfRankChoices + 2}, 1fr)`}}>
            <div style={{borderRight: '1px solid #ddd'}}>
                Candidate
            </div>
            <div style={{gridColumn: `2 / ${numberOfRankChoices + 3}`}}>
                Ranked Vote Choice
            </div>
            {_.map(choices, (choice, index) => {
                return (
                    getRankings(choice.name, choice.description)
                );
            })}
        </div>
    );
};

export default IrvChoiceForm;
