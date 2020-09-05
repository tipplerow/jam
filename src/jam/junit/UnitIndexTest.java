
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

    @Test public void testParse() {
        assertEquals(UnitIndex.instance(33), UnitIndex.parse("33"));
        assertEquals(UnitIndex.instance(88), UnitIndex.parse("  88  "));
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
