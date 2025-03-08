This directory contains the http files for loading up the database intended to be used for demo 1.

# Setup
Before running these demos, 
- drop all tables from the corla database,
- run colorado-rla by following the 
[developer instructions](https://github.com/DemocracyDevelopers/colorado-rla/blob/main/docs/25_developer.md), 
- including loading the test credentials from colorado-rla/test/corla-test-credentials.psql.
- Run the raire-service by following its [README](https://github.com/DemocracyDevelopers/raire-service).

Both the raire-service and colorado-rla need to be running while the demos are running.

# Running Demo 1.
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

It also saves the sample sizes and assertions files in the `workflows/demo-stored-data` directory.

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

# Running the NSW Demo

Very similar to Demo 1.
To run the complete demo, set the environment to 'dev' and execute the following:
- `NSW_demo_loadCVRs.http`
- `NSW_demo_loadManifests.http`
- `NSW_defineAudit.http`

# Running the Tiny IRV Demo

Very similar to the other demos.
To run the complete demo, set the environment to 'dev' and execute the following:
- `TinyIRV_demo_loadCVRs.http`
- `TinyIRV_demo_loadManifests.http`
- `TinyIRV_defineAudit.http`

## Details of the data
- Lake County (33) has the TinyExample1 IRV contest, plus a tied and a non-tied plurality contest.
- La Plata County (34) has an example of TinyExample1 that is meant to contain
  - an invalid contest name (TinyInvalidExample should be canonicalized to TinyExample1),
  - an invalid candidate name (Alicia should be canonicalized to Alice),
  - invalid IRV votes (e.g. repeated or missing ranks).
- Larimer County (35) has a TiedIRV contest.
- Las Animas County (36) has the Guide To Raire Example 3.

## Running it manually
This demo is well suited to running manually with the client UI. All of the data files are available
in `src/test/resources/CSVs/Tiny-IRV-Examples`. For every file `x.y` requiring a sha256sum, the directory
also contains it in a file called `x.y.sha256sum`.

1. County data upload.
   - Log in as countyadmin33 (Lake) and upload CVRs `ThreeCandidatesTenVotesPlusTiedPlurality.csv`
     and manifest `ThreeCandidatesTenVotes_Manifest.csv`.
   - Log in as countyadmin34 (La Plata) and upload CVRs `ThreeCandidatesTenInvalidVotes.csv` 
     and manifest `ThreeCandidatesTenVotes_Manifest.csv`.   
   - Log in as countyadmin35 (Larimer) and upload CVRs `ThreeCandidatesTenVotes_TiedIRV.csv`
     and manifest `ThreeCandidatesTenVotes_Manifest.csv`.
   - Log in as countyadmin36 (Las Animas) and upload CVRs `GuideToRAIREExample3.csv`
     and manifest `GuideToRAIREExample3-manifest.csv`.
2. Defining the audit. 
   - Log in as stateadmin. Choose any dates you like, and a risk limit of 0.03.
   - Upload Tiny_IRV_Demo_Canonical_List.csv as the canonical list file.
   - At the 'Standardize contest names' step, canonicalize 'TinyInvalidExample1' to 'TinyExample1' as colorado-rla suggests.
   - At the 'Standardize choice names' step, canonicalize A to Aaron, B to Bernice, C to Cherry and D to Dinh.
   - Generate the assertions. You should see successes for Example3 (Las Animas) and TinyExample1 (Multiple), and a TIED_WINNERS failure for Tied_IRV. Optionally, download some.
   - Optionally, download the sample size estimate csv.
   - At the 'Select contests' page, choose Example3 (County Contest) and TinyExample1 (State Contest).
     The TiedIRV checkbox should be disabled because it is not auditable. (VT: neither is tied plurality - should that be disabled?)
   - Enter the seed (our example is "9823749812374981273489712389471238974").
   - Launch the audit. I see 5 remaining ballots to audit in Lake and La Plata Counties and 57 in Las Animas, but you will
     see different numbers if you chose a different risk limit or seed.
3. Auditing ballots. TODO - Add instructions and testing for ballot auditing.

Alternatively, all these steps can be done without manifests, in which case the CDOS audit sequence
blocks after sample size estimation. For this data, the sample size estimates should be the same
with or without manifests.

## Examining the output
TODO - Calculate expected sample sizes etc.

