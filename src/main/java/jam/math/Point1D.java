
package jam.math;

import java.text.DecimalFormat;

/**
 * Represents an immutable point on a line.
 */
public final class Point1D implements Point {
    /** The immutable x-coordinate. */
    public final double x;

    /**
     * Creates a new one-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     */
    public Point1D(double x) {
        this.x = x;
    }

    /**
     * The point at the origin.
     */
    public static final Point1D ORIGIN = at(0.0);

    /**
     * Returns the one-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @return the one-dimensional point at {@code (x)}.
     */
    public static Point1D at(double x) {
        return new Point1D(x);
    }

    @Override public double coord(int dim) {
        if (dim == 0)
            return x;
        else
            throw new IndexOutOfBoundsException();
    }

    @Override public int dimensionality() {
        return 1;
    }

    @Override public double distance(Point that) {
        return distance1D((Point1D) that);
    }

    private double distance1D(Point1D that) {
        return Math.abs(this.x - that.x);
    }

    @Override public String formatCSV(DecimalFormat format) {
        return format.format(x);
    }

    @Override public double x() {
        return x;
    }

    @Override public double[] toArray() {
        return new double[] { x };
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Point1D) && equalsPoint((Point1D) that);
    }

    private boolean equalsPoint(Point1D that) {
        return DoubleComparator.DEFAULT.EQ(this.x, that.x);
    }

    @Override public String toString() {
        return "(" + x + ")";
    }
}
