
package jam.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides a read-only indexed view of a collection.
 *
 * @param <K> the runtime key type.
 *
 * @param <V> the runtime record (value) type.
 */
public abstract class IndexView<K, V> extends AbstractMap<K, V> {
    private final Map<K, V> map;

    /**
     * Creates a new read-only index view of a collection.
     *
     * @param map an empty map to provide the underlying storage.
     *
     * @param objects the objects to index.
     *
     * @param getKey a function mapping the objects in the collection
     * to a unique key.
     *
     * @throws IllegalArgumentException unless the input map is empty
     * and all objects in the collection have a unique key.
     */
    protected IndexView(Map<K, V> map, Collection<V> objects, Function<V, K> getKey) {
        if (!map.isEmpty())
            throw new IllegalArgumentException("The underlying map must be empty.");

        for (V object : objects)
            MapUtil.putUnique(map, getKey.apply(object), object);

        this.map = Collections.unmodifiableMap(map);
    }

    @Override public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
