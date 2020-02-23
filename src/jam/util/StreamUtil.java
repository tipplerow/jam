
package jam.util;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtil {
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
}
