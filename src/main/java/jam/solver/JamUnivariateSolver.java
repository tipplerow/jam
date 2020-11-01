
package jam.solver;

import java.util.function.DoubleFunction;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

import jam.math.DoubleRange;
import jam.math.JamUnivariateFunction;

/**
 * Defines root-finding algorithms that solve univariate equations
 * iteratively.
 */
public interface JamUnivariateSolver {
    /**
     * Finds the root of a univariate function lying within a given
     * interval.
     *
     * @param function the univariate function to solve.
     *
     * @param init the initial value for the iterative root-finding
     * algorithm.
     *
     * @param range a range containing the solution.
     *
     * @return the solution and information about the execution of the
     * algorithm.
     *
     * @throws RuntimeException if a solution cannot be found.
     */
    public abstract UnivariateSolution solve(JamUnivariateFunction function, double init, DoubleRange range);

    /**
     * Finds the root of a univariate function lying within a given
     * interval.
     *
     * @param function the univariate function to solve.
     *
     * @param init the initial value for the iterative root-finding
     * algorithm.
     *
     * @param lower the lower bound of a range containing the solution.
     *
     * @param upper the upper bound of a range containing the solution.
     *
     * @return the solution and information about the execution of the
     * algorithm.
     *
     * @throws RuntimeException if a solution cannot be found.
     */
    public default UnivariateSolution solve(JamUnivariateFunction function, double init, double lower, double upper) {
        return solve(function, init, DoubleRange.closed(lower, upper));
    }

    /**
     * Finds the root of a univariate function lying within a given
     * interval.
     *
     * @param function the univariate function to solve.
     *
     * @param init the initial value for the iterative root-finding
     * algorithm.
     *
     * @param range a range containing the solution.
     *
     * @return the solution and information about the execution of the
     * algorithm.
     *
     * @throws RuntimeException if a solution cannot be found.
     */
    public default UnivariateSolution solve(DoubleFunction<Double> function, double init, DoubleRange range) {
        return solve(JamUnivariateFunction.lambda(function), init, range);
    }

    /**
     * Finds the root of a univariate function lying within a given
     * interval.
     *
     * @param function the univariate function to solve.
     *
     * @param init the initial value for the iterative root-finding
     * algorithm.
     *
     * @param lower the lower bound of a range containing the solution.
     *
     * @param upper the upper bound of a range containing the solution.
     *
     * @return the solution and information about the execution of the
     * algorithm.
     *
     * @throws RuntimeException if a solution cannot be found.
     */
    public default UnivariateSolution solve(DoubleFunction<Double> function, double init, double lower, double upper) {
        return solve(JamUnivariateFunction.lambda(function), init, DoubleRange.closed(lower, upper));
    }

    /**
     * A solver that implements the Van Wijngaarden-Dekker-Brent method,
     * the preferred method when function derivatives are not available.
     */
    public static final JamUnivariateSolver BRENT =
        new ApacheUnivariateSolver() {
            @Override public UnivariateSolver newSolver() {
                return new BrentSolver();
            }
        };
}
