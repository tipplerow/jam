
package jam.peptide;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import jam.io.IOUtil;
import jam.io.ObjectParser;
import jam.io.ObjectReader;
import jam.lang.ObjectFactory;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.report.LineBuilder;
import jam.util.ListUtil;

final class PeptideParser implements ObjectParser<Peptide> {
    @Override public Peptide parse(String s) {
        return Peptide.parse(s);
    }
}

/**
 * Defines a fixed linear sequence of amino acids.
 */
public interface Peptide extends Iterable<Residue> {
    /**
     * The maximum peptide length that can be explicitly enumerated
     * without exceeding the maximum size of a {@code Collection}.
     */
    public static final int ENUMERATION_LIMIT = 7;

    /**
     * The global peptide parser.
     */
    public static final ObjectParser<Peptide> PARSER = new PeptideParser();

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the sequence of residues to compose the peptide.
     *
     * @return a new peptide with the specified sequence.
     *
     * @throws IllegalArgumentException if the residue sequence is empty.
     */
    public static Peptide of(Residue... residues) {
        return new ArrayPeptide(residues);
    }

    /**
     * Creates a new peptide from a sequence of residues.
     *
     * @param residues the list of residues to compose the peptide.
     *
     * @return a new peptide with the specified sequence.
     *
     * @throws IllegalArgumentException if the residue list is empty.
     */
    public static Peptide of(List<Residue> residues) {
        return new ArrayPeptide(residues, true);
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
     * Enumerates all native peptides of a given length having a
     * distinct unordered representation (residue count).
     *
     * @param length the desired peptide length.
     *
     * @return a list containing all native peptides (composed of
     * native residues) with the specified length having a distinct
     * unordered representation (residue count).
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static List<Peptide> enumerateUnordered(int length) {
        List<Peptide> allPeptides = enumerate(length);
        ArrayList<Peptide> unorderedPeptides = new ArrayList<Peptide>();
        Set<Multiset<Residue>> unorderedKeys = new HashSet<Multiset<Residue>>();

        for (Peptide peptide : allPeptides) {
            Multiset<Residue> unorderedKey = peptide.unordered();

            if (!unorderedKeys.contains(unorderedKey)) {
                unorderedKeys.add(unorderedKey);
                unorderedPeptides.add(peptide);
            }
        }

        unorderedPeptides.trimToSize();
        return unorderedPeptides;
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
     * Returns the header line for CSV files containing peptides of a
     * given length.
     *
     * @param length the number of residues in the peptides.
     *
     * @return the header line for CSV files containing peptides with
     * the specified length.
     */
    public static String headerCSV(int length) {
        LineBuilder builder = LineBuilder.csv();

        for (int k = 0; k < length; ++k)
            builder.append(String.format("R%d", k));

        return builder.toString();
    }

    /**
     * Identifies native peptides.
     *
     * @return {@code true} iff every residue in this peptide is a
     * native residue.
     */
    public default boolean isNative() {
        for (Residue residue : this)
            if (!residue.isNative())
                return false;

        return true;
    }

    /**
     * Generates a unique key to identify peptide isomers.
     *
     * @return the isomer key for this peptide.
     */
    public default String isomerKey() {
        char[] codes = new char[length()];

        for (int k = 0; k < length(); ++k)
            codes[k] = at(k).code1();

        Arrays.sort(codes);
        return String.valueOf(codes);
    }

    /**
     * Reads peptides from a flat file containing one peptide per line
     * (and no header line).
     *
     * @param fileName the name of the flat file to read.
     *
     * @return a list of the peptides contained in the specified file.
     *
     * @throws RuntimeException if any I/O errors occur.
     */
    public static List<Peptide> loadFlatFile(String fileName) {
        return ObjectReader.load(fileName, PARSER);
    }

    /**
     * Enumerates all distinct isomers for peptides with a fixed
     * length and counts the number of occurrences of each isomer.
     *
     * @param length the desired peptide length.
     *
     * @return a multiset counting the number of occurrences of each
     * distinct isomer for peptides with the specified length.
     *
     * @throws IllegalArgumentException unless the length is positive
     * but not greater than the enumeration limit.
     */
    public static Multiset<String> mapIsomers(int length) {
        return mapIsomers(enumerate(length));
    }

    /**
     * Enumerates all distinct isomers for a collection of peptides.
     *
     * @param peptides a collection of peptides to examine.
     *
     * @return a multiset counting the number of occurrences of each
     * distinct isomer in the peptide collection.
     */
    public static Multiset<String> mapIsomers(Collection<Peptide> peptides) {
        Multiset<String> counts = TreeMultiset.create();

        for (Peptide peptide : peptides)
            counts.add(peptide.isomerKey());

        return counts;
    }

    /**
     * Returns an object factory that creates new peptides of
     * fixed length with native residues chosen randomly with 
     * equal probability.
     *
     * @param length the desired number of residues.
     *
     * @return an object factory that creates new peptides of
     * fixed length with native residues chosen randomly with 
     * equal probability.
     */
    public static ObjectFactory<Peptide> nativeFactory(int length) {
        return ArrayPeptide.nativeFactory(length);
    }

    /**
     * Extract all native N-mers from this peptide.
     *
     * @param N the length of peptide fragments to extract.
     *
     * @return a list containing all native N-mers from this peptide.
     */
    public default List<Peptide> nativeFragments(int N) {
        if (length() < N)
            return Collections.emptyList();

        List<Peptide> fragments = new ArrayList<Peptide>(length() - N + 1);

        for (int start = 0; start <= length() - N; ++start) {
            Peptide fragment = fragment(new IntRange(start, start + N - 1));

            if (fragment.isNative())
                fragments.add(fragment);
        }

        return fragments;
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
        return ArrayPeptide.newNative(length);
    }

    /**
     * Creates new peptides with native residues chosen randomly with
     * equal probability.
     *
     * @param length the desired number of residues.
     *
     * @param count the desired number of peptides.
     *
     * @return a list of {@code count} new peptides, each with exactly
     * {@code length} randomly chosen native residues.
     */
    public static List<Peptide> newNative(int length, int count) {
        List<Peptide> peptides = new ArrayList<Peptide>(count);

        while (peptides.size() < count)
            peptides.add(newNative(length));

        return peptides;
    }

    /**
     * Generates randomly mutations in a collection of peptides.
     *
     * <p>In one iteration, a parent peptide is selected at random and
     * then mutated; the process repeats {@code mutationCount} times.
     * Some parents may be selected more than once, some not at all.
     *
     * @param parents the parent peptides to choose from.
     *
     * @param mutationCount the desired number of mutated peptides to
     * generate.
     *
     * @return a list of mutated peptides.
     */
    public static List<Peptide> mutate(Collection<? extends Peptide> parents, int mutationCount) {
        //
        // Dump the parents into an ArrayList for efficient random
        // selection...
        //
        List<Peptide> parentList = new ArrayList<Peptide>(parents);
        List<Peptide> mutantList = new ArrayList<Peptide>(mutationCount);

        while (mutantList.size() < mutationCount)
            mutantList.add(ListUtil.select(parentList).mutate());

        return mutantList;
    }

    /**
     * Creates a peptide from a string representation containing a
     * sequence of single-character residue codes.
     *
     * @param s a sequence of single-character residue codes.
     *
     * @return a peptide containing the residues specified in the
     * input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * valid peptide representation.
     */
    public static Peptide parse(String s) {
        List<Residue> residues = new ArrayList<Residue>(s.length());

        for (int k = 0; k < s.length(); ++k)
            residues.add(Residue.valueOfCode1(s.charAt(k)));

        return new ArrayPeptide(residues, false);
    }

    /**
     * Appends a sequence of residues to this peptide and returns a
     * new peptide with the full sequence; this peptide is unchanged.
     *
     * @param addlResidues the residues to append.
     *
     * @return the new peptide with the additional residues.
     */
    public default Peptide append(Residue... addlResidues) {
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
    public abstract Peptide append(List<Residue> addlResidues);

    /**
     * Appends another peptide to this peptide and returns a new
     * peptide with the full sequence; this peptide is unchanged.
     *
     * @param peptide the peptide to append.
     *
     * @return the new peptide with the additional residues.
     */
    public default Peptide append(Peptide peptide) {
        return append(peptide.viewResidues());
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
    public abstract Residue at(int index);

    /**
     * Returns a read-only view of a subsegment of this peptide.
     *
     * <p>The residue indexes in the fragment are {@code 0, ..., range.size() - 1}
     * and correspond to indexes {@code range.lower(), ..., range.upper()} on this
     * peptide.
     *
     * @param range the index range of residues in the fragment.
     *
     * @return a read-only view of a subsegment of this peptide.
     *
     * @throws RuntimeException unless the specified falls entirely
     * within this peptide.
     */
    public abstract Peptide fragment(IntRange range);

    /**
     * Returns the number of residues in this peptide.
     *
     * @return the number of residues in this peptide.
     */
    public abstract int length();

    /**
     * Randomly mutates this peptide.
     *
     * @return a new peptide with one residue chosen at random changed
     * to another residue chosen at random with equal probability.
     */
    public abstract Peptide mutate();

    /**
     * Applies a single-residue mutation to this peptide.
     *
     * @param mutation the mutation to apply.
     *
     * @return a new peptide with the specified mutation applied.
     *
     * @throws IllegalArgumentException unless the residue at the
     * mutation location in this peptide matches the one specified
     * as the original residue and the mutation position lies within
     * this peptide.
     */
    public abstract Peptide mutate(ProteinChange mutation);

    /**
     * Returns an unordered view of this peptide: a multiset counting
     * the number of times each residue occurs in this peptide.
     *
     * @return an unordered view of this peptide.
     */
    public default Multiset<Residue> unordered() {
        Multiset<Residue> counts = EnumMultiset.create(Residue.class);

        for (Residue residue : viewResidues())
            counts.add(residue);

        return counts;
    }

    /**
     * Returns a read-only view of the residues in this peptide.
     *
     * @return a read-only view of the residues in this peptide.
     */
    public abstract List<Residue> viewResidues();

    /**
     * Formats the residues in this peptide for output to a CSV file.
     *
     * @return the residues in this peptide formatted for output to a
     * CSV file.
     */
    public default String formatCSV() {
        LineBuilder builder = LineBuilder.csv();

        for (Residue residue : viewResidues())
            builder.append(residue.code1());

        return builder.toString();
    }

    /**
     * Formats the residues in this peptide into a string of
     * single-character codes.
     *
     * @return the residues in this peptide as string of
     * single-character codes.
     */
    public default String formatString() {
        StringBuilder builder = new StringBuilder();

        for (Residue residue : viewResidues())
            builder.append(residue.code1());

        return builder.toString();
    }

    /**
     * Formats each peptide in a collection.
     *
     * @param peptides the peptides to format.
     *
     * @return the formatted peptide strings in the order returned by
     * the collection iterator.
     */
    public static List<String> formatString(Collection<Peptide> peptides) {
        List<String> strings = new ArrayList<String>(peptides.size());

        for (Peptide peptide : peptides)
            strings.add(peptide.formatString());

        return strings;
    }

    /**
     * Returns the header line for CSV files containing peptides with
     * the same length as this peptide.
     *
     * @return the header line for CSV files containing peptides with
     * the same length as this peptide.
     */
    public default String headerCSV() {
        return headerCSV(length());
    }

    @Override public default Iterator<Residue> iterator() {
        return viewResidues().iterator();
    }
}
