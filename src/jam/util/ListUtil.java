
package jam.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import jam.lang.ObjectFactory;
import jam.math.JamRandom;

/**
 * Provides utility operations on lists.
 */
public final class ListUtil {
    private ListUtil() {}

    /**
     * Applies a function (or lambda expression) to each element of a
     * collection.
     *
     * @param <T> element type for the source list.
     *
     * @param <R> element type for the result list.
     *
     * @param source the collection to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a list whose kth element is the result of applying the
     * input function to the kth element of the source.
     */
    public static <T, R> List<R> apply(Collection<T> source, Function<T, R> function) {
        List<R> result = new ArrayList<R>(source.size());

        for (T t : source)
            result.add(function.apply(t));

        return result;
    }

    /**
     * Returns an object factory that will create empty {@code ArrayList} instances.
     *
     * @param <E> the type of element to be stored in the list.
     *
     * @return an object factory that will create empty {@code ArrayList} instances.
     */
    public static <E> ObjectFactory<ArrayList<E>> arrayFactory() {
        return new ArrayFactory<E>();
    }

    private static final class ArrayFactory<E> implements ObjectFactory<ArrayList<E>> {
        @Override public ArrayList<E> newInstance() {
            return new ArrayList<E>();
        }
    }

    /**
     * Concatenates lists into a single list.
     *
     * @param <V> the element type.
     *
     * @param lists the lists to concatenate.
     *
     * @return a new list containing all elements from the input
     * lists, joined from left to right.
     */
    public static <V> List<V> cat(Collection<List<V>> lists) {
        List<V> result = new ArrayList<V>();

        for (List<V> list : lists)
            result.addAll(list);

        return result;
    }

    /**
     * Concatenates lists into a single list.
     *
     * @param <V> the element type.
     *
     * @param lists the lists to concatenate.
     *
     * @return a new list containing all elements from the input
     * lists, joined from left to right.
     */
    @SafeVarargs public static <V> List<V> cat(List<V>... lists) {
        return cat(Arrays.asList(lists));
    }

    /**
     * Returns the first element from a list.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @return the first element from a list.
     *
     * @throws NoSuchElementException if the list is empty.
     */
    public static <V> V first(List<V> list) {
        if (list.isEmpty())
            throw new NoSuchElementException();

        return list.get(0);
    }

    /**
     * Returns the last element from a list.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @return the last element from a list.
     *
     * @throws NoSuchElementException if the list is empty.
     */
    public static <V> V last(List<V> list) {
        if (list.isEmpty())
            throw new NoSuchElementException();

        return list.get(list.size() - 1);
    }

    /**
     * Returns an object factory that will create empty {@code LinkedList} instances.
     *
     * @param <E> the type of element to be stored in the list.
     *
     * @return an object factory that will create empty {@code LinkedList} instances.
     */
    public static <E> ObjectFactory<LinkedList<E>> linkedFactory() {
        return new LinkedFactory<E>();
    }

    private static final class LinkedFactory<E> implements ObjectFactory<LinkedList<E>> {
        @Override public LinkedList<E> newInstance() {
            return new LinkedList<E>();
        }
    }

    /**
     * Selects one list element at random.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @param random the random number source.
     *
     * @return an element selected at random.
     */
    public static <V> V select(List<V> list, JamRandom random) {
        return list.get(random.nextInt(list.size()));
    }
    
    /**
     * Randomly reorders elements in a list (in place).
     *
     * @param list the list to shuffle.
     *
     * @param random the random number source.
     */
    public static void shuffle(List<?> list, JamRandom random) {
        for (int k = list.size() - 1; k > 0; k--)
            Collections.swap(list, k, random.nextInt(k));
    }

    /**
     * Constructs the transpose of a ragged array.
     *
     * <p>For example, the following ragged array:
     * <pre>
     *    [A, B]
     *    [C, D, E, F]
     *    [G]
     * </pre>
     * has this ragged transpose:
     * <pre>
     *    [A, C, G]
     *    [B, D]
     *    [E]
     *    [F]
     * </pre>
     *
     * @param <V> the element type.
     *
     * @param rows the row lists, not necessarily of the same
     * size.
     *
     * @return the transposed ragged array.
     */
    public static <V> List<List<V>> transpose(List<List<V>> rows) {
        //
        // First determine the number of rows in the transpose: the
        // maximum size of input rows...
        //
        int transposeRowCount = 0;

        for (List<V> row : rows)
            transposeRowCount = Math.max(transposeRowCount, row.size());

        // Create the empty rows of the transpose...
        List<List<V>> transpose = new ArrayList<List<V>>(transposeRowCount);

        for (int transposeRowIndex = 0; transposeRowIndex < transposeRowCount; transposeRowIndex++)
            transpose.add(new ArrayList<V>());

        // Now fill the transpose...
        for (List<V> row : rows)
            for (int transposeRowIndex = 0; transposeRowIndex < row.size(); transposeRowIndex++)
                transpose.get(transposeRowIndex).add(row.get(transposeRowIndex));

        return transpose;
    }

    /**
     * Creates a read-only list view for a collection of elements.
     *
     * @param <V> the element type.
     *
     * @param elements the elements to be viewed.
     *
     * @return a new unmodifiable list containing the elements of the
     * collection in the order returned by its iterator.
     */
    public static <V> List<V> view(Collection<V> elements) {
        return Collections.unmodifiableList(new ArrayList<V>(elements));
    }
}
