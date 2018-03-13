#!/bin/sh
########################################################################
# Usage: jam-test.sh APP_SCRIPT GOLD_DIR FILE1 [FILE2 ...]
########################################################################

TEST_SCRIPT=`basename $0`

if [ $# -lt 3 ]
then
    echo "Usage: $TEST_SCRIPT APP_SCRIPT GOLD_DIR FILE1 [FILE2 ...]; exiting."
    exit 1
fi

if [ -z "${JAM_HOME}" ]
then
    echo "Environment variable JAM_HOME must be set; exiting."
    exit 1
fi

APP_SCRIPT=$1
shift

GOLD_DIR=$1
shift

. ${JAM_HOME}/bin/shell.lib

echo "Running [${APP_SCRIPT}]..."
eval $APP_SCRIPT

for OutFile in "$@"
do
    AssertIdentical $OutFile ${GOLD_DIR}/$OutFile
    echo "${OutFile}: OKAY"
    /bin/rm -f $OutFile
done

exit 0
