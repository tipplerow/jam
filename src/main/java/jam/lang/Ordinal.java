
package jam.lang;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Provides a skeletal implementation for classes that are indexed
 * by a long integer.
 *
 * <p>Ordinal objects have a <em>natural ordering</em> imposed by
 * their index, but ordinal objects may only be compared to others
 * with the same runtime class.
 */
public abstract class Ordinal implements Comparable<Ordinal> {
    private final long index;

    /**
     * Creates a new ordinal object with a specific index.
     *
     * @param index the ordinal index.
     */
    protected Ordinal(long index) {
        this.index = index;
    }

    /**
     * Creates a new ordinal object with the next index produced by
     * the automatic ordinal index counter.
     *
     * @param index the automatic ordinal index counter.
     */
    protected Ordinal(OrdinalIndex index) {
        this(index.next());
    }

    /**
     * Sorts a collection of {@code Ordinal} objects by their index.
     *
     * @param <E> the runtime ordinal type.
     *
     * @param ordinals the {@code Ordinal} objects to sort.
     *
     * @return a new {@code TreeSet} containing the (unique) ordinal
     * objects ordered by their index.
     */
    public static <E extends Ordinal> TreeSet<E> sort(Collection<E> ordinals) {
        //
        // Dump into a tree set to order by index...
        //
        return new TreeSet<E>(ordinals);
    }

    /**
     * Returns the index for this object.
     *
     * @return the index for this object.
     */
    public final long getIndex() {
        return index;
    }

    /**
     * Compares the index of another ordinal object with the index of
     * this object.
     *
     * <p>Ordinal objects with different runtime types should not be
     * stored in the same collections, so there should be no need to
     * compare ordinal objects of different runtime types. Therefore,
     * this method throws a {@link ClassCastException} unless the
     * input object has the same runtime type as this object.
     *
     * @param that the ordinal object to compare to this object.
     *
     * @return a negative integer, zero, or positive integer according
     * to whether the index of this object is less than, equal to, or
     * greater than the index of the input object.
     *
     * @throws ClassCastException unless the input object has the same
     * runtime time as this object.
     */
    @Override public int compareTo(Ordinal that) {
        if (!this.getClass().equals(that.getClass()))
            throw new ClassCastException("Invalid comparison.");

        return Long.compare(this.index, that.index);
    }

    /**
     * Implements an equality test for this object.
     *
     * <p>Ordinal objects are equal if and only if they have the same
     * runtime class and identical indexes.  Objects with identical
     * indexes but but different runtime classes are <em>not</em>
     * equal.
     *
     * @param that the object to test for equality.
     *
     * @return {@code true} iff the input object has the same index
     * and runtime type as this.
     */
    @Override public boolean equals(Object that) {
        return this.getClass().equals(that.getClass()) && equalsOrdinal((Ordinal) that);
    }

    private boolean equalsOrdinal(Ordinal that) {
        return this.index == that.index;
    }

    @Override public int hashCode() {
        return (int) index;
    }

    @Override public String toString() {
        return String.format("%s(%d)", getClass().getSimpleName(), index);
    }
}
