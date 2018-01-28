
package jam.math;

import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Represents and computes generalized distances.
 */
public final class Distance {
    private Distance() {}

    /**
     * Computes a generalized distance between two points: the p-norm
     * of the vector difference between the points.
     *
     * @param v1 the coordinates of the first point.
     *
     * @param v2 the coordinates of the second point.
     *
     * @param index the index of the vector norm to apply to the
     * coordinate difference.
     *
     * @return the generalized distance between the points.
     *
     * @throws IllegalArgumentException unless the points have the
     * same dimensionality and the index is positive.
     */
    public static double compute(VectorView v1, VectorView v2, int index) {
        return StatUtil.normp(JamVector.minus(v1, v2), index);
    }

    /**
     * Computes the Euclidean distance between two points.
     *
     * @param v1 the coordinates of the first point.
     *
     * @param v2 the coordinates of the second point.
     *
     * @return the Euclidean distance between the points.
     *
     * @throws IllegalArgumentException unless the points have the
     * same dimensionality.
     */
    public static double euclidean(VectorView v1, VectorView v2) {
        return compute(v1, v2, 2);
    }

    /**
     * Computes the Manhattan distance between two points.
     *
     * @param v1 the coordinates of the first point.
     *
     * @param v2 the coordinates of the second point.
     *
     * @return the Manhattan distance between the points.
     *
     * @throws IllegalArgumentException unless the points have the
     * same dimensionality.
     */
    public static double manhattan(VectorView v1, VectorView v2) {
        return compute(v1, v2, 1);
    }
}
