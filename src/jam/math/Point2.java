
package jam.math;

/**
 * Represents a point in a two-dimensional space.
 */
public final class Point2 {
    private final double x;
    private final double y;

    private Point2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the two-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @param y the y-coordinate.
     *
     * @return the two-dimensional point at {@code (x, y)}.
     */
    public static Point2 at(double x, double y) {
        return new Point2(x, y);
    }

    /**
     * Returns the x-coordinate of this point.
     *
     * @return the x-coordinate of this point.
     */
    public double x() {
        return x;
    }

    /**
     * Returns the y-coordinate of this point.
     *
     * @return the y-coordinate of this point.
     */
    public double y() {
        return y;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Point2) && equalsPoint((Point2) that);
    }

    private boolean equalsPoint(Point2 that) {
        return DoubleComparator.DEFAULT.EQ(this.x, that.x) 
            && DoubleComparator.DEFAULT.EQ(this.y, that.y);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
