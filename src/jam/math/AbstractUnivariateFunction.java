
package jam.math;

/**
 * Provides standard argument checking for univariate functions.
 */
public abstract class AbstractUnivariateFunction implements UnivariateFunction {
    /**
     * Evaluates this function at the specified location, which is
     * guaranteed to be in the valid range.
     *
     * @param x the location (within the valid range) at which to
     * evaluate the function.
     *
     * @return the value of this function at the specified location.
     */
    protected abstract double evaluateInRange(double x);

    @Override public double evaluate(double x) {
        range().validate(x);
        return evaluateInRange(x);
    }
}
