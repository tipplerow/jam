
package jam.pepmhc.net;

import java.io.BufferedReader;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jam.app.JamProperties;
import jam.pepmhc.PepMHCPredictor;
import jam.pepmhc.PredictionMethod;
import jam.peptide.Peptide;

public final class NetMHCPan implements PepMHCPredictor {
    private NetMHCPan() {}

    /**
     * The single instance.
     */
    public static final NetMHCPan INSTANCE = new NetMHCPan();

    /**
     * Name of the system property that defines the full path to the
     * {@code netMHCpan} executable file.
     */
    public static final String EXECUTABLE_PATH_PROPERTY = "jam.pepmhc.net.executablePath";

    /**
     * Default value for the path to the {@code netMHCpan} executable
     * file (assumed to be in the working directory).
     */
    public static final String EXECUTABLE_PATH_DEFAULT = "netMHCpan";

    /**
     * Determines whether the {@code netMHCpan} executable is installed
     * at the location specified by the {@code EXECUTABLE_PATH_PROPERTY}.
     *
     * @return {@code true} iff an executable file is installed at the
     * location specified by the {@code EXECUTABLE_PATH_PROPERTY}.
     */
    public static boolean isInstalled() {
        return resolveExecutableFile().canExecute();
    }

    /**
     * Parses the output of a {@code netMHCpan} process.
     *
     * @param reader a stream to read the process output.
     *
     * @return a mapping from peptides in the output stream to their
     * predicted binding affinity.
     *
     * @throws RuntimeException if any errors occur.
     */
    public static Map<Peptide, Double> parseOutput(BufferedReader reader) {
        return NetMHCPanParser.parse(reader);
    }

    /**
     * Resolves the full path to the {@code netMHCpan} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY},
     * or {@code netMHCpan} in the working directory if the system property
     * is not set.
     */
    public static File resolveExecutableFile() {
        return new File(resolveExecutableName());
    }

    /**
     * Resolves the full path to the {@code netMHCpan} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY},
     * or {@code netMHCpan} in the working directory if the system property
     * is not set.
     */
    public static String resolveExecutableName() {
        return JamProperties.getOptional(EXECUTABLE_PATH_PROPERTY, EXECUTABLE_PATH_DEFAULT);
    }

    @Override public PredictionMethod getMethod() {
        return PredictionMethod.NETMHCPAN;
    }

    @Override public double predictIC50(String allele, Peptide peptide) {
        return predictIC50(allele, List.of(peptide)).get(peptide);
    }

    @Override public Map<Peptide, Double> predictIC50(String allele, Collection<Peptide> peptides) {
        return NetMHCPanRunner.run(allele, peptides);
    }
}
