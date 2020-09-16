
package jam.math;

import java.text.DecimalFormat;

/**
 * Represents an immutable point in space.
 */
public interface Point {
    /**
     * Returns a one-dimensional point at a given location.
     *
     * @param x the {@code x}-coordinate of the location.
     *
     * @return the one-dimensional point at the specified
     * location.
     */
    public static Point at(double x) {
        return Point1D.at(x);
    }

    /**
     * Returns a two-dimensional point at a given location.
     *
     * @param x the {@code x}-coordinate of the location.
     *
     * @param y the {@code y}-coordinate of the location.
     *
     * @return the two-dimensional point at the specified
     * location.
     */
    public static Point at(double x, double y) {
        return Point2D.at(x, y);
    }

    /**
     * Returns a three-dimensional point at a given location.
     *
     * @param x the {@code x}-coordinate of the location.
     *
     * @param y the {@code y}-coordinate of the location.
     *
     * @param z the {@code z}-coordinate of the location.
     *
     * @return the one-dimensional point at the specified
     * location.
     */
    public static Point at(double x, double y, double z) {
        return Point3D.at(x, y, z);
    }

    /**
     * Returns the coordinate value along a given dimension.
     *
     * @param dim the (zero-based) index for the desired dimension.
     *
     * @return the coordinate value along the given dimension.
     *
     * @throws IndexOutOfBoundsException unless the dimension index is
     * valid.
     */
    public abstract double coord(int dim);

    /**
     * Returns the dimensionality of this point.
     *
     * @return the dimensionality of this point.
     */
    public abstract int dimensionality();

    /**
     * Computes the distance between this point and another.
     *
     * @param that the reference point.
     *
     * @return the distance between this point and the input point.
     *
     * @throws RuntimeException unless this point and the input point
     * have the same dimensionality.
     */
    public default double distance(Point that) {
        validateDimensionality(that);
        double dsqr = 0.0;

        for (int dim = 0; dim < dimensionality(); ++dim)
            dsqr += Math.pow(this.coord(dim) - that.coord(dim), 2);

        return Math.sqrt(dsqr);
    }

    /**
     * Returns a comma-separated string containing the coordinates of
     * this point.
     *
     * @param format the decimal formatter for the coordinate values.
     *
     * @return a comma-separated string containing the coordinates of
     * this point.
     */
    public abstract String formatCSV(DecimalFormat format);

    /**
     * Returns a comma-separated header string identifying the
     * coordinates of this point.
     *
     * @return a comma-separated header string identifying the
     * coordinates of this point.
     */
    public default String headerCSV() {
        return headerCSV(dimensionality());
    }

    /**
     * Returns a comma-separated header string identifying the
     * coordinates of points with a given dimensionality.
     *
     * @param dimensionality the dimensionality of the points of
     * interest.
     *
     * @return a comma-separated header string identifying the
     * coordinates of points with the specified dimensionality.
     */
    public static String headerCSV(int dimensionality) {
        switch (dimensionality) {
        case 1:
            return "x";

        case 2:
            return "x,y";

        case 3:
            return "x,y,z";

        default:
            throw new IllegalArgumentException("Unsupported dimensionality.");
        }
    }

    /**
     * Ensures that this point and another point have the same dimensionality.
     *
     * @param that the reference point.
     *
     * @throws RuntimeException unless this point and the input point
     * have the same dimensionality.
     */
    public default void validateDimensionality(Point that) {
        if (this.dimensionality() != that.dimensionality())
            throw new IllegalArgumentException("Inconsistent dimensionality.");
    }

    /**
     * Returns the {@code x}-component of this vector.
     *
     * @return the {@code x}-component of this vector.
     *
     * @throws RuntimeException unless this vector has
     * dimension 1 or greater.
     */
    public default double x() {
        return coord(0);
    }

    /**
     * Returns the {@code y}-component of this vector.
     *
     * @return the {@code y}-component of this vector.
     *
     * @throws RuntimeException unless this vector has
     * dimension 2 or greater.
     */
    public default double y() {
        return coord(1);
    }

    /**
     * Returns the {@code z}-component of this vector.
     *
     * @return the {@code z}-component of this vector.
     *
     * @throws RuntimeException unless this vector has 
     * dimension 3 or greater.
     */
    public default double z() {
        return coord(2);
    }

    /**
     * Returns a new bare array containing the components of this
     * point.
     *
     * @return a new bare array containing the components of this
     * point.
     */
    public abstract double[] toArray();
}
