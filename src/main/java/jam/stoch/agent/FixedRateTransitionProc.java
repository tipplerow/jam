
package jam.stoch.agent;

/**
 * Represents a transition process with a fixed first-order rate constant.
 */
public final class FixedRateTransitionProc<A extends StochAgent> extends TransitionProc<A> {
    private final double rateConst;

    private FixedRateTransitionProc(A reactant, A product, double rateConst) {
        super(reactant, product);

        validateRateConstant(rateConst);
        this.rateConst = rateConst;
    }

    /**
     * Creates a new transition process with a fixed rate constant.
     *
     * @param reactant the reactant agent for the process.
     *
     * @param product the product agent for the process.
     *
     * @param rateConst the fixed rate constant for the process.
     *
     * @return a new transition process with the specified parameters.
     */
    public static <A extends StochAgent> TransitionProc<A> create(A reactant, A product, double rateConst) {
        return new FixedRateTransitionProc<A>(reactant, product, rateConst);
    }

    @Override public double getRateConstant(AgentSystem<A, ?> state) {
        return rateConst;
    }
}
