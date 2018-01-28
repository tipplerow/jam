
package jam.lang;

/**
 * Identifies a unique element within a sequence during iteration over
 * all elements.
 */
public final class Cursor {
    private final int index;

    /**
     * Creates a new cursor pointing to the current element in an
     * iteration.
     *
     * @param index the current element index.
     */
    public Cursor(int index) {
        this.index = index;
    }

    /**
     * Returns the current element index.
     *
     * @return the current element index.
     */
    public int index() {
        return index;
    }

    @Override public boolean equals(Object that) {
        return (that instanceof Cursor) && equalsCursor((Cursor) that);
    }

    private boolean equalsCursor(Cursor that) {
        return this.index == that.index;
    }

    @Override public String toString() {
        return String.format("Cursor(%d)", index);
    }
}
