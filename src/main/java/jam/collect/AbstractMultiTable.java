
package jam.collect;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Implements the {@code MultiTable} interface using an in-memory
 * multimap for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the table records.
 */
public abstract class AbstractMultiTable<K, V> extends AbstractMultiView<K, V> implements MultiTable<K, V> {
    /**
     * The function that extracts keys from records.
     */
    protected final Function<V, K> keyFunc;

    /**
     * Creates a new table with an underlying map for storage and a
     * function to extract keys from records.
     *
     * @param backMap the backing map for record storage.
     *
     * @param keyFunc a function to extract keys from records.
     */
    protected AbstractMultiTable(SetMultimap<K, V> backMap, Function<V, K> keyFunc) {
        super(backMap);
        this.keyFunc = keyFunc;
    }

    /**
     * Extracts the key from a record.
     *
     * @param record the record of interest.
     *
     * @return the key for the specified record.
     */
    public K getKey(V record) {
        return keyFunc.apply(record);
    }

    @Override public void delete() {
        backMap.clear();
    }

    @Override public boolean delete(V record) {
        return backMap.remove(getKey(record), record);
    }

    @Override public void store(V record) {
        backMap.put(getKey(record), record);
    }
}
