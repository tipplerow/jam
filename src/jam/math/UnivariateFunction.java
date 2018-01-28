
package jam.math;

/**
 * Represents a real-valued function of a single floating-point
 * argument.
 */
public interface UnivariateFunction {
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
}
