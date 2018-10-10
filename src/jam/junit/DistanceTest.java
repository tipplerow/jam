
package jam.junit;

import jam.math.Distance;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class DistanceTest extends NumericTestBase {
    private final VectorView v1 = VectorView.wrap(1.0, 2.0, 3.0);
    private final VectorView v2 = VectorView.wrap(0.0, 4.0, 7.0);

    @Test public void testEuclidean() {
        assertDouble(0.0, Distance.EUCLIDEAN.compute(v1, v1));
        assertDouble(0.0, Distance.EUCLIDEAN.compute(v2, v2));

        assertDouble(Math.sqrt(21.0), Distance.EUCLIDEAN.compute(v1, v2));
        assertDouble(Math.sqrt(21.0), Distance.EUCLIDEAN.compute(v2, v1));
    }

    @Test public void testManhattan() {
        assertDouble(0.0, Distance.MANHATTAN.compute(v1, v1));
        assertDouble(0.0, Distance.MANHATTAN.compute(v2, v2));

        assertDouble(7.0, Distance.MANHATTAN.compute(v1, v2));
        assertDouble(7.0, Distance.MANHATTAN.compute(v2, v1));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DistanceTest");
    }
}
