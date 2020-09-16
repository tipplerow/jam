
package jam.spin;

import java.util.Collection;

import jam.lang.Formatted;
import jam.lang.Sequence;
import jam.math.IntSequence;
import jam.math.JamRandom;
import jam.vector.BitVector;
import jam.vector.VectorView;

/**
 * A fixed-length vector of spin variables.
 */
public final class SpinVector implements Formatted, Sequence<Spin>, VectorView {
    // 
    // Use a bit vector as the underlying storage for maximum memory
    // efficiency...
    //
    private final BitVector bits;

    /**
     * Creates a new spin vector of specified length; all elements are
     * set to {@code Spin.DOWN}.
     *
     * @param length the desired number of spins.
     *
     * @throws RuntimeException if the length is negative.
     */
    public SpinVector(int length) {
        this.bits = new BitVector(length);
    }

    /**
     * Creates a new spin vector of specified length with all elements
     * initialized with the same spin state.
     *
     * @param length the desired number of spins.
     *
     * @param spin the spin state to assign to all elements.
     *
     * @throws RuntimeException if the length is negative.
     */
    public SpinVector(int length, Spin spin) {
        this(length);
        fill(spin);
    }

    /**
     * Creates a new spin vector with an initial spin assignment.
     *
     * @param spins the initial spin assignments.
     */
    public SpinVector(Spin... spins) {
        this.bits = new BitVector(spins.length);

        for (int index : IntSequence.along(spins))
            set(index, spins[index]);
    }

    /**
     * Creates a new spin vector from a collection of spins.
     * 
     * @param spins the spins to assign.
     */
    public SpinVector(Collection<Spin> spins) {
        this(spins.toArray(new Spin[0]));
    }

    /**
     * Creates a copy of this spin vector.
     *
     * @return a new spin vector identical to this.
     */
    public SpinVector copy() {
        return new SpinVector(toArray());
    }

    /**
     * Concatenates multiple spin vectors into a single vector.
     *
     * @param vectors the spin vectors to concatenate.
     *
     * @return a new {@code SpinVector} containing the elements of all
     * input vectors joined from left to right.
     */
    public static SpinVector concat(SpinVector... vectors) {
        return new SpinVector(Sequence.concat(vectors));
    }

    /**
     * Concatenates multiple spin vectors into a single vector.
     *
     * @param vectors the spin vectors to concatenate.
     *
     * @return a new {@code SpinVector} containing the elements of all
     * input vectors joined from left to right.
     */
    public static SpinVector concat(Collection<SpinVector> vectors) {
        return concat(vectors.toArray(new SpinVector[0]));
    }

    /**
     * Converts this spin vector into a string of {@code '+'} and
     * {@code '-'} characters.
     *
     * @return a string {@code s} with the same length as this vector
     * with {@code s.charAt(k)} having value {@code get(k).charValue()}.
     */
    @Override public String format() {
        StringBuilder builder = new StringBuilder();

        for (int index : IntSequence.along(this))
            builder.append(get(index).charValue());

        return builder.toString();
    }

    /**
     * Converts a formatted string of {@code '+'/'-'} characters to a
     * spin vector.
     *
     * @param s a string of {@code '+'/'-'} characters (as would be
     * returned by the {@code format()} function).
     *
     * @return a new spin vector with elements corresponding to the
     * input string.
     *
     * @throws IllegalArgumentException unless all characters in the
     * input string are {@code '+'} or {@code '-'}.
     */
    public static SpinVector parse(String s) {
        SpinVector v = new SpinVector(s.length());

        for (int index = 0; index < s.length(); index++)
            v.set(index, Spin.valueOf(s.charAt(index)));

        return v;
    }

    /**
     * Generates a spin vector of specified length with spins assigned
     * randomly.
     *
     * <p>The {@code JamRandom.global()} instance is used as the
     * random number source.
     *
     * @param length the desired vector length.
     *
     * @return a new spin vector with the specified length and with
     * spins assigned randomly.
     */
    public static SpinVector random(int length) {
        return random(length, JamRandom.global());
    }

    /**
     * Generates a spin vector of specified length with spins assigned
     * randomly.
     *
     * @param length the desired vector length.
     *
     * @param source the random number source.
     *
     * @return a new spin vector with the specified length and with
     * spins assigned randomly.
     */
    public static SpinVector random(int length, JamRandom source) {
        SpinVector result = new SpinVector(length);

        for (int index = 0; index < length; index++)
            result.set(index, Spin.next(source));

        return result;
    }

    /**
     * Assigns the same spin state to all elements in this vector.
     *
     * @param spin the spin state to assign to all elements.
     */
    public void fill(Spin spin) {
        for (int index : IntSequence.along(this))
            set(index, spin);
    }

    /**
     * Computes the fracional overlap between this spin vector and
     * another of the same length.
     *
     * @param that the other spin vector operand.
     *
     * @return the fraction of spins that are the same.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public double fit(SpinVector that) {
        return this.bits.fit(that.bits);
    }

    /**
     * Flips all spins in this vector.
     */
    public void flip() {
        bits.flip();
    }

    /**
     * Flips the spin at a specified location.
     *
     * @param index the location of the spin to flip.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public void flip(int index) {
        bits.flip(index);
    }

    /**
     * Returns the spin value at a specified location.
     *
     * @param index the location of the spin to query.
     *
     * @return the value of the spin at the specified location.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public Spin get(int index) {
        return Spin.valueOf(bits.get(index));
    }

    /**
     * Computes the Hamming distance between this spin vector and
     * another of the same length.
     *
     * @param that the other spin vector operand.
     *
     * @return the number of spins that differ.
     *
     * @throws IllegalArgumentException unless the length of the
     * operand matches this vector.
     */
    public int hamming(SpinVector that) {
        return this.bits.hamming(that.bits);
    }

    /**
     * Assigns a value to the spin at a specified location.
     *
     * @param index the location of the spin to set.
     *
     * @param value the value to assign.
     *
     * @throws IndexOutOfBoundsException if the index is out of
     * bounds.
     */
    public void set(int index, Spin value) {
        bits.set(index, value.booleanValue());
    }

    /**
     * Returns the spins in a bare array.
     *
     * @return an array with element {@code k} equal to {@code get(k)}.
     */
    public Spin[] toArray() {
        Spin[] result = new Spin[length()];

        for (int index : IntSequence.along(this))
            result[index] = get(index);

        return result;
    }

    /**
     * Returns the floating-point representation of the spin at a
     * given location.
     *
     * @param index the index of the spin to examine.
     *
     * @return the floating-point representation of the spin at the
     * specified location: {@code get(index).doubleValue()}.
     */
    @Override public double getDouble(int index) {
        return get(index).doubleValue();
    }

    @Override public int length() {
        return bits.length();
    }

    @Override public Spin objectAt(int index) {
        return get(index);
    }

    @Override public boolean equals(Object that) {
        return (that instanceof SpinVector) && equalsSpinVector((SpinVector) that);
    }

    private boolean equalsSpinVector(SpinVector that) {
        return this.bits.equals(that.bits);
    }

    @Override public int hashCode() {
        return bits.hashCode();
    }

    @Override public String toString() {
        return debug();
    }
}
