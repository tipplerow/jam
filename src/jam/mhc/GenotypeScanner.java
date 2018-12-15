
package jam.mhc;

import java.io.PrintWriter;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.lang.ObjectFactory;
import jam.peptide.Peptide;

/**
 * Provides a command-line application to assist with tuning the
 * presentation threshold.
 */
public abstract class GenotypeScanner extends JamApp {
    private int trialIndex;
    private PrintWriter writer;

    private static final int LOG_INTERVAL = 100;

    /**
     * Number of random genotypes to generate.
     */
    public static final int TRIAL_COUNT = 10000;

    /**
     * Name of the presentation detail report.
     */
    public static final String GENOTYPE_SCAN_NAME = "genotype-scan.csv";

    /**
     * Creates a new presentation tuner.
     *
     * @param propertyFiles files containing the system properties
     * that define the simulation parameters.
     */
    protected GenotypeScanner(String[] propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Returns the creator of individual MHC alleles.
     *
     * @return the creator of individual MHC alleles.
     */
    protected abstract ObjectFactory<? extends MHC> getMHCFactory();

    /**
     * Computes and reports the presentation rates.
     */
    protected void run() {
        writer = openWriter(GENOTYPE_SCAN_NAME);
        writer.println("trial,rate");

        for (trialIndex = 0; trialIndex < TRIAL_COUNT; ++trialIndex)
            runTrial();

        autoClose();
    }

    private void runTrial() {
        if (trialIndex % LOG_INTERVAL == 0)
            JamLogger.info("Running trial [%d]...", trialIndex);

        Genotype genotype = Genotype.create(getMHCFactory());
        double rate = genotype.computePresentationRate(MHCProperties.enumerateCanonicalTargets());

        writer.println(String.format("%d,%.6f", trialIndex, rate));
        writer.flush();
    }
}
