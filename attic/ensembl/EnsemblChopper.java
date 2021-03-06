
package jam.ensembl;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import jam.app.JamLogger;
import jam.io.IOUtil;
import jam.io.LineReader;
import jam.peptide.Peptide;

/**
 * Reads an input file containing Ensembl gene identifiers and writes
 * an output file containing all unique {@code n}-mer peptides derived
 * from the corresponding proteins.
 */
public final class EnsemblChopper {
    private final int peptideLength;
    private final String inputFile;
    private final String outputFile;
    private final Set<String> peptideFragments;
    private int peptidesProcessed;

    private EnsemblChopper(String inputFile, String outputFile, int peptideLength) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.peptideLength = peptideLength;
        this.peptideFragments = new TreeSet<String>();
        this.peptidesProcessed = 0;
    }

    private static final int LOG_INTERVAL = 1000;

    private void run() {
        generateFragments();
        writeFragments();
    }

    private void generateFragments() {
        LineReader reader = LineReader.open(inputFile);

        for (String gene : reader)
            processGene(EnsemblGene.instance(gene));

        reader.close();
    }

    private void processGene(EnsemblGene gene) {
        Collection<EnsemblRecord> records = EnsemblDb.reference().get(gene);

        for (EnsemblRecord record : records)
            processPeptide(record.getPeptide());
    }

    private void processPeptide(Peptide peptide) {
        for (Peptide fragment : peptide.nativeFragments(peptideLength))
            peptideFragments.add(fragment.formatString());

        ++peptidesProcessed;

        if (peptidesProcessed % LOG_INTERVAL == 0)
            JamLogger.info("Processed [%d] peptides...", peptidesProcessed);
    }

    private void writeFragments() {
        JamLogger.info("Writing fragments...");
        PrintWriter writer = IOUtil.openWriter(outputFile);

        for (String fragment : peptideFragments)
            writer.println(fragment);

        writer.close();
    }

    private static void usage() {
        System.err.println("Usage: java jam.ensembl.EnsemblChopper INPUT_FILE OUTPUT_FILE PEPTIDE_LENGTH");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 3)
            usage();

        String inputFile = args[0];
        String outputFile = args[1];
        int    peptideLength = Integer.parseInt(args[2]);

        EnsemblChopper chopper =
            new EnsemblChopper(inputFile, outputFile, peptideLength);

        chopper.run();
    }
}
