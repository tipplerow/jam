
package jam.math;

import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Implements standard distance metrics.
 */
public enum Distance {
    /**
     * The Euclidean (L2-norm) distance.
     */
    EUCLIDEAN {
        @Override protected double computeChecked(VectorView v1, VectorView v2) {
            double sumsqr = 0.0;

            for (int k = 0; k < v1.length(); ++k) {
                double dxk = v1.getDouble(k) - v2.getDouble(k);
                sumsqr += dxk * dxk;
            }

            return Math.sqrt(sumsqr);
        }
    },

    /**
     * The Manhattan (L1-norm) distance.
     */
    MANHATTAN {
        @Override protected double computeChecked(VectorView v1, VectorView v2) {
            double sumabs = 0.0;

            for (int k = 0; k < v1.length(); ++k)
                sumabs += Math.abs(v1.getDouble(k) - v2.getDouble(k));

            return sumabs;
        }
    };

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
     * Computes the metric distance between two points.
     *
     * @param v1 the coordinates of the first point.
     *
     * @param v2 the coordinates of the second point.
     *
     * @return the distance between the points.
     *
     * @throws IllegalArgumentException unless the points have the
     * same dimensionality.
     */
    public double compute(VectorView v1, VectorView v2) {
        if (v1.length() != v2.length())
            throw new IllegalArgumentException("Incongruent vectors.");

        return computeChecked(v1, v2);
    }

    /**
     * Computes the metric distance between two points (with assured
     * congruent dimensionality).
     *
     * @param v1 the coordinates of the first point.
     *
     * @param v2 the coordinates of the second point.
     *
     * @return the distance between the points.
     */
    protected abstract double computeChecked(VectorView v1, VectorView v2);
}
