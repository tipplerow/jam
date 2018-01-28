
package jam.math;

import org.apache.commons.math3.special.Gamma;

/**
 * Performs factorial and related calculations.
 */
public final class Factorial {
    private static int[] intValues = null;

    private Factorial() {
        // Prevent instantiation...
    }

    private static final double MAX_LOG = Math.log(Integer.MAX_VALUE);

    /**
     * Largest integer argument for which the factorial can be
     * represented as an {@code int} value.
     */
    public static final int MAX_INT_ARGUMENT = 12;

    /**
     * Computes the binomial coefficient: the number of ways to choose
     * a subset of size {@code k} (disregarding their order) from a set
     * of size {@code N}.
     *
     * @param N the number of items to choose from.
     *
     * @param k the number of items to choose.
     *
     * @return the binomial coefficient {@code C(N, k)}.
     *
     * @throws IllegalArgumentException if either argument is negative,
     * if {@code k > N}, or if the result is larger than the maximum
     * integer value.
     */
    public static int choose(int N, int k) {
        validateChoice(N, k);

        if (N <= MAX_INT_ARGUMENT)
            return chooseInt(N, k);

        double logValue = chooseLog(N, k);

        if (logValue > MAX_LOG)
            throw new IllegalArgumentException("The result is larger than the maximum integer value.");

        return (int) Math.rint(Math.exp(logValue));
    }

    private static void validateChoice(int N, int k) {
        if (N < 0 || k < 0 || k > N)
            throw new IllegalArgumentException(String.format("Invalid choice: (%d, %d)", N, k));
    }

    private static int chooseInt(int N, int k) {
        return intValue(N) / (intValue(N - k) * intValue(k));
    }

    /**
     * Computes the <em>logarithm</em> of a binomial coefficient.
     *
     * @param N the number of items to choose from.
     *
     * @param k the number of items to choose.
     *
     * @return the logarithm of the binomial coefficient {@code C(N, k)}.
     *
     * @throws IllegalArgumentException if either argument is negative
     * or if {@code k > N}.
     */
    public static double chooseLog(int N, int k) {
        validateChoice(N, k);
        return logValue(N) - (logValue(N - k) + logValue(k));
    }

    /**
     * Computes the exact factorial of an integer argument.
     *
     * <p>Note that this method may only be called with integers less
     * than or equal to {@code MAX_INT_ARGUMENT} so that the factorial
     * may be represented exactly as an {@code int} value.
     *
     * @param N the integer argument.
     *
     * @return the exact integer value of {@code N!}.
     *
     * @throws IllegalArgumentException unless the argument is in the
     * valid range {@code [0, MAX_INT_ARGUMENT]}.
     */
    public static int intValue(int N) {
        if (N < 0)
            throw new IllegalArgumentException("Negative argument.");

        if (N > MAX_INT_ARGUMENT)
            throw new IllegalArgumentException("Argument too large: no exact integer representation.");

        if (intValues == null)
            fillIntValues();

        return intValues[N];
    }

    private static void fillIntValues() {
        intValues = new int[MAX_INT_ARGUMENT + 1];
        intValues[0] = 1;

        for (int N = 1; N <= MAX_INT_ARGUMENT; N++)
            intValues[N] = N * intValues[N - 1];
    }

    /**
     * Computes the logarithm of a factorial.
     *
     * @param N the integer argument.
     *
     * @return the logarithm of {@code N!}.
     */
    public static double logValue(int N) {
        return Gamma.logGamma(N + 1);
    }

    /**
     * Computes the number of <em>partial</em> permutations: the
     * number of ways to arrange {@code k} objects in {@code N}
     * available positions (with {@code 0 <= k <= N}), equal to
     * {@code N! / (N - k)!}.
     *
     * @param N the number of available positions.
     *
     * @param k the number of items to place in position.
     *
     * @return the number of partial permutations: {@code N! / (N - k)!}.
     *
     * @throws IllegalArgumentException if either argument is negative
     * or if {@code k > N}.
     */
    public static int permute(int N, int k) {
        validateChoice(N, k);

        if (N <= MAX_INT_ARGUMENT)
            return permuteInt(N, k);

        double logValue = permuteLog(N, k);

        if (logValue > MAX_LOG)
            throw new IllegalArgumentException("The result is larger than the maximum integer value.");

        return (int) Math.rint(Math.exp(logValue));
    }

    private static int permuteInt(int N, int k) {
        return intValue(N) / intValue(N - k);
    }

    /**
     * Computes the <em>logarithm</em> of the number of partial
     * permutations.
     *
     * @param N the number of available positions.
     *
     * @param k the number of items to place in position.
     *
     * @return the logarithm of the number of partial permutations:
     * {@code log(N!) - log[(N - k)!]}.
     *
     * @throws IllegalArgumentException if either argument is negative
     * or if {@code k > N}.
     */
    public static double permuteLog(int N, int k) {
        validateChoice(N, k);
        return logValue(N) - logValue(N - k);
    }
}
