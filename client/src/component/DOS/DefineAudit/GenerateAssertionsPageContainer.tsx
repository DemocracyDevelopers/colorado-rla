import * as React from 'react';

import { Redirect } from 'react-router-dom';

import { History } from 'history';

import generateAssertions from 'corla/action/dos/generateAssertions';

import GenerateAssertionsPage from './GenerateAssertionsPage';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';
import * as _ from 'lodash';

interface ContainerProps {
    canonicalizationComplete: boolean;
    dosState: DOS.AppState;
    history: History;
    readyToGenerate: boolean;
}

class GenerateAssertionsPageContainer extends React.Component<ContainerProps> {

    public render() {
        const {
            canonicalizationComplete,
            dosState,
            history,
            readyToGenerate,
        } = this.props;

        if (!dosState) {
            return <div />;
        }

        if (!dosState.asm) {
            return <div />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const generate = async () => {
            generateAssertions().then()
                .catch(reason => {
                    alert('generateAssertions error in fetchAction ' + reason);
                });
        };

        const props = {
            canonicalizationComplete,
            forward: () => history.push('/sos/audit/select-contests'),
            generate,
            readyToGenerate,
        };

        return <GenerateAssertionsPage { ...props } />;
    }
}

const mapStateToProps = (dosState: DOS.AppState) => {
    if (!dosState) { return {}; }

    const canonicalContests = dosState.canonicalContests;
    const canonicalChoices = dosState.canonicalChoices;

    const canonicalizationComplete = !_.isEmpty(canonicalChoices)
        && !dosState.settingAuditInfo && !_.isEmpty(canonicalContests);
    const readyToGenerate = canonicalizationComplete && !dosState.generatingAssertions
        && !dosState.assertionsGenerated;

    return {
        canonicalizationComplete,
        dosState,
        readyToGenerate,
    };
};

export default withSync(
    withDOSState(GenerateAssertionsPageContainer),
    'DOS_GENERATE_ASSERTIONS_SYNC',
    mapStateToProps,
);
