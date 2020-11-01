
package jam.solver;

/**
 * Encapsulates the results of a univariate root-finding algorithm.
 */
public final class UnivariateSolution {
    private final int iterations;
    private final double accuracy;
    private final double solution;

    /**
     * Creates a new solution with fixed attributes.
     *
     * @param solution the solution to the equation (root).
     *
     * @param accuracy an estimate for the absolute accuracy of the
     * solution.
     *
     * @param iterations the number of iterations required to reach
     * the solution.
     */
    public UnivariateSolution(double solution, double accuracy, int iterations) {
        this.solution = solution;
        this.accuracy = accuracy;
        this.iterations = iterations;
    }

    /**
     * Returns the number of iterations required to reach the solution.
     *
     * @return the number of iterations required to reach the solution.
     */
    public int countIterations() {
        return iterations;
    }

    /**
     * Returns an estimate for the absolute accuracy of the solution.
     *
     * @return an estimate for the absolute accuracy of the solution.
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Returns the solution to the univariate eqution (the root).
     *
     * @return the solution to the univariate eqution (the root).
     */
    public double getSolution() {
        return solution;
    }
}
