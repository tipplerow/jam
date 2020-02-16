#!/bin/sh
########################################################################
# Processes the MAF file from Liu et al. and generates the protein
# structures arising from missense mutations.
########################################################################

if [ -z "$JAM_HOME" ]
then
    echo "JAM_HOME is not set; exiting."
    exit 1
fi

PROP_FILE=liu_missense_processor.prop
MAF_INPUT_FILE=${CSB_DATA_VAULT}/Liu/Liu_Missense.maf.gz
FASTA_OUTPUT_FILE=Liu_Missense.fa

${JAM_HOME}/bin/jam-run.sh $JAM_HOME \
           -Djam.app.reportDir=`pwd` \
           -Djam.maf.mafFileInput=${MAF_INPUT_FILE} \
           -Djam.maf.fastaFileOutput=${FASTA_OUTPUT_FILE} \
           jam.maf.MissenseProcessor $PROP_FILE

gzip -v $FASTA_OUTPUT_FILE

/bin/mv jam-exceptions.log missense-processor-exceptions.log
/bin/mv runtime.env missense-processor-runtime.env
/bin/mv runtime.prop missense-processor-runtime.prop
