
package jam.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.util.MapUtil;
import jam.util.SetUtil;

/**
 * Maintains an in-memory cache of records backed by a persistent
 * database table.
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
     * The persistent database table.
     */
    protected final SQLTable<K, V> table;

    /**
     * Creates a new cache with a backing database table to provide
     * persistent storage and to compute records on demand.
     *
     * @param table the backing database table.
     */
    protected SQLCache(SQLTable<K, V> table) {
        this.table = table;
        this.cache = table.load();
    }

    /**
     * Computes a new record on demand.
     *
     * @param key the key of the desired record.
     *
     * @return the new record for the specified key.
     */
    protected abstract V compute(K key);

    /**
     * Computes new records on demand.
     *
     * @param keys the keys of the desired records.
     *
     * @return the new records with keys in the same order as the
     * iterator of the input collection.
     */
    protected List<V> compute(Collection<K> keys) {
        List<V> records = new ArrayList<V>(keys.size());

        for (K key : keys)
            records.add(compute(key));

        return records;
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
            // Compute records for the missing keys...
            //
            List<V> records = compute(missing);

            // Add the new records to the in-memory cache and the
            // persistent database table...
            updateCache(records);
            updateTable(records);
        }

        // Pull the records from the cache in the order requested;
        // all keys are guaranteed to be present in the cache...
        return MapUtil.get(cache, keys);
    }

    private void updateCache(List<V> records) {
        JamLogger.info("Adding [%d] records to cache [%s]...", records.size(), getName());

        for (V record : records)
            cache.put(table.getKey(record), record);

        JamLogger.info("Added [%d] records to cache [%s].", records.size(), getName());
    }

    private void updateTable(List<V> records) {
        table.store(records);
    }

    /**
     * Returns the key for a given record.
     *
     * @param record a record to be indexed.
     *
     * @return the key for the specified record.
     */
    public K getKey(V record) {
        return table.getKey(record);
    }
}
