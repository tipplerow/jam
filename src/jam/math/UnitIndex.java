
package jam.math;

import java.util.Comparator;
import java.util.List;

/**
 * Defines unit-offset indexes for arrays and lists.
 *
 * <p>The scientific literature often uses unit-offset indexes to
 * identify items in arrays or lists: the first item in an array of
 * length {@code L} has index 1 and the last has index {@code L}.
 * Java arrays and lists, on the other hand, use zero-offset indexing:
 * the first element in a list or array of length {@code L} has index
 * 0 and the last has index {@code L - 1}. To avoid endless confusion,
 * we use this class to encode the distinction.
 */
public final class UnitIndex implements Comparable<UnitIndex> {
    private final int unitIndex;

    private UnitIndex(int unitIndex) {
        validate(unitIndex);
        this.unitIndex = unitIndex;
    }

    /**
     * A comparator that arranges indexes in increasing order.
     */
    public static final Comparator<UnitIndex> COMPARATOR =
        new Comparator<UnitIndex>() {
            @Override public int compare(UnitIndex pos1, UnitIndex pos2) {
                return Integer.compare(pos1.unitIndex, pos2.unitIndex);
            }
        };

    /**
     * The range of valid unit-offset positions.
     */
    public static final IntRange RANGE = IntRange.POSITIVE;

    /**
     * Returns the unit-offset index at a fixed location.
     *
     * @param unitIndex the unit-offset index.
     *
     * @return the unit-offset index at the specifed location.
     *
     * @throws RuntimeException unless the index is positive.
     */
    public static UnitIndex instance(int unitIndex) {
        return new UnitIndex(unitIndex);
    }

    /**
     * Returns the unit-offset index encoded in a string.
     *
     * @param s the string to parse.
     *
     * @return the unit-offset index encoded in the specified
     * string.
     *
     * @throws RuntimeException unless the string is a properly
     * formatted integer.
     */
    public static UnitIndex parse(String s) {
        return instance(Integer.parseInt(s.trim()));
    }

    /**
     * Extracts the array element with the index corresponding to
     * this position.
     *
     * @param <E> the runtime type for the array elements.
     *
     * @param array the array of elements on which to operate.
     *
     * @return the array element with the index corresponding to
     * this position.
     */
    public <E> E get(E[] array) {
        return array[unitIndex - 1];
    }

    /**
     * Extracts the list element with the index corresponding to
     * this position.
     *
     * @param <E> the runtime type for the list elements.
     *
     * @param list the list of elements on which to operate.
     *
     * @return the list element with the index corresponding to
     * this position.
     */
    public <E> E get(List<E> list) {
        return list.get(unitIndex - 1);
    }

    /**
     * Returns the zero-offset array or list index corresponding to
     * this position.
     *
     * @return the zero-offset array or list index corresponding to
     * this position.
     */
    public int getListIndex() {
        return unitIndex - 1;
    }

    /**
     * Returns the unit-offset index.
     *
     * @return the unit-offset index.
     */
    public int getUnitIndex() {
        return unitIndex;
    }

    /**
     * Replaces the element at this position in an array with the
     * specified element.
     *
     * @param <E> the runtime type for the array elements.
     *
     * @param array the array of elements on which to operate.
     *
     * @param element the element to assign.
     *
     * @throws RuntimeException if this position is out of range.
     */
    public <E> void set(E[] array, E element) {
        array[unitIndex - 1] = element;
    }

    /**
     * Replaces the element at this position in a list with the
     * specified element.
     *
     * @param <E> the runtime type for the list elements.
     *
     * @param list the list of elements on which to operate.
     *
     * @param element the element to assign.
     *
     * @return the element previously at this location.
     *
     * @throws RuntimeException if the list is immutable or this
     * position if out of range.
     */
    public <E> E set(List<E> list, E element) {
        return list.set(unitIndex - 1, element);
    }

    /**
     * Ensures that a unit-offset index is valid.
     *
     * @param unitIndex a unit-offset index to validate.
     *
     * @throws RuntimeException unless the index is positive.
     */
    public void validate(int unitIndex) {
        RANGE.validate("Unit index", unitIndex);
    }

    @Override public int compareTo(UnitIndex that) {
        return COMPARATOR.compare(this, that);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof UnitIndex) && equalsPosition((UnitIndex) obj);
    }

    private boolean equalsPosition(UnitIndex that) {
        return this.unitIndex == that.unitIndex;
    }

    @Override public int hashCode() {
        return unitIndex;
    }

    @Override public String toString() {
        return Integer.toString(unitIndex);
    }
}
