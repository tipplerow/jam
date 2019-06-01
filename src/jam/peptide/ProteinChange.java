
package jam.peptide;

/**
 * Represents a single missense mutation in a peptide.
 */
public final class ProteinChange {
    private final int position;
    private final Residue native_;
    private final Residue mutated;

    /**
     * The canonical column name for protein changes in the header
     * line of data files to be analyzed by the {@code jam} library.
     */
    public static final String COLUMN_NAME = "Protein_Change";

    /**
     * Creates a new single missense mutation.
     *
     * @param position the position in the protein at which the
     * residue change occurs (starting at position 1, not zero).
     *
     * @param native_ the original (native) residue.
     *
     * @param mutated the final (mutated) residue.
     */
    public ProteinChange(int position, Residue native_, Residue mutated) {
        this.position = position;
        this.native_  = native_;
        this.mutated  = mutated;
        validate();
    }

    private void validate() {
        if (position < 1)
            throw new IllegalArgumentException("Position must be positive.");

        if (!native_.isNative())
            throw new IllegalArgumentException("Original residue must be naturally occurring.");

        if (!mutated.isNative())
            throw new IllegalArgumentException("Final residue must be naturally occurring.");
    }

    /**
     * Parses a protein change string in standard format.
     *
     * <p>The standard format for a protein change from residue {@code X}
     * to residue {@code Y} at position {@code k} is {@code XkY}, where
     * {@code X} and {@code Y} are the single-character residue codes.
     *
     * @param s a protein-change string in standard format.
     *
     * @return the protein change encoded in the given string.
     *
     * @throws RuntimeException unless the string is properly formatted.
     */
    public static ProteinChange parse(String s) {
        char nativeChar = s.charAt(0);
        char mutatedChar = s.charAt(s.length() - 1);
        String positionStr = s.substring(1, s.length() - 1);

        return new ProteinChange(Integer.parseInt(positionStr),
                                 Residue.valueOfCode1(nativeChar),
                                 Residue.valueOfCode1(mutatedChar));
    }

    /**
     * Encodes this protein change in the standard format.
     *
     * @return a string describing this protein change in standard
     * format.
     */
    public String format() {
        return Character.toString(native_.code1())
            + Integer.toString(position)
            + Character.toString(mutated.code1());
    }

    /**
     * Returns the original (native) residue.
     *
     * @return the original (native) residue.
     */
    public Residue getNative() {
        return native_;
    }

    /**
     * Returns the final (mutated) residue.
     *
     * @return the final (mutated) residue.
     */
    public Residue getMutated() {
        return mutated;
    }

    /** 
     * Returns the position the position in the protein at which the
     * residue change occurs (starting at position 1, not zero).
     *
     * @return the position the position in the protein at which the
     * residue change occurs.
     */
    public int getPosition() {
        return position;
    }
         
    @Override public boolean equals(Object obj) {
        return (obj instanceof ProteinChange) && equalsProteinChange((ProteinChange) obj);
    }

    private boolean equalsProteinChange(ProteinChange that) {
        return this.position == that.position
            && this.native_.equals(that.native_)
            && this.mutated.equals(that.mutated);
    }

    @Override public int hashCode() {
        return position + 37 * native_.hashCode() + 37 * 37 * mutated.hashCode();
    }

    @Override public String toString() {
        return "ProteinChange(" + format() + ")";
    }
}
