
package jam.lang;

import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.vector.VectorView;

/**
 * Represents floating-point quantities that have a specific meaning
 * in a particular problem domain and have a well-defined range of
 * valid values.
 *
 * <p>All {@code DomainDouble} objects are validated at the time of
 * creation. They are immutable, so they are always contain a valid
 * value.
 *
 * <p>All {@code DomainDouble} objects are expected to be scaled so
 * that differences below {@code 1.0E-12} can be ignored. Tests for 
 * exact equality can then be made by the {@code DoubleComparator}.
 */
public abstract class DomainDouble implements Formatted {
    private final double value;

    protected DomainDouble(double value, DoubleRange range) {
        range.validate(value);
        this.value = value;
    }

    /**
     * Compares two {@code DomainDouble} objects numerically, allowing
     * for the default floating-point tolerance.
     *
     * <p>This method should be used to implement {@code compareTo} in
     * the subclasses; it is declared as {@code protected} to disallow
     * comparisions of difference subclasses.
     *
     * @param d1 the first {@code DomainDouble} to be compared.
     *
     * @param d2 the second {@code DomainDouble} to be compared.
     *
     * @return an integer less than, equal to, or greater than zero
     * according to whether the underlying double value in {@code d1}
     * is less than, equal to, or greater than 
     */
    protected static int compare(DomainDouble d1, DomainDouble d2) {
        return DoubleComparator.DEFAULT.compare(d1.value, d2.value);
    }

    /**
     * Returns a vector view of the underlying double values from a
     * {@code DomainDouble} array.
     *
     * @param domainDoubles the array to view.
     *
     * @return a vector view of the underlying double values.
     */
    public static VectorView asVectorView(DomainDouble... domainDoubles) {
        return VectorView.wrap(doubleValues(domainDoubles));
    }

    /**
     * Returns the underlying double value.
     *
     * @return the underlying double value.
     */
    public final double doubleValue() {
        return value;
    }

    /**
     * Returns the underlying double values from a {@code DomainDouble} array.
     *
     * @param domainDoubles the array to convert.
     *
     * @return an array in which element {@code k} contains the underlying
     * double value from the {@code DomainDouble} in element {@code k} of
     * the input array.
     */
    public static double[] doubleValues(DomainDouble... domainDoubles) {
        double[] values = new double[domainDoubles.length];

        for (int index = 0; index < domainDoubles.length; index++)
            values[index] = domainDoubles[index].value;
        
        return values;
    }

    /**
     * Tests for equality with a bare {@code double} value.
     *
     * @param value the value to test.
     *
     * @return {@code true} iff the value of this object is equal to
     * the input value within the default tolerance defined by the
     * {@code DoubleComparator} class.
     */
    public boolean equals(double value) {
        return DoubleComparator.DEFAULT.equals(this.value, value);
    }

    /**
     * Tests for equality with a bare {@code double} value.
     *
     * @param value the value to test.
     *
     * @param tolerance the floating-point tolerance.
     *
     * @return {@code true} iff the value of this object is equal to
     * the input value within the specified tolerance.
     */
    public boolean equals(double value, double tolerance) {
        return DoubleComparator.equals(this.value, value, tolerance);
    }

    @Override public boolean equals(Object that) {
        return this.getClass().equals(that.getClass()) && equalsDomainDouble((DomainDouble) that);
    }

    private boolean equalsDomainDouble(DomainDouble that) {
        return equals(that.value);
    }

    /**
     * Identifies negative values.
     *
     * @return {@code true} iff this value is negative allowing for
     * the default {@code DoubleComparator} tolerance (is less than
     * the tolerance).
     */
    public boolean isNegative() {
        return DoubleComparator.DEFAULT.isNegative(value);
    }

    /**
     * Identifies positive values.
     *
     * @return {@code true} iff this value is positive allowing for
     * the default {@code DoubleComparator} tolerance (greater than
     * the tolerance).
     */
    public boolean isPositive() {
        return DoubleComparator.DEFAULT.isPositive(value);
    }

    /**
     * Identifies (nearly) zero values.
     *
     * @return {@code true} iff this value is equal to zero when
     * allowing for the default {@code DoubleComparator} tolerance
     * (is less than the tolerance in absolute value).
     */
    public boolean isZero() {
        return DoubleComparator.DEFAULT.isZero(value);
    }

    @Override public String format() {
        return Double.toString(value);
    }

    /**
     * Forbids the use of {@code DomainDouble} objects as hash keys.
     *
     * <p>As floating-point quantities, {@code DomainDouble} objects
     * are not appropriate as hash keys, but the general contract for
     * {@link Object#hashCode} requires that we override this method
     * since we have implemented {@link DomainDouble#equals}.  There
     * is no straightforward way to generate equal hash codes for all
     * pairs of {@code DomainDouble} objects that are equal according
     * to {@link DomainDouble#equals}, so we will throw an exception
     * to prohibit the use of {@code DomainDouble} objects as hash
     * keys.
     *
     * @throws UnsupportedOperationException for all invocations.
     */
    @Override public int hashCode() {
        throw new UnsupportedOperationException("Cannot use DomainDouble objects as hash keys.");
    }

    @Override public String toString() {
        return debug();
    }
}
