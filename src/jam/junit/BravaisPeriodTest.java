
package jam.junit;

import java.util.List;

import jam.bravais.Period;
import jam.bravais.UnitIndex;
import jam.util.ListUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisPeriodTest {
    @Test public void testCountSites() {
        assertEquals(  10, Period.box(10).countSites());
        assertEquals( 200, Period.box(10, 20).countSites());
        assertEquals(6000, Period.box(10, 20, 30).countSites());
    }

    @Test public void testEnumerate1() {
        List<UnitIndex> images = Period.box(3).enumerate();

        assertEquals(3, images.size());
        assertTrue(ListUtil.isSorted(images));

        assertUnitIndex(images.get(0), 0);
        assertUnitIndex(images.get(1), 1);
        assertUnitIndex(images.get(2), 2);
    }

    private void assertUnitIndex(UnitIndex actual, int... expected) {
        assertEquals(expected.length, actual.dimensionality());

        for (int dim = 0; dim < expected.length; ++dim)
            assertEquals(expected[dim], actual.coord(dim));
    }

    @Test public void testEnumerate2() {
        List<UnitIndex> images = Period.box(2, 3).enumerate();

        assertEquals(6, images.size());
        assertTrue(ListUtil.isSorted(images));

        assertUnitIndex(images.get(0), 0, 0);
        assertUnitIndex(images.get(1), 1, 0);
        assertUnitIndex(images.get(2), 0, 1);
        assertUnitIndex(images.get(3), 1, 1);
        assertUnitIndex(images.get(4), 0, 2);
        assertUnitIndex(images.get(5), 1, 2);
    }

    @Test public void testEnumerate3() {
        List<UnitIndex> images = Period.box(2, 3, 4).enumerate();

        assertEquals(24, images.size());
        assertTrue(ListUtil.isSorted(images));

        assertUnitIndex(images.get(0), 0, 0, 0);
        assertUnitIndex(images.get(1), 1, 0, 0);
        assertUnitIndex(images.get(2), 0, 1, 0);
        assertUnitIndex(images.get(3), 1, 1, 0);
        assertUnitIndex(images.get(4), 0, 2, 0);
        assertUnitIndex(images.get(5), 1, 2, 0);

        assertUnitIndex(images.get(6),  0, 0, 1);
        assertUnitIndex(images.get(7),  1, 0, 1);
        assertUnitIndex(images.get(8),  0, 1, 1);
        assertUnitIndex(images.get(9),  1, 1, 1);
        assertUnitIndex(images.get(10), 0, 2, 1);
        assertUnitIndex(images.get(11), 1, 2, 1);

        assertUnitIndex(images.get(12), 0, 0, 2);
        assertUnitIndex(images.get(13), 1, 0, 2);
        assertUnitIndex(images.get(14), 0, 1, 2);
        assertUnitIndex(images.get(15), 1, 1, 2);
        assertUnitIndex(images.get(16), 0, 2, 2);
        assertUnitIndex(images.get(17), 1, 2, 2);

        assertUnitIndex(images.get(18), 0, 0, 3);
        assertUnitIndex(images.get(19), 1, 0, 3);
        assertUnitIndex(images.get(20), 0, 1, 3);
        assertUnitIndex(images.get(21), 1, 1, 3);
        assertUnitIndex(images.get(22), 0, 2, 3);
        assertUnitIndex(images.get(23), 1, 2, 3);
    }

    @Test public void testImageOf() {
	assertEquals(9, Period.imageOf(-21, 10));
	assertEquals(0, Period.imageOf(-20, 10));
	assertEquals(1, Period.imageOf(-19, 10));

	assertEquals(9, Period.imageOf(-11, 10));
	assertEquals(0, Period.imageOf(-10, 10));
	assertEquals(1, Period.imageOf( -9, 10));

	assertEquals(8, Period.imageOf(-2, 10));
	assertEquals(9, Period.imageOf(-1, 10));
	assertEquals(0, Period.imageOf( 0, 10));
	assertEquals(1, Period.imageOf( 1, 10));
	assertEquals(2, Period.imageOf( 2, 10));

	assertEquals(9, Period.imageOf( 9, 10));
	assertEquals(0, Period.imageOf(10, 10));
	assertEquals(1, Period.imageOf(11, 10));

	assertEquals(9, Period.imageOf(19, 10));
	assertEquals(0, Period.imageOf(20, 10));
	assertEquals(1, Period.imageOf(21, 10));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BravaisPeriodTest");
    }
}
