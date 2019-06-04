
package jam.hla;

import java.util.Map;
import java.util.regex.Pattern;

import jam.tcga.PatientID;
import jam.util.RegexUtil;

/**
 * Manages the reading and writing of genotype files in a standard
 * format.
 *
 * <p><b>File format.</b> The standard file format contains the header
 * line {@code Patient_ID,Genotype} followed by comman-separated lines
 * containing a patient identifier and a genotype code.  The genotype
 * code lists each allele present in the genotype (duplicates will be
 * present for homozygous alleles) separated by white space.
 */
public final class GenotypeFile {
    /**
     * The delimiter separating patient identifiers from genotype
     * string codes.
     */
    public static final Pattern PATIENT_GENOTYPE_DELIM = RegexUtil.COMMA;

    /**
     * The delimiter separating HLA alleles within genotype string
     * codes.
     */
    public static final Pattern ALLELE_ALELE_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Reads patient genotypes from a file in standard format.
     *
     * @param fileName the name of the genotype file to read.
     *
     * @return patient genotypes mapped to their unique identifier.
     *
     * @throws RuntimeException unless the file name contains a valid
     * genotype file in standard format.
     */
    public static Map<PatientID, Genotype> read(String fileName) {
        return GenotypeReader.read(fileName);
    }
}
