
package jam.bravais;

import java.util.List;

import jam.vector.VectorView;

/**
 * Represents a two-dimensional square unit cell.
 */
public final class SquareUnitCell extends UnitCell2D {
    private final double side;

    private static final List<UnitIndex> TRANSLATION_VECTORS =
        List.of(UnitIndex2D.at( 0, -1),
                UnitIndex2D.at(-1,  0),
                UnitIndex2D.at( 1,  0),
                UnitIndex2D.at( 0,  1));

    /**
     * The fundamental square unit cell with unit side length.
     */
    public static final SquareUnitCell FUNDAMENTAL = new SquareUnitCell(1.0);

    /**
     * Creates a new square unit cell with a given side length.
     *
     * @param side the side length for the unit cell.
     *
     * @throws IllegalArgumentException unless the side length is
     * positive.
     */
    public SquareUnitCell(double side) {
        super(constructBasis(side));
        this.side = side;
    }

    private static List<VectorView> constructBasis(double side) {
        validateSide(side);

        return List.of(VectorView.wrap(side, 0.0),
                       VectorView.wrap(0.0, side));
    }

    /**
     * Returns the length of each side of the unit cell.
     *
     * @return the length of each side of the unit cell.
     */
    public double side() {
        return side;
    }

    @Override public double getNeighborDistance() {
        return side;
    }

    @Override public List<UnitIndex> viewNeighborTranslationVectors() {
        return TRANSLATION_VECTORS;
    }
}
