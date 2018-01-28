
package jam.junit;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jam.math.IntRange;

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
        IntRange range = new IntRange(-10, 20);

        assertEquals(-10, range.lower());
        assertEquals( 20, range.upper());
        assertEquals( 31, range.size());

        assertFalse(range.contains(-11));
        assertTrue( range.contains(-10));
        assertTrue( range.contains( 20));
        assertFalse(range.contains( 21));
    }

    @Test public void testContainsDouble() {
        IntRange range = new IntRange(-10, 20);

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
        IntRange range = new IntRange(1, 0);
    }

    @Test public void testEquals() {
        IntRange r1 = new IntRange(-5, 20);
        IntRange r2 = new IntRange(-5, 20);
        IntRange r3 = new IntRange(-5, 10);
        IntRange r4 = new IntRange(15, 20);

        assertTrue(r1.equals(r1));
        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));
    }

    @Test public void testHashCode() {
        IntRange r1 = new IntRange(-5, 20);
        IntRange r2 = new IntRange(-5, 20);
        IntRange r3 = new IntRange(-5, 10);
        IntRange r4 = new IntRange(15, 20);

        assertTrue(r1.hashCode() == r2.hashCode());
        assertTrue(r1.hashCode() != r3.hashCode());
        assertTrue(r1.hashCode() != r4.hashCode());
    }

    @Test public void testIterator() {
        IntRange range = new IntRange(5, 7);
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
        IntRange range = new IntRange(5, 7);
        Iterator<Integer> iter = range.iterator();

        iter.next();
        iter.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorNoSuch() {
        IntRange range = new IntRange(5, 6);
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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IntRangeTest");
    }
}
