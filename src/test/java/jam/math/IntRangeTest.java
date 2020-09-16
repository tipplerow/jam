
package jam.math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.*;
import static org.junit.Assert.*;

public class IntRangeTest {
    @Test public void testAll() {
        IntRange range = IntRange.ALL;

        assertTrue(range.contains(-123456789));
        assertTrue(range.contains(-1));
        assertTrue(range.contains( 0));
        assertTrue(range.contains(+1));
        assertTrue(range.contains(+123456789));
    }

    @Test public void testBasic() {
        IntRange range = IntRange.instance(-10, 20);

        assertEquals(-10, range.lower());
        assertEquals( 20, range.upper());
        assertEquals( 31, range.size());

        assertFalse(range.contains(-11));
        assertTrue( range.contains(-10));
        assertTrue( range.contains( 20));
        assertFalse(range.contains( 21));
    }

    @Test public void testBoundComparator() {
        IntRange r1 = IntRange.instance( 1,  5);
        IntRange r2 = IntRange.instance(10, 50);
        IntRange r3 = IntRange.instance(20, 40);
        IntRange r4 = IntRange.instance(20, 60);
        IntRange r5 = IntRange.instance(70, 90);

        Comparator<IntRange> cmp = IntRange.BOUND_COMPARATOR;

        assertTrue(cmp.compare(r1, r1) == 0);
        assertTrue(cmp.compare(r1, r2)  < 0);
        assertTrue(cmp.compare(r1, r3)  < 0);
        assertTrue(cmp.compare(r1, r4)  < 0);
        assertTrue(cmp.compare(r1, r5)  < 0);

        assertTrue(cmp.compare(r2, r1)  > 0);
        assertTrue(cmp.compare(r2, r2) == 0);
        assertTrue(cmp.compare(r2, r3)  < 0);
        assertTrue(cmp.compare(r2, r4)  < 0);
        assertTrue(cmp.compare(r2, r5)  < 0);

        assertTrue(cmp.compare(r3, r1)  > 0);
        assertTrue(cmp.compare(r3, r2)  > 0);
        assertTrue(cmp.compare(r3, r3) == 0);
        assertTrue(cmp.compare(r3, r4)  < 0);
        assertTrue(cmp.compare(r3, r5)  < 0);

        assertTrue(cmp.compare(r4, r1)  > 0);
        assertTrue(cmp.compare(r4, r2)  > 0);
        assertTrue(cmp.compare(r4, r3)  > 0);
        assertTrue(cmp.compare(r4, r4) == 0);
        assertTrue(cmp.compare(r4, r5)  < 0);

        assertTrue(cmp.compare(r5, r1)  > 0);
        assertTrue(cmp.compare(r5, r2)  > 0);
        assertTrue(cmp.compare(r5, r3)  > 0);
        assertTrue(cmp.compare(r5, r4)  > 0);
        assertTrue(cmp.compare(r5, r5) == 0);
    }

    @Test public void testContainsDouble() {
        IntRange range = IntRange.instance(-10, 20);

        assertFalse(range.containsDouble(-99.0));
        assertFalse(range.containsDouble(-10.000001));
        assertTrue( range.containsDouble(-10.0));
        assertTrue( range.containsDouble(  0.0));
        assertTrue( range.containsDouble( 20.0));
        assertFalse(range.containsDouble( 20.000001));
        assertFalse(range.containsDouble(100.0));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalid() {
        IntRange range = IntRange.instance(1, 0);
    }

    @Test public void testEquals() {
        IntRange r1 = IntRange.instance(-5, 20);
        IntRange r2 = IntRange.instance(-5, 20);
        IntRange r3 = IntRange.instance(-5, 10);
        IntRange r4 = IntRange.instance(15, 20);

        assertTrue(r1.equals(r1));
        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));

        assertTrue(r1.equals(-5, 20));
        assertTrue(r2.equals(-5, 20));
        assertTrue(r3.equals(-5, 10));
        assertTrue(r4.equals(15, 20));

        assertFalse(r1.equals(-6, 20));
        assertFalse(r1.equals(-5, 21));
    }

    @Test public void testHashCode() {
        IntRange r1 = IntRange.instance(-5, 20);
        IntRange r2 = IntRange.instance(-5, 20);
        IntRange r3 = IntRange.instance(-5, 10);
        IntRange r4 = IntRange.instance(15, 20);

        assertTrue(r1.hashCode() == r2.hashCode());
        assertTrue(r1.hashCode() != r3.hashCode());
        assertTrue(r1.hashCode() != r4.hashCode());
    }

    @Test public void testIterator() {
        IntRange range = IntRange.instance(5, 7);
        Iterator<Integer> iter = range.iterator();
        
        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(5), iter.next());

        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(6), iter.next());

        assertTrue(iter.hasNext());
        assertEquals(Integer.valueOf(7), iter.next());

        assertFalse(iter.hasNext());
    }

    @Test(expected = RuntimeException.class)
    public void testIteratorRemove() {
        IntRange range = IntRange.instance(5, 7);
        Iterator<Integer> iter = range.iterator();

        iter.next();
        iter.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorNoSuch() {
        IntRange range = IntRange.instance(5, 6);
        Iterator<Integer> iter = range.iterator();

        iter.next();
        iter.next();
        iter.next();
    }

    @Test public void testNegative() {
        IntRange range = IntRange.NEGATIVE;

        assertTrue( range.contains(-123456789));
        assertTrue( range.contains(-1));
        assertFalse(range.contains( 0));
        assertFalse(range.contains(+1));
        assertFalse(range.contains(+123456789));
    }

    @Test public void testNonNegative() {
        IntRange range = IntRange.NON_NEGATIVE;

        assertFalse(range.contains(-123456789));
        assertFalse(range.contains(-1));
        assertTrue( range.contains( 0));
        assertTrue( range.contains(+1));
        assertTrue( range.contains(+123456789));
    }

    @Test public void testNonPositive() {
        IntRange range = IntRange.NON_POSITIVE;

        assertTrue( range.contains(-123456789));
        assertTrue( range.contains(-1));
        assertTrue( range.contains( 0));
        assertFalse(range.contains(+1));
        assertFalse(range.contains(+123456789));
    }

    @Test public void testPositive() {
        IntRange range = IntRange.POSITIVE;

        assertFalse(range.contains(-123456789));
        assertFalse(range.contains(-1));
        assertFalse(range.contains( 0));
        assertTrue( range.contains(+1));
        assertTrue( range.contains(+123456789));
    }

    @Test public void testValidateOkay() {
        IntRange.POSITIVE.validate(1);
        IntRange.NEGATIVE.validate(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateBad1() {
        IntRange.POSITIVE.validate(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateBad2() {
        IntRange.NEGATIVE.validate(1);
    }

    @Test public void testParse() {
        assertRange(-2, 3, IntRange.parse("[-2, 3]"));
        assertRange(0, 15, IntRange.parse("[0, 15]"));
    }

    private void assertRange(int lower, int upper, IntRange range) {
        assertEquals(lower, range.lower());
        assertEquals(upper, range.upper());
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid1() {
        IntRange.parse("1, 2");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid2() {
        IntRange.parse("[1, 2");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid3() {
        IntRange.parse("1, 2]");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid4() {
        IntRange.parse("[1 2]");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid5() {
        IntRange.parse("[1, 2, 3]");
    }

    @Test public void testShift() {
        IntRange range1 = IntRange.instance(5, 15);
        IntRange range2 = range1.shift(300);
        IntRange range3 = range1.shift(-30);

        assertEquals( 5, range1.lower());
        assertEquals(15, range1.upper());

        assertEquals(305, range2.lower());
        assertEquals(315, range2.upper());

        assertEquals(-25, range3.lower());
        assertEquals(-15, range3.upper());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.IntRangeTest");
    }
}
