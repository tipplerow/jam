
package jam.vector;

import java.text.NumberFormat;
import java.util.Iterator;

/**
 * A skeletal implementation of the {@code VectorView} interface that
 * is independent of the physical storage of the underlying elements.
 */
public abstract class AbstractVector implements VectorView, Iterable<Double> {
    /**
     * Formats this vector for output to a CSV file.
     *
     * @return a string containing the elements of this vector
     * separated by commas.
     */
    public String formatCSV() {
        return format(",", null);
    }

    /**
     * Formats this vector for output to a CSV file.
     *
     * @param formatter the number formatter; pass {@code null} to use
     * the default format from {@code Double.toString(double)}.
     *
     * @return a string containing the elements of this vector
     * separated by commas.
     */
    public String formatCSV(NumberFormat formatter) {
        return format(",", formatter);
    }

    /**
     * Formats this vector for output to a text file.
     *
     * @param delimiter the element delimiter.
     *
     * @return a string containing the elements of this vector
     * separated by the given delimiter.
     */
    public String format(String delimiter) {
        return format(delimiter, null);
    }

    /**
     * Formats this vector for output to a text file.
     *
     * @param delimiter the element delimiter.
     *
     * @param formatter the number formatter; pass {@code null} to use
     * the default format from {@code Double.toString(double)}.
     *
     * @return a string containing the elements of this vector
     * separated by the given delimiter.
     */
    public String format(String delimiter, NumberFormat formatter) {
        StringBuilder builder = new StringBuilder();

        if (length() > 0)
            builder.append(format(formatter, getDouble(0)));

        for (int k = 1; k < length(); k++) {
            builder.append(delimiter);
            builder.append(format(formatter, getDouble(k)));
        }

        return builder.toString();
    }

    private static String format(NumberFormat formatter, double value) {
        if (formatter != null)
            return formatter.format(value);
        else
            return Double.toString(value);
    }

    /**
     * Returns a read-only iterator over the elements in this vector.
     *
     * <p>Since vectors have a fixed length, the iterator does not
     * support the {@code remove()} operation.
     *
     * @return a read-only iterator over the elements in this vector.
     */
    @Override public Iterator<Double> iterator() {
        return new VectorIterator(this);
    }

    /**
     * Performs equality tests with arbitrary objects.
     *
     * @param that the object to test for equality.
     *
     * @return {@code true} iff the input object is a {@code VectorView} 
     * with the same length as this vector and all elements are equal
     * within the default floating-point tolerance.
     */
    @Override public boolean equals(Object that) {
        return (that instanceof VectorView) && this.equalsVector((VectorView) that);
    }

    /**
     * Formats the contents of this vector in a string.
     *
     * @return a string formatted as {@code ClassName(x1, x2, ...)}
     * where {@code ClassName} is the name of the runtime subclass,
     * {@code x1} is the first element, {@code x2} the second, etc.
     */
    @Override public String toString() {
        return String.format("%s(%s)", getClass().getName(), formatCSV());
    }
}
