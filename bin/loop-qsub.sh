#!/bin/sh
########################################################################
# Submit a series of production cases to the cluster without
# overloading the queue...
########################################################################

if [ $# -lt 2 ]
then
    echo "Usage:" `basename $0` "TRIAL_START TRIAL_STOP [QUEUE_LIMIT SLEEP_SECONDS]"
    exit 1
fi

WORK_DIR=`pwd`

TRIAL_START=$1
shift

TRIAL_STOP=$1
shift

if [ $# -ge 1 ]
then
    QUEUE_LIMIT=$1
    shift
else
    QUEUE_LIMIT=18
fi

if [ $# -ge 1 ]
then
    SLEEP_SECONDS=$1
    shift
else
    SLEEP_SECONDS=60
fi

BLOCK_SIZE=100
JOB_SCRIPT="qsub ${WORK_DIR}/job-qsub.sh"

TrialIndex=$TRIAL_START

while [ $TrialIndex -le $TRIAL_STOP ]
do
    BlockIndex=$(expr $TrialIndex / $BLOCK_SIZE + 1)
    QueueCount=`squeue | grep $USER | wc -l`

    if [ $QueueCount -lt $QUEUE_LIMIT ]
    then
	echo -n "Queue available [$QueueCount of $QUEUE_LIMIT]: submitting job [$TrialIndex] "
	$JOB_SCRIPT $BlockIndex $TrialIndex
	TrialIndex=$(expr $TrialIndex + 1)

	# Give the queuing system time to register the job...
	sleep 1
    else
	DoneList=`grep DONE slurm-*.out | cut -d':' -f1`
	ErrorList=`grep Exception slurm-*.out | cut -d':' -f1`

	for FileName in $DoneList $ErrorList
	do
	    echo "Removing $FileName..."
	    /bin/rm -f $FileName
	done

	echo "Queue full [$QueueCount of $QUEUE_LIMIT]: waiting..."
	sleep $SLEEP_SECONDS
    fi
done
