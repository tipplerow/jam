
package jam.bravais;

import java.util.List;

import jam.vector.VectorView;

/**
 * Represents a three-dimensional simple cubic unit cell.
 */
public final class SimpleCubicUnitCell extends UnitCell3D {
    private final double side;

    private static final List<UnitIndex> TRANSLATION_VECTORS =
        List.of(UnitIndex3D.at( 0,  0, -1),
                UnitIndex3D.at( 0, -1,  0),
                UnitIndex3D.at(-1,  0,  0),
                UnitIndex3D.at( 1,  0,  0),
                UnitIndex3D.at( 0,  1,  0),
                UnitIndex3D.at( 0,  0,  1));

    /**
     * The fundamental simple cubic unit cell with unit side length.
     */
    public static final SimpleCubicUnitCell FUNDAMENTAL = new SimpleCubicUnitCell(1.0);

    /**
     * Creates a new simple cubic unit cell with a given side length.
     *
     * @param side the side length for the unit cell.
     *
     * @throws IllegalArgumentException unless the side length is
     * positive.
     */
    public SimpleCubicUnitCell(double side) {
        super(constructBasis(side));
        this.side = side;
    }

    private static List<VectorView> constructBasis(double side) {
        validateSide(side);

        return List.of(VectorView.wrap(side, 0.0, 0.0),
                       VectorView.wrap(0.0, side, 0.0),
                       VectorView.wrap(0.0, 0.0, side));
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
