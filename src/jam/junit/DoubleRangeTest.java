
package jam.junit;

import jam.math.DoubleRange;

import org.junit.*;
import static org.junit.Assert.*;

public class DoubleRangeTest extends NumericTestBase {
    private static final double LOWER = -2.0;
    private static final double UPPER =  1.0;

    private static final double SMALL = 1.0E-08;
    private static final double TINY  = 1.0E-15;

    @Test public void testClosed() {
        DoubleRange range = DoubleRange.closed(LOWER, UPPER);

	assertFalse(range.contains(LOWER - SMALL));
	assertTrue( range.contains(LOWER - TINY));
	assertTrue( range.contains(LOWER));
        assertTrue( range.contains(LOWER + TINY));
	assertTrue( range.contains(LOWER + SMALL));

	assertTrue( range.contains(UPPER - SMALL));
	assertTrue( range.contains(UPPER - TINY));
	assertTrue( range.contains(UPPER));
        assertTrue( range.contains(UPPER + TINY));
	assertFalse(range.contains(UPPER + SMALL));

        assertTrue( range.contains(0.0));
	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(3.0, range.getWidth());

        assertFalse(range.getLowerPredicate().test(-5.0));
        assertTrue( range.getUpperPredicate().test(-5.0));

        assertTrue( range.getLowerPredicate().test(5.0));
        assertFalse(range.getUpperPredicate().test(5.0));
    }

    @Test public void testLeftClosed() {
        DoubleRange range = DoubleRange.leftClosed(LOWER, UPPER);

	assertFalse(range.contains(LOWER - SMALL));
	assertTrue( range.contains(LOWER - TINY));
	assertTrue( range.contains(LOWER));
        assertTrue( range.contains(LOWER + TINY));
	assertTrue( range.contains(LOWER + SMALL));

	assertTrue( range.contains(UPPER - SMALL));
	assertFalse(range.contains(UPPER - TINY));
	assertFalse(range.contains(UPPER));
        assertFalse(range.contains(UPPER + TINY));
	assertFalse(range.contains(UPPER + SMALL));

        assertTrue( range.contains(0.0));
	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(3.0, range.getWidth());

        assertFalse(range.getLowerPredicate().test(-5.0));
        assertTrue( range.getUpperPredicate().test(-5.0));

        assertTrue( range.getLowerPredicate().test(5.0));
        assertFalse(range.getUpperPredicate().test(5.0));
    }

    @Test public void testLeftOpen() {
        DoubleRange range = DoubleRange.leftOpen(LOWER, UPPER);

	assertFalse(range.contains(LOWER - SMALL));
	assertFalse(range.contains(LOWER - TINY));
	assertFalse(range.contains(LOWER));
        assertFalse(range.contains(LOWER + TINY));
	assertTrue( range.contains(LOWER + SMALL));

	assertTrue( range.contains(UPPER - SMALL));
	assertTrue( range.contains(UPPER - TINY));
	assertTrue( range.contains(UPPER));
        assertTrue( range.contains(UPPER + TINY));
	assertFalse(range.contains(UPPER + SMALL));

        assertTrue( range.contains(0.0));
	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(3.0, range.getWidth());

        assertFalse(range.getLowerPredicate().test(-5.0));
        assertTrue( range.getUpperPredicate().test(-5.0));

        assertTrue( range.getLowerPredicate().test(5.0));
        assertFalse(range.getUpperPredicate().test(5.0));
    }

    @Test public void testOpen() {
        DoubleRange range = DoubleRange.open(LOWER, UPPER);

	assertFalse(range.contains(LOWER - SMALL));
	assertFalse(range.contains(LOWER - TINY));
	assertFalse(range.contains(LOWER));
        assertFalse(range.contains(LOWER + TINY));
	assertTrue( range.contains(LOWER + SMALL));

	assertTrue( range.contains(UPPER - SMALL));
	assertFalse(range.contains(UPPER - TINY));
	assertFalse(range.contains(UPPER));
        assertFalse(range.contains(UPPER + TINY));
	assertFalse(range.contains(UPPER + SMALL));

        assertTrue( range.contains(0.0));
	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(3.0, range.getWidth());

        assertFalse(range.getLowerPredicate().test(-5.0));
        assertTrue( range.getUpperPredicate().test(-5.0));

        assertTrue( range.getLowerPredicate().test(5.0));
        assertFalse(range.getUpperPredicate().test(5.0));
    }

    @Test public void testEmpty() {
        DoubleRange range = DoubleRange.EMPTY;

        assertFalse(range.contains(-1.0E+20));
        assertFalse(range.contains( 0.0));
        assertFalse(range.contains(+1.0E+20));

	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(0.0, range.getWidth());
    }

    @Test public void testFractional() {
        DoubleRange range = DoubleRange.FRACTIONAL;

        assertFalse(range.contains(-1.0E-08));
        assertTrue( range.contains(0.0));
        assertTrue( range.contains(1.0));
        assertFalse(range.contains(1.0 + 1.0E-08));

	assertFalse(range.contains(Double.NaN));
	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

        assertDouble(1.0, range.getWidth());
    }

    @Test public void testInfinite() {
        DoubleRange range = DoubleRange.INFINITE;

	assertTrue(range.contains(Double.NEGATIVE_INFINITY));
        assertTrue(range.contains(-1.0E+20));
        assertTrue(range.contains( 0.0));
        assertTrue(range.contains(+1.0E+20));
	assertTrue(range.contains(Double.POSITIVE_INFINITY));

	assertFalse(range.contains(Double.NaN));
        assertTrue(Double.isInfinite(range.getWidth()));
    }

    @Test public void testNegative() {
        DoubleRange range = DoubleRange.NEGATIVE;

	assertTrue( range.contains(Double.NEGATIVE_INFINITY));
        assertTrue( range.contains(-1.0E+20));
        assertTrue( range.contains(-1.0));
        assertFalse(range.contains( 0.0));
        assertFalse(range.contains(+1.0));
        assertFalse(range.contains(+1.0E+20));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

	assertFalse(range.contains(Double.NaN));
        assertTrue(Double.isInfinite(range.getWidth()));
    }

    @Test public void testNonNegative() {
        DoubleRange range = DoubleRange.NON_NEGATIVE;

	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
        assertFalse(range.contains(-1.0E+20));
        assertFalse(range.contains(-1.0));
        assertTrue( range.contains( 0.0));
        assertTrue( range.contains(+1.0));
        assertTrue( range.contains(+1.0E+20));
	assertTrue( range.contains(Double.POSITIVE_INFINITY));

	assertFalse(range.contains(Double.NaN));
        assertTrue(Double.isInfinite(range.getWidth()));
    }

    @Test public void testNonPositive() {
        DoubleRange range = DoubleRange.NON_POSITIVE;

	assertTrue( range.contains(Double.NEGATIVE_INFINITY));
        assertTrue( range.contains(-1.0E+20));
        assertTrue( range.contains(-1.0));
        assertTrue( range.contains( 0.0));
        assertFalse(range.contains(+1.0));
        assertFalse(range.contains(+1.0E+20));
	assertFalse(range.contains(Double.POSITIVE_INFINITY));

	assertFalse(range.contains(Double.NaN));
        assertTrue(Double.isInfinite(range.getWidth()));
    }

    @Test public void testPositive() {
        DoubleRange range = DoubleRange.POSITIVE;

	assertFalse(range.contains(Double.NEGATIVE_INFINITY));
        assertFalse(range.contains(-1.0E+20));
        assertFalse(range.contains(-1.0));
        assertFalse(range.contains( 0.0));
        assertTrue( range.contains(+1.0));
        assertTrue( range.contains(+1.0E+20));
	assertTrue( range.contains(Double.POSITIVE_INFINITY));

	assertFalse(range.contains(Double.NaN));
        assertTrue(Double.isInfinite(range.getWidth()));
    }

    @Test public void testValidateOkay() {
        DoubleRange.POSITIVE.validate(1.0);
        DoubleRange.NEGATIVE.validate(-1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateBad1() {
        DoubleRange.POSITIVE.validate(-1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateBad2() {
        DoubleRange.NEGATIVE.validate(1.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DoubleRangeTest");
    }
}
