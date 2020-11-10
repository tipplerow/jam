
package jam.math;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class DoubleComparatorTest {
    @Test public void testCompare() {
        DoubleComparator comparator = new DoubleComparator(1.0e-12);

        assertEquals( 0, comparator.compare(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals(-1, comparator.compare(Double.NEGATIVE_INFINITY, -0.0));
        assertEquals(-1, comparator.compare(Double.NEGATIVE_INFINITY, +0.0));
        assertEquals(-1, comparator.compare(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(-1, comparator.compare(Double.NEGATIVE_INFINITY, Double.NaN));

        assertEquals( 1, comparator.compare(-0.0, Double.NEGATIVE_INFINITY));
        assertEquals( 0, comparator.compare(-0.0, -0.0));
        assertEquals( 0, comparator.compare(-0.0, +0.0));
        assertEquals(-1, comparator.compare(-0.0, Double.POSITIVE_INFINITY));
        assertEquals(-1, comparator.compare(-0.0, Double.NaN));

        assertEquals( 1, comparator.compare(+0.0, Double.NEGATIVE_INFINITY));
        assertEquals( 0, comparator.compare(+0.0, -0.0));
        assertEquals( 0, comparator.compare(+0.0, +0.0));
        assertEquals(-1, comparator.compare(+0.0, Double.POSITIVE_INFINITY));
        assertEquals(-1, comparator.compare(+0.0, Double.NaN));

        assertEquals( 1, comparator.compare(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals( 1, comparator.compare(Double.POSITIVE_INFINITY, -0.0));
        assertEquals( 1, comparator.compare(Double.POSITIVE_INFINITY, +0.0));
        assertEquals( 0, comparator.compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(-1, comparator.compare(Double.POSITIVE_INFINITY, Double.NaN));

        assertEquals(1, comparator.compare(Double.NaN, Double.NEGATIVE_INFINITY));
        assertEquals(1, comparator.compare(Double.NaN, -0.0));
        assertEquals(1, comparator.compare(Double.NaN, +0.0));
        assertEquals(1, comparator.compare(Double.NaN, Double.POSITIVE_INFINITY));
        assertEquals(0, comparator.compare(Double.NaN, Double.NaN));

        assertEquals(-1, comparator.compare(1.33333333,       4.0 / 3.0));
        assertEquals( 0, comparator.compare(1.33333333333333, 4.0 / 3.0));
        assertEquals( 1, comparator.compare(1.33333333444444, 4.0 / 3.0));
    }

    @Test public void testCustom() {
        assertEquals(DoubleComparator.DEFAULT_TOLERANCE, DoubleComparator.custom().getTolerance(), 1.0E-15);
        assertEquals(DoubleComparator.DEFAULT_TOLERANCE, DoubleComparator.custom(0.001).getTolerance(), 1.0E-15);

        assertEquals(100.0 * DoubleComparator.DEFAULT_TOLERANCE, 
                     DoubleComparator.custom(1.0, 10.0, 100.0).getTolerance(), 1.0E-12);
    }

    @Test public void testEquals() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertTrue(comparator.equals(1.3333, 4.0 / 3.0));
        assertFalse(comparator.equals(1.3335, 4.0 / 3.0));
    }

    @Test public void testIsInteger() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertTrue(comparator.isInteger(1.0 - 1.0E-14));
        assertTrue(comparator.isInteger(1.0));
        assertTrue(comparator.isInteger(1.0 + 1.0E-14));

        assertFalse(comparator.isInteger(0.00000001));
        assertFalse(comparator.isInteger(0.5));
        assertFalse(comparator.isInteger(0.99999999));
    }

    @Test public void testIsUnity() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertFalse(comparator.isUnity(Double.NEGATIVE_INFINITY));
        assertFalse(comparator.isUnity(-0.0));
        assertFalse(comparator.isUnity(0.0));
        assertFalse(comparator.isUnity(0.9999));
        assertFalse(comparator.isUnity(1.0001));
        assertFalse(comparator.isUnity(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isUnity(Double.NaN));

        assertTrue(comparator.isUnity(1.0));
        assertTrue(comparator.isUnity(1.0 + DoubleComparator.EPSILON));
        assertTrue(comparator.isUnity(1.0 - DoubleComparator.EPSILON));
    }

    @Test public void testIsZero() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertFalse(comparator.isZero(Double.NEGATIVE_INFINITY));
        assertFalse(comparator.isZero(-0.0002));
        assertTrue(comparator.isZero(-0.00009));
        assertTrue(comparator.isZero(-0.0));
        assertTrue(comparator.isZero(0.0));
        assertTrue(comparator.isZero(-0.00009));
        assertFalse(comparator.isZero(0.0002));
        assertFalse(comparator.isZero(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isZero(Double.NaN));
    }

    @Test public void testIsNonZero() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertTrue(comparator.isNonZero(Double.NEGATIVE_INFINITY));
        assertTrue(comparator.isNonZero(-0.0002));
        assertFalse(comparator.isNonZero(-0.00009));
        assertFalse(comparator.isNonZero(-0.0));
        assertFalse(comparator.isNonZero(0.0));
        assertFalse(comparator.isNonZero(-0.00009));
        assertTrue(comparator.isNonZero(0.0002));
        assertTrue(comparator.isNonZero(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isNonZero(Double.NaN));
    }

    @Test public void testIsPositive() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertFalse(comparator.isPositive(Double.NEGATIVE_INFINITY));
        assertFalse(comparator.isPositive(-0.0002));
        assertFalse(comparator.isPositive(-0.00009));
        assertFalse(comparator.isPositive(-0.0));
        assertFalse(comparator.isPositive(0.0));
        assertFalse(comparator.isPositive(-0.00009));
        assertTrue(comparator.isPositive(0.0002));
        assertTrue(comparator.isPositive(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isPositive(Double.NaN));
    }

    @Test public void testIsNegative() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertTrue(comparator.isNegative(Double.NEGATIVE_INFINITY));
        assertTrue(comparator.isNegative(-0.0002));
        assertFalse(comparator.isNegative(-0.00009));
        assertFalse(comparator.isNegative(-0.0));
        assertFalse(comparator.isNegative(0.0));
        assertFalse(comparator.isNegative(-0.00009));
        assertFalse(comparator.isNegative(0.0002));
        assertFalse(comparator.isNegative(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isNegative(Double.NaN));
    }

    @Test public void testIsNonPositive() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertTrue(comparator.isNonPositive(Double.NEGATIVE_INFINITY));
        assertTrue(comparator.isNonPositive(-0.0002));
        assertTrue(comparator.isNonPositive(-0.00009));
        assertTrue(comparator.isNonPositive(-0.0));
        assertTrue(comparator.isNonPositive(0.0));
        assertTrue(comparator.isNonPositive(-0.00009));
        assertFalse(comparator.isNonPositive(0.0002));
        assertFalse(comparator.isNonPositive(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isNonPositive(Double.NaN));
    }

    @Test public void testIsNonNegative() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertFalse(comparator.isNonNegative(Double.NEGATIVE_INFINITY));
        assertFalse(comparator.isNonNegative(-0.0002));
        assertTrue(comparator.isNonNegative(-0.00009));
        assertTrue(comparator.isNonNegative(-0.0));
        assertTrue(comparator.isNonNegative(0.0));
        assertTrue(comparator.isNonNegative(-0.00009));
        assertTrue(comparator.isNonNegative(0.0002));
        assertTrue(comparator.isNonNegative(Double.POSITIVE_INFINITY));
        assertFalse(comparator.isNonNegative(Double.NaN));
    }

    @Test public void testIsIncreasing() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertTrue(comparator.isIncreasing(1.0));
        assertTrue(comparator.isIncreasing(1.0, 1.1, 1.2));

        assertFalse(comparator.isIncreasing(1.0, 1.1, 1.1));
        assertFalse(comparator.isIncreasing(1.0, 0.9, 1.1));
    }

    @Test public void testIsNonDecreasing() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertTrue(comparator.isNonDecreasing(1.0));
        assertTrue(comparator.isNonDecreasing(1.0, 1.1, 1.2));
        assertTrue(comparator.isNonDecreasing(1.0, 1.0, 1.1, 1.1));

        assertFalse(comparator.isNonDecreasing(1.0, 0.9, 1.1));
    }

    @Test public void testIsNonIncreasing() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertTrue(comparator.isNonIncreasing(1.0));
        assertTrue(comparator.isNonIncreasing(1.0, 1.0, 0.9));
        assertTrue(comparator.isNonIncreasing(1.0, 1.0, 0.9, 0.9));

        assertFalse(comparator.isNonIncreasing(1.0, 0.9, 1.1));
    }

    @Test public void testIsDecreasing() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        assertTrue(comparator.isDecreasing(1.0));
        assertTrue(comparator.isDecreasing(1.0, 0.9, 0.8));

        assertFalse(comparator.isDecreasing(1.0, 1.0, 0.9, 0.9));
        assertFalse(comparator.isDecreasing(1.0, 0.9, 1.1));
    }

    @Test public void testInequalities() {
        DoubleComparator comparator = DoubleComparator.DEFAULT;

        double x = -1.0;
        double y =  1.0;

        assertFalse(comparator.LT(x, x));
        assertTrue(comparator.LE(x, x));
        assertTrue(comparator.EQ(x, x));
        assertFalse(comparator.NE(x, x));
        assertTrue(comparator.GE(x, x));
        assertFalse(comparator.GT(x, x));

        assertTrue(comparator.LT(x, y));
        assertTrue(comparator.LE(x, y));
        assertFalse(comparator.EQ(x, y));
        assertTrue(comparator.NE(x, y));
        assertFalse(comparator.GE(x, y));
        assertFalse(comparator.GT(x, y));

        assertFalse(comparator.LT(y, x));
        assertFalse(comparator.LE(y, x));
        assertFalse(comparator.EQ(y, x));
        assertTrue(comparator.NE(y, x));
        assertTrue(comparator.GE(y, x));
        assertTrue(comparator.GT(y, x));
    }

    @Test public void testSign() {
        DoubleComparator comparator = new DoubleComparator(0.0001);

        assertEquals(-1, comparator.sign(-1.0));
        assertEquals(-1, comparator.sign(-0.0002));
        assertEquals( 0, comparator.sign(-0.000001));
        assertEquals( 0, comparator.sign( 0.0));
        assertEquals( 0, comparator.sign( 0.000001));
        assertEquals( 1, comparator.sign( 0.0002));
        assertEquals( 1, comparator.sign( 1.0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.DoubleComparatorTest");
    }
}
