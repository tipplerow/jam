
package jam.lattice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.junit.NumericTestBase;
import jam.util.MultisetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class NeighborhoodTest extends NumericTestBase {
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

    @Test public void testRandomBasisVector() {
        Multiset<Coord> coords = HashMultiset.create();

        for (int trial = 0; trial < 1000000; ++trial)
            coords.add(Neighborhood.MOORE.randomBasisVector(random()));

        for (Coord coord : Neighborhood.MOORE.viewBasis())
            assertEquals(1.0 / 26.0, MultisetUtil.frequency(coords, coord), 0.001);
    }

    @Test public void testRandomNeighbors() {
        for (int trial = 0; trial < 100; ++trial) {
            for (int count = 0; count <= 26; ++ count) {
                Set<Coord> neighbors =
                    new HashSet<Coord>(Neighborhood.MOORE.randomNeighbors(Coord.ORIGIN, count, random()));

                // Assert that all neighbors are unique...
                assertEquals(count, neighbors.size());
            }
        }
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
        org.junit.runner.JUnitCore.main("jam.lattice.NeighborhoodTest");
    }
}
