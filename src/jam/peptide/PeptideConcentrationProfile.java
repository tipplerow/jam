
package jam.peptide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jam.app.JamLogger;
import jam.chem.Concentration;
import jam.io.TableReader;
import jam.io.TableWriter;
import jam.lang.JamException;

/**
 * Maps peptides to cellular concentrations.
 */
public final class PeptideConcentrationProfile {
    private final Map<Peptide, PeptideConcentration> map =
        new HashMap<Peptide, PeptideConcentration>();

    private static final String CONCENTRATION_FORMAT = "%.6E";

    private PeptideConcentrationProfile() {
    }

    /**
     * The single empty concentration profile.
     */
    public static PeptideConcentrationProfile EMPTY = new PeptideConcentrationProfile();

    /**
     * Creates a new, empty concentration profile.
     *
     * @return a new, empty concentration profile.
     */
    public static PeptideConcentrationProfile create() {
        return new PeptideConcentrationProfile();
    }

    /**
     * Loads a peptide concentration profile from a data file.
     *
     * @param file the data file to load.
     *
     * @return the peptide concentration profile stored in the
     * specified data file.
     */
    public static PeptideConcentrationProfile load(File file) {
        JamLogger.info("Loading peptide concentration profile [%s]...", file.getName());

        try (TableReader reader = TableReader.open(file)) {
            return load(reader);
        }
    }

    private static PeptideConcentrationProfile load(TableReader reader) {
        if (reader.ncol() != 2)
            throw JamException.runtime("Exactly two columns are required for a concentration profile.");

        PeptideConcentrationProfile profile = create();

        for (List<String> fields : reader) {
            Peptide peptide = Peptide.parse(fields.get(0));
            Concentration concentration = Concentration.parse(fields.get(1));

            profile.add(peptide, concentration);
        }

        return profile;
    }

    /**
     * Loads a peptide concentration profile from a data file.
     *
     * @param fileName the name of the data file to load.
     *
     * @return the peptide concentration profile stored in the
     * specified data file.
     */
    public static PeptideConcentrationProfile load(String fileName) {
        return load(new File(fileName));
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

        PeptideConcentration existing = map.get(peptide);

        if (existing == null)
            put(new PeptideConcentration(peptide, concentration));
        else
            put(existing.plus(concentration));
    }

    private void put(PeptideConcentration record) {
        map.put(record.getPeptide(), record);
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
     * Returns the number of peptides in this concentration profile.
     *
     * @return the number of peptides in this concentration profile.
     */
    public int countPeptides() {
        return map.size();
    }

    /**
     * Returns a list containing the peptide concentration records in
     * this profile.
     *
     * @return a list containing the peptide concentration records in
     * this profile.
     */
    public List<PeptideConcentration> list() {
        return new ArrayList<PeptideConcentration>(map.values());
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
        PeptideConcentration record = map.get(peptide);

        if (record != null)
            return record.getConcentration();
        else
            return Concentration.ZERO;
    }

    /**
     * Stores this peptide concentration profile in a data file.
     *
     * @param file the data file to write (previous contents will be
     * erased).
     */
    public void store(File file) {
        JamLogger.info("Storing peptide concentration profile [%s]...", file.getName());

        try (TableWriter writer = TableWriter.open(file)) {
            store(writer);
        }
    }

    private void store(TableWriter writer) {
        writeHeader(writer);
        writeConcentration(writer);
    }

    private void writeHeader(TableWriter writer) {
        writer.println("Hugo_Symbol", "Concentration");
    }

    private void writeConcentration(TableWriter writer) {
        for (PeptideConcentration conc : map.values())
            writeConcentration(writer, conc);
    }

    private void writeConcentration(TableWriter writer, PeptideConcentration conc) {
        String peptide = conc.getPeptide().formatString();
        String concStr = conc.getConcentration().format(CONCENTRATION_FORMAT);

        writer.println(peptide, concStr);
    }

    /**
     * Stores this peptide concentration profile in a data file.
     *
     * @param fileName the name of the data file to write (previous
     * contents will be erased).
     */
    public void store(String fileName) {
        store(new File(fileName));
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
