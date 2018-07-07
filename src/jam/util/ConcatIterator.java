
package jam.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Implements an iterator that provides a concatenated view of
 * multiple collections and iterates over them seamlessly as one.
 *
 * @param E the type of object stored in the collections.
 */
public final class ConcatIterator<E> implements Iterator<E> {
    // The active iterator...
    private Iterator<E> active;

    // Iterators remaining to be processed...
    private final Queue<Iterator<E>> queue;

    @SuppressWarnings("unchecked")
    private ConcatIterator(LinkedList<Iterator<E>> iterators) {
        if (iterators.isEmpty()) {
            //
            // The active iterator should never be null, so provide an
            // empty one...
            //
            this.active = (Iterator<E>) Collections.emptyList().iterator();
        }
        else {
            this.active = iterators.removeFirst();
        }

        this.queue  = iterators;
    }

    /**
     * Creates a new concatenating iterator for a collection of
     * underlying iterators.
     *
     * @param <E> the type of object returned by the underlying iterators.
     *
     * @param iterators the underlying iterators to traverse.
     *
     * @return a new concatenating iterator that will traverse through
     * the underlying iterators in their collection order.
     */
    public static <E> Iterator<E> concat(Collection<Iterator<E>> iterators) {
        return new ConcatIterator<E>(new LinkedList<Iterator<E>>(iterators));
    }

    /**
     * Creates a new concatenating iterator over a collection of
     * collections.
     *
     * @param <E> the type of object stored in the collections.
     *
     * @param collections the collections over which to iterate.
     *
     * @return a new concatenating iterator that will iterate over the
     * given collections in their collection order.
     */
    public static <E> Iterator<E> over(Collection<Collection<E>> collections) {
        LinkedList<Iterator<E>> iterators = new LinkedList<Iterator<E>>();

        for (Collection<E> collection : collections)
            iterators.add(collection.iterator());

        return new ConcatIterator<E>(iterators);
    }

    @Override public boolean hasNext() {
        return active.hasNext() || !queue.isEmpty();
    }
        
    @Override public E next() {
        if (!active.hasNext())
            active = queue.remove();

        return active.next();
    }

    @Override public void remove() {
        active.remove();
    }
}
