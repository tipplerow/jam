
package jam.dist;

import jam.math.JamRandom;
import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Provides a skeleton implementation of the {@code MultivariateDistribution}
 * interface.
 */
public abstract class AbstractMultivariateDistribution implements MultivariateDistribution {
    /**
     * Validates a point in the sample space (at which the PDF might
     * be evaluated).
     *
     * @param x a point in the sample space.
     *
     * @throws IllegalArgumentException unless the point {@code x} has
     * the correct dimensionality and lies within the sample space of
     * this distribution.
     */
    protected void validatePoint(VectorView x) {
        if (x.length() != dim())
            throw new IllegalArgumentException("Incompatible dimensionality.");
    }

    @Override public JamVector sample() {
        return sample(JamRandom.global());
    }

    @Override public JamVector[] sample(int count) {
        return sample(JamRandom.global(), count);
    }

    @Override public JamVector[] sample(JamRandom source, int count) {
        JamVector[] samples = new JamVector[count];

        for (int index = 0; index < samples.length; index++)
            samples[index] = sample(source);

        return samples;
    }
}
