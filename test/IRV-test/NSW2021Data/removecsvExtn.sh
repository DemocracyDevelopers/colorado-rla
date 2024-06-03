#!/bin/bash
for f in $(ls *.csv.manifest.csv.sha256sum);
do
	base=${f%.csv.manifest.csv.sha256sum}
	echo $base
	mv "$base".csv.manifest.csv "$base".manifest.csv 
	mv "$base".csv.manifest.csv.sha256sum "$base".manifest.csv.sha256sum 
done
