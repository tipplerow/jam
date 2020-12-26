
package jam.stoch.agent;

import jam.lang.Ordinal;
import jam.lang.OrdinalIndex;

/**
 * Represents the discrete agents in a stochastic process.
 */
public abstract class StochAgent extends Ordinal {
    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    /**
     * Creates a new stochastic agent with a unique index that is
     * assigned automatically.
     */
    protected StochAgent() {
        super(ordinalIndex.next());
    }

    /**
     * Returns the unique integer index for this agent.
     *
     * @return the unique integer index for this agent.
     */
    public final int getAgentIndex() {
        return (int) getIndex();
    }
}
