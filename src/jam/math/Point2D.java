
package jam.math;

import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * Represents an immutable point in a two-dimensional space.
 */
public final class Point2D implements Point {
    /** The immutable x-coordinate. */
    public final double x;

    /** The immutable y-coordinate. */
    public final double y;

    /**
     * Creates a new two-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @param y the y-coordinate.
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The point at the origin.
     */
    public static final Point2D ORIGIN = at(0.0, 0.0);

    /**
     * A comparator to sort points into ascending order by their
     * x-coordinate.
     */
    public static final Comparator<Point2D> X_COMPARATOR =
        new Comparator<Point2D>() {
            @Override public int compare(Point2D p1, Point2D p2) {
                return Double.compare(p1.x, p2.x);
            }
        };

    /**
     * A comparator to sort points into ascending order by their
     * y-coordinate.
     */
    public static final Comparator<Point2D> Y_COMPARATOR =
        new Comparator<Point2D>() {
            @Override public int compare(Point2D p1, Point2D p2) {
                return Double.compare(p1.y, p2.y);
            }
        };

    /**
     * Returns the two-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @param y the y-coordinate.
     *
     * @return the two-dimensional point at {@code (x, y)}.
     */
    public static Point2D at(double x, double y) {
        return new Point2D(x, y);
    }

    @Override public double coord(int dim) {
        switch (dim) {
        case 0:
            return x;

        case 1:
            return y;

        default:
            throw new IndexOutOfBoundsException();
        }
    }

    @Override public int dimensionality() {
        return 2;
    }

    @Override public String formatCSV(DecimalFormat format) {
        return format.format(x) + "," + format.format(y);
    }

    @Override public double x() {
        return x;
    }

    @Override public double y() {
        return y;
    }

    @Override public double[] toArray() {
        return new double[] { x, y };
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Point2D) && equalsPoint((Point2D) that);
    }

    private boolean equalsPoint(Point2D that) {
        return DoubleComparator.DEFAULT.EQ(this.x, that.x) 
            && DoubleComparator.DEFAULT.EQ(this.y, that.y);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
