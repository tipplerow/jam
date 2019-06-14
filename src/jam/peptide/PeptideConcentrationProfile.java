
package jam.peptide;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jam.chem.Concentration;

/**
 * Maps peptides to cellular concentrations.
 */
public final class PeptideConcentrationProfile {
    private final Map<Peptide, Concentration> map =
        new HashMap<Peptide, Concentration>();

    private PeptideConcentrationProfile() {
    }

    /**
     * Creates a new, empty concentration profile.
     *
     * @return a new, empty concentration profile.
     */
    public static PeptideConcentrationProfile create() {
        return new PeptideConcentrationProfile();
    }

    /**
     * Adds a positive peptide concentration to this profile.
     *
     * <p>If the peptide is already present in this profile, the
     * specified concentration is added to the existing concentration.
     *
     * <p>Concentrations that are equal to zero (within the standard
     * floating-point tolerance) will <em>not</em> be added, so that
     * this profile will only contain peptides with a net positive
     * concentration.
     *
     * @param peptide the peptide of interest.
     *
     * @param concentration the concentration of the peptide.
     */
    public void add(Peptide peptide, Concentration concentration) {
        if (!concentration.isPositive())
            return;

        Concentration existing = map.get(peptide);

        if (existing != null)
            concentration = concentration.plus(existing);

        map.put(peptide, concentration);
    }

    /**
     * Adds peptides to this profile.
     *
     * <p>If any peptide is already present in this profile, the
     * specified concentration is added to its existing concentration.
     *
     * @param peptides the peptides of interest.
     *
     * @param concentration the uniform concentration of each peptide.
     */
    public void addAll(Collection<Peptide> peptides, Concentration concentration) {
        for (Peptide peptide : peptides)
            add(peptide, concentration);
    }

    /**
     * Identifies peptides in this profile.
     *
     * @param peptide the peptide of interest.
     *
     * @return {@code true} iff this profile contains the specified
     * peptide.
     */
    public boolean contains(Peptide peptide) {
        return map.containsKey(peptide);
    }

    /**
     * Returns the concentration of a given peptide.
     *
     * @param peptide the peptide of interest.
     *
     * @return the concentration of the specified peptide, or {@code
     * Concentration.ZERO} if this profile does not contain the given
     * peptide.
     */
    public Concentration lookup(Peptide peptide) {
        Concentration result = map.get(peptide);

        if (result != null)
            return result;
        else
            return Concentration.ZERO;
    }

    /**
     * Returns a read-only view of the peptides in this profile.
     *
     * @return all peptides in this profile in an unmodifiable set.
     */
    public Set<Peptide> viewPeptides() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
