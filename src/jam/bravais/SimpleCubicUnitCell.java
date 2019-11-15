
package jam.bravais;

import java.util.List;

import jam.math.JamRandom;
import jam.math.DoubleUtil;
import jam.vector.VectorView;

/**
 * Represents a three-dimensional simple cubic unit cell.
 */
public final class SimpleCubicUnitCell extends UnitCell3D {
    private final double side;

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

    @Override public List<UnitIndex> getNeighbors(UnitIndex index) {
        validateDimensionality(index);

        int i = index.coord(0);
        int j = index.coord(1);
        int k = index.coord(2);

        return List.of(UnitIndex3D.at(i, j, k - 1),
                       UnitIndex3D.at(i, j - 1, k),
                       UnitIndex3D.at(i - 1, j, k),
                       UnitIndex3D.at(i + 1, j, k),
                       UnitIndex3D.at(i, j + 1, k),
                       UnitIndex3D.at(i, j, k + 1));
    }

    @Override public UnitIndex selectNeighbor(UnitIndex index) {
        //
        // To maximize efficiency, implement the selection here rather
        // than construct the complete list of neighbors...
        //
        validateDimensionality(index);

        int i = index.coord(0);
        int j = index.coord(1);
        int k = index.coord(2);

        // Select an integer from the half-open interval [0, 6)...
        int draw = JamRandom.global().nextInt(0, 6);

        switch (draw) {
        case 0:
            return UnitIndex3D.at(i, j, k - 1);

        case 1:
            return UnitIndex3D.at(i, j - 1, k);

        case 2:
            return UnitIndex3D.at(i - 1, j, k);

        case 3:
            return UnitIndex3D.at(i + 1, j, k);

        case 4:
            return UnitIndex3D.at(i, j + 1, k);

        case 5:
            return UnitIndex3D.at(i, j, k + 1);

        default:
            throw new IllegalStateException("Unexpected random integer draw.");
        }
    }
}
