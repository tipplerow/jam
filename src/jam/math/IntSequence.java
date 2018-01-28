
package jam.math;

import java.lang.reflect.Array;

import jam.lang.Traversable;

/**
 * Represents an arbitrary sequence of integer values.
 */
public abstract class IntSequence implements Iterable<Integer> {
    /**
     * The special empty sequence.
     */
    public static final IntSequence EMPTY = new ContiguousIntSequence(0, 0);

    /**
     * Creates a sequence to be used for iteration over elements in an
     * array.
     *
     * <p>Sample usage:
     * <pre>
     *     for (int index : IntSequence.along(array))
     *         processElement(array[index]);
     * </pre>
     *
     * @param array the array to be processed.
     *
     * @return a contiguous sequence covering the indexes of the given
     * array.
     *
     * @throws IllegalArgumentException unless the input argument is
     * an array.
     */
    public static final IntSequence along(Object array) {
        return new ContiguousIntSequence(0, Array.getLength(array));
    }

    /**
     * Creates a sequence to be used for iteration over elements in a
     * traversable object.
     *
     * <p>Sample usage:
     * <pre>
     *     for (int index : IntSequence.along(spinVector))
     *         spinVector.flip(index);
     * </pre>
     *
     * @param traversable the object to be processed.
     *
     * @return a contiguous sequence covering the indexes of the given
     * array.
     *
     * @throws IllegalArgumentException unless the input argument is
     * an array.
     */
    public static final IntSequence along(Traversable traversable) {
        return new ContiguousIntSequence(0, traversable.length());
    }

    /**
     * Creates a contiguous sequence of integer values.
     * array.
     *
     * <p>Contiguous integer sequences are defined as the half-open
     * range {@code [begin, end)} following the conventions of the
     * {@code String.substring()} method and C++ iterators.  The
     * {@code begin} value is the first integer in the sequence, while
     * {@code end} is the first integer <em>not</em> in the sequence.
     * The length of the sequence is therefore {@code end - begin} and
     * empty sequences may be defined when {@code end == begin}.
     *
     * @param begin the first integer contained in the sequence.
     *
     * @param end the first integer <em>not</em> contained in the
     * sequence.
     *
     * @return the contiguous sequence {@code [begin, end)}.
     *
     * @throws IllegalArgumentException if {@code end < begin}.
     */
    public static final IntSequence contiguous(int begin, int end) {
        return new ContiguousIntSequence(begin, end);
    }

    /**
     * Identifies integer values in this sequence.
     *
     * @param value a value to examine.
     *
     * @return {@code true} iff this sequence contains the input value.
     */
    public abstract boolean contains(int value);

    /**
     * Formats this sequence as a character string.
     *
     * @return a string containing the integers in this sequence
     * separated by commas.
     */
    public String format() {
        int[] values = toArray();
        StringBuilder builder = new StringBuilder();

        if (values.length > 0)
            builder.append(values[0]);

        for (int index = 1; index < values.length; index++) {
            builder.append(", ");
            builder.append(values[index]);
        }

        return builder.toString();
    }

    /**
     * Returns an element identified by its ordinal position in this
     * sequence.
     *
     * @param index the ordinal index of the element to return.
     *
     * @return the integer element at the specified position in this
     * sequence.
     */
    public abstract int get(int index);

    /**
     * Returns the number of integers contained in this sequence.
     *
     * @return the number of integers contained in this sequence.
     */
    public abstract int length();

    /**
     * Returns the elements of this sequence in an array.
     *
     * @return a new array containing the elements of this sequence in
     * the order returned by the iterator.
     */
    public int[] toArray() {
        int index = 0;
        int[] result = new int[length()];

        for (Integer value : this)
            result[index++] = value.intValue();

        return result;
    }

    /**
     * Validates an element index.
     *
     * @param index the index to validate.
     *
     * @throws IllegalArgumentException if {@code index < 0 || index >= length()}.
     */
    public void validateIndex(int index) {
        if (index < 0 || index >= length())
            throw new IndexOutOfBoundsException();
    }
    
    @Override public String toString() {
        return "IntSequence(" + format() + ")";
    }
}
