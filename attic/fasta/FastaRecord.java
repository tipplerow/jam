
package jam.fasta;

import java.util.regex.Pattern;

import jam.peptide.Peptide;
import jam.util.RegexUtil;
import jam.util.StringUtil;

/**
 * Encapsulates a single record from a FASTA file.
 */
public final class FastaRecord {
    private final String  key;
    private final String  comment;
    private final Peptide peptide;

    /**
     * Special character identifying header lines in FASTA files.
     */
    public static final String HEADER_MARKER = ">";

    /**
     * Delimiter separating the header key from header comment text.
     */
    public static final Pattern KEY_COMMENT_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Maximum line length for FASTA files.
     */
    public static final int LINE_LENGTH = 70;

    /**
     * Creates a new FASTA record.
     *
     * @param key     the record key.
     * @param comment thd record comment.
     * @param peptide the peptide sequence.
     */
    public FastaRecord(String key, String comment, Peptide peptide) {
        this.key = key;
	this.comment = comment;
        this.peptide = peptide;
    }

    /**
     * Formats this record for output to a FASTA file.
     *
     * @return this record formatted as a single string.
     */
    public String format() {
        StringBuilder builder = new StringBuilder();

        builder.append(HEADER_MARKER);
        builder.append(key);
        builder.append(" ");
        builder.append(comment);

        for (int index = 0; index < peptide.length(); ++index) {
            if (index % LINE_LENGTH == 0)
                builder.append(System.lineSeparator());

            builder.append(peptide.get(index).code1());
        }

        return builder.toString();
    }

    /**
     * Returns the key for this record.
     *
     * @return the key for this record.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the comment for this record.
     *
     * @return the comment for this record.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the peptide sequence in this record.
     *
     * @return the peptide sequence in this record.
     */
    public Peptide getPeptide() {
        return peptide;
    }

    @Override public String toString() {
	return format();
    }
}
