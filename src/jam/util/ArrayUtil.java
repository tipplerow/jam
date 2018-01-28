
package jam.util;

import java.util.Arrays;
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
        List<List<V>> lists = new ArrayList<List<V>>();

        for (V[] array : arrays)
            lists.add(Arrays.asList(array));

        return (V[]) ListUtil.cat(lists).toArray();
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
        return cat(Arrays.asList(arrays));
    }
}
