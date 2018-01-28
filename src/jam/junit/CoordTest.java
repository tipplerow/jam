
package jam.junit;

import jam.lattice.Coord;

import org.junit.*;
import static org.junit.Assert.*;

public class CoordTest {
    @Test public void testComputeSquaredDistance() {
	Coord coord1 = Coord.at(1, 2, 3);
	Coord coord2 = Coord.at(1, 2, 4);
	Coord coord3 = Coord.at(1, 2, 2);
	Coord coord4 = Coord.at(2, 4, 6);

	assertEquals( 0, Coord.computeSquaredDistance(coord1, coord1));
	assertEquals( 1, Coord.computeSquaredDistance(coord1, coord2));
	assertEquals( 1, Coord.computeSquaredDistance(coord2, coord1));
	assertEquals( 1, Coord.computeSquaredDistance(coord1, coord3));
	assertEquals( 1, Coord.computeSquaredDistance(coord3, coord1));
	assertEquals(14, Coord.computeSquaredDistance(coord1, coord4));
	assertEquals(14, Coord.computeSquaredDistance(coord4, coord1));
    }

    @Test public void testGetSquaredLength() {
	assertEquals( 0, Coord.at(0, 0, 0).getSquaredLength());
	assertEquals( 1, Coord.at(1, 0, 0).getSquaredLength());
	assertEquals(14, Coord.at(1, 2, 3).getSquaredLength());
    }

    @Test public void testEquals() {
	Coord coord123A = Coord.at(1, 2, 3);
	Coord coord123B = Coord.at(1, 2, 3);
	Coord coord023  = Coord.at(0, 2, 3);
	Coord coord103  = Coord.at(1, 0, 3);
	Coord coord120  = Coord.at(1, 2, 0);

	assertFalse(coord123A.equals(null));

	assertTrue(coord123A.equals(coord123A));
	assertTrue(coord123A.equals(coord123B));

	assertFalse(coord123A.equals(coord023));
	assertFalse(coord123A.equals(coord103));
	assertFalse(coord123A.equals(coord120));
    }

    @Test public void testPlusMinus() {
	Coord coord1 = Coord.at( 1,  2,  3);
	Coord coord2 = Coord.at(10, 20, 30);
	Coord coord3 = Coord.at(11, 22, 33);

	assertEquals(coord3, coord1.plus(coord2));
	assertEquals(coord3, coord2.plus(coord1));

	assertEquals(coord1, coord3.minus(coord2));
	assertEquals(coord2, coord3.minus(coord1));
    }

    @Test public void testDot() {
	Coord ex = Coord.at(1, 0, 0);
	Coord ey = Coord.at(0, 1, 0);
	Coord ez = Coord.at(0, 0, 1);

	assertEquals(1, ex.dot(ex));
	assertEquals(0, ex.dot(ey));
	assertEquals(0, ex.dot(ez));

	assertEquals(0, ey.dot(ex));
	assertEquals(1, ey.dot(ey));
	assertEquals(0, ey.dot(ez));

	assertEquals(0, ez.dot(ex));
	assertEquals(0, ez.dot(ey));
	assertEquals(1, ez.dot(ez));
    }

    @Test public void testFormatParse() {
	Coord coord = Coord.at(-123456789, 987654321, 0);

	assertEquals(coord, Coord.parseCSV(coord.formatCSV()));
	assertEquals(coord, Coord.parseCSV("  -123456789,  987654321  ,  0 "));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CoordTest");
    }
}
