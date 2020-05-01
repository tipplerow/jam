
package jam.sql;

import java.util.Collection;

/**
 * A database table that supports bulk import of {@code BulkRecord}s.
 *
 * @param <V> the runtime type of the table records.
 */
public interface BulkTable<V extends BulkRecord> {
    /**
     * Returns the database where this table resides.
     *
     * @return the database where this table resides.
     */
    public abstract SQLDb db();

    /**
     * Returns the name of this table.
     *
     * @return the name of this table.
     */
    public abstract String getTableName();

    /**
     * Inserts a collection of records using the bulk copy operation
     * of the database.
     *
     * @param records the records to insert.
     *
     * @return whether the bulk import was successful.
     */
    public default boolean insert(Collection<V> records) {
        return db().bulkCopy(getTableName(), records);
    }
}
