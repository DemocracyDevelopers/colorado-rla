import * as React from 'react';

import { Breadcrumb, Button, Card, Intent } from '@blueprintjs/core';

import DOSLayout from 'corla/component/DOSLayout';

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb href='/sos' text='SoS' />></li>
        <li><Breadcrumb href='/sos/audit' text='Audit Admin' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Generate Assertions' /></li>
    </ul>
);

interface GenerateAssertionsPageProps {
    assertionsGenerated: boolean;
    forward: OnClick;
    generate: OnClick;
    readyToGenerate: boolean;
}

class GenerateAssertionsPage extends React.Component<GenerateAssertionsPageProps> {
    public constructor(props: GenerateAssertionsPageProps) {
        super(props);
    }

    public render() {
        const {
            assertionsGenerated,
            forward,
            generate,
            readyToGenerate,
        } = this.props;

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
                                disabled={!readyToGenerate || assertionsGenerated}
                                className='pt-button pt-intent-primary'>
                            Generate Assertions
                        </Button>
                    </div>

                    <div className='control-buttons mt-default'>
                        <Button onClick={forward}
                                disabled={!assertionsGenerated}
                                className='pt-button pt-intent-primary'>
                            Next
                        </Button>
                    </div>
                </div>;

        return <DOSLayout main={ main } />;
    }

}

export default GenerateAssertionsPage;
