
package jam.collect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides utility methods operating on lists.
 */
public final class JamLists {
    private JamLists() {}

    /**
     * Adds the elements provided by an iteration to a list.
     *
     * @param items the items to add to the list.
     *
     * @return the input list, for operator chaining.
     */
    public static <E> List<E> addAll(List<E> list, Iterable<E> items) {
        items.forEach(item -> list.add(item));
        return list;
    }

    /**
     * Returns an {@code ArrayList} containing the elements provided
     * by an iteration.
     *
     * @param items the items to populate the list.
     *
     * @return an {@code ArrayList} containing the elements in the
     * specified iteration.
     */
    public static <E> List<E> arrayList(Iterable<E> items) {
        return addAll(new ArrayList<E>(), items);
    }

    /**
     * Returns a {@code LinkedList} containing the elements provided
     * by an iteration.
     *
     * @param items the items to populate the list.
     *
     * @return a {@code LinkedList} containing the elements in the
     * specified iteration.
     */
    public static <E> List<E> linkedList(Iterable<E> items) {
        return addAll(new LinkedList<E>(), items);
    }
}
