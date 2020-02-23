
package jam.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.util.MapUtil;
import jam.util.SetUtil;

/**
 * Maintains an in-memory cache of records backed by a persistent
 * database store.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLCache<K, V> {
    /**
     * The in-memory cache.
     */
    protected final Map<K, V> cache;

    /**
     * The persistent database store.
     */
    protected final SQLStore<K, V> store;

    /**
     * Creates a new cache with a backing database store to provide
     * persistent storage and to compute records on demand.
     *
     * @param store the backing database store.
     */
    protected SQLCache(SQLStore<K, V> store) {
        this.store = store;
        this.cache = new HashMap<K, V>();
    }

    /**
     * Returns the name of this cache (for message logging).
     *
     * @return the name of this cache (for message logging).
     */
    public abstract String getName();

    /**
     * Removes all cached records from memory (but retains them in the
     * persistent database).
     */
    public void clear() {
        JamLogger.info("Clearing cache [%s]...", getName());

        synchronized (cache) {
            cache.clear();
        }

        JamLogger.info("Cleared cache [%s].", getName());
    }

    /**
     * Returns the record for a given key.
     *
     * <p>If the record is not already contained in this cache, it
     * will be computed and stored in the persistent database.
     *
     * @param key the key of interest.
     *
     * @return the record for the specified key.
     */
    public V get(K key) {
        return get(List.of(key)).get(0);
    }

    /**
     * Returns the records for a collection of keys.
     *
     * <p>Records that are not already contained in this cache
     * will be computed and stored in the persistent database.
     *
     * @param keys the keys of interest.
     *
     * @return the records for the specified keys (in the order
     * returned by the collection iterator).
     */
    public List<V> get(Collection<K> keys) {
        JamLogger.info("Requesting [%d] records from cache [%s]...", keys.size(), getName());
        List<V> records = getSync(keys);

        JamLogger.info("Returned [%d] records from cache [%s].", records.size(), getName());
        return records;
    }

    private List<V> getSync(Collection<K> keys) {
        //
        // Identify keys from the input collection that are not
        // present in the cache...
        //
        Set<K> missing = SetUtil.missing(cache.keySet(), keys);

        if (!missing.isEmpty()) {
            //
            // Query the persistent store (and compute on-demand)
            // for each missing key...
            //
            List<V> records = store.get(missing);

            // Add the new records to the in-memory cache (the
            // persistent store is updated automatically)...
            updateCache(records);
        }

        // Pull the records from the cache in the order requested...
        return MapUtil.get(cache, keys);
    }

    private void updateCache(List<V> records) {
        JamLogger.info("Adding [%d] records to cache [%s]...", records.size(), getName());

        for (V record : records)
            cache.put(getKey(record), record);

        JamLogger.info("Added [%d] records to cache [%s].", records.size(), getName());
    }

    /**
     * Returns the key for a given record.
     *
     * @param record a record to be indexed.
     *
     * @return the key for the specified record.
     */
    public K getKey(V record) {
        return store.getKey(record);
    }
}
