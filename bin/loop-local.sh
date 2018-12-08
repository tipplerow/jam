#!/bin/sh
########################################################################
# Run a series of local jobs sequentially.
#
# Usage: loop-local.sh WorkDir TrialStart TrialStop [JVM_FLAGS] ClassName ...
########################################################################

if [ $# -lt 4 ]
then
    echo "Usage:" `basename $0` "WorkDir TrialStart TrialStop [JVM_FLAGS] ClassName ..."
    exit 1
fi

if [ -z "$JAM_HOME" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

WorkDir=$1
shift

TrialStart=$1
shift

TrialStop=$1
shift

BLOCK_SIZE=100
TrialIndex=$TrialStart

while [ $TrialIndex -le $TrialStop ]
do
    BlockIndex=$(expr $TrialIndex / $BLOCK_SIZE + 1)
    ${JAM_HOME}/bin/job-local.sh $WorkDir $BlockIndex $TrialIndex "$@"
    TrialIndex=$(expr $TrialIndex + 1)
done

exit 0
