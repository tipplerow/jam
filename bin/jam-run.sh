#!/bin/sh
########################################################################
# Usage: jam-run.sh project [-D<name>=<value> ...] class [ARGUMENTS]
########################################################################

SCRIPT=`basename $0`

if [ $# -lt 2 ]
then
    echo "Usage: $SCRIPT project [-D<name>=<value> ...] class [ARGUMENTS]"
    exit 1
fi

PROJECT_NAME=$1
shift

TIPPLEROW_ROOT=$(cd `dirname $0`/../..; pwd)
export TIPPLEROW_ROOT=$TIPPLEROW_ROOT

. ${TIPPLEROW_ROOT}/jam/bin/shell.lib

AddLib ${TIPPLEROW_ROOT}/jlib
AddJar ${TIPPLEROW_ROOT}/jam/lib/jam.jar
AddJar ${TIPPLEROW_ROOT}/${PROJECT_NAME}/lib/${PROJECT_NAME}.jar

export CLASSPATH=$CLASSPATH
LOG4J_CONF=${TIPPLEROW_ROOT}/${PROJECT_NAME}/conf/log4j.xml

java -Dlog4j.configurationFile=${LOG4J_CONF} "$@"
