
package jam.rna;

import jam.app.JamProperties;
import jam.hugo.HugoSymbol;
import jam.lang.JamException;
import jam.tcga.TumorBarcode;

/**
 * Defines an interface to RNA expression data for a patient cohort.
 */
public abstract class ExpressionProfile {
    private static ExpressionProfile global = null;

    /**
     * Name of the system property that specifies the type of the
     * global RNA expression profile.
     */
    public static final String PROFILE_TYPE_PROPERTY =
        "jam.rna.expressionProfileType";

    /**
     * Returns the global expression profile defined by system
     * properties.
     *
     * @return the global expression profile defined by system
     * properties.
     */
    public static ExpressionProfile global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static ExpressionProfile createGlobal() {
        ExpressionProfileType type = resolveProfileType();

        switch (type) {
        case AGGREGATE:
            return AggregateProfile.global();

        case CANCER_TYPE:
            return CancerTypeProfile.global();

        default:
            throw JamException.runtime("Unsupported expression profile type: [%s].", type);
        }
    }

    private static ExpressionProfileType resolveProfileType() {
        return JamProperties.getRequiredEnum(PROFILE_TYPE_PROPERTY, ExpressionProfileType.class);
    }

    /**
     * Returns the RNA expression level (FPKM) for a specified tumor
     * and gene.
     *
     * @param barcode the identifer for the tumor of interest.
     *
     * @param symbol the HUGO symbol for the gene of interest.
     *
     * @return the RNA expression level for the specified tumor and
     * gene ({@code null} if the profile does not contain a matching
     * record).
     */
    public abstract Expression lookup(TumorBarcode barcode, HugoSymbol symbol);

    /**
     * Returns the RNA expression level (FPKM) for a specified tumor
     * and gene.
     *
     * @param barcode the identifer for the tumor of interest.
     *
     * @param symbol the HUGO symbol for the gene of interest.
     *
     * @return the RNA expression level (FPKM) for the specified tumor
     * and gene.
     *
     * @throws RuntimeException unless the profile contains a matching
     * record.
     */
    public Expression require(TumorBarcode barcode, HugoSymbol symbol) {
        Expression result = lookup(barcode, symbol);

        if (result != null)
            return result;
        else
            throw JamException.runtime("Missing expression record: [%s, %s].",
                                       barcode.getKey(), symbol.getKey());
    }
}
