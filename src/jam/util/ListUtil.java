
package jam.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jam.lang.ObjectFactory;
import jam.math.IntUtil;
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
    public static <T, R> List<R> apply(Collection<T> source, Function<? super T, ? extends R> function) {
        return StreamUtil.apply(source.stream(), function);
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
     * Determines whether a list ends with a particular sublist.
     *
     * @param <V> the element type.
     *
     * @param full the full list to examine.
     *
     * @param sub the sublist to test.
     *
     * @return {@code true} iff the last {@code k} elements of the
     * complete list {@code full} are the elements in the sublist
     * {@code sub}, where {@code k} is the length of {@code sub}.
     */
    public static <V> boolean endsWith(List<V> full, List<V> sub) {
        int fullLength = full.size();
        int subLength  = sub.size();

        return subLength <= fullLength && full.subList(fullLength - subLength, fullLength).equals(sub);
    }

    /**
     * Removes elements of a list that do not pass a filter predicate.
     *
     * @param <V> the runtime element type.
     *
     * @param list the list to be filtered.
     *
     * @param predicate the predicate filter to apply to each element
     * in the list.
     *
     * @return a list containing only elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> List<V> filter(List<V> list, Predicate<? super V> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Removes elements of a list that do not pass a filter predicate
     * <em>using a parallel stream</em>.
     *
     * @param <V> the runtime element type.
     *
     * @param list the list to be filtered.
     *
     * @param predicate the predicate filter to apply to each element
     * in the list.
     *
     * @return a list containing only elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> List<V> filterParallel(List<V> list, Predicate<? super V> predicate) {
        return list.parallelStream().filter(predicate).collect(Collectors.toList());
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
     * Retrieves elements from a list by index position.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @param indexes the indexes of the desired elements.
     *
     * @return a list {@code L} with {@code L.get(k)} containing the
     * element from position {@code indexes[k]} in the input list.
     *
     * @throws IndexOutOfBoundsException if any indexes are out of
     * bounds.
     */
    public static <V> List<V> get(List<V> list, int[] indexes) {
        List<V> elements = new ArrayList<V>(indexes.length);

        for (int index : indexes)
            elements.add(list.get(index));

        return elements;
    }

    /**
     * Determines whether a list is sorted by the natural ordering of
     * its elements.
     *
     * @param <V> the element type.
     *
     * @param list the list to examine.
     *
     * @return {@code true} iff the elements in the input list are in
     * non-decreasing natural order.
     */
    public static <V extends Comparable<? super V>> boolean isSorted(List<V> list) {
        //
        // Do this with an iterator rather than "get()" to avoid the
        // O(n^2) scaling for linked lists...
        //
        V prev = null;

        for (V curr : list)
            if (prev != null && prev.compareTo(curr) > 0)
                return false;
            else
                prev = curr;

        return true;
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

        // Linked lists maintain references to the first and last
        // elements, so this operation is constant time for array
        // lists and linked lists...
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
     * Copies the contents of an iteration into a new array list.
     *
     * @param <E> the element type.
     *
     * @param iterator the iterator to process.
     *
     * @return a new array list containing the items returned by the
     * given iterator.
     */
    public static <E> ArrayList<E> newArrayList(Iterator<E> iterator) {
        ArrayList<E> list = new ArrayList<E>();

        while (iterator.hasNext())
            list.add(iterator.next());

        return list;
    }

    /**
     * Selects one list element at random using the global random
     * number generator.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @return an element selected at random.
     *
     * @throws IllegalArgumentException if the list is empty.
     */
    public static <V> V select(List<V> list) {
        return select(list, JamRandom.global());
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
     *
     * @throws IllegalArgumentException if the list is empty.
     */
    public static <V> V select(List<V> list, JamRandom random) {
        if (list.isEmpty())
            throw new IllegalArgumentException("Empty list.");

        if (list.size() == 1)
            return list.get(0);
        
        return list.get(random.nextInt(list.size()));
    }
    
    /**
     * Selects a number of unique list elements (without replacement)
     * at random using the global random number generator.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @param count the number of elements to select.
     *
     * @return a new list containing {@code count} unique elements
     * selected at random (without replacement).
     *
     * @throws IllegalArgumentException if the number of selected
     * elements exceeds the size of the list.
     */
    public static <V> List<V> select(List<V> list, int count) {
        return select(list, count, JamRandom.global());
    }
    
    /**
     * Selects a number of unique list elements (without replacement)
     * at random.
     *
     * @param <V> the element type.
     *
     * @param list the list to select from.
     *
     * @param count the number of elements to select.
     *
     * @param random the random number source.
     *
     * @return a new list containing {@code count} unique elements
     * selected at random (without replacement).
     *
     * @throws IllegalArgumentException if the number of selected
     * elements exceeds the size of the list.
     */
    public static <V> List<V> select(List<V> list, int count, JamRandom random) {
        int[] indexes = IntUtil.sample(list.size(), count, random);
        List<V> selected = new ArrayList<V>(count);

        for (int index : indexes)
            selected.add(list.get(index));

        return selected;
    }

    /**
     * Randomly reorders elements in a list (in place) using the
     * global random number generator.
     *
     * @param list the list to shuffle.
     */
    public static void shuffle(List<?> list) {
        shuffle(list, JamRandom.global());
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
     * Splits a list into sublists of equal length, except for the
     * last sublist when the original list cannot be split exactly.
     *
     * <p>The returned sublists are backed by the original list,
     * so changes to a sublist are reflected in the original and
     * vice-versa.
     *
     * @param <V> the element type.
     *
     * @param list the list to split.
     *
     * @param size the desired size of the sublists.
     *
     * @return a list containing the sublists.
     *
     * @throws IllegalArgumentException unless the size is positive.
     */
    public static <V> List<List<V>> split(List<V> list, int size) {
        if (size < 1)
            throw new IllegalArgumentException("Sublist size must be positive.");

        List<List<V>> subLists = new ArrayList<List<V>>();

        for (int fromIndex = 0; fromIndex < list.size(); fromIndex += size)
            subLists.add(list.subList(fromIndex, Math.min(fromIndex + size, list.size())));

        return subLists;
    }

    /**
     * Determines whether a list starts with a particular sublist.
     *
     * @param <V> the element type.
     *
     * @param full the full list to examine.
     *
     * @param sub the sublist to test.
     *
     * @return {@code true} iff the first {@code k} elements of the
     * complete list {@code full} are the elements in the sublist
     * {@code sub}, where {@code k} is the length of {@code sub}.
     */
    public static <V> boolean startsWith(List<V> full, List<V> sub) {
        int fullLength = full.size();
        int subLength  = sub.size();

        return subLength <= fullLength && full.subList(0, subLength).equals(sub);
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
