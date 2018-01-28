
package jam.junit;

import jam.math.JamRandom;
import jam.math.RandomSequence;
import jam.vector.VectorUtil;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class NumericTestBase {
    private final double tolerance;

    static {
        System.setProperty(JamRandom.SEED_PROPERTY, "19410711");
    }

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

    public JamRandom random() {
        return JamRandom.global();
    }

    public RandomSequence uniform() {
        return RandomSequence.uniform(random());
    }
}
