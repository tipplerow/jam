
package jam.solver;

import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

import jam.math.DoubleRange;
import jam.math.JamUnivariateFunction;

/**
 * Provides a base class for root-finding algorithms implemented in
 * the Apache Commons Math library.
 */
public abstract class ApacheUnivariateSolver implements JamUnivariateSolver {
    private static final int MAX_EVAL = 10000;

    /**
     * Creates the underlying solver from the Apache Commons Math
     * library.
     *
     * @return the underlying solver from the Apache Commons Math
     * library.
     */
    public abstract UnivariateSolver newSolver();

    @Override public UnivariateSolution solve(JamUnivariateFunction function, double init, DoubleRange range) {
        UnivariateSolver solver = newSolver();

        double solution =
            solver.solve(MAX_EVAL, function, range.getLowerBound(), range.getUpperBound(), init);

        return new UnivariateSolution(solution,
                                      solver.getAbsoluteAccuracy(),
                                      solver.getEvaluations());
    }
}
