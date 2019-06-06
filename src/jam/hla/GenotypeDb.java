
package jam.hla;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jam.io.TableReader;
import jam.lang.JamException;
import jam.tcga.PatientID;
import jam.util.RegexUtil;

/**
 * Reads genotypes from a file and stores them in memory indexed by
 * patient key.
 *
 * <p><b>File format.</b> The standard file format contains a header
 * line followed by comman-separated lines containing a patient key
 * and a genotype code.  The genotype code lists each allele present
 * in the genotype (duplicates will be present for homozygous alleles)
 * separated by white space.
 *
 * <p>Commas are the stanard delimiter used to separate patient keys
 * and genotypes, but tabs and pipe characters ({@code |}) are also
 * permitted.
 */
public final class GenotypeDb {
    private final Map<PatientID, Genotype> genotypes;

    private static final Pattern ALLELE_ALELE_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    private GenotypeDb() {
        this.genotypes = new HashMap<PatientID, Genotype>();
    }

    /**
     * Loads patient genotypes from a file in standard format.
     *
     * @param file the genotype file to read.
     *
     * @return a genotype database containing the patient genotypes
     * mapped to their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static GenotypeDb load(File file) {
        GenotypeDb db = new GenotypeDb();

        TableReader reader = openReader(file);
        db.load(reader);

        return db;
    }

    /**
     * Loads patient genotypes from a file in standard format.
     *
     * @param fileName the name of the genotype file to read.
     *
     * @return a genotype database containing the patient genotypes
     * mapped to their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static GenotypeDb load(String fileName) {
        return load(new File(fileName));
    }

    private static TableReader openReader(File file) {
        TableReader reader = TableReader.open(file);

        if (reader.columnKeys().size() != 2)
            throw JamException.runtime("Invalid header in genotype file: [%s].", file);

        return reader;
    }

    private void load(TableReader reader) {
        try {
            for (List<String> columns : reader)
                parseColumns(columns);
        }
        finally {
            reader.close();
        }
    }

    private void parseColumns(List<String> columns) {
        assert columns.size() == 2;

        PatientID patientID = PatientID.instance(columns.get(0));
        Genotype  genotype  = Genotype.parse(columns.get(1), ALLELE_ALELE_DELIM);

        if (genotypes.containsKey(patientID))
            throw JamException.runtime("Duplicate patient ID: [%s]", patientID.getKey());

        genotypes.put(patientID, genotype);
    }

    /**
     * Identifies patients with genotypes in this database.
     *
     * @param patientID a patient key of interest.
     *
     * @return {@code true} iff this database contains a genotype for
     * the specified patient.
     */
    public boolean contains(PatientID patientID) {
        return genotypes.containsKey(patientID);
    }

    /**
     * Returns the genotype for a given patient.
     *
     * @param patientID a patient key of interest.
     *
     * @return the genotype for the specified patient, or {@code null}
     * if the patient is not in this database.
     */
    public Genotype lookup(PatientID patientID) {
        return genotypes.get(patientID);
    }

    /**
     * Returns the genotype for a given patient.
     *
     * @param patientID a patient key of interest.
     *
     * @return the genotype for the specified patient, or {@code null}
     * if the patient is not in this database.
     */
    public Genotype require(PatientID patientID) {
        Genotype genotype = lookup(patientID);

        if (genotype != null)
            return genotype;
        else
            throw JamException.runtime("No genotype for patient [%s].", patientID.getKey());
    }

    /**
     * Returns the number of genotypes in this database.
     *
     * @return the number of genotypes in this database.
     */
    public int size() {
        return genotypes.size();
    }

    /**
     * Returns a read-only view of the patients in this database.
     *
     * @return an unmodifiable set containing all patient keys from
     * this database.
     */
    public Set<PatientID> viewPatients() {
        return Collections.unmodifiableSet(genotypes.keySet());
    }
}
