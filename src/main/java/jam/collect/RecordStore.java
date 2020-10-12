
package jam.collect;

/**
 * Defines a common interface for containers that store records
 * (either in memory or in a persistent medium).
 *
 * @param <V> the runtime type for the records.
 */
public interface RecordStore<V> {
    /**
     * Deletes all records from this store.
     */
    public abstract void delete();

    /**
     * Deletes a record from this store if it exists.
     *
     * @param record the record to delete.
     *
     * @return {@code true} iff the record was deleted.
     */
    public abstract boolean delete(V record);

    /**
     * Deletes records from this store.
     *
     * @param records the records to delete.
     */
    public default void delete(Iterable<V> records) {
        records.forEach(record -> delete(record));
    }

    /**
     * Stores a record in this store: new records will be inserted,
     * existing records will be updated.
     *
     * @param record the record to store (insert or update).
     */
    public abstract void store(V record);

    /**
     * Stores records in this store: new records will be inserted,
     * existing records will be updated.
     *
     * @param records the records to store (insert or update).
     */
    public default void store(Iterable<V> records) {
        records.forEach(record -> store(record));
    }
}
