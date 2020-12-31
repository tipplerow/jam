
package jam.space;

import jam.bravais.Lattice;
import jam.bravais.UnitIndex;
import jam.math.Point;

import org.junit.*;
import static org.junit.Assert.*;

public class SiteMapTest {
   @Test public void testSquare() {
        Lattice lattice = Lattice.parse("SQUARE; 2.0; 3, 4");
        SiteMap siteMap = SiteMap.create(lattice);

        assertEquals(12, siteMap.size());

        assertSite(siteMap, 0, 0, 0.0, 0.0);
        assertSite(siteMap, 1, 0, 2.0, 0.0);
        assertSite(siteMap, 2, 0, 4.0, 0.0);

        assertSite(siteMap, 0, 1, 0.0, 2.0);
        assertSite(siteMap, 1, 1, 2.0, 2.0);
        assertSite(siteMap, 2, 1, 4.0, 2.0);

        assertSite(siteMap, 0, 2, 0.0, 4.0);
        assertSite(siteMap, 1, 2, 2.0, 4.0);
        assertSite(siteMap, 2, 2, 4.0, 4.0);

        assertSite(siteMap, 0, 3, 0.0, 6.0);
        assertSite(siteMap, 1, 3, 2.0, 6.0);
        assertSite(siteMap, 2, 3, 4.0, 6.0);
    }

    private void assertSite(SiteMap siteMap, int ux, int uy, double px, double py) {
        assertEquals(Point.at(px, py), siteMap.require(UnitIndex.at(ux, uy)).getPoint());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.space.SiteMapTest");
    }
}
