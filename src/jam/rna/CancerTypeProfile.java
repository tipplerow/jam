
package jam.rna;

import java.io.File;

import jam.app.JamProperties;
import jam.data.DataMatrix;
import jam.data.DenseDataMatrixLoader;
import jam.peptide.HugoSymbol;
import jam.tcga.CancerType;
import jam.tcga.PatientCancerTypeTable;
import jam.tcga.PatientID;
import jam.tcga.TumorBarcode;
import jam.tcga.TumorPatientTable;

/**
 * Represents a gene expression profile with uniform expression within
 * a cancer type (typically the median expression of a cancer-specific
 * cohort).  The mapping from tumor barcode to patient key is defined
 * by the global {@code TumorPatientTable} table, and the mapping from
 * patient to cancer type by the global {@code PatientCancerTypeTable}.
 */
public final class CancerTypeProfile extends ExpressionProfile {
    private final DataMatrix<HugoSymbol, CancerType> cancerTypeExpression;

    private final TumorPatientTable tumorPatientTable = TumorPatientTable.global();
    private final PatientCancerTypeTable patientCancerTypeTable = PatientCancerTypeTable.global();

    private static CancerTypeProfile global = null;

    private CancerTypeProfile(DataMatrix<HugoSymbol, CancerType> cancerTypeExpression) {
        this.cancerTypeExpression = cancerTypeExpression.immutable();
    }

    /**
     * Name of the system property that specifies the data file
     * containing the global cancer type expression profile.
     */
    public static final String PROFILE_FILE_NAME_PROPERTY =
        "jam.rna.cancerTypeExpressionProfile";

    /**
     * Returns the global expression profile defined by system
     * properties.
     *
     * @return the global expression profile defined by system
     * properties.
     */
    public static CancerTypeProfile global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static CancerTypeProfile createGlobal() {
        return instance(resolveProfileFileName());
    }

    private static String resolveProfileFileName() {
        return JamProperties.getRequired(PROFILE_FILE_NAME_PROPERTY);
    }

    /**
     * Creates a new cancer type expression profile by reading cancer
     * type expression data from a file.  (The mapping from tumor to
     * cancer type is defined by the global {@code CancerTypeDb}).
     *
     * @param expressionFile the file containing gene expression by
     * cancer type.
     *
     * @return a new cancer type profile with gene expression data (by
     * cancer type) loaded from the specified file.
     *
     * @throws RuntimeException unless the specified file can be
     * opened for reading and contains properly formatted gene
     * expression data by cancer type.
     */
    public static CancerTypeProfile instance(File expressionFile) {
        return new CancerTypeProfile(loadExpression(expressionFile));
    }

    private static DataMatrix<HugoSymbol, CancerType> loadExpression(File expressionFile) {
        Loader loader = new Loader(expressionFile);
        return loader.load();
    }

    private static final class Loader extends DenseDataMatrixLoader<HugoSymbol, CancerType> {
        private Loader(File file) {
            super(file);
        }

        @Override public CancerType parseColKey(String typeKey) {
            return CancerType.valueOf(typeKey);
        }

        @Override public HugoSymbol parseRowKey(String geneKey) {
            return HugoSymbol.instance(geneKey);
        }
    }

    /**
     * Creates a new cancer type expression profile by reading cancer
     * type expression data from a file.
     *
     * @param expressionFileName the name of the file containing gene
     * expression by cancer type.
     *
     * @return a new cancer type profile with gene expression data (by
     * cancer type) loaded from the specified file.
     *
     * @throws RuntimeException unless the specified file can be
     * opened for reading and contains properly formatted gene
     * expression data by cancer type.
     */
    public static CancerTypeProfile instance(String expressionFileName) {
        return instance(new File(expressionFileName));
    }

    @Override public Expression lookup(TumorBarcode barcode, HugoSymbol symbol) {
        PatientID  patientID  = tumorPatientTable.require(barcode);
        CancerType cancerType = patientCancerTypeTable.require(patientID);

        if (cancerTypeExpression.contains(symbol, cancerType))
            return Expression.valueOf(cancerTypeExpression.get(symbol, cancerType));
        else
            return null;
    }
}
