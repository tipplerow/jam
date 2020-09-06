
package jam.junit;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import jam.math.UnitIndex;

import org.junit.*;
import static org.junit.Assert.*;

public class UnitIndexTest {
    @Test public void testComparator() {
        UnitIndex u1a = UnitIndex.instance(33);
        UnitIndex u1b = UnitIndex.instance(33);
        UnitIndex u2  = UnitIndex.instance(88);

        assertTrue(UnitIndex.COMPARATOR.compare(u1a, u1a) == 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u1a, u1b) == 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u1a, u2)   < 0);

        assertTrue(UnitIndex.COMPARATOR.compare(u1b, u1a) == 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u1b, u1b) == 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u1b, u2)   < 0);

        assertTrue(UnitIndex.COMPARATOR.compare(u2, u1a) > 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u2, u1b) > 0);
        assertTrue(UnitIndex.COMPARATOR.compare(u2, u2) == 0);
    }

    @Test public void testEquals() {
        UnitIndex u1a = UnitIndex.instance(1);
        UnitIndex u1b = UnitIndex.instance(1);
        UnitIndex u2  = UnitIndex.instance(2);

        assertTrue(u1a.equals(u1a));
        assertTrue(u1a.equals(u1b));
        assertFalse(u1a.equals(u2));

        assertTrue(u1b.equals(u1a));
        assertTrue(u1b.equals(u1b));
        assertFalse(u1b.equals(u2));

        assertFalse(u2.equals(u1a));
        assertFalse(u2.equals(u1b));
        assertTrue(u2.equals(u2));
    }

    @Test public void testFirst() {
        assertEquals(0, UnitIndex.first().getListIndex());
        assertEquals(1, UnitIndex.first().getUnitIndex());
    }

    @Test public void testGet() {
        String[] array = new String[] { "A", "B", "C", "D", "E" };
        List<String> list = List.of("A", "B", "C", "D", "E");

        UnitIndex u1 = UnitIndex.instance(1);
        UnitIndex u5 = UnitIndex.instance(5);

        assertEquals("A", u1.get(array));
        assertEquals("E", u5.get(array));

        assertEquals("A", u1.get(list));
        assertEquals("E", u5.get(list));
    }

    @Test public void testHashCode() {
        assertEquals(33, UnitIndex.instance(33).hashCode());
        assertEquals(88, UnitIndex.instance(88).hashCode());
    }

    @Test public void testIndexing() {
        UnitIndex u33 = UnitIndex.instance(33);
        UnitIndex u88 = UnitIndex.instance(88);

        assertEquals(32, u33.getListIndex());
        assertEquals(33, u33.getUnitIndex());

        assertEquals(87, u88.getListIndex());
        assertEquals(88, u88.getUnitIndex());
    }

    @Test public void testInequalities() {
        UnitIndex u10 = UnitIndex.instance(10);
        UnitIndex u20 = UnitIndex.instance(20);
        UnitIndex u30 = UnitIndex.instance(30);

        assertFalse(u20.LT(u10));
        assertFalse(u20.LT(u20));
        assertTrue( u20.LT(u30));

        assertFalse(u20.LE(u10));
        assertTrue( u20.LE(u20));
        assertTrue( u20.LE(u30));

        assertFalse(u20.EQ(u10));
        assertTrue( u20.EQ(u20));
        assertFalse(u20.EQ(u30));

        assertTrue( u20.GE(u10));
        assertTrue( u20.GE(u20));
        assertFalse(u20.GE(u30));

        assertTrue( u20.GT(u10));
        assertFalse(u20.GT(u20));
        assertFalse(u20.GT(u30));
    }

    @Test public void testIsFirst() {
        assertTrue(UnitIndex.instance(1).isFirst());
        assertFalse(UnitIndex.instance(2).isFirst());
        assertFalse(UnitIndex.instance(222).isFirst());
    }

    @Test public void testIsIndexOf() {
        String[] array = new String[] { "A", "B", "C", "D", "E" };
        List<String> list = List.of("A", "B", "C", "D", "E");

        assertTrue(UnitIndex.instance(5).isIndexOf(array));
        assertTrue(UnitIndex.instance(5).isIndexOf(list));

        assertFalse(UnitIndex.instance(6).isIndexOf(array));
        assertFalse(UnitIndex.instance(6).isIndexOf(list));
    }

    @Test public void testMinus() {
        UnitIndex u7 = UnitIndex.instance(7);
        UnitIndex u2 = u7.minus(5);
        UnitIndex u9 = u7.minus(-2);
        
        assertEquals(7, u7.getUnitIndex());
        assertEquals(2, u2.getUnitIndex());
        assertEquals(9, u9.getUnitIndex());
    }

    @Test(expected = RuntimeException.class)
    public void testMinusInvalid() {
        UnitIndex.instance(10).minus(20);
    }

    @Test public void testNext() {
        UnitIndex base  = UnitIndex.instance(7);
        UnitIndex next1 = base.next();
        UnitIndex next2 = next1.next();

        assertEquals(7, base.getUnitIndex());
        assertEquals(8, next1.getUnitIndex());
        assertEquals(9, next2.getUnitIndex());
    }

    @Test public void testParse() {
        assertEquals(UnitIndex.instance(33), UnitIndex.parse("33"));
        assertEquals(UnitIndex.instance(88), UnitIndex.parse("  88  "));
    }

    @Test public void testPlus() {
        UnitIndex u7 = UnitIndex.instance(7);
        UnitIndex u2 = u7.plus(-5);
        UnitIndex u9 = u7.plus(2);
        
        assertEquals(7, u7.getUnitIndex());
        assertEquals(2, u2.getUnitIndex());
        assertEquals(9, u9.getUnitIndex());
    }

    @Test(expected = RuntimeException.class)
    public void testPlusInvalid() {
        UnitIndex.instance(10).plus(-20);
    }

    @Test public void testPrev() {
        UnitIndex base  = UnitIndex.instance(7);
        UnitIndex prev1 = base.prev();
        UnitIndex prev2 = prev1.prev();

        assertEquals(7, base.getUnitIndex());
        assertEquals(6, prev1.getUnitIndex());
        assertEquals(5, prev2.getUnitIndex());
    }

    @Test(expected = RuntimeException.class)
    public void testPrevInvalid() {
        UnitIndex.instance(1).prev();
    }

    @Test public void testSet() {
        String[] array = new String[] { "A", "B", "C", "D", "E" };
        List<String> list = new ArrayList<String>(List.of("A", "B", "C", "D", "E"));

        UnitIndex u1 = UnitIndex.instance(1);
        UnitIndex u5 = UnitIndex.instance(5);

        u1.set(array, "Q");
        u5.set(array, "P");

        assertEquals(List.of("Q", "B", "C", "D", "P"), Arrays.asList(array));

        assertEquals("A", u1.set(list, "Q"));
        assertEquals("E", u5.set(list, "P"));

        assertEquals(List.of("Q", "B", "C", "D", "P"), list);
    }

    @Test(expected = RuntimeException.class)
    public void testNegative() {
        UnitIndex ui = UnitIndex.instance(-77);
    }

    @Test(expected = RuntimeException.class)
    public void testZero() {
        UnitIndex u0 = UnitIndex.instance(0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.UnitIndexTest");
    }
}
