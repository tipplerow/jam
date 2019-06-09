
package jam.rna;

import java.io.File;
import java.util.List;

import jam.data.DataMatrix;
import jam.data.DenseDataMatrixLoader;
import jam.peptide.HugoSymbol;
import jam.tcga.CancerType;

/**
 * Manages data matrices containing gene expression by cancer type.
 */
public final class ExpressionByCancerType {
    /**
     * Creates a new data matrix indexed by gene and cancer type.
     *
     * @param genes the genes of interest.
     *
     * @param types the cancer types of interest.
     *
     * @return a new {@code DataMatrix} with the specified keys.
     */
    public static DataMatrix<HugoSymbol, CancerType> create(List<HugoSymbol> genes, List<CancerType> types) {
        return DataMatrix.dense(genes, types);
    }

    /**
     * Loads gene expression by cancer type from a data file.
     *
     * @param file the file to load.
     *
     * @return a new {@code DataMatrix} containing gene expression by
     * cancer type loaded from the specified file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted gene expression data.
     */
    public static DataMatrix<HugoSymbol, CancerType> load(File file) {
        Loader loader = new Loader(file);
        return loader.load();
    }

    /**
     * Loads gene expression by cancer type from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a new {@code DataMatrix} containing gene expression by
     * cancer type loaded from the specified file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains properly formatted gene expression data.
     */
    public static DataMatrix<HugoSymbol, CancerType> load(String fileName) {
        return load(new File(fileName));
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
}
