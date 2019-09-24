
package jam.bravais;

import jam.math.IntUtil;
import jam.util.RegexUtil;

public enum LatticeType {
    /**
     * The two-dimensional lattice with hexagonal unit cells.
     */
    HEXAGONAL {
        @Override public <T> Lattice<T> create(Period period) {
            return Lattice.create(HexagonalUnitCell.FUNDAMENTAL, period);
        }
    },

    /**
     * The two-dimensional lattice with square unit cells.
     */
    SQUARE {
        @Override public <T> Lattice<T> create(Period period) {
            return Lattice.create(SquareUnitCell.FUNDAMENTAL, period);
        }
    };

    /**
     * Creates a new empty lattice with the fundamental unit cell for
     * this lattice type and the given period dimensions.
     *
     * <p>The fundamental unit cell typically has unit side length.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param period the periodic dimensions for the lattice.
     *
     * @return a new empty lattice with the fundamental unit cell for
     * this lattice type and the specified period dimensions.
     *
     * @throws IllegalArgumentException unless the periodic dimensions
     * are valid.
     */
    public abstract <T> Lattice<T> create(Period period);

    /**
     * Parses a single string that defines a Bravais lattice.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param def the string defining a Bravais lattice, formatted as
     * {@code TYPE; period1, period2, ...}, where {@code TYPE} is the
     * enumerated type code and {@code period1, period2, ...} are the
     * comma-separated periodic dimensions.
     *
     * @return the empty Bravais lattice defined by the input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * properly formatted lattice definition.
     */
    public static <T> Lattice<T> parse(String def) {
        String[] fields = RegexUtil.split(RegexUtil.SEMICOLON, def, 2);

        String typeField   = fields[0];
        String periodField = fields[1];

        return valueOf(typeField).create(parsePeriod(periodField));
    }

    private static Period parsePeriod(String field) {
        return Period.box(RegexUtil.parseInt(RegexUtil.COMMA, field));
    }
}
