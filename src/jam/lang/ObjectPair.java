
package jam.lang;

/**
 * Defines an immutable pair of objects.
 */
public abstract class ObjectPair<FIRST, SECOND> {
    /**
     * The first object in the pair.
     */
    public final FIRST first;

    /**
     * The second object in the pair.
     */
    public final SECOND second;

    /**
     * Creates a new object pair.
     *
     * @param first the first object of the pair.
     *
     * @param second the second object of the pair.
     */
    protected ObjectPair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof ObjectPair) && equalsObjectPair((ObjectPair) that);
    }

    private boolean equalsObjectPair(ObjectPair that) {
        return this.first.equals(that.first) && this.second.equals(that.second);
    }

    @Override public int hashCode() {
        return first.hashCode() + 37 * second.hashCode();
    }

    @Override public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
