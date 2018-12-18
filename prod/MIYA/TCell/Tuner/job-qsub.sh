#!/bin/sh
########################################################################
# Execute one trial in the thymic selection simulation.
#
# Usage: qsub job-qsub.sh BLOCK_INDEX TRIAL_INDEX
########################################################################

#SBATCH --job-name="Neg4"
#SBATCH --nodes=1
#SBATCH --ntasks=1

#SBATCH --cpus-per-task=1
#SBATCH --mem=4G
#SBATCH --time=1:00:00

#SBATCH --mail-user=jsshaff@stanford.edu
#SBATCH --mail-type=FAIL

#SBATCH --workdir=/N/users/jsshaff/SimWork/MIYA/T6/Tuner/Neg4

# --------------------------------------------------------------------

if [ $# -ne 2 ]
then
    echo "Usage:" `basename $0` "BLOCK_INDEX TRIAL_INDEX"
    exit 1
fi

. $HOME/.bash_profile

if [ -z "$JAM_HOME" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

if [ -z "$JAVA_HOME" ]
then
    echo "Environment variable JAVA_HOME is not set; exiting."
    exit 1
fi

WORK_DIR=/N/users/jsshaff/SimWork/MIYA/T6/Tuner/Neg4

${JAM_HOME}/bin/job-local.sh $WORK_DIR "$@" jam.tcell.miya.MiyaTuner miya.prop
