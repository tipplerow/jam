#!/bin/sh
head -1 List1/log_1.0_genotype_score.csv                      > _header_
cat     List*/log_1.0_genotype_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > log_1.0_genotype_score.csv

head -1 List1/log_1.0_allele_score.csv                      > _header_
cat     List*/log_1.0_allele_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > log_1.0_allele_score.csv

/bin/rm -f _header_ _body_

