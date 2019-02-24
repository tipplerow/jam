
package jam.ensembl;

import java.util.regex.Pattern;

import jam.fasta.FastaRecord;
import jam.lang.JamException;
import jam.lang.KeyedObject;
import jam.util.RegexUtil;

/**
 * Represents the unique Ensembl gene identifier.
 */
public final class EnsemblGene extends KeyedObject<String> {
    private static final String GENE_DELIM = "gene:";
    private static final Pattern FIELD_DELIM = RegexUtil.MULTI_WHITE_SPACE;
    private static final Pattern VERSION_DELIM = RegexUtil.DOT;

    /**
     * Creates a new gene key.
     *
     * @param key the key string.
     */
    public EnsemblGene(String key) {
        super(key);
    }

    /**
     * Returns a gene object corresponding to a given key string.
     *
     * @param key the key string.
     *
     * @return a gene object corresponding to a given key string.
     */
    public static EnsemblGene instance(String key) {
        return new EnsemblGene(key);
    }

    /**
     * Extracts the gene key from an Ensembl FASTA record.
     *
     * @param record the FASTA record to parse.
     *
     * @return the gene key contained in the comment text of the FASTA
     * record.
     */
    public static EnsemblGene parse(FastaRecord record) {
        String[] fields = RegexUtil.split(FIELD_DELIM, record.getComment());

        for (String field : fields)
            if (field.startsWith(GENE_DELIM))
                return parse(field);

        throw JamException.runtime("Missing gene identifier: [%s]", record);
    }

    private static EnsemblGene parse(String keyField) {
        //
        // Remove the delimiter string...
        //
        keyField = keyField.substring(GENE_DELIM.length());

        // Remove the version number...
        String[] subFields = RegexUtil.split(VERSION_DELIM, keyField);

        return new EnsemblGene(subFields[0]);
    }
}
