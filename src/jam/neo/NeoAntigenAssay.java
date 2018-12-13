
package jam.neo;

import java.util.Collection;
import java.io.PrintWriter;

import org.apache.commons.lang3.time.StopWatch;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.lang.ObjectFactory;
import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.peptide.Peptide;
import jam.report.LineBuilder;
import jam.tcell.TCR;
import jam.tcell.TCRAssay;
import jam.tcell.TCellProperties;
import jam.thymus.ThymicOutcome;
import jam.thymus.Thymus;
import jam.thymus.ThymusProperties;
import jam.util.CollectionUtil;

public abstract class NeoAntigenAssay extends JamApp {
    private final StopWatch stopWatch;

    private final int pathogenPeptideCount;
    private final int sharedNeoPeptideCount;
    private final int medullaNeoPeptideCount;

    private Thymus thymus;
    private Collection<? extends TCR> repertoire;

    private Collection<? extends Peptide> pathogenPeptides;
    private Collection<? extends Peptide> sharedNeoPeptides;
    private Collection<? extends Peptide> medullaNeoPeptides;

    private TCRAssay pathogenAssay;
    private TCRAssay sharedNeoAssay;
    private TCRAssay medullaNeoAssay;

    /**
     * Name of the system property that specifies the number of
     * pathogen-derived peptides to test for immunogenicity.
     */
    public static final String PATHOGEN_PEPTIDE_COUNT_PROPERTY = "jam.neo.pathogenPeptideCount";

    /**
     * Name of the system property that specifies the number of
     * shared neopeptides to test for immunogenicity.
     */
    public static final String SHARED_NEOPEPTIDE_COUNT_PROPERTY = "jam.neo.sharedNeoPeptideCount";

    /**
     * Name of the system property that specifies the number of
     * medulla-only neopeptides to test for immunogenicity.
     */
    public static final String MEDULLA_NEOPEPTIDE_COUNT_PROPERTY = "jam.neo.medullaNeoPeptideCount";

    /**
     * Base name for the summary file.
     */
    public static final String SUMMARY_NAME = "neoantigen-assay-summary.csv";

    /**
     * Creates a new assay application.
     *
     * @param propertyFiles files containing the system properties
     * that define the simulation parameters.
     */
    protected NeoAntigenAssay(String[] propertyFiles) {
        super(propertyFiles);
        this.stopWatch = new StopWatch();

        this.pathogenPeptideCount =
            JamProperties.getRequiredInt(PATHOGEN_PEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);

        this.sharedNeoPeptideCount =
            JamProperties.getRequiredInt(SHARED_NEOPEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);

        this.medullaNeoPeptideCount =
            JamProperties.getRequiredInt(MEDULLA_NEOPEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);
    }

    /**
     * Returns the factory that creates cortical and medullary
     * self-peptides and pathogen-derived peptides.
     *
     * @return the factory that creates cortical and medullary
     * self-peptides and pathogen-derived peptides.
     */
    protected abstract ObjectFactory<? extends Peptide> getPeptideFactory();

    /**
     * Returns the factory that creates immature T cell receptors.
     *
     * @return the factory that creates immature T cell receptors.
     */
    protected abstract ObjectFactory<? extends TCR> getTCRFactory();

    /**
     * Runs the simulation.
     */
    protected void run() {
        stopWatch.start();
        writeRuntimeProperties("jam.");

        JamLogger.info("Simulating T cell selection...");
        thymus = Thymus.select(getPeptideFactory(), getTCRFactory());
        repertoire = thymus.viewRepertoire();

        JamLogger.info("Creating the peptide challengers...");
        pathogenPeptides = getPeptideFactory().newInstances(pathogenPeptideCount);
        sharedNeoPeptides = Peptide.mutate(thymus.viewSharedPeptides(), sharedNeoPeptideCount);
        medullaNeoPeptides = Peptide.mutate(thymus.viewMedullaPeptides(), medullaNeoPeptideCount);

        JamLogger.info("Running assays...");
        pathogenAssay = TCRAssay.run(repertoire, pathogenPeptides);
        sharedNeoAssay = TCRAssay.run(repertoire, sharedNeoPeptides);
        medullaNeoAssay = TCRAssay.run(repertoire, medullaNeoPeptides);

        JamLogger.info("Writing summary...");
        writeSummary();
        autoClose();

        JamLogger.info("DONE!");
    }

    private void writeSummary() {
        PrintWriter writer = openWriter(SUMMARY_NAME);

        writer.println(summaryHeaderLine());
        writer.println(summaryDataLine());
    }

    private String summaryHeaderLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("trialIndex");
        builder.append("sharedPeptideCount");
        builder.append("cortexPrivateCount");
        builder.append("medullaPrivateCount");
        builder.append("repertoireSize");
        builder.append("positiveThreshold");
        builder.append("negativeThreshold");
        builder.append("positivePassRate");
        builder.append("negativePassRate");
        builder.append("netSelectionRate");

        builder.append("meanAff.FP");
        builder.append("meanAff.FN");
        builder.append("meanAff.EX");

        appendAssayHeader(builder, "pathogen");
        appendAssayHeader(builder, "sharedNeo");
        appendAssayHeader(builder, "medullaNeo");

        builder.append("elapsedSec");

        return builder.toString();
    }

    private void appendAssayHeader(LineBuilder builder, String prefix) {
        builder.append(prefix + "." + "coveredFrac");
        builder.append(prefix + "." + "reactiveFrac");
    }

    private String summaryDataLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(getTrialIndex());
        builder.append(ThymusProperties.getSharedPeptideCount());
        builder.append(ThymusProperties.getCortexPrivateCount());
        builder.append(ThymusProperties.getMedullaPrivateCount());
        builder.append(TCellProperties.getRepertoireSize());
        builder.append(TCellProperties.getPositiveThreshold());
        builder.append(TCellProperties.getNegativeThreshold());
        builder.append(thymus.getPositivePassRate(), "%.4g");
        builder.append(thymus.getNegativePassRate(), "%.4g");
        builder.append(thymus.getNetSelectionRate(), "%.4g");

        double meanAffFP = meanAffinity(ThymicOutcome.FAILED_POSITIVE);
        double meanAffFN = meanAffinity(ThymicOutcome.FAILED_NEGATIVE);
        double meanAffEX = meanAffinity(ThymicOutcome.EXPORTED);

        builder.append(meanAffFP, "%.4g");
        builder.append(meanAffFN, "%.4g");
        builder.append(meanAffEX, "%.4g");

        appendAssayData(builder, pathogenAssay);
        appendAssayData(builder, sharedNeoAssay);
        appendAssayData(builder, medullaNeoAssay);

        builder.append((int) stopWatch.getTime() / 1000);
        return builder.toString();
    }

    private double meanAffinity(ThymicOutcome outcome) {
        return CollectionUtil.average(thymus.viewReceptors(outcome), x -> x.computeMeanAffinity());
    }

    private void appendAssayData(LineBuilder builder, TCRAssay assay) {
        builder.append(assay.getCoveredFrac(), "%.4g");
        builder.append(assay.getReactiveFrac(), "%.4g");
    }
}
