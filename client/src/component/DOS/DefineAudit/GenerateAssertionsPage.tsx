import * as _ from 'lodash';
import * as React from 'react';

import { Breadcrumb, Button, Card, Intent } from '@blueprintjs/core';

import generateAssertions from 'corla/action/dos/generateAssertions';
import DOSLayout from 'corla/component/DOSLayout';
import exportAssertionsAsJson from "corla/action/dos/exportAssertionsAsJson";
import exportAssertionsAsCsv from "corla/action/dos/exportAssertionsAsCsv";
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
}

class GenerateAssertionsPage extends React.Component<GenerateAssertionsPageProps, GenerateAssertionsPageState> {
    public constructor(props: GenerateAssertionsPageProps) {
        super(props);

        this.state = {
            canGenerateAssertions: !props.dosState.assertionsGenerated && props.readyToGenerate,
        };
    }

    public render() {
        const {
            dosState,
            forward,
            readyToGenerate,
        } = this.props;

        const generate = async () => {
            this.setState({canGenerateAssertions: false});
            this.render();

            generateAssertions().then()
                .catch(reason => {
                    alert('generateAssertions error in fetchAction ' + reason);
                });

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
                <p>
                    This prototype is not sophisticated enough to give you
                    feedback on how assertion generation is progressing ... but
                    once it is done either a green alert will tell you that it
                    was successful or a red one will tell you it has failed.
                </p>
                <p>
                    Then click on 'Export Assertions' and the generated assertions
                    will be exported to a JSON file and automatically downloaded.
                </p>
                <div className='control-buttons mt-default'>
                    <Button onClick={generate}
                            disabled={!this.state.canGenerateAssertions}
                            className='pt-button pt-intent-primary'>
                        Generate Assertions
                    </Button>
                </div>

                <div className='control-buttons mt-default'>
                    <Button onClick={exportAssertionsAsJson}
                            className='pt-button pt-intent-primary'>
                        Export Assertions as JSON
                    </Button>
                </div>

                <div className='control-buttons mt-default'>
                    <Button onClick={exportAssertionsAsCsv}
                            className='pt-button pt-intent-primary'>
                        Export Assertions as CSV
                    </Button>
                </div>

                <div className='control-buttons mt-default'>
                    <Button onClick={forward}
                            className='pt-button pt-intent-primary'>
                        Next
                    </Button>
                </div>
                <div>
                    {this.getAssertionGenerationSummaryTable()}
                </div>
            </div>;

        return <DOSLayout main={main}/>;
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
