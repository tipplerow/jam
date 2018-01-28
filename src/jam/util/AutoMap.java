
package jam.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Provides a map that supports on-demand object creation.
 *
 * <p>When a user calls {@code get(key)} with a key that is not
 * contained in the map, a new object is created on-demand by a
 * factory method passed to the map when it was constructed.
 */
public final class AutoMap<K, V> extends MapWrapper<K, V> {
    private final Function<K, V> factory;

    private AutoMap(Map<K, V> map, Function<K, V> factory) {
        super(map);
        this.factory = factory;
    }

    /**
     * Creates a new {@code AutoMap} backed by a {@code HashMap}.
     *
     * @param <K> the type of keys maintained by the map.
     *
     * @param <V> the type of values maintained by the map.
     *
     * @param factory the factory function used to create on-demand
     * instances.
     *
     * @return a new {@code AutoMap} backed by a {@code HashMap}.
     */
    public static <K, V> AutoMap<K, V> hash(Function<K, V> factory) {
        return new AutoMap<K, V>(new HashMap<K, V>(), factory);
    }

    /**
     * Creates a new {@code AutoMap} backed by a {@code TreeMap}.
     *
     * @param <K> the type of keys maintained by the map.
     *
     * @param <V> the type of values maintained by the map.
     *
     * @param factory the factory function used to create on-demand
     * instances.
     *
     * @return a new {@code AutoMap} backed by a {@code TreeMap}.
     */
    public static <K, V> AutoMap<K, V> tree(Function<K, V> factory) {
        return new AutoMap<K, V>(new TreeMap<K, V>(), factory);
    }

    /**
     * Returns the factory function used to create on-demand
     * instances.
     *
     * @return the factory function used to create on-demand
     * instances.
     */
    public Function<K, V> getFactory() {
        return factory;
    }

    /**
     * Returns the object to which the specified key is mapped; if no
     * mapping exists, this map will create a new on-demand instance
     * using the factory method and insert that object into the map.
     *
     * @param key the key of the object to retrieve (or create).
     *
     * @return the object to which the specified key is mapped, or a
     * new on-demand instance for the key.
     *
     * @throws RuntimeException if the key is not contained in this
     * map (and therefore a new on-demand instance is required) and
     * the key cannot be cast to the proper runtime type.
     */
    @SuppressWarnings("unchecked") @Override public V get(Object key) {
        if (!containsKey(key))
            put((K) key, factory.apply((K) key));

        return super.get(key);
    }
}

