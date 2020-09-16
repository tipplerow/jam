
package jam.bravais;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class BravaisUnitIndexTest {
    @Test public void testAtCoord() {
        assertEquals(3, UnitIndex.at(3).coord(0));

        assertEquals(3, UnitIndex.at(3, 5).coord(0));
        assertEquals(5, UnitIndex.at(3, 5).coord(1));

        assertEquals(3, UnitIndex.at(3, 5, 2).coord(0));
        assertEquals(5, UnitIndex.at(3, 5, 2).coord(1));
        assertEquals(2, UnitIndex.at(3, 5, 2).coord(2));
    }

    @Test public void testCompareTo() {
        UnitIndex i10 = UnitIndex.at(10);
        UnitIndex i20 = UnitIndex.at(20);
        UnitIndex i30 = UnitIndex.at(30);
        UnitIndex i40 = UnitIndex.at(40);
        UnitIndex i50 = UnitIndex.at(50);

        UnitIndex ij52 = UnitIndex.at(5, 2);
        UnitIndex ij43 = UnitIndex.at(4, 3);
        UnitIndex ij53 = UnitIndex.at(5, 3);
        UnitIndex ij63 = UnitIndex.at(6, 3);
        UnitIndex ij54 = UnitIndex.at(5, 4);

        UnitIndex ijk110 = UnitIndex.at(1, 1, 0);
        UnitIndex ijk101 = UnitIndex.at(1, 0, 1);
        UnitIndex ijk001 = UnitIndex.at(0, 1, 1);
        UnitIndex ijk111 = UnitIndex.at(1, 1, 1);
        UnitIndex ijk211 = UnitIndex.at(2, 1, 1);
        UnitIndex ijk121 = UnitIndex.at(1, 2, 1);
        UnitIndex ijk112 = UnitIndex.at(1, 1, 2);

        runCompareTest(i10, i20, i30, i40, i50,
                       ij52, ij43, ij53, ij63, ij54,
                       ijk110, ijk101, ijk001, ijk111, ijk211, ijk121, ijk112);
    }

    private void runCompareTest(UnitIndex... indexes) {
        List<UnitIndex> ordered = List.of(indexes);

        for (int k = 1; k < ordered.size(); ++k)
            assertTrue(ordered.get(k).compareTo(ordered.get(k - 1)) > 0);

        List<UnitIndex> shuffled = new ArrayList<UnitIndex>(ordered);
        Collections.shuffle(shuffled);

        List<UnitIndex> sorted = new ArrayList<UnitIndex>(shuffled);
        Collections.sort(sorted);

        assertEquals(ordered, sorted);
    }

    @Test public void testDimensionality() {
        assertEquals(1, UnitIndex.at(3).dimensionality());
        assertEquals(2, UnitIndex.at(3, 5).dimensionality());
        assertEquals(3, UnitIndex.at(3, 5, 2).dimensionality());
    }

    @Test public void testOrigin() {
        assertEquals(UnitIndex.at(0), UnitIndex.origin(1));
        assertEquals(UnitIndex.at(0, 0), UnitIndex.origin(2));
        assertEquals(UnitIndex.at(0, 0, 0), UnitIndex.origin(3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.bravais.BravaisUnitIndexTest");
    }
}
