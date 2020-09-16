
package jam.lattice;

import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import static org.junit.Assert.*;

public class SiteTest {
    private static final String occ0 = "A";
    private static final String occ1 = "B"; 
    private static final String occ2 = "C";

    @Test public void testConstructors() {
	Site<String> empty = new Site<String>();
	assertEquals(0, empty.countOccupants());

	Site<String> single = new Site<String>(occ0);
	assertEquals(1, single.countOccupants());

	Site<String> multiple = new Site<String>(Arrays.asList(occ0, occ1, occ2, occ2, occ2));
	assertEquals(5, multiple.countOccupants());
	assertEquals(3, multiple.countUniqueOccupants());
    }

    @Test public void testOccupyVacate() {
	//
	// Start with an empty site...
	//
	Collection<String> occupants;
	Site<String> site = new Site<String>();

	assertEquals(0, site.countOccupants());
	assertTrue(site.isEmpty());
	assertFalse(site.isOccupied());

	assertFalse(site.contains(occ0));
	assertFalse(site.contains(occ1));
	assertFalse(site.contains(occ2));

	occupants = site.getOccupants();
	assertEquals(0, occupants.size());

	assertFalse(site.contains(occ0));
	assertFalse(site.containsOnly(occ0));

	// Add a bead...
	site.occupy(occ0);

	assertEquals(1, site.countOccupants());
	assertFalse(site.isEmpty());
	assertTrue(site.isOccupied());

	assertTrue(site.contains(occ0));
	assertFalse(site.contains(occ1));
	assertFalse(site.contains(occ2));

	occupants = site.getOccupants();
	assertEquals(1, occupants.size());
	assertTrue(occupants.contains(occ0));

	assertTrue(site.contains(occ0));
	assertTrue(site.containsOnly(occ0));

	// Add another...
	site.occupy(occ1);

	assertEquals(2, site.countOccupants());
	assertFalse(site.isEmpty());
	assertTrue(site.isOccupied());

	assertTrue(site.contains(occ0));
	assertTrue(site.contains(occ1));
	assertFalse(site.contains(occ2));

	occupants = site.getOccupants();
	assertEquals(2, occupants.size());
	assertTrue(occupants.contains(occ0));
	assertTrue(occupants.contains(occ1));

	assertTrue(site.contains(occ0));
	assertFalse(site.containsOnly(occ0));

	// Remove the first...
	site.vacate(occ0);

	assertEquals(1, site.countOccupants());
	assertFalse(site.isEmpty());
	assertTrue(site.isOccupied());

	assertFalse(site.contains(occ0));
	assertTrue(site.contains(occ1));
	assertFalse(site.contains(occ2));

	occupants = site.getOccupants();
	assertEquals(1, occupants.size());
	assertTrue(occupants.contains(occ1));

	// Remove the second...
	site.vacate(occ1);

	assertEquals(0, site.countOccupants());
	assertTrue(site.isEmpty());
	assertFalse(site.isOccupied());

	assertFalse(site.contains(occ0));
	assertFalse(site.contains(occ1));
	assertFalse(site.contains(occ2));

	occupants = site.getOccupants();
	assertEquals(0, occupants.size());

        // Add three copies of the first...
        site.occupy(occ0);
        site.occupy(occ0);
        site.occupy(occ0);

        assertEquals(3, site.countOccupants());
        assertEquals(1, site.countUniqueOccupants());

        assertTrue(site.contains(occ0));
        assertFalse(site.contains(occ1));

        assertTrue(site.containsOnly(occ0));
        assertFalse(site.containsOnly(occ1));

        // Remove one-by-one...
        site.vacate(occ0);
        assertEquals(2, site.countOccupants());
        assertEquals(1, site.countUniqueOccupants());

        assertTrue(site.contains(occ0));
        assertFalse(site.contains(occ1));

        assertTrue(site.containsOnly(occ0));
        assertFalse(site.containsOnly(occ1));

        site.vacate(occ0);
        assertEquals(1, site.countOccupants());
        assertEquals(1, site.countUniqueOccupants());

        assertTrue(site.contains(occ0));
        assertFalse(site.contains(occ1));

        assertTrue(site.containsOnly(occ0));
        assertFalse(site.containsOnly(occ1));

        site.vacate(occ0);
        assertEquals(0, site.countOccupants());
        assertEquals(0, site.countUniqueOccupants());

        assertFalse(site.contains(occ0));
        assertFalse(site.contains(occ1));

        assertFalse(site.containsOnly(occ0));
        assertFalse(site.containsOnly(occ1));
    }

    @Test(expected = RuntimeException.class)
    public void testVacateMissing() {
	Site<String> site = new Site<String>();
	site.occupy(occ0);
	site.vacate(occ1);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lattice.SiteTest");
    }
}
