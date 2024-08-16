import * as React from 'react';
import AuditReportForm from 'corla/component/AuditReportForm';
import {Button, Intent} from "@blueprintjs/core";
import exportAssertionsAsJson from "corla/action/dos/exportAssertionsAsJson";
import exportAssertionsAsCsv from "corla/action/dos/exportAssertionsAsCsv";

interface StatusProps {
    auditIsComplete: boolean;
    canRenderReport: boolean;
    currentRound: number;
    finishedCountiesCount: number;
    totalCountiesCount: number;
}

const Status = (props: StatusProps) => {
    const {
        auditIsComplete,
        canRenderReport,
        currentRound,
        finishedCountiesCount,
        totalCountiesCount,
    } = props;

    return (
        <div className='state-dashboard-round'>
            <div>
                { !auditIsComplete && <h4>Round { currentRound } in progress</h4> }
                { auditIsComplete && <h4>Congratulations! The audit is complete.</h4> }
                <span className='state-dashboard-round-summary'>
                    { finishedCountiesCount } of { totalCountiesCount } counties
                    have finished this round.
                </span>
            </div>
            <div>
                <Button onClick={exportAssertionsAsJson} className='pt-button pt-intent-primary'>
                    Export Assertions as JSON
                </Button>
            </div>
            <div>
                <Button onClick={exportAssertionsAsCsv} className='pt-button pt-intent-primary'>
                    Export Assertions as CSV
                </Button>
            </div>
            <div>
                {canRenderReport && (<AuditReportForm
                canRenderReport={canRenderReport}
                /> 
                )}
            </div>
        </div>
    );
};

export default Status;
