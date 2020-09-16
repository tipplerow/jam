
package jam.lattice;

import org.junit.*;
import static org.junit.Assert.*;

public class DistanceComparatorTest {
    private static final Coord REFERENCE = Coord.at(1, 2, 3);
    private static final DistanceComparator COMPARATOR = new DistanceComparator(REFERENCE);

    @Test public void testCompare() {
	Coord coord1 = REFERENCE;
	Coord coord2 = Coord.at(1, 2, 4);
	Coord coord3 = Coord.at(1, 2, 2);
	Coord coord4 = Coord.at(2, 4, 6);

	assertTrue(COMPARATOR.compare(coord1, REFERENCE) == 0);
	assertTrue(COMPARATOR.compare(REFERENCE, coord1) == 0);

	assertTrue(COMPARATOR.compare(coord1, coord2) < 0);
	assertTrue(COMPARATOR.compare(coord2, coord1) > 0);

	assertTrue(COMPARATOR.compare(coord1, coord3) < 0);
	assertTrue(COMPARATOR.compare(coord3, coord1) > 0);

	assertTrue(COMPARATOR.compare(coord1, coord4) < 0);
	assertTrue(COMPARATOR.compare(coord4, coord1) > 0);

	assertTrue(COMPARATOR.compare(coord2, coord3) == 0);
	assertTrue(COMPARATOR.compare(coord3, coord2) == 0);

	assertTrue(COMPARATOR.compare(coord2, coord4) < 0);
	assertTrue(COMPARATOR.compare(coord4, coord2) > 0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lattice.DistanceComparatorTest");
    }
}
