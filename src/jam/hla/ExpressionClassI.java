
package jam.hla;

import java.util.EnumMap;
import java.util.Map;

import jam.rna.Expression;
import jam.rna.TumorExpressionMatrix;
import jam.tcga.TumorBarcode;

/**
 * Encapsulates RNA expression levels for the HLA class I genes.
 */
public final class ExpressionClassI {
    private final double total;
    private final Map<Locus, Expression> expr;

    private ExpressionClassI(Map<Locus, Expression> expr, double total) {
        this.expr = expr;
        this.total = total;
    }

    /**
     * Creates a new expression record for fixed RNA levels.
     *
     * @param exprA the RNA expression of the HLA-A gene.
     *
     * @param exprB the RNA expression of the HLA-B gene.
     *
     * @param exprC the RNA expression of the HLA-C gene.
     *
     * @return a new expression record with the specfied RNA levels.
     */
    public static ExpressionClassI create(Expression exprA, Expression exprB, Expression exprC) {
        Map<Locus, Expression> expr =
            new EnumMap<Locus, Expression>(Locus.class);

        expr.put(Locus.A, exprA);
        expr.put(Locus.B, exprB);
        expr.put(Locus.C, exprC);

        double total =
            exprA.doubleValue() + exprB.doubleValue() + exprC.doubleValue();

        return new ExpressionClassI(expr, total);
    }

    /**
     * Creates a new expression record for a tumor sample.
     *
     * @param barcode the barcode of interest.
     *
     * @param matrix the RNA expression matrix.

     * @return a new expression record with the RNA levels for the
     * specified tumor sample.
     */
    public static ExpressionClassI create(TumorBarcode barcode, TumorExpressionMatrix matrix) {
        return create(matrix.get(barcode, Locus.A.getHugoSymbol()),
                      matrix.get(barcode, Locus.B.getHugoSymbol()),
                      matrix.get(barcode, Locus.C.getHugoSymbol()));
    }

    /**
     * Returns the raw RNA expression for a given HLA locus.
     *
     * @param locus the locus of interest.
     *
     * @return the raw RNA expression for the specified locus.
     */
    public Expression get(Locus locus) {
        return expr.get(locus);
    }

    /**
     * Returns the fraction of HLA expression that can be attributed
     * to a single allele (the fractional population of that allele
     * inferred from RNA expression and homozygosity).
     *
     * @param allele the allele of interest.
     *
     * @param genotype the genotype to which the allele belongs.
     *
     * @return the fraction of HLA expression that can be attributed
     * to the specified allele (its fractional population among the
     * alleles present in the genotype).
     *
     * @throws IllegalArgumentException unless the genotype contains
     * the allele.
     */
    public double normalize(Allele allele, Genotype genotype) {
        if (!genotype.contains(allele))
            throw new IllegalArgumentException("Allele/genotype mismatch.");

        Locus locus = allele.getLocus();
        int   count = genotype.countUniqueAlleles(locus);

        return expr.get(locus).doubleValue() / total / count;
    }
}
