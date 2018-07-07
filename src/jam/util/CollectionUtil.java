
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
