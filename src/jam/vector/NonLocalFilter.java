
package jam.vector;

/**
 * Applies non-local transformations to each element in a vector.
 */
public abstract class NonLocalFilter extends VectorFilter {
    @Override public final boolean isLocal() {
        return false;
    }

    @Override public final boolean isNonLocal() {
        return true;
    }
}
