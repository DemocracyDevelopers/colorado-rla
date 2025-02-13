-- Vote Counts by County, Contest, Choice

SELECT
  county.name AS county_name,
  contest.name AS contest_name,
  county_contest_vote_total.choice AS choice,
  county_contest_vote_total.vote_total AS votes
FROM
  county_contest_vote_total,
  county_contest_result,
  contest,
  county
WHERE
  county_contest_vote_total.result_id = county_contest_result.id AND
  county_contest_result.contest_id = contest.id AND
  county_contest_result.county_id = county.id AND
  contest.description = 'PLURALITY'
ORDER BY
  county.name ASC,
  contest.id ASC,
  county_contest_vote_total.vote_total DESC;
