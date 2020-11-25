
package jam.stoch;

import java.util.Collection;

import jam.math.JamRandom;

/**
 * Implements the direct stochastic simulation method of Gillespie
 * with a few performance optimizations.
 */
public final class DirectAlgo<P extends StochProc> extends StochAlgo<P> {
    private final RateManager<P> rateManager;
    private final PriorityList<P> priorityList;

    private DirectAlgo(JamRandom random, StochSystem<P> system) {
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
    public static <P extends StochProc> DirectAlgo<P> create(JamRandom random, StochSystem<P> system) {
        return new DirectAlgo<P>(random, system);
    }

    @Override protected StochEvent<P> nextEvent() {
        StochRate totalRate =
            rateManager.getTotalRate();

        return StochEvent.mark(nextProc(totalRate),
                               nextTime(totalRate));
    }

    private P nextProc(StochRate totalRate) {
        return priorityList.select(random, totalRate);
    }

    private StochTime nextTime(StochRate totalRate) {
        return totalRate.sampleTime(getEventTime(), random);
    }

    @Override protected void updateState(Collection<P> changed) {
        rateManager.updateTotalRate(changed);
    }
}
