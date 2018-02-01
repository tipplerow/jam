
package jam.junit;

import java.util.List;

import jam.lattice.Coord;
import jam.lattice.Neighborhood;

import org.junit.*;
import static org.junit.Assert.*;

public class NeighborhoodTest {
    @Test public void testMoore() {
        assertEquals(26, Neighborhood.MOORE.size());
        assertEquals(26, Neighborhood.MOORE.viewBasis().size());

        List<Coord> neighbors = Neighborhood.MOORE.getNeighbors(Coord.at(1, 2, 3));
        assertEquals(26, neighbors.size());

        assertTrue(neighbors.contains(Coord.at(0, 1, 2)));
        assertTrue(neighbors.contains(Coord.at(1, 1, 2)));
        assertTrue(neighbors.contains(Coord.at(2, 1, 2)));

        assertTrue(neighbors.contains(Coord.at(0, 2, 2)));
        assertTrue(neighbors.contains(Coord.at(1, 2, 2)));
        assertTrue(neighbors.contains(Coord.at(2, 2, 2)));

        assertTrue(neighbors.contains(Coord.at(0, 3, 2)));
        assertTrue(neighbors.contains(Coord.at(1, 3, 2)));
        assertTrue(neighbors.contains(Coord.at(2, 3, 2)));

        assertTrue(neighbors.contains(Coord.at(0, 1, 3)));
        assertTrue(neighbors.contains(Coord.at(1, 1, 3)));
        assertTrue(neighbors.contains(Coord.at(2, 1, 3)));

        assertTrue(neighbors.contains(Coord.at(0, 2, 3)));
        assertTrue(neighbors.contains(Coord.at(2, 2, 3)));

        assertTrue(neighbors.contains(Coord.at(0, 3, 3)));
        assertTrue(neighbors.contains(Coord.at(1, 3, 3)));
        assertTrue(neighbors.contains(Coord.at(2, 3, 3)));

        assertTrue(neighbors.contains(Coord.at(0, 1, 4)));
        assertTrue(neighbors.contains(Coord.at(1, 1, 4)));
        assertTrue(neighbors.contains(Coord.at(2, 1, 4)));

        assertTrue(neighbors.contains(Coord.at(0, 2, 4)));
        assertTrue(neighbors.contains(Coord.at(1, 2, 4)));
        assertTrue(neighbors.contains(Coord.at(2, 2, 4)));

        assertTrue(neighbors.contains(Coord.at(0, 3, 4)));
        assertTrue(neighbors.contains(Coord.at(1, 3, 4)));
        assertTrue(neighbors.contains(Coord.at(2, 3, 4)));
    }

    @Test public void testVonNeumann() {
        assertEquals(6, Neighborhood.VON_NEUMANN.size());
        assertEquals(6, Neighborhood.VON_NEUMANN.viewBasis().size());

        List<Coord> neighbors = Neighborhood.VON_NEUMANN.getNeighbors(Coord.at(1, 2, 3));
        assertEquals(6, neighbors.size());

        assertTrue(neighbors.contains(Coord.at(1, 2, 2)));
        assertTrue(neighbors.contains(Coord.at(2, 2, 3)));
        assertTrue(neighbors.contains(Coord.at(1, 3, 3)));
        assertTrue(neighbors.contains(Coord.at(0, 2, 3)));
        assertTrue(neighbors.contains(Coord.at(1, 1, 3)));
        assertTrue(neighbors.contains(Coord.at(1, 2, 4)));
    }

    @Test public void testFirstNearest() {
        assertEquals(6, Neighborhood.FIRST_NEAREST.size());

        for (Coord coord : Neighborhood.FIRST_NEAREST)
            assertEquals(1, coord.getSquaredLength());
    }

    @Test public void testSecondNearest() {
        assertEquals(12, Neighborhood.SECOND_NEAREST.size());

        for (Coord coord : Neighborhood.SECOND_NEAREST)
            assertEquals(2, coord.getSquaredLength());
    }

    @Test public void testThirdNearest() {
        assertEquals(8, Neighborhood.THIRD_NEAREST.size());

        for (Coord coord : Neighborhood.THIRD_NEAREST)
            assertEquals(3, coord.getSquaredLength());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.NeighborhoodTest");
    }
}