
package jam.nap;

import jam.app.JamApp;
import jam.app.JamLogger;

import jam.hla.Genotype;
import jam.hla.GenotypeDb;
import jam.tcga.PatientID;
import jam.tcga.TumorBarcode;

/**
 * Computes neoantigen presentation (NAP) scores for a tumor genomic
 * dataset.
 */
public final class NAPDriver extends JamApp {

    private NAPDriver(String... propFiles) {
        super(propFiles);
    }

    public static final String ALLELE_REPORT_FILE_PROPERTY = "jam.nap.alleleReportFile";

    public static final String GENOTYPE_REPORT_FILE_PROPERTY = "jam.nap.genotypeReportFile";

    private void run() {
    }

    public static void main(String[] propFiles) {
        NAPDriver driver = new NAPDriver(propFiles);
        driver.run();
    }
}
