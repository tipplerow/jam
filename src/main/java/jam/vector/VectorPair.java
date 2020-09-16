
package jam.vector;

import jam.lang.ObjectPair;

/**
 * Contains an immutable pair of vectors.
 */
public final class VectorPair extends ObjectPair<VectorView, VectorView> {
    private VectorPair(VectorView first, VectorView second) {
        super(first, second);
    }

    /**
     * Returns a new vector pair.
     *
     * @param first the first vector of the pair.
     *
     * @param second the second vector of the pair.
     *
     * @return the paired vectors.
     */
    public static VectorPair of(VectorView first, VectorView second) {
        return new VectorPair(first, second);
    }
}
