-- Show the IRV summaries, sorted by contest name.
-- This is usually a (unique) winner and an empty error, but may also be a
-- blank winner and a non-empty error with an explanatory message.
-- This also adds a column to indicate whether the contest was targeted for audit. This requires
-- a join with the contest_to_audit table, which is slightly complicated by the fact that that table
-- stores _one of_ the contest ids for multi-county contests, so we have to check whether there is
-- _any_ contest of the same name with an ID in the contest_to_audit table. This simply concatenates
-- all the audit reasons, in the very unlikely event that one contest is selected for both COUNTY_WIDE
-- and STATE_WIDE reasons.

SELECT gas.contest_name, targets.targeted_reason, gas.winner, gas.error, gas.message
FROM generate_assertions_summary as gas
     -- This sub-query attaches audit target reasons (aggregated), if any, to contest names.
LEFT JOIN (
    SELECT contest.name as name, STRING_AGG(contest_to_audit.reason, ',') as targeted_reason
    FROM contest
    LEFT JOIN contest_to_audit ON contest.id = contest_to_audit.contest_id
    GROUP BY contest.name
) AS targets
ON gas.contest_name = targets.name;




