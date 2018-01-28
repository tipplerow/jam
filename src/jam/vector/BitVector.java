
package jam.vector;

import java.util.BitSet;
import java.util.Collection;

import jam.lang.Formatted;
import jam.lang.JamBoolean;
import jam.lang.Sequence;
import jam.math.DoubleUtil;
import jam.math.IntSequence;
import jam.math.JamRandom;

/**
 * A fixed-length vector of binary variables.
 *
 * <p>Unlike the {@code java.util.BitSet}, this class assigns an
 * explicit (fixed) length to each bit vector, independent of the
 * contents of the vector.  Attempting to access bits beyond the
 * vector length triggers an exception.
 */
public final class BitVector implements Formatted, Sequence<Boolean>, VectorView {
    private final int length;
    private final BitSet bits;

    /**
     * Creates a new bit vector of specified length; all elements are
     * set to {@code false}.
     *
     * @param length the desired number of bits.
     *
     * @throws RuntimeException if the length is negative.
     */
    public BitVector(int length) {
        this(length, new BitSet(length), false);
    }

    /**
     * Creates a new bit vector of specified length with all elements
     * initialized to a single boolean value.
     *
     * @param length the desired number of bits.
     *
     * @param value the boolean value to assign to all elements.
     *
     * @throws RuntimeException if the length is negative.
     */
    public BitVector(int length, boolean value) {
        this(length);
	fill(value);
    }

    /**
     * Creates a new bit vector with an initial bit assignment.
     *
     * @param bits the initial bit assignment.
     */
    public BitVector(boolean... bits) {
        this(bits.length, makeSet(bits), false);
    }

    private static BitSet makeSet(boolean... bits) {
        BitSet bitset = new BitSet(bits.length);

        for (int index : IntSequence.along(bits))
            bitset.set(index, bits[index]);

        return bitset;
    }

    /**
     * Creates a new bit vector of specified length with an initial
     * bit assignment.
     *
     * <p>This constructor makes a copy of the input bit set, so
     * subsequent modifications will not change the new bit vector.
     *
     * @param length the desired number of bits.
     *
     * @param bits the initial bit assignment.
     *
     * @throws RuntimeException if the length is negative or if the
     * logical length of the input bit set (one plus the index of the
     * highest {@code true}) exceeds the specified vector length.
     */
    public BitVector(int length, BitSet bits) {
        this(length, bits, true);
    }

    private BitVector(int length, BitSet bits, boolean copy) {
        this.length = length;

        if (copy)
            this.bits = (BitSet) bits.clone();
        else
            this.bits = bits;

        validateLength();
        validateBits();
    }

    private void validateBits() {
        if (bits.length() > length())
            throw new IllegalStateException("Bit set outside allowed range.");
    }

    /**
     * Creates a new bit vector from a collection of bits.
     *
     * @param bits the bits to assign.
     */
    public BitVector(Collection<Boolean> bits) {
        this(bits.size());

        int index = 0;

        for (Boolean bit : bits)
            set(index++, bit);
    }

    /**
     * Creates a copy of this bit vector.
     *
     * @return a new bit vector identical to this.
     */
    public BitVector copy() {
        return new BitVector(length, bits, true);
    }

    /**
     * Concatenates multiple bit vectors into a single vector.
     *
     * @param vectors the bit vectors to concatenate.
     *
     * @return a new {@code BitVector} containing the elements of all
     * input vectors joined from left to right.
     */
    public static BitVector concat(BitVector... vectors) {
        return new BitVector(Sequence.concat(vectors));
    }

    /**
     * Concatenates multiple bit vectors into a single vector.
     *
     * @param vectors the bit vectors to concatenate.
     *
     * @return a new {@code BitVector} containing the elements of all
     * input vectors joined from left to right.
     */
    public static BitVector concat(Collection<BitVector> vectors) {
        return concat(vectors.toArray(new BitVector[0]));
    }

    /**
     * Converts this bit vector into a string of 0/1 characters.
     *
     * @return a string {@code s} with the same length as this bit vector
     * having {@code s.charAt(k) == '0'} if {@code this.get(k) == false}
     * and {@code s.charAt(k) == '1'} if {@code this.get(k) == true}.
     */
    @Override public String format() {
        StringBuilder builder = new StringBuilder();

        for (int index : IntSequence.along(this))
            if (get(index))
                builder.append("1");
            else
                builder.append("0");

        return builder.toString();
    }

    /**
     * Converts a formatted string of 0/1 characters to a bit vector.
     *
     * @param s a string of 0/1 characters (as would be returned by
     * the {@code format()} function).
     *
     * @return a new bit vector with elements corresponding to the
     * input string.
     *
     * @throws IllegalArgumentException unless all characters in the
     * input string are {@code '0'} or {@code '1'}.
     */
    public static BitVector parse(String s) {
        BitVector v = new BitVector(s.length());

        for (int index = 0; index < s.length(); index++)
            v.set(index, parse(s.charAt(index)));

        return v;
    }

    private static boolean parse(char c) {
        if (c == '0')
            return false;

        if (c == '1')
            return true;

        throw new IllegalArgumentException("Invalid boolean character.");
    }

    /**
     * Generates a bit vector of specified length with bits assigned
     * randomly.
     *
     * <p>The {@code JamRandom.global()} instance is used as the
     * random number source.
     *
     * @param length the desired vector length.
     *
     * @return a new bit vector with the specified length and with
     * bits assigned randomly.
     */
    public static BitVector random(int length) {
        return random(length, JamRandom.global());
    }

    /**
     * Generates a bit vector of specified length with bits assigned
     * randomly.
     *
     * @param length the desired vector length.
     *
     * @param source the random number source.
     *
     * @return a new bit vector with the specified length and with
     * bits assigned randomly.
     */
    public static BitVector random(int length, JamRandom source) {
        BitVector result = new BitVector(length);

        for (int index = 0; index < length; index++)
            result.set(index, source.nextBoolean());

        return result;
    }

    /**
     * Performs a logical AND operation between this bit vector and
     * another (of the same length) and returns the result in a new
     * bit vector; this vector is unchanged.
     *
     * @param that the other bit vector operand.
     *
     * @return a new bit vector (with the same length as this) having
     * element {@code k} equal to {@code this.get(k) && that.get(k)}.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public BitVector and(BitVector that) {
        validateOperand(that);

        BitSet newBits = (BitSet) this.bits.clone();
        newBits.and(that.bits);

        return new BitVector(this.length(), newBits, false);
    }

    private void validateOperand(BitVector that) {
        if (this.length() != that.length())
            throw new IllegalArgumentException("Length mismatch.");
    }

    /**
     * Returns the number of {@code true} bits in this vector.
     *
     * @return the number of {@code true} bits in this vector.
     */
    public int cardinality() {
        return bits.cardinality();
    }

    /**
     * Assigns a single value to all elements.
     *
     * @param value the value to assign.
     */
    public void fill(boolean value) {
        for (int index : IntSequence.along(this))
            set(index, value);
    }

    /**
     * Computes the fracional overlap between this bit vector and
     * another of the same length.
     *
     * @param that the other bit vector operand.
     *
     * @return the fraction of bits that are the same.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public double fit(BitVector that) {
        int L = this.length();
        int D = this.hamming(that);

        return DoubleUtil.ratio(L - D, L);
    }

    /**
     * Flips all bits in this vector.
     */
    public void flip() {
        for (int index : IntSequence.along(this))
            flip(index);
    }

    /**
     * Flips the bit at a specified location.
     *
     * @param index the location of the bit to flip.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public void flip(int index) {
        validateIndex(index);
        bits.flip(index);
    }

    /**
     * Returns the bit value at a specified location.
     *
     * @param index the location of the bit to query.
     *
     * @return the value of the bit at the specified location.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public boolean get(int index) {
        validateIndex(index);
        return bits.get(index);
    }

    /**
     * Computes the Hamming distance between this bit vector and
     * another of the same length.
     *
     * @param that the other bit vector operand.
     *
     * @return the number of bits that differ.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public int hamming(BitVector that) {
        return this.xor(that).cardinality();
    }

    /**
     * Performs a logical NOT operation on this bit vector and returns
     * the result in a new bit vector; this vector is unchanged.
     *
     * @return a new bit vector (with the same length as this) having
     * all bits flipped relative to this vector.
     */
    public BitVector not() {
        BitVector result = copy();
        result.flip();
        return result;
    }

    /**
     * Performs a logical OR operation between this bit vector and
     * another (of the same length) and returns the result in a new
     * bit vector; this vector is unchanged.
     *
     * @param that the other bit vector operand.
     *
     * @return a new bit vector (with the same length as this) having
     * element {@code k} equal to {@code this.get(k) || that.get(k)}.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public BitVector or(BitVector that) {
        validateOperand(that);

        BitSet newBits = (BitSet) this.bits.clone();
        newBits.or(that.bits);

        return new BitVector(this.length(), newBits, false);
    }

    /**
     * Sets a bit at a specified location to {@code true}.
     *
     * @param index the location of the bit to set.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public void set(int index) {
        set(index, true);
    }

    /**
     * Assigns a value to the bit at a specified location.
     *
     * @param index the location of the bit to set.
     *
     * @param value the value to assign.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public void set(int index, boolean value) {
        validateIndex(index);
        bits.set(index, value);
    }

    /**
     * Extracts a contiguous section of this bit vector.  
     *
     * As with {@code java.lang.String.substring(int, int}, the
     * subvector begins at the specified {@code beginIndex} and
     * extends to the bit at {@code endIndex - 1}.
     *
     * @param beginIndex the beginning index (inclusive).
     *
     * @param endIndex the ending index (exclusive).
     *
     * @return a new bit vector containing the bits from elements
     * {@code [beginIndex, endIndex)}.
     *
     * @throws IllegalArgumentException unless the indexes define a
     * valid subvector.
     */
    public BitVector subvector(int beginIndex, int endIndex) {
        if (beginIndex < 0)
            throw new IllegalArgumentException("Beginning index out of bounds.");

        if (endIndex > length())
            throw new IllegalArgumentException("Ending index out of bounds.");

        if (beginIndex > endIndex)
            throw new IllegalArgumentException("Beginning index beyond ending index.");

        int subLen = endIndex - beginIndex;
        BitVector subVec = new BitVector(subLen);

        for (int subInd = 0; subInd < subLen; subInd++)
            subVec.set(subInd, this.get(subInd + beginIndex));

        return subVec;
    }

    /**
     * Performs a logical XOR operation between this bit vector and
     * another (of the same length) and returns the result in a new
     * bit vector; this vector is unchanged.
     *
     * @param that the other bit vector operand.
     *
     * @return a new bit vector (with the same length as this) having
     * element {@code k} equal to {@code this.get(k) != that.get(k)}.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public BitVector xor(BitVector that) {
        validateOperand(that);

        BitSet newBits = (BitSet) this.bits.clone();
        newBits.xor(that.bits);

        return new BitVector(this.length(), newBits, false);
    }

    /**
     * Returns a boolean array representation of this bit vector.
     *
     * <p>The name <em>logical</em> is borrowed from the {@code R}
     * language primitive logical type.
     *
     * @return a boolean array with element {@code k} equal to 
     * {@code get(k)}.
     */
    public boolean[] toLogical() {
        boolean[] result = new boolean[length()];

        for (int index : IntSequence.along(this))
            result[index] = get(index);

        return result;
    }

    @Override public int length() {
        return length;
    }

    @Override public Boolean objectAt(int index) {
        return Boolean.valueOf(get(index));
    }

    /**
     * Returns the floating-point representation of the bit at a
     * specified location.
     *
     * @param index the index of the bit to examine.
     *
     * @return the floating-point representation of the bit at the
     * specified location: {@code get(index) ? 1.0 : 0.0}.
     *
     * @throws RuntimeException unless the specified index is a valid
     * location.
     */
    @Override public double getDouble(int index) {
        return JamBoolean.doubleValue(get(index));
    }

    @Override public boolean equals(Object that) {
        return (that instanceof BitVector) && equalsBitVector((BitVector) that);
    }

    private boolean equalsBitVector(BitVector that) {
        return this.length == that.length && this.bits.equals(that.bits);
    }

    @Override public int hashCode() {
        return bits.hashCode();
    }

    @Override public String toString() {
        return debug();
    }
}
