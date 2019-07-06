
package jam.nap;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.hla.Allele;
import jam.hla.Genotype;
import jam.hla.GenotypeDb;
import jam.io.IOUtil;
import jam.io.LineReader;
import jam.report.LineBuilder;
import jam.tcga.PatientID;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPatientTable;

/**
 * Computes neoantigen presentation (NAP) scores for a tumor genomic
 * dataset.
 */
public final class NAPDriver extends JamApp {
    private final String patientFile;
    private final boolean stopOnError;

    private LineReader patientReader;
    private PrintWriter alleleWriter;
    private PrintWriter genotypeWriter;
    private PrintWriter exceptionWriter;

    private NAPDriver(String patientFile, String[] propertyFiles) {
        super(propertyFiles);

        this.patientFile = patientFile;
        this.stopOnError = resolveStopOnError();
    }

    /**
     * Name of the system property that assigns the full path to the
     * allele score report.
     */ 
    public static final String ALLELE_REPORT_FILE_PROPERTY = "jam.nap.alleleReportFile";

    /**
     * Name of the system property that assigns the full path to the
     * genotype score report.
     */ 
    public static final String GENOTYPE_REPORT_FILE_PROPERTY = "jam.nap.genotypeReportFile";

    /**
     * Name of the system property that assigns the full path to the
     * exception report.
     */ 
    public static final String EXCEPTION_REPORT_FILE_PROPERTY = "jam.nap.exceptionReportFile";

    /**
     * Name of the system property that specifies whether to throw an
     * exception and exit the application if an error occurs while
     * processing any tumor.  The default is {@code true}.
     */
    public static final String STOP_ON_ERROR_PROPERTY = "jam.nap.stopOnError";

    private void run() {
        openIO();

        try {
            for (String patient : patientReader)
                processPatient(PatientID.instance(patient));

            JamLogger.info("DONE!");
        }
        finally {
            closeIO();
        }
    }

    private void openIO() {
        patientReader = LineReader.open(patientFile);

        alleleWriter    = IOUtil.openWriter(resolveAlleleFile());
        genotypeWriter  = IOUtil.openWriter(resolveGenotypeFile());
        exceptionWriter = IOUtil.openWriter(resolveExceptionFile());

        writeAlleleHeader();
        writeGenotypeHeader();
    }

    private static String resolveAlleleFile() {
        return JamProperties.getRequired(ALLELE_REPORT_FILE_PROPERTY);
    }

    private static String resolveGenotypeFile() {
        return JamProperties.getRequired(GENOTYPE_REPORT_FILE_PROPERTY);
    }

    private static String resolveExceptionFile() {
        return JamProperties.getRequired(EXCEPTION_REPORT_FILE_PROPERTY);
    }

    private static boolean resolveStopOnError() {
        return JamProperties.getOptionalBoolean(STOP_ON_ERROR_PROPERTY, true);
    }

    private void writeAlleleHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("Patient_ID");
        builder.append("Tumor_Barcode");
        builder.append("Allele");
        builder.append("Neo_Bound_Count");
        builder.append("Self_Bound_Count");
        builder.append("Neo_Total_Conc");
        builder.append("Self_Total_Conc");
        builder.append("Neo_WtMean_Stab");
        builder.append("Self_WtMean_Stab");
        builder.append("Binding_Threshold_Score");
        builder.append("Concentration_Ratio_Score");
        builder.append("Stability_Ratio_Score");

        alleleWriter.println(builder.toString());
        alleleWriter.flush();
    }

    private void writeGenotypeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("Patient_ID");
        builder.append("Tumor_Barcode");
        builder.append("Total_Mutation_Count");
        builder.append("Expressed_Gene_Count");
        builder.append("Mutated_Gene_Count");
        builder.append("Expressed_Mutation_Count");
        builder.append("Binding_Threshold_Score");
        builder.append("Concentration_Ratio_Score");
        builder.append("Stability_Ratio_Score");

        genotypeWriter.println(builder.toString());
        genotypeWriter.flush();
    }

    private void closeIO() {
        IOUtil.close(patientReader);
        IOUtil.close(alleleWriter);
        IOUtil.close(genotypeWriter);
        IOUtil.close(exceptionWriter);
    }

    private void processPatient(PatientID patient) {
        Collection<TumorBarcode> barcodes = TumorPatientTable.global().lookup(patient);

        for (TumorBarcode barcode : barcodes)
            processBarcode(patient, barcode);
    }

    private void processBarcode(PatientID patient, TumorBarcode barcode) {
        Genotype genotype = GenotypeDb.global().lookup(barcode);

        if (genotype != null)
            processGenotype(patient, barcode, genotype);
        else
            logException(patient, barcode, "No genotype mapped to tumor.");
    }

    private void processGenotype(PatientID patient, TumorBarcode barcode, Genotype genotype) {
        JamLogger.info("Processing sample [%s:%s]...", patient.getKey(), barcode.getKey());

        try {
            NAPGenotypeScore genotypeScore =
                NAPGenotypeScorer.compute(barcode, genotype);

            writeAlleleScores(patient, barcode, genotypeScore);
            writeGenotypeScore(patient, barcode, genotypeScore);
        }
        catch (Exception ex) {
            logException(patient, barcode, ex.getMessage());

            if (stopOnError)
                throw ex;
        }
    }

    private void logException(PatientID patient, TumorBarcode barcode, String message) {
        String fullMessage = String.format("[%s:%s] %s", patient.getKey(), barcode.getKey(), message);

        exceptionWriter.println(fullMessage);
        exceptionWriter.flush();

        JamLogger.error(fullMessage);
    }

    private void writeAlleleScores(PatientID patient, TumorBarcode barcode, NAPGenotypeScore genotypeScore) {
        Map<Allele, NAPAlleleScore> alleleScores = genotypeScore.viewAlleleScores();

        for (Map.Entry<Allele, NAPAlleleScore> entry : alleleScores.entrySet())
            writeAlleleScore(patient, barcode, entry.getKey(), entry.getValue());
    }

    private void writeAlleleScore(PatientID patient, TumorBarcode barcode, Allele allele, NAPAlleleScore score) {
        LineBuilder builder = LineBuilder.csv();

        builder.append(patient.getKey());
        builder.append(barcode.getKey());
        builder.append(allele.shortKey());
        builder.append(score.getNeoBoundCount());
        builder.append(score.getSelfBoundCount());
        builder.append(score.getNeoTotalConc());
        builder.append(score.getSelfTotalConc());
        builder.append(score.getNeoWtMeanStab());
        builder.append(score.getSelfWtMeanStab());
        builder.append(score.getBindingThresholdScore().doubleValue());
        builder.append(score.getConcentrationRatioScore().doubleValue());
        builder.append(score.getStabilityRatioScore().doubleValue());

        alleleWriter.println(builder.toString());
        alleleWriter.flush();
    }

    private void writeGenotypeScore(PatientID patient, TumorBarcode barcode, NAPGenotypeScore score) {
        LineBuilder builder = LineBuilder.csv();

        builder.append(patient.getKey());
        builder.append(barcode.getKey());
        builder.append(score.getTotalMutationCount());
        builder.append(score.getExpressedGeneCount());
        builder.append(score.getMutatedGeneCount());
        builder.append(score.getExpressedMutationCount());
        builder.append(score.getBindingThresholdScore().doubleValue());
        builder.append(score.getConcentrationRatioScore().doubleValue());
        builder.append(score.getStabilityRatioScore().doubleValue());

        genotypeWriter.println(builder.toString());
        genotypeWriter.flush();
    }

    private static void usage() {
        System.err.println("Usage: java [JVM_OPTIONS] jam.nap.NAPDriver PATIENT_FILE PROPERTY_FILE1 [PROPERTY_FILE2 ...]");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length < 2)
            usage();

        String   patientFile   = args[0];
        String[] propertyFiles = Arrays.copyOfRange(args, 1, args.length);

        NAPDriver driver = new NAPDriver(patientFile, propertyFiles);
        driver.run();
    }
}
