
package jam.pepmhc.net;

import java.io.File;
import java.util.Collection;
import java.util.List;

import jam.app.JamProperties;
import jam.pepmhc.BindingRecord;
import jam.pepmhc.PepMHCPredictor;
import jam.pepmhc.PredictionMethod;
import jam.peptide.Peptide;

public final class NetMHC implements PepMHCPredictor {
    private NetMHC() {}

    /**
     * The single instance.
     */
    public static final NetMHC INSTANCE = new NetMHC();

    /**
     * Name of the system property that defines the full path to the
     * {@code netMHC} executable file.
     */
    public static final String EXECUTABLE_PATH_PROPERTY = "jam.pepmhc.net.netMHC";

    /**
     * Default value for the path to the {@code netMHC} executable
     * file (assumed to be in the working directory).
     */
    public static final String EXECUTABLE_PATH_DEFAULT = "netMHC";

    /**
     * Determines whether the {@code netMHC} executable is installed
     * at the location specified by the {@code EXECUTABLE_PATH_PROPERTY}.
     *
     * @return {@code true} iff an executable file is installed at the
     * location specified by the {@code EXECUTABLE_PATH_PROPERTY}.
     */
    public static boolean isInstalled() {
        return resolveExecutableFile().canExecute();
    }

    /**
     * Resolves the full path to the {@code netMHC} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY},
     * or {@code netMHC} in the working directory if the system property
     * is not set.
     */
    public static File resolveExecutableFile() {
        return new File(resolveExecutableName());
    }

    /**
     * Resolves the full path to the {@code netMHC} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY},
     * or {@code netMHC} in the working directory if the system property
     * is not set.
     */
    public static String resolveExecutableName() {
        return JamProperties.getOptional(EXECUTABLE_PATH_PROPERTY, EXECUTABLE_PATH_DEFAULT);
    }

    @Override public PredictionMethod getMethod() {
        return PredictionMethod.NET_MHC;
    }

    @Override public List<BindingRecord> predict(String allele, Collection<Peptide> peptides) {
        return NetMHCRunner.run(allele, peptides);
    }
}
