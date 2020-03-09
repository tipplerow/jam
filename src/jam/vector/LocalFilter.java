
package jam.vector;

import java.util.function.Function;

/**
 * Applies local transformations to each element in a vector.
 */
public abstract class LocalFilter extends VectorFilter {
    /**
     * Applies the filter transformation to a single element.
     *
     * @param value the vector element value to transform.
     *
     * @return the filtered element value.
     */
    protected abstract double filter(double value);

    @Override public final boolean isLocal() {
        return true;
    }

    @Override public final boolean isNonLocal() {
        return false;
    }

    @Override protected void filter(JamVector vector) {
        for (int index = 0; index < vector.length(); ++index)
            vector.set(index, filter(vector.get(index)));
    }
}
