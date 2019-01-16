
package jam.pepmhc.smm;

import java.util.List;
import java.util.Map;

import jam.app.JamHome;
import jam.io.FileUtil;
import jam.lang.JamException;
import jam.pepmhc.PredictionMethod;
import jam.pepmhc.PredictorKey;
import jam.peptide.Peptide;
import jam.peptide.Residue;

/**
 * Implements a <em>stabilized matrix</em> for predicting peptide-MHC
 * binding affinities.
 */
public final class StabilizedMatrix {
    private final double intercept;
    private final List<Map<Residue, Double>> elements;

    StabilizedMatrix(List<Map<Residue, Double>> elements, double intercept) {
        this.elements = elements;
        this.intercept = intercept;
    }

    /**
     * Returns the stabilized matrix for a given allele code and
     * peptide length.
     *
     * @param key the key of the desired predictor.
     *
     * @return the stabilized matrix for the specified key.
     */
    public static StabilizedMatrix instance(PredictorKey key) {
        return load(resolveFileName(key));
    }

    /**
     * Reads a stabilized matrix from a formatted data file.
     *
     * @param fileName the name of the data file.
     *
     * @return the stabilized matrix with parameters contained in the
     * specified data file.
     */
    public static StabilizedMatrix load(String fileName) {
        return MatrixReader.load(fileName);
    }

    /**
     * Returns the data file for a specified predictor.
     *
     * @param key the key of the desired predictor.
     *
     * @return the name of the data file containing parameters for the
     * given predictor.
     */
    public static String resolveFileName(PredictorKey key) {
        return FileUtil.join(dirName(key), baseName(key));
    }

    private static String dirName(PredictorKey key) {
        return dirName(key.getPredictionMethod());
    }

    private static String dirName(PredictionMethod method) {
        switch (method) {
        case SMM:
            return FileUtil.join(JamHome.NAME, "data", "pepmhc", "smm");

        case SMM_PMBEC:
            return FileUtil.join(JamHome.NAME, "data", "pepmhc", "smm_pmbec");

        default:
            throw JamException.runtime("Unsupported prediction method: [%s].", method);
        }
    }

    private static String baseName(PredictorKey key) {
        return baseName(key.getAlleleCode(), key.getPeptideLength());
    }

    private static String baseName(String alleleCode, int peptideLength) {
        return String.format("%s-%d.txt", alleleCode.replace('*', '-'), peptideLength);
    }

    /**
     * Computes the predicted binding affinity for a given peptide.
     *
     * @param peptide the peptide of interest.
     *
     * @return the predicted binding affinity for the given peptide.
     */
    public double computeIC50(Peptide peptide) {
        if (peptide.length() != elements.size())
            throw new IllegalArgumentException("Invalid peptide length.");

        double logsum = intercept;

        for (int index = 0; index < peptide.length(); ++index)
            logsum += elements.get(index).get(peptide.at(index));

        return Math.pow(10.0, logsum);
    }

    /**
     * Returns the matrix element (log10 contribution) for a given
     * residue and binding position.
     *
     * @param residue the peptide residue of interest.
     *
     * @param position the position where the residue binds.
     *
     * @return the matrix element (log10 contribution) for the given
     * residue and binding position.
     */
    public double getElement(Residue residue, int position) {
        return elements.get(position).get(residue);
    }

    /**
     * Returns the value of the intercept for this stabilized matrix.
     *
     * @return the value of the intercept for this stabilized matrix.
     */
    public double getIntercept() {
        return intercept;
    }

    /**
     * Returns the peptide length covered by this stabilized matrix.
     *
     * @return the peptide length covered by this stabilized matrix.
     */
    public int getPeptideLength() {
        return elements.size();
    }
}
