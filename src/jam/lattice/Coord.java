
package jam.lattice;

import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Represents a discrete absolute position in three dimensions.
 *
 * <p>This class always stores absolute coordinates.  Periodic images
 * are represented by the {@code Image} class and are computed by the
 * lattice used in the simulation.
 *
 * <p><b>Immutability.</b> Once created, the coordinate components are
 * fixed and cannot be changed. This allows standard coordinates (e.g.,
 * the origin, allowed bond vectors) to be shared globally.
 *
 * <p><b>Natural ordering.</b> The {@code compareTo(Coord)} method
 * compares the {@code x}, {@code y}, and {@code z} coordinates in
 * that order.
 */
public final class Coord implements Comparable<Coord> {
    /** The discrete x-coordinate. */
    public final int x;

    /** The discrete y-coordinate. */
    public final int y;

    /** The discrete z-coordinate. */
    public final int z;

    /**
     * The coordinate at the origin.
     */
    public static final Coord ORIGIN = new Coord(0, 0, 0);

    /**
     * Creates a new absolute coordinate.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
    */
    public Coord(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * Returns the absolute coordinate at a specific location.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
     *
     * @return an absolute coordinate with the specified components.
     */
    public static Coord at(int x, int y, int z) {
        return new Coord(x, y, z);
    }

    /**
     * Returns the discrete lattice coordinate nearest a point in
     * continuous space.
     *
     * @param point the continuous 3D-vector coordinate.
     *
     * @return the discrete lattice coordinate nearest the given point
     * in continuous space.
     *
     * @throws IllegalArgumentException unless the input vector is
     * three dimensional.
     */
    public static Coord nearest(VectorView point) {
        validatePoint(point);
        return nearest(point.getDouble(0), point.getDouble(1), point.getDouble(2));
    }

    private static void validatePoint(VectorView point) {
        if (point.length() != 3)
            throw new IllegalArgumentException("A three-dimensional point is required.");
    }

    /**
     * Returns the discrete lattice coordinate nearest a point in
     * continuous space.
     *
     * @param x the continuous x-coordinate.
     * @param y the continuous y-coordinate.
     * @param z the continuous z-coordinate.
     *
     * @return the discrete lattice coordinate nearest the given point
     * in continuous space.
     */
    public static Coord nearest(double x, double y, double z) {
        return at((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    /**
     * Writes this coordinate in CSV format.
     *
     * @return a string representation of this coordinate in CSV format.
     */
    public String formatCSV() {
	StringBuilder builder = new StringBuilder();

	builder.append(x);
	builder.append(",");
	builder.append(y);
	builder.append(",");
	builder.append(z);

	return builder.toString();
    }

    /**
     * Parses a string representation of a coordinate in CSV format.
     *
     * @param s the string to parse.
     *
     * @return the corresponding coordinate object.
     *
     * @throws IllegalArgumentException unless the input string is a
     * properly formatted CSV representation of a coordinate.
     */
    public static Coord parseCSV(String s) {
	String[] fields = s.split(",");

	if (fields.length != 3)
	    throw new IllegalArgumentException(String.format("Invalid coordinate format [%s].", s));

	return new Coord(Integer.parseInt(fields[0].trim()),
			 Integer.parseInt(fields[1].trim()),
			 Integer.parseInt(fields[2].trim()));
    }

    /**
     * Computes the squared distance between two coordinates.
     *
     * @param coord1 the first coordinate.
     *
     * @param coord2 the second coordinate.
     *
     * @return the squared distance between {@code coord1} and
     * {@code coord2}.
     */
    public static int computeSquaredDistance(Coord coord1, Coord coord2) {
	int dx = coord1.x - coord2.x;
	int dy = coord1.y - coord2.y;
	int dz = coord1.z - coord2.z;

	return (dx * dx) + (dy * dy) + (dz * dz);
    }

    /**
     * Returns the distance between this coordinate and a point in
     * continuous space.
     *
     * @param point the continuous 3D-vector coordinate.
     *
     * @return the distance between this coordinate and the given
     * point in continuous space.
     *
     * @throws IllegalArgumentException unless the input vector is
     * three dimensional.
     */
    public double distance(VectorView point) {
        validatePoint(point);
        return distance(point.getDouble(0), point.getDouble(1), point.getDouble(2));
    }

    /**
     * Returns the distance between this coordinate and a point in
     * continuous space.
     *
     * @param x the continuous x-coordinate.
     * @param y the continuous y-coordinate.
     * @param z the continuous z-coordinate.
     *
     * @return the distance between this coordinate and the given
     * point in continuous space.
     */
    public double distance(double x, double y, double z) {
	double dx = x - this.x;
	double dy = y - this.y;
	double dz = z - this.z;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Returns the squared distance of this coordinate from the
     * origin.
     *
     * @return the squared distance of this coordinate from the
     * origin.
     */
    public int getSquaredLength() {
	return (x * x) + (y * y) + (z * z);
    }

    /**
     * Returns the squared distance between this coordinate and
     * another.
     *
     * @param that the other coordinate.
     *
     * @return the squared distance between this coordinate and
     * the input coordinate.
     */
    public int getSquaredDistance(Coord that) {
	return computeSquaredDistance(this, that);
    }
    
    /**
     * Computes the sum of this (vector) coordinate and another.
     *
     * @param that the coordinate to add.
     *
     * @return a new coordinate whose components are the sum of the
     * components of this coordinate and the input coordinate; this
     * coordinate is unchanged.
     */
    public Coord plus(Coord that) {
	return plus(that.x, that.y, that.z);
    }

    /**
     * Computes the sum of this (vector) coordinate and another.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
     *
     * @return a new coordinate whose components are the sum of the
     * components of this coordinate and the input coordinates; this
     * coordinate is unchanged.
     */
    public Coord plus(int x, int y, int z) {
	return new Coord(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Computes the difference of this (vector) coordinate and another.
     *
     * @param that the coordinate to subtract.
     *
     * @return a new coordinate whose components are the difference of
     * the components of this coordinate and the input coordinate; this 
     * coordinate is unchanged.
     */
    public Coord minus(Coord that) {
	return minus(that.x, that.y, that.z);
    }

    /**
     * Computes the difference of this (vector) coordinate and another.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
     *
     * @return a new coordinate whose components are the difference of
     * the components of this coordinate and the input coordinates; this 
     * coordinate is unchanged.
     */
    public Coord minus(int x, int y, int z) {
        return plus(-x, -y, -z);
    }

    /**
     * Computes the dot product of this (vector) coordinate with
     * another.
     *
     * @param that another coordinate.
     *
     * @return the dot product between this coordinate vector and the
     * input coordinate.
     */
    public int dot(Coord that) {
	return (this.x * that.x) + (this.y * that.y) + (this.z * that.z);
    }

    /**
     * Returns a floating-point vector representation of this
     * coordinate.
     *
     * @return a 3-vector whose elements are the x, y, and z
     * coordinates.
     */
    public JamVector toVector() {
	return JamVector.valueOf(x, y, z);
    }

    @Override public int compareTo(Coord that) {
        int cmpX = Integer.compare(this.x, that.x);

        if (cmpX != 0)
            return cmpX;

        int cmpY = Integer.compare(this.y, that.y);

        if (cmpY != 0)
            return cmpY;

        return Integer.compare(this.z, that.z);
    }

    @Override public int hashCode() {
	//
	// This definition will generate unique values for coordinates
	// within a cube with sides of length (2 ^ 10) = 1024.
	//
	return x + (y << 10) + (z << 20);
    }

    @Override public boolean equals(Object that) {
	return (that instanceof Coord) && equalsCoord((Coord) that);
    }

    private boolean equalsCoord(Coord that) {
	return this.x == that.x
	    && this.y == that.y
	    && this.z == that.z;
    }

    @Override public String toString() {
	return String.format("Coord(%d, %d, %d)", x, y, z);
    }
}
