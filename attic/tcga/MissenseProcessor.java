
package jam.tcga;

import java.io.File;
import java.io.PrintWriter;

import jam.ensembl.EnsemblDb;
import jam.fasta.FastaRecord;
import jam.io.IOUtil;

/**
 * Generates the protein structures that result from missense
 * mutations.
 */
public final class MissenseProcessor {
    private final File ensemblDbFile;  
    private final File missenseInput;
    private final File proteinOutput;

    private EnsemblDb ensemblDb;
    private MissenseReader missenseReader;
    private PrintWriter fastaWriter;

    private MissenseProcessor(File ensemblDbFile,
                              File missenseInput,
                              File proteinOutput) {
        this.ensemblDbFile = ensemblDbFile;
        this.missenseInput = missenseInput;
        this.proteinOutput = proteinOutput;
    }

    /**
     * Runs the processor.
     *
     * @param ensemblDbFile path to the FASTA file containing the
     * Ensembl protein structure database (containing the native
     * protein structures).
     *
     * @param missenseInput path to the MAF file containing the
     * missense mutations (in standardized form required by the
     * {@code MisssenseReader}).
     *
     * @param proteinOutput path to the output file where the mutated
     * protein structures will be written in FASTA format.
     */
    public static void run(File ensemblDbFile,
                           File missenseInput,
                           File proteinOutput) {
        MissenseProcessor processor =
            new MissenseProcessor(ensemblDbFile,
                                  missenseInput,
                                  proteinOutput);
        processor.run();
    }

    private void run() {
        ensemblDb = EnsemblDb.load(ensemblDbFile);
        missenseReader = MissenseReader.open(missenseInput);
        fastaWriter = IOUtil.openWriter(proteinOutput);
    }

    private static void usage() {
        System.err.println("Usage: java jam.tcga.MissenseProcessor "
                           + "<ENSEMBL_DB_FILE> <MISSENSE_INPUT> <PROTEIN_OUTPUT>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 3)
            usage();

        File ensemblDbFile = new File(args[0]);
        File missenseInput = new File(args[1]);
        File proteinOutput = new File(args[2]);

        run(ensemblDbFile, missenseInput, proteinOutput);
    }
}
