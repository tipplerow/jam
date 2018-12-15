
package jam.mhc;

import java.io.PrintWriter;
import java.util.List;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.math.DoubleUtil;
import jam.peptide.Peptide;
import jam.report.LineBuilder;

/**
 * Provides a command-line application to assist with tuning the
 * presentation threshold.
 */
public abstract class PresentationTuner extends JamApp {
    private PrintWriter writer;
    private List<Peptide> targets;
    private List<? extends PairwiseMHC> alleles;

    /**
     * Name of the presentation detail report.
     */
    public static final String DETAIL_REPORT_NAME = "presentation-detail-report.csv";

    /**
     * Creates a new presentation tuner.
     *
     * @param propertyFiles files containing the system properties
     * that define the simulation parameters.
     */
    protected PresentationTuner(String[] propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Returns a list containing all MHC alleles.
     *
     * @return a list containing all MHC alleles.
     */
    protected abstract List<? extends PairwiseMHC> enumerateAlleles();

    /**
     * Computes and reports the presentation rates.
     */
    protected void run() {
        writer = openWriter(DETAIL_REPORT_NAME);
        alleles = enumerateAlleles();
        targets = MHC.enumerateCanonicalTargets();

        writeHeader();
        writeData();
        autoClose();
    }

    private void writeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("alleleIndex");
        builder.append(alleles.get(0).getAnchor().header());
        builder.append("presentationRate");

        writer.println(builder.toString());
    }

    private void writeData() {
        for (int index = 0; index < alleles.size(); ++index)
            writeData(index);
    }

    private void writeData(int index) {
        PairwiseMHC mhc = alleles.get(index);
        LineBuilder builder = LineBuilder.csv();
        
        builder.append(getTrialIndex());
        builder.append(mhc.getAnchor().format());
        builder.append(computePresentationRate(mhc));

        writer.println(builder.toString());
    }

    private double computePresentationRate(MHC allele) {
        return allele.computePresentationRate(targets);
    }
}
