
package jam.nap;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;

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
    private LineReader patientReader;
    private PrintWriter alleleWriter;
    private PrintWriter genotypeWriter;
    private PrintWriter exceptionWriter;

    private NAPDriver(String... propFiles) {
        super(propFiles);
    }

    /**
     * Name of the system property that assigns the full path to the
     * file containing the patients to process.
     */ 
    public static final String PATIENT_COHORT_FILE_PROPERTY = "jam.nap.patientCohort";

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
        patientReader = LineReader.open(resolvePatientFile());

        alleleWriter    = IOUtil.openWriter(resolveAlleleFile());
        genotypeWriter  = IOUtil.openWriter(resolveGenotypeFile());
        exceptionWriter = IOUtil.openWriter(resolveExceptionFile());

        writeAlleleHeader();
        writeGenotypeHeader();
    }

    private static String resolvePatientFile() {
        return JamProperties.getRequired(PATIENT_COHORT_FILE_PROPERTY);
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

    private void writeAlleleHeader() {
    }

    private void writeGenotypeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("Patient_ID");
        builder.append("Tumor_Barcode");
        builder.append("Expressed_Gene_Count");
        builder.append("Mutated_Gene_Count");
        builder.append("Total_Mutation_Count");
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
        Genotype genotype = GenotypeDb.global().lookup(patient);

        if (genotype != null)
            processGenotype(patient, barcode, genotype);
        else
            logException(patient, barcode, "No genotype mapped to patient.");
    }

    private void processGenotype(PatientID patient, TumorBarcode barcode, Genotype genotype) {
        JamLogger.info("Processing [%s:%s]...", patient.getKey(), barcode.getKey());

        try {
            NAPGenotypeScore genotypeScore =
                NAPGenotypeScorer.compute(barcode, genotype);

            writeGenotypeScore(patient, barcode, genotype, genotypeScore);
        }
        catch (Exception ex) {
            logException(patient, barcode, ex.getMessage());
        }
    }

    private void logException(PatientID patient, TumorBarcode barcode, String message) {
        String fullMessage = String.format("[%s:%s] %s", patient.getKey(), barcode.getKey(), message);

        exceptionWriter.println(fullMessage);
        exceptionWriter.flush();

        JamLogger.error(fullMessage);
    }

    private void writeGenotypeScore(PatientID patient, TumorBarcode barcode, Genotype genotype, NAPGenotypeScore score) {
        LineBuilder builder = LineBuilder.csv();

        builder.append(patient.getKey());
        builder.append(barcode.getKey());
        builder.append(score.getExpressedGeneCount());
        builder.append(score.getMutatedGeneCount());
        builder.append(score.getTotalMutationCount());
        builder.append(score.getBindingThresholdScore().doubleValue());
        builder.append(score.getConcentrationRatioScore().doubleValue());
        builder.append(score.getStabilityRatioScore().doubleValue());

        genotypeWriter.println(builder.toString());
        genotypeWriter.flush();
    }

    public static void main(String[] propFiles) {
        NAPDriver driver = new NAPDriver(propFiles);
        driver.run();
    }
}
