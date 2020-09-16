
package jam.bravais;

import java.util.List;

/**
 * Defines the dimensions of a periodic lattice and translates
 * absolute lattice coordinates into their periodic images.
 */
public interface Period {
    /**
     * Creates a linear lattice period.
     *
     * @param N the periodic length.
     *
     * @return the new linear lattice period.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static Period linear(int N) {
        return new Period1D(N);
    }

    /**
     * Creates a linear lattice period.
     *
     * @param N the periodic length along both directions.
     *
     * @return the new square lattice period.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static Period square(int N) {
        return new Period2D(N, N);
    }

    /**
     * Creates a cubic lattice period.
     *
     * @param N the periodic length along all directions.
     *
     * @return the new cubic lattice period.
     *
     * @throws IllegalArgumentException unless the periodic dimension
     * is positive.
     */
    public static Period cubic(int N) {
        return new Period3D(N, N, N);
    }

    /**
     * Creates an arbitrary lattice period.
     *
     * @param period the periodic length along each direction.
     *
     * @return a lattice period with the specified dimensions.
     *
     * @throws IllegalArgumentException unless the dimensionality lies
     * between one and three (inclusive).
     */
    public static Period box(int... period) {
        switch (period.length) {
        case 1:
            return new Period1D(period[0]);
        
        case 2:
            return new Period2D(period[0], period[1]);
        
        case 3:
            return new Period3D(period[0], period[1], period[2]);

        default:
            throw new IllegalArgumentException("Invalid period dimensionality.");
        }
    }

    /**
     * Creates an N-dimensional box with sides of equal length.
     *
     * @param period the periodic length along each direction.
     *
     * @param dim the dimensionality of the box.
     *
     * @return a lattice period with the specified dimensions.
     *
     * @throws IllegalArgumentException unless the dimensionality lies
     * between one and three (inclusive).
     */
    public static Period boxND(int period, int dim) {
        switch (dim) {
        case 1:
            return linear(period);

        case 2:
            return square(period);

        case 3:
            return cubic(period);

        default:
            throw new IllegalArgumentException("Invalid period dimensionality.");
        }
    }

    /**
     * Translates an absolute unit index into its periodic image.
     *
     * @param index the coordinate of an absolute unit index.
     *
     * @param period the lattice period.
     *
     * @return the periodic image.
     */
    public static int imageOf(int index, int period) {
	int image = index % period;

	if (image < 0)
	    image += period;

	return image;
    }

    /**
     * Returns the dimensionality of this lattice period.
     *
     * @return the dimensionality of this lattice period.
     */
    public abstract int dimensionality();

    /**
     * Returns the length of the period along a given dimension.
     *
     * @param dim the dimension of interest.
     *
     * @return the length of the period along the specified dimension.
     */
    public abstract int period(int dim);

    /**
     * Translates an absolute unit index into its periodic image.
     *
     * @param index an absolute coordinate.
     *
     * @return the periodic image.
     */
    public abstract UnitIndex imageOf(UnitIndex index);

    /**
     * Returns the number of distinct sites on a lattice with this
     * period.
     *
     * @return the number of distinct sites on a lattice with this
     * period.
     */
    public abstract long countSites();

    /**
     * Enumerates all images in the box defined by this period.
     *
     * @return a list containing all images in the box defined
     * by this period.
     */
    public abstract List<UnitIndex> enumerate();
}
