
package jam.solver;

import org.junit.*;
import static org.junit.Assert.*;

public class JamUnivariateSolverTest {
    private static final double TOLERANCE = 1.0E-06;

    @Test public void testBrent() {
        runTest(JamUnivariateSolver.BRENT);
    }

    private void runTest(JamUnivariateSolver solver) {
        UnivariateSolution solution;

        solution = solver.solve(x -> Math.log(x) - 1.0, 1.0, 0.01, 100.0);
        assertEquals(Math.E, solution.getSolution(), TOLERANCE);

        solution = solver.solve(x -> Math.exp(x) - 1.0, -1.0, -10, 10.0);
        assertEquals(0.0, solution.getSolution(), TOLERANCE);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.solver.JamUnivariateSolverTest");
    }
}
