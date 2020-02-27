#!/bin/sh
########################################################################
# Processes MAF files and generates the protein structures generated                                                               
# by missense mutations.  
#
# Usage: missense-processor.sh MAF_FILE BARCODE_FILE MISSENSE_DIR CCF_THRESHOLD
########################################################################

if [ $# -ne 4 ]
then
    echo "Usage: `basename $0` MAF_FILE BARCODE_FILE MISSENSE_DIR CCF_THRESHOLD"
    exit 1
fi

if [ -z "$JAM_HOME" ]
then
    echo "JAM_HOME is not set; exiting."
    exit 1
fi

MAF_FILE=$1
BARCODE_FILE=$2
MISSENSE_DIR=$3
CCF_THRESHOLD=$4

if [ ! -d $MISSENSE_DIR ]
then
    echo "Creating ${MISSENSE_DIR}..."
    mkdir -p $MISSENSE_DIR
fi

${JAM_HOME}/bin/jam-run.sh $JAM_HOME \
           jam.missense.MissenseProcessor \
           $MAF_FILE $BARCODE_FILE $MISSENSE_DIR $CCF_THRESHOLD
