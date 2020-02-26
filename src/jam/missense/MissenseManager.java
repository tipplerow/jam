
package jam.missense;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.maf.MAFFastaList;
import jam.maf.MAFFastaRecord;
import jam.tcga.TumorBarcode;

/**
 * Manages FASTA files that contain protein structures resulting from
 * missense mutations.
 */
public final class MissenseManager {
    private final String dirName;

    private MissenseManager(String dirName) {
        this.dirName = dirName;
    }

    private static final String BASE_SUFFIX = "_missense.fa.gz";

    /**
     * Creates a new missense data manager.
     *
     * @param dirName the directory containing individual tumor
     * FASTA files.
     *
     * @return a new missense data manager for the specified
     * directory.
     */
    public static MissenseManager create(String dirName) {
        return new MissenseManager(dirName);
    }

    /**
     * Returns the full path name of the FASTA file for a given
     * tumor sample.
     *
     * @param barcode the barcode of the tumor sample.
     *
     * @return the full path name of the FASTA file for a given
     * tumor sample.
     */
    public File barcodeFile(TumorBarcode barcode) {
        return new File(dirName, baseName(barcode));
    }

    private static String baseName(TumorBarcode barcode) {
        return barcode.getKey() + BASE_SUFFIX;
    }

    /**
     * Determines whether the FASTA file for a given tumor
     * sample exists.
     *
     * @param barcode the barcode of the tumor sample.
     *
     * @return {@code true} iff the FASTA file for the
     * specified tumor sample exists in the data directory.
     */
    public boolean exists(TumorBarcode barcode) {
        return barcodeFile(barcode).exists();
    }

    /**
     * Loads the FASTA records for a given tumor sample.
     *
     * @param barcode the barcode of the desired tumor sample.
     *
     * @return the FASTA records for the specified tumor sample
     * (an empty list if the sample is not found).
     */
    public MAFFastaList load(TumorBarcode barcode) {
        File file = barcodeFile(barcode);

        if (file.canRead())
            return load(file);
        else
            return MAFFastaList.EMPTY;
    }

    private static MAFFastaList load(File file) {
        Collection<MAFFastaRecord> records = new ArrayList<MAFFastaRecord>();
        JamLogger.info("Loading missense FASTA file [%s]...", file.getName());
        /*
        try (PrintWriter writer = IOUtil.openWriter(file)) {
            for (MAFFastaRecord record : records)
                writer.println(record.format());
        }
        */
        return MAFFastaList.create(records);
    }

    /**
     * Stores the FASTA file for a given tumor sample.
     *
     * @param barcode the barcode for the tumor sample.
     *
     * @param records the FASTA records for the tumor sample.
     */
    public void store(TumorBarcode barcode, Collection<MAFFastaRecord> records) {
        store(barcodeFile(barcode), records);
    }

    private static void store(File file, Collection<MAFFastaRecord> records) {
        JamLogger.info("Writing missense FASTA file [%s]...", file.getName());

        try (PrintWriter writer = IOUtil.openWriter(file)) {
            for (MAFFastaRecord record : records)
                writer.println(record.format());
        }
    }
}
