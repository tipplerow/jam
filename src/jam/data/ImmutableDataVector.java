
package jam.data;

import java.util.List;
import java.util.Set;

final class ImmutableDataVector<K> implements DataVector<K> {
    private final DataVector<K> vector;

    ImmutableDataVector(DataVector<K> vector) {
        this.vector = vector;
    }

    @Override public int indexOf(K key) {
        return vector.indexOf(key);
    }

    @Override public K keyAt(int index) {
        return vector.keyAt(index);
    }

    @Override public List<K> keyList() {
        return vector.keyList();
    }

    @Override public Set<K> keySet() {
        return vector.keySet();
    }

    @Override public boolean contains(K key) {
        return vector.contains(key);
    }

    @Override public double get(K key) {
        return vector.get(key);
    }

    @Override public double get(int index) {
        return vector.get(index);
    }

    @Override public int length() {
        return vector.length();
    }

    @Override public void set(K key, double value) {
        throw new UnsupportedOperationException("Value assignment is not supported.");
    }

    @Override public void set(int index, double value) {
        throw new UnsupportedOperationException("Value assignment is not supported.");
    }

    @Override public boolean equals(Object obj) {
        return vector.equals(obj);
    }

    @Override public int hashCode() {
        return vector.hashCode();
    }

    @Override public String toString() {
        return vector.toString();
    }
}
