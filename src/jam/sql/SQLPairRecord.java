
package jam.sql;

/**
 * Encapsulates a <em>pair record</em>.
 *
 * <p>A pair record represents a many-to-many relationship.  It
 * contains two values, each of which may be a key for other data
 * items.  An article keyword list is a good example.  One article
 * contains multiple keywords, and one keyword appears in multiple
 * articles.  We might want to use either the article or the keyword
 * as the key in a search.
 *
 * @param <K1> the runtime type of the first key.
 *
 * @param <K2> the runtime type of the second key.
 */
public abstract class SQLPairRecord<K1, K2> {
    private final K1 key1;
    private final K2 key2;

    /**
     * Creates a new pair record.
     *
     * @param key1 the first key.
     *
     * @param key2 the second key.
     */
    protected SQLPairRecord(K1 key1, K2 key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    /**
     * Returns the first key of the pair.
     *
     * @return the first key of the pair.
     */
    public K1 getKey1() {
        return key1;
    }

    /**
     * Returns the second key of the pair.
     *
     * @return the second key of the pair.
     */
    public K2 getKey2() {
        return key2;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object obj) {
        return obj != null
            && obj.getClass().equals(this.getClass())
            && equalsRecord((SQLPairRecord<K1, K2>) obj);
    }

    private boolean equalsRecord(SQLPairRecord<K1, K2> that) {
        return this.key1.equals(that.key1) && this.key2.equals(that.key2);
    }

    @Override public int hashCode() {
        return key1.hashCode() + 31 * key2.hashCode();
    }
}
