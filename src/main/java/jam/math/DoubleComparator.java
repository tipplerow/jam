
package jam.math;

import java.util.Comparator;

import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Compares floating-point numbers with allowance for a finite
 * precision.
 *
 * <p>Like the built-in {@code Double.compare(double, double)} method,
 * {@code Double.NaN} is considered equal to itself and greater than
 * all other double values.  However, the methods {@code isPositive}
 * and {@code isNegative} return {@code false} for {@code Double.NaN}.
 * The value {@code Double.POSITIVE_INFINITY} is equal to itself and
 * greater than all finite values; {@code Double.NEGATIVE_INFINITY} is
 * equal to itself and less than all other values.
 */
public final class DoubleComparator implements Comparator<Double> {
    private final double tolerance;

    /**
     * Creates a new comparator with a specified tolerance.
     *
     * @param tolerance the maximum amount by which values may differ
     * and still be considered equal by the comparator.
     *
     * @throws IllegalArgumentException unless the tolerance is
     * positive.
     */
    public DoubleComparator(double tolerance) {
        if (tolerance <= 0.0)
            throw new IllegalArgumentException("Tolerance must be positive.");

        this.tolerance = tolerance;
    }

    /**
     * Creates a new comparator with a tolerance that is customized
     * for the magnitudes of a set of typical values.
     *
     * @param values numeric values that typify the magnitude of the
     * values that will be compared.
     *
     * @return a customized comparator.
     */
    public static DoubleComparator custom(double... values) {
        double normInf = VectorAggregator.normInf(VectorView.wrap(values));

        if (normInf < 1.0)
            return DEFAULT;
        else
            return new DoubleComparator(DEFAULT_TOLERANCE * normInf);
    }

    /**
     * Order-of-magnitude estimate for the floating-point precision.
     */
    public static double EPSILON = 2.2e-16;

    /**
     * Default tolerance for floating-point comparision for quantities
     * of order one.
     */
    public static double DEFAULT_TOLERANCE = 1.0e-12;

    /**
     * Comparator with the default tolerance.
     */
    public static final DoubleComparator DEFAULT = new DoubleComparator(DEFAULT_TOLERANCE);

    public static int compare(double x, double y, double tol) {
        if (DoubleUtil.isFinite(x) && DoubleUtil.isFinite(y))
            return compareFinite(x, y, tol);
        else
            return Double.compare(x, y); // Tolerance does not apply with NaN and infinite values...
    }

    private static int compareFinite(double x, double y, double tol) {
        double diff = x - y;

        if (diff < -tol)
            return -1;
        else if (diff > tol)
            return 1;
        else
            return 0;
    }

    public static boolean equals(double x, double y, double tol) {
        return compare(x, y, tol) == 0;
    }

    public static boolean isUnity(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 1.0, tol) == 0;
    }

    public static boolean isZero(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) == 0;
    }

    public static boolean isNonZero(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) != 0;
    }

    public static boolean isPositive(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) > 0;
    }

    public static boolean isNegative(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) < 0;
    }

    public static boolean isNonPositive(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) <= 0;
    }

    public static boolean isNonNegative(double x, double tol) {
        return !Double.isNaN(x) && compare(x, 0.0, tol) >= 0;
    }

    public static boolean LT(double x, double y, double tol) {
        return compare(x, y, tol) < 0;
    }

    public static boolean LE(double x, double y, double tol) {
        return compare(x, y, tol) <= 0;
    }

    public static boolean EQ(double x, double y, double tol) {
        return compare(x, y, tol) == 0;
    }

    public static boolean NE(double x, double y, double tol) {
        return compare(x, y, tol) != 0;
    }

    public static boolean GE(double x, double y, double tol) {
        return compare(x, y, tol) >= 0;
    }

    public static boolean GT(double x, double y, double tol) {
        return compare(x, y, tol) > 0;
    }

    /**
     * Returns the floating-point tolerance for this comparator.
     *
     * @return the floating-point tolerance for this comparator.
     */
    public double getTolerance() {
        return tolerance;
    }

    public boolean equals(double x, double y) {
        return compare(x, y) == 0;
    }

    public boolean isUnity(double x) {
        return !Double.isNaN(x) && compare(x, 1.0) == 0;
    }

    public boolean isZero(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) == 0;
    }

    public boolean isNonZero(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) != 0;
    }

    public boolean isPositive(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) > 0;
    }

    public boolean isNegative(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) < 0;
    }

    public boolean isNonPositive(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) <= 0;
    }

    public boolean isNonNegative(double x) {
        return !Double.isNaN(x) && compare(x, 0.0) >= 0;
    }

    public boolean isInteger(double x) {
        return equals(x, Math.rint(x));
    }

    public boolean isIncreasing(double... x) {
        for (int k = 1; k < x.length; ++k)
            if (LE(x[k], x[k - 1]))
                return false;

        return true;
    }

    public boolean isNonDecreasing(double... x) {
        for (int k = 1; k < x.length; ++k)
            if (LT(x[k], x[k - 1]))
                return false;

        return true;
    }

    public boolean isNonIncreasing(double... x) {
        for (int k = 1; k < x.length; ++k)
            if (GT(x[k], x[k - 1]))
                return false;

        return true;
    }

    public boolean isDecreasing(double... x) {
        for (int k = 1; k < x.length; ++k)
            if (GE(x[k], x[k - 1]))
                return false;

        return true;
    }

    public boolean LT(double x, double y) {
        return compare(x, y) < 0;
    }

    public boolean LE(double x, double y) {
        return compare(x, y) <= 0;
    }

    public boolean EQ(double x, double y) {
        return compare(x, y) == 0;
    }

    public boolean NE(double x, double y) {
        return compare(x, y) != 0;
    }

    public boolean GE(double x, double y) {
        return compare(x, y) >= 0;
    }

    public boolean GT(double x, double y) {
        return compare(x, y) > 0;
    }

    /**
     * Returns the sign of a floating-point value.
     *
     * @param x the value to examine.
     *
     * @return {@code -1} if the input value is negative (in excess
     * of the comparison tolerance), {@code 0} if the input value is
     * equal to zero (within the comparison tolerance), or {@code +1}
     * if the input value is positive (in excess of the comparison
     * tolerance).
     */
    public int sign(double x) {
        if (isNegative(x))
            return -1;

        if (isPositive(x))
            return 1;

        return 0;
    }

    @Override public int compare(Double x, Double y) {
        return compare(x.doubleValue(), y.doubleValue(), tolerance);
    }
}
