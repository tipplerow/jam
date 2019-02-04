
package jam.junit;

import java.util.Arrays;
import java.util.ArrayList;

import com.google.common.collect.Multiset;

import jam.math.DoubleUtil;
import jam.math.IntUtil;
import jam.math.StatUtil;
import jam.util.RegexUtil;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class IntUtilTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0e-12;

    @Test public void testCount1() {
        Multiset counts = IntUtil.count(Arrays.asList(1, 2, 3, 2, 3, 3));

        assertEquals(1, counts.count(1));
        assertEquals(2, counts.count(2));
        assertEquals(3, counts.count(3));
    }

    @Test public void testCount2() {
        Multiset counts = IntUtil.count(1, 2, 3, 2, 3, 3);

        assertEquals(1, counts.count(1));
        assertEquals(2, counts.count(2));
        assertEquals(3, counts.count(3));
    }

    @Test public void testIsDivisible() {
        assertTrue(IntUtil.isDivisible(10,  1));
        assertTrue(IntUtil.isDivisible(10,  2));
        assertTrue(IntUtil.isDivisible(10,  5));
        assertTrue(IntUtil.isDivisible(10, 10));

        assertFalse(IntUtil.isDivisible(10,  3));
        assertFalse(IntUtil.isDivisible(10,  4));
        assertFalse(IntUtil.isDivisible(10,  6));
        assertFalse(IntUtil.isDivisible(10,  7));
        assertFalse(IntUtil.isDivisible(10,  8));
        assertFalse(IntUtil.isDivisible(10,  9));
        assertFalse(IntUtil.isDivisible(10, 11));
        assertFalse(IntUtil.isDivisible(10, 99));
    }

    @Test public void testParseIntArray() {
        int[] actual   = IntUtil.parseIntArray("1, 2, 3", RegexUtil.COMMA);
        int[] expected = new int[] { 1, 2, 3 };

        assertTrue(Arrays.equals(actual, expected));
    }

    @Test public void testSample() {
        int ITER_COUNT = 10000;
        int SAMPLE_SIZE = 80;
        int ELEMENT_COUNT = 101;

        double[] corr  = new double[ITER_COUNT];
        double[] mean1 = new double[ITER_COUNT];
        double[] mean2 = new double[ITER_COUNT];

        for (int iter = 0; iter < ITER_COUNT; ++iter) {
            VectorView sample1 = VectorView.wrap(IntUtil.sample(ELEMENT_COUNT, SAMPLE_SIZE));
            VectorView sample2 = VectorView.wrap(IntUtil.sample(ELEMENT_COUNT, SAMPLE_SIZE));

            corr[iter]  = StatUtil.cor(sample1, sample2);
            mean1[iter] = StatUtil.mean(sample1);
            mean2[iter] = StatUtil.mean(sample2);
        }

        assertEquals( 0.0, StatUtil.mean(VectorView.wrap(corr)), 0.002);
        assertEquals(50.0, StatUtil.mean(VectorView.wrap(mean1)), 0.1);
        assertEquals(50.0, StatUtil.mean(VectorView.wrap(mean2)), 0.1);
    }

    @Test public void testToArray() {
        assertTrue(Arrays.equals(new int[] { 3, 2, 1 }, IntUtil.toArray(Arrays.asList(3, 2, 1))));
    }

    @Test public void testToDouble1() {
        assertTrue(VectorUtil.equals(new double[] { 1.0, 2.0, 3.0 }, IntUtil.toDouble(1, 2, 3), TOLERANCE));
    }

    @Test public void testToDouble2() {
        assertTrue(VectorUtil.equals(new double[] { 1.0, 2.0, 3.0 }, IntUtil.toDouble(Arrays.asList(1, 2, 3)), TOLERANCE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IntUtilTest");
    }
}
