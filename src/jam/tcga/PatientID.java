
package jam.tcga;

import jam.lang.JamException;
import jam.lang.KeyedObject;

/**
 * Represents a unique patient identifier in the TCGA database.
 */
public final class PatientID extends KeyedObject<String> {
    private PatientID(String key) {
        super(key);
        validate(key);
    }

    private static final void validate(String key) {
        if (key.length() != KEY_LENGTH)
            throw JamException.runtime("Invalid patient ID: [%s].", key);
    }

    /**
     * String length of all valid identifiers.
     */
    public static final int KEY_LENGTH = 12;

    /**
     * Returns the patient ID for a given key string.
     *
     * @param key the key string.
     *
     * @return the patient ID for the given key string.
     */
    public static PatientID instance(String key) {
        return new PatientID(key);
    }
}
