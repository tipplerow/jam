
package jam.bravais;

import java.util.List;

import jam.math.DoubleComparator;
import jam.math.Point;
import jam.vector.VectorView;

/**
 * Represents a linear unit cell.
 */
public final class LinearUnitCell extends AbstractUnitCell {
    private final double length;

    private static final List<UnitIndex> TRANSLATION_VECTORS =
        List.of(UnitIndex.at(-1), UnitIndex.at(1));

    private LinearUnitCell(double length) {
        super(List.of(VectorView.wrap(length)));
        this.length = length;
    }

    /**
     * Creates a one-dimensional unit cell with a fixed length.
     *
     * @param length the length of the unit cell.
     *
     * @return a one-dimensional unit cell with the specified length.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static LinearUnitCell create(double length) {
        validateLength(length);
        return new LinearUnitCell(length);
    }

    private static void validateLength(double length) {
        if (!DoubleComparator.DEFAULT.isPositive(length))
            throw new IllegalArgumentException("Unit cell length must be positive.");
    }

    /**
     * The fundamental linear unit cell with unit length.
     */
    public static final LinearUnitCell FUNDAMENTAL = new LinearUnitCell(1.0);

    @Override public int dimensionality() {
        return 1;
    }

    @Override public double getNeighborDistance() {
        return length;
    }

    @Override public UnitIndex indexOf(Point point) {
        validateDimensionality(point);
        return UnitIndex.at((int) Math.round(point.coord(0) / length));
    }

    @Override public Point pointAt(UnitIndex index) {
        validateDimensionality(index);
        return Point.at(index.coord(0) * length);
    }

    @Override public List<UnitIndex> viewNeighborTranslationVectors() {
        return TRANSLATION_VECTORS;
    }
}
