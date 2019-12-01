
package jam.bravais;

import java.util.List;

import jam.math.DoubleUtil;
import jam.vector.VectorView;

/**
 * Represents a two-dimensional hexagonal unit cell.
 */
public final class HexagonalUnitCell extends UnitCell2D {
    private final double side;

    private static final List<UnitIndex> TRANSLATION_VECTORS =
        List.of(UnitIndex2D.at(-1, -1),
                UnitIndex2D.at( 0, -1),
                UnitIndex2D.at(-1,  0),
                UnitIndex2D.at( 1,  0),
                UnitIndex2D.at( 0,  1),
                UnitIndex2D.at( 1,  1));

    /**
     * The fundamental hexagonal unit cell with unit side length.
     */
    public static final HexagonalUnitCell FUNDAMENTAL = new HexagonalUnitCell(1.0);

    /**
     * Creates a new hexagonal unit cell with a given side length.
     *
     * @param side the side length for the unit cell.
     *
     * @throws IllegalArgumentException unless the side length is
     * positive.
     */
    public HexagonalUnitCell(double side) {
        super(constructBasis(side));
        this.side = side;
    }

    private static List<VectorView> constructBasis(double side) {
        validateSide(side);

        return List.of(VectorView.wrap(side, 0.0),
                       VectorView.wrap(-0.5 * side, DoubleUtil.HALF_SQRT3 * side));
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
