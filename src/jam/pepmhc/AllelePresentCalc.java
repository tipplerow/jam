
package jam.pepmhc;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.fasta.FastaReader;
import jam.fasta.FastaRecord;
import jam.math.DoubleUtil;
import jam.peptide.Peptide;

public final class AllelePresentCalc {
    private final int peptideLength;
    private final String inputFile;
    private final String alleleCode;
    private final PredictionMethod predMethod;

    private PepMHCPredictor predictor;
    private FastaReader inputReader;
    private PrintWriter outputWriter;
    private Set<Peptide> pepFragments;
    private int binderCount;

    private AllelePresentCalc(String inputFile, PredictionMethod predMethod, String alleleCode, int peptideLength) {
        this.inputFile     = inputFile;
        this.predMethod    = predMethod;
        this.alleleCode    = alleleCode;
        this.peptideLength = peptideLength;
    }

    private static final int LOG_INTERVAL = 1000;

    /**
     * Prefix for the name of the output file, written to the working
     * directory.
     */
    public static final String OUTPUT_PREFIX = "presentation-rate";

    /**
     * Suffix for the name of the output file, written to the working
     * directory.
     */
    public static final String OUTPUT_SUFFIX = ".csv";

    /**
     * Threshold IC50 value to be considered "bound".
     */
    public static final double BINDING_THRESHOLD = 500.0;

    private void run() {
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
    }

    private void processPeptide(Peptide peptide) {
        for (Peptide fragment : peptide.nativeFragments(peptideLength))
            processFragment(fragment);
    }

    private void processFragment(Peptide fragment) {
        //
        // Do not double-count any duplicate fragments...
        //
        if (pepFragments.contains(fragment))
            return;

        double ic50 = predictor.predictIC50(fragment);

        if (ic50 < BINDING_THRESHOLD)
            ++binderCount;

        pepFragments.add(fragment);

        if (pepFragments.size() % LOG_INTERVAL == 0)
            JamLogger.info("Processed [%d] peptide fragments...", pepFragments.size());
    }

    private String outputFile() {
        return String.format("%s_%s_%s_%d%s",
                             OUTPUT_PREFIX,
                             predMethod,
                             alleleCode.replace('*', '-'),
                             peptideLength,
                             OUTPUT_SUFFIX);
    }

    private static void usage() {
        System.err.println("Usage: java jam.pepmhc.AllelePresentCalc "
                           + "FASTA_FILE PREDICTION_METHOD ALLELE_CODE PEPTIDE_LENGTH");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 4)
            usage();

        int peptideLength;
        String alleleCode, inputFile;
        PredictionMethod predMethod;

        inputFile     = args[0];
        predMethod    = PredictionMethod.valueOf(args[1].toUpperCase());
        alleleCode    = args[2];
        peptideLength = Integer.parseInt(args[3]);

        AllelePresentCalc calculator =
            new AllelePresentCalc(inputFile, predMethod, alleleCode, peptideLength);

        calculator.run();
    }
}