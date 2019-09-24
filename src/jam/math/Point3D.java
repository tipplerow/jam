
package jam.math;

import java.util.Comparator;

/**
 * Represents an immutable point in a three-dimensional space.
 */
public final class Point3D implements Point {
    /** The immutable x-coordinate. */
    public final double x;

    /** The immutable y-coordinate. */
    public final double y;

    /** The immutable z-coordinate. */
    public final double z;

    /**
     * Creates a new three-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @param y the y-coordinate.
     *
     * @param z the z-coordinate.
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns the three-dimensional point at a fixed location.
     *
     * @param x the x-coordinate.
     *
     * @param y the y-coordinate.
     *
     * @param z the z-coordinate.
     *
     * @return the three-dimensional point at {@code (x, y, z)}.
     */
    public static Point3D at(double x, double y, double z) {
        return new Point3D(x, y, z);
    }

    @Override public double coord(int dim) {
        switch (dim) {
        case 0:
            return x;

        case 1:
            return y;

        case 2:
            return z;

        default:
            throw new IndexOutOfBoundsException();
        }
    }

    @Override public int dimensionality() {
        return 3;
    }

    @Override public double x() {
        return x;
    }

    @Override public double y() {
        return y;
    }

    @Override public double z() {
        return z;
    }

    @Override public double[] toArray() {
        return new double[] { x, y, z };
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Point3D) && equalsPoint((Point3D) that);
    }

    private boolean equalsPoint(Point3D that) {
        return DoubleComparator.DEFAULT.EQ(this.x, that.x) 
            && DoubleComparator.DEFAULT.EQ(this.y, that.y)
            && DoubleComparator.DEFAULT.EQ(this.z, that.z);
    }

    @Override public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
