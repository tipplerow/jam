
package jam.flat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jam.util.ReadOnlyIterator;

/**
 * Provides an indexed table of unique flat-file records.
 * 
 * @param <K> the runtime type of the primary record key.
 *
 * @param <V> the runtime type of the record class.
 */
public abstract class FlatTable<K, V extends FlatRecord<K>> extends RecordStore<V> {
    private final Map<K, V> records = new LinkedHashMap<K, V>();

    /**
     * Identifies records in this table.
     *
     * @param key the primary key to search for.
     *
     * @return {@code true} iff this table contains a record with the
     * specified key.
     */
    public boolean contains(K key) {
        return records.containsKey(key);
    }

    /**
     * Removes a record from this table (has no effect if there is no
     * matching record).
     *
     * @param key the primary key of the record to remove.
     */
    public void delete(K key) {
        records.remove(key);
    }

    /**
     * Removes records from this table.
     *
     * @param keys the primary keys of the records to remove.
     */
    public void delete(Collection<K> keys) {
        for (K key : keys)
            delete(key);
    }

    /**
     * Returns the record with a given key.
     *
     * @param key the primary key of the record to select.
     *
     * @return the record with the specified primary key (or
     * {@code null} if there is no matching record).
     */
    public V select(K key) {
        return records.get(key);
    }

    /**
     * Selects records by their primary keys.
     *
     * @param keys the primary keys of the records to select.
     *
     * @return a list containing the records that match the specified
     * primary keys (non-matching records will be omitted rather than
     * adding {@code null} values to the list).
     */
    public List<V> select(Collection<K> keys) {
        List<V> records = new ArrayList<V>(keys.size());

        for (K key : keys) {
            V record = select(key);

            if (record != null)
                records.add(record);
        }

        return records;
    }

    @Override public int count() {
        return records.size();
    }

    @Override public void insert(V record) {
        records.put(record.getPrimaryKey(), record);
    }

    @Override public Iterator<V> iterator() {
        return ReadOnlyIterator.create(records.values());
    }
}
