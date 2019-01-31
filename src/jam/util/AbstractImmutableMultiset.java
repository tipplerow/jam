
package jam.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Multiset;

/**
 * Provides a skeletal implementation of an immutable multiset.
 */
public abstract class AbstractImmutableMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    private final Multiset<E> set;

    /**
     * Creates a new immutable multiset.
     *
     * @param set the underlying multiset, whose ownership is
     * transfered to the new multiset.
     */
    protected AbstractImmutableMultiset(Multiset<E> set) {
        this.set = set;
    }

    @Override public boolean add(E element) {
        throw new UnsupportedOperationException();
    }

    @Override public int add(E element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Override public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean contains(Object element) {
        return set.contains(element);
    }

    @Override public int count(Object element) {
        return set.count(element);
    }

    @Override public Set<E> elementSet() {
        return set.elementSet();
    }

    @Override public Set<Multiset.Entry<E>> entrySet() {
        return set.entrySet();
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Multiset) && equalsMultiset((Multiset) obj);
    }

    private boolean equalsMultiset(Multiset that) {
        return this.set.equals(that);
    }

    @Override public int hashCode() {
        return set.hashCode();
    }

    @Override public Iterator<E> iterator() {
        return set.iterator();
    }

    @Override public boolean remove(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override public int remove(Object element, int occurrences) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean removeAll(Collection<?> elements) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean retainAll(Collection<?> elements) {
        throw new UnsupportedOperationException();
    }

    @Override public int setCount(E element, int count) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean setCount(E element, int oldCount, int newCount) {
        throw new UnsupportedOperationException();
    }

    @Override public int size() {
        return set.size();
    }
}
