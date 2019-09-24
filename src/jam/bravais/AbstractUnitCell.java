
package jam.bravais;

import java.util.Collections;
import java.util.List;

import jam.math.DoubleComparator;
import jam.math.Point;
import jam.vector.VectorView;

/**
 * Provides a partial implementation of the {@code UnitCell}
 * interface.
 */
public abstract class AbstractUnitCell implements UnitCell {
    private final List<VectorView> basis;

    protected AbstractUnitCell(List<VectorView> basis) {
        this.basis = Collections.unmodifiableList(basis);
    }

    /**
     * Ensures that the number of primitive (basis) vectors and their
     * dimensionality match the dimensionality of a unit cell.
     *
     * @param basis the basis vectors to validate.
     *
     * @param dimensionality the dimensionality of the unit cell.
     *
     * @throws IllegalArgumentException unless the number of primitive
     * (basis) vectors and their dimensionality match the specified
     * dimensionality.
     */
    public static void validateBasis(List<VectorView> basis, int dimensionality) {
        if (basis.size() != dimensionality)
            throw new IllegalArgumentException("Inconsistent number of basis vectors.");

        for (VectorView vector : basis)
            if (vector.length() != dimensionality)
                throw new IllegalArgumentException("Inconsistent basis vector dimensionality.");
    }

    /**
     * Ensures that the dimensionality of a continuous-space location
     * matches the dimensionality of this unit cell.
     *
     * @param point the point to validate.
     *
     * @throws IllegalArgumentException unless the dimensionality of
     * the input point matches the dimensionality of this unit cell.
     */
    public void validateDimensionality(Point point) {
        if (point.dimensionality() != this.dimensionality())
            throw new IllegalArgumentException("Inconsistent point dimensionality.");
    }

    /**
     * Ensures that the dimensionality of a discrete unit index
     * matches the dimensionality of this unit cell.
     *
     * @param index the index to validate.
     *
     * @throws IllegalArgumentException unless the dimensionality of
     * the input index matches the dimensionality of this unit cell.
     */
    public void validateDimensionality(UnitIndex index) {
        if (index.dimensionality() != this.dimensionality())
            throw new IllegalArgumentException("Inconsistent index dimensionality.");
    }

    /**
     * Ensures that the side length in a unit cell is positive.
     *
     * @param side the side length to validate.
     *
     * @throws IllegalArgumentException unless the side length is
     * positive.
     */
    public static void validateSide(double side) {
        if (!DoubleComparator.DEFAULT.isPositive(side))
            throw new IllegalArgumentException("Non-positive side length.");
    }

    @Override public List<VectorView> viewBasis() {
        return basis;
    }
}
