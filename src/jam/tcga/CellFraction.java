
package jam.tcga;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;

/**
 * Quantifies the fraction of tumor cells containing a specific
 * mutation.
 */
public final class CellFraction extends DomainDouble {
    /**
     * Valid range for cell fractions.
     */
    public static final DoubleRange RANGE = DoubleRange.FRACTIONAL;

    /**
     * A globally sharable instance representing zero cell fraction.
     */
    public static final CellFraction ZERO = valueOf(0.0);

    /**
     * A globally sharable instance representing unit cell fraction.
     */
    public static final CellFraction UNIT = valueOf(1.0);

    private CellFraction(double value) {
        super(value, RANGE);
    }

    /**
     * Name of the cell fraction column in stanardized data files.
     */
    public static final String COLUMN_NAME = "CCF";

    /**
     * Validates a cell fraction value.
     *
     * @param value the expression to validate.
     *
     * @throws IllegalArgumentException if the expression is negative.
     */
    public static void validate(double value) {
        RANGE.validate(value);
    }

    /**
     * Marks a {@code double} value as a {@code CellFraction}.
     *
     * @param value the expression value.
     *
     * @return a {@code CellFraction} object having the specified
     * expression value.
     *
     * @throws IllegalArgumentException if the expression is
     * negative.
     */
    public static CellFraction valueOf(double value) {
        return new CellFraction(value);
    }

    /**
     * Returns a cell fraction object corresponding to a string
     * representation.
     *
     * @param s a string representation of the expression value.
     *
     * @return a cell fraction object with the value represented
     * by the input string.
     */
    public static CellFraction valueOf(String s) {
        return valueOf(Double.parseDouble(s));
    }
}
