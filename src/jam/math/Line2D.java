
package jam.math;

/**
 * Represents a line in a two-dimensional space.
 *
 * <p>Each line has a slope and a reference point lying on the line.
 * For vertical lines, the reference point is the x-intercept.  For
 * all other lines, the reference point is the y-intercept.
 */
public final class Line2D {
    //
    // The reference point is (x-intercept, 0) for vertical lines, 
    // (0, y-intercept) for all others...
    //
    private final Point2D point;
    private final double  slope;

    /**
     * Creates a new line from a point and a slope.
     *
     * @param point a point on the line.
     *
     * @param slope the slope of the line.
     */
    public Line2D(Point2D point, double slope) {
        this.point = computeReferencePoint(point, slope);
        this.slope = slope;
    }

    /**
     * Creates a new line passing through two points.
     *
     * @param pointA the first point on the line.
     *
     * @param pointB the second point on the line.
     *
     * @return the line passing through {@code pointA} and {@code pointB}.
     *
     * @throws IllegalArgumentException if the points are identical.
     */
    public static Line2D through(Point2D pointA, Point2D pointB) {
        return new Line2D(pointA, computeSlope(pointA, pointB));
    }

    /**
     * Computes the reference point for a line.
     *
     * <p>For vertical lines, the reference point is the x-intercept.
     * For all other lines, the reference point is the y-intercept.
     * 
     * @param point a point on the line.
     *
     * @param slope the slope of the line.
     *
     * @return the reference point for the line defined by the given
     * point and slope.
     */
    public static Point2D computeReferencePoint(Point2D point, double slope) {
        if (isVertical(slope))
            return Point2D.at(point.x, 0.0);
        else
            return Point2D.at(0.0, point.y - slope * point.x);
    }

    private static boolean isVertical(double slope) {
        return Double.isInfinite(slope);
    }

    /**
     * Computes the slope of the line passing through two points.
     *
     * @param pointA the first reference point on the line.
     *
     * @param pointB the second reference point on the line.
     *
     * @return the slope of the line passing through {@code pointA}
     * and {@code pointB}.
     *
     * @throws IllegalArgumentException if the points are identical.
     */
    public static double computeSlope(Point2D pointA, Point2D pointB) {
        if (pointA.equals(pointB))
            throw new IllegalArgumentException("Identical points.");

        double xa = pointA.x;
        double xb = pointB.x;

        double ya = pointA.y;
        double yb = pointB.y;

        if (DoubleComparator.DEFAULT.EQ(xa, xb))
            return Double.POSITIVE_INFINITY;
        else
            return (yb - ya) / (xb - xa);
    }

    /**
     * Creates a new horizontal line with a specified y-coordinate.
     *
     * @param y the y-coordinate of the horizontal line.
     *
     * @return a new horizontal line with the specified y-coordinate.
     */
    public static Line2D horizontal(double y) {
        return new Line2D(Point2D.at(0.0, y), 0.0);
    }

    /**
     * Creates a new vertical line with a specified x-coordinate.
     *
     * @param x the x-coordinate of the vertical line.
     *
     * @return a new vertical line with the specified x-coordinate.
     */
    public static Line2D vertical(double x) {
        return new Line2D(Point2D.at(x, 0.0), Double.POSITIVE_INFINITY);
    }

    /**
     * Performs a linear interpolation.
     *
     * @param pointA the first reference point on the line.
     *
     * @param pointB the second reference point on the line.
     *
     * @param x the point at which to compute the interpolation.
     *
     * @return the y-coordinate correspoinding to the given
     * x-coordinate.
     */
    public static double interpolate(Point2D pointA, Point2D pointB, double x) {
        Line2D line = through(pointA, pointB);
        return line.getY(x);
    }

    /**
     * Identifies points that lie on this line.
     *
     * @param pt a point to test.
     *
     * @return {@code true} iff the given point lies on this line.
     */
    public boolean contains(Point2D pt) {
        return contains(pt.x, pt.y);
    }
    
    /**
     * Identifies points that lie on this line.
     *
     * @param x the x-coordinate of the point to test.
     *
     * @param y the y-coordinate of the point to test.
     *
     * @return {@code true} iff the point {@code (x, y)} lies on this line.
     */
    public boolean contains(double x, double y) {
        if (isVertical())
            return DoubleComparator.DEFAULT.EQ(x, this.point.x);
        else
            return DoubleComparator.DEFAULT.EQ(y, this.getY(x));
    }
    
    /**
     * Returns a reference point on this line.
     *
     * @return a reference point on this line.
     */
    public Point2D getPoint() {
        return point;
    }

    /**
     * Returns the slope of this line.
     *
     * @return the slope of this line.
     */
    public double getSlope() {
        return slope;
    }

    /**
     * Returns the x-coordinate at a specified location on the y-axis.
     *
     * @param y the y-coordinate at which to evaluate the line.
     *
     * @return the x-coordinate at the given location on the y-axis,
     * or {@code Double.NaN} if the line is horizontal.
     */
    public double getX(double y) {
        if (isHorizontal())
            return Double.NaN;
        else
            return point.x + (y - point.y) / slope;
    }

    /**
     * Returns the y-coordinate at a specified location on the x-axis.
     *
     * @param x the x-coordinate at which to evaluate the line.
     *
     * @return the y-coordinate at the given location on the x-axis,
     * or {@code Double.NaN} if the line is vertical.
     */
    public double getY(double x) {
        if (isVertical())
            return Double.NaN;
        else
            return point.y + slope * (x - point.x);
    }

    /**
     * Identifies horizontal lines (those with zero slope).
     *
     * @return {@code true} iff this line is horizontal.
     */
    public boolean isHorizontal() {
        return DoubleComparator.DEFAULT.EQ(0.0, slope);
    }

    /**
     * Identifies vertical lines (those with infinite slope).
     *
     * @return {@code true} iff this line is vertical.
     */
    public boolean isVertical() {
        return isVertical(slope);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Line2D) && equalsLine((Line2D) that);
    }

    private boolean equalsLine(Line2D that) {
        if (this.isVertical())
            return that.isVertical() && this.contains(that.point);
        else
            return DoubleComparator.DEFAULT.EQ(this.slope, that.slope) && this.contains(that.point);
    }

    @Override public String toString() {
        if (isVertical())
            return "x = " + point.x;
        else if (isHorizontal())
            return "y = " + point.y;
        else
            return "y = " + point.y + formatSlope() + " * x";
    }

    private String formatSlope() {
        if (slope < 0.0)
            return " - " + -slope;
        else
            return " + " + slope;
    }
}
