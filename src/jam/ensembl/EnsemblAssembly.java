
package jam.ensembl;

import java.io.File;

import jam.app.JamEnv;
import jam.app.JamProperties;

/**
 * Enumerates published Ensembl genome assemblies.
 */
public enum EnsemblAssembly {
    GRCh37("ENSEMBL_GRCH37"),
    GRCh38("ENSEMBL_GRCH38");

    private final String    envName;
    private       EnsemblDb ensemblDb = null;

    private static EnsemblAssembly reference = null;

    private EnsemblAssembly(String envName) {
        this.envName = envName;
    }

    /**
     * Name of the environment variable that specifies the reference
     * assembly used to create a mapping from Ensembl transcript
     * identifiers to Ensembl gene identifiers to HUGO gene symbols.
     */
    public static final String REFERENCE_ASSEMBLY_ENV = "ENSEMBL_ASSEMBLY";

    /**
     * Name of the system property that specifies the reference
     * assembly used to create a mapping from Ensembl transcript
     * identifiers to Ensembl gene identifiers to HUGO gene symbols.
     *
     * <p><b>The system property takes precedence over the environment
     * variable if both are specified.</b>
     */
    public static final String REFERENCE_ASSEMBLY_PROPERTY = "jam.ensembl.referenceAssembly";

    /**
     * The default reference assembly.
     */
    public static final EnsemblAssembly DEFAULT_REFERENCE_ASSEMBLY = GRCh38;

    /**
     * Environment variable that specifies the directory containing
     * the active Ensembl release.
     */
    public static final String ENSEMBL_RELEASE_ENV = "ENSEMBL_RELEASE_DIR";

    /**
     * Returns the reference assembly used to create a mapping from
     * Ensembl transcript identifiers to Ensembl gene identifiers to
     * HUGO gene symbols.
     *
     * @return the reference assembly.
     */
    public static EnsemblAssembly reference() {
        if (reference == null)
            reference = resolveReference();

        return reference;
    }

    private static EnsemblAssembly resolveReference() {
        if (JamProperties.isSet(REFERENCE_ASSEMBLY_PROPERTY))
            return valueOf(JamProperties.getRequired(REFERENCE_ASSEMBLY_PROPERTY));

        if (JamEnv.isSet(REFERENCE_ASSEMBLY_ENV))
            return valueOf(JamEnv.getRequired(REFERENCE_ASSEMBLY_ENV));

        return DEFAULT_REFERENCE_ASSEMBLY;
    }

    /**
     * Returns the top-level directory containing the active Ensembl
     * release.
     *
     * @return the top-level directory containing the active Ensembl
     * release.
     */
    public static File releaseDir() {
        return new File(JamEnv.getOptional(ENSEMBL_RELEASE_ENV, "."));
    }

    /**
     * Returns the Ensembl database built for this assembly.
     *
     * @return the Ensembl database built for this assembly.
     */
    public EnsemblDb db() {
        if (ensemblDb == null)
            ensemblDb = createDb();

        return ensemblDb;
    }

    private EnsemblDb createDb() {
        return EnsemblDb.load(JamEnv.getRequired(envName));
    }
}
