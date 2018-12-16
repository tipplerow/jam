
package jam.neo;

import java.util.Collection;
import java.io.PrintWriter;

import org.apache.commons.lang3.time.StopWatch;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.lang.ObjectFactory;
import jam.math.DoubleRange;
import jam.math.DoubleUtil;
import jam.math.IntRange;
import jam.math.StatSummary;
import jam.mhc.Genotype;
import jam.mhc.MHC;
import jam.peptide.Peptide;
import jam.peptide.Peptidome;
import jam.report.LineBuilder;
import jam.tcell.Repertoire;
import jam.tcell.TCR;
import jam.tcell.TCRAssay;
import jam.tcell.TCellProperties;
import jam.thymus.ThymicOutcome;
import jam.thymus.Thymus;
import jam.thymus.ThymusProperties;
import jam.util.CollectionUtil;

public abstract class NeoAntigenSim extends JamApp {
    private final StopWatch stopWatch;

    private final int sharedPeptideCount;
    private final int cortexPrivateCount;
    private final int medullaPrivateCount;

    private final int pathogenPeptideCount;
    private final int sharedNeoPeptideCount;
    private final int cortexNeoPeptideCount;
    private final int medullaNeoPeptideCount;

    private final int repertoireSize;

    private Peptidome allSharedSelfPeptides;
    private Peptidome allCortexSelfPeptides;
    private Peptidome allMedullaSelfPeptides;

    private Peptidome allPathogenPeptides;
    private Peptidome allSharedNeoPeptides;
    private Peptidome allCortexNeoPeptides;
    private Peptidome allMedullaNeoPeptides;

    private Genotype genotype;

    private Peptidome presentedSharedSelfPeptides;
    private Peptidome presentedCortexSelfPeptides;
    private Peptidome presentedMedullaSelfPeptides;

    private Peptidome presentedPathogenPeptides;
    private Peptidome presentedSharedNeoPeptides;
    private Peptidome presentedCortexNeoPeptides;
    private Peptidome presentedMedullaNeoPeptides;

    private double sharedPeptidePresentFrac;
    private double cortexPrivatePresentFrac;
    private double medullaPrivatePresentFrac;

    private double pathogenPeptidePresentFrac;
    private double sharedNeoPeptidePresentFrac;
    private double cortexNeoPeptidePresentFrac;
    private double medullaNeoPeptidePresentFrac;

    private StatSummary presentationSummary;

    private Thymus thymus;
    private Repertoire repertoire;

    private ImmuneStateReport pathogenISR;
    private ImmuneStateReport sharedNeoISR;
    private ImmuneStateReport medullaNeoISR;

    private TCRAssay pathogenAssay;
    private TCRAssay sharedNeoAssay;
    private TCRAssay cortexNeoAssay;
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
     * cortex-only neopeptides to test for immunogenicity.
     */
    public static final String CORTEX_NEOPEPTIDE_COUNT_PROPERTY = "jam.neo.cortexNeoPeptideCount";

    /**
     * Name of the system property that specifies the number of
     * medulla-only neopeptides to test for immunogenicity.
     */
    public static final String MEDULLA_NEOPEPTIDE_COUNT_PROPERTY = "jam.neo.medullaNeoPeptideCount";

    /**
     * Base name for the summary file.
     */
    public static final String SUMMARY_NAME = "neoantigen-sim-summary.csv";

    /**
     * Creates a new assay application.
     *
     * @param propertyFiles files containing the system properties
     * that define the simulation parameters.
     */
    protected NeoAntigenSim(String[] propertyFiles) {
        super(propertyFiles);
        this.stopWatch = new StopWatch();

        this.sharedPeptideCount  = ThymusProperties.getSharedPeptideCount();
        this.cortexPrivateCount  = ThymusProperties.getCortexPrivateCount();
        this.medullaPrivateCount = ThymusProperties.getMedullaPrivateCount();

        this.pathogenPeptideCount =
            JamProperties.getRequiredInt(PATHOGEN_PEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);

        this.sharedNeoPeptideCount =
            JamProperties.getRequiredInt(SHARED_NEOPEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);

        this.cortexNeoPeptideCount =
            JamProperties.getRequiredInt(CORTEX_NEOPEPTIDE_COUNT_PROPERTY, IntRange.NON_NEGATIVE);

        this.medullaNeoPeptideCount =
            JamProperties.getRequiredInt(MEDULLA_NEOPEPTIDE_COUNT_PROPERTY, IntRange.POSITIVE);

        this.repertoireSize = TCellProperties.getRepertoireSize();
    }

    /**
     * Returns the factory that creates MHC alleles.
     *
     * @return the factory that creates MHC alleles.
     */
    protected abstract ObjectFactory<? extends MHC> getMHCFactory();

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

        genotype = Genotype.create(getMHCFactory());
        generatePeptidome();
        restrictPeptidome();

        selectRepertoire();

        //runAssays();
        runImmuneStateReports();
        /*
        JamLogger.info("Running assays...");
        pathogenAssay = TCRAssay.run(repertoire, pathogenPeptides);
        sharedNeoAssay = TCRAssay.run(repertoire, sharedNeoPeptides);
        medullaNeoAssay = TCRAssay.run(repertoire, medullaNeoPeptides);
        */

        JamLogger.info("Writing summary...");
        writeSummary();
        autoClose();

        JamLogger.info("DONE!");
    }

    private void generatePeptidome() {
        JamLogger.info("Generating self-peptides...");
        allSharedSelfPeptides  = Peptidome.create(getPeptideFactory(), sharedPeptideCount); 
        allCortexSelfPeptides  = Peptidome.create(getPeptideFactory(), cortexPrivateCount); 
        allMedullaSelfPeptides = Peptidome.create(getPeptideFactory(), medullaPrivateCount);

        JamLogger.info("Generating neo-peptides...");
        allSharedNeoPeptides  = allSharedSelfPeptides.mutate(sharedNeoPeptideCount);
        allCortexNeoPeptides  = allCortexSelfPeptides.mutate(cortexNeoPeptideCount);
        allMedullaNeoPeptides = allMedullaSelfPeptides.mutate(medullaNeoPeptideCount);

        JamLogger.info("Generating pathogen-derived peptides...");
        allPathogenPeptides = Peptidome.create(getPeptideFactory(), pathogenPeptideCount);
    }

    private void restrictPeptidome() {
        JamLogger.info("Restricting self-peptides...");
        presentedSharedSelfPeptides  = genotype.restrict(allSharedSelfPeptides);
        presentedCortexSelfPeptides  = genotype.restrict(allCortexSelfPeptides);
        presentedMedullaSelfPeptides = genotype.restrict(allMedullaSelfPeptides);

        JamLogger.info("Restricting neo-peptides...");
        presentedSharedNeoPeptides  = genotype.restrict(allSharedNeoPeptides);
        presentedCortexNeoPeptides  = genotype.restrict(allCortexNeoPeptides);
        presentedMedullaNeoPeptides = genotype.restrict(allMedullaNeoPeptides);

        JamLogger.info("Restricting pathogen-derived peptides...");
        presentedPathogenPeptides = genotype.restrict(allPathogenPeptides);

        JamLogger.info("Computing presentation statistics...");
        sharedPeptidePresentFrac  = DoubleUtil.ratio(presentedSharedSelfPeptides.size(),  sharedPeptideCount);
        cortexPrivatePresentFrac  = DoubleUtil.ratio(presentedCortexSelfPeptides.size(),  cortexPrivateCount);
        medullaPrivatePresentFrac = DoubleUtil.ratio(presentedMedullaSelfPeptides.size(), medullaPrivateCount);
        
        sharedNeoPeptidePresentFrac  = DoubleUtil.ratio(presentedSharedNeoPeptides.size(),  sharedNeoPeptideCount);  
        cortexNeoPeptidePresentFrac  = DoubleUtil.ratio(presentedCortexNeoPeptides.size(),  cortexNeoPeptideCount);  
        medullaNeoPeptidePresentFrac = DoubleUtil.ratio(presentedMedullaNeoPeptides.size(), medullaNeoPeptideCount); 
        
        pathogenPeptidePresentFrac = DoubleUtil.ratio(presentedPathogenPeptides.size(), pathogenPeptideCount);

        presentationSummary =
            StatSummary.compute(sharedPeptidePresentFrac,
                                cortexPrivatePresentFrac,
                                medullaPrivatePresentFrac,        
                                sharedNeoPeptidePresentFrac,
                                cortexNeoPeptidePresentFrac,
                                medullaNeoPeptidePresentFrac,
                                pathogenPeptidePresentFrac);
    }

    private void selectRepertoire() {
        JamLogger.info("Simulating T cell selection...");

        DoubleRange positivePassRange = ThymusProperties.getPositivePassRange();
        DoubleRange negativePassRange = ThymusProperties.getNegativePassRange();
        DoubleRange netSelectionRange = ThymusProperties.getNetSelectionRange();

        thymus =
            Thymus.select(repertoireSize,
                          positivePassRange,
                          negativePassRange,
                          netSelectionRange,
                          getTCRFactory(),
                          presentedSharedSelfPeptides,
                          presentedCortexSelfPeptides,
                          presentedMedullaSelfPeptides);

        repertoire = thymus.viewRepertoire();
    }

    private void runImmuneStateReports() {
        pathogenISR    = ImmuneStateReport.run(genotype, repertoire, allPathogenPeptides);
        sharedNeoISR   = ImmuneStateReport.run(genotype, repertoire, allSharedNeoPeptides);
        medullaNeoISR  = ImmuneStateReport.run(genotype, repertoire, allMedullaNeoPeptides);
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
        builder.append("sharedNeoPeptideCount");
        builder.append("cortexNeoPeptideCount");
        builder.append("medullaNeoPeptideCount");
        builder.append("pathogenPeptideCount");

        builder.append("sharedPeptidePresentFrac");
        builder.append("cortexPrivatePresentFrac");
        builder.append("medullaPrivatePresentFrac");
        builder.append("sharedNeoPeptidePresentFrac");
        builder.append("cortexNeoPeptidePresentFrac");
        builder.append("medullaNeoPeptidePresentFrac");
        builder.append("pathogenPeptidePresentFrac");

        builder.append("presentationRate");
        builder.append("presentationStdErr");

        builder.append("repertoireSize");
        builder.append("positiveThreshold");
        builder.append("negativeThreshold");
        builder.append("positivePassRate");
        builder.append("negativePassRate");
        builder.append("netSelectionRate");

        builder.append("meanAffinity.FP");
        builder.append("meanAffinity.FN");
        builder.append("meanAffinity.EX");

        appendImmuneStateReportHeader(builder, "pathogen");
        appendImmuneStateReportHeader(builder, "sharedNeo");
        appendImmuneStateReportHeader(builder, "medullaNeo");
        /*
        appendAssayHeader(builder, "pathogen");
        appendAssayHeader(builder, "sharedNeo");
        appendAssayHeader(builder, "medullaNeo");
        */
        builder.append("elapsedSec");

        return builder.toString();
    }

    private void appendAssayHeader(LineBuilder builder, String prefix) {
        builder.append(prefix + ".coveredFrac");
        builder.append(prefix + ".reactiveFrac");
    }

    private void appendImmuneStateReportHeader(LineBuilder builder, String prefix) {
        for (ImmuneState state : ImmuneState.values())
            builder.append(prefix + "." + state.getCode());
    }

    private String summaryDataLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(getTrialIndex());

        builder.append(sharedPeptideCount);
        builder.append(cortexPrivateCount);
        builder.append(medullaPrivateCount);
        builder.append(sharedNeoPeptideCount);
        builder.append(cortexNeoPeptideCount);
        builder.append(medullaNeoPeptideCount);
        builder.append(pathogenPeptideCount);

        builder.append(sharedPeptidePresentFrac);
        builder.append(cortexPrivatePresentFrac);
        builder.append(medullaPrivatePresentFrac);
        builder.append(sharedNeoPeptidePresentFrac);
        builder.append(cortexNeoPeptidePresentFrac);
        builder.append(medullaNeoPeptidePresentFrac);
        builder.append(pathogenPeptidePresentFrac);

        builder.append(presentationSummary.getMean());
        builder.append(presentationSummary.getError());

        builder.append(repertoireSize);
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

        appendImmuneStateReportData(builder, pathogenISR);
        appendImmuneStateReportData(builder, sharedNeoISR);
        appendImmuneStateReportData(builder, medullaNeoISR);

        /*
        appendAssayData(builder, pathogenAssay);
        appendAssayData(builder, sharedNeoAssay);
        appendAssayData(builder, medullaNeoAssay);
        */
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

    private void appendImmuneStateReportData(LineBuilder builder, ImmuneStateReport report) {
        for (ImmuneState state : ImmuneState.values())
            builder.append(report.getFraction(state), "%.6f");
    }
}
