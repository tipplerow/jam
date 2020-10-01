
package jam.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Implements the {@code TableView} interface with storage provided by
 * an in-memory map.
 */
public final class MapView<K, V> implements TableView<K, V> {
    private final Map<K, V> map;

    private MapView(Map<K, V> map) {
        this.map = map;
    }

    /**
     * Wraps a map in a table view; subsequent changes to the map will
     * be reflected in the view.
     *
     * @param <K> the runtime time for the map keys.
     *
     * @param <V> the runtime time for the map values.
     *
     * @param map the map to view.
     *
     * @return a map view backed by a copy of the specified map.
     */
    public static <K, V> MapView<K, V> wrap(Map<K, V> map) {
        return new MapView<K, V>(map);
    }

    @Override public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override public int count() {
        return map.size();
    }

    @Override public Set<K> keys() {
        return map.keySet();
    }

    @Override public Collection<V> select() {
        return map.values();
    }

    @Override public V select(K key) {
        return map.get(key);
    }

    @Override public String toString() {
        return map.toString();
    }
}

