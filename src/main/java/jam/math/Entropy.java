
package jam.math;

/**
 * Computes the (generalized) entropy for a distribution of states.
 */
public final class Entropy {
    private Entropy() {}

    /**
     * Computes the Shannon (base 2) information entropy for a
     * distribution of states.
     *
     * @param distribution the fractional population of each state.
     *
     * @return the Shannon (base 2) information entropy for the
     * specified distribution.
     *
     * @throws IllegalArgumentException unless the state distribution
     * is normalized.
     */
    public static double shannon(Probability[] distribution) {
        return shannon(distribution, 2.0);
    }

    /**
     * Computes the natural (base E) information entropy for a
     * distribution of states.
     *
     * @param distribution the fractional population of each state.
     *
     * @return the natural (base E) information entropy for the
     * specified distribution.
     *
     * @throws IllegalArgumentException unless the state distribution
     * is normalized.
     */
    public static double natural(Probability[] distribution) {
        return shannon(distribution, Math.E);
    }

    private static double shannon(Probability[] distribution, double logBase) {
        if (!Probability.isNormalized(distribution))
            throw new IllegalArgumentException("States are not fully populated.");

        double result = 0.0;

        for (Probability p : distribution) {
            double x = p.doubleValue();

            if (DoubleComparator.DEFAULT.isPositive(x))
                result += x * Math.log(x);
        }

        return -result / Math.log(logBase);
    }
}
