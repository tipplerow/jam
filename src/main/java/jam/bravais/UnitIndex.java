
package jam.bravais;

import java.util.Arrays;

import jam.lang.JamException;

/**
 * Defines the discrete absolute position of a unit cell within a
 * Bravais lattice.
 */
public interface UnitIndex extends Comparable<UnitIndex> {
    /**
     * Returns a one-dimensional unit index at a given location.
     *
     * @param index the integer coordinate of the location.
     *
     * @return the one-dimensional unit index at the specified
     * location.
     */
    public static UnitIndex at(int index) {
        return UnitIndex1D.at(index);
    }

    /**
     * Returns a two-dimensional unit index at a given location.
     *
     * @param i the integer coordinate along the {@code x}-direction.
     *
     * @param j the integer coordinate along the {@code y}-direction.
     *
     * @return the two-dimensional unit index at the specified
     * location.
     */
    public static UnitIndex at(int i, int j) {
        return UnitIndex2D.at(i, j);
    }

    /**
     * Returns a three-dimensional unit index at a given location.
     *
     * @param i the integer coordinate along the {@code x}-direction.
     *
     * @param j the integer coordinate along the {@code y}-direction.
     *
     * @param k the integer coordinate along the {@code z}-direction.
     *
     * @return the three-dimensional unit index at the specified
     * location.
     */
    public static UnitIndex at(int i, int j, int k) {
        return UnitIndex3D.at(i, j, k);
    }

    /**
     * Returns the unit index at a specified location.
     *
     * @param coords the integer coordinates of the index.
     *
     * @return the unit index at the specified location.
     *
     * @throws RuntimeException unless the number of coordinates
     * is 1, 2, or 3.
     */
    public static UnitIndex at(int[] coords) {
        switch (coords.length) {
        case 1:
            return at(coords[0]);

        case 2:
            return at(coords[0], coords[1]);

        case 3:
            return at(coords[0], coords[1], coords[2]);

        default:
            throw JamException.runtime("Invalid index coordinates: [%s].", Arrays.toString(coords));
        }
    }

    /**
     * Returns the unit index at the origin of the coordinate system
     * with a given dimensionality.
     *
     * @param dim the dimensionality of the unit index.
     *
     * @return the unit index at the origin of the coordinate system
     * with the specified dimensionality.
     *
     * @throws IllegalArgumentException unless the dimensionality is
     * in the allowed set {@code (1, 2, 3)}.
     */
    public static UnitIndex origin(int dim) {
        switch (dim) {
        case 1:
            return UnitIndex1D.ORIGIN;

        case 2:
            return UnitIndex2D.ORIGIN;

        case 3:
            return UnitIndex3D.ORIGIN;

        default:
            throw new IllegalArgumentException("Invalid dimensionality.");
        }
    }

    /**
     * Returns the index coordinate along a given dimension.
     *
     * @param dim the (zero-based) index for the desired dimension.
     *
     * @return the index coordinate along the given dimension.
     *
     * @throws IndexOutOfBoundsException unless the dimension index is
     * valid.
     */
    public abstract int coord(int dim);

    /**
     * Returns the dimensionality of this index.
     *
     * @return the dimensionality of this index.
     */
    public abstract int dimensionality();

    /**
     * Adds another index to this index and returns the result in a
     * new index; this index is unchanged.
     *
     * @param index the shifting index addend.
     *
     * @return the new shifted index.
     *
     * @throws RuntimeException unless the dimensionality of the input
     * argument agrees with this index.
     */
    public abstract UnitIndex plus(UnitIndex index);

    /**
     * Multiplies each coordinate in this index by a scalar factor and
     * returns the result in a new index; this index is unchanged.
     *
     * @param scalar the scalar integer factor.
     *
     * @return the new rescaled index.
     */
    public abstract UnitIndex times(int scalar);

    /**
     * Returns an array containing the components of this index.
     *
     * @return an array containing the components of this index.
     */
    public abstract int[] toArray();

    @Override public default int compareTo(UnitIndex that) {
        int dimComp = Integer.compare(this.dimensionality(), that.dimensionality());

        if (dimComp != 0)
            return dimComp;

        // The highest dimensionality takes precedence...
        for (int dim = dimensionality() - 1; dim >= 0; --dim) {
            int coordComp = Integer.compare(this.coord(dim), that.coord(dim));

            if (coordComp != 0)
                return coordComp;
        }

        return 0;
    }
}
