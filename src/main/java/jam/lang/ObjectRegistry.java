
package jam.lang;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * Maintains a cache of step records keyed by trial index and time
 * step.
 */
public abstract class ObjectRegistry<K, V> extends AbstractCollection<V> {
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private final Function<V, K> keyFunction;

    private final Map<K, V> registry = new HashMap<K, V>();

    /**
     * Creates a new, empty object registry.
     *
     * @param keyClass the class of the key type for the registry.
     *
     * @param valueClass the class of the value type for the registry.
     *
     * @param keyFunction the function that pulls the key from a
     * registry object.
     */
    protected ObjectRegistry(Class<K> keyClass,
                             Class<V> valueClass,
                             Function<V, K> keyFunction) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.keyFunction = keyFunction;
    }

    /**
     * Adds a <em>unique</em> object to this registry.
     *
     * @param obj the object to add.
     *
     * @return {@code true} if the registry does not already contain
     * an object with the same key.
     *
     * @throws RuntimeException if the registry already contains an
     * object with the same key.
     */
    @Override public boolean add(V obj) {
        K key = keyOf(obj);
        V prev = registry.put(key, obj);

        if (prev != null)
            throw JamException.runtime("Duplicate key: [%s].", key);
        else
            return true;
    }

    private K keyOf(V value) {
        return keyFunction.apply(value);
    }

    /**
     * Identifies objects in this registry.
     *
     * @param target either the key of the object in question or
     * the object itself.
     *
     * @return {@code true} iff the registry contains the target
     * object.
     */
    @SuppressWarnings("unchecked")
    @Override public boolean contains(Object target) {
        if (keyClass.isInstance(target))
            return containsKey((K) target);

        if (valueClass.isInstance(target))
            return containsValue((V) target);

        return false;
    }

    private boolean containsKey(K key) {
        return registry.containsKey(key);
    }

    private boolean containsValue(V value) {
        return registry.containsKey(keyOf(value));
    }

    /**
     * Returns the object with a given key.
     *
     * @param key the key of the desired object.
     *
     * @return the object with the given key ({@code null} if there is
     * no matching object).
     */
    public V get(K key) {
        return registry.get(key);
    }

    /**
     * Removes an object from this registry.
     *
     * @param target either the key of the object to remove or the
     * object itself.
     *
     * @return {@code true} iff the object was contained in the
     * registry and was removed.
     */
    @SuppressWarnings("unchecked")
    @Override public boolean remove(Object target) {
        if (keyClass.isInstance(target))
            return removeKey((K) target);

        if (valueClass.isInstance(target))
            return removeValue((V) target);

        return false;
    }

    private boolean removeKey(K key) {
        return registry.remove(key) != null;
    }

    private boolean removeValue(V value) {
        return removeKey(keyOf(value));
    }

    @Override public void clear() {
        registry.clear();
    }

    @Override public boolean isEmpty() {
        return registry.isEmpty();
    }

    @Override public Iterator<V> iterator() {
        return registry.values().iterator();
    }

    @Override public int size() {
        return registry.size();
    }
}
