
package jam.stoch;

import java.util.Collection;

import jam.math.JamRandom;

/**
 * Implements the direct stochastic simulation method of Gillespie
 * with a few performance optimizations.
 */
public final class DirectAlgo extends StochAlgo {
    private final RateManager rateManager;
    private final PriorityList priorityList;

    private DirectAlgo(JamRandom random, StochSystem system) {
        super(random, system);

        this.rateManager = RateManager.create(system);
        this.priorityList = PriorityList.create(system);
    }

    /**
     * Creates a new stochastic simulation algorithm that implements
     * the original <em>direct method</em> of Gillespie (with a few
     * performance optimizations).
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a direct simulation algorithm for the specified system.
     */
    public static DirectAlgo create(JamRandom random, StochSystem system) {
        return new DirectAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        StochRate totalRate =
            rateManager.getTotalRate();

        return StochEvent.mark(nextProc(totalRate),
                               nextTime(totalRate));
    }

    private StochProc nextProc(StochRate totalRate) {
        return priorityList.select(random, totalRate);
    }

    private StochTime nextTime(StochRate totalRate) {
        return totalRate.sampleTime(system.lastEventTime(), random);
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        rateManager.updateTotalRate(event.getProcess(), dependents);
    }
}
