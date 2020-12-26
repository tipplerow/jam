
package jam.stoch.decay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.lang.JamException;
import jam.stoch.StochEvent;
import jam.stoch.StochSystem;

/**
 * Represents a stochastic system with a fixed number of independent
 * first-order decay processes.  This system has a simple analytical
 * solution and is provided primarily to test stochastic simulation
 * algorithms.
 */
public final class DecaySystem extends StochSystem<DecayProc> {
    private DecaySystem(List<DecayProc> procs) {
        super(procs, List.of());
    }

    /**
     * Creates a new stochastic system with a fixed collection of
     * independent first-order decay processes.
     *
     * @param pops the initial populations of the undecayed states.
     *
     * @param rates the unit rate constants for each decay process.
     *
     * @return the stochastic system defined by the input parameters.
     *
     * @throws IllegalArgumentException unless the parameter arrays
     * have equal length and all populations and rate constants are
     * positive.
     */
    public static DecaySystem create(int[] pops, double[] rates) {
        return new DecaySystem(createProcs(pops, rates));
    }

    private static List<DecayProc> createProcs(int[] pops, double[] rates) {
        if (pops.length < 1)
            throw JamException.runtime("At least one process must be defined.");

        if (pops.length != rates.length)
            throw JamException.runtime("Populations and rates are not consistent.");

        List<DecayProc> procs = new ArrayList<DecayProc>(pops.length);

        for (int index = 0; index < pops.length; ++index)
            procs.add(DecayProc.create(pops[index], rates[index]));

        return procs;
    }

    @Override public void updateState() {
        //
        // All decay processes are independent...
        //
        lastEventProcess().decay();
    }
}
