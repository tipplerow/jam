
package jam.junit;

import java.util.List;

import jam.bravais.UnitCell;
import jam.bravais.UnitIndex;
import jam.math.Point;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisUnitCellTest {
    private static final double ROOT3 = Math.sqrt(3.0);

    @Test public void testLinear() {
        UnitCell cell = UnitCell.linear(2.5);

        assertEquals(UnitIndex.at(-1), cell.indexOf(Point.at(-1.26)));
        assertEquals(UnitIndex.at( 0), cell.indexOf(Point.at(-1.24)));
        assertEquals(UnitIndex.at( 0), cell.indexOf(Point.at( 0.0)));
        assertEquals(UnitIndex.at( 0), cell.indexOf(Point.at( 1.24)));
        assertEquals(UnitIndex.at( 1), cell.indexOf(Point.at( 1.26)));

        assertEquals(Point.at(-2.5), cell.pointAt(UnitIndex.at(-1)));
        assertEquals(Point.at( 0.0), cell.pointAt(UnitIndex.at( 0)));
        assertEquals(Point.at( 2.5), cell.pointAt(UnitIndex.at( 1)));

        assertEquals(2, cell.countNeighbors());
        assertEquals(2.5, cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    private void validateNeighbors(UnitCell cell) {
        UnitIndex index = UnitIndex.origin(cell.dimensionality());
        Point     point = cell.pointAt(index);

        for (UnitIndex neighbor : cell.getNeighbors(index))
            assertEquals(cell.getNeighborDistance(), point.distance(cell.pointAt(neighbor)), 1.0E-12);
    }

    @Test public void testHexagonal() {
        UnitCell cell = UnitCell.hexagonal(2.0);

        assertEquals(UnitIndex.at(1, 2), cell.indexOf(Point.at(0.49, 4.32)));
        assertEquals(UnitIndex.at(2, 2), cell.indexOf(Point.at(0.51, 4.32)));
        assertEquals(UnitIndex.at(1, 3), cell.indexOf(Point.at(0.49, 4.34)));
        assertEquals(UnitIndex.at(2, 3), cell.indexOf(Point.at(0.51, 4.34)));

        assertEquals(Point.at( 0.0, 2.0 * ROOT3), cell.pointAt(UnitIndex.at(1, 2)));
        assertEquals(Point.at( 2.0, 2.0 * ROOT3), cell.pointAt(UnitIndex.at(2, 2)));
        assertEquals(Point.at(-1.0, 3.0 * ROOT3), cell.pointAt(UnitIndex.at(1, 3)));
        assertEquals(Point.at( 1.0, 3.0 * ROOT3), cell.pointAt(UnitIndex.at(2, 3)));

        assertEquals(6, cell.countNeighbors());
        assertEquals(2.0, cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    @Test public void testSquare() {
        UnitCell cell = UnitCell.square(2.5);

        assertEquals(UnitIndex.at(1, 2), cell.indexOf(Point.at(3.74, 6.24)));
        assertEquals(UnitIndex.at(2, 2), cell.indexOf(Point.at(3.76, 6.24)));
        assertEquals(UnitIndex.at(1, 3), cell.indexOf(Point.at(3.74, 6.26)));
        assertEquals(UnitIndex.at(2, 3), cell.indexOf(Point.at(3.76, 6.26)));

        assertEquals(Point.at(2.5, 5.0), cell.pointAt(UnitIndex.at(1, 2)));
        assertEquals(Point.at(5.0, 5.0), cell.pointAt(UnitIndex.at(2, 2)));
        assertEquals(Point.at(2.5, 7.5), cell.pointAt(UnitIndex.at(1, 3)));
        assertEquals(Point.at(5.0, 7.5), cell.pointAt(UnitIndex.at(2, 3)));

        assertEquals(4, cell.countNeighbors());
        assertEquals(2.5, cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    @Test public void testCubic() {
        UnitCell cell = UnitCell.cubic(2.5);

        assertEquals(UnitIndex.at(1, 2, 3), cell.indexOf(Point.at(3.74, 6.24, 8.74)));
        assertEquals(UnitIndex.at(2, 2, 3), cell.indexOf(Point.at(3.76, 6.24, 8.74)));
        assertEquals(UnitIndex.at(2, 3, 3), cell.indexOf(Point.at(3.76, 6.26, 8.74)));
        assertEquals(UnitIndex.at(2, 3, 4), cell.indexOf(Point.at(3.76, 6.26, 8.76)));

        assertEquals(Point.at( 2.5,  5.0,  7.5), cell.pointAt(UnitIndex.at( 1,  2, 3)));
        assertEquals(Point.at( 5.0,  5.0,  5.0), cell.pointAt(UnitIndex.at( 2,  2, 2)));
        assertEquals(Point.at(-2.5, -7.5, 15.0), cell.pointAt(UnitIndex.at(-1, -3, 6)));
        assertEquals(Point.at(-5.0, -2.5,  0.0), cell.pointAt(UnitIndex.at(-2, -1, 0)));

        assertEquals(6, cell.countNeighbors());
        assertEquals(2.5, cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    @Test public void testBCC() {
        UnitCell cell = UnitCell.BCC(2.0);

        assertEquals(8, cell.countNeighbors());
        assertEquals(Math.sqrt(3.0), cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    @Test public void testFCC() {
        UnitCell cell = UnitCell.FCC(2.0);

        assertEquals(12, cell.countNeighbors());
        assertEquals(Math.sqrt(2.0), cell.getNeighborDistance(), 1.0E-12);

        validateNeighbors(cell);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BravaisUnitCellTest");
    }
}
