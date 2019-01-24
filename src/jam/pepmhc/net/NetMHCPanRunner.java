
package jam.pepmhc.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import jam.io.IOUtil;
import jam.lang.JamException;
import jam.peptide.Peptide;

final class NetMHCPanRunner {
    private final String allele;
    private final Collection<Peptide> peptides;

    private File peptideFile;
    private Map<Peptide, Double> affinityMap;

    private NetMHCPanRunner(String allele, Collection<Peptide> peptides) {
        this.allele = allele;
        this.peptides = peptides;
    }

    static Map<Peptide, Double> run(String allele, Collection<Peptide> peptides) {
        NetMHCPanRunner runner = new NetMHCPanRunner(allele, peptides);
        return runner.run();
    }

    private Map<Peptide, Double> run() {
        try {
            initPeptideFile();
            writePeptideFile();
            launchProcess();
        }
        catch (IOException ioex) {
            throw JamException.runtime(ioex);
        }
        finally {
            peptideFile.delete();
        }

        return affinityMap;
    }

    private void initPeptideFile() throws IOException {
        peptideFile = File.createTempFile("netMHCpan", ".pep");
    }
        
    private void writePeptideFile() throws IOException {
        PrintWriter writer = new PrintWriter(peptideFile);

        for (Peptide peptide : peptides)
            writer.println(peptide.formatString());

        writer.close();
    }

    private void launchProcess() throws IOException {
        ProcessBuilder builder =
            new ProcessBuilder(NetMHCPan.resolveExecutableName(),
                               "-a", formatAllele(),
                               "-p", "-BA", peptideFile.getAbsolutePath());

        Process process = builder.start();
        BufferedReader reader = IOUtil.openReader(process.getInputStream());

        affinityMap = NetMHCPanParser.parse(reader);
    }

    private String formatAllele() {
        return allele.replace("*", "");
    }
}
