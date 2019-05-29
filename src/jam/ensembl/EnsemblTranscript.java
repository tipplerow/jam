
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
