
package jam.math;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Multiset;

import jam.io.Delimiter;
import jam.junit.NumericTestBase;
import jam.vector.VectorUtil;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class IntUtilTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0e-12;

    @Test public void testCbrt() {
        assertEquals(-3, IntUtil.cbrt(-27));
        assertEquals(-2, IntUtil.cbrt( -8));
        assertEquals(-1, IntUtil.cbrt( -1));
        assertEquals( 0, IntUtil.cbrt(  0));
        assertEquals( 1, IntUtil.cbrt(  1));
        assertEquals( 2, IntUtil.cbrt(  8));
        assertEquals( 3, IntUtil.cbrt( 27));
    }

    @Test(expected = RuntimeException.class)
    public void testCbrtInvalid() {
        IntUtil.cbrt(10);
    }

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

    @Test public void testIsCube() {
        assertTrue(IntUtil.isCube(-8));
        assertTrue(IntUtil.isCube(-1));
        assertTrue(IntUtil.isCube( 0));
        assertTrue(IntUtil.isCube( 1));
        assertTrue(IntUtil.isCube( 8));
        assertTrue(IntUtil.isCube(27));
        assertTrue(IntUtil.isCube(64));

        assertFalse(IntUtil.isCube(2));
        assertFalse(IntUtil.isCube(3));
        assertFalse(IntUtil.isCube(4));
        assertFalse(IntUtil.isCube(5));
        assertFalse(IntUtil.isCube(6));
        assertFalse(IntUtil.isCube(7));
        assertFalse(IntUtil.isCube(9));
        assertFalse(IntUtil.isCube(10));

        for (int k = 10; k < 1000; ++k) {
            assertTrue(IntUtil.isCube(k * k * k));

            assertFalse(IntUtil.isCube(k * k * k + 1));
            assertFalse(IntUtil.isCube(k * k * k - 1));
        }
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

    @Test public void testIsSquare() {
        assertFalse(IntUtil.isSquare(-4));
        assertFalse(IntUtil.isSquare(-1));

        assertTrue(IntUtil.isSquare(0));
        assertTrue(IntUtil.isSquare(1));
        assertFalse(IntUtil.isSquare(2));
        assertFalse(IntUtil.isSquare(3));
        assertTrue(IntUtil.isSquare(4));
        assertFalse(IntUtil.isSquare(5));
        assertFalse(IntUtil.isSquare(6));
        assertFalse(IntUtil.isSquare(7));
        assertFalse(IntUtil.isSquare(8));
        assertTrue(IntUtil.isSquare(9));
        assertFalse(IntUtil.isSquare(10));

        for (int k = 10; k < 1000; ++k) {
            assertTrue(IntUtil.isSquare(k * k));

            assertFalse(IntUtil.isSquare(k * k + 1));
            assertFalse(IntUtil.isSquare(k * k - 1));
        }
    }

    @Test public void testParseInt() {
        assertEquals(1000000, IntUtil.parseInt("1E6"));
        assertEquals(1000000, IntUtil.parseInt("1,000,000"));
        assertEquals(1234567, IntUtil.parseInt("1,234,567"));
    }

    @Test public void testParseIntArray() {
        int[] actual   = IntUtil.parseIntArray("1, 2, 3", Delimiter.COMMA);
        int[] expected = new int[] { 1, 2, 3 };

        assertTrue(Arrays.equals(actual, expected));
    }

    @Test public void testParseIntList() {
        List<Integer> actual   = IntUtil.parseIntList("1, 2, 3", Delimiter.COMMA);
        List<Integer> expected = List.of(1, 2, 3);

        assertEquals(actual, expected);
    }

    @Test public void testSample() {
        int ITER_COUNT = 10000;
        int SAMPLE_SIZE = 80;
        int ELEMENT_COUNT = 101;

        double[] corr  = new double[ITER_COUNT];
        double[] mean1 = new double[ITER_COUNT];
        double[] mean2 = new double[ITER_COUNT];

        for (int iter = 0; iter < ITER_COUNT; ++iter) {
            VectorView sample1 = VectorView.wrap(IntUtil.sample(ELEMENT_COUNT, SAMPLE_SIZE, random()));
            VectorView sample2 = VectorView.wrap(IntUtil.sample(ELEMENT_COUNT, SAMPLE_SIZE, random()));

            corr[iter]  = StatUtil.cor(sample1, sample2);
            mean1[iter] = StatUtil.mean(sample1);
            mean2[iter] = StatUtil.mean(sample2);
        }

        assertEquals( 0.0, StatUtil.mean(VectorView.wrap(corr)), 0.002);
        assertEquals(50.0, StatUtil.mean(VectorView.wrap(mean1)), 0.1);
        assertEquals(50.0, StatUtil.mean(VectorView.wrap(mean2)), 0.1);
    }

    @Test public void testSqrt() {
        assertEquals(0, IntUtil.sqrt(0));
        assertEquals(1, IntUtil.sqrt(1));
        assertEquals(2, IntUtil.sqrt(4));
        assertEquals(3, IntUtil.sqrt(9));
    }

    @Test(expected = RuntimeException.class)
    public void testSqrtInvalid() {
        IntUtil.sqrt(10);
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
        org.junit.runner.JUnitCore.main("jam.math.IntUtilTest");
    }
}
