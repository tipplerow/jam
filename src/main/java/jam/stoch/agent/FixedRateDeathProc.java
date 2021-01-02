
package jam.stoch.agent;

/**
 * Represents a death process with a fixed first-order rate constant.
 */
public final class FixedRateDeathProc extends DeathProc {
    private final double rateConst;

    private FixedRateDeathProc(StochAgent agent, double rateConst) {
        super(agent);

        validateRateConstant(rateConst);
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
    public static DeathProc create(StochAgent agent, double rateConst) {
        return new FixedRateDeathProc(agent, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
