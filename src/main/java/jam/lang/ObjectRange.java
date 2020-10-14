
package jam.lang;

/**
 * Defines a half-open range for a comparable class of objects.
 * Following the convention used by {@code List.subList()} and
 * {@code String.substring()}, the lower bound of the range is
 * inclusive but the upper bound is exclusive.
 */
public abstract class ObjectRange<E extends Comparable<E>> {
    private final E begin;
    private final E end;

    /**
     * Creates the half-open range {@code [begin, end)}.
     *
     * @param begin the first value included in the range.
     *
     * @param end the first value <em>excluded from</em> the
     * range.
     *
     * @throws RuntimeException if {@code begin > end}.
     */
    protected ObjectRange(E begin, E end) {
        validate(begin, end);

        this.begin = begin;
        this.end = end;
    }

    public static <E extends Comparable<E>> void validate(E begin, E end) {
        if (begin.compareTo(end) > 0)
            throw JamException.runtime("Invalid range: [%s, %s].", begin, end);
    }

    /**
     * Identifies objects in this range.
     *
     * @param obj an object to test.
     *
     * @return {@code true} iff this range contains the specified
     * object, that is, iff {@code begin <= obj && obj < end}.
     */
    public boolean contains(E obj) {
        return begin.compareTo(obj) <= 0 && obj.compareTo(end) < 0;
    }

    /**
     * Returns the inclusive lower bound of this range: objects less
     * than this bound are excluded from the range.
     *
     * @return the inclusive lower bound of this range.
     */
    public final E begin() {
        return begin;
    }

    /**
     * Returns the exclusive upper bound of this range:  objects
     * greater than or equal to this bound are excluded from the
     * range.
     *
     * @return the exclusive upper bound of this range.
     */
    public final E end() {
        return end;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object obj) {
        return ObjectUtil.equalsClass(this, obj) && equalsRange((ObjectRange<E>) obj);
    }

    private boolean equalsRange(ObjectRange<E> that) {
        return this.begin.equals(that.begin) && this.end.equals(that.end);
    }

    @Override public int hashCode() {
        return begin.hashCode() + 37 * end.hashCode();
    }

    @Override public String toString() {
        return String.format("%s([%s, %s))", getClass().getSimpleName(), begin, end);
    }
}
