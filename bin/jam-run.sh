#!/bin/sh
########################################################################
# Usage: jam-run.sh PROJECT_HOME [-D<name>=<value> ...] class [ARGUMENTS]
########################################################################

SCRIPT=`basename $0`

if [ $# -lt 2 ]
then
    echo "Usage: $SCRIPT PROJECT_HOME [-D<name>=<value> ...] class [ARGUMENTS]"
    exit 1
fi

if [ -z "${JAM_HOME}" ]
then
    echo "Environment variable JAM_HOME must be set; exiting."
    exit 1
fi

PROJECT_HOME=$1
shift

. ${JAM_HOME}/bin/shell.lib

AddLib ${JAM_HOME}/jlib
AddLib ${JAM_HOME}/lib
AddLib ${PROJECT_HOME}/lib

export CLASSPATH=$CLASSPATH
LOG4J_CONF=${PROJECT_HOME}/conf/log4j.xml

java -Dlog4j.configurationFile=${LOG4J_CONF} "$@"
