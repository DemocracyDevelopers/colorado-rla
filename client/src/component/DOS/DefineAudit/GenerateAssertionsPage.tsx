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
    canonicalizationComplete: boolean;
    readyToGenerate: boolean;
    forward: () => void;
    generate: () => void;
}

class GenerateAssertionsPage extends React.Component<GenerateAssertionsPageProps> {
    public constructor(props: GenerateAssertionsPageProps) {
        super(props);
    }

    public render() {
        const {
            canonicalizationComplete,
            readyToGenerate,
            generate,
            forward,
        } = this.props;

        let main = null;

        if (readyToGenerate) {
            main =
                <div>
                    <Breadcrumbs/>
                    <h2>Generate Assertions for IRV Contests</h2>
                    <Card>
                        <p>
                            Assertions will be generated for all IRV contests to
                            support opportunistic discrepancy computation.
                        </p>
                    </Card>
                    <Button onClick={generate}
                            className='pt-breadcrumb'>
                        Generate Assertions
                    </Button>
                    <Button disabled
                            intent={Intent.PRIMARY}
                            className='pt-breadcrumb'>
                        Next
                    </Button>
                </div>;
        } else if (!canonicalizationComplete) {
            main =
                <div>
                    <Breadcrumbs />
                    <h2>Generate Assertions</h2>
                    <Card>
                        <p>
                            Assertions cannot be generated for IRV contests until counties
                            have uploaded choice and contest data, and canonicalization
                            (if required) is complete.
                        </p>
                    </Card>
                    <Button disabled
                            className='pt-breadcrumb'>
                        Generate Assertions
                    </Button>
                    <Button disabled
                            intent={ Intent.PRIMARY }
                            className='pt-breadcrumb'>
                        Next
                    </Button>
                </div>;
        } else {
            main =
                <div>
                    <Breadcrumbs />
                    <h2>Generate Assertions</h2>
                    <Card>
                        <p>
                            Assertions have been generated for IRV contests.
                        </p>
                    </Card>
                    <Button disabled
                            className='pt-breadcrumb'>
                        Generate Assertions
                    </Button>
                    <Button onClick={ () => forward() }
                            intent={ Intent.PRIMARY }
                            className='pt-breadcrumb'>
                        Next
                    </Button>
                </div>;
        }

        return <DOSLayout main={ main } />;
    }

}

export default GenerateAssertionsPage;
