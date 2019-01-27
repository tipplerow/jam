
package jam.pepmhc.net;

import java.io.File;
import java.util.Collection;
import java.util.List;

import jam.pepmhc.BindingRecord;
import jam.peptide.Peptide;

/**
 * Executes {@code netMHCpan} command-line processes.
 */
public final class NetMHCPanRunner extends NetRunner {
    private NetMHCPanRunner(String allele, Collection<Peptide> peptides) {
        super(allele, peptides);
    }

    /**
     * Executes a {@code netMHCpan} command-line process.
     *
     * @param allele the string code for the binding MHC allele.
     *
     * @param peptides the peptide targets.
     *
     * @return the binding records generated by the command-line
     * prediction process.
     */
    public static List<BindingRecord> run(String allele, Collection<Peptide> peptides) {
        NetMHCPanRunner runner = new NetMHCPanRunner(allele, peptides);
        return runner.run();
    }

    @Override protected List<String>formatCommand(String allele, File peptideFile) {
        return List.of(NetMHCPan.resolveExecutableName(), 
                       "-a", formatAllele(allele),
                       "-BA", "-p", peptideFile.getAbsolutePath());
    }

    private static String formatAllele(String allele) {
        return allele.replace("*", "");
    }
}
