
package jam.tumor.mutation;

import jam.math.DoubleComparator;
import jam.math.JamRandom;
import jam.math.Probability;

/**
 * Defines the birth (division) rate, death rate, and net growth rate
 * of a cell or other biological entity.
 *
 * <p>This class is defined in the context of discrete-time simulation
 * where at most one birth (division) or death event may occur at a
 * single time step.
 */
public final class GrowthRate {
    private final Probability birthRate;
    private final Probability deathRate;

    // Cumulative probability distribution for the set of possible
    // events...
    private final double[] eventCDF;

    // Event indexes corresponding to elements in the "eventCDF" array...
    private static final int BIRTH_EVENT = 0;
    private static final int DEATH_EVENT = 1;
    private static final int NO_EVENT    = 2;

    /**
     * A growth rate with zero net growth rate and unit event rate:
     * birth and death probabilities are both one-half.
     */
    public static GrowthRate NO_GROWTH = noGrowth(1.0);

    /**
     * Creates a new growth rate with fixed birth and death components.
     *
     * @param birthRate the probability of cell division in the next
     * time step.
     *
     * @param deathRate the probability of cell death in the next time
     * step.
     *
     * @throws IllegalArgumentException if the sum of the birth and
     * death rates exceeds one.
     */
    public GrowthRate(double birthRate, double deathRate) {
        this(Probability.valueOf(birthRate), Probability.valueOf(deathRate));
    }

    /**
     * Creates a new growth rate with fixed birth and death components.
     *
     * @param birthRate the probability of cell division in the next
     * time step.
     *
     * @param deathRate the probability of cell death in the next time
     * step.
     *
     * @throws IllegalArgumentException if the sum of the birth and
     * death rates exceeds one.
     */
    public GrowthRate(Probability birthRate, Probability deathRate) {
        validate(birthRate, deathRate);

        this.birthRate = birthRate;
        this.deathRate = deathRate;
        this.eventCDF  = computeCDF(birthRate, deathRate);
    }

    private static double[] computeCDF(Probability birthRate, Probability deathRate) {
        double[] CDF = new double[3];

        CDF[BIRTH_EVENT] = birthRate.doubleValue();
        CDF[DEATH_EVENT] = birthRate.doubleValue() + deathRate.doubleValue();
        CDF[NO_EVENT]    = 1.0;

        return CDF;
    }

    /**
     * Creates a new growth rate with a specified target net rate and
     * unit total event rate.
     *
     * @param netRate the desired net rate.
     *
     * @return a new growth rate with birth rate {@code b = (1 + r) / 2}
     * and death rate {@code d = (1 - r) / 2}, where {@code r} is the net
     * growth rate.
     *
     * @throws IllegalArgumentException unless the target growth rate
     * is in the valid range {@code [-1, 1]}.
     */
    public static GrowthRate net(double netRate) {
        double birth = 0.5 * (1 + netRate);
        double death = 0.5 * (1 - netRate);

        GrowthRate newRate = new GrowthRate(birth, death);
        assert newRate.getEventRate().equals(Probability.ONE);

        return newRate;
    }

    /**
     * Creates a new growth rate with zero birth rate and a specified
     * death rate.
     *
     * @param deathRate the desired death rate.
     *
     * @return a new growth rate with zero birth rate and the given
     * death rate.
     *
     * @throws IllegalArgumentException unless the input argument is a
     * valid probability.
     */
    public static GrowthRate noBirth(double deathRate) {
        return new GrowthRate(0.0, deathRate);
    }

    /**
     * Creates a new growth rate with zero net population growth rate
     * and a specified overall event rate.
     *
     * @param eventProb the desired event probability.
     *
     * @return a new growth rate with {@code b = d = p / 2} where
     * {@code b} is the birth rate, {@code d} the death rate and
     * {@code p} the overall event rate.
     *
     * @throws IllegalArgumentException unless the input argument is a
     * valid probability.
     */
    public static GrowthRate noGrowth(double eventProb) {
        return new GrowthRate(0.5 * eventProb, 0.5 * eventProb);
    }

    /**
     * Validates a pair of birth and death rates.
     *
     * @param birthRate the probability of cell division in the next
     * time step.
     *
     * @param deathRate the probability of cell death in the next time
     * step.
     *
     * @throws IllegalArgumentException if the sum of the birth and
     * death rates exceeds one.
     */
    public static void validate(Probability birthRate, Probability deathRate) {
        Probability.validate(birthRate.doubleValue() + deathRate.doubleValue());
    }

    /**
     * Computes the expected number of cell divisions and deaths in a
     * population.
     *
     * <p>Note that this is a <em>semi-stochastic</em> calculation,
     * with a random number source used only to discretize the exact
     * expectation value.
     *
     * @param population the number of entities having this growth
     * rate.
     *
     * @return the expected number of cell divisions and deaths.
     */
    public GrowthCount compute(int population) {
        double eventRate = birthRate.doubleValue() + deathRate.doubleValue();
        double birthFrac = birthRate.doubleValue() / eventRate;

        int eventCount = JamRandom.global().discretize(population * eventRate);
        int birthCount = JamRandom.global().discretize(eventCount * birthFrac);
        int deathCount = eventCount - birthCount;

        return new GrowthCount(birthCount, deathCount);
    }

    /**
     * Samples the realized number of cell divisions and deaths in a
     * population.
     *
     * <p>Note that this is a <em>fully stochastic</em> calculation,
     * with a random number drawn for each member of the population.
     *
     * @param population the number of entities having this growth
     * rate.
     *
     * @return the realized number of cell divisions.
     */
    public GrowthCount sample(int population) {
        int birthCount = 0;
        int deathCount = 0;

        for (int trial = 0; trial < population; ++trial) {
            int eventIndex = JamRandom.global().selectCDF(eventCDF);

            if (eventIndex == BIRTH_EVENT)
                ++birthCount;
            else if (eventIndex == DEATH_EVENT)
                ++deathCount;
        }

        return new GrowthCount(birthCount, deathCount);
    }

    /**
     * Returns the probability of cell division in the next
     * time step.
     *
     * @return the probability of cell division in the next
     * time step.
     */
    public Probability getBirthRate() {
        return birthRate;
    }

    /**
     * Returns the probability of cell death in the next time
     * step.
     *
     * @return the probability of cell death in the next time
     * step.
     */
    public Probability getDeathRate() {
        return deathRate;
    }

    /**
     * Returns the probability of cell division <em>or</em> death in
     * the next time step.
     *
     * @return the probability of cell division <em>or</em> death in
     * the next time step.
     */
    public Probability getEventRate() {
        return birthRate.or(deathRate);
    }

    /**
     * Returns the (non-negative) population growth factor.
     *
     * <p>Let {@code N(t)} be the population at time {@code t} and
     * let {@code g} be the population growth factor; the expected
     * population at time {@code t + 1} is {@code g * N(t)}.
     *
     * <p>The population growth factor {@code g} may be computed 
     * from the birth rate {@code b} and death rate {@code d} as:
     * {@code g = 1 + b - d}.
     *
     * <p>Note that {@code g} is bounded on the interval {@code [0, 2]}.
     *
     * @return the (non-negative) population growth factor.
     */
    public double getGrowthFactor() {
        return 1.0 + getNetRate();
    }

    /**
     * Returns the net growth rate: {@code b - d}, where {@code b} is
     * the birth rate and {@code d} is the death rate.
     *
     * @return the net growth rate.
     */
    public double getNetRate() {
        return birthRate.doubleValue() - deathRate.doubleValue();
    }

    /**
     * Returns a new growth rate with the same death rate as this rate
     * but a zero birth rate.
     *
     * @return a new growth rate with the same death rate as this rate
     * but a zero birth rate.
     */
    public GrowthRate noBirth() {
        return new GrowthRate(Probability.ZERO, this.deathRate);
    }

    /**
     * Returns a new growth rate with the same overall event rate as
     * this rate but allowing no net population growth.
     *
     * <p>Growth rates with population growth factors less than or
     * equal to 1.0 are left unchanged.  In all other growth rates,
     * the birth and death rates are adjusted so that the population
     * growth factor becomes 1.0 while the overall event rate is
     * unchanged.
     *
     * @return a new growth rate allowing no net population growth.
     */
    public GrowthRate noGrowth() {
        if (this.getGrowthFactor() <= 1.0)
            return this;

        double B = birthRate.doubleValue();
        double D = deathRate.doubleValue();

        double Bprime = 0.5 * (B + D);
        double Dprime = Bprime;

        assert DoubleComparator.DEFAULT.EQ(B + D, Bprime + Dprime);
        return new GrowthRate(Bprime, Dprime);
    }

    /**
     * Returns a new growth rate with the same death rate as this but
     * with the birth rate multiplied by a scalar factor.
     *
     * @param scalar the factor by which the birth rate will be
     * multiplied.
     *
     * @return the rescaled growth rate.
     *
     * @throws IllegalArgumentException if the resulting birth rate is
     * outside the allowed range.
     */
    public GrowthRate rescaleBirthRate(double scalar) {
        return new GrowthRate(birthRate.times(scalar), deathRate);
    }

    /**
     * Returns a new growth rate with the same birth rate as this but
     * with the death rate multiplied by a scalar factor.
     *
     * @param scalar the factor by which the death rate will be
     * multiplied.
     *
     * @return the rescaled growth rate.
     *
     * @throws IllegalArgumentException if the resulting death rate is
     * outside the allowed range.
     */
    public GrowthRate rescaleDeathRate(double scalar) {
        return new GrowthRate(birthRate, deathRate.times(scalar));
    }

    /**
     * Rescales the population growth factor while maintining the same
     * overall event rate.
     *
     * <p>Let {@code b} be the birth rate, {@code d} the death rate,
     * {@code g = 1 + b - d} the population growth factor, and {@code f}
     * the rescale fraction.  This method returns a new object with
     * growth factor {@code g' = 1 + b' - d' = f * g}, where {@code b'}
     * is the new birth rate, {@code d'} the new death rate, and
     * {@code b + d = b' + d'}.
     *
     * @param scalar the scalar value by which the population growth
     * factor will change.
     *
     * @return the rescaled growth rate.
     *
     * @throws IllegalArgumentException if the rescale value is either
     * negative or large enough to create an invalid growth rate.  The
     * range of valid rescale values is {@code [0.0, 2.0 / g]}, where
     * {@code g} is the current growth factor.
     */
    public GrowthRate rescaleGrowthFactor(double scalar) {
        double B = birthRate.doubleValue();
        double D = deathRate.doubleValue();

        double sm1 = scalar - 1.0;
        double sp1 = scalar + 1.0;

        double Bprime = 0.5 * ( sm1 + sp1 * B - sm1 * D);
        double Dprime = 0.5 * (-sm1 - sm1 * B + sp1 * D);

        assert DoubleComparator.DEFAULT.EQ(B + D, Bprime + Dprime);
        return new GrowthRate(Bprime, Dprime);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof GrowthRate) && equalsGrowthRate((GrowthRate) that);
    }

    private boolean equalsGrowthRate(GrowthRate that) {
        return this.birthRate.equals(that.birthRate)
            && this.deathRate.equals(that.deathRate);
    }

    @Override public String toString() {
        return "GrowthRate(B = " + birthRate.doubleValue() + ", D = " + deathRate.doubleValue() + ")";
    }
}