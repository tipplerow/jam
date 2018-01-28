
package jam.vector;

import java.util.Arrays;
import java.util.Collection;

import jam.math.DoubleComparator;

/**
 * Provides utility methods operating on bare floating-point arrays
 * ({@code double[]} arrays).
 */
public final class VectorUtil {
    /**
     * Brackets a target value in a sorted array.  
     *
     * <p>This method only works for sorted arrays; the results are
     * undefined if the array is not sorted.
     *
     * @param array an array sorted into increasing order.
     *
     * @param target the target value to search for.
     *
     * @return an index {@code k} encoding the location of the target
     * value within the array: (1) if {@code target < array[0]}, then
     * {@code k == -1}; (2) if {@code target < array[N - 1]} (where
     * {@code N} is the array length), then {@code array[k] <= target
     * && target < array[k + 1]}; (3) if {@code target == array[N - 1]},
     * then {@code k == N - 1}; and (4) if {@code target > array[N - 1},
     * then {@code k == N}.
     */
    public static int bracket(double[] array, double target) {
        int N = array.length;

        if (target < array[0])
            return -1;

        if (target == array[N - 1])
            return N - 1;

        if (target > array[N - 1])
            return N;

        int k = Arrays.binarySearch(array, target);

        if (k >= 0)
            return k;
        else
            return -k - 2;
    }

    /**
     * Creates a new bare array and assigns all elements to a single
     * value.
     *
     * @param length the number of element in the new array.
     *
     * @param fill the value to assign to each element.
     *
     * @return the new array.
     *
     * @throws IllegalArgumentException unless the length is positive.
     */
    public static double[] create(int length, double fill) {
        double[] result = new double[length];
        Arrays.fill(result, fill);
        return result;
    }

    /**
     * Copies an array.
     *
     * @param array the array to copy.
     *
     * @return a deep copy of the input array.
     */
    public static double[] copy(double[] array) {
        return Arrays.copyOf(array, array.length);
    }

    /**
     * Compares two bare arrays for equality, allowing for a given
     * floating-point tolerance.
     *
     * @param array1 the first array to compare.
     *
     * @param array2 the second array to compare.
     *
     * @param tolerance the maximum difference to allow and still
     * compare elements as equal.
     *
     * @return {@code true} iff the input arrays have the same length
     * and all corresponding elements are equal within the specified
     * tolerance.
     */
    public static boolean equals(double[] array1, double[] array2, double tolerance) {
        if (array1.length != array2.length)
            return false;

        DoubleComparator comparator = new DoubleComparator(tolerance);

        for (int k = 0; k < array1.length; ++k)
            if (comparator.NE(array1[k], array2[k]))
                return false;

        return true;
    }

    /**
     * Converts one or more floating-point values into an array.
     *
     * @param first the first (required) value.
     *
     * @param others the remaining (optional) values.
     *
     * @return a new array with the values copied in order.
     */
    public static double[] toArray(double first, double... others) {
        double[] result = new double[1 + others.length];
        result[0] = first;

        for (int index = 0; index < others.length; index++)
            result[1 + index] = others[index];

        return result;
    }

    /**
     * Converts a collection of {@code Double} objects into a bare array.
     *
     * @param values the values to convert.
     *
     * @return a new array with {@code array[k]} containing the {@code
     * k}th element returned by the collection iterator.
     */
    public static double[] toArray(Collection<Double> values) {
        int index = 0;
        double[] result = new double[values.size()];

        for (double value : values)
            result[index++] = value;

        return result;
    }
}
