
package jam.junit;

import jam.lattice.Coord;
import jam.lattice.Image;
import jam.lattice.Period;

import org.junit.*;
import static org.junit.Assert.*;

public class PeriodTest {
    @Test public void testComputeScalarImage() {
	assertEquals(9, Period.computeImage(-21, 10));
	assertEquals(0, Period.computeImage(-20, 10));
	assertEquals(1, Period.computeImage(-19, 10));

	assertEquals(9, Period.computeImage(-11, 10));
	assertEquals(0, Period.computeImage(-10, 10));
	assertEquals(1, Period.computeImage( -9, 10));

	assertEquals(8, Period.computeImage(-2, 10));
	assertEquals(9, Period.computeImage(-1, 10));
	assertEquals(0, Period.computeImage( 0, 10));
	assertEquals(1, Period.computeImage( 1, 10));
	assertEquals(2, Period.computeImage( 2, 10));

	assertEquals(9, Period.computeImage( 9, 10));
	assertEquals(0, Period.computeImage(10, 10));
	assertEquals(1, Period.computeImage(11, 10));

	assertEquals(9, Period.computeImage(19, 10));
	assertEquals(0, Period.computeImage(20, 10));
	assertEquals(1, Period.computeImage(21, 10));
    }

    @Test public void testComputeCoordImage() {
	Period period = new Period(10, 11, 12);

	Coord coord1 = new Coord(1, 2, 3);
	Image image1 = new Image(1, 2, 3);

	Coord coord2 = new Coord(11, 11, 11);
	Image image2 = new Image( 1,  0, 11);

	Coord coord3 = new Coord(-30, -30, -30);
	Image image3 = new Image(  0,   3,   6);

	assertEquals(image1, period.computeImage(coord1));
	assertEquals(image2, period.computeImage(coord2));
	assertEquals(image3, period.computeImage(coord3));
    }

    @Test public void testGetSiteCount() {
	Period period = new Period(10, 20, 30);
	assertEquals(6000, period.getSiteCount());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PeriodTest");
    }
}
