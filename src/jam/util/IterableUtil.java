
package jam.util;

import java.util.Iterator;

import jam.lang.ObjectUtil;

/**
 * Provides utility methods operating on {@code Iterable} objects.
 */
public final class IterableUtil {
    /**
     * Determines whether two iterable objects are equal.
     *
     * @param <T> the common runtime type.
     *
     * @param obj1 the first iterable object to compare.
     *
     * @param obj2 the second iterable object to compare.
     *
     * @return {@code true} iff the two iterable objects return
     * identical sequences.
     */
    public static <T> boolean equals(Iterable<? extends T> obj1, Iterable<? extends T> obj2) {
        Iterator<? extends T> iter1 = obj1.iterator();
        Iterator<? extends T> iter2 = obj2.iterator();

        while (iter1.hasNext()) {
            //
            // The first iterator has another object, the second
            // iterator must also...
            //
            if (!iter2.hasNext())
                return false;

            // The next object in the first iterator must be identical
            // to the next object in the second iterator...
            if (!ObjectUtil.equals(iter1.next(), iter2.next()))
                return false;
        }

        // The first iterator has no more objects, neither may the
        // second...
        return !iter2.hasNext();
    }
}
