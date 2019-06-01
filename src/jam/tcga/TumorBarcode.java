
package jam.tcga;

import jam.lang.JamException;
import jam.lang.KeyedObject;

/**
 * Represents a unique tumor sample identifier in the TCGA database.
 */
public final class TumorBarcode extends KeyedObject<String> {
    private TumorBarcode(String key) {
        super(key);
    }

    /**
     * The canonical column name for tumor identifiers in the header
     * line of data files to be analyzed by the {@code jam} library.
     */
    public static final String COLUMN_NAME = "Tumor_Sample_Barcode";

    /**
     * String length of the patient key encoded at the beginning of
     * TCGA barcodes.
     */
    public static final int PATIENT_KEY_LENGTH = 12;

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
        return PatientID.instance(getKey().substring(0, PATIENT_KEY_LENGTH));
    }
}
