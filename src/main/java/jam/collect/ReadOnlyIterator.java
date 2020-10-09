
package jam.collect;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implements a read-only iterator whose {@code remove()} method
 * throws an {@code UnsupportedOperationException}.
 *
 * @param <E> the type of object stored in the collection.
 */
public final class ReadOnlyIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;

    private ReadOnlyIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    /**
     * Creates a new read-only iterator for a given collection.
     *
     * @param <E> the type of object stored in the collection.
     *
     * @param collection the collection over which to iterate.
     *
     * @return a new read-only iterator for the given collection.
     */
    public static <E> Iterator<E> create(Collection<E> collection) {
        return create(collection.iterator());
    }

    /**
     * Creates a read-only view of an iterator.
     *
     * @param <E> the type of object returned by the iterator.
     *
     * @param iterator the iterator to force as read-only.
     *
     * @return a new read-only view of the given iterator.
     */
    public static <E> Iterator<E> create(Iterator<E> iterator) {
        return new ReadOnlyIterator<E>(iterator);
    }

    @Override public boolean hasNext() {
        return iterator.hasNext();
    }
        
    @Override public E next() {
        return iterator.next();
    }

    /**
     * Throws an {@code UnsupportedOperationException} always.
     *
     * @throws UnsupportedOperationException always.
     */
    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
