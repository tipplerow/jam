
package jam.junit;

import jam.math.DoubleUtil;
import jam.vector.JamVector;
import jam.vector.VectorFilter;

import org.junit.*;
import static org.junit.Assert.*;

public class VectorFilterTest extends NumericTestBase {
    private void runFilterTest(VectorFilter filter, JamVector initial, JamVector expected) {
        JamVector actual = filter.apply(initial);

        // The filter must operate on the vector in place and return
        // the same physical object...
        assertTrue(actual == initial);
        assertEquals(expected, actual);
    }

    @Test public void testAbs() {
        JamVector initial  = JamVector.valueOf(-2.0, -1.0, 0.0, 4.0, 8.0);
        JamVector expected = JamVector.valueOf( 2.0,  1.0, 0.0, 4.0, 8.0);

        runFilterTest(VectorFilter.ABS, initial, expected);
    }

    @Test public void testDemean() {
        JamVector initial  = JamVector.valueOf( 1.0,  2.0, 3.0, 4.0);
        JamVector expected = JamVector.valueOf(-1.5, -0.5, 0.5, 1.5);

        runFilterTest(VectorFilter.DEMEAN, initial, expected);
    }

    @Test public void testLog() {
        JamVector initial  = JamVector.valueOf(0.5, 1.0, 2.0);
        JamVector expected = JamVector.valueOf(-DoubleUtil.LOG2, 0.0, DoubleUtil.LOG2);

        runFilterTest(VectorFilter.LOG, initial, expected);
    }

    @Test public void testZScore() {
        double x1 =  1.0;
        double x2 =  2.0;
        double x3 =  4.0;
        double x4 =  8.0;
        double x5 = 16.0;

        double mean = 6.2;
        double sd   = Math.sqrt(37.2);

        double y1 = (x1 - mean) / sd;
        double y2 = (x2 - mean) / sd;
        double y3 = (x3 - mean) / sd;
        double y4 = (x4 - mean) / sd;
        double y5 = (x5 - mean) / sd;

        JamVector initial  = JamVector.valueOf(x1, x2, x3, x4, x5);
        JamVector expected = JamVector.valueOf(y1, y2, y3, y4, y5);

        runFilterTest(VectorFilter.ZSCORE, initial, expected);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.VectorFilterTest");
    }
}
