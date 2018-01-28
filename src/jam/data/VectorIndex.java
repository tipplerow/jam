
package jam.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Allows arrays, lists, and vectors to be indexed by key strings.
 */
public final class VectorIndex {
    private final Map<String, Integer> index = new LinkedHashMap<String, Integer>();

    /**
     * Creates a new element index for a set of keys.
     *
     * <p>The keys will be indexed in the order returned by the set
     * iterator, beginning with zero.
     *
     * @param keys the element keys.
     */
    public VectorIndex(Set<String> keys) {
        fillIndex(keys);
    }

    private void fillIndex(Set<String> keys) {
        int cursor = 0;

        for (String key : keys)
            index.put(key, cursor++);
    }

    /**
     * Creates a new element index for a set of keys.
     *
     * <p>The keys will be indexed in the order provided, beginning
     * with zero.
     *
     * @param keys the element keys.
     *
     * @throws IllegalArgumentException if any keys are duplicated.
     */
    public VectorIndex(String... keys) {
        fillIndex(keys);
    }

    private void fillIndex(String... keys) {
        for (int jj = 0; jj < keys.length; jj++) {
            String key = keys[jj];

            if (index.containsKey(key))
                throw new IllegalArgumentException(String.format("Duplicate key: [%s].", key));

            index.put(key, jj);
        }
    }

    /**
     * Identifies keys mapped by this index.
     *
     * @param key the key to examine.
     *
     * @return {@code true} iff this index contains the specified key.
     */
    public boolean contains(String key) {
        return index.containsKey(key);
    }

    /**
     * Returns the index of a given key.
     *
     * @param key the key to index.
     *
     * @return the index of the specified key.
     *
     * @throws IllegalArgumentException unless this index contains the
     * specified key.
     */
    public int indexOf(String key) {
        validateKey(key);
        return index.get(key).intValue();
    }

    /**
     * Returns the keys contained in the index.
     *
     * @return the keys contained in the index, in a list {@code L}
     * ordered such that the key in element {@code L.get(k)} has the
     * index {@code k}.
     */
    public List<String> keys() {
        return new ArrayList<String>(index.keySet());
    }

    /**
     * Returns the number of keys contained in this index.
     *
     * @return the number of keys contained in this index.
     */
    public int size() {
        return index.size();
    }

    /**
     * Ensures that this index contains a specific key.
     *
     * @param key the key to examine.
     *
     * @throws IllegalArgumentException unless this index contains the
     * specified key.
     */
    public void validateKey(String key) {
        if (!contains(key))
            throw new IllegalArgumentException(String.format("Undefined key: [%s].", key));
    }

    @Override public String toString() {
        return index.toString();
    }
}
