
package jam.bravais;

import java.util.List;
import java.util.Map;

import jam.bravais.CoordType;
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

        assertFalse(lattice.contains(pointM));
        assertTrue(lattice.contains(point0));
        assertTrue(lattice.contains(point1));
        assertTrue(lattice.contains(point2));
        assertTrue(lattice.contains(point3));
        assertTrue(lattice.contains(point4));
        assertFalse(lattice.contains(point5));

        UnitIndex indexM = UnitIndex.at(-1);
        UnitIndex index0 = UnitIndex.at(0);
        UnitIndex index1 = UnitIndex.at(1);
        UnitIndex index2 = UnitIndex.at(2);
        UnitIndex index3 = UnitIndex.at(3);
        UnitIndex index4 = UnitIndex.at(4);
        UnitIndex index5 = UnitIndex.at(5);

        Map<UnitIndex, List<UnitIndex>> neighbors =
            lattice.mapIndexNeighbors(CoordType.ABSOLUTE);

        assertEquals(List.of(indexM, index1), neighbors.get(index0));
        assertEquals(List.of(index0, index2), neighbors.get(index1));
        assertEquals(List.of(index1, index3), neighbors.get(index2));
        assertEquals(List.of(index2, index4), neighbors.get(index3));
        assertEquals(List.of(index3, index5), neighbors.get(index4));

        neighbors = lattice.mapIndexNeighbors(CoordType.IMAGE);

        assertEquals(List.of(index4, index1), neighbors.get(index0));
        assertEquals(List.of(index0, index2), neighbors.get(index1));
        assertEquals(List.of(index1, index3), neighbors.get(index2));
        assertEquals(List.of(index2, index4), neighbors.get(index3));
        assertEquals(List.of(index3, index0), neighbors.get(index4));
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

        assertTrue(lattice.contains(p00));
        assertTrue(lattice.contains(p01));
        assertTrue(lattice.contains(p02));
        assertTrue(lattice.contains(p10));
        assertTrue(lattice.contains(p11));
        assertTrue(lattice.contains(p12));

        assertFalse(lattice.contains(Point.at(-1.0, 0.0)));
        assertFalse(lattice.contains(Point.at( 0.0, 3.0)));

        lattice = Lattice.parse("SQUARE; 2.5; 8, 4");

        Map<UnitIndex, List<UnitIndex>> neighbors =
            lattice.mapIndexNeighbors(CoordType.ABSOLUTE);

        assertEquals(List.of(UnitIndex.at( 0, -1),
                             UnitIndex.at(-1,  0),
                             UnitIndex.at( 1,  0),
                             UnitIndex.at( 0,  1)),
                     neighbors.get(UnitIndex.at(0, 0)));

        neighbors = lattice.mapIndexNeighbors(CoordType.IMAGE);

        assertEquals(List.of(UnitIndex.at(0, 3),
                             UnitIndex.at(7, 0),
                             UnitIndex.at(1, 0),
                             UnitIndex.at(0, 1)),
                     neighbors.get(UnitIndex.at(0, 0)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.bravais.BravaisLatticeTest");
    }
}
