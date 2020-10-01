
package jam.collect;

import java.util.Collection;
import java.util.Set;

import jam.lang.JamException;
import jam.stream.JamStreams;

/**
 * Provides a mutable table of records indexed by a key.
 *
 * @param <K> the runtime type of the table keys.
 *
 * @param <V> the runtime type of the table values.
 */
public interface JamTable<K, V> extends TableView<K, V> {
    /**
     * Deletes all records from this table.
     */
    public abstract void delete();

    /**
     * Deletes the record indexed by a given key (a no-op if there is
     * no matching record).
     *
     * @param key the key of the record to delete.
     *
     * @return {@code true} iff the matching record was deleted.
     */
    public abstract boolean delete(K key);

    /**
     * Deletes the records indexed by collection of keys.
     *
     * @param keys the keys of the records to delete.
     */
    public default void delete(Collection<K> keys) {
        keys.forEach(key -> delete(key));
    }

    /**
     * Extracts the key field from a record.
     *
     * @param record a record to examine.
     *
     * @return the key field for the specified record.
     */
    public abstract K getKey(V record);

    /**
     * Inserts a new record into this table; this operation will fail
     * if this table already contains a record with the same key.
     *
     * @param record the record to insert.
     *
     * @return {@code true} iff the record was successfully inserted.
     */
    public abstract boolean insert(V record);

    /**
     * Inserts new records into this table.
     *
     * @param records the records to insert.
     *
     * @throw RuntimeException if any of the insertions fail.
     */
    public default void insert(Collection<V> records) {
        for (V record : records)
            if (!insert(record))
                throw JamException.runtime("Failed to insert record: [%s].", getKey(record));
    }

    /**
     * Returns a read-only view of the keys in this table.
     *
     * @return a read-only view of the keys in this table.
     */
    @Override public default Set<K> keys() {
        return JamStreams.toHashSet(select().stream().map(record -> getKey(record)));
    }

    /**
     * Updates an existing record in this table; this operation will
     * fail unless this table already contains a record with the same
     * key.
     *
     * @param record the record to update.
     *
     * @return {@code true} iff the record was successfully updated.
     */
    public abstract boolean update(V record);

    /**
     * Updates existing records in this table.
     *
     * @param records the records to update.
     *
     * @throw RuntimeException if any of the updateions fail.
     */
    public default void update(Collection<V> records) {
        for (V record : records)
            if (!update(record))
                throw JamException.runtime("Failed to update record: [%s].", getKey(record));
    }

    /**
     * Inserts a new record or updates an existing record in this
     * table; this operation should always succeed.
     *
     * @param record the record to insert or update.
     */
    public default void upsert(V record) {
        if (contains(getKey(record)))
            update(record);
        else
            insert(record);
    }

    /**
     * Inserts new records or updates existing records in this table.
     *
     * @param records the records to insert or update.
     */
    public default void upsert(Collection<V> records) {
        records.forEach(record -> upsert(record));
    }
}
