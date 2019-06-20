#!/bin/sh
head -1 List1/log_0.1_genotype_score.csv                      > _header_
cat     List*/log_0.1_genotype_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > log_0.1_genotype_score.csv

head -1 List1/log_0.1_allele_score.csv                      > _header_
cat     List*/log_0.1_allele_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > log_0.1_allele_score.csv

/bin/rm -f _header_ _body_

