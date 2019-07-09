#!/bin/sh
head -1 List1/linear_genotype_score.csv                      > _header_
cat     List*/linear_genotype_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > linear_genotype_score.csv

head -1 List1/linear_allele_score.csv                      > _header_
cat     List*/linear_allele_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > linear_allele_score.csv

/bin/rm -f _header_ _body_

cat List*/*_exceptions.txt > linear_exceptions.txt

