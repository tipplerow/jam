
package jam.ensembl;

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
     * Name of the system property that specifies the reference
     * assembly used to create a mapping from Ensembl transcript
     * identifiers to Ensembl gene identifiers to HUGO gene symbols.
     */
    public static final String REFERENCE_ASSEMBLY_PROPERTY = "jam.ensembl.referenceAssembly";

    /**
     * The default reference assembly.
     */
    public static final EnsemblAssembly DEFAULT_REFERENCE_ASSEMBLY = GRCh38;

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
        return JamProperties.getOptionalEnum(REFERENCE_ASSEMBLY_PROPERTY, DEFAULT_REFERENCE_ASSEMBLY);
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
