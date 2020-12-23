
package jam.stoch.decay;

import jam.lang.JamException;
import jam.stoch.StochProc;
import jam.stoch.StochRate;
import jam.stoch.StochTime;

/**
 * Represents a first-order decay process.
 */
public final class DecayProc extends StochProc {
    private final int initPop;
    private final double rateConst;

    private int population;

    private DecayProc(int initPop, double rateConst) {
        validateInitPop(initPop);
        validateRateConst(rateConst);

        this.initPop = initPop;
        this.rateConst = rateConst;

        this.population = initPop;
    }

    private static void validateInitPop(int initPop) {
        if (initPop <= 0)
            throw JamException.runtime("Initial population must be positive.");
    }

    private static void validateRateConst(double rateConst) {
        if (rateConst <= 0.0)
            throw JamException.runtime("Decay rate constant must be positive.");
    }

    /**
     * Creates a new first-order decay process with a fixed rate
     * constant.
     *
     * @param initPop the initial population of the undecayed state.
     *
     * @param rateConst the unit rate constant for the process.
     *
     * @return a new first-order decay process with the specified
     * parameters.
     */
    public static DecayProc create(int initPop, double rateConst) {
        return new DecayProc(initPop, rateConst);
    }

    void decay() {
        --population;
    }

    /**
     * Computes the expected population of the undecayed state at a
     * particular time.
     *
     * @param time the elapsed time.
     *
     * @return the expected population of the undecayed state at the
     * specified time.
     */
    public int getExpectedPopulation(StochTime time) {
        return (int) Math.round(initPop * Math.exp(-rateConst * time.doubleValue()));
    }

    /**
     * Returns the initial population of the undecayed state.
     *
     * @return the initial population of the undecayed state.
     */
    public int getInitialPopulation() {
        return initPop;
    }

    /**
     * Returns the current population of the undecayed state.
     *
     * @return the current population of the undecayed state.
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Returns the unit rate constant for this process.
     *
     * @return the unit rate constant for this process.
     */
    public double getRateConst() {
        return rateConst;
    }

    @Override public StochRate getStochRate() {
        return StochRate.valueOf(population * rateConst);
    }
}
