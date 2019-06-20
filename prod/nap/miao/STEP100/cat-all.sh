#!/bin/sh
head -1 List1/step_100_genotype_score.csv                      > _header_
cat     List*/step_100_genotype_score.csv | grep -v Patient_ID > _body_

cat _header_ _body_ > step_100_genotype_score.csv

/bin/rm -f _header_ _body_

