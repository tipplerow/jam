
package jam.stoch.agent;

/**
 * Represents a transition process with a fixed first-order rate constant.
 */
public final class FixedRateTransitionProc extends TransitionProc {
    private final double rateConst;

    private FixedRateTransitionProc(StochAgent reactant, StochAgent product, double rateConst) {
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
    public static TransitionProc create(StochAgent reactant, StochAgent product, double rateConst) {
        return new FixedRateTransitionProc(reactant, product, rateConst);
    }

    @Override public double getRateConstant(AgentSystem system) {
        return rateConst;
    }
}
