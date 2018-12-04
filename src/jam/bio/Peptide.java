
package jam.bio;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.report.LineBuilder;
import jam.math.JamRandom;

/**
 * A fixed sequence of amino acids.
 */
public final class Peptide {
    private int hashCode;
    private final List<Residue> residues;

    private static final int UNSET_HASH_CODE = 0;

    private Peptide(List<Residue> residues, boolean copy) {
        validateResidues(residues);

        if (copy)
            this.residues = Collections.unmodifiableList(new ArrayList<Residue>(residues));
        else
            this.residues = Collections.unmodifiableList(residues);

        this.hashCode = UNSET_HASH_CODE;
    }

    private static void validateResidues(List<Residue> residues) {
        if (residues.isEmpty())
            throw new IllegalArgumentException("Empty residue list.");
    }

    /**
     * The maximum peptide length that can be explicitly enumerated
     * without exceeding the maximum size of a {@code Collection}.
     */
    public static final int ENUMERATION_LIMIT = 7;

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the sequence of residues to compose the peptide.
     *
     * @throws IllegalArgumentException if the residue sequence is empty.
     */
    public Peptide(Residue... residues) {
        this(Arrays.asList(residues), true);
    }

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the list of residues to compose the peptide.
     *
     * @throws IllegalArgumentException if the residue list is empty.
     */
    public Peptide(List<Residue> residues) {
        this(residues, true);
    }

    /**
     * Enumerates every native peptide of a given length.
     *
     * @param length the desired peptide length.
     *
     * @return a list containing all possible native peptides
     * (composed of native residues) with the specified length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static List<Peptide> enumerate(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");

        if (length == 1)
            return enumerate1();

        if (length <= ENUMERATION_LIMIT)
            return addGeneration(enumerate(length - 1));

        throw new IllegalArgumentException("Length must not exceed the enumeration limit.");
    }

    private static List<Peptide> enumerate1() {
        List<Peptide> resultList =
            new ArrayList<Peptide>(Residue.countNative());

        for (Residue residue : Residue.listNative())
            resultList.add(Peptide.of(residue));

        return resultList;
    }

    private static List<Peptide> addGeneration(List<Peptide> parentList) {
        List<Peptide> resultList =
            new ArrayList<Peptide>(Residue.countNative() * parentList.size());

        for (Peptide parent : parentList)
            for (Residue residue : Residue.listNative())
                resultList.add(parent.append(residue));

        return resultList;
    }

    /**
     * Returns the number of unique native peptides with a fixed
     * length.
     *
     * @param length the desired peptide length.
     *
     * @return the number of unique native peptides with the specified
     * length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static int enumerationSize(int length) {
        if (length < 1)
            throw new IllegalArgumentException("Length must be positive.");
        else if (length > ENUMERATION_LIMIT)
            throw new IllegalArgumentException("Length must not exceed the enumeration limit.");

        return (int) Math.pow(Residue.countNative(), length);
    }

    /**
     * Creates a new peptide with native residues chosen randomly with
     * equal probability.
     *
     * @param length the desired number of residues.
     *
     * @return a new peptide with exactly {@code length} native
     * residues chosen at random with equal probability.
     */
    public static Peptide newNative(int length) {
        List<Residue> residues = new ArrayList<Residue>(length);

        while (residues.size() < length)
            residues.add(Residue.selectNative(JamRandom.global()));

        return new Peptide(residues, false);
    }

    /**
     * Returns a peptide with a fixed sequence of residues.
     *
     * @param residues the sequence of residues to compose the peptide.
     *
     * @return the peptide with the specified residues.
     *
     * @throws IllegalArgumentException if the residue sequence is empty.
     */
    public static Peptide of(Residue... residues) {
        return new Peptide(residues);
    }

    /**
     * Returns a peptide with a fixed sequence of residues.
     *
     * @param residues the list of residues to compose the peptide.
     *
     * @return the peptide with the specified residues.
     *
     * @throws IllegalArgumentException if the residue list is empty.
     */
    public static Peptide of(List<Residue> residues) {
        return new Peptide(residues);
    }

    /**
     * Appends a sequence of residues to this peptide and returns a
     * new peptide with the full sequence; this peptide is unchanged.
     *
     * @param addlResidues the residues to append.
     *
     * @return the new peptide with the additional residues.
     */
    public Peptide append(Residue... addlResidues) {
        return append(List.of(addlResidues));
    }

    /**
     * Appends a list of residues to this peptide and returns a new
     * peptide with the full sequence; this peptide is unchanged.
     *
     * @param addlResidues the residues to append.
     *
     * @return the new peptide with the additional residues.
     */
    public Peptide append(List<Residue> addlResidues) {
        List<Residue> newResidues =
            new ArrayList<Residue>(this.residues);

        newResidues.addAll(addlResidues);

        return new Peptide(newResidues, false);
    }

    /**
     * Returns the residue at a specified location.
     *
     * @param index the index of the desired location.
     *
     * @return the residue at the specified location.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public Residue at(int index) {
        return residues.get(index);
    }

    /**
     * Formats the residues in this peptide for output to a CSV file.
     *
     * @return the residues in this peptide formatted for output to a
     * CSV file.
     */
    public String format() {
        LineBuilder builder = LineBuilder.csv();

        for (Residue residue : residues)
            builder.append(residue.code1());

        return builder.toString();
    }

    /**
     * Returns the header line for CSV files containing peptides with
     * the same length as this peptide.
     *
     * @return the header line for CSV files containing peptides with
     * the same length as this peptide.
     */
    public String header() {
        return header(length());
    }

    /**
     * Returns the header line for CSV files containing peptides of a
     * given length.
     *
     * @param length the number of residues in the peptides.
     *
     * @return the header line for CSV files containing peptides with
     * the specified length.
     */
    public static String header(int length) {
        LineBuilder builder = LineBuilder.csv();

        for (int k = 0; k < length; ++k)
            builder.append(String.format("R%d", k));

        return builder.toString();
    }

    /**
     * Returns the number of residues in this peptide.
     *
     * @return the number of residues in this peptide.
     */
    public int length() {
        return residues.size();
    }

    /**
     * Randomly mutates this peptide.
     *
     * @return a new peptide with one residue chosen at random changed
     * to another residue chosen at random with equal probability.
     */
    public Peptide mutate() {
        int index = JamRandom.global().nextInt(length());

        List<Residue> newResidues = new ArrayList<Residue>(residues);
        newResidues.set(index, residues.get(index).mutate(JamRandom.global()));

        return new Peptide(newResidues, false);
    }

    /**
     * Returns a read-only view of the residues in this peptide.
     *
     * @return a read-only view of the residues in this peptide.
     */
    public List<Residue> viewResidues() {
        return residues;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Peptide) && equalsPeptide((Peptide) obj);
    }

    private boolean equalsPeptide(Peptide that) {
        return this.residues.equals(that.residues);
    }

    @Override public int hashCode() {
        if (hashCode == UNSET_HASH_CODE)
            hashCode = residues.hashCode();

        return hashCode;
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Peptide(");

        for (Residue residue : residues)
            builder.append(residue.code1());

        builder.append(")");
        return builder.toString();
    }
}
