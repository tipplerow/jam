#!/bin/sh
########################################################################
# Extracts individual tumor profiles from an RNA expression matrix.
#
# Usage: extract-expression.sh MATRIX_FILE BARCODE_FILE EXTRACT_DIR
########################################################################

if [ $# -ne 3 ]
then
    echo "Usage: `basename $0` MATRIX_FILE BARCODE_FILE EXTRACT_DIR"
    exit 1
fi

if [ -z "$JAM_HOME" ]
then
    echo "JAM_HOME is not set; exiting."
    exit 1
fi

MATRIX_FILE=$1
BARCODE_FILE=$2
EXTRACT_DIR=$3

if [ ! -d $EXTRACT_DIR ]
then
    echo "Creating ${EXTRACT_DIR}..."
    mkdir -p $EXTRACT_DIR
fi

${JAM_HOME}/bin/jam-run.sh $JAM_HOME jam.rna.ExpressionExtractor $MATRIX_FILE $BARCODE_FILE $EXTRACT_DIR
