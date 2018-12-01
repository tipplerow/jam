
package jam.bio;

import java.io.File;

import jam.app.JamHome;
import jam.io.FileUtil;
import jam.io.LineReader;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;

/**
 * Represents pairwise interactions between residues (a
 * <em>R</em>esidue <em>I</em>nteraction <em>M</em>atrix).
 */
public final class RIM {
    private final MatrixView matrix;

    private RIM(MatrixView matrix) {
        this.matrix = matrix;
        validateMatrix(matrix);
    }

    private static void validateMatrix(MatrixView matrix) {
        int N = Residue.countNative();

        if (matrix.nrow() != N)
            throw new IllegalArgumentException("Invalid matrix row dimension.");

        if (matrix.ncol() != N)
            throw new IllegalArgumentException("Invalid matrix column dimension.");

        if (!matrix.isSymmetric())
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
     * Returns the interaction strength between specific residues.
     *
     * @param res1 the first residue.
     *
     * @param res2 the second residue.
     *
     * @return the interaction strength between the specific residues.
     */
    public double get(Residue res1, Residue res2) {
	return matrix.get(res1.ordinal(), res2.ordinal());
    }
}
