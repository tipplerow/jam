
package jam.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Provides the means to decorate a map with additional methods while
 * maintaining the standard {@code #java.util.Map} interface.
 *
 * @param K the type of keys maintained by this map.
 *
 * @param V the type of values maintained by this map.
 */
public abstract class MapWrapper<K, V> extends AbstractMap<K, V> {
    /**
     * The underlying map.
     */
    protected final Map<K, V> map;

    /**
     * Creates a new wrapper for a given map.  
     *
     * <p>The new wrapper assumes ownership of the map; all mutable
     * methods will write through to the map.
     *
     * @param map the map to wrap.
     */
    protected MapWrapper(Map<K, V> map) {
        this.map = map;
    }

    @Override public void clear() {
        map.clear();
    }

    @Override public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
  
    @Override public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override public V get(Object key) {
        return map.get(key);
    }

    @Override public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override public Set<K> keySet() {
        return map.keySet();
    }

    @Override public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override public V remove(Object key) {
        return map.remove(key);
    }

    @Override public int size() {
        return map.size();
    }

    @Override public Collection<V> values() {
        return map.values();
    }
}
