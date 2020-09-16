
package jam.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.lang.JamException;
import jam.math.DoubleComparator;

/**
 * Provides a partial implementation of the {@code DataVector}
 * interface that is independent of the element storage scheme
 * (dense or sparse).
 */
public abstract class AbstractDataVector<K> implements DataVector<K> {
    /**
     * The fixed keys in index order.
     */
    protected final List<K> keyList;

    /**
     * Mapping from key to element index.
     */
    protected final Map<K, Integer> keyMap;

    /**
     * Creates a new data vector with fixed keys.
     *
     * @param keyList the element keys.
     *
     * @param copy whether or not to make deep copies of the keys.
     *
     * @throws IllegalArgumentException if the key list is empty or
     * contains duplicates.
     */
    protected AbstractDataVector(List<K> keyList, boolean copy) {
        if (copy) {
            this.keyList = Collections.unmodifiableList(new ArrayList<K>(keyList));
        }
        else {
            this.keyList = Collections.unmodifiableList(keyList);
        }

        this.keyMap = mapKeyList(keyList);
    }

    private static <V> Map<V, Integer> mapKeyList(List<V> keyList) {
        if (keyList.isEmpty())
            throw new IllegalArgumentException("Empty key list.");

        Map<V, Integer> keyMap = new HashMap<V, Integer>(keyList.size());

        for (int index = 0; index < keyList.size(); ++index)
            if (keyMap.put(keyList.get(index), index) != null)
                throw JamException.runtime("Duplicate key: [%s].", keyList.get(index));

        return Collections.unmodifiableMap(keyMap);
    }

    @Override public int indexOf(K key) {
        Integer index = keyMap.get(key);

        if (index != null)
            return index.intValue();
        else
            return KEY_MISSING;
    }

    @Override public K keyAt(int index) {
        return keyList.get(index);
    }

    @Override public List<K> keyList() {
        return keyList;
    }

    @Override public Set<K> keySet() {
        return keyMap.keySet();
    }

    @Override public boolean equals(Object that) {
        return (that instanceof DataVector) && equalsDataVector((DataVector) that);
    }

    private boolean equalsDataVector(DataVector that) {
        //
        // Must have identical dimensions...
        //
        if (this.length() != that.length())
            return false;

        // Must have identical keys in the same order...
        for (int index = 0; index < length(); ++index)
            if (!this.keyAt(index).equals(that.keyAt(index)))
                return false;

        // Must have identical elements...
        for (int index = 0; index < length(); ++index)
            if (DoubleComparator.DEFAULT.NE(this.get(index), that.get(index)))
                return false;

        return true;
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("Data vectors should not be used as keys.");
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < length(); ++index)
            builder.append(formatElement(index));

        return builder.toString();
    }

    private String formatElement(int index) {
        return String.format("(%s) => %s\n", keyAt(index), Double.toString(get(index)));
    }
}
