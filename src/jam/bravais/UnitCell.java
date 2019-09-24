
package jam.bravais;

import java.util.List;

import jam.math.Point;
import jam.vector.VectorView;

/**
 * Defines the unit cell of a Bravais lattice.
 */
public interface UnitCell {
    /**
     * Returns a two-dimensional hexagonal unit cell with a given side
     * length.
     *
     * @param side the side length of the unit cell.
     *
     * @return a two-dimensional hexagonal unit cell with the specified
     * side length.
     *
     * @throws IllegalArgumentException unless the side length is positive.
     */
    public static UnitCell hexagonal(double side) {
        return new HexagonalUnitCell(side);
    }

    /**
     * Returns a two-dimensional square unit cell with a given side
     * length.
     *
     * @param side the side length of the unit cell.
     *
     * @return a two-dimensional square unit cell with the specified
     * side length.
     *
     * @throws IllegalArgumentException unless the side length is positive.
     */
    public static UnitCell square(double side) {
        return new SquareUnitCell(side);
    }

    /**
     * Returns the dimensionality of this unit cell.
     *
     * @return the dimensionality of this unit cell.
     */
    public abstract int dimensionality();

    /**
     * Finds the discrete unit index of the unit cell containing a
     * given continuous-space location.
     *
     * @param point the continuous-space point to locate.
     *
     * @return the discrete unit index corresponding to the specified
     * continuous-space location.
     *
     * @throws IllegalArgumentException unless the dimensionality of
     * the input point matches the dimensionality of this unit cell.
     */
    public abstract UnitIndex indexOf(Point point);

    /**
     * Translates a discrete unit index into its corresponding
     * continuous-space location.
     *
     * @param index the index to translate.
     *
     * @return the continuous-space location corresponding to the
     * specified discrete index.
     *
     * @throws IllegalArgumentException unless the dimensionality of
     * the input index matches the dimensionality of this unit cell.
     */
    public abstract Point pointAt(UnitIndex index);

    /**
     * Finds the nearest neighbors for a given unit cell location.
     *
     * @param index the discrete unit index of a unit cell.
     *
     * @return the indexes of the neighboring cells nearest to the
     * cell at the specified location.
     */
    public abstract List<UnitIndex> getNeighbors(UnitIndex index);

    /**
     * Selects one neighboring cell at random (with equal probability)
     * from the set of all nearest neighbors.
     *
     * @param index the discrete unit index of a unit cell.
     *
     * @return the index of one neighboring cell selected at random
     * (with equal probability) from the set of all nearest neighbors.
     */
    public abstract UnitIndex selectNeighbor(UnitIndex index);

    /**
     * Returns a read-only view of the primitive (basis) vectors for
     * this unit cell.
     *
     * @return a read-only view of the primitive (basis) vectors for
     * this unit cell.
     */
    public abstract List<VectorView> viewBasis();
}
