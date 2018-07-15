
package jam.math;

public final class IntPair {
    /**
     * The first integer in this pair.
     */
    public final int first;

    /**
     * The second integer in this pair.
     */
    public final int second;

    /**
     * Groups two integers into an {@code IntPair}.
     *
     * @param first the first value in the pair.
     *
     * @param second the second value in the pair.
     *
     * @return an {@code IntPair} equal to {@code (first, second)}.
     */
    public static IntPair of(int first, int second) {
        return new IntPair(first, second);
    }

    private IntPair(int first, int second) {
        this.first  = first;
        this.second = second;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof IntPair) && equalsPair((IntPair) obj);
    }

    private boolean equalsPair(IntPair that) {
        return this.first  == that.first
            && this.second == that.second;
    }

    @Override public int hashCode() {
        return first + 37 * second;
    }

    @Override public String toString() {
        return "IntPair(" + first + ", " + second + ")";
    }
}
