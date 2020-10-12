
package jam.collect;

import java.util.Map;
import java.util.function.Function;

/**
 * Implements the {@code MapTable} interface using an in-memory map
 * for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the table records.
 */
public abstract class AbstractMapTable<K, V> extends AbstractMapView<K, V> implements MapTable<K, V> {
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
    protected AbstractMapTable(Map<K, V> backMap, Function<V, K> keyFunc) {
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
        return backMap.remove(getKey(record)) != null;
    }

    @Override public boolean insert(V record) {
        K key = getKey(record);

        if (backMap.containsKey(key)) {
            return false;
        }
        else {
            backMap.put(key, record);
            return true;
        }
    }

    @Override public void store(V record) {
        backMap.put(getKey(record), record);
    }

    @Override public boolean update(V record) {
        K key = getKey(record);

        if (backMap.containsKey(key)) {
            backMap.put(key, record);
            return true;
        }
        else {
            return false;
        }
    }
}
