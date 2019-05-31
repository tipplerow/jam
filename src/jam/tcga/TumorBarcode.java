
package jam.tcga;

import jam.lang.JamException;
import jam.lang.KeyedObject;

/**
 * Represents a unique tumor sample identifier in the TCGA database.
 */
public final class TumorBarcode extends KeyedObject<String> {
    private TumorBarcode(String key) {
        super(key);
        validate(key);
    }

    private static final void validate(String key) {
        if (key.length() < 16)
            throw JamException.runtime("Invalid tumor barcode: [%s].", key);
    }

    /**
     * Returns the tumor barcode for a given key string.
     *
     * @param key the key string.
     *
     * @return the tumor barcode for the given key string.
     */
    public static TumorBarcode instance(String key) {
        return new TumorBarcode(key);
    }

    /**
     * Returns the ID for the patient from which this tumor was
     * sampled.
     *
     * @return the ID for the patient from which this tumor was
     * sampled.
     */
    public PatientID patientID() {
        return PatientID.instance(getKey().substring(0, PatientID.KEY_LENGTH));
    }
}
