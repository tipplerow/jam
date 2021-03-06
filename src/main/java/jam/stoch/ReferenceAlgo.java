
package jam.stoch;

import java.util.Collection;

import jam.lang.JamException;
import jam.math.DoubleComparator;
import jam.math.JamRandom;

/**
 * Implements the direct stochastic simulation method of Gillespie
 * without any optimizations.
 *
 * <p>The calculation time per step scales linearly with the number
 * of stochastic processes (reactions or pathways).  This algorithm
 * is unlikely to be the best choice for real-world problems; it is
 * provided primarily as a baseline against which to benchmark more
 * efficient algorithms.
 */
public final class ReferenceAlgo extends StochAlgo {
    private ReferenceAlgo(JamRandom random, StochSystem system) {
        super(random, system);
    }

    /**
     * Creates a new reference simulation algorithm that implements
     * the original <em>direct method</em> of Gillespie without any
     * efficiency optimizations.
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a reference simulation algorithm for the specified
     * stochastic system.
     */
    public static ReferenceAlgo create(JamRandom random, StochSystem system) {
        return new ReferenceAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        StochRate totalRate = StochProc.computeTotalRate(system.viewProcesses());
        return StochEvent.mark(nextProc(totalRate), nextTime(totalRate));
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        //
        // This algorithm does not maintain any internal state
        // variables (the total reaction rate is recomputed at
        // every time step), so there is nothing to update...
        //
    }

    private StochProc nextProc(StochRate totalRate) {
        //
        // Accumulate the process rates until we find one greater than
        // U * totalRate, where U is a uniform random deviate on [0, 1]...
        //
        double procTotal = 0.0;
        double threshold = random.nextDouble() * totalRate.doubleValue();

        for (StochProc proc : system.viewProcesses()) {
            procTotal += proc.getStochRate().doubleValue();

            if (DoubleComparator.DEFAULT.GE(procTotal, threshold))
                return proc;
        }

        // This should never happen...
        throw JamException.runtime("Next process selection failed.");
    }

    private StochTime nextTime(StochRate totalRate) {
        return totalRate.sampleTime(system.lastEventTime(), random);
    }
}

