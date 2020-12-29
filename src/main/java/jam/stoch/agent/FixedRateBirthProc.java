
package jam.stoch.agent;

/**
 * Represents a birth process with a fixed first-order rate constant.
 */
public final class FixedRateBirthProc<A extends StochAgent> extends BirthProc<A> {
    private final double rateConst;

    private FixedRateBirthProc(A parent, A child, double rateConst) {
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
    public static <A extends StochAgent> BirthProc<A> create(A agent, double rateConst) {
        return new FixedRateBirthProc<A>(agent, agent, rateConst);
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
    public static <A extends StochAgent> BirthProc<A> create(A parent, A child, double rateConst) {
        return new FixedRateBirthProc<A>(parent, child, rateConst);
    }

    @Override public double getRateConstant(AgentSystem<A, ?> state) {
        return rateConst;
    }
}
