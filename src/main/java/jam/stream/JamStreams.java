
package jam.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utility methods operating on streams.
 */
public final class JamStreams {
    private JamStreams() {}

    /**
     * Applies a function (or lambda expression) to each element of a
     * stream.
     *
     * @param <T> element type for the source stream.
     *
     * @param <R> element type for the result list.
     *
     * @param stream the stream to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a list containing the objects produced by the mapping
     * function.
     */
    public static <T, R> List<R> apply(Stream<T> stream, Function<? super T, ? extends R> function) {
        return stream.map(function).collect(Collectors.toList());
    }

    /**
     * Applies a function (or lambda expression) to each element of a
     * collection.
     *
     * @param <T> element type for the source collection.
     *
     * @param <R> element type for the result list.
     *
     * @param collection the collection to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a list containing the objects produced by the mapping
     * function.
     */
    public static <T, R> List<R> apply(Collection<T> collection, Function<? super T, ? extends R> function) {
        return apply(collection.stream(), function);
    }

    /**
     * Applies a function (or lambda expression) to each element of a
     * collection <em>using a parallel stream</em>.
     *
     * @param <T> element type for the source collection.
     *
     * @param <R> element type for the result list.
     *
     * @param collection the collection to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @return a list containing the objects produced by the mapping
     * function.
     */
    public static <T, R> List<R> applyParallel(Collection<T> collection, Function<? super T, ? extends R> function) {
        return apply(collection.parallelStream(), function);
    }

    /**
     * Counts the number of stream elements that match a predicate.
     *
     * @param <V> the runtime element type.
     *
     * @param stream the stream to be filtered.
     *
     * @param predicate the predicate to apply to each element in the
     * stream.
     *
     * @return the number of stream elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> long count(Stream<V> stream, Predicate<? super V> predicate) {
        return stream.filter(predicate).count();
    }

    /**
     * Counts the number of collection elements that match a predicate.
     *
     * @param <V> the runtime element type.
     *
     * @param collection the collection to be filtered.
     *
     * @param predicate the predicate to apply to each element in the
     * collection.
     *
     * @return the number of collection elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> long count(Collection<V> collection, Predicate<? super V> predicate) {
        return count(collection.stream(), predicate);
    }

    /**
     * Removes elements of a stream that do not pass a filter
     * predicate.
     *
     * @param <V> the runtime element type.
     *
     * @param stream the stream to be filtered.
     *
     * @param predicate the predicate filter to apply to each element
     * in the stream.
     *
     * @return a list containing only elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> List<V> filter(Stream<V> stream, Predicate<? super V> predicate) {
        return stream.filter(predicate).collect(Collectors.toList());
    }

    /**
     * Removes elements of a collection that do not pass a filter
     * predicate.
     *
     * @param <V> the runtime element type.
     *
     * @param collection the collection to be filtered.
     *
     * @param predicate the predicate filter to apply to each element
     * in the collection.
     *
     * @return a list containing only elements for which the predicate
     * evaluates to {@code true}.
     */
    public static <V> List<V> filter(Collection<V> collection, Predicate<? super V> predicate) {
        return filter(collection.stream(), predicate);
    }

    /**
     * Removes elements of a collection that do not pass a filter
     * predicate <em>using a parallel stream</em>.
     *
     * @param <V> the runtime element type.
     *
     * @param collection the collection to be filtered.
     *
     * @param predicate the predicate filter to apply to each element
     * in the collection.
     *
     * @return a list containing only elements for which the predicate
     * evaluates to {@code true} (in in indeterminate order, because
     * the collection is processed in parallel).
     */
    public static <V> List<V> filterParallel(Collection<V> collection, Predicate<? super V> predicate) {
        return filter(collection.parallelStream(), predicate);
    }

    /**
     * Collects a stream into an {@code ArrayList}.
     *
     * @param <T> element type for the stream.
     *
     * @param stream the stream to collect.
     *
     * @return a new {@code ArrayList} containing the stream elements.
     */
    public static <T> ArrayList<T> toArrayList(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Collects a stream into an {@code HashSet}.
     *
     * @param <T> element type for the stream.
     *
     * @param stream the stream to collect.
     *
     * @return a new {@code HashSet} containing the stream elements.
     */
    public static <T> HashSet<T> toHashSet(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Collects a stream into an {@code LinkedList}.
     *
     * @param <T> element type for the stream.
     *
     * @param stream the stream to collect.
     *
     * @return a new {@code LinkedList} containing the stream elements.
     */
    public static <T> LinkedList<T> toLinkedList(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Collects a stream into an {@code TreeSet}.
     *
     * @param <T> element type for the stream.
     *
     * @param stream the stream to collect.
     *
     * @return a new {@code TreeSet} containing the stream elements.
     */
    public static <T> TreeSet<T> toTreeSet(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(TreeSet::new));
    }
}
