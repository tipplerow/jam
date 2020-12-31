
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

        System.out.println(neighborMap);
    }

    private void assertSite(SiteMap siteMap, int ux, int uy, double px, double py) {
        assertEquals(Point.at(px, py), siteMap.require(UnitIndex.at(ux, uy)).getPoint());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.space.NeighborMapTest");
    }
}
