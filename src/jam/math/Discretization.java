
package jam.math;

/**
 * Maps continuous coordinates onto a lattice of discrete points
 * indexed by an integral value.
 *
 * <p>A discretization is defined by a <em>resolution</em>, the
 * distance between adjacent lattice points.
 */
public final class Discretization {
    private final double resolution;

    /**
     * The minimum resolution allowed for discretization.
     */
    public static final double MINIMUM_RESOLUTION = 1.0E-12;

    /**
     * Creates a new discretization with a fixed resolution.
     *
     * @param resolution the distance beween adjacent discrete points.
     *
     * @throws IllegalArgumentException if the resolution is less than
     * the minimum defined by {@link Discretization#MINIMUM_RESOLUTION}.
     */
    public Discretization(double resolution) {
        validateResolution(resolution);
        this.resolution = resolution;
    }

    /**
     * Creates a new discretization with a fixed resolution.
     *
     * @param resolution the distance beween adjacent discrete points.
     *
     * @return a new discretization with the specified fixed resolution.
     *
     * @throws IllegalArgumentException if the resolution is less than
     * the minimum defined by {@link Discretization#MINIMUM_RESOLUTION}.
     */
    public static Discretization withResolution(double resolution) {
        return new Discretization(resolution);
    }

    /**
     * Returns the continuous coordinate for a given lattice point.
     *
     * @param discrete the index of the discrete lattice point.
     *
     * @return the continuous coordinate at the specified lattice
     * point.
     */
    public double asContinuous(long discrete) {
        return ((double) discrete) * resolution;
    }

    /**
     * Returns the continuous coordinates for a set of lattice points.
     *
     * @param discrete the indexes of the discrete lattice points.
     *
     * @return the continuous coordinates at the specified lattice
     * points.
     */
    public double[] asContinuous(long[] discrete) {
        double[] result = new double[discrete.length];

        for (int k = 0; k < discrete.length; k++)
            result[k] = asContinuous(discrete[k]);

        return result;
    }

    /**
     * Maps a continuous coordinate onto a discrete lattice point.
     *
     * @param continuous the continuous coordinate to map.
     *
     * @return the index of the nearest lattice point (to which the
     * coordinate is mapped).
     */
    public long asDiscrete(double continuous) {
        return Math.round(continuous / resolution);
    }

    /**
     * Maps continuous coordinates onto discrete lattice points.
     *
     * @param continuous the continuous coordinates to map.
     *
     * @return the indexes of the nearest lattice points (to which the
     * coordinates are mapped).
     */
    public long[] asDiscrete(double[] continuous) {
        long[] result = new long[continuous.length];

        for (int k = 0; k < continuous.length; k++)
            result[k] = asDiscrete(continuous[k]);

        return result;
    }

    /**
     * Compares two continuous coordinates by the lattice points to
     * which they are mapped.
     *
     * @param x the first continuous coordinate.
     *
     * @param y the second continuous coordinate.
     *
     * @return a negative integer, zero, or positive integer according
     * to whether the continuous coordinates {@code x} is mapped to a
     * lattice point with index less than, equal to, or greater than
     * the lattice point to which {@code y} is mapped..
     */
    public int compare(double x, double y) {
        return Long.compare(asDiscrete(x), asDiscrete(y));
    }

    /**
     * Determines whether two coordinates are mapped to the same
     * lattice point.
     *
     * @param x the first continuous coordinate.
     *
     * @param y the second continuous coordinate.
     *
     * @return {@code true} iff the two continuous coordinates are
     * mapped to the same lattice point.
     */
    public boolean equals(double x, double y) {
        return compare(x, y) == 0;
    }

    /**
     * Returns the resolution (distance between adjacent lattice
     * points) for this discretization.
     *
     * @return the resolution (distance between adjacent lattice
     * points) for this discretization.
     */
    public double getResolution() {
        return resolution;
    }

    /**
     * Ensures that a resolution is valid.
     *
     * @param resolution the resolution to validate.
     *
     * @throws IllegalArgumentException if the resolution is less than
     * the minimum defined by {@link Discretization#MINIMUM_RESOLUTION}.
     */
    public void validateResolution(double resolution) {
        if (resolution < MINIMUM_RESOLUTION)
            throw new IllegalArgumentException("Resolution is below the minimum");
    }
}
