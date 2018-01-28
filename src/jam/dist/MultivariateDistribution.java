
package jam.dist;

import jam.math.JamRandom;
import jam.matrix.MatrixView;
import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Represents a univariate probability distribution over the real
 * numbers.
 */
public interface MultivariateDistribution {
    /**
     * Returns the dimensionality of the sample space.
     *
     * @return the dimensionality of the sample space.
     */
    public abstract int dim();

    /**
     * Returns the mean vector for this distribution.
     *
     * @return the mean vector for this distribution.
     */
    public abstract VectorView mean();

    /**
     * Returns the covariance matrix for this distribution.
     *
     * @return the covariance matrix for this distribution.
     */
    public abstract MatrixView covar();

    /**
     * Computes the probability density function at a point.
     *
     * @param x the point at which the PDF is evaluated.
     *
     * @return the probability density at {@code x}.
     *
     * @throws IllegalArgumentException unless the length of the input
     * vector matches the dimensionality of the sample space.
     */
    public abstract double pdf(VectorView x);

    /**
     * Samples from this distribution using the globally shared random
     * number source.
     *
     * @return the next multivariate value from this distribution.
     */
    public abstract JamVector sample();

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @return the next multivariate value from this distribution.
     */
    public abstract JamVector sample(JamRandom source);

    /**
     * Samples from this distribution using the globally shared random
     * number source.
     *
     * @param count the number of samples to generate.
     *
     * @return the next {@code count} values from this distribution.
     */
    public abstract JamVector[] sample(int count);

    /**
     * Samples from this distribution using a specified random number
     * source.
     *
     * @param source the source of uniform random deviates.
     *
     * @param count the number of samples to generate.
     *
     * @return the next {@code count} values from this distribution.
     */
    public abstract JamVector[] sample(JamRandom source, int count);
}
