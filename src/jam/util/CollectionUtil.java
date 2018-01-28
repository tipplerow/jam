
package jam.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.ToDoubleFunction;

/**
 * Provides utility methods operating on collections.
 */
public final class CollectionUtil {
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
