
package jam.junit;

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
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BravaisUnitCellTest");
    }
}
