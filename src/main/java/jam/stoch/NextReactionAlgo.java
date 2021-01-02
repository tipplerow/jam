
package jam.stoch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.math.JamRandom;

/**
 * Implements the <em>next reaction</em> stochastic simulation method
 * of Gibson and Bruck [J. Phys. Chem. A (2000) 104, 1876-1889]. 
 */
public final class NextReactionAlgo extends StochAlgo {
    private final EventQueue eventQueue;

    private NextReactionAlgo(JamRandom random, StochSystem system) {
        super(random, system);
        this.eventQueue = EventQueue.create(StochEvent.first(system, random));
    }

    /**
     * Creates a new stochastic simulation algorithm that implements
     * the <em>next reaction</em> method of Gibson and Bruck [see
     * J. Phys. Chem. A (2000) 104, 1876-1889].
     *
     * @param random the random number source.
     *
     * @param system the stochastic system to simulate.
     *
     * @return a next-reaction simulation algorithm for the specified
     * system.
     */
    public static NextReactionAlgo create(JamRandom random, StochSystem system) {
        return new NextReactionAlgo(random, system);
    }

    @Override protected StochEvent nextEvent() {
        return eventQueue.nextEvent();
    }

    @Override protected void updateState(StochEvent event, Collection<? extends StochProc> dependents) {
        eventQueue.updateEvent(event.next(random));

        for (StochProc dependent : dependents) {
            StochEvent prevEvent = eventQueue.findEvent(dependent);
            StochEvent nextEvent = prevEvent.update(event, random);

            eventQueue.updateEvent(nextEvent);
        }
    }
}
