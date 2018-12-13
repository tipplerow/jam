
package jam.peptide;

import java.io.File;
import java.util.List;

import jam.app.JamHome;
import jam.dist.RealDistribution;
import jam.io.FileUtil;
import jam.matrix.MatrixUtil;

/**
 * Represents pairwise interactions between native residues (a
 * <em>R</em>esidue <em>I</em>nteraction <em>M</em>atrix).
 */
public final class RIM {
    private final double[][] matrix;
    private final double[]   means;

    private RIM(double[][] matrix) {
        validateMatrix(matrix);

        this.matrix = matrix;
        this.means  = MatrixUtil.rowMeans(matrix);
    }

    private static void validateMatrix(double[][] matrix) {
        int N = Residue.countNative();

        if (MatrixUtil.nrow(matrix) != N)
            throw new IllegalArgumentException("Invalid matrix row dimension.");

        if (MatrixUtil.ncol(matrix) != N)
            throw new IllegalArgumentException("Invalid matrix column dimension.");

        if (!MatrixUtil.isSymmetric(matrix))
            throw new IllegalArgumentException("Non-symmetric matrix.");
    }

    /**
     * The Miyazawa-Jernigan interaction matrix (Journal of Molecular Biology, 1996).
     */
    public static final RIM MiyazawaJernigan = createMJ();

    private static RIM createMJ() {
        try {
            return new RIM(RIMReader.readSparse(findMJData()));
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to load Miyazawa-Jernigan interaction matrix.");
        }
    }

    private static File findMJData() {
        return new File(FileUtil.join(JamHome.NAME, "data", "MJ_upper.csv"));
    }

    /**
     * Creates a residue-interaction matrix with elements sampled from
     * a probability distribution.
     *
     * @param distrib the distribution from which to sample interactions.
     *
     * @return the new random interaction matrix.
     */
    public static RIM random(RealDistribution distrib) {
        int N = Residue.countNative();
        double[][] elements = MatrixUtil.square(N, Double.NaN);

        for (int i = 0; i < N; ++i) {
            elements[i][i] = distrib.sample();

            for (int j = i + 1; j < N; ++j) {
                double xij = distrib.sample();

                elements[i][j] = xij;
                elements[j][i] = xij;
            }
        }

        return new RIM(elements);
    }

    /**
     * Computes the nearest-neighbor interaction energy between two
     * peptides of the same length, assuming that they are perfectly
     * aligned.
     *
     * @param pep1 the first peptide.
     *
     * @param pep2 the second peptide.
     *
     * @return the nearest-neighbor interaction energy defined above.
     *
     * @throws IllegalArgumentException unless the peptides have the
     * same length.
     */
    public double computeNearest(Peptide pep1, Peptide pep2) {
        if (pep1.length() != pep2.length())
            throw new IllegalArgumentException("Peptide lengths are unequal.");

        double result = 0.0;

        for (int k = 0; k < pep1.length(); k++)
            result += get(pep1.at(k), pep2.at(k));

        return result;
    }

    /**
     * Computes the average nearest-neighbor interaction energy for a
     * given binder peptide averaged over all possible target peptides
     * of the same length, assuming that amino acids are distributed
     * independently with equal probability.
     *
     * @param binder the binder peptide.
     *
     * @return the average nearest-neighbor interaction energy.
     *
     * @throws IllegalArgumentException if the binder peptide exceeds
     * the enumeration length.
     */
    public double computeMeanNearest(Peptide binder) {
        double total = 0.0;
        List<Peptide> targets = Peptide.enumerate(binder.length());

        for (Peptide target : targets)
            total += computeNearest(binder, target);

        return total / targets.size();
    }

    /**
     * Returns the interaction strength between specific residues.
     *
     * @param res1 the first residue.
     *
     * @param res2 the second residue.
     *
     * @return the interaction strength between the specific residues.
     */
    public double get(Residue res1, Residue res2) {
	return matrix[indexOf(res1)][indexOf(res2)];
    }

    private static int indexOf(Residue res) {
        return res.ordinal();
    }

    /**
     * Returns the mean interaction for one residue taken over all
     * native residues.
     *
     * @param res the residue of interest.
     *
     * @return the mean interaction of the specified residue taken
     * over all native residues.
     */
    public double mean(Residue res) {
        return means[indexOf(res)];
    }
}
