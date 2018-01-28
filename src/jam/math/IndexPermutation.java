
package jam.math;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Generates unique index permutations to be used for selecting items
 * from arrays or lists.
 *
 * <p>The permutations are generated in a regular sequence that may be
 * best illustrated by example.  Choosing three elements from an array
 * of length five produces the following indexes (in the order shown):
 *
 * <pre>
   [0, 1, 2]
   [0, 1, 3]
   [0, 1, 4]
   [0, 2, 3]
   [0, 2, 4]
   [0, 3, 4]
   [1, 2, 3]
   [1, 2, 4]
   [1, 3, 4]
   [2, 3, 4]
 * </pre>
 *
 * Within one permutation, the indexes are always in ascending order.
 * From one permutation to the next, the index at location {@code k}
 * of the permutation is always greater than or equal to the index at
 * the same location in the previous permutation.
 *
 * <p>Instances of this class provide {@code Iterator}-like processing
 * of the permutations, stepping through the sequence one by one.  As
 * the total number of permutations may be astronomical, only the next
 * permutation is kept in memory; the entire set is never present.
 */
public final class IndexPermutation {
    private final int length;
    private final int choose;

    private int[] permut;
    private int[] maxind;

    /**
     * Creates a new permutation generator.
     *
     * @param length the length of the target array.
     *
     * @param choose the number of elements to choose.
     *
     * @throws IllegalArgumentException unless both arguments are
     * positive and {@code choose <= length}.
     */
    public IndexPermutation(int length, int choose) {
        validate(length, choose);

        this.length = length;
        this.choose = choose;

        initPermut();
        initMaxInd();
    }

    private void initPermut() {
        //
        // The first permutation to deliver: 
        //
        //     [0, 1, ..., choose - 1]
        //
        permut = new int[choose];

        for (int k = 0; k < permut.length; k++)
            permut[k] = k;
    }

    private void initMaxInd() {
        //
        // The maximum index for each element of the permutation:
        //
        //     [length - choose, length - choose + 1, ..., length - 1]
        //
        maxind = new int[choose];

        for (int k = 0; k < maxind.length; k++)
            maxind[k] = length - choose + k;
    }

    /**
     * Returns the number of unique permutations that may be
     * generated.
     *
     * @return the number of unique permutations that may be
     * generated.
     */
    public int count() {
        return Factorial.choose(length, choose);
    }

    /**
     * Returns {@code true} if there is another permutation to be
     * generated.
     *
     * @return {@code true} if there is another permutation to be
     * generated.
     */
    public boolean hasNext() {
        return permut != null;
    }

    /**
     * Returns the next permutation in the sequence.
     *
     * @return the next permutation in the sequence.
     *
     * @throws NoSuchElementException if all permutations have been
     * generated.
     */
    public int[] next() {
        if (!hasNext())
            throw new NoSuchElementException();

        // The "permut" array always contains the next permutation...
        int[] result = Arrays.copyOf(permut, permut.length);

        // Now find the next permutation to return on the next call...
        findNext();

        return result;
    }

    private void findNext() {
        int cursor = permut.length - 1;

        if (permut[cursor] < maxind[cursor]) {
            //
            // Increment the index of the right-most element...
            //
            ++permut[cursor];
        }
        else {
            //
            // Move to the next subsequence...
            //
            resetPermut();
        }
    }

    private void resetPermut() {
        //
        // The right-most element has reached its maximum value. Walk
        // backwards (from right to left) to find the next right-most
        // element less than its maximum value.
        //
        int cursor = permut.length - 2;

        while (cursor >= 0 && permut[cursor] >= maxind[cursor])
            --cursor;

        if (cursor < 0) {
            //
            // No elements are below their maximum value, so there are
            // no more permutations; set the array to null to indicate
            // that condition...
            //
            permut = null;
        }
        else {
            //
            // Increment this element and reset all others lying to
            // the right to generate the next subsequence...
            //
            ++permut[cursor];

            for (int k = cursor + 1; k < permut.length; k++)
                permut[k] = permut[k - 1] + 1;
        }
    }

    /**
     * Validates the arguments for an index permutation.
     *
     * @param length the length of the target array.
     *
     * @param choose the number of elements to choose.
     *
     * @throws IllegalArgumentException unless both arguments are
     * positive and {@code choose <= length}.
     */
    public static void validate(int length, int choose) {
        if (length < 1)
            throw new IllegalArgumentException("Array length must be positive.");

        if (choose < 1)
            throw new IllegalArgumentException("Must choose at least one index.");

        if (choose > length)
            throw new IllegalArgumentException("Cannot choose more indexes than the array length.");
    }
}
