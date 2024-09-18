This directory contains the http files for loading up the database intended to be used for demo 1.

## Running the demo

Before running the demo, 
- drop all tables from the corla database,
- run colorado-rla by following the 
[developer instructions](https://github.com/DemocracyDevelopers/colorado-rla/blob/main/docs/25_developer.md), 
- including loading the test credentials from colorado-rla/test/corla-test-credentials.psql.
- Run the raire-service by following its [README](https://github.com/DemocracyDevelopers/raire-service).

Both the raire-service and colorado-rla need to be running while the demo is running.

Now you should be able to run demo http files by clicking on the double-green-arrow in IntelliJ. 
Run them in the `dev` environment, which appears as a drop down after 'Run with' if you open the .http
file in the IntelliJ editor. This loads some variables from `http-client.env.json`. 
To run the complete demo, execute the following:
- `demo1_loadCVRs.http`
- `demo1_loadManifests.http`
- `Boulder_loadCVRs.http`
- `Boulder_loadManifests.http`
- `demo1_defineAudit.http`
- `county-2-Alamosa-do-audit.http`

The first 4 can run in any order, but they can take a while - make sure they're finished before running `demo1_defineAudit` and then `county-2-Alamosa-do-audit`.

## Examining the output.
`demo1-defineAudit` prints into the console a list of 'ballots remaining in round' for all contests. Counties 4 and 7 should have hundreds of ballots to audit; the rest should have a range of values from 0 to about 6.

It also saves the sample sizes and assertions files in the `workflows/demo1-stored-data` directory.

`county-2-Alamosa-do-audit` prints the requested audit ballot IDs to the console - there should be two.

## Details of the data.
 
The NSW 2021 Byron Mayoral contest has been spread across all 64 Colorado counties 
These are stored in `src/test/resources/CSVs/split-Byron`.

Five counties have their own data, to which the split Byron data has been added.
These are loaded from `src/test/resources/CSVs/Demo1`.

- Adams County has test data from AdamsAndAlamosa, with 284 Byron Mayoral votes from Byron-1.csv appended. `Demo1` uses the ballot manifests from AdamsAndAlamosa.
- Alamosa County has test data from AdamsAndAlamosa, with 284 Byron Mayoral votes Byron-2.csv appended. `Demo1` uses the ballot manifests from AdamsAndAlamosa.
- Arapahoe has 284 Byron Mayoral votes from `Byron-3.csv`, with the tied IRV contest from `Tiny-IRV-Examples/ThreeCandidatesTenVotes_TiedIRV` appended to make `/Demo1/3-arapahoe-Byron-3-plus-tied-irv.csv`. `Demo1` uses the ballot manifest `split-Byron/Byron-3-manifest.csv`.
- Archuleta has the votes from `NewSouthWales/Kempsey_Mayoral.csv`, with 131 Byron Mayoral votes from `Byron-4.csv` appended to make 4-archuleta-kempsey-plusByron-4.csv. `Demo1` uses the ballot manifest `NewSouthWales/Kempsey_Mayoral.manifest.csv`.
- Boulder has the redacted real data from Boulder23, with 284 Byron Mayoral votes from `Byron-7.csv` appended to make `7-boulder-2023-plusByron-7.csv`. `Boulder_loadManifest.http` uses Boulder's manifest from `Boulder23/Boulder-IRV-Manifest.csv`.
 
For each other county n, the demo loads `split-Byron/Byron-n.csv` and uses the accompanying manifest.  

Each of the .http files can be used independently if you like. For example, if you want to test whether Sample Size estimation works without manifests, run `Boulder_loadCVRs` and `demo1_loadCVRs.http`
then hit the sample size estimation endpoint.