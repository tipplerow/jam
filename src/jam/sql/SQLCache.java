
package jam.sql;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
public abstract class SQLCache<K, V> extends AbstractCollection<V> {
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
        table.require();

        this.table = table;
        this.cache = table.load();
    }

    private void updateCache(V record) {
        cache.put(table.getKey(record), record);
    }

    private void updateCache(List<V> records) {
        for (V record : records)
            updateCache(record);
    }

    private void updateTable(V record) {
        table.store(record);
    }

    private void updateTable(List<V> records) {
        table.store(records);
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
     * Returns the runtime class for the key objects.
     *
     * @return the runtime class for the key objects.
     */
    public abstract Class getKeyClass();

    /**
     * Returns the name of this cache (for message logging).
     *
     * @return the name of this cache (for message logging).
     */
    public abstract String getName();

    /**
     * Returns the runtime class for the record objects.
     *
     * @return the runtime class for the record objects.
     */
    public abstract Class getRecordClass();

    /**
     * Identifies records contained in this cache.
     *
     * @param key the key of a record in question.
     *
     * @return {@code true} iff this cache contains a record with the
     * specified key.
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * Retrieves a record by key (but does not compute missing keys
     * on demand).
     *
     * @param key the key of the desired record.
     *
     * @return the record with the desired key, or {@code null} if
     * there is no matching key.
     */
    public V fetch(K key) {
        return cache.get(key);
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
    public V require(K key) {
        return require(List.of(key)).get(0);
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
    public synchronized List<V> require(Collection<K> keys) {
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

    @Override public boolean add(V record) {
        if (containsKey(getKey(record)))
            return false;

        updateCache(record);
        updateTable(record);

        return true;
    }

    @Override public boolean addAll(Collection<? extends V> records) {
        //
        // We cannot add duplicate records to the database,
        // so select those not already present...
        //
        List<V> missing = new ArrayList<V>(records.size());

        for (V record : records)
            if (!containsKey(getKey(record)))
                missing.add(record);

        if (missing.isEmpty())
            return false;

        updateCache(missing);
        updateTable(missing);

        return true;
    }

    /**
     * Removes all cached records from memory (but retains them in the
     * persistent database).
     */
    @Override public synchronized void clear() {
        cache.clear();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean contains(Object obj) {
        if (obj == null)
            return false;

        if (obj.getClass().equals(getKeyClass()))
            return containsKey((K) obj);

        if (obj.getClass().equals(getRecordClass()))
            return containsKey(getKey((V) obj));

        return false;
    }

    @Override public Iterator<V> iterator() {
        return cache.values().iterator();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean remove(Object obj) {
        if (!contains(obj))
            return false;

        K key = getKey((V) obj);

        cache.remove(key);
        table.remove(key);

        return true;
    }

    @Override public int size() {
        return cache.size();
    }

    @Override public String toString() {
        return String.format("SQLCache(%s)", getName());
    }
}
