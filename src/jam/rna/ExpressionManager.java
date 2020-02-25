
package jam.rna;

import java.io.File;

import jam.tcga.TumorBarcode;

/**
 * Manages persistent RNA expression profile data.
 */
public final class ExpressionManager {
    private final String dirName;

    private ExpressionManager(String dirName) {
        this.dirName = dirName;
    }

    /**
     * Creates a new expression data manager.
     *
     * @param dirName the directory containing individual tumor
     * expression profiles.
     *
     * @return a new expression data manager for the specified
     * directory.
     */
    public static ExpressionManager create(String dirName) {
        return new ExpressionManager(dirName);
    }

    /**
     * Determines whether the expression profile for a given tumor
     * sample exists.
     *
     * @param barcode the barcode of the tumor sample.
     *
     * @return {@code true} iff the expression profile for the
     * specified tumor sample exists in the data directory.
     */
    public boolean exists(TumorBarcode barcode) {
        return barcodeFile(barcode).exists();
    }

    /**
     * Loads the expression profile for a given tumor sample.
     *
     * @param barcode the barcode of the desired tumor sample.
     *
     * @return the expression profile for the specified tumor sample
     * ({@code null} if the profile does not exist).
     */
    public ExpressionProfile load(TumorBarcode barcode) {
        File file = barcodeFile(barcode);

        if (file.canRead())
            return ExpressionProfile.load(file);
        else
            return null;
    }

    private File barcodeFile(TumorBarcode barcode) {
        return new File(dirName, baseName(barcode));
    }

    private String baseName(TumorBarcode barcode) {
        return barcode.getKey() + "_expression_profile.csv.gz";
    }

    /**
     * Stores the expression profile for a given tumor sample.
     *
     * @param barcode the barcode for the tumor sample.
     *
     * @param profile the expression profile for the tumor sample.
     */
    public void store(TumorBarcode barcode, ExpressionProfile profile) {
        profile.store(barcodeFile(barcode));
    }
}
