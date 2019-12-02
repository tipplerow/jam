
package jam.bravais;

import java.util.List;

import jam.math.DoubleUtil;
import jam.vector.VectorView;

/**
 * Represents a three-dimensional body-centered cubic (BCC) unit cell.
 */
public final class BCCUnitCell extends UnitCell3D {
    private final double side;

    private static final List<UnitIndex> TRANSLATION_VECTORS =
        List.of(UnitIndex3D.at(-1, -1, -1),
                UnitIndex3D.at( 0,  0, -1),
                UnitIndex3D.at( 0, -1,  0),
                UnitIndex3D.at(-1,  0,  0),
                UnitIndex3D.at( 1,  0,  0),
                UnitIndex3D.at( 0,  1,  0),
                UnitIndex3D.at( 0,  0,  1),
                UnitIndex3D.at( 1,  1,  1));

    /**
     * The fundamental BCC unit cell with unit side length.
     */
    public static final BCCUnitCell FUNDAMENTAL = new BCCUnitCell(1.0);

    /**
     * Creates a new BCC unit cell with a given side length.
     *
     * @param side the side length for the unit cell.
     *
     * @throws IllegalArgumentException unless the side length is
     * positive.
     */
    public BCCUnitCell(double side) {
        super(constructBasis(side));
        this.side = side;
    }

    private static List<VectorView> constructBasis(double side) {
        validateSide(side);

        return List.of(VectorView.wrap( 0.5 * side,  0.5 * side, -0.5 * side),
                       VectorView.wrap(-0.5 * side,  0.5 * side,  0.5 * side),
                       VectorView.wrap( 0.5 * side, -0.5 * side,  0.5 * side));
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
        return side * DoubleUtil.HALF_SQRT3;
    }

    @Override public List<UnitIndex> viewNeighborTranslationVectors() {
        return TRANSLATION_VECTORS;
    }
}
