
package jam.bravais;

import java.util.List;

import jam.math.JamRandom;
import jam.vector.VectorView;

/**
 * Represents a two-dimensional square unit cell.
 */
public final class SquareUnitCell extends UnitCell2D {
    private final double side;

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

    @Override public List<UnitIndex> getNeighbors(UnitIndex index) {
        validateDimensionality(index);

        int i = index.coord(0);
        int j = index.coord(1);

        return List.of(UnitIndex2D.at(    i, j - 1),
                       UnitIndex2D.at(i - 1,     j),
                       UnitIndex2D.at(i + 1,     j),
                       UnitIndex2D.at(    i, j + 1));
    }

    @Override public UnitIndex selectNeighbor(UnitIndex index) {
        //
        // To maximize efficiency, implement the selection here rather
        // than construct the complete list of neighbors...
        //
        validateDimensionality(index);

        int i = index.coord(0);
        int j = index.coord(1);

        // Select an integer from the half-open interval [0, 4)...
        int draw = JamRandom.global().nextInt(0, 4);

        switch (draw) {
        case 0:
            return UnitIndex2D.at(i, j - 1);

        case 1:
            return UnitIndex2D.at(i - 1, j);

        case 2:
            return UnitIndex2D.at(i + 1, j);

        case 3:
            return UnitIndex2D.at(i, j + 1);

        default:
            throw new IllegalStateException("Unexpected random integer draw.");
        }
    }
}
