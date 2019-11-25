
package jam.bravais;

public enum UnitCellType {
    /**
     * The simple cubic unit cell.
     */
    CUBIC {
        @Override public UnitCell create(double... length) {
            if (length.length == 1)
                return new SimpleCubicUnitCell(length[0]);
            else
                throw new IllegalArgumentException("Exactly one length is required.");
        }

        @Override public UnitCell fundamental() {
            return SimpleCubicUnitCell.FUNDAMENTAL;
        }
    },

    /**
     * The one-dimensional unit cell.
     */
    LINEAR {
        @Override public UnitCell create(double... length) {
            if (length.length == 1)
                return LinearUnitCell.create(length[0]);
            else
                throw new IllegalArgumentException("Exactly one length is required.");
        }

        @Override public UnitCell fundamental() {
            return LinearUnitCell.FUNDAMENTAL;
        }
    },

    /**
     * The two-dimensional hexagonal unit cell.
     */
    HEXAGONAL {
        @Override public UnitCell create(double... sides) {
            if (sides.length == 1)
                return new HexagonalUnitCell(sides[0]);
            else
                throw new IllegalArgumentException("Exactly one side length is required.");
        }

        @Override public UnitCell fundamental() {
            return HexagonalUnitCell.FUNDAMENTAL;
        }
    },

    /**
     * The two-dimensional square unit cell.
     */
    SQUARE {
        @Override public UnitCell create(double... sides) {
            if (sides.length == 1)
                return new SquareUnitCell(sides[0]);
            else
                throw new IllegalArgumentException("Exactly one side length is required.");
        }

        @Override public UnitCell fundamental() {
            return SquareUnitCell.FUNDAMENTAL;
        }
    };

    /**
     * Creates a new unit cell of this type.
     *
     * @param sides the lengths of the sides of the unit cell.
     *
     * @return a new unit cell of this type with the specified side
     * lengths.
     *
     * @throws IllegalArgumentException unless the side lengths are
     * valid for this lattice type.
     */
    public abstract UnitCell create(double... sides);

    /**
     * Returns the fundamental unit cell for this type (typically with
     * unit side length).
     *
     * @return the fundamental unit cell for this type (typically with
     * unit side length).
     */
    public abstract UnitCell fundamental();
}
