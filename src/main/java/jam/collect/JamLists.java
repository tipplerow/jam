
package jam.collect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jam.math.JamRandom;
import jam.util.ListUtil;

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

    /**
     * Copies a sequence of items into an {@code ArrayList} and then
     * randomly shuffles the items.
     *
     * @param items the items to populate the list.
     *
     * @return an {@code ArrayList} containing the elements in the
     * specified iteration shuffled into a random order.
     */
    public static <E> List<E> shuffle(Iterable<E> items) {
        return shuffle(items, JamRandom.global());
    }

    /**
     * Copies a sequence of items into an {@code ArrayList} and then
     * randomly shuffles the items.
     *
     * @param items the items to populate the list.
     *
     * @param random the random number source.
     *
     * @return an {@code ArrayList} containing the elements in the
     * specified iteration shuffled into a random order.
     */
    public static <E> List<E> shuffle(Iterable<E> items, JamRandom random) {
        List<E> list = arrayList(items);
        ListUtil.shuffle(list, random);
        return list;
    }

    /**
     * Swaps the location of two elements in a list.
     *
     * @param list the list on which to operate.
     *
     * @param ind1 the index of the first element to swap.
     *
     * @param ind2 the index of the second element to swap.
     *
     * @throws IndexOutOfBoundsException unless both indexes are
     * valid.
     */
    public static <E> void swap(List<E> list, int ind1, int ind2) {
        E tmp = list.get(ind1);

        list.set(ind1, list.get(ind2));
        list.set(ind2, tmp);
    }
}
