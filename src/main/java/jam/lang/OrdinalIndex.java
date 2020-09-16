
package jam.lang;

/**
 * Encapsulates the management of ordinal indexes.
 */
public final class OrdinalIndex {
    private long count = 0;

    private OrdinalIndex() {}

    /**
     * Returns a new ordinal index initialized to zero.
     *
     * @return a new ordinal index initialized to zero.
     */
    public static OrdinalIndex create() {
        return new OrdinalIndex();
    }

    /**
     * Returns the index of the next object to be indexed (and
     * increments the underlying counter).
     *
     * @return the index of the next object to be indexed.
     */
    public long next() {
        return count++;
    }

    /**
     * Returns the index of the next object to be indexed (but leaves
     * the underlying counter unchanged).
     *
     * @return the index of the next object to be indexed.
     */
    public long peek() {
        return count;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof OrdinalIndex) && equalsOrdinalIndex((OrdinalIndex) that);
    }

    private boolean equalsOrdinalIndex(OrdinalIndex that) {
        return this.count == that.count;
    }

    @Override public int hashCode() {
        return (int) count;
    }

    @Override public String toString() {
        return String.format("OrdinalIndex(%d)", count);
    }
}
