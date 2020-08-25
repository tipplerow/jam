
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides utility operations on arrays.
 */
public final class ArrayUtil {
    private ArrayUtil() {}

    /**
     * Concatenates arrays into a single array.
     *
     * @param <V> the element type.
     *
     * @param arrays the arrays to concatenate.
     *
     * @return a new array containing all elements from the input
     * arrays, joined from left to right.
     */
    @SuppressWarnings("unchecked") public static <V> V[] cat(Collection<V[]> arrays) {
        List<V> elements = new ArrayList<V>();

        for (V[] array : arrays)
            for (V element : array)
                elements.add(element);

        return (V[]) elements.toArray();
    }

    /**
     * Concatenates arrays into a single array.
     *
     * @param <V> the element type.
     *
     * @param arrays the arrays to concatenate.
     *
     * @return a new array containing all elements from the input
     * arrays, joined from left to right.
     */
    @SafeVarargs public static <V> V[] cat(V[]... arrays) {
        return cat(List.of(arrays));
    }
}
