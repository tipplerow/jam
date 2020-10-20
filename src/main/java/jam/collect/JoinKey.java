
package jam.collect;

import jam.lang.ObjectUtil;

/**
 * Provides a base class for composite keys that represent a
 * many-to-many mapping as in a database JOIN operation.
 */
public abstract class JoinKey<K1 extends Comparable<? super K1>,
                              K2 extends Comparable<? super K2>> implements Comparable<JoinKey<K1, K2>> {
    /**
     * The first key in the composite.
     */
    protected final K1 key1;

    /**
     * The second key in the composite.
     */
    protected final K2 key2;

    // The pre-computed hash code...
    private final int hash;

    /**
     * Creates a new composite joining key.
     *
     * @param key1 the first key in the composite.
     *
     * @param key2 the second key in the composite.
     */
    protected JoinKey(K1 key1, K2 key2) {
        this.key1 = key1;
        this.key2 = key2;
        this.hash = key1.hashCode() + 31 * key2.hashCode();
    }

    /**
     * Returns the first key in the composite.
     *
     * @return the first key in the composite.
     */
    public K1 getKey1() {
        return key1;
    }

    /**
     * Returns the second key in the composite.
     *
     * @return the second key in the composite.
     */
    public K2 getKey2() {
        return key2;
    }

    /**
     * Compares this join key to another so that join keys are ordered
     * by {@code key1} first and {@code key2} second.
     *
     * @param that a join key to compare with this.
     *
     * @return a negative integer, zero, or positive integer according
     * to whether this key compares as less than, equal to, or greater
     * than the input key.
     */
    @Override public int compareTo(JoinKey<K1, K2> that) {
        int cmp1 = this.key1.compareTo(that.key1);

        if (cmp1 != 0)
            return cmp1;
        else
            return this.key2.compareTo(that.key2);
    }

    /**
     * Implements the equality test for join keys.
     *
     * @param obj an object to test for equality.
     *
     * @return {@code true} iff the input object has the same concrete
     * class as this and has identical key components.
     */
    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object obj) {
        return ObjectUtil.equalsClass(this, obj) && equalsJoinKey((JoinKey<K1, K2>) obj);
    }

    private boolean equalsJoinKey(JoinKey<K1, K2> that) {
        return this.key1.equals(that.key1)
            && this.key2.equals(that.key2);
    }

    /**
     * Returns a hash code for this join key.
     *
     * @return a hash code for this join key.
     */
    @Override public int hashCode() {
        return hash;
    }

    /**
     * Returns a string of the form {@code ClassName(key1, key2)}.
     *
     * @return a string of the form {@code ClassName(key1, key2)}.
     */
    @Override public String toString() {
        return String.format("%s(%s, %s)", getClass().getSimpleName(), key1, key2);
    }
}
