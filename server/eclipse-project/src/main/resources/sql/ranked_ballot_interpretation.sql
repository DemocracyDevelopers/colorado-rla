-- List of invalid ranked (IRV) ballots, and their valid interpretation as an order list of choices.
-- correlated with cvr_id in contest_comparison export, and with audited_cvr_count.

SELECT
    -- cou.name,
    con.name,
    irv.record_type,
    irv.cvr_id,
    irv.imprinted_id,
    irv.raw_choices,
    irv.valid_choices
FROM
   irv_ballot_interpretation AS irv
LEFT JOIN
   contest AS con
   ON irv.contest_id = con.id;
-- LEFT JOIN
 --    county as cou
  --   ON con.county_d = cou.id;
