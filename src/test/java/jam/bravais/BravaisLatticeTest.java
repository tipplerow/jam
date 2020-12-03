
package jam.bravais;

import java.util.List;

import jam.math.Point;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisLatticeTest {
    @Test public void testLinear() {
        Lattice lattice = Lattice.parse("LINEAR; 2.0; 5");

        assertEquals(1, lattice.period().dimensionality());
        assertEquals(5, lattice.period().period(0));
        assertEquals(5, lattice.countSites());

        Point pointM = Point.at(-2.0);
        Point point0 = Point.at(0.0);
        Point point1 = Point.at(2.0);
        Point point2 = Point.at(4.0);
        Point point3 = Point.at(6.0);
        Point point4 = Point.at(8.0);
        Point point5 = Point.at(10.0);

        assertEquals(List.of(point0, point1, point2, point3, point4), lattice.listPoints());

        assertEquals(List.of(pointM, point1), lattice.listNeighbors(point0));
        assertEquals(List.of(point0, point2), lattice.listNeighbors(point1));
        assertEquals(List.of(point1, point3), lattice.listNeighbors(point2));
        assertEquals(List.of(point2, point4), lattice.listNeighbors(point3));
        assertEquals(List.of(point3, point5), lattice.listNeighbors(point4));
    }

   @Test public void testSquare() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");

        assertEquals(2, lattice.period().dimensionality());
        assertEquals(3, lattice.period().period(0));
        assertEquals(4, lattice.period().period(1));
        assertEquals(12, lattice.countSites());

        Point pointA  = Point.at( 4.0, 10.0);
        Point pointB  = Point.at( 4.0, 10.0);
        Point pointC  = Point.at(-5.0, 18.0);
        Point pointD  = Point.at( 6.0, -3.0);
        Point pointD2 = Point.at(-1.0, -1.0);

        UnitIndex indexA  = UnitIndex.at( 4, 10);
        UnitIndex indexB  = UnitIndex.at( 4, 10);
        UnitIndex indexC  = UnitIndex.at(-5, 18);
        UnitIndex indexD  = UnitIndex.at( 6, -3);
        UnitIndex indexD2 = UnitIndex.at(-1, -1);

        assertEquals(UnitIndex.at(1, 2), lattice.imageOf(indexA));
        assertEquals(UnitIndex.at(1, 2), lattice.imageOf(indexB));
        assertEquals(UnitIndex.at(1, 2), lattice.imageOf(indexC));
        assertEquals(UnitIndex.at(0, 1), lattice.imageOf(indexD));
        assertEquals(UnitIndex.at(2, 3), lattice.imageOf(indexD2));

        assertEquals(Point.at(1.0, 2.0), lattice.imageOf(pointA));
        assertEquals(Point.at(1.0, 2.0), lattice.imageOf(pointB));
        assertEquals(Point.at(1.0, 2.0), lattice.imageOf(pointC));
        assertEquals(Point.at(0.0, 1.0), lattice.imageOf(pointD));
        assertEquals(Point.at(2.0, 3.0), lattice.imageOf(pointD2));

        lattice = Lattice.parse("SQUARE; 0.5; 2, 3");

        Point p00 = Point.at(0.0, 0.0);
        Point p10 = Point.at(0.5, 0.0);
        Point p01 = Point.at(0.0, 0.5);
        Point p11 = Point.at(0.5, 0.5);
        Point p02 = Point.at(0.0, 1.0);
        Point p12 = Point.at(0.5, 1.0);
        Point p21 = Point.at(1.0, 0.5);

        assertEquals(List.of(p00, p10, p01, p11, p02, p12), lattice.listPoints());
        assertEquals(List.of(p10, p01, p21, p12), lattice.listNeighbors(p11));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.bravais.BravaisLatticeTest");
    }
}
