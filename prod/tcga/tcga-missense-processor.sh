#!/bin/sh
########################################################################
# Processes the TCGA MAF file and generates the protein structures
# arising from missense mutations.
########################################################################

if [ -z "$JAM_HOME" ]
then
    echo "JAM_HOME is not set; exiting."
    exit 1
fi

PROP_FILE=${JAM_HOME}/data/tcga/tcga_missense_processor.prop

MAF_INPUT_FILE=${CSB_DATA_VAULT}/TCGA/CellOrigin/TCGA_Missense.maf.gz
FASTA_OUTPUT_FILE=TCGA_Missense.fa

${JAM_HOME}/bin/jam-run.sh $JAM_HOME \
           -Djam.app.reportDir=`pwd` \
           -Djam.maf.mafFileInput=${MAF_INPUT_FILE} \
           -Djam.maf.fastaFileOutput=${FASTA_OUTPUT_FILE} \
           jam.maf.MissenseProcessor $PROP_FILE
