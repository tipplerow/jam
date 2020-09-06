
package jam.junit;

import java.util.Comparator;

import jam.math.UnitIndex;
import jam.math.UnitIndexRange;

import org.junit.*;
import static org.junit.Assert.*;

public class UnitIndexRangeTest {
    @Test public void testBackward() {
        UnitIndex lower = UnitIndex.instance(10);
        UnitIndex upper = UnitIndex.instance(20);

        UnitIndexRange range = UnitIndexRange.backward(upper, 11);

        assertEquals(11, range.size());
        assertEquals(lower, range.lower());
        assertEquals(upper, range.upper());
    }

    @Test public void testBasic() {
        UnitIndexRange range = UnitIndexRange.instance(1, 20);

        assertEquals(UnitIndex.instance(1), range.lower());
        assertEquals(UnitIndex.instance(20), range.upper());
        assertEquals(20, range.size());

        assertFalse(range.contains(0));
        assertTrue(range.contains(1));
        assertTrue(range.contains(UnitIndex.instance(20)));
        assertFalse(range.contains(UnitIndex.instance(21)));
        assertFalse(range.contains(22));
    }

    @Test public void testBoundComparator() {
        UnitIndexRange r1 = UnitIndexRange.instance( 1,  5);
        UnitIndexRange r2 = UnitIndexRange.instance(10, 50);
        UnitIndexRange r3 = UnitIndexRange.instance(20, 40);
        UnitIndexRange r4 = UnitIndexRange.instance(20, 60);
        UnitIndexRange r5 = UnitIndexRange.instance(70, 90);

        Comparator<UnitIndexRange> cmp = UnitIndexRange.BOUND_COMPARATOR;

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

    @Test public void testEquals() {
        UnitIndexRange r1 = UnitIndexRange.instance(5, 20);
        UnitIndexRange r2 = UnitIndexRange.instance(5, 20);
        UnitIndexRange r3 = UnitIndexRange.instance(5, 30);
        UnitIndexRange r4 = UnitIndexRange.instance(15, 20);

        assertTrue(r1.equals(r1));
        assertTrue(r1.equals(r2));
        assertFalse(r1.equals(r3));
        assertFalse(r1.equals(r4));

        assertTrue(r1.equals(5, 20));
        assertTrue(r2.equals(5, 20));
        assertTrue(r3.equals(5, 30));
        assertTrue(r4.equals(15, 20));

        assertFalse(r1.equals(6, 20));
        assertFalse(r1.equals(5, 21));
    }

    @Test public void testFirst() {
        UnitIndexRange r1 = UnitIndexRange.first(1);
        UnitIndexRange r2 = UnitIndexRange.first(8);

        assertEquals(1, r1.lower().getUnitIndex());
        assertEquals(1, r1.upper().getUnitIndex());
        assertEquals(1, r1.size());

        assertEquals(1, r2.lower().getUnitIndex());
        assertEquals(8, r2.upper().getUnitIndex());
        assertEquals(8, r2.size());
    }

    @Test public void testForward() {
        UnitIndex lower = UnitIndex.instance(10);
        UnitIndex upper = UnitIndex.instance(20);

        UnitIndexRange range = UnitIndexRange.forward(lower, 11);

        assertEquals(11, range.size());
        assertEquals(lower, range.lower());
        assertEquals(upper, range.upper());
    }

    @Test public void testHashCode() {
        UnitIndexRange r1 = UnitIndexRange.instance(5, 20);
        UnitIndexRange r2 = UnitIndexRange.instance(5, 20);
        UnitIndexRange r3 = UnitIndexRange.instance(5, 30);
        UnitIndexRange r4 = UnitIndexRange.instance(15, 20);

        assertTrue(r1.hashCode() == r2.hashCode());
        assertTrue(r1.hashCode() != r3.hashCode());
        assertTrue(r1.hashCode() != r4.hashCode());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidOrder() {
        UnitIndexRange range = UnitIndexRange.instance(11, 1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSign() {
        UnitIndexRange range = UnitIndexRange.instance(0, 10);
    }

    @Test public void testParse() {
        assertRange(1, 10, UnitIndexRange.parse("[1, 10]"));
    }

    private void assertRange(int lower, int upper, UnitIndexRange range) {
        assertEquals(lower, range.lower().getUnitIndex());
        assertEquals(upper, range.upper().getUnitIndex());
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid1() {
        UnitIndexRange.parse("1, 2");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid2() {
        UnitIndexRange.parse("[1, 2");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid3() {
        UnitIndexRange.parse("1, 2]");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid4() {
        UnitIndexRange.parse("[1 2]");
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalid5() {
        UnitIndexRange.parse("[1, 2, 3]");
    }

    @Test public void testShift() {
        UnitIndexRange range1 = UnitIndexRange.instance(10, 20);
        UnitIndexRange range2 = range1.shift(5);
        UnitIndexRange range3 = range1.shift(-3);

        assertTrue(range1.equals(10, 20));
        assertTrue(range2.equals(15, 25));
        assertTrue(range3.equals(7,  17));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.UnitIndexRangeTest");
    }
}
