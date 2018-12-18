#!/bin/sh

cat Block*/Trial*/threshold-tuner-summary.csv > tmp

head -1 tmp > summary.csv
grep -v ndex tmp >> summary.csv

/bin/rm -f tmp
