
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
     * The unit-offset index equal to {@code 1}.
     */
    public static final UnitIndex FIRST = instance(1);

    /**
     * Returns the unit-offset index equal to {@code 1}.
     *
     * @return the unit-offset index equal to {@code 1}.
     */
    public static UnitIndex first() {
        return FIRST;
    }

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
     * Determines whether this index is less than another.
     *
     * @param that the index to compare to this index.
     *
     * @return {@code true} iff this index is less than the input
     * index.
     */
    public boolean LT(UnitIndex that) {
        return this.unitIndex < that.unitIndex;
    }

    /**
     * Determines whether this index is less than or equal to another.
     *
     * @param that the index to compare to this index.
     *
     * @return {@code true} iff this index is less than or equal to
     * the input index.
     */
    public boolean LE(UnitIndex that) {
        return this.unitIndex <= that.unitIndex;
    }

    /**
     * Determines whether this index is equal to another.
     *
     * @param that the index to compare to this index.
     *
     * @return {@code true} iff this index is equal to the input
     * index.
     */
    public boolean EQ(UnitIndex that) {
        return this.unitIndex == that.unitIndex;
    }

    /**
     * Determines whether this index is greater than or equal to
     * another.
     *
     * @param that the index to compare to this index.
     *
     * @return {@code true} iff this index is greater than or equal to
     * the input index.
     */
    public boolean GE(UnitIndex that) {
        return this.unitIndex >= that.unitIndex;
    }

    /**
     * Determines whether this index is greater than another.
     *
     * @param that the index to compare to this index.
     *
     * @return {@code true} iff this index is greater than the input
     * index.
     */
    public boolean GT(UnitIndex that) {
        return this.unitIndex > that.unitIndex;
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
     * Identifies indexes equal to {@code 1}.
     *
     * @return {@code true} iff the unit index is equal to {@code 1}.
     */
    public boolean isFirst() {
        return unitIndex == 1;
    }

    /**
     * Identifies arrays that contain an element with this index.
     *
     * @param array the array to examine.
     *
     * @return {@code true} iff the specified array contains an
     * element with this index.
     */
    public boolean isIndexOf(Object[] array) {
        return unitIndex <= array.length;
    }

    /**
     * Identifies lists that contain an element with this index.
     *
     * @param list the list to examine.
     *
     * @return {@code true} iff the specified list contains an
     * element with this index.
     */
    public boolean isIndexOf(List<?> list) {
        return unitIndex <= list.size();
    }

    /**
     * Subtracts an amount from this index and returns the result in a
     * new index object; this index is unchanged.
     *
     * @param subtrahend the amount to subtract from this index.
     *
     * @return a new index object with the index decremented by the
     * specified ammount.
     *
     * @throws RuntimeException if the resulting unit index is not
     * positive.
     */
    public UnitIndex minus(int subtrahend) {
        return plus(-subtrahend);
    }

    /**
     * Returns the unit index that is one greater than this index;
     * this index is unchanged.
     *
     * @return the unit index that is one greater than this index.
     */
    public UnitIndex next() {
        return instance(unitIndex + 1);
    }

    /**
     * Adds an amount to this index and returns the result in a new
     * index object; this index is unchanged.
     *
     * @param addend the amount to add to this index.
     *
     * @return a new index object with the index incremented by the
     * specified ammount.
     *
     * @throws RuntimeException if the resulting unit index is not
     * positive.
     */
    public UnitIndex plus(int addend) {
        return instance(unitIndex + addend);
    }

    /**
     * Returns the unit index that is one less than this index;
     * this index is unchanged.
     *
     * @return the unit index that is one less than this index.
     *
     * @throws RuntimeException if this is the lowest unit index
     * ({@code 1}).
     */
    public UnitIndex prev() {
        return instance(unitIndex - 1);
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
