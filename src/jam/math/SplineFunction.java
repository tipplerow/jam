
package jam.math;

import java.util.Arrays;
import java.util.Collection;

import jam.vector.VectorUtil;

/**
 * Represents a univariate function defined by explicitly enumerated
 * knot points and a spline interpolation to compute values between
 * the knots.
 */
public abstract class SplineFunction extends AbstractUnivariateFunction {
    // Knot points sorted into ascending order by x-coordinate...
    private final Point2D[] knots;

    // X-coordinates of the sorted knot points, used for the binary
    // searches necessary for interpolation...
    private final double[] knotX;

    private SplineFunction(Point2D[] knots, boolean copy) {
        this.knots = copy ? Arrays.copyOf(knots, knots.length) : knots;

        sortKnots();
        validateKnots();

        this.knotX = assignKnotX();
    }

    private void sortKnots() {
        Arrays.sort(knots, Point2D.X_COMPARATOR);
    }

    private void validateKnots() {
        if (knots.length < 2)
            throw new IllegalArgumentException("At least two knot points are required.");

        for (int k = 1; k < knots.length; ++k) {
            double xj = knots[k - 1].x;
            double xk = knots[k].x;

            if (DoubleComparator.DEFAULT.GT(xj, xk))
                throw new IllegalArgumentException("Knot points are not ordered by x-coordinate.");

            if (DoubleComparator.DEFAULT.EQ(knots[k - 1].x, knots[k].x))
                throw new IllegalArgumentException("Duplicate knot point x-coordinate.");
        }
    }

    private double[] assignKnotX() {
        double[] xarr = new double[knots.length];

        for (int k = 0; k < knots.length; ++k)
            xarr[k] = knots[k].x;

        return xarr;
    }

    /**
     * Creates a new spline function from a sequence of knot points.
     *
     * @param knots the knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    protected SplineFunction(Point2D... knots) {
        this(knots, true);
    }

    /**
     * Creates a new spline function from a collection of knot points.
     *
     * @param knots the knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    protected SplineFunction(Collection<Point2D> knots) {
        this(knots.toArray(new Point2D[knots.size()]), false);
    }

    /**
     * Creates a new linear spline function from a sequence of knot
     * points.
     *
     * @param knots the knot points.
     *
     * @return the linear spline function for the given knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    public static SplineFunction linear(Point2D... knots) {
        return new LinearSpline(knots);
    }

    /**
     * Creates a new linear spline function from a collection of knot
     * points.
     *
     * @param knots the knot points.
     *
     * @return the linear spline function for the given knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    public static SplineFunction linear(Collection<Point2D> knots) {
        return new LinearSpline(knots);
    }

    /**
     * Finds the knot point that brackets a target x-coordinate from
     * below (as the lower bound of the range containing the target).
     *
     * @param x the target x-coordinate.
     *
     * @return the index of the knot that forms the lower bound of an
     * interval bracketing the target coordinate.
     */
    protected int knotLower(double x) {
        int index = VectorUtil.bracket(knotX, x);

        index = Math.max(0, index);
        index = Math.min(knots.length - 2, index);

        return index;
    }

    /**
     * Returns the number of knot points.
     *
     * @return the number of knot points.
     */
    public int countKnots() {
        return knots.length;
    }

    /**
     * Returns a knot point indexed by its position in the array of
     * knots.
     *
     * @param index the index of the knot point to return.
     *
     * @return the knot point at the specified position.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Point2D getKnot(int index) {
        return knots[index];
    }
}
