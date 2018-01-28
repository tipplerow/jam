
package jam.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implements a read-only iterator whose {@code remove()} method
 * throws an {@code UnsupportedOperationException}.
 *
 * @param E the type of object stored in the collection.
 */
public final class ReadOnlyIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;

    private ReadOnlyIterator(Collection<E> collection) {
        this.iterator = collection.iterator();
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
        return new ReadOnlyIterator<E>(collection);
    }

    @Override public boolean hasNext() {
        return iterator.hasNext();
    }
        
    @Override public E next() {
        return iterator.next();
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
