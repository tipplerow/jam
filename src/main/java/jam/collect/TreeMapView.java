
package jam.collect;

import java.util.TreeMap;
import java.util.function.Function;

/**
 * Implements the {@code MapView} interface using an in-memory
 * {@code TreeMap} for record storage.
 *
 * @param <K> the runtime type of the record key.
 *
 * @param <V> the runtime type of the view records.
 */
public class TreeMapView<K, V> extends AbstractMapView<K, V> {
    /**
     * Creates a new view over an existing {@code TreeMap}.
     *
     * @param map the backing map for record storage.
     */
    protected TreeMapView(TreeMap<K, V> map) {
        super(map);
    }

    /**
     * Creates a new view populated by a collection of records.
     *
     * @param records the initial contents of the view.
     *
     * @param keyFunc a function to generate keys for the records.
     */
    protected TreeMapView(Iterable<V> records, Function<V, K> keyFunc) {
        super(JamMaps.tree(records, keyFunc));
    }

    /**
     * Creates a new view populated by a collection of records.
     *
     * @param <K> the runtime type for the record keys.
     *
     * @param <V> the runtime type for the record values.
     *
     * @param records the initial contents of the view.
     *
     * @param keyFunc a function to generate keys for the records.
     *
     * @return a new view containing the specified records with
     * keys generated by the input function.
     */
    public static <K, V> TreeMapView<K, V> create(Iterable<V> records, Function<V, K> keyFunc) {
        TreeMap<K, V> map = new TreeMap<K, V>();

        for (V record : records)
            map.put(keyFunc.apply(record), record);

        return new TreeMapView<K, V>(map);
    }
}

