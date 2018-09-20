
package jam.junit;

import java.util.Arrays;
import java.util.ArrayList;

import com.google.common.collect.Multiset;

import jam.math.IntUtil;
import jam.util.RegexUtil;
import jam.vector.VectorUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class IntUtilTest {
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
