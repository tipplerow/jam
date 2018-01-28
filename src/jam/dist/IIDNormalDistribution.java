
package jam.dist;

import jam.math.JamRandom;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Represents an identically and independently distributed
 * multivariate normal (Gaussian) distribution.
 */
public final class IIDNormalDistribution extends AbstractMultivariateDistribution {
    private final int nvar;
    private final VectorView mean;
    private final MatrixView covar;
    private final NormalDistribution dist;

    /**
     * Creates an identically and independently distributed
     * multivariate normal (Gaussian) distribution.
     *
     * @param nvar the number of IID variables.
     *
     * @param mean the mean of each variable.
     *
     * @param stdev the standard deviation of each variable.
     *
     * @throws IllegalArgumentException unless the number of variables
     * and standard deviation are positive.
     */
    public IIDNormalDistribution(int nvar, double mean, double stdev) {
        validateNumber(nvar);

        this.nvar  = nvar;
        this.dist  = new NormalDistribution(mean, stdev);
        this.mean  = computeMean(nvar, mean);
        this.covar = computeCovar(nvar, stdev);
    }

    private static void validateNumber(int nvar) {
        if (nvar < 1)
            throw new IllegalArgumentException("Non-positive variable count.");
    }

    private static VectorView computeMean(int nvar, double mean) {
        return JamVector.ones(nvar).times(mean);
    }

    private static MatrixView computeCovar(int nvar, double stdev) {
        return JamMatrix.identity(nvar).times(stdev * stdev);
    }

    @Override public int dim() {
        return nvar;
    }

    @Override public VectorView mean() {
        return mean;
    }

    @Override public MatrixView covar() {
        return covar;
    }

    @Override public double pdf(VectorView x) {
        validatePoint(x);
        double result = 1.0;

        for (double coord : x.elements())
            result *= dist.pdf(coord);

        return result;
    }

    @Override public JamVector sample(JamRandom source) {
        JamVector result = new JamVector(nvar);

        for (int index = 0; index < nvar; index++)
            result.set(index, dist.sample(source));

        return result;
    }
}
