
package jam.lang;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a fixed linear sequence of objects accessible by
 * zero-offset indexed location (e.g., an array of objects) and
 * provides a formal structure for traversing them.
 *
 * @param <E> the type of elements stored in the sequence.
 */
public interface Sequence<E> extends Traversable, Iterable<E> {
    /**
     * Returns the element at a specified location.
     *
     * @param index the index of the element to return.
     *
     * @return the element at the specified location.
     *
     * @throws IndexOutOfBoundsException unless the index is in
     * bounds.
     */
    public abstract E objectAt(int index);

    /**
     * Returns the element at a location specified by a cursor.
     *
     * @param cursor a cursor pointing to the element to return.
     *
     * @return the element at a location specified by the cursor.
     *
     * @throws IndexOutOfBoundsException unless the cursor points to a
     * valid location.
     */
    public default E objectAt(Cursor cursor) {
        return objectAt(cursor.index());
    }

    /**
     * Tests another sequence for equality.
     *
     * @param that the sequence to test.
     *
     * @return {@code true} iff this sequence and the input sequence
     * contain the same elements in the same order.
     */
    public default boolean equalsSequence(Sequence<E> that) {
        if (this.length() != that.length())
            return false;

        for (Cursor cursor : traverse())
            if (!this.objectAt(cursor).equals(that.objectAt(cursor)))
                return false;

        return true;
    }

    /**
     * Formats the contents of this sequence in the same manner as
     * {@link java.util.List}.
     *
     * @return a string formatted as {@code [e1, e2, ...]} where
     * {@code e1} is the first element, {@code e2} the second, etc.
     */
    public default String listFormat() {
        StringBuilder builder = new StringBuilder("[");

        if (length() > 0)
            builder.append(objectAt(0));

        for (int index = 1; index < length(); index++) {
            builder.append(", ");
            builder.append(objectAt(index));
        }

        builder.append("]");
        return builder.toString();
    }

    /**
     * Copies this sequence into a new bare array.
     *
     * @return a new array {@code A} with the same length as this
     * sequence and {@code A[k].equals(this.objectAt(k))}.
     */
    @SuppressWarnings("unchecked") public default E[] toArray() {
        E[] result = (E[]) new Object[length()];

        for (Cursor cursor : traverse())
            result[cursor.index()] = objectAt(cursor);

        return result;
    }

    /**
     * Copies this sequence into a new list.
     *
     * @return a new list {@code L} with the same number of elements
     * as this sequence and {@code L.get(k).equals(this.objectAt(k))}.
     */
    public default List<E> toList() {
        ArrayList<E> result = new ArrayList<E>(length());

        for (E element : this)
            result.add(element);

        return result;
    }

    /**
     * Concatenates sequences into a single list.
     *
     * @param <E> the type of elements stored in the sequence.
     *
     * @param sequences the sequences to concatenate.
     *
     * @return a new list containing the elements from each input
     * sequence concatenated from left to right.
     */
    @SafeVarargs public static <E> List<E> concat(Iterable<E>... sequences) {
        return concat(Arrays.asList(sequences));
    }

    /**
     * Concatenates sequences into a single list.
     *
     * @param <E> the type of elements stored in the sequence.
     *
     * @param sequences the sequences to concatenate.
     *
     * @return a new list containing the elements from each input
     * sequence concatenated from left to right.
     */
    public static <E> List<E> concat(Collection<Iterable<E>> sequences) {
        ArrayList<E> result = new ArrayList<E>();

        for (Iterable<E> sequence : sequences)
            for (E element : sequence)
                result.add(element);

        return result;
    }

    @Override public Iterator<E> iterator() {
        return new SequenceIterator<E>(this);
    }
}
