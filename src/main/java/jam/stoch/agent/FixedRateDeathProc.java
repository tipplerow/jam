
package jam.stoch.agent;

/**
 * Represents a death process with a fixed first-order rate constant.
 */
public final class FixedRateDeathProc<A extends StochAgent> extends DeathProc<A> {
    private final double rateConst;

    private FixedRateDeathProc(A agent, double rateConst) {
        super(agent);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new death process with a fixed rate constant.
     *
     * @param agent the target agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new death process with the specified parameters.
     */
    public static <A extends StochAgent> DeathProc<A> create(A agent, double rateConst) {
        return new FixedRateDeathProc<A>(agent, rateConst);
    }

    @Override public double getRateConstant(AgentSystem<A, ?> state) {
        return rateConst;
    }
}
