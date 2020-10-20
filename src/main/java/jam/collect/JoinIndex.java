
package jam.collect;

import java.util.Set;

/**
 * Defines indexes for the individual components of a join key.
 */
public interface JoinIndex<K1 extends Comparable<? super K1>,
                           K2 extends Comparable<? super K2>,
                           JK extends JoinKey<K1, K2>> {
    /**
     * Selects all join keys with a given first key.
     *
     * @param key1 the first key of the join keys to select.
     *
     * @return a set containing all join keys with the specified
     * first key.
     */
    public abstract Set<JK> selectKey1(K1 key1);

    /**
     * Selects all join keys with a given second key.
     *
     * @param key2 the second key of the join keys to select.
     *
     * @return a set containing all join keys with the specified
     * second key.
     */
    public abstract Set<JK> selectKey2(K2 key2);
}
