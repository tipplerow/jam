
package jam.pepmhc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import jam.app.JamApp;
import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.IOUtil;
import jam.io.LineReader;
import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.math.DoubleUtil;
import jam.math.IntUtil;
import jam.peptide.Peptide;
import jam.report.LineBuilder;
import jam.util.RegexUtil;

public final class GenotypePresentCalc extends JamApp {
    private final int peptideLength;

    private final String genotypeFileName;
    private final String peptidomeFileName;
    private final String reportFileName;

    private final PredictionMethod predictionMethod;

    private final int[] affinityThreshold;

    private final Set<Peptide> peptideFragments;

    private String patientKeyName;
    private LineReader genotypeReader;
    private PrintWriter reportWriter;

    private GenotypePresentCalc(String... propFiles) {
        super(propFiles);

        this.genotypeFileName  = resolveGenotypeFileName();
        this.peptidomeFileName = resolvePeptidomeFileName();
        this.reportFileName    = resolveReportFileName();   
        this.predictionMethod  = resolvePredictionMethod();
        this.peptideLength     = resolvePeptideLength();
        this.affinityThreshold = resolveAffinityThreshold();
        this.peptideFragments  = new HashSet<Peptide>();
    }

    /**
     * Column delimiter for the genotype input file.
     */
    public static final Pattern GENOTYPE_COLUMN_DELIM = RegexUtil.COMMA;

    /**
     * Allele delimiter for the genotype input file.
     */
    public static final Pattern GENOTYPE_ALLELE_DELIM = RegexUtil.MULTI_WHITE_SPACE;

    /**
     * Name of the system property that specifies the full path name
     * of the genotype file.
     *
     * The genotype file must contain two comma-separated columns. The
     * first column must contain a key identifying the patient to whom
     * the genotype belongs; the second column must list the alleles
     * contained in the genotype separated by white space.
     */
    public static final String GENOTYPE_FILE_PROPERTY = "jam.pepmhc.genotypeFile";

    /**
     * Name of the system property that specifies the full path name
     * of a FASTA file containing the peptidome from which the peptide
     * binding targets will be derived.
     */
    public static final String PEPTIDOME_FILE_PROPERTY = "jam.pepmhc.peptidomeFile";

    /**
     * Name of the system property that specifies the base name of the
     * report file (written to the report directory.)
     */
    public static final String REPORT_FILE_PROPERTY = "jam.pepmhc.reportFile";

    /**
     * Name of the system property that specifies the peptide-MHC
     * affinity prediction method.
     */
    public static final String PREDICTION_METHOD_PROPERTY = "jam.pepmhc.predictionMethod";

    /**
     * Name of the system property that specifies the length of the
     * peptides for the binding prediction.
     */
    public static final String PEPTIDE_LENGTH_PROPERTY = "jam.pepmhc.peptideLength";

    /**
     * Name of the system property that specifies the affinity
     * thresholds to report.  Multiple thresholds may be given,
     * separated by commas.
     */
    public static final String AFFINITY_THRESHOLD_PROPERTY = "jam.pepmhc.affinityThreshold";

    private static String resolveGenotypeFileName() {
        return JamProperties.getRequired(GENOTYPE_FILE_PROPERTY);
    }

    private static String resolvePeptidomeFileName() {
        return JamProperties.getRequired(PEPTIDOME_FILE_PROPERTY);
    }

    private static String resolveReportFileName() {   
        return JamProperties.getRequired(REPORT_FILE_PROPERTY);
    }

    private static PredictionMethod resolvePredictionMethod() {
        return JamProperties.getRequiredEnum(PREDICTION_METHOD_PROPERTY, PredictionMethod.class);
    }

    private static int resolvePeptideLength() {
        return JamProperties.getRequiredInt(PEPTIDE_LENGTH_PROPERTY);
    }

    private static int[] resolveAffinityThreshold() {
        return IntUtil.parseIntArray(JamProperties.getRequired(AFFINITY_THRESHOLD_PROPERTY), RegexUtil.COMMA);
    }

    private void run() {
        generateFragments();
        processGenotypes();
    }
        
        /*
        predictor    = PepMHCPredictor.instance(predMethod, alleleCode, peptideLength);
        inputReader  = FastaReader.open(inputFile);
        pepFragments = new HashSet<Peptide>();
        binderCount  = 0;

        for (FastaRecord record : inputReader)
            processPeptide(record.getPeptide());

        outputWriter = IOUtil.openWriter(outputFile());
        outputWriter.println("predMethod,alleleCode,peptideLength,fragmentCount,binderFrac");
        outputWriter.println(String.format("%s,%s,%d,%d,%8.6f",
                                           predMethod,
                                           alleleCode,
                                           peptideLength,
                                           pepFragments.size(),
                                           DoubleUtil.ratio(binderCount, pepFragments.size())));

        inputReader.close();
        outputWriter.close();
        */
  
    private void generateFragments() {
        JamLogger.info("Reading peptidome...");
        List<FastaRecord> records = FastaReader.read(peptidomeFileName);

        for (FastaRecord record : records) {
            Peptide protein = record.getPeptide();

            for (Peptide fragment : protein.nativeFragments(peptideLength))
                peptideFragments.add(fragment);
        }

        JamLogger.info("Generated [%d] unique peptides...", peptideFragments.size());
    }

    private void processGenotypes() {
        genotypeReader = LineReader.open(genotypeFileName);
        reportWriter   = IOUtil.openWriter(reportFileName);

        try {
            readGenotypeHeader();
            writeReportHeader();

            for (String genotypeLine : genotypeReader)
                processGenotype(genotypeLine);
        }
        finally {
            IOUtil.close(genotypeReader);
            IOUtil.close(reportWriter);
        }
    }

    private void readGenotypeHeader() {
        String   line    = genotypeReader.next();
        String[] columns = RegexUtil.split(GENOTYPE_COLUMN_DELIM, line, 2);

        patientKeyName = columns[0];
    }

    private void writeReportHeader() {
        LineBuilder builder = LineBuilder.csv();
        builder.append(patientKeyName);

        for (int threshold : affinityThreshold)
            builder.append(String.format("rate.%d", threshold));

        reportWriter.println(builder);
    }

    private void processGenotype(String line) {
        /*
        String patientKey = parsePatientKey(line);
        Set<String> alleleSet = parseAlleleSet(line);
        List<PepMHCPredictor> predictors;

        try {
            predictors = PepMHCPredictor.instances(predictionMethod, alleleSet, peptideLength);
        }
        catch (Exception ex) {
            //
            // Many of the prediction models are fit only for common
            // alleles. This genotype contains a rare allele with no
            // available predictor, so skip it...
            //
            JamLogger.info("No predictor for patient [%s], skipping...", patientKey);
            return;
        }

        JamLogger.info("Processing patient [%s]...", patientKey);
        double[] presentationRates = computePresentationRates(predictors);

        writeReportData(patientKey, presentationRates);
        */
    }

    private String parsePatientKey(String line) {
        String[] columns = RegexUtil.split(GENOTYPE_COLUMN_DELIM, line, 2);
        String   genoKey = columns[0];

        return genoKey;
    }

    private Set<String> parseAlleleSet(String line) {
        String[]    columns   = RegexUtil.split(GENOTYPE_COLUMN_DELIM, line, 2);
        String[]    alleleVec = RegexUtil.split(GENOTYPE_ALLELE_DELIM, columns[1]);
        Set<String> alleleSet = new TreeSet<String>(List.of(alleleVec));

        return alleleSet;
    }

    private double[] computePresentationRates(List<PepMHCPredictor> predictors) {
        int[] binderCounts = new int[affinityThreshold.length];

        for (Peptide fragment : peptideFragments) {
            throw new UnsupportedOperationException();
            /*
            double ic50 = PepMHCPredictor.minimumIC50(predictors, fragment);

            for (int k = 0; k < affinityThreshold.length; ++k)
                if (ic50 <= affinityThreshold[k])
                    ++binderCounts[k];
            */
        }

        double[] presentRates = new double[affinityThreshold.length];

        for (int k = 0; k < affinityThreshold.length; ++k)
            presentRates[k] = DoubleUtil.ratio(binderCounts[k], peptideFragments.size());

        return presentRates;
    }

    private void writeReportData(String patientKey, double[] presentationRates) {
        LineBuilder builder = LineBuilder.csv();
        builder.append(patientKey);

        for (double rate : presentationRates)
            builder.append(rate, "%.8f");

        reportWriter.println(builder);
        reportWriter.flush();
    }

    public static void main(String[] propFiles) {
        GenotypePresentCalc calculator = new GenotypePresentCalc(propFiles);
        calculator.run();
    }
}
