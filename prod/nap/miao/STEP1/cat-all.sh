#!/bin/sh
head -1 List1/step1_genotype_score.csv                      > _header_
cat     List*/step1_genotype_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > step1_genotype_score.csv

head -1 List1/step1_allele_score.csv                      > _header_
cat     List*/step1_allele_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > step1_allele_score.csv

/bin/rm -f _header_ _body_

cat List*/*_exceptions.txt > step1_exceptions.txt

