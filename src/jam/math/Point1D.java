
package jam.math;

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
