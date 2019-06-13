
package jam.stab;

import java.io.File;
import java.util.Collection;
import java.util.List;

import jam.app.JamEnv;
import jam.app.JamProperties;
import jam.hla.Allele;
import jam.peptide.Peptide;

/**
 * Predicts the stability of peptide-MHC complexes using the
 * {@code netMHCstabpan} engine.
 */ 
public final class NetStab {
    private NetStab() {}

    /**
     * Name of the environment variable that defines the full path to
     * the {@code netMHCstsabpan} executable.  If the system property
     * {@code pepmhc.engine.net.netMHCstabpan} is also defined, it
     * will take precedence.
     */
    public static final String EXECUTABLE_PATH_ENV = "NET_MHC_STAB_PAN_EXE";

    /**
     * Name of the system property that defines the full path to the
     * {@code netMHCpan} executable file.
     */
    public static final String EXECUTABLE_PATH_PROPERTY = "jam.stab.netMHCstabpan";

    /**
     * Determines whether the {@code netMHCstabpan} command-line
     * program is installed and is executable.
     *
     * @return {@code true} iff the {@code netMHCstabpan} executable
     * is installed at the resolved executable file location.
     */
    public static boolean isInstalled() {
        return resolveExecutableFile().canExecute();
    }

    /**
     * Resolves the full path to the {@code netMHCstabpan} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY}
     * (if set), or the path specified by the {@code EXECUTABLE_PATH_ENV}
     * environment variable (if set), or {@code netMHCstabpan} otherwise.
     */
    public static File resolveExecutableFile() {
        return new File(resolveExecutableName());
    }

    /**
     * Resolves the full path to the {@code netMHCstabpan} executable file.
     *
     * @return the path specified by the {@code EXECUTABLE_PATH_PROPERTY}
     * (if set), or the path specified by the {@code EXECUTABLE_PATH_ENV}
     * environment variable (if set), or {@code ./netMHCstabpan} otherwise.
     */
    public static String resolveExecutableName() {
        if (JamProperties.isSet(EXECUTABLE_PATH_PROPERTY))
            return JamProperties.getRequired(EXECUTABLE_PATH_PROPERTY);

        if (JamEnv.isSet(EXECUTABLE_PATH_ENV))
            return JamEnv.getRequired(EXECUTABLE_PATH_ENV);

        return "./netMHCstabpan";
    }

    /**
     * Predicts the MHC-peptide complex stability for a given allele
     * and target peptide.
     *
     * @param allele the binding MHC allele.
     *
     * @param peptide the peptide target.
     *
     * @return the stability record for the specified allele and
     * peptide.
     */
    public static StabilityRecord run(Allele allele, Peptide peptide) {
        return run(allele, List.of(peptide)).get(0);
    }

    /**
     * Predicts the MHC-peptide complex stability for a given allele
     * and a collection of target peptides.
     *
     * @param allele the binding MHC allele.
     *
     * @param peptides the peptide targets.
     *
     * @return the stability records for the specified allele and
     * peptides.
     */
    public static List<StabilityRecord> run(Allele allele, Collection<Peptide> peptides) {
        return NetStabRunner.run(allele, peptides);
    }
}
