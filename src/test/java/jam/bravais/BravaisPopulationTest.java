
package jam.bravais;

import java.util.List;

import jam.lang.ObjectFactory;
import jam.math.Point;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisPopulationTest {
    @Test public void testBasic() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");
        Population<String> population = Population.empty(lattice);

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
        assertTrue(population.isEmpty());
        assertEquals(0, population.countOccupants());

        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("C"));
        assertFalse(population.contains("D"));

        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("C"));
        assertNull(population.indexOf("D"));

        assertNull(population.locate("A"));
        assertNull(population.locate("B"));
        assertNull(population.locate("C"));
        assertNull(population.locate("D"));

        assertFalse(population.isOccupied(pointA));
        assertFalse(population.isOccupied(pointB));
        assertFalse(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertFalse(population.isOccupied(indexA));
        assertFalse(population.isOccupied(indexB));
        assertFalse(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ------------------
        // Place occupant "A"
        // ------------------
        assertNull(population.place("A", pointA));

        assertFalse(population.isEmpty());
        assertEquals(1, population.countOccupants());

        assertTrue(population.contains("A"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("C"));
        assertFalse(population.contains("D"));

        assertEquals(indexA, population.indexOf("A"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("C"));
        assertNull(population.indexOf("D"));

        assertEquals(pointA, population.locate("A"));
        assertNull(population.locate("B"));
        assertNull(population.locate("C"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ------------------
        // Place occupant "B"
        // ------------------

        // Point "pointB" is the same absolute coordinate as "pointA"
        // so occupant "B" should replace occupant "A"...
        assertEquals("A", population.place("B", pointB));

        assertFalse(population.isEmpty());
        assertEquals(1, population.countOccupants());

        assertTrue(population.contains("B"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("C"));
        assertFalse(population.contains("D"));

        assertEquals(indexB, population.indexOf("B"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("C"));
        assertNull(population.indexOf("D"));

        assertEquals(pointB, population.locate("B"));
        assertNull(population.locate("A"));
        assertNull(population.locate("C"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ------------------
        // Place occupant "C"
        // ------------------

        // Point "pointC" has the same periodic image as "pointB"
        // so occupant "C" should replace occupant "B"...
        assertEquals("B", population.place("C", pointC));

        assertFalse(population.isEmpty());
        assertEquals(1, population.countOccupants());

        assertTrue(population.contains("C"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("D"));

        assertEquals(indexC, population.indexOf("C"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("D"));

        assertEquals(pointC, population.locate("C"));
        assertNull(population.locate("A"));
        assertNull(population.locate("B"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ------------------
        // Place occupant "D"
        // ------------------

        // Point "pointD" is distinct from "pointA", "pointB", and
        // "pointC" so occupant "C" should remain...
        assertNull(population.place("D", pointD));

        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("C"));
        assertTrue(population.contains("D"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));

        assertEquals(indexC, population.indexOf("C"));
        assertEquals(indexD, population.indexOf("D"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));

        assertEquals(pointC, population.locate("C"));
        assertEquals(pointD, population.locate("D"));
        assertNull(population.locate("A"));
        assertNull(population.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertTrue(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertTrue(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ----------------
        // Swap "C" and "D"
        // ----------------
        population.swap("C", "D");

        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("C"));
        assertTrue(population.contains("D"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));

        assertEquals(indexD, population.indexOf("C"));
        assertEquals(indexC, population.indexOf("D"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));

        assertEquals(pointD, population.locate("C"));
        assertEquals(pointC, population.locate("D"));
        assertNull(population.locate("A"));
        assertNull(population.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertTrue(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertTrue(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        population.swap("D", "C");
        
        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("C"));
        assertTrue(population.contains("D"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));

        assertEquals(indexC, population.indexOf("C"));
        assertEquals(indexD, population.indexOf("D"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));

        assertEquals(pointC, population.locate("C"));
        assertEquals(pointD, population.locate("D"));
        assertNull(population.locate("A"));
        assertNull(population.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertTrue(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertTrue(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));

        // ---------------------------
        // Place "D" at a new location
        // ---------------------------

        // Point "pointD2" is distinct from "pointA", "pointB", and
        // "pointC" so occupant "C" should remain...
        assertNull(population.place("D", pointD2));

        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("C"));
        assertTrue(population.contains("D"));
        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));

        assertEquals(indexC, population.indexOf("C"));
        assertEquals(indexD2, population.indexOf("D"));
        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));

        assertEquals(pointC, population.locate("C"));
        assertEquals(pointD2, population.locate("D"));
        assertNull(population.locate("A"));
        assertNull(population.locate("B"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertTrue(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertTrue(population.isOccupied(indexD2));

        // --------------------
        // Replace "D" with "A"
        // --------------------
        population.replace("D", "A");

        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("A"));
        assertTrue(population.contains("C"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("D"));

        assertEquals(indexD2, population.indexOf("A"));
        assertEquals(indexC, population.indexOf("C"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("D"));

        assertEquals(pointD2, population.locate("A"));
        assertEquals(pointC, population.locate("C"));
        assertNull(population.locate("B"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertTrue(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertTrue(population.isOccupied(indexD2));

        // ----------------
        // Remove occupants
        // ----------------

        // Removing occupants that are not present should have no
        // effect...
        population.remove("B");
        population.remove("D");

        assertFalse(population.isEmpty());
        assertEquals(2, population.countOccupants());

        assertTrue(population.contains("A"));
        assertTrue(population.contains("C"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("D"));

        assertEquals(indexD2, population.indexOf("A"));
        assertEquals(indexC, population.indexOf("C"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("D"));

        assertEquals(pointD2, population.locate("A"));
        assertEquals(pointC, population.locate("C"));
        assertNull(population.locate("B"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertTrue(population.isOccupied(pointA));
        assertTrue(population.isOccupied(pointB));
        assertTrue(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertTrue(population.isOccupied(pointD2));

        assertTrue(population.isOccupied(indexA));
        assertTrue(population.isOccupied(indexB));
        assertTrue(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertTrue(population.isOccupied(indexD2));

        population.remove("C");

        assertFalse(population.isEmpty());
        assertEquals(1, population.countOccupants());

        assertTrue(population.contains("A"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("C"));
        assertFalse(population.contains("D"));

        assertEquals(indexD2, population.indexOf("A"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("C"));
        assertNull(population.indexOf("D"));

        assertEquals(pointD2, population.locate("A"));
        assertNull(population.locate("B"));
        assertNull(population.locate("C"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertFalse(population.isOccupied(pointA));
        assertFalse(population.isOccupied(pointB));
        assertFalse(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertTrue(population.isOccupied(pointD2));

        assertFalse(population.isOccupied(indexA));
        assertFalse(population.isOccupied(indexB));
        assertFalse(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertTrue(population.isOccupied(indexD2));

        population.remove("A");

        assertTrue(population.isEmpty());
        assertEquals(0, population.countOccupants());

        assertFalse(population.contains("A"));
        assertFalse(population.contains("B"));
        assertFalse(population.contains("C"));
        assertFalse(population.contains("D"));

        assertNull(population.indexOf("A"));
        assertNull(population.indexOf("B"));
        assertNull(population.indexOf("C"));
        assertNull(population.indexOf("D"));

        assertNull(population.locate("A"));
        assertNull(population.locate("B"));
        assertNull(population.locate("C"));
        assertNull(population.locate("D"));

        // Points "pointA", "pointB", and "pointC" have identical
        // periodic images...
        assertFalse(population.isOccupied(pointA));
        assertFalse(population.isOccupied(pointB));
        assertFalse(population.isOccupied(pointC));
        assertFalse(population.isOccupied(pointD));
        assertFalse(population.isOccupied(pointD2));

        assertFalse(population.isOccupied(indexA));
        assertFalse(population.isOccupied(indexB));
        assertFalse(population.isOccupied(indexC));
        assertFalse(population.isOccupied(indexD));
        assertFalse(population.isOccupied(indexD2));
    }

    @Test public void testFillFactory() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");
        Population<Integer> population = Population.empty(lattice);

        // Ensure that this previous occupant is removed...
        Integer int88 = Integer.valueOf(88);

        assertTrue(population.isEmpty());
        assertFalse(population.isFull());
        assertNull(population.indexOf(int88));
        assertFalse(population.contains(int88));

        population.place(int88, UnitIndex.at(1, 2));

        assertFalse(population.isEmpty());
        assertFalse(population.isFull());
        assertEquals(UnitIndex.at(1, 2), population.indexOf(int88));
        assertTrue(population.contains(int88));

        population.fill(ObjectFactory.forInteger());

        assertFalse(population.isEmpty());
        assertTrue(population.isFull());
        assertNull(population.indexOf(int88));
        assertFalse(population.contains(int88));

        assertEquals(UnitIndex.at(0, 0), population.indexOf(Integer.valueOf(0)));
        assertEquals(UnitIndex.at(1, 0), population.indexOf(Integer.valueOf(1)));
        assertEquals(UnitIndex.at(2, 0), population.indexOf(Integer.valueOf(2)));
        assertEquals(UnitIndex.at(0, 1), population.indexOf(Integer.valueOf(3)));
        assertEquals(UnitIndex.at(1, 1), population.indexOf(Integer.valueOf(4)));
        assertEquals(UnitIndex.at(2, 1), population.indexOf(Integer.valueOf(5)));
        assertEquals(UnitIndex.at(0, 2), population.indexOf(Integer.valueOf(6)));
        assertEquals(UnitIndex.at(1, 2), population.indexOf(Integer.valueOf(7)));
        assertEquals(UnitIndex.at(2, 2), population.indexOf(Integer.valueOf(8)));
        assertEquals(UnitIndex.at(0, 3), population.indexOf(Integer.valueOf(9)));
        assertEquals(UnitIndex.at(1, 3), population.indexOf(Integer.valueOf(10)));
        assertEquals(UnitIndex.at(2, 3), population.indexOf(Integer.valueOf(11)));

        assertEquals(Integer.valueOf(0), population.occupantAt(UnitIndex.at(0, 0)));
        assertEquals(Integer.valueOf(1), population.occupantAt(UnitIndex.at(1, 0)));
        assertEquals(Integer.valueOf(2), population.occupantAt(UnitIndex.at(2, 0)));
        assertEquals(Integer.valueOf(3), population.occupantAt(UnitIndex.at(0, 1)));
        assertEquals(Integer.valueOf(4), population.occupantAt(UnitIndex.at(1, 1)));
        assertEquals(Integer.valueOf(5), population.occupantAt(UnitIndex.at(2, 1)));
        assertEquals(Integer.valueOf(6), population.occupantAt(UnitIndex.at(0, 2)));
        assertEquals(Integer.valueOf(7), population.occupantAt(UnitIndex.at(1, 2)));
        assertEquals(Integer.valueOf(8), population.occupantAt(UnitIndex.at(2, 2)));
        assertEquals(Integer.valueOf(9), population.occupantAt(UnitIndex.at(0, 3)));
        assertEquals(Integer.valueOf(10), population.occupantAt(UnitIndex.at(1, 3)));
        assertEquals(Integer.valueOf(11), population.occupantAt(UnitIndex.at(2, 3)));
    }

    @Test public void testFillCollection() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");
        Population<Integer> population = Population.empty(lattice);

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

        assertTrue(population.isEmpty());
        assertFalse(population.isFull());

        population.fill(occupants);

        assertFalse(population.isEmpty());
        assertTrue(population.isFull());

        assertTrue(population.containsAll(occupants));
        assertFalse(population.containsAll(List.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(33))));

        for (Integer occupant : occupants)
            assertTrue(population.contains(occupant));

        assertFalse(population.contains(Integer.valueOf(12)));
        assertFalse(population.contains(Integer.valueOf(13)));

        assertEquals(Point.at(0.0, 0.0), population.locate(occupants.get(0)));
        assertEquals(Point.at(1.0, 0.0), population.locate(occupants.get(1)));
        assertEquals(Point.at(2.0, 0.0), population.locate(occupants.get(2)));
        assertEquals(Point.at(0.0, 1.0), population.locate(occupants.get(3)));
        assertEquals(Point.at(1.0, 1.0), population.locate(occupants.get(4)));
        assertEquals(Point.at(2.0, 1.0), population.locate(occupants.get(5)));
        assertEquals(Point.at(0.0, 2.0), population.locate(occupants.get(6)));
        assertEquals(Point.at(1.0, 2.0), population.locate(occupants.get(7)));
        assertEquals(Point.at(2.0, 2.0), population.locate(occupants.get(8)));
        assertEquals(Point.at(0.0, 3.0), population.locate(occupants.get(9)));
        assertEquals(Point.at(1.0, 3.0), population.locate(occupants.get(10)));
        assertEquals(Point.at(2.0, 3.0), population.locate(occupants.get(11)));
    }

    @Test(expected = RuntimeException.class)
    public void testFillInvalid1() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");
        Population<Integer> population = Population.empty(lattice);

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

        population.fill(occupants);
    }

    @Test(expected = RuntimeException.class)
    public void testFillInvalid2() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 3, 4");
        Population<Integer> population = Population.empty(lattice);

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

        population.fill(occupants);
    }

    @Test public void testUnoccupiedNeighbors() {
        Lattice lattice = Lattice.parse("SQUARE; 1.0; 5, 4");
        Population<String> population = Population.empty(lattice);

        population.place("A", UnitIndex.at(1, 0));
        population.place("B", UnitIndex.at(2, 0));
        population.place("C", UnitIndex.at(3, 0));
        population.place("D", UnitIndex.at(2, 1));
        population.place("E", UnitIndex.at(3, 2));
        population.place("F", UnitIndex.at(0, 3));
        population.place("G", UnitIndex.at(2, 3));

        assertEquals(List.of(),
                     population.unoccupiedNeighbors(UnitIndex.at(2, 0)));

        assertEquals(List.of(UnitIndex.at(1, 2)),
                     population.unoccupiedNeighbors(UnitIndex.at(2, 2)));

        assertEquals(List.of(UnitIndex.at(-1, 0),
                             UnitIndex.at( 0, 1)),
                     population.unoccupiedNeighbors(UnitIndex.at(0, 0)));

        assertEquals(List.of(UnitIndex.at(1, 1),
                             UnitIndex.at(0, 2),
                             UnitIndex.at(2, 2),
                             UnitIndex.at(1, 3)),
                     population.unoccupiedNeighbors(UnitIndex.at(1, 2)));
    }

    @Test public void testLinear() {
        Lattice lattice = Lattice.parse("LINEAR; 2.0; 5");
        Population<String> population = Population.empty(lattice);

        population.place("A", UnitIndex.at(0));
        population.place("B", UnitIndex.at(1));
        population.place("C", UnitIndex.at(2));
        population.place("D", UnitIndex.at(3));
        population.place("E", UnitIndex.at(4));

        for (int imageIndex = -2; imageIndex <= 2; ++imageIndex) {
            assertEquals("A", population.occupantAt(UnitIndex.at(5 * imageIndex + 0)));
            assertEquals("B", population.occupantAt(UnitIndex.at(5 * imageIndex + 1)));
            assertEquals("C", population.occupantAt(UnitIndex.at(5 * imageIndex + 2)));
            assertEquals("D", population.occupantAt(UnitIndex.at(5 * imageIndex + 3)));
            assertEquals("E", population.occupantAt(UnitIndex.at(5 * imageIndex + 4)));
        }

        assertEquals(List.of("E", "B"), population.neighborsOf("A"));
        assertEquals(List.of("A", "C"), population.neighborsOf("B"));
        assertEquals(List.of("B", "D"), population.neighborsOf("C"));
        assertEquals(List.of("C", "E"), population.neighborsOf("D"));
        assertEquals(List.of("D", "A"), population.neighborsOf("E"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.bravais.BravaisPopulationTest");
    }
}
