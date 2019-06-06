
package jam.peptide;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jam.lang.ObjectFactory;
import jam.math.IntRange;
import jam.math.JamRandom;

final class ArrayPeptide extends AbstractPeptide {
    private final List<Residue> residues;

    ArrayPeptide(List<Residue> residues, boolean copy) {
        validateResidues(residues);

        if (copy)
            this.residues = new ArrayList<Residue>(residues);
        else
            this.residues = residues;
    }

    private static void validateResidues(List<Residue> residues) {
        if (residues.isEmpty())
            throw new IllegalArgumentException("Empty residue list.");
    }

    ArrayPeptide(Residue... residues) {
        this(Arrays.asList(residues), true);
    }

    static ObjectFactory<Peptide> nativeFactory(int length) {
        return new NativeFactory(length);
    }

    private static final class NativeFactory implements ObjectFactory<Peptide> {
        private final int length;

        private NativeFactory(int length) {
            this.length = length;
        }

        @Override public Peptide newInstance() {
            return newNative(length);
        }
    }

    static Peptide newNative(int length) {
        List<Residue> residues = new ArrayList<Residue>(length);

        while (residues.size() < length)
            residues.add(Residue.selectNative(JamRandom.global()));

        return new ArrayPeptide(residues, false);
    }

    @Override public Peptide append(List<Residue> addlResidues) {
        List<Residue> newResidues =
            new ArrayList<Residue>(this.residues);

        newResidues.addAll(addlResidues);

        return new ArrayPeptide(newResidues, false);
    }

    @Override public Residue at(int index) {
        return residues.get(index);
    }

    @Override public Peptide fragment(IntRange range) {
        return new ArrayPeptide(residues.subList(range.lower(), range.upper() + 1), false);
    }

    @Override public int length() {
        return residues.size();
    }

    @Override public Peptide mutate() {
        int index = JamRandom.global().nextInt(length());

        List<Residue> newResidues = new ArrayList<Residue>(residues);
        newResidues.set(index, residues.get(index).mutate(JamRandom.global()));

        return new ArrayPeptide(newResidues, false);
    }

    @Override public Peptide mutate(ProteinChange mutation) {
        List<Residue> newResidues = new ArrayList<Residue>(residues);
        mutation.apply(newResidues);

        return new ArrayPeptide(newResidues, false);
    }

    @Override public Peptide mutate(Collection<ProteinChange> mutations) {
        List<Residue> newResidues = new ArrayList<Residue>(residues);
        ProteinChange.apply(mutations, newResidues);

        return new ArrayPeptide(newResidues, false);
    }

    @Override public List<Residue> viewResidues() {
        return Collections.unmodifiableList(residues);
    }
}
