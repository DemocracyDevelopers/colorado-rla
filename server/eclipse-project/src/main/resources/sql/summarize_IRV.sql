-- Show the IRV summaries, sorted by contest name.
-- This is usually a (unique) winner and an empty error, but may also be a
-- blank winner and a non-empty error with an explanatory message.

SELECT gas.contest_name, gas.winner, gas.error, gas.message
FROM generate_assertions_summary as gas
ORDER BY contest_name;
