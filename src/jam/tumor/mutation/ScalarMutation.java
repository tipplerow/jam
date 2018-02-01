
package jam.tumor.mutation;

import jam.math.DoubleComparator;
import jam.math.DoubleRange;

/**
 * Represents mutations whose effects can be captured by a single
 * <em>selection coefficient</em> {@code s}, which multiplies the
 * carrier growth rate by a factor {@code (1 + s)}.
 */
public final class ScalarMutation extends Mutation {
    private final double selectionCoeff;

    /**
     * The validate range of selection coefficients.
     */
    public static final DoubleRange COEFF_RANGE = DoubleRange.open(-1.0, 1.0);

    /**
     * Creates a scalar mutation with a fixed selection coefficient.
     *
     * @param creationTime the index of the current time step (when 
     * the mutation is created).
     *
     * @param selectionCoeff the scalar selection coefficient.
     */
    public ScalarMutation(int creationTime, double selectionCoeff) {
        super(creationTime);

        validateSelectionCoeff(selectionCoeff);
        this.selectionCoeff = selectionCoeff;
    }

    private static void validateSelectionCoeff(double selectionCoeff) {
        if (!COEFF_RANGE.contains(selectionCoeff))
            throw new IllegalArgumentException("Invalid selection coefficient.");
    }

    /**
     * Returns the scalar selection coefficient for this mutation.
     *
     * @return the scalar selection coefficient for this mutation.
     */
    public double getSelectionCoeff() {
        return selectionCoeff;
    }

    @Override public GrowthRate apply(GrowthRate rate) {
        double B = rate.getBirthRate().doubleValue();
        double D = rate.getDeathRate().doubleValue();

        double delta = 0.5 * selectionCoeff * rate.getNetRate();

        double Bprime = B + delta;
        double Dprime = D - delta;

        return new GrowthRate(Bprime, Dprime);
    }

    @Override public boolean isIndependent() {
        return true;
    }

    @Override public boolean isNeutral() {
        return DoubleComparator.DEFAULT.isZero(selectionCoeff);
    }
}