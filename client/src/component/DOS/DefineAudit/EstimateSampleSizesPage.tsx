import * as React from 'react';

import { Breadcrumb, Button, Card, Intent } from '@blueprintjs/core';

import estimateSampleSizes from 'corla/action/dos/estimateSampleSizes';
import DOSLayout from 'corla/component/DOSLayout';

const Breadcrumbs = () => (
    <ul className='pt-breadcrumbs mb-default'>
        <li><Breadcrumb href='/sos' text='SoS' />></li>
        <li><Breadcrumb href='/sos/audit' text='Audit Admin' /></li>
        <li><Breadcrumb className='pt-breadcrumb-current' text='Estimate Sample Sizes' /></li>
    </ul>
);

interface EstimateSampleSizesPageProps {
    forward: OnClick;
}


class EstimateSampleSizesPage extends React.Component<EstimateSampleSizesPageProps> {
    public constructor(props: EstimateSampleSizesPageProps) {
        super(props);
    }

    public render() {
        const {
            forward,
        } = this.props;


        const main =
            <div>
                <Breadcrumbs/>
                <h2>Estimate Sample Sizes for all Contests</h2>
                <p>
                    Clicking on the estimate button will produce a CSV file containing
                    sample sizes for all contests in the database, which will be
                    automatically downloaded.
                </p>
                <div className='control-buttons mt-default'>
                    <Button onClick={estimateSampleSizes}
                            className='pt-button pt-intent-primary'>
                        Estimate
                    </Button>
                </div>

                <div className='control-buttons mt-default'>
                    <Button onClick={forward}
                            className='pt-button pt-intent-primary'>
                        Next
                    </Button>
                </div>
            </div>;

        return <DOSLayout main={ main } />;
    }

}

export default EstimateSampleSizesPage;
