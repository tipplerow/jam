
package jam.lattice;

/**
 * Represents the locations of sites on periodic lattice.  
 *
 * <p>While coordinates represented by the {@code Coord} class are
 * absolute, locations represented by this class are periodic images
 * of absolute coordinates and always lie within the boundaries of a
 * lattice.
 *
 * <p><b>Immutability.</b> Once created, the coordinates of an image
 * are fixed and cannot be changed.  This allows images to be safely
 * used as keys in maps (e.g., in sparse lattice implementations).
 */
public final class Image {
    /** The discrete x-coordinate. */
    public final int x;

    /** The discrete y-coordinate. */
    public final int y;

    /** The discrete z-coordinate. */
    public final int z;

    /**
     * Creates a new image.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
     *
     * @throws IllegalArgumentException if any coordinates are
     * negative.
     */
    public Image(int x, int y, int z) {
	validateCoordinate(x);
	validateCoordinate(y);
	validateCoordinate(z);

	this.x = x;
	this.y = y;
	this.z = z;
    }

    private static void validateCoordinate(int k) {
	if (k < 0)
	    throw new IllegalArgumentException("Negative image coordinate.");
    }

    /**
     * Returns the periodic image at a specific location.
     *
     * @param x the discrete x-coordinate.
     * @param y the discrete y-coordinate.
     * @param z the discrete z-coordinate.
     *
     * @return a periodic image with the specified components.
     *
     * @throws IllegalArgumentException if any coordinates are
     * negative.
     */
    public static Image at(int x, int y, int z) {
        return new Image(x, y, z);
    }

    @Override public int hashCode() {
	//
	// This definition will generate unique values for all images
	// on periodic lattices with sides of length (2 ^ 10) = 1024
	// or smaller.
	//
	return x + (y << 10) + (z << 20);
    }

    @Override public boolean equals(Object that) {
	return (that instanceof Image) && equalsImage((Image) that);
    }

    private boolean equalsImage(Image that) {
	return this.x == that.x
	    && this.y == that.y
	    && this.z == that.z;
    }

    @Override public String toString() {
	return String.format("Image(%d, %d, %d)", x, y, z);
    }
}
