
package jam.junit;

import java.util.List;

import jam.bravais.Lattice;
import jam.bravais.LatticeType;
import jam.bravais.Period;
import jam.bravais.UnitIndex;
import jam.math.Point;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisLatticeTest {
    @Test public void testBasic() {
        Lattice<String> lattice = LatticeType.parse("SQUARE; 1.0; 3, 4");

        assertEquals(2, lattice.period().dimensionality());
        assertEquals(3, lattice.period().period(0));
        assertEquals(4, lattice.period().period(1));

        assertEquals(2, lattice.unitCell().dimensionality());

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

        // -------------------------------------
        // Start with a completely empty lattice
        // -------------------------------------
        assertTrue(lattice.isEmpty());
        assertEquals(0, lattice.countOccupants());

        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("C"));
        assertFalse(lattice.contains("D"));

        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("C"));
        assertNull(lattice.indexOf("D"));

        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("C"));
        assertNull(lattice.locate("D"));

        assertFalse(lattice.isOccupied(pointA));
        assertFalse(lattice.isOccupied(pointB));
        assertFalse(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertFalse(lattice.isOccupied(indexA));
        assertFalse(lattice.isOccupied(indexB));
        assertFalse(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ------------------
        // Place occupant "A"
        // ------------------
        assertNull(lattice.place("A", pointA));

        assertFalse(lattice.isEmpty());
        assertEquals(1, lattice.countOccupants());

        assertTrue(lattice.contains("A"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("C"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexA, lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("C"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointA, lattice.locate("A"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("C"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ------------------
        // Place occupant "B"
        // ------------------

        // Point "pointB" is the same absolute coordinate as "pointA"
        // so occupant "B" should replace occupant "A"...
        assertEquals("A", lattice.place("B", pointB));

        assertFalse(lattice.isEmpty());
        assertEquals(1, lattice.countOccupants());

        assertTrue(lattice.contains("B"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("C"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexB, lattice.indexOf("B"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("C"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointB, lattice.locate("B"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("C"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ------------------
        // Place occupant "C"
        // ------------------

        // Point "pointC" has the same periodic image as "pointB"
        // so occupant "C" should replace occupant "B"...
        assertEquals("B", lattice.place("C", pointC));

        assertFalse(lattice.isEmpty());
        assertEquals(1, lattice.countOccupants());

        assertTrue(lattice.contains("C"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexC, lattice.indexOf("C"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointC, lattice.locate("C"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ------------------
        // Place occupant "D"
        // ------------------

        // Point "pointD" is distinct from "pointA", "pointB", and
        // "pointC" so occupant "C" should remain...
        assertNull(lattice.place("D", pointD));

        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("C"));
        assertTrue(lattice.contains("D"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));

        assertEquals(indexC, lattice.indexOf("C"));
        assertEquals(indexD, lattice.indexOf("D"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));

        assertEquals(pointC, lattice.locate("C"));
        assertEquals(pointD, lattice.locate("D"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertTrue(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertTrue(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ----------------
        // Swap "C" and "D"
        // ----------------
        lattice.swap("C", "D");

        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("C"));
        assertTrue(lattice.contains("D"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));

        assertEquals(indexD, lattice.indexOf("C"));
        assertEquals(indexC, lattice.indexOf("D"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));

        assertEquals(pointD, lattice.locate("C"));
        assertEquals(pointC, lattice.locate("D"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertTrue(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertTrue(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        lattice.swap("D", "C");
        
        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("C"));
        assertTrue(lattice.contains("D"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));

        assertEquals(indexC, lattice.indexOf("C"));
        assertEquals(indexD, lattice.indexOf("D"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));

        assertEquals(pointC, lattice.locate("C"));
        assertEquals(pointD, lattice.locate("D"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertTrue(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertTrue(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));

        // ---------------------------
        // Place "D" at a new location
        // ---------------------------

        // Point "pointD2" is distinct from "pointA", "pointB", and
        // "pointC" so occupant "C" should remain...
        assertNull(lattice.place("D", pointD2));

        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("C"));
        assertTrue(lattice.contains("D"));
        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));

        assertEquals(indexC, lattice.indexOf("C"));
        assertEquals(indexD2, lattice.indexOf("D"));
        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));

        assertEquals(pointC, lattice.locate("C"));
        assertEquals(pointD2, lattice.locate("D"));
        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertTrue(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertTrue(lattice.isOccupied(indexD2));

        // --------------------
        // Replace "D" with "A"
        // --------------------
        lattice.replace("D", "A");

        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("A"));
        assertTrue(lattice.contains("C"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexD2, lattice.indexOf("A"));
        assertEquals(indexC, lattice.indexOf("C"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointD2, lattice.locate("A"));
        assertEquals(pointC, lattice.locate("C"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertTrue(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertTrue(lattice.isOccupied(indexD2));

        // ----------------
        // Remove occupants
        // ----------------

        // Removing occupants that are not present should have no
        // effect...
        lattice.remove("B");
        lattice.remove("D");

        assertFalse(lattice.isEmpty());
        assertEquals(2, lattice.countOccupants());

        assertTrue(lattice.contains("A"));
        assertTrue(lattice.contains("C"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexD2, lattice.indexOf("A"));
        assertEquals(indexC, lattice.indexOf("C"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointD2, lattice.locate("A"));
        assertEquals(pointC, lattice.locate("C"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(lattice.isOccupied(pointA));
        assertTrue(lattice.isOccupied(pointB));
        assertTrue(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertTrue(lattice.isOccupied(pointD2));

        assertTrue(lattice.isOccupied(indexA));
        assertTrue(lattice.isOccupied(indexB));
        assertTrue(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertTrue(lattice.isOccupied(indexD2));

        lattice.remove("C");

        assertFalse(lattice.isEmpty());
        assertEquals(1, lattice.countOccupants());

        assertTrue(lattice.contains("A"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("C"));
        assertFalse(lattice.contains("D"));

        assertEquals(indexD2, lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("C"));
        assertNull(lattice.indexOf("D"));

        assertEquals(pointD2, lattice.locate("A"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("C"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertFalse(lattice.isOccupied(pointA));
        assertFalse(lattice.isOccupied(pointB));
        assertFalse(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertTrue(lattice.isOccupied(pointD2));

        assertFalse(lattice.isOccupied(indexA));
        assertFalse(lattice.isOccupied(indexB));
        assertFalse(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertTrue(lattice.isOccupied(indexD2));

        lattice.remove("A");

        assertTrue(lattice.isEmpty());
        assertEquals(0, lattice.countOccupants());

        assertFalse(lattice.contains("A"));
        assertFalse(lattice.contains("B"));
        assertFalse(lattice.contains("C"));
        assertFalse(lattice.contains("D"));

        assertNull(lattice.indexOf("A"));
        assertNull(lattice.indexOf("B"));
        assertNull(lattice.indexOf("C"));
        assertNull(lattice.indexOf("D"));

        assertNull(lattice.locate("A"));
        assertNull(lattice.locate("B"));
        assertNull(lattice.locate("C"));
        assertNull(lattice.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertFalse(lattice.isOccupied(pointA));
        assertFalse(lattice.isOccupied(pointB));
        assertFalse(lattice.isOccupied(pointC));
        assertFalse(lattice.isOccupied(pointD));
        assertFalse(lattice.isOccupied(pointD2));

        assertFalse(lattice.isOccupied(indexA));
        assertFalse(lattice.isOccupied(indexB));
        assertFalse(lattice.isOccupied(indexC));
        assertFalse(lattice.isOccupied(indexD));
        assertFalse(lattice.isOccupied(indexD2));
    }

    @Test public void testFill() {
        Lattice<Integer> lattice = LatticeType.parse("SQUARE; 1.0; 3, 4");

        List<Integer> occupants =
            List.of(Integer.valueOf(0),
                    Integer.valueOf(1),
                    Integer.valueOf(2),
                    Integer.valueOf(3),
                    Integer.valueOf(4),
                    Integer.valueOf(5),
                    Integer.valueOf(6),
                    Integer.valueOf(7),
                    Integer.valueOf(8),
                    Integer.valueOf(9),
                    Integer.valueOf(10),
                    Integer.valueOf(11));

        lattice.fill(occupants);

        for (Integer occupant : occupants)
            assertTrue(lattice.contains(occupant));

        assertFalse(lattice.contains(Integer.valueOf(12)));
        assertFalse(lattice.contains(Integer.valueOf(13)));

        assertEquals(Point.at(0.0, 0.0), lattice.locate(occupants.get(0)));
        assertEquals(Point.at(1.0, 0.0), lattice.locate(occupants.get(1)));
        assertEquals(Point.at(2.0, 0.0), lattice.locate(occupants.get(2)));
        assertEquals(Point.at(0.0, 1.0), lattice.locate(occupants.get(3)));
        assertEquals(Point.at(1.0, 1.0), lattice.locate(occupants.get(4)));
        assertEquals(Point.at(2.0, 1.0), lattice.locate(occupants.get(5)));
        assertEquals(Point.at(0.0, 2.0), lattice.locate(occupants.get(6)));
        assertEquals(Point.at(1.0, 2.0), lattice.locate(occupants.get(7)));
        assertEquals(Point.at(2.0, 2.0), lattice.locate(occupants.get(8)));
        assertEquals(Point.at(0.0, 3.0), lattice.locate(occupants.get(9)));
        assertEquals(Point.at(1.0, 3.0), lattice.locate(occupants.get(10)));
        assertEquals(Point.at(2.0, 3.0), lattice.locate(occupants.get(11)));
    }

    @Test(expected = RuntimeException.class)
    public void testFillInvalid1() {
        Lattice<Integer> lattice = LatticeType.parse("SQUARE; 1.0; 3, 4");

        List<Integer> occupants =
            List.of(Integer.valueOf(0),
                    Integer.valueOf(1),
                    Integer.valueOf(2),
                    Integer.valueOf(3),
                    Integer.valueOf(4),
                    Integer.valueOf(5),
                    Integer.valueOf(6),
                    Integer.valueOf(7),
                    Integer.valueOf(8),
                    Integer.valueOf(9),
                    Integer.valueOf(10));

        lattice.fill(occupants);
    }

    @Test(expected = RuntimeException.class)
    public void testFillInvalid2() {
        Lattice<Integer> lattice = LatticeType.parse("SQUARE; 1.0; 3, 4");

        List<Integer> occupants =
            List.of(Integer.valueOf(0),
                    Integer.valueOf(1),
                    Integer.valueOf(2),
                    Integer.valueOf(3),
                    Integer.valueOf(4),
                    Integer.valueOf(5),
                    Integer.valueOf(6),
                    Integer.valueOf(7),
                    Integer.valueOf(8),
                    Integer.valueOf(9),
                    Integer.valueOf(10),
                    Integer.valueOf(11),
                    Integer.valueOf(12));

        lattice.fill(occupants);
    }

    @Test public void testUnoccupiedNeighbors() {
        Lattice<String> lattice = LatticeType.parse("SQUARE; 1.0; 5, 4");

        lattice.place("A", UnitIndex.at(1, 0));
        lattice.place("B", UnitIndex.at(2, 0));
        lattice.place("C", UnitIndex.at(3, 0));
        lattice.place("D", UnitIndex.at(2, 1));
        lattice.place("E", UnitIndex.at(3, 2));
        lattice.place("F", UnitIndex.at(0, 3));
        lattice.place("G", UnitIndex.at(2, 3));

        assertEquals(List.of(),
                     lattice.unoccupiedNeighbors(UnitIndex.at(2, 0)));

        assertEquals(List.of(UnitIndex.at(1, 2)),
                     lattice.unoccupiedNeighbors(UnitIndex.at(2, 2)));

        assertEquals(List.of(UnitIndex.at(-1, 0),
                             UnitIndex.at( 0, 1)),
                     lattice.unoccupiedNeighbors(UnitIndex.at(0, 0)));

        assertEquals(List.of(UnitIndex.at(1, 1),
                             UnitIndex.at(0, 2),
                             UnitIndex.at(2, 2),
                             UnitIndex.at(1, 3)),
                     lattice.unoccupiedNeighbors(UnitIndex.at(1, 2)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.BravaisLatticeTest");
    }
}
