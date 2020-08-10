
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utility methods operating on streams.
 */
public final class StreamUtil {
    private StreamUtil() {}

    /**
     * Applies a function (or lambda expression) to each element of a
     * stream.
     *
     * @param <T> element type for the source list.
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
     * collection using a parallel stream.
     *
     * @param <T> element type for the source list.
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
     * Applies a function (or lambda expression) to each element of a
     * list using parallel streams with a maximum concurrency.
     *
     * @param <T> element type for the source list.
     *
     * @param <R> element type for the result list.
     *
     * @param source the objects to process.
     *
     * @param function the function (or lambda expression) to apply.
     *
     * @param concurrency the maximum number of concurrent threads.
     *
     * @return a list containing the objects produced by the mapping
     * function (in an unpredictable order).
     *
     * @throws IllegalArgumentException unless the concurrency is
     * positive.
     */
    public static <T, R> List<R> apply(List<T> source, Function<? super T, ? extends R> function, int concurrency) {
        List<R> resultList = new ArrayList<R>(source.size());

        for (List<T> subList : ListUtil.split(source, concurrency))
            resultList.addAll(apply(subList.parallelStream(), function));

        return resultList;
    }

    /**
     * Performs an action on each element of a list using parallel
     * streams with a maximum concurrency.
     *
     * @param <T> element type for the source list.
     *
     * @param source the objects to process.
     *
     * @param action the action to perform on each object.
     *
     * @param concurrency the maximum number of concurrent threads.
     *
     * @throws IllegalArgumentException unless the concurrency is
     * positive.
     */
    public static <T> void forEach(List<T> source, Consumer<? super T> action, int concurrency) {
        for (List<T> subList : ListUtil.split(source, concurrency))
            subList.parallelStream().forEach(action);
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
