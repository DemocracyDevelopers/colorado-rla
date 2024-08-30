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

const generationTimeoutParam = 'timeLimitSeconds';

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
            timeoutQueryParams.set(generationTimeoutParam, this.state.generationTimeOutSeconds.toString());
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
                            disabled={this.state.generatingAssertions}
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

       interface CombinedData {
           contestName : string,
           succeeded : boolean | undefined,
           retry : boolean | undefined,
           winner : string,
           error : string,
           warning : string,
           message : string
       }

       interface CombinationSummary {
           combinedData : CombinedData;
       }

        const CombinedTableRow = (input: CombinationSummary) => {
           const {combinedData } = input;

            // FIXME: succeeded and retry can now be undefined, so this needs to be updated to be blank then.
            return (
                <tr>
                    <td>{combinedData.contestName}</td>
                    <td style={{color: combinedData.succeeded ? 'green' : 'red'}}>
                        {combinedData.succeeded ? 'Success' : 'Failure'}
                    </td>
                    <td>{combinedData.retry ? 'Yes' : 'No'}</td>
                    <td>{combinedData.winner}</td>
                    <td>{combinedData.error}</td>
                    <td>{combinedData.warning}</td>
                    <td>{combinedData.message}</td>
                </tr>
            );
        };

        // Join up the rows by contest name if matching. If there is no matching contest name in the other
        // list, add a row with blanks for the missing data.
        // Various kinds of absences are possible, because there may be empty summaries at the start;
       // conversely, in later phases we may rerun generation (and hence get status) for only a few contests.
       const joinRows = (statuses: DOS.AssertionGenerationStatuses | undefined, summaries: DOS.GenerateAssertionsSummary[]) => {
           summaries.sort((a, b) => a.contestName < b.contestName ? -1 : 1);
           let rows: CombinedData[] = [];

           var i = 0;
           var j = 0;

           if(statuses === undefined) {
               // No status yet. Just print summary data.
               return summaries.map(s => {
                   let row: CombinedData = {
                       contestName : s.contestName,
                       succeeded : undefined,
                       retry : undefined,
                       winner : s.winner,
                       error : s.error,
                       warning : s.warning,
                       message : s.message
                   }
                   console.log("Status undefined. Summaries only for contest "+s.contestName);
                   return row;
               })

           } else {
               statuses.sort((a, b) => a.contestName < b.contestName ? -1 : 1);
               while (i < statuses.length) {
                   if (statuses[i].contestName === summaries[j].contestName) {
                       // Matching contest names. Join the rows and move indices along both lists.
                       let row: CombinedData = {
                           contestName: statuses[i].contestName,
                           succeeded: statuses[i].succeeded,
                           retry: statuses[i].retry,
                           winner: summaries[j].winner,
                           error: summaries[j].error,
                           warning: summaries[j].warning,
                           message: summaries[j].message
                       }
                       console.log("Matching for contest "+statuses[i].contestName+" . Status = "+statuses[i].succeeded)
                       rows.push(row);
                       i++;
                       j++;
                   } else if (statuses[i].contestName < summaries[j].contestName) {
                       // We have a status with no matching summary. Fill in the summary with blanks.
                       // increment status index only.
                       let row: CombinedData = {
                           contestName: statuses[i].contestName,
                           succeeded: statuses[i].succeeded,
                           retry: statuses[i].retry,
                           winner: '',
                           error: '',
                           warning: '',
                           message: ''
                       }
                       console.log("Status only for contest "+statuses[i].contestName)
                       rows.push(row);
                       i++;
                   } else if (statuses[i].contestName < summaries[j].contestName) {
                       // We have a summary with no matching status. Fill in status 'undefined'.
                       // Increment summary index only.
                       let row: CombinedData = {
                           contestName: summaries[j].contestName,
                           succeeded: undefined,
                           retry: undefined,
                           winner: summaries[j].winner,
                           error: summaries[j].error,
                           warning: summaries[j].warning,
                           message: summaries[j].message
                       }
                       console.log("Summary only for contest "+summaries[j].contestName)
                       rows.push(row);
                       j++;
                   }
               }
           }

           return rows;
       }

       const combinedRows = _.map(joinRows(this.props.dosState.assertionGenerationStatuses, this.props.dosState.generateAssertionsSummaries), d => (
           <CombinedTableRow combinedData={d} />
        ))

        if (this.state.generatingAssertions) {
            return (
                <Card className='mt-default'>
                    <Spinner className='pt-medium' intent={ Intent.PRIMARY } />
                    <div>Generating Assertions...</div>
                </Card>
            );
        } else if (this.props.dosState.assertionGenerationStatuses || this.props.dosState.generateAssertionsSummaries.length > 0) {
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
                            <th>Winner</th>
                            <th>Error</th>
                            <th>Warning</th>
                            <th>Message</th>
                        </tr>
                        </thead>
                        <tbody>{combinedRows}</tbody>
                    </table>
                </div>
            );
        } else {
            return <div/>;
        }
    }

}

export default GenerateAssertionsPage;
