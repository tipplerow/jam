
package jam.math;

import java.util.Iterator;

/**
 * Represents a contiguous sequence of integer values.
 *
 * <p>Contiguous integer sequences are defined as the half-open range
 * {@code [begin, end)} following the conventions of C++ iterators and
 * the {@code String.substring()} method.  The beginning value is the
 * first integer in the sequence, while the ending value is the first
 * integer <em>not</em> in the sequence.  The length of the sequence
 * is therefore {@code end - begin} and empty sequences may be defined
 * when {@code end == begin}.
 */
final class ContiguousIntSequence extends IntSequence {
    private final int begin;
    private final int end;

    /**
     * Creates a new contiguous integer sequence.
     *
     * @param begin the first integer contained in the sequence.
     *
     * @param end the first integer <em>not</em> contained in the
     * sequence.
     *
     * @throws IllegalArgumentException if {@code end < begin}.
     */
    ContiguousIntSequence(int begin, int end) {
        validate(begin, end);

        this.begin = begin;
        this.end   = end;
    }

    /**
     * Returns the first integer contained in this sequence.
     *
     * @return the first integer contained in this sequence.
     */
    public int begin() {
        return begin;
    }

    /**
     * Returns the first integer <em>not</em> contained in this sequence.
     *
     * @return the first integer <em>not</em> contained in this sequence.
     */
    public int end() {
        return end;
    }

    /**
     * Validates the endpoints of a contiguous integer sequence.
     *
     * @param begin the first integer contained in the sequence.
     *
     * @param end the first integer <em>not</em> contained in the
     * sequence.
     *
     * @throws IllegalArgumentException if {@code end < begin}.
     */
    public static void validate(int begin, int end) {
        if (end < begin)
            throw new IllegalArgumentException("Invalid contiguous sequence.");
    }

    @Override public boolean contains(int value) {
        return begin <= value && value < end;
    }

    @Override public int get(int index) {
        validateIndex(index);
        return begin + index;
    }

    @Override public int length() {
        return end - begin;
    }

    @Override public Iterator<Integer> iterator() {
        return new ContiguousIntSequenceIterator(this);
    }
}
