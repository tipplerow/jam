
package jam.math;

import java.util.function.DoubleFunction;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Represents a real-valued function of a single floating-point
 * argument.
 */
public interface JamUnivariateFunction extends UnivariateFunction {
    /**
     * Evaluates this function at the specified location.
     *
     * @param x the location at which to evaluate the function.
     *
     * @return the value of this function at the specified location.
     *
     * @throws IllegalArgumentException unless the input argument is
     * in the range over which the function is defined.
     */
    public abstract double evaluate(double x);

    /**
     * Returns the range over which this function is defined.
     *
     * @return the range over which this function is defined.
     *
     * @throws RuntimeException unless this function has a single
     * continuous range.
     */
    public abstract DoubleRange range();

    public static JamUnivariateFunction lambda(DoubleFunction<Double> function) {
        return new LambdaFunction(function);
    }

    @Override public default double value(double x) {
        return evaluate(x);
    }
}

final class LambdaFunction implements JamUnivariateFunction {
    private final DoubleFunction<Double> function;

    LambdaFunction(DoubleFunction<Double> function) {
        this.function = function;
    }

    @Override public double evaluate(double x) {
        return function.apply(x);
    }

    @Override public DoubleRange range() {
        return DoubleRange.INFINITE;
    }
}
