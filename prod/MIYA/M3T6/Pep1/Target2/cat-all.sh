#!/bin/sh

cat Block*/Trial*/*summary.csv > tmp

head -1 tmp > summary.csv
grep -v ndex tmp >> summary.csv

/bin/rm -f tmp