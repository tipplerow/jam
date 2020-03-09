
package jam.vector;

import java.util.function.DoubleFunction;

/**
 * Applies local transformations to each element in a vector.
 */
public final class FunctionFilter extends LocalFilter {
    private final DoubleFunction<Double> function;

    /**
     * Creates a new local function filter.
     *
     * @param function the local {@code double}-to-{@code double}
     * transformation function.
     */
    public FunctionFilter(DoubleFunction<Double> function) {
        this.function = function;
    }

    @Override protected double filter(double value) {
        return function.apply(value);
    }
}
