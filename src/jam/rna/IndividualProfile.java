
package jam.rna;

import java.io.File;

import jam.app.JamProperties;
import jam.hugo.HugoSymbol;
import jam.tcga.TumorBarcode;

/**
 * Stores unique RNA profiles for each tumor in the sample cohort.
 */
public final class IndividualProfile extends ExpressionProfile {
    private final TumorExpressionMatrix profile;

    private static IndividualProfile global = null;

    private IndividualProfile(TumorExpressionMatrix profile) {
        this.profile = profile;
    }

    /**
     * Name of the system property that specifies the data file
     * containing the tumor expression profiles.
     */
    public static final String PROFILE_FILE_NAME_PROPERTY =
        "jam.rna.tumorExpressionProfile";

    /**
     * Returns the global expression profile defined by system
     * properties.
     *
     * @return the global expression profile defined by system
     * properties.
     */
    public static IndividualProfile global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static IndividualProfile createGlobal() {
        return load(resolveProfileFileName());
    }

    private static String resolveProfileFileName() {
        return JamProperties.getRequired(PROFILE_FILE_NAME_PROPERTY);
    }

    /**
     * Loads an individual expression profile from a data file.
     *
     * @param file the file to load.
     *
     * @return the individual expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static IndividualProfile load(File file) {
        return new IndividualProfile(TumorExpressionMatrix.load(file));
    }

    /**
     * Loads an individual expression profile from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return the individual expression profile contained in the
     * specified data file.
     *
     * @throws RuntimeException unless the specified file contains a
     * valid expression profile.
     */
    public static IndividualProfile load(String fileName) {
        return load(new File(fileName));
    }

    @Override public Expression lookup(TumorBarcode barcode, HugoSymbol symbol) {
        return profile.get(barcode, symbol);
    }
}
