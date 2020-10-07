
package jam.collect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jam.lang.ClassUtil;
import jam.lang.ObjectUtil;

/**
 * Provides utility methods operating on collections.
 */
public final class JamCollections {
    private JamCollections() {}

    /**
     * Determines whether two collections contain exactly the same
     * items regardless of order or the underlying collection type.
     *
     * @param <V> the runtime time common to both collections.
     *
     * @param col1 the first collection to examine.
     *
     * @param col2 the second collection to examine.
     *
     * @return {@code true} iff the specified collections contain
     * exactly the same items.
     */
    public static <V> boolean equalsContents(Collection<? extends V> col1,
                                             Collection<? extends V> col2) {
        if (col1.size() != col2.size())
            return false;

        // A set provides the most efficient implementation of
        // containsAll(), so find a collection that implements
        // the Set interface or create one if necessary...
        Set<? extends V> set;
        Collection<? extends V> col;

        if (ClassUtil.implements_(col1, Set.class)) {
            set = (Set<? extends V>) col1;
            col = col2;
        }
        else if (ClassUtil.implements_(col2, Set.class)) {
            set = (Set<? extends V>) col2;
            col = col1;
        }
        else {
            set = new HashSet<V>(col1);
            col = col2;
        }

        return set.containsAll(col);
    }

    /**
     * Determines whether two collections contain exactly the same
     * items in exactly the same order (returned by their iterators).
     *
     * @param <V> the runtime time common to both collections.
     *
     * @param col1 the first collection to examine.
     *
     * @param col2 the second collection to examine.
     *
     * @return {@code true} iff the specified collections contain
     * exactly the same items in exactly the same order (returned
     * by their iterators).
     */
    public static <V> boolean equalsOrdered(Collection<? extends V> col1,
                                            Collection<? extends V> col2) {
        if (col1.size() != col2.size())
            return false;

        Iterator<? extends V> iter1 = col1.iterator();
        Iterator<? extends V> iter2 = col2.iterator();

        while (iter1.hasNext()) {
            assert iter2.hasNext();

            V item1 = iter1.next();
            V item2 = iter2.next();

            if (!ObjectUtil.equals(item1, item2))
                return false;
        }

        return true;
    }

    /**
     * Peeks at the first item in a collection; the collection is
     * unchanged.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection to examine.
     *
     * @return the first element returned by the collection iterator
     * (or {@code null} if the collection is empty).
     */
    public static <V> V peek(Collection<V> collection) {
        Iterator<V> iterator = collection.iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }
}

