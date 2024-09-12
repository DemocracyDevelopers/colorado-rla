This directory contains the http file (demo1.http) for loading up the database intended to be used for demo 1.

Before running demo1.http, run colorado-rla by following the 
[developer instructions](https://github.com/DemocracyDevelopers/colorado-rla/blob/main/docs/25_developer.md). 
Then load the test credentials from colorado-rla/test/corla-test-credentials.psql.

Now you should be able to run demo1.http by clicking on the double-green-arrow in IntelliJ.

The NSW 2021 Byron Mayoral contest has been spread across all 64 Colorado counties 
These are stored in src/test/resources/CSVs/split-Byron.

Five counties have their own data, to which the split Byron data has been added.
These are loaded from src/test/resources/CSVs/Demo1.

- Adams County has test data from AdamsAndAlamosa, with 284 Byron Mayoral votes from Byron-1.csv appended. Demo1 uses the ballot manifests from AdamsAndAlamosa.
- Alamosa County has test data from AdamsAndAlamosa, with 284 Byron Mayoral votes Byron-2.csv appended. Demo1 uses the ballot manifests from AdamsAndAlamosa.
- Arapahoe has 284 Byron Mayoral votes from Byron-3.csv, with the tied IRV contest from Tiny-IRV-Examples/ThreeCandidatesTenVotes_TiedIRV appended to make /Demo1/3-arapahoe-Byron-3-plus-tied-irv.csv. Demo1 uses the ballot manifest split-Byron/Byron-3-manifest.csv.
- Archuleta has the votes from NewSouthWales/Kempsey_Mayoral.csv, with 131 Byron Mayoral votes from Byron-4.csv appended to make 4-archuleta-kempsey-plusByron-4.csv. Demo1 uses the ballot manifest NewSouthWales/Kempsey_Mayoral.manifest.csv. 
- Boulder has the redacted real data from Boulder23, with 284 Byron Mayoral votes from Byron-7.csv appended. Demo1 uses Boulder's manifest from Boulder23/Boulder-IRV-Manifest.csv.
 
For each other county n, the demo loads split-Byron/Byron-n.csv and uses the accompanying manifest.  
