
package jam.math;

import java.util.Collection;

/**
 * Represents a univariate function defined by explicitly enumerated
 * knot points and a lineage interpolation to compute values between
 * the knots.
 */
public final class LinearSpline extends SplineFunction {
    /**
     * Creates a new linear spline function from a sequence of knot
     * points.
     *
     * @param knots the knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    public LinearSpline(Point2D... knots) {
        super(knots);
    }

    /**
     * Creates a new linear spline function from a collection of knot
     * points.
     *
     * @param knots the knot points.
     *
     * @throws IllegalArgumentException unless two or more unique knot
     * points are provided.
     */
    public LinearSpline(Collection<Point2D> knots) {
        super(knots);
    }

    /**
     * Evaluates the spline function at a target coordinate.
     *
     * @param x the target coordinate.
     *
     * @return the value of the spline function at the given target
     * coordinate.
     */
    @Override protected double evaluateInRange(double x) {
        int lowerIndex = knotLower(x);
        int upperIndex = lowerIndex + 1;

        Point2D lowerPt = getKnot(lowerIndex);
        Point2D upperPt = getKnot(upperIndex);

        return Line2D.through(lowerPt, upperPt).getY(x);
    }

    @Override public DoubleRange range() {
        return DoubleRange.INFINITE;
    }
}
