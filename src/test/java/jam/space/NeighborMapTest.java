
package jam.space;

import java.util.Set;

import jam.bravais.Lattice;
import jam.bravais.UnitIndex;
import jam.math.Point;

import org.junit.*;
import static org.junit.Assert.*;

public class NeighborMapTest {
   @Test public void testSquare() {
        Lattice lattice = Lattice.parse("SQUARE; 2.0; 5, 4");
        SiteMap siteMap = SiteMap.create(lattice);
        NeighborMap neighborMap = NeighborMap.create(lattice, siteMap);

        assertNeighbors(neighborMap,
                        siteMap.require(0, 0),
                        siteMap.require(0, 3),
                        siteMap.require(4, 0),
                        siteMap.require(1, 0),
                        siteMap.require(0, 1));

        assertNeighbors(neighborMap,
                        siteMap.require(2, 1),
                        siteMap.require(2, 0),
                        siteMap.require(1, 1),
                        siteMap.require(3, 1),
                        siteMap.require(2, 2));

        assertNeighbors(neighborMap,
                        siteMap.require(4, 3),
                        siteMap.require(4, 2),
                        siteMap.require(3, 3),
                        siteMap.require(0, 3),
                        siteMap.require(4, 0));
    }

    private void assertNeighbors(NeighborMap map, Site site, Site... neighbors) {
        assertEquals(Set.of(neighbors), map.getNeighbors(site));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.space.NeighborMapTest");
    }
}
