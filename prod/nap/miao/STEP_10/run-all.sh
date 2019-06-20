#!/bin/sh

# File paths are relative to the run directory, one level below this
# script...

DATA_PROPERTY_FILE="../../common/miao_data.prop"
MODEL_PROPERTY_FILE="../miao_model_step_10.prop"
PATIENT_LIST_FORMAT="../../common/miao_patient_list_%d.txt"

CONSOLE_OUT_FORMAT="../out%d"
RUN_DIR_FORMAT="List%d"

ListIndex=1
ListCount=8

while [ $ListIndex -le $ListCount ]
do
    RunDir=$(printf $RUN_DIR_FORMAT $ListIndex)

    if [ ! -d $RunDir ]
    then
        mkdir $RunDir
    fi

    cd $RunDir
    PatientList=$(printf $PATIENT_LIST_FORMAT $ListIndex)
    ConsoleOut=$(printf $CONSOLE_OUT_FORMAT $ListIndex)

    nohup $JAM_HOME/bin/jam-run.sh $JAM_HOME jam.nap.NAPDriver \
          $PatientList $DATA_PROPERTY_FILE $MODEL_PROPERTY_FILE > $ConsoleOut 2>&1 &

    cd ..
    ListIndex=`expr $ListIndex + 1`
done
