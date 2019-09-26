
package jam.bravais;

import jam.util.RegexUtil;

public enum LatticeType {
    /**
     * The two-dimensional lattice with hexagonal unit cells.
     */
    HEXAGONAL {
        @Override public <T> Lattice<T> create(double[] sides, int[] period) {
            if (sides.length != 1)
                throw new IllegalArgumentException("Exactly one side length is required.");

            return Lattice.create(new HexagonalUnitCell(sides[0]), Period.box(period));
        }
    },

    /**
     * The two-dimensional lattice with square unit cells.
     */
    SQUARE {
        @Override public <T> Lattice<T> create(double[] sides, int[] period) {
            if (sides.length != 1)
                throw new IllegalArgumentException("Exactly one side length is required.");

            return Lattice.create(new SquareUnitCell(sides[0]), Period.box(period));
        }
    };

    /**
     * Creates a new empty lattice the unit cell defined by this type.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param sides the lengths of the sides of the unit cell.
     *
     * @param period the periodic dimensions for the lattice.
     *
     * @return a new empty lattice with the unit cell defined by this
     * type and the side lengths and periodic dimensions specified in
     * the argument list.
     *
     * @throws IllegalArgumentException unless the dimensions of the
     * unit cell and periodic box are valid for this lattice type.
     */
    public abstract <T> Lattice<T> create(double[] sides, int[] period);

    /**
     * Parses a single string that defines a Bravais lattice.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param def the string defining a Bravais lattice, formatted as
     * {@code TYPE; side1, side2, ...; period1, period2, ...}, where
     * {@code TYPE} is the enumerated type code, {@code side1, side2,
     * ...} are the (floating-point) side lengths for the unit cell,
     * and {@code period1, period2, ...} are the (integer) periodic
     * dimensions.
     *
     * @return the empty Bravais lattice defined by the input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * properly formatted lattice definition.
     */
    public static <T> Lattice<T> parse(String def) {
        String[] fields = RegexUtil.split(RegexUtil.SEMICOLON, def, 3);

        String typeField   = fields[0];
        String sideField   = fields[1];
        String periodField = fields[2];

        LatticeType type   = valueOf(typeField);
        double[]    sides  = RegexUtil.parseDouble(RegexUtil.COMMA, sideField);
        int[]       period = RegexUtil.parseInt(RegexUtil.COMMA, periodField);

        return type.create(sides, period);
    }
}
