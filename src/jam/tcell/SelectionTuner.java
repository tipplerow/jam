
package jam.tcell;

import java.io.PrintWriter;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.lang.ObjectFactory;
import jam.math.DoubleUtil;
import jam.peptide.Peptide;
import jam.report.LineBuilder;
import jam.thymus.Thymus;
import jam.thymus.ThymusProperties;

public abstract class ThresholdTuner extends JamApp {
    private Thymus thymus;
    private PrintWriter writer;

    /**
     * Name of the summary file.
     */
    public static final String SUMMARY_NAME = "threshold-tuner-summary.csv";

    /**
     * Creates a new threshold tuner.
     *
     * @param propertyFiles files containing the system properties
     * that define the simulation parameters.
     */
    protected ThresholdTuner(String[] propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Returns the factory that creates cortical and medullary
     * self-peptides.
     *
     * @return the factory that creates cortical and medullary
     * self-peptides.
     */
    protected abstract ObjectFactory<? extends Peptide> getPeptideFactory();

    /**
     * Returns the factory that creates immature T cell receptors.
     *
     * @return the factory that creates immature T cell receptors.
     */
    protected abstract ObjectFactory<? extends TCR> getTCRFactory();

    /**
     * Simulates thymic selection and reports the selection rates.
     */
    protected void run() {
        thymus = Thymus.select(getPeptideFactory(), getTCRFactory());
        writer = openWriter(SUMMARY_NAME);

        writeHeader();
        writeData();
        autoClose();
    }

    private void writeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("trialIndex");
        builder.append("sharedCount");
        builder.append("cortexCount");
        builder.append("medullaCount");
        builder.append("posThreshold");
        builder.append("negThreshold");
        builder.append("posPassRate");
        builder.append("negPassRate");
        builder.append("netSelectRate");

        writer.println(builder.toString());
    }

    private void writeData() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(getTrialIndex());
        builder.append(ThymusProperties.getSharedPeptideCount());
        builder.append(ThymusProperties.getCortexPrivateCount());
        builder.append(ThymusProperties.getMedullaPrivateCount());
        builder.append(TCellProperties.getPositiveThreshold());
        builder.append(TCellProperties.getNegativeThreshold());
        builder.append(thymus.getPositivePassRate(), "%.4f");
        builder.append(thymus.getNegativePassRate(), "%.4f");
        builder.append(thymus.getNetSelectionRate(), "%.4f");

        writer.println(builder.toString());
    }
}
