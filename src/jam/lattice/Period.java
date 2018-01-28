
package jam.lattice;

/**
 * Defines the dimensions of a periodic cubic lattice and translates
 * absolute coordinates into their periodic images.
 */
public final class Period {
    private final int nx;
    private final int ny;
    private final int nz;

    /**
     * Creates an arbitrary rectangular lattice period.
     *
     * @param nx the periodic length along the x-direction.
     * @param ny the periodic length along the y-direction.
     * @param nz the periodic length along the z-direction.
     *
     * @throws IllegalArgumentException unless all periodic dimensions
     * are positive.
     */
    public Period(int nx, int ny, int nz) {
	validateDimension(nx);
	validateDimension(ny);
	validateDimension(nz);

	this.nx = nx;
	this.ny = ny;
	this.nz = nz;
    }

    /**
     * Creates a cubic lattice period.
     *
     * @param N the periodic length along all directions.
     *
     * @return the new cubic lattice.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static Period cubic(int N) {
        return new Period(N, N, N);
    }

    /**
     * Validates a periodic dimension.
     *
     * @param N the periodic dimension to validate.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static void validateDimension(int N) {
	if (N < 1)
	    throw new IllegalArgumentException("Non-positive periodic dimension.");
    }

    /**
     * Translates an absolute coordinate into its periodic image.
     *
     * @param k the absolute coordinate.
     *
     * @param N the length of the periodic dimension.
     *
     * @return the periodic image of the absolute coordinate.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static int computeImage(int k, int N) {
	validateDimension(N);
	int result = k % N;

	if (result < 0)
	    result += N;

	return result;
    }

    /**
     * Translates an absolute coordinate into its periodic image on a
     * lattice with this period.
     *
     * @param coord an absolute coordinate.
     *
     * @return the periodic image.
     */
    public Image computeImage(Coord coord) {
	return computeImage(coord.x, coord.y, coord.z);
    }

    /**
     * Translates an absolute coordinate into its periodic image on a
     * lattice with this period.
     *
     * @param x the absolute x-coordinate.
     * @param y the absolute y-coordinate.
     * @param z the absolute z-coordinate.
     *
     * @return the periodic image.
     */
    public Image computeImage(int x, int y, int z) {
	return new Image(computeImage(x, nx), computeImage(y, ny), computeImage(z, nz));
    }

    /**
     * Returns the periodic length along the x-direction.
     *
     * @return the periodic length along the x-direction.
     */
    public int getPeriodX() {
	return nx;
    }

    /**
     * Returns the periodic length along the y-direction.
     *
     * @return the periodic length along the y-direction.
     */
    public int getPeriodY() {
	return ny;
    }

    /**
     * Returns the periodic length along the z-direction.
     *
     * @return the periodic length along the z-direction.
     */
    public int getPeriodZ() {
	return nz;
    }

    /**
     * Computes the number of unique sites on a lattice with this
     * period.
     *
     * @return the product of the linear dimensions.
     */
    public long getSiteCount() {
	return nx * ny * nz;
    }

    @Override public int hashCode() {
	//
	// This hash code definition will generate unique values for
	// all periodic lattices with sides of length (2 ^ 10) = 1024
	// or smaller.
	//
	return nx + (ny << 10) + (nz << 20);
    }

    @Override public boolean equals(Object that) {
	return (that instanceof Period) && equalsPeriod((Period) that);
    }

    private boolean equalsPeriod(Period that) {
	return this.nx == that.nx
	    && this.ny == that.ny
	    && this.nz == that.nz;
    }

    @Override public String toString() {
	return String.format("Period(%d, %d, %d)", nx, ny, nz);
    }
}
