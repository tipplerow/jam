
package jam.vector;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.ToDoubleFunction;

import jam.lang.Cursor;
import jam.lang.Traversable;
import jam.math.DoubleComparator;
import jam.math.IntSequence;

/**
 * Presents a read-only view of a vector of objects having a
 * floating-point ({@code double}) representation.
 */
public interface VectorView extends Traversable {
    /**
     * The single empty vector.
     */
    public static final VectorView EMPTY = wrap(new double[0]);

    /**
     * Returns the floating-point representation of the element at a
     * specified location.
     *
     * <p>This method is not simply named {@code get()}, as one might
     * expect, to allow implementation by classes whose elements have
     * valid floating-point representations but are not {@code double}
     * values, like {@code BitVector} and {@code SpinVector}.
     *
     * @param index the index of the element to examine.
     *
     * @return the floating-point representation of the element at the
     * specified location.
     *
     * @throws RuntimeException unless the specified index is a valid
     * location.
     */
    public abstract double getDouble(int index);

    /**
     * Returns the floating-point representation of the element at a
     * specified location.
     *
     * @param cursor a cursor pointing to the element to return.
     *
     * @return the floating-point representation of the element at the
     * specified location.
     *
     * @throws RuntimeException unless the specified cursor points to
     * a valid location.
     */
    public default double getDouble(Cursor cursor) {
        return getDouble(cursor.index());
    }

    /**
     * Returns an iterable view of the elements in this vector.
     *
     * <p>Vector views have a fixed length, so the iteration does not
     * support the {@code remove()} operation.
     *
     * @return an iterable view of the elements in this vector.
     */
    public default Iterable<Double> elements() {
        return new DoubleIterable(this);
    }

    /**
     * Compares this vector element-by-element to a raw array,
     * allowing the default tolerance for floating-point precision.
     *
     * @param elements elements against which to compare.
     *
     * @return {@code true} iff this vector and the input array have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(double... elements) {
        return equalsVector(wrap(elements));
    }

    /**
     * Compares this vector element-by-element to a raw array,
     * allowing a tolerance for floating-point precision.
     *
     * @param elements elements against which to compare.
     *
     * @param tolerance the tolerance to allow.
     *
     * @return {@code true} iff this vector and the input array have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(double[] elements, double tolerance) {
        return equalsVector(wrap(elements), tolerance);
    }

    /**
     * Compares this vector element-by-element to a raw array,
     * allowing a tolerance for floating-point precision.
     *
     * @param elements elements against which to compare.
     *
     * @param comparator the comparator to test elements for equality.
     *
     * @return {@code true} iff this vector and the input array have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(double[] elements, DoubleComparator comparator) {
        return equalsVector(wrap(elements), comparator);
    }

    /**
     * Compares this vector element-by-element to another, allowing a
     * sensible default tolerance for floating-point precision.
     *
     * @param that vector against which to compare.
     *
     * @return {@code true} iff this vector and the input vector have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(VectorView that) {
        return equalsVector(that, defaultTolerance());
    }

    /**
     * Returns a default tolerance for vector equality comparisons.
     *
     * @return a default tolerance for vector equality comparisons.
     */
    public default double defaultTolerance() {
        return DoubleComparator.DEFAULT_TOLERANCE * Math.max(1.0, VectorAggregator.norm1(this));
    }

    /**
     * Compares this vector element-by-element to another, allowing a
     * tolerance for floating-point precision.
     *
     * @param that vector against which to compare.
     *
     * @param tolerance the tolerance to allow.
     *
     * @return {@code true} iff this vector and the input vector have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(VectorView that, double tolerance) {
        return equalsVector(that, new DoubleComparator(tolerance));
    }

    /**
     * Compares this vector element-by-element to another, allowing a
     * tolerance for floating-point precision.
     *
     * @param that vector against which to compare.
     *
     * @param comparator the comparator to test elements for equality.
     *
     * @return {@code true} iff this vector and the input vector have
     * the same length and are equal when compared element-by-element.
     */
    public default boolean equalsVector(VectorView that, DoubleComparator comparator) {
        if (this.length() != that.length())
            return false;

        for (int index : IntSequence.along(this))
            if (comparator.NE(this.getDouble(index), that.getDouble(index)))
                return false;

        return true;
    }

    /**
     * Formats the contents of this vector in a string.
     *
     * @return a string formatted as {@code [x1, x2, ...]} where
     * {@code x1} is the first element, {@code x2} the second, etc.
     */
    public default String format() {
        StringBuilder builder = new StringBuilder("[");

        if (length() > 0)
            builder.append(getDouble(0));

        for (int index = 1; index < length(); index++) {
            builder.append(", ");
            builder.append(getDouble(index));
        }

        builder.append("]");
        return builder.toString();
    }

    /**
     * Determines whether another vector is a valid operand for
     * algebraic operations with this vector.
     *
     * @param operand the operand to validate.
     *
     * @return {@code true} iff the operand has the same length as
     * this vector.
     */
    public default boolean isValidOperand(VectorView operand) {
        return this.length() == operand.length();
    }

    /**
     * Returns a subvector of this vector.
     *
     * <p>Similarly to {@code String.substring}, the subvector begins
     * at the specified index and extends to the end of this vector.
     *
     * @param beginIndex the beginning index, inclusive.
     *
     * @return the specified subvector (in a new object).
     *
     * @throws RuntimeException unless the specifed index is valid.
     */
    public default VectorView subvector(int beginIndex) {
        return subvector(beginIndex, length());
    }

    /**
     * Returns a subvector of this vector.
     *
     * <p>Similarly to {@code String.substring}, the subvector
     * contains elements {@code (beginIndex, endIndex]}.
     *
     * @param beginIndex the beginning index, inclusive.
     *
     * @param endIndex the ending index, exclusive.
     *
     * @return the specified subvector (in a new object).
     *
     * @throws RuntimeException unless the specifed indexes are valid.
     */
    public default VectorView subvector(int beginIndex, int endIndex) {
        return new SubVectorView(this, beginIndex, endIndex);
    }

    /**
     * Copies the elements of this vector into a bare array.
     *
     * @return a new array {@code v} with {@code v[k] == getDouble(k)}.
     */
    public default double[] toNumeric() {
        double[] result = new double[length()];

        for (int index : IntSequence.along(this))
            result[index] = getDouble(index);

        return result;
    }

    /**
     * Validates a vector operand in an algebraic expression.
     *
     * @param operand the operand to validate.
     *
     * @throws IllegalArgumentException unless the operand has the
     * same length as this vector.
     */
    public default void validateOperand(VectorView operand) {
        if (!isValidOperand(operand))
            throw new IllegalArgumentException("Vector length mismatch.");
    }

    /**
     * Wraps a bare array in a read-only vector view.
     *
     * <p>The view is a shallow wrapper around the underlying array,
     * so changes to the bare array will be reflected in the view.
     * Therefore, this construction is most appropriate for vector
     * views returned from functions that have created new arrays.
     *
     * @param array the array to wrap.
     *
     * @return a vector view of the input array.
     */
    public static VectorView wrap(double... array) {
        return new ArrayWrapper(array);
    }

    /**
     * Wraps a list of {@code Double} objects in a read-only vector
     * view.  In the interest of efficiency, the list must implement
     * the {@link java.util.RandomAccess} interface.
     *
     * <p>The view is a shallow wrapper around the underlying list, so
     * changes to the list will be reflected in the view.
     *
     * @param list the list to wrap.
     *
     * @return a vector view of the input list.
     *
     * @throws IllegalArgumentException unless the list implements the
     * {@link java.util.RandomAccess} interface.
     */
    public static VectorView wrap(List<Double> list) {
        return new ListWrapper(list);
    }

    /**
     * Maps a list of objects onto a read-only vector view.  In the
     * interest of efficiency, the list must implement the {@link
     * java.util.RandomAccess} interface.
     *
     * <p>The view is a shallow wrapper around the underlying list, so
     * changes to the list will be reflected in the view.
     *
     * @param <E> the type of elements contained in the list.
     *
     * @param list the list to wrap.
     *
     * @param func a function that maps list objects to floating-point
     * values.
     *
     * @return a vector mapping of the input list.
     *
     * @throws IllegalArgumentException unless the list implements the
     * {@link java.util.RandomAccess} interface.
     */
    public static <E> VectorView map(List<E> list, ToDoubleFunction<? super E> func) {
        return new ListMapper<E>(list, func);
    }
}
