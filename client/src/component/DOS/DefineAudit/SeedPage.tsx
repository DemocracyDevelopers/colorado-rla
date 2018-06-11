import * as React from 'react';

import Nav from '../Nav';

import SeedForm from './SeedForm';

import * as corlaDate from 'corla/date';


const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/audit'>
                Audit Admin
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Seed
            </a>
        </li>
    </ul>
);

interface PageProps {
    back: OnClick;
    nextPage: OnClick;
    publicMeetingDate: Date;
    seed: string;
    uploadRandomSeed: OnClick;
}

const AuditSeedPage = (props: PageProps) => {
    const { back, nextPage, publicMeetingDate, seed, uploadRandomSeed } = props;

    const forms: DOS.Form.Seed.Ref = {};

    const onSaveAndNext = () => {
        if (forms.seedForm) {
            uploadRandomSeed(forms.seedForm.seed);
        }

        nextPage();
    };

    const setSeedDiv = (
           <div className='pt-card'>
                <table className='pt-table'>
                    <tbody>
                        <tr>
                            <td><strong>Random seed: </strong></td>
                            <td>{ seed }</td>
                        </tr>
                    </tbody>
                </table>
            </div>        
    );

    const dynamicSeedForm = <SeedForm forms={ forms } />;
    const seedForm = seed ? setSeedDiv : dynamicSeedForm;

    const formattedPublicMeetingDate = corlaDate.format(publicMeetingDate);

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div className='pt-card'>
                <h3>Audit Definition - Enter Random Seed</h3>
                <div className='pt-card'>
                    Enter the random seed generated from the public meeting on { formattedPublicMeetingDate }.
                </div>
                <div className='pt-card'>
                    <span className='pt-icon pt-intent-warning pt-icon-warning-sign' />
                    <span> </span>
                    <strong>Once saved, this random seed cannot be modified.</strong>
                </div>
                <div className='pt-card'>
                    { seedForm }
                </div>
            </div>
            <div>
                <button onClick={ back } className='pt-button pt-intent-primary pt-breadcrumb'>
                    Back
                </button>
                <button onClick={ onSaveAndNext } className='pt-button pt-intent-primary pt-breadcrumb'>
                    Save & Next
                </button>
            </div>
        </div>
    );
};


export default AuditSeedPage;
