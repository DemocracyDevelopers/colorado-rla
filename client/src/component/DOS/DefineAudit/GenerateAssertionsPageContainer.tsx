import * as React from 'react';

import { Redirect } from 'react-router-dom';

import { History } from 'history';

import generateAssertions from 'corla/action/dos/generateAssertions';

import resetAudit from 'corla/action/dos/resetAudit';
import GenerateAssertionsPage from './GenerateAssertionsPage';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';
import * as _ from 'lodash';

// The next URL path to transition to.
const NEXT_PATH = '/sos/audit/select-contests';

interface GenerateAssertionsProps {
    asm: DOS.ASMState;
    canonicalizationComplete: boolean;
    readyToGenerate: boolean;
    history: History;
}

const GenerateAssertionsPageContainer = (props: GenerateAssertionsProps) => {
    const {
        canonicalizationComplete,
        readyToGenerate,
        asm,
        history,
    } = props;

    const generate = () => {
        generateAssertions().then(r => {
            if (!r) {
                alert('Assertions could not be generated');
            }
        }).catch(reason => {
                alert('generateAssertions error in fetchAction ' + reason);
        });

    };

    const nextPage = () => {
        history.push(NEXT_PATH);
    };

    if (asm === 'DOS_AUDIT_ONGOING') {
        return <Redirect to='/sos'/>;
    }

    return <GenerateAssertionsPage canonicalizationComplete={canonicalizationComplete}
                                   readyToGenerate={readyToGenerate}
                                   forward={ nextPage }
                                   generate={ generate } />;

};

const mapStateToProps = (state: DOS.AppState) => {
    const canonicalContests = state.canonicalContests;
    const canonicalChoices = state.canonicalChoices;

    const canonicalizationComplete = !_.isEmpty(canonicalChoices)
        && !state.settingAuditInfo && !_.isEmpty(canonicalContests);
    const readyToGenerate = canonicalizationComplete && !state.generatingAssertions
        && !state.assertionsGenerated;

    return {
        asm: state.asm,
        canonicalizationComplete,
        readyToGenerate,
    };
};

export default withPoll(
    withDOSState(GenerateAssertionsPageContainer),
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    mapStateToProps,
);
