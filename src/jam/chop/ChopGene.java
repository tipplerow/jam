
package jam.chop;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import jam.app.JamLogger;
import jam.chop.NetChop;
import jam.ensembl.EnsemblDb;
import jam.ensembl.EnsemblRecord;
import jam.ensembl.TranscriptBiotype;
import jam.io.IOUtil;
import jam.io.LineReader;
import jam.peptide.HugoSymbol;
import jam.peptide.Peptide;

/**
 * Predicts proteasomal cleavage for genes presented in an input file.
 * and writes the cleaved peptides to an output file.
 */
public final class ChopGene {
    private final LineReader reader;
    private final PrintWriter writer;

    private static final int[] PEPTIDE_LENGTHS = new int[] { 9, 10 };

    private ChopGene(LineReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void run(String geneFile, String peptideFile) {
        LineReader reader = LineReader.open(geneFile);
        PrintWriter writer = IOUtil.openWriter(peptideFile);

        ChopGene driver = new ChopGene(reader, writer);
        driver.run();
    }

    private void run() {
        writeHeader();
        processGenes();
    }

    private void writeHeader() {
        writer.println("Hugo_Symbol\tPeptide");
    }

    private void processGenes() {
        for (String gene : reader)
            processGene(gene);
    }

    private void processGene(String gene) {
        JamLogger.info("Chopping gene [%s]...", gene);
        writePeptides(gene, generatePeptides(gene));
    }

    private void writePeptides(String gene, Collection<String> peptides) {
        for (String peptide : peptides)
            writer.println(gene + "\t" + peptide);

        writer.flush();
    }

    private Collection<String> generatePeptides(String gene) {
        Collection<String> peptides = new TreeSet<String>();
        Collection<EnsemblRecord> records = EnsemblDb.global().get(HugoSymbol.instance(gene));

        for (EnsemblRecord record : records)
            peptides.addAll(generatePeptides(record));

        return peptides;
    }

    private Collection<String> generatePeptides(EnsemblRecord record) {
        if (!record.getTranscriptBiotype().equals(TranscriptBiotype.PROTEIN_CODING))
            return Collections.emptyList();

        Peptide peptide = record.getPeptide();

        if (!peptide.isNative())
            return Collections.emptyList();

        Collection<Peptide> chopped = NetChop.chop(peptide, PEPTIDE_LENGTHS);
        return Peptide.formatString(chopped);
    }

    private static void usage() {
        System.err.println("Usage: java [JVMOPTIONS] jam.chop.ChopGene <GENE_INPUT> <PEPTIDE_OUTPUT>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 2)
            usage();

        run(args[0], args[1]);
    }
}
