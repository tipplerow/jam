
package jam.rna;

import java.io.File;

import jam.data.DataMatrix;
import jam.data.DenseDataMatrixLoader;
import jam.hugo.HugoSymbol;
import jam.tcga.TumorBarcode;

/**
 * Stores RNA expression indexed by tumor barcode and HUGO symbol.
 */
public final class TumorExpressionMatrix {
    private final DataMatrix<TumorBarcode, HugoSymbol> expression;

    private TumorExpressionMatrix(DataMatrix<TumorBarcode, HugoSymbol> expression, boolean wrap) {
        if (wrap)
            this.expression = expression.immutable();
        else
            this.expression = expression;
    }

    /**
     * Creates a new tumor expression matrix.
     *
     * @param expression the expression data.
     */ 
    public TumorExpressionMatrix(DataMatrix<TumorBarcode, HugoSymbol> expression) {
        this(expression, true);
    }

    /**
     * Loads a tumor expression matrix from a data file.
     *
     * @param file the file to load.
     *
     * @return a new matrix with expression data loaded from the
     * specified file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains valid expression data.
     */
    public static TumorExpressionMatrix load(File file) {
        return new TumorExpressionMatrix(loadExpression(file), false);
    }

    /**
     * Loads a tumor expression matrix from a data file.
     *
     * @param fileName the name of the file to load.
     *
     * @return a new matrix with expression data loaded from the
     * specified file.
     *
     * @throws RuntimeException unless the file can be opened for
     * reading and contains valid expression data.
     */
    public static TumorExpressionMatrix load(String fileName) {
        return load(new File(fileName));
    }

    private static DataMatrix<TumorBarcode, HugoSymbol> loadExpression(File file) {
        Loader loader = new Loader(file);
        return loader.load();
    }

    private static final class Loader extends DenseDataMatrixLoader<TumorBarcode, HugoSymbol> {
        private Loader(File file) {
            super(file);
        }

        @Override public HugoSymbol parseColKey(String geneKey) {
            return HugoSymbol.instance(geneKey);
        }

        @Override public TumorBarcode parseRowKey(String tumorKey) {
            return TumorBarcode.instance(tumorKey);
        }
    }

    /**
     * Identifies tumors contained in this matrix.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @return {@code true} iff this matrix contains expression data
     * for the specified barcode.
     */
    public boolean contains(TumorBarcode barcode) {
        return expression.containsRow(barcode);
    }

    /**
     * Identifies genes contained in this matrix.
     *
     * @param symbol the HUGO gene symbol of interest.
     *
     * @return {@code true} iff this matrix contains expression data
     * for the specified symbol.
     */
    public boolean contains(HugoSymbol symbol) {
        return expression.containsCol(symbol);
    }

    /**
     * Identifies tumors and genes contained in this matrix.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO gene symbol of interest.
     *
     * @return {@code true} iff this matrix contains expression data
     * for the specified barcode and symbol.
     */
    public boolean contains(TumorBarcode barcode, HugoSymbol symbol) {
        return expression.contains(barcode, symbol);
    }

    /**
     * Returns the expression for a given tumor and gene.
     *
     * @param barcode the tumor barcode of interest.
     *
     * @param symbol the HUGO gene symbol of interest.
     *
     * @return the expression for the specified tumor and gene, or
     * {@code null} if this matrix does not contain the tumor and
     * gene.
     */
    public Expression get(TumorBarcode barcode, HugoSymbol symbol) {
        if (contains(barcode, symbol))
            return Expression.valueOf(expression.get(barcode, symbol));
        else
            return null;
    }
}
