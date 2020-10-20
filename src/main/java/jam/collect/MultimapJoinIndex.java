
package jam.collect;

import java.util.Set;

import com.google.common.collect.SetMultimap;

/**
 * Provides an implementation of the {@code JoinIndex} interface using
 * multimaps for in-memory storage.
 */
public abstract class MultimapJoinIndex<K1 extends Comparable<? super K1>,
                                        K2 extends Comparable<? super K2>,
                                        JK extends JoinKey<K1, K2>> implements JoinIndex<K1, K2, JK> {
    /**
     * The index over the first key component.
     */
    protected final SetMultimap<K1, JK> index1;

    /**
     * The index over the second key component.
     */
    protected final SetMultimap<K2, JK> index2;

    /**
     * Creates an index using multimaps for in-memory storage.
     *
     * @param index1 the index over the first key component.
     *
     * @param index2 the index over the second key component.
     */
    protected MultimapJoinIndex(SetMultimap<K1, JK> index1,
                                SetMultimap<K2, JK> index2) {
        this.index1 = index1;
        this.index2 = index2;
    }

    /**
     * Adds a join key to this index (a no-op if the key is already
     * present).
     *
     * @param joinKey the join key to add.
     */
    public void add(JK joinKey) {
        index1.put(joinKey.getKey1(), joinKey);
        index2.put(joinKey.getKey2(), joinKey);
    }

    /**
     * Removes a join key from this index (a no-op if the key is not
     * present).
     *
     * @param joinKey the join key to remove.
     */
    public void remove(JK joinKey) {
        index1.remove(joinKey.getKey1(), joinKey);
        index2.remove(joinKey.getKey2(), joinKey);
    }

    @Override public Set<JK> selectKey1(K1 key1) {
        return index1.get(key1);
    }

    @Override public Set<JK> selectKey2(K2 key2) {
        return index2.get(key2);
    }
}
