
package jam.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Provides static utility methods operating on integers.
 */
public final class IntUtil {
    /**
     * Counts the number of occurrences for each integer in a
     * sequence.
     *
     * @param values the integer sequence to process.
     *
     * @return a new {@code Multiset} containing the number of
     * occurrences for each integer in the input sequence.
     */
    public static Multiset<Integer> count(int... values) {
        Multiset<Integer> counts = HashMultiset.create();

        for (int value : values)
            counts.add(value);

        return counts;
    }

    /**
     * Counts the number of occurrences for each integer in a
     * collection.
     *
     * @param values the integer collection to process.
     *
     * @return a new {@code Multiset} containing the number of
     * occurrences for each integer in the input collection.
     */
    public static Multiset<Integer> count(Collection<Integer> values) {
        return HashMultiset.create(values);
    }

    /**
     * Determines whether one integer is evenly divisible by another.
     *
     * @param numer the numerator to examine.
     *
     * @param denom the denominator to examine.
     *
     * @return {@code true} iff the numerator is evenly divisible by
     * the denominator (has no remainder).
     */
    public static boolean isDivisible(int numer, int denom) {
        return numer % denom == 0;
    }

    /**
     * Parses a string representation of an integer value.
     *
     * <p>In addition to all formats accepted by the built-in 
     * {@code Integer.parseInt}, this method supports scientific
     * notation (e.g., {@code 1.23E9}) provided that the value is
     * an exact integer value (has no fractional part).
     *
     * @param string the string to parse.
     *
     * @return the integer value represented by the given string.
     *
     * @throws RuntimeException unless the string is properly
     * formatted.
     */
    public static int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            double result = DoubleUtil.parseDouble(string);

            if (DoubleUtil.isInt(result))
                return (int) result;
            else
                throw ex;
        }
    }

    /**
     * Parses a delimited string of integer values.
     *
     * @param string the delimited string to parse.
     *
     * @param pattern the element delimiter.
     *
     * @return the integer array represented by the given string.
     *
     * @throws RuntimeException unless the string is properly
     * formatted.
     */
    public static int[] parseIntArray(String string, Pattern pattern) {
        String[] fields = pattern.split(string);
        int[] elements = new int[fields.length];

        for (int index = 0; index < fields.length; index++)
            elements[index] = parseInt(fields[index].trim());

        return elements;
    }

    /**
     * Generates indexes required for a random sample of array or list
     * elements (using the global random number source).
     *
     * @param elementCount the number of elements to choose from (the
     * array length or list size).
     *
     * @param sampleSize the number of elements to choose.
     *
     * @return an array of length {@code sampleSize} containing the
     * indexes of the elements to choose.
     *
     * @throws IllegalArgumentException if the sample size is negative
     * or greater than the element count.
     */
    public static int[] sample(int elementCount, int sampleSize) {
        return sample(elementCount, sampleSize, JamRandom.global());
    }

    /**
     * Generates indexes required for a random sample of array or list
     * elements.
     *
     * @param elementCount the number of elements to choose from (the
     * array length or list size).
     *
     * @param sampleSize the number of elements to choose.
     *
     * @param random the random number source.
     *
     * @return an array of length {@code sampleSize} containing the
     * indexes of the elements to choose.
     *
     * @throws IllegalArgumentException if the sample size is negative
     * or greater than the element count.
     */
    public static int[] sample(int elementCount, int sampleSize, JamRandom random) {
        if (sampleSize < 0)
            throw new IllegalArgumentException("Sample size may not be negative.");

        if (sampleSize > elementCount)
            throw new IllegalArgumentException("Sample size may not exceed the element count.");

        int[] indexes = new int[elementCount];

        for (int k = 0; k < elementCount; ++k)
            indexes[k] = k;

        shuffle(indexes, random);
        return Arrays.copyOfRange(indexes, 0, sampleSize);
    }

    /**
     * Randomly reorders elements in an array (in place) using the
     * global random number source.
     *
     * @param elements the array to shuffle.
     */
    public static void shuffle(int[] elements) {
        shuffle(elements, JamRandom.global());
    }

    /**
     * Randomly reorders elements in an array (in place).
     *
     * @param elements the array to shuffle.
     *
     * @param random the random number source.
     */
    public static void shuffle(int[] elements, JamRandom random) {
        for (int k = elements.length - 1; k > 0; k--)
            swap(elements, k, random.nextInt(k));
    }

    /**
     * Swaps two array elements.
     *
     * @param elements the array on which to operate.
     *
     * @param j the index of the first element to swap.
     *
     * @param k the index of the second element to swap.
     */
    public static void swap(int[] elements, int j, int k) {
        int tmp = elements[j];
        elements[j] = elements[k];
        elements[k] = tmp;
    }

    /**
     * Converts a collection of {@code Integer} objects into a bare array.
     *
     * @param collection the collection to convert.
     *
     * @return a new array filled with the integer values in the order
     * returned by the collection iterator.
     */
    public static int[] toArray(Collection<Integer> collection) {
        int index = 0;
        int[] result = new int[collection.size()];

        for (Integer value : collection)
            result[index++] = value.intValue();

        return result;
    }

    /**
     * Converts a sequence of integers into an array of doubles.
     *
     * @param ints the integers to convert.
     *
     * @return an array of {@code double} values converted from the
     * integers in the corresponding elements of the input array.
     */
    public static double[] toDouble(int... ints) {
        double[] doubles = new double[ints.length];

        for (int index = 0; index < ints.length; index++)
            doubles[index] = ints[index];

        return doubles;
    }

    /**
     * Converts a collection of integers into an array of doubles.
     *
     * @param ints the integers to convert.
     *
     * @return an array of {@code double} values converted from the
     * integers in the order returned by the collection iterator.
     */
    public static double[] toDouble(Collection<Integer> ints) {
        int index = 0;
        double[] doubles = new double[ints.size()];

        for (Integer value : ints)
            doubles[index++] = value.intValue();

        return doubles;
    }
}
