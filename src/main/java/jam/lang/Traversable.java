
package jam.lang;

/**
 * Represents a fixed linear sequence of objects or primitives that
 * may be traversed by a cursor, a pointer which keeps track of the
 * position along the sequence.
 */
public interface Traversable {
    /**
     * Returns the number of elements in this sequence.
     *
     * @return the number of elements in this sequence.
     */
    public abstract int length();

    /**
     * Returns an iterable adapter that provides a cursor to traverse
     * all elements in this sequence from left to right.
     *
     * <p>Traversable sequences have a fixed length, so the iterator
     * returned by the adapter does not support the {@code remove()}
     * operation.
     *
     * @return an iterable adapter that provides a cursor to traverse
     * all elements in this sequence from left to right.
     */
    public default Iterable<Cursor> traverse() {
        return new CursorIterable(length());
    }

    /**
     * Determines whether a proposed sequence length is valid.
     *
     * @param length the sequence length to validate.
     *
     * @return {@code false} iff the proposed length is negative.
     */
    public static boolean isValidLength(int length) {
        return length >= 0;
    }

    /**
     * Determines whether an element index is a valid position in this
     * sequence.
     *
     * @param index the element index to validate.
     *
     * @return {@code true} iff the specified index is the valid range
     * {@code [0, length())}.
     */
    public default boolean isValidIndex(int index) {
        return 0 <= index && index < length();
    }

    /**
     * Validates an element index.
     *
     * @param index the index to validate.
     *
     * @throws IndexOutOfBoundsException unless the index is in the
     * valid range {@code [0, length())}.
     */
    public default void validateIndex(int index) {
        if (!isValidIndex(index))
            throw new IndexOutOfBoundsException(String.format("Index [%d] out of bounds: [0, %d).", index, length()));
    }

    /**
     * Validates the length of this sequence.
     *
     * @throws IllegalArgumentException if this sequence has a
     * negative length.
     */
    public default void validateLength() {
        validateLength(length());
    }

    /**
     * Validates a proposed sequence length.
     *
     * @param length the sequence length to validate.
     *
     * @throws IllegalArgumentException if the proposed length is
     * negative.
     */
    public static void validateLength(int length) {
        if (!isValidLength(length))
            throw new IllegalArgumentException("Negative length.");
    }
}
