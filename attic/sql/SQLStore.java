
package jam.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jam.util.MapUtil;
import jam.util.SetUtil;

/**
 * Maintains a persistent database table and computes and stores new
 * records on demand.
 *
 * @param <K> the runtime type of the record keys.
 *
 * @param <V> the runtime type of the record values.
 */
public abstract class SQLStore<K, V> {
    private final SQLKeyTable<K, V> table;

    /**
     * Creates a new SQL store backed by a database table.
     *
     * @param table the persistent backing database table.
     */
    protected SQLStore(SQLKeyTable<K, V> table) {
        this.table = table;
        table.require();
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
     * Retrieves a record from this store, computing it on-demand and
     * storing in the database if necessary.
     *
     * @param key the key of the desired record.
     *
     * @return the record with the specfied key.
     *
     * @throws RuntimeException unless the record could be retrieved
     * from the database or computed on demand.
     */
    public V get(K key) {
        return get(List.of(key)).get(0);
    }

    /**
     * Retrieves records from this store, computing them on-demand and
     * storing them the database if necessary.
     *
     * @param keys the keys of the desired records.
     *
     * @return the records with the specfied keys in the ordered
     * returned by the collection iterator.
     *
     * @throws RuntimeException unless all records could be retrieved
     * from the database or computed on demand.
     */
    public synchronized List<V> get(Collection<K> keys) {
        //
        // Load all previously computed records...
        //
        Map<K, V> tableRecords = table.load();

        // Identify keys from the input collection that are not
        // present in the table...
        Set<K> missingKeys = SetUtil.missing(tableRecords.keySet(), keys);

        if (!missingKeys.isEmpty()) {
            // Compute records for each missing key...
            List<V> computedRecords = compute(missingKeys);

            // ...and add the new records to the database table.
            table.store(computedRecords);
            addRecords(computedRecords, tableRecords);
        }

        return MapUtil.get(tableRecords, keys);
    }

    private void addRecords(Collection<V> records, Map<K, V> map) {
        for (V record : records)
            addRecord(record, map);
    }

    private void addRecord(V record, Map<K, V> map) {
        map.put(table.getKey(record), record);
    }

    /**
     * Retrieves records from this store, computing them on-demand and
     * storing them the database if necessary.
     *
     * @param keys the keys of the desired records.
     *
     * @return the records with the specfied keys in a new {@code HashMap}.
     *
     * @throws RuntimeException unless all records could be retrieved
     * from the database or computed on demand.
     */
    public HashMap<K, V> hash(Collection<K> keys) {
        List<V> recordList = get(keys);
        HashMap<K, V> recordMap = new HashMap<K, V>(keys.size());

        addRecords(recordList, recordMap);
        return recordMap;
    }
}
