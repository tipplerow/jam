
package jam.flat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jam.util.ConcatIterator;
import jam.util.ReadOnlyIterator;

/**
 * Provides a many-to-many mapping table.
 *
 * @param <K1> the primary key for the joining records.
 *
 * @param <K2> the foreign key for the joining records.
 *
 * @param <V> the runtime type of the joining records.
 */
public abstract class JoinTable<K1, K2, V extends JoinRecord<K1, K2>> extends RecordStore<V> {
    private final Map<K1, Map<K2, V>> primaryIndex = new LinkedHashMap<K1, Map<K2, V>>();
    private final Map<K2, Map<K1, V>> foreignIndex = new LinkedHashMap<K2, Map<K1, V>>();

    private Map<K2, V> primaryInner(K1 primaryKey, boolean create) {
        Map<K2, V> innerMap = primaryIndex.get(primaryKey);

        if (innerMap == null && create) {
            innerMap = new LinkedHashMap<K2, V>();
            primaryIndex.put(primaryKey, innerMap);
        }

        return innerMap;
    }

    private Map<K1, V> foreignInner(K2 foreignKey, boolean create) {
        Map<K1, V> innerMap = foreignIndex.get(foreignKey);

        if (innerMap == null && create) {
            innerMap = new LinkedHashMap<K1, V>();
            foreignIndex.put(foreignKey, innerMap);
        }

        return innerMap;
    }

    private static int count(Map map) {
        if (map != null)
            return map.size();
        else
            return 0;
    }

    private static void delete(Map map, Object key) {
        if (map != null)
            map.remove(key);
    }

    private static <V> List<V> select(Map<?, V> map) {
        if (map != null)
            return new ArrayList<V>(map.values());
        else
            return List.of();
    }

    /**
     * Identifies records contained in this table.
     *
     * @param primaryKey the primary key to search for.
     *
     * @param foreignKey the foreign key to search for.
     *
     * @return {@code true} iff this table contains a record with the
     * specified keys.
     */
    public boolean contains(K1 primaryKey, K2 foreignKey) {
        return containsPrimary(primaryKey) && containsForeign(foreignKey);
    }

    /**
     * Identifies primary keys contained in this table.
     *
     * @param key the primary key to search for.
     *
     * @return {@code true} iff this table contains one or more
     * records with the specified primary key.
     */
    public boolean containsPrimary(K1 key) {
        return primaryIndex.containsKey(key);
    }

    /**
     * Identifies foreign keys contained in this table.
     *
     * @param key the foreign key to search for.
     *
     * @return {@code true} iff this table contains one or more
     * records with the specified foreign key.
     */
    public boolean containsForeign(K2 key) {
        return foreignIndex.containsKey(key);
    }

    /**
     * Returns the number of unique primary keys in this table.
     *
     * @return the number of unique primary keys in this table.
     */
    public int countPrimary() {
        return primaryIndex.size();
    }

    /**
     * Returns the number of records with a given primary key.
     *
     * @param key the primary key to search for.
     *
     * @return the number of records with the specified primary key.
     */
    public int countPrimary(K1 key) {
        return count(primaryInner(key, false));
    }

    /**
     * Returns the number of unique foreign keys in this table.
     *
     * @return the number of unique foreign keys in this table.
     */
    public int countForeign() {
        return foreignIndex.size();
    }

    /**
     * Returns the number of records with a given foreign key.
     *
     * @param key the foreign key to search for.
     *
     * @return the number of records with the specified foreign key.
     */
    public int countForeign(K2 key) {
        return count(foreignInner(key, false));
    }

    /**
     * Removes a record from this table (has no effect if there is no
     * matching record).
     *
     * @param primaryKey the primary key of the record to remove.
     *
     * @param foreignKey the foreign key of the record to remove.
     */
    public void delete(K1 primaryKey, K2 foreignKey) {
        Map<K2, V> primaryInner = primaryInner(primaryKey, false);
        Map<K1, V> foreignInner = foreignInner(foreignKey, false);

        if (primaryInner != null) {
            primaryInner.remove(foreignKey);

            if (primaryInner.isEmpty())
                primaryIndex.remove(primaryKey);
        }

        if (foreignInner != null) {
            foreignInner.remove(primaryKey);

            if (foreignInner.isEmpty())
                foreignIndex.remove(foreignKey);
        }
    }

    /**
     * Removes all records with a matching primary key.
     *
     * @param primaryKey the primary key of the records to remove.
     */
    public void deletePrimary(K1 primaryKey) {
        delete(selectPrimary(primaryKey));
    }

    private void delete(List<V> records) {
        for (V record : records)
            delete(record.getPrimaryKey(), record.getForeignKey());
    }

    /**
     * Removes all records with matching primary keys.
     *
     * @param keys the primary keys of the records to remove.
     */
    public void deletePrimary(Collection<K1> keys) {
        for (K1 key : keys)
            deletePrimary(key);
    }

    /**
     * Removes all records with a matching foreign key.
     *
     * @param foreignKey the foreign key of the records to remove.
     */
    public void deleteForeign(K2 foreignKey) {
        delete(selectForeign(foreignKey));
    }

    /**
     * Removes all records with matching foreign keys.
     *
     * @param keys the foreign keys of the records to remove.
     */
    public void deleteForeign(Collection<K2> keys) {
        for (K2 key : keys)
            deleteForeign(key);
    }

    /**
     * Selects a single record matching desired primary and foreign
     * keys.
     *
     * @param primaryKey the primary key to match.
     *
     * @param foreignKey the foreign key to match.
     *
     * @return the single record with the specified keys ({@code null}
     * if there is no matching record).
     */
    public V select(K1 primaryKey, K2 foreignKey) {
        Map<K2, V> primaryInner = primaryInner(primaryKey, false);

        if (primaryInner != null)
            return primaryInner.get(foreignKey);
        else
            return null;
    }

    /**
     * Selects records that match a given primary key.
     *
     * @param key the primary key of the records to select.
     *
     * @return a list containing the records that match the specified
     * primary key.
     */
    public List<V> selectPrimary(K1 key) {
        return select(primaryInner(key, false));
    }

    /**
     * Selects records that match a given foreign key.
     *
     * @param key the foreign key of the records to select.
     *
     * @return a list containing the records that match the specified
     * foreign key.
     */
    public List<V> selectForeign(K2 key) {
        return select(foreignInner(key, false));
    }

    @Override public int count() {
        int count = 0;

        for (Map<K2, V> foreignMap : primaryIndex.values())
            count += foreignMap.size();

        return count;
    }

    @Override public void insert(V record) {
        K1 primaryKey = record.getPrimaryKey();
        K2 foreignKey = record.getForeignKey();

        Map<K2, V> primaryInner = primaryInner(primaryKey, true);
        Map<K1, V> foreignInner = foreignInner(foreignKey, true);

        primaryInner.put(foreignKey, record);
        foreignInner.put(primaryKey, record);
    }

    @Override public Iterator<V> iterator() {
        List<Collection<V>> values =
            new ArrayList<Collection<V>>(primaryIndex.size());

        for (Map<K2, V> foreignMap : primaryIndex.values())
            values.add(foreignMap.values());

        return ReadOnlyIterator.create(ConcatIterator.over(values));
    }
}
