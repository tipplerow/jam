
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import jam.math.JamRandom;

/**
 * Provides utility methods operating on collections.
 */
public final class CollectionUtil {
    /**
     * Adds every item returned by an iterator to an existing
     * collection.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param destination the existing collection.
     *
     * @param iterator an iterator over items to be added to the collection.
     * added.
     *
     * @return the destination collection.
     */
    public static <V> Collection<V> addAll(Collection<V> destination, Iterator<? extends V> iterator) {
        while (iterator.hasNext())
            destination.add(iterator.next());

        return destination;
    }

    /**
     * Adds every item in a collection of collections into another
     * collection.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param destination the collection to which the items will be
     * added.
     *
     * @param sources the collections to add to the destination.
     *
     * @return the destination collection.
     */
    public static <V> Collection<V> addAll(Collection<V> destination, Iterable<? extends Collection<? extends V>> sources) {
        for (Collection<? extends V> source : sources)
            destination.addAll(source);

        return destination;
    }

    /**
     * Determines whether every object in a collection is unique
     * (appears exactly once).
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection to examine.
     *
     * @return {@code true} iff every object in the input collection
     * is unique.
     */
    public static <V> boolean allUnique(Collection<V> collection) {
        //
        // We need to iterate over all elements anyway, so returning
        // !anyDuplicates(collection) would not be any faster...
        //
        return countUnique(collection) == collection.size();
    }

    /**
     * Determines whether a collection contains any duplicate
     * elements.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection to examine.
     *
     * @return {@code true} iff the input collection contains one or
     * more duplicate elements.
     */
    public static <V> boolean anyDuplicates(Collection<V> collection) {
        HashSet<V> unique = new HashSet<V>(collection.size());

        for (V object : collection) {
            if (unique.contains(object))
                return true;
            else
                unique.add(object);
        }

        return false;
    }

    /**
     * Computes the average of an attribute over a collection of
     * objects.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection over which to operate.
     *
     * @param mapper a function to extract a numeric attribute from
     * each object in the collection.
     *
     * @return the average of all numeric attributes over all objects in
     * the collection (or {@code Double.NaN} if the collection is empty).
     */
    public static <V> double average(Collection<V> collection, ToDoubleFunction<? super V> mapper) {
        return collection.stream().mapToDouble(mapper).average().orElse(Double.NaN);

    }

    /**
     * Compares two collections by their iteration order.
     *
     * <p>This method iterates over both collections simultaneously and
     * compares the elements returned by the iterators.  The iteration
     * continues until either (1) the elements compare as "not equal",
     * and the method returns the result of the element comparison, or
     * (2) one iterator reaches the end of its collection, and the 
     * method returns the result of comparing the collection sizes.
     *
     * @param <E> the runtime collection type.
     *
     * @param coll1 the first collection to compare.
     *
     * @param coll2 the second collection to compare.
     *
     * @return an integer less than, equal to, or greater than zero
     * according to whether the first collection compares as "less
     * than", "equal to", or "greater than" the second.
     */
    public static <E extends Comparable<E>> int compareIterationOrder(Collection<? extends E> coll1,
                                                                      Collection<? extends E> coll2) {
        Iterator<? extends E> iter1 = coll1.iterator();
        Iterator<? extends E> iter2 = coll2.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            int comp = iter1.next().compareTo(iter2.next());

            if (comp != 0)
                return comp;
        }

        // Every element in the smaller collection compared as equal
        // to the corresponding element in the larger collection; the
        // smaller collection is therefore "less than" the larger one...
        return Integer.compare(coll1.size(), coll2.size());
    }

    /**
     * Counts the number of items returned by an iterator (and moves
     * the iterator to the end of the underlying collection).
     *
     * @param iterator an iterator to process.
     *
     * @return the number of items that remained in the iteration.
     */
    public static int count(Iterator<?> iterator) {
        int count = 0;

        while (iterator.hasNext()) {
            ++count;
            iterator.next();
        }

        return count;
    }

    /**
     * Counts the number of elements in common between two collections
     * (the size of their intersection).
     *
     * @param <V> the type of object contained in the collections.
     *
     * @param first the first collection.
     *
     * @param second the second collections.
     *
     * @return the size of the intersection of the collections.
     */
    public static <V> int countCommon(Collection<V> first, Collection<V> second) {
        HashSet<V> common = new HashSet<V>(first);
        common.retainAll(second);
        return common.size();
    }

    /**
     * Counts the number of unique objects in a collection.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection over which to operate.
     *
     * @return the number of unique objects in the collection.
     */
    public static <V> int countUnique(Collection<V> collection) {
        return new HashSet<V>(collection).size();
    }

    /**
     * Returns the element at a given position in a collection (as
     * determined by the iteration order).
     *
     * <p>If the input collection is a {@code List}, this method calls
     * its {@code get(int)} method; he resulting complexity is {@code
     * O(1)} for random-access lists.  Otherwise, this method iterates
     * through the first {@code index} elements of the collection,
     * ignores them, and then returns the next element.  The resulting
     * complexity is {@code O(n)}, where {@code n} is the size of the
     * collection.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection to process.
     *
     * @param index the index of the element to return.
     *
     * @return the element at the specified position in the iteration
     * order of the input collection.
     *
     * @throws IndexOutOfBoundsException unless the index is in range.
     */
    public static <V> V get(Collection<V> collection, int index) {
        if (collection instanceof List)
            return ((List<V>) collection).get(index);

        if (index < 0 || index >= collection.size())
            throw new IndexOutOfBoundsException(index);

        Iterator<V> iterator = collection.iterator();

        for (int skip = 0; skip < index; ++skip)
            iterator.next();

        return iterator.next();
    }

    /**
     * Peeks at the first item in the collection; the collection is
     * unchanged.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection on which to operate.
     *
     * @return the first element returned by the collection iterator,
     * or {@code null} if the collection is empty.
     */
    public static <V> V peek(Collection<V> collection) {
        if (collection.isEmpty())
            return null;
        else
            return collection.iterator().next();
    }

    /**
     * Finds the maximum value of an attribute over a collection of
     * objects.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection over which to operate.
     *
     * @param mapper a function to extract a numeric attribute from
     * each object in the collection.
     *
     * @return the maximum numeric attribute over all objects in the
     * collection (or {@code Double.NaN} if the collection is empty).
     */
    public static <V> double max(Collection<V> collection, ToDoubleFunction<? super V> mapper) {
        return collection.stream().mapToDouble(mapper).max().orElse(Double.NaN);
    }

    /**
     * Finds the minimum value of an attribute over a collection of
     * objects.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection over which to operate.
     *
     * @param mapper a function to extract a numeric attribute from
     * each object in the collection.
     *
     * @return the minimum numeric attribute over all objects in the
     * collection (or {@code Double.NaN} if the collection is empty).
     */
    public static <V> double min(Collection<V> collection, ToDoubleFunction<? super V> mapper) {
        return collection.stream().mapToDouble(mapper).min().orElse(Double.NaN);
    }

    /**
     * Selects a random sample <em>with replacement</em> from a
     * collection.
     *
     * <p>Since the sampling is done with replacement, items may be
     * selected more than once: the result may contain duplicates.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection to sample from.
     *
     * @param count the number of items to select (which may be
     * greater than the collection size).
     *
     * @param random the random number source.
     *
     * @return a new random-access list containing the sampled items;
     * the may contain duplicate items.
     *
     * @throws IllegalArgumentException if the sample count is
     * negative.
     */
    public static <V> List<V> sample(Collection<V> collection, int count, JamRandom random) {
        //
        // Copy the collection into a random-access list for efficient
        // indexing...
        //
        List<V> pooled  = new ArrayList<V>(collection);
        List<V> sampled = new ArrayList<V>(count);

        for (int index = 0; index < count; ++index)
            sampled.add(ListUtil.select(pooled, random));

        return sampled;
    }

    /**
     * Splits a collection into lists of equal length, except for the
     * last list when the original collection cannot be split exactly.
     *
     * @param <V> the element type.
     *
     * @param collection the collection to split.
     *
     * @param splitSize the desired size of the sublists.
     *
     * @return a list containing the sublists.
     *
     * @throws IllegalArgumentException unless the size is positive.
     */
    public static <V> List<List<V>> split(Collection<V> collection, int splitSize) {
        if (splitSize < 1)
            throw new IllegalArgumentException("Sublist size must be positive.");

        List<V> subList = new ArrayList<V>(splitSize);
        Iterator<V> iterator = collection.iterator();
        List<List<V>> subLists = new ArrayList<List<V>>();

        while (iterator.hasNext()) {
            subList.add(iterator.next());

            if (subList.size() == splitSize) {
                subLists.add(subList);
                subList = new ArrayList<V>(splitSize);
            }
        }

        if (!subList.isEmpty())
            subLists.add(subList);

        return subLists;
    }

    /**
     * Computes the sum of an attribute over a collection of objects.
     *
     * @param <V> the type of object contained in the collection.
     *
     * @param collection the collection over which to operate.
     *
     * @param mapper a function to extract a numeric attribute from
     * each object in the collection.
     *
     * @return the sum of all numeric attributes over all objects in
     * the collection (or {@code 0.0} if the collection is empty).
     */
    public static <V> double sum(Collection<V> collection, ToDoubleFunction<? super V> mapper) {
        return collection.stream().mapToDouble(mapper).sum();
    }
}
