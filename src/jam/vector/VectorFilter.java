
package jam.vector;

import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Applies a transformation to each element in a vector.
 *
 * <p>Filters take a vector as input and return the same vector (for
 * operator chaining) with the elements modified in place.
 */
public abstract class VectorFilter implements Function<JamVector, JamVector> {
    /**
     * A local filter that takes the absolute value of each element.
     */
    public static final VectorFilter ABS = local(Math::abs);

    /**
     * A non-local filter that subtracts the vector mean, returning
     * the vector with zero mean (but same variance).
     */
    public static final VectorFilter DEMEAN = new NonLocalFilter() {
            @Override protected void filter(JamVector vector) {
                vector.subtract(VectorAggregator.mean(vector));
            }
        };

    /**
     * A local filter that takes the natural logarithm of each
     * element.
     */
    public static final VectorFilter LOG = local(Math::log);

    /**
     * A non-local filter that subtracts the vector mean and then
     * divides by the standard deviation, returning the vector with
     * zero mean and unit variance.
     */
    public static final VectorFilter ZSCORE = new NonLocalFilter() {
            @Override protected void filter(JamVector vector) {
                vector.subtract(VectorAggregator.mean(vector));
                vector.divide(VectorAggregator.sd(vector));
            }
        };

    /**
     * Creates a filter that applies a local transformation to each
     * vector element.
     *
     * @param function the local {@code double}-to-{@code double}
     * transformation function.
     *
     * @return a filter that applies the specified local transformation.
     */
    public static VectorFilter local(DoubleFunction<Double> function) {
        return new FunctionFilter(function);
    }

    /**
     * Applies this filter to a vector, which is modified in place.
     *
     * @param vector the vector to filter.
     *
     * @return the aggregate value.
     */
    public final JamVector apply(JamVector vector) {
        filter(vector);
        return vector;
    }

    /**
     * Distinguishes local and non-local filters.
     *
     * <p><em>Local</em> filters operate on each element independently,
     * while <em>non-local</em> filters use aggregate vector properties
     * in their transformation.
     *
     * @return {@code true} iff this is a local filter.
     */
    public abstract boolean isLocal();

    /**
     * Distinguishes local and non-local filters.
     *
     * <p><em>Local</em> filters operate on each element independently,
     * while <em>non-local</em> filters use aggregate vector properties
     * in their transformation.
     *
     * @return {@code true} iff this is a non-local filter.
     */
    public boolean isNonLocal() {
        return !isLocal();
    }

    /**
     * Applies this filter to a vector (modifies it in place).
     *
     * @param vector the vector to filter.
     */
    protected abstract void filter(JamVector vector);
}
