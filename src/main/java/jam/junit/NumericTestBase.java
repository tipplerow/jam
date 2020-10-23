
package jam.junit;

import java.util.List;

import jam.math.JamRandom;
import jam.math.RandomSequence;
import jam.vector.VectorUtil;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Provides a base class for all numerical JUnit tests in the
 * {@code jam} project.
 */
public abstract class NumericTestBase extends JamTestBase {
    private final double tolerance;
    private JamRandom random = null;

    private static final long SEED = 19410711;

    public static final double DEFAULT_TOLERANCE = 1.0e-12;

    protected NumericTestBase() {
        this(DEFAULT_TOLERANCE);
    }

    protected NumericTestBase(double tolerance) {
        this.tolerance = tolerance;
    }

    public void assertDouble(double expected, double actual) {
        assertEquals(expected, actual, tolerance);
    }

    public void assertDouble(double[] expected, double[] actual) {
        assertTrue(VectorUtil.equals(expected, actual, tolerance));
    }

    public void assertDouble(List<Double> expected, List<Double> actual) {
        assertEquals(expected.size(), actual.size());

        for (int index = 0; index < expected.size(); ++index)
            assertDouble(expected.get(index), actual.get(index));
    }

    public JamRandom random() {
        if (random == null)
            random = JamRandom.generator(SEED);

        return random;
    }

    public RandomSequence uniform() {
        return RandomSequence.uniform(random());
    }
}
