
package jam.junit;

import java.util.ArrayList;
import jam.math.DoubleComparator;
import jam.math.DoubleUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class DoubleUtilTest extends NumericTestBase {
    @Test public void testIsDouble() {
        assertTrue(DoubleUtil.isDouble("1.0"));
        assertTrue(DoubleUtil.isDouble("-1.0"));
        assertTrue(DoubleUtil.isDouble("NA"));
        assertTrue(DoubleUtil.isDouble("NaN"));
        assertTrue(DoubleUtil.isDouble("Infinity"));
        assertTrue(DoubleUtil.isDouble("-Infinity"));

        assertFalse(DoubleUtil.isDouble(null));
        assertFalse(DoubleUtil.isDouble(""));
        assertFalse(DoubleUtil.isDouble("abc"));
        assertFalse(DoubleUtil.isDouble("1.0, 2.0"));
    }

    @Test public void testIsFinite() {
        assertTrue(DoubleUtil.isFinite(0.0));
        assertTrue(DoubleUtil.isFinite(1.0E+200));
        assertFalse(DoubleUtil.isFinite(Double.NaN));
        assertFalse(DoubleUtil.isFinite(Double.POSITIVE_INFINITY));
        assertFalse(DoubleUtil.isFinite(Double.NEGATIVE_INFINITY));
    }

    @Test public void testIsInt() {
        assertFalse(DoubleUtil.isInt(-Math.pow(2.0, 31) - 1.0));
        assertTrue( DoubleUtil.isInt(-Math.pow(2.0, 31)));
        assertTrue( DoubleUtil.isInt(-Math.pow(2.0, 31) + 1.0));

        assertFalse(DoubleUtil.isInt(-1.0E-10));
        assertTrue( DoubleUtil.isInt( 0.0));
        assertFalse(DoubleUtil.isInt( 1.0E-10));

        assertTrue( DoubleUtil.isInt(Math.pow(2.0, 31) - 2.0));
        assertTrue( DoubleUtil.isInt(Math.pow(2.0, 31) - 1.0));
        assertFalse(DoubleUtil.isInt(Math.pow(2.0, 31)));
    }

    @Test public void testIsLong() {
        assertFalse(DoubleUtil.isLong(-Math.pow(2.0, 64)));
        assertTrue( DoubleUtil.isLong(-Math.pow(2.0, 62)));

        assertFalse(DoubleUtil.isLong(-1.0E-10));
        assertTrue( DoubleUtil.isLong( 0.0));
        assertFalse(DoubleUtil.isLong( 1.0E-10));

        assertTrue( DoubleUtil.isLong(Math.pow(2.0, 62)));
        assertFalse(DoubleUtil.isLong(Math.pow(2.0, 64)));
    }

    @Test public void testLog() {
        double x = 1.23456;

        assertDouble(Math.log(x), DoubleUtil.log(x, Math.E));
        assertDouble(Math.log10(x), DoubleUtil.log(x, 10.0));
        assertDouble(1.0, DoubleUtil.log(2.0, 2.0));
    }

    @Test public void testLog2() {
        assertDouble(1.0,      DoubleUtil.log2(2.0));
        assertDouble(2.0,      DoubleUtil.log2(4.0));
        assertDouble(3.0,      DoubleUtil.log2(8.0));
        assertEquals(3.321928, DoubleUtil.log2(10.0), 0.000001);
    }

    @Test public void testNearest() {
        assertDouble(1.25,   DoubleUtil.nearest(4.0 / 3.0, 0.25));
        assertDouble(1.325,  DoubleUtil.nearest(4.0 / 3.0, 0.025));
        assertDouble(1.3325, DoubleUtil.nearest(4.0 / 3.0, 0.0025));

        assertDouble(-1.25,   DoubleUtil.nearest(-4.0 / 3.0, 0.25));
        assertDouble(-1.325,  DoubleUtil.nearest(-4.0 / 3.0, 0.025));
        assertDouble(-1.3325, DoubleUtil.nearest(-4.0 / 3.0, 0.0025));

        assertDouble(123456.0, DoubleUtil.nearest(123456.0, 1.0));
        assertDouble(123460.0, DoubleUtil.nearest(123456.0, 10.0));
        assertDouble(123500.0, DoubleUtil.nearest(123456.0, 100.0));

        assertDouble(-123456.0, DoubleUtil.nearest(-123456.0, 1.0));
        assertDouble(-123460.0, DoubleUtil.nearest(-123456.0, 10.0));
        assertDouble(-123500.0, DoubleUtil.nearest(-123456.0, 100.0));
    }

    @Test public void testParseDouble() {
        assertTrue(Double.isNaN(DoubleUtil.parseDouble("NA")));
    }

    @Test public void testRatio() {
	assertDouble( 0.5,  DoubleUtil.ratio( 1,  2));
	assertDouble( 0.5,  DoubleUtil.ratio(-1, -2));
	assertDouble(-1.25, DoubleUtil.ratio(-5,  4));
	assertDouble(-1.25, DoubleUtil.ratio( 5, -4));
    }

    @Test public void testReplaceNaN() {
        assertDouble(0.0, DoubleUtil.replaceNaN(Double.NaN, 0.0));
        assertDouble(1.0, DoubleUtil.replaceNaN(Double.NaN, 1.0));

        assertDouble(3.4, DoubleUtil.replaceNaN(3.4, 0.0));
        assertDouble(5.6, DoubleUtil.replaceNaN(5.6, 1.0));

        assertTrue(DoubleUtil.replaceNaN(Double.NEGATIVE_INFINITY, 0.0) == Double.NEGATIVE_INFINITY);
        assertTrue(DoubleUtil.replaceNaN(Double.POSITIVE_INFINITY, 1.0) == Double.POSITIVE_INFINITY);
    }

    @Test public void testRound() {
        assertDouble(0.3333, DoubleUtil.round(1.0 / 3.0, 4));
        assertDouble(0.67, DoubleUtil.round(2.0 / 3.0, 2));

        assertDouble(136130.0, DoubleUtil.round(136130.0, 6));
        assertDouble(136000.0, DoubleUtil.round(136130.0, -3));
    }

    @Test public void testTruncate() {
        assertEquals(-5, DoubleUtil.truncate(-5.1), DoubleComparator.EPSILON);
        assertEquals(-5, DoubleUtil.truncate(-5.0), DoubleComparator.EPSILON);
        assertEquals(-4, DoubleUtil.truncate(-4.9), DoubleComparator.EPSILON);

        assertEquals(-1, DoubleUtil.truncate(-1.1), DoubleComparator.EPSILON);
        assertEquals(-1, DoubleUtil.truncate(-1.0), DoubleComparator.EPSILON);
        assertEquals( 0, DoubleUtil.truncate(-0.9), DoubleComparator.EPSILON);

        assertEquals(0, DoubleUtil.truncate(-0.1), DoubleComparator.EPSILON);
        assertEquals(0, DoubleUtil.truncate(-0.0), DoubleComparator.EPSILON);
        assertEquals(0, DoubleUtil.truncate( 0.0), DoubleComparator.EPSILON);
        assertEquals(0, DoubleUtil.truncate( 0.1), DoubleComparator.EPSILON);

        assertEquals(0, DoubleUtil.truncate(0.9), DoubleComparator.EPSILON);
        assertEquals(1, DoubleUtil.truncate(1.0), DoubleComparator.EPSILON);
        assertEquals(1, DoubleUtil.truncate(1.1), DoubleComparator.EPSILON);

        assertEquals(7, DoubleUtil.truncate(7.9), DoubleComparator.EPSILON);
        assertEquals(8, DoubleUtil.truncate(8.0), DoubleComparator.EPSILON);
        assertEquals(8, DoubleUtil.truncate(8.8), DoubleComparator.EPSILON);
    }

    @Test public void testToArray() {
        ArrayList<Double> list = new ArrayList<Double>();
        double[] array = DoubleUtil.toArray(list);

        assertEquals(0, array.length);

        list.add(1.0);
        array = DoubleUtil.toArray(list);

        assertEquals(1, array.length);
        assertDouble(1.0, array[0]);

        list.add(3.3);
        list.add(8.8);
        array = DoubleUtil.toArray(list);

        assertEquals(3, array.length);
        assertDouble(1.0, array[0]);
        assertDouble(3.3, array[1]);
        assertDouble(8.8, array[2]);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DoubleUtilTest");
    }
}
