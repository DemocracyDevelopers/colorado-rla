import counties from 'corla/data/counties';
import * as _ from 'lodash';
import * as React from 'react';

import { Breadcrumb, Button, Card, Intent, Spinner } from '@blueprintjs/core';

import exportAssertionsAsCsv from 'corla/action/dos/exportAssertionsAsCsv';
import exportAssertionsAsJson from 'corla/action/dos/exportAssertionsAsJson';
import generateAssertions from 'corla/action/dos/generateAssertions';
import DOSLayout from 'corla/component/DOSLayout';
import AssertionStatus = DOS.AssertionStatus;
import GenerateAssertionsSummary = DOS.GenerateAssertionsSummary;

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb href='/sos' text='SoS' />></li>
        <li><Breadcrumb href='/sos/audit' text='Audit Admin' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Generate Assertions' /></li>
    </ul>
);

interface GenerateAssertionsPageProps {
    dosState: DOS.AppState;
    forward: OnClick;
    readyToGenerate: boolean;
}

interface GenerateAssertionsPageState {
    canGenerateAssertions: boolean;
    generatingAssertions: boolean;
    generationTimeOutSeconds: number;
}

class GenerateAssertionsPage extends React.Component<GenerateAssertionsPageProps, GenerateAssertionsPageState> {
    public constructor(props: GenerateAssertionsPageProps) {
        super(props);

        this.state = {
            canGenerateAssertions: !props.dosState.assertionsGenerated && props.readyToGenerate,
            generatingAssertions: false,
            generationTimeOutSeconds: 5,
        };
    }

    public render() {
        const {
            dosState,
            forward,
            readyToGenerate,
        } = this.props;

        const generate = async () => {
            this.setState({generatingAssertions: true});
            this.setState({canGenerateAssertions: false});
            this.render();

            const timeoutQueryParams = new URLSearchParams();
            timeoutQueryParams.set('generationTimeOutSeconds', this.state.generationTimeOutSeconds.toString());
            generateAssertions(timeoutQueryParams).then()
                .catch(reason => {
                    alert('generateAssertions error in fetchAction ' + reason);
                });

            this.setState({generatingAssertions: false});
            this.setState({canGenerateAssertions: true});
        };

        const main =
            <div>
                <Breadcrumbs/>
                <h2>Generate Assertions for IRV Contests</h2>
                <p>
                    Assertions will be generated for all IRV contests to
                    support opportunistic discrepancy computation.
                </p>
                <div className='control-buttons mt-default'>
                    <Button onClick={generate}
                            disabled={!this.state.canGenerateAssertions || this.state.generatingAssertions}
                            className='pt-button pt-intent-primary'>
                        Generate Assertions
                    </Button>
                </div>
                { this.displayTimeoutInput() &&
                    <div>
                        <label htmlFor='timeOut'>Timeout (in seconds): </label>
                        <input
                            name='timeOut'
                            type='number'
                            min={5}
                            max={999}
                            className='pt-input'
                            style={{width: '60px'}}
                            value={this.state.generationTimeOutSeconds}
                            onChange={this.onTimeoutChange}>
                        </input>
                    </div>
                }
                <div className='control-buttons mt-default'>
                    <Button onClick={forward}
                            className='pt-button pt-intent-primary'>
                        Next
                    </Button>
                </div>
                <div>
                    {this.getAssertionGenerationSummaryTable()}
                </div>
                <div>
                    {this.getAssertionGenerationStatusTable()}
                </div>
            </div>;

        return <DOSLayout main={main}/>;
    }

    private displayTimeoutInput() {
        return this.props.dosState.assertionGenerationStatuses &&
            this.props.dosState.assertionGenerationStatuses.some(ags => !ags.succeeded && ags.retry);
    }

    private onTimeoutChange = (event: React.FormEvent<HTMLInputElement>) => {
        const timeout = (event.target as HTMLInputElement).value;
        this.setState({generationTimeOutSeconds: Number(timeout)});
    }

    private getAssertionGenerationStatusTable() {
        interface RowProps {
            status: AssertionStatus;
        }

        const AssertionStatusTableRow = (props: RowProps) => {
            const {status} = props;

            return (
                <tr>
                    <td>{ status.contestName }</td>
                    <td style={{color:  status.succeeded ? 'green' : 'red'}}>
                        { status.succeeded ? 'Success' : 'Failure' }
                    </td>
                    <td>{status.retry ? 'Yes' : 'No'}</td>
                </tr>
            );
        };

        const assertionRows = _.map(this.props.dosState.assertionGenerationStatuses, a => (
            <AssertionStatusTableRow key={ a.contestName } status={ a } />
        ));

        if (this.state.generatingAssertions) {
            return (
                <Card className='mt-default'>
                    <Spinner className='pt-medium' intent={ Intent.PRIMARY } />
                    <div>Generating Assertions...</div>
                </Card>
            );
        } else if (this.props.dosState.assertionGenerationStatuses) {
            return (
                <div>
                <span className='generate-assertions-exports'>
                    <b>Export Assertions: </b>
                    <a href='#' onClick={exportAssertionsAsCsv}>CSV</a>
                    &nbsp;|&nbsp;
                    <a href='#' onClick={exportAssertionsAsJson}>JSON</a>
                </span>
                    <table className='pt-html-table pt-html-table-striped rla-table mt-default'>
                        <thead>
                        <tr>
                            <th>Contest</th>
                            <th>Assertion Generation Status</th>
                            <th>Advise Retry</th>
                        </tr>
                        </thead>
                        <tbody>{ assertionRows }</tbody>
                    </table>
                </div>
            );
        } else {
            return <div />;
        }
    }
    private getAssertionGenerationSummaryTable() {
       interface RowSummary {
           summary: GenerateAssertionsSummary;
       }

       const AssertionSummaryTableRow = (props: RowSummary) => {
           const {summary} = props;

           return (
               <tr>
                   <td>{summary.contestName}</td>
                   <td>{summary.winner}</td>
                   <td>{summary.error}</td>
                   <td>{summary.warning}</td>
                   <td>{summary.message}</td>

               </tr>
           );
       };

       const assertionSummaryRows = _.map(this.props.dosState.generateAssertionsSummaries, a => (
            <AssertionSummaryTableRow summary={a} />
       ));

       return (
           <table className='pt-html-table pt-html-table-striped rla-table mt-default'>
               <thead>
               <tr>
                   <th>Contest</th>
                   <th>Winner</th>
                   <th>Error</th>
                   <th>Warning</th>
                   <th>Message</th>
               </tr>
               </thead>
               <tbody>{assertionSummaryRows}</tbody>
           </table>
       );
    }
}

export default GenerateAssertionsPage;
