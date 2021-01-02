
package jam.stoch.agent;

/**
 * Represents a birth process with a fixed first-order rate constant.
 */
public final class FixedRateBirthProc extends BirthProc {
    private final double rateConst;

    private FixedRateBirthProc(StochAgent parent, StochAgent child, double rateConst) {
        super(parent, child);

        validateRateConstant(rateConst);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new non-mutating birth process with a fixed rate
     * constant.
     *
     * @param agent the replicating agent in the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new non-mutating birth process with the specified
     * parameters.
     */
    public static BirthProc create(StochAgent agent, double rateConst) {
        return new FixedRateBirthProc(agent, agent, rateConst);
    }

    /**
     * Creates a new mutation process with a fixed rate constant.
     *
     * @param parent the parent agent for the process.
     *
     * @param child the child agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new mutation process with the specified parameters.
     */
    public static BirthProc create(StochAgent parent, StochAgent child, double rateConst) {
        return new FixedRateBirthProc(parent, child, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
