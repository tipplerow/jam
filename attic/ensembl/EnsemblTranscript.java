
package jam.ensembl;

/**
 * Represents the unique Ensembl transcript identifier.
 */
public final class EnsemblTranscript extends EnsemblID {
    private static final String LABEL_CODE = "transcript:";

    private EnsemblTranscript(String key) {
        super(key);
    }

    /**
     * The canonical column name for transcript identifiers in the
     * header line of data files to be analyzed by the {@code jam}
     * library.
     */
    public static final String COLUMN_NAME = "Transcript_ID";

    /**
     * Returns the Ensemble transcript identifier for a given key
     * string.
     *
     * @param key the key string.
     *
     * @return the Ensemble transcript identifier for the given key
     * string.
     */
    public static EnsemblTranscript instance(String key) {
        return new EnsemblTranscript(key);
    }

    /**
     * Extracts the transcript key from an Ensembl record header line.
     *
     * @param headerLine the header line from an Ensembl record.
     *
     * @return the transcript key contained in the given header line.
     *
     * @throws RuntimeException unless the header line contains a
     * properly formatted transcript key.
     */
    public static EnsemblTranscript parseHeader(String headerLine) {
        return new EnsemblTranscript(parseHeader(headerLine, LABEL_CODE));
    }
}
