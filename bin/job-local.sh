#!/bin/sh
########################################################################
# Run one job in a production sesquence on the local machine.
#
# Usage: job-local.sh WorkDir BlockIndex TrialIndex [JVM_FLAGS] ClassName ...
########################################################################

if [ $# -lt 4 ]
then
    echo "Usage:" `basename $0` "WorkDir BlockIndex TrialIndex [JVM_FLAGS] ClassName ..."
    exit 1
fi

if [ -z "$JAM_HOME" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

WorkDir=$1
shift

BlockIndex=$1
shift

TrialIndex=$1
shift

ReportDir=$(printf "%s/Block%03d/Trial%05d" $WorkDir $BlockIndex $TrialIndex)

if [ ! -d $ReportDir ]
then
    mkdir -p $ReportDir
fi

if [ ! -d $ReportDir ]
then
    echo "Could not create local report directory ${ReportDir}; exiting."
    exit 1
fi

cd $WorkDir

${JAM_HOME}/bin/jam-run.sh ${NEO_HOME} \
    -Djam.app.reportDir=$ReportDir \
    -Djam.app.trialIndex=$TrialIndex \
    "$@"

exit 0
