
package jam.junit;

import java.util.List;
import java.util.Map;

import jam.lattice.Coord;
import jam.lattice.Lattice;
import jam.lattice.Neighborhood;
import jam.lattice.Period;
import jam.util.SetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class LatticeTest {
    private final Period period = Period.cubic(20);

    private final Coord coord1A = Coord.at(19, 10, 11); // Periodic image of coord1B
    private final Coord coord1B = Coord.at(-1, 30, 51); // Periodic image of coord1A
    private final Coord coord2  = Coord.at(19,  9, 11);
    private final Coord coord3  = Coord.at(13,  6, -5);

    private final String occ1 = "A";
    private final String occ2 = "B";
    private final String occ3 = "C";

    @Test public void testCountOccupantsInNeighborhood() {
        runOccupantsInNeighboorhood(Neighborhood.MOORE);
        runOccupantsInNeighboorhood(Neighborhood.VON_NEUMANN);
    }

    private void runOccupantsInNeighboorhood(Neighborhood neighborhood) {
        Lattice<String> lattice = Lattice.sparseMO(period);

        Coord center = Coord.at(8, 8, 8);
        List<Coord> neighbors = neighborhood.getNeighbors(center);

        assertTrue(lattice.isEmpty());
        lattice.occupy("A", neighbors.get(0));
        assertFalse(lattice.isEmpty());

        lattice.occupy("B", neighbors.get(1));
        lattice.occupy("C", neighbors.get(1));
        lattice.occupy("D", neighbors.get(2));
        lattice.occupy("E", neighbors.get(2));
        lattice.occupy("F", neighbors.get(2));


        Map<Coord, Integer> occupants = lattice.countOccupants(center, neighborhood);
        assertEquals(neighborhood.size(), occupants.size());

        for (Coord neighbor : neighbors)
            assertTrue(occupants.containsKey(neighbor));

        for (int k = 0; k <= 2; ++k)
            assertEquals(Integer.valueOf(k + 1), occupants.get(neighbors.get(k)));

        for (int k = 3; k < neighbors.size(); ++k)
            assertEquals(Integer.valueOf(0), occupants.get(neighbors.get(k)));
    }

    @Test public void testDenseSO() {
        runAvailabilitySO(Lattice.denseSO(period));
        runOccupyVacateSO(Lattice.denseSO(period));
    }

    @Test(expected = IllegalStateException.class)
    public void testDenseSODoubleOccupancy() {
        Lattice<String> lattice = Lattice.denseSO(period);

        lattice.occupy(occ2, coord2);
        lattice.occupy(occ3, coord2);
    }

    @Test public void testSparseSO() {
        runAvailabilitySO(Lattice.sparseSO(period));
        runOccupyVacateSO(Lattice.sparseSO(period));
    }

    @Test(expected = IllegalStateException.class)
    public void testSparseSODoubleOccupancy() {
        Lattice<String> lattice = Lattice.sparseSO(period);

        lattice.occupy(occ2, coord2);
        lattice.occupy(occ3, coord2);
    }

    @Test public void testSparseMO() {
        runOccupyVacateMO(Lattice.sparseMO(period));
    }

    private void runAvailabilitySO(Lattice<String> lattice) {
        //
        // The input lattice should be empty...
        //
        assertTrue(lattice.isEmpty());
        assertEquals(0, lattice.countOccupants());

        String center = "CENTER";
        lattice.occupy(center, Coord.at(10, 15, 20));
        assertFalse(lattice.isEmpty());

        assertEquals(26, lattice.countAvailable(center, Neighborhood.MOORE));
        assertEquals(6, lattice.countAvailable(center, Neighborhood.VON_NEUMANN));

        List<Coord> moore = lattice.findAvailable(center, Neighborhood.MOORE);
        List<Coord> vonNeumann = lattice.findAvailable(center, Neighborhood.VON_NEUMANN);

        assertEquals(26, moore.size());
        assertEquals(6, vonNeumann.size());
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.MOORE));
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.VON_NEUMANN));

        lattice.occupy("A", Coord.at( 9, 14, 19));
        lattice.occupy("B", Coord.at(10, 14, 19));
        lattice.occupy("C", Coord.at(11, 14, 19));
        lattice.occupy("D", Coord.at( 9, 15, 19));
        lattice.occupy("E", Coord.at(10, 15, 19));
        lattice.occupy("F", Coord.at(11, 15, 19));
        lattice.occupy("G", Coord.at( 9, 16, 19));
        lattice.occupy("H", Coord.at(10, 16, 19));
        lattice.occupy("I", Coord.at(11, 16, 19));

        assertEquals(17, lattice.countAvailable(center, Neighborhood.MOORE));
        assertEquals(5, lattice.countAvailable(center, Neighborhood.VON_NEUMANN));

        moore = lattice.findAvailable(center, Neighborhood.MOORE);
        vonNeumann = lattice.findAvailable(center, Neighborhood.VON_NEUMANN);

        assertEquals(17, moore.size());
        assertEquals(5, vonNeumann.size());
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.MOORE));
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.VON_NEUMANN));

        lattice.occupy("J", Coord.at( 9, 14, 20));
        lattice.occupy("K", Coord.at(10, 14, 20));
        lattice.occupy("L", Coord.at(11, 14, 20));
        lattice.occupy("M", Coord.at( 9, 15, 20));
        lattice.occupy("N", Coord.at(11, 15, 20));
        lattice.occupy("O", Coord.at( 9, 16, 20));
        lattice.occupy("P", Coord.at(10, 16, 20));
        lattice.occupy("Q", Coord.at(11, 16, 20));

        assertEquals(9, lattice.countAvailable(center, Neighborhood.MOORE));
        assertEquals(1, lattice.countAvailable(center, Neighborhood.VON_NEUMANN));

        moore = lattice.findAvailable(center, Neighborhood.MOORE);
        vonNeumann = lattice.findAvailable(center, Neighborhood.VON_NEUMANN);

        assertEquals(9, moore.size());
        assertEquals(1, vonNeumann.size());
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.MOORE));
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.VON_NEUMANN));

        lattice.occupy("R", Coord.at( 9, 14, 21));
        lattice.occupy("S", Coord.at(10, 14, 21));
        lattice.occupy("T", Coord.at(11, 14, 21));
        lattice.occupy("U", Coord.at( 9, 15, 21));
        lattice.occupy("V", Coord.at(10, 15, 21));

        assertEquals(4, lattice.countAvailable(center, Neighborhood.MOORE));
        assertEquals(0, lattice.countAvailable(center, Neighborhood.VON_NEUMANN));

        moore = lattice.findAvailable(center, Neighborhood.MOORE);
        vonNeumann = lattice.findAvailable(center, Neighborhood.VON_NEUMANN);

        assertEquals(4, moore.size());
        assertEquals(0, vonNeumann.size());
        assertTrue(lattice.hasAvailableNeighbor(center, Neighborhood.MOORE));
        assertFalse(lattice.hasAvailableNeighbor(center, Neighborhood.VON_NEUMANN));

        lattice.occupy("W", Coord.at(11, 15, 21));
        lattice.occupy("X", Coord.at( 9, 16, 21));
        lattice.occupy("Y", Coord.at(10, 16, 21));
        lattice.occupy("Z", Coord.at(11, 16, 21));

        assertEquals(0, lattice.countAvailable(center, Neighborhood.MOORE));
        assertEquals(0, lattice.countAvailable(center, Neighborhood.VON_NEUMANN));

        moore = lattice.findAvailable(center, Neighborhood.MOORE);
        vonNeumann = lattice.findAvailable(center, Neighborhood.VON_NEUMANN);

        assertEquals(0, moore.size());
        assertEquals(0, vonNeumann.size());
        assertFalse(lattice.hasAvailableNeighbor(center, Neighborhood.MOORE));
        assertFalse(lattice.hasAvailableNeighbor(center, Neighborhood.VON_NEUMANN));
    }

    private void runOccupyVacateSO(Lattice<String> lattice) {
        //
        // The input lattice should be empty...
        //
        assertFalse(lattice.contains(occ1));
        assertFalse(lattice.contains(occ2));
        assertFalse(lattice.contains(occ3));

	assertEquals(0, lattice.countOccupants());
	assertEquals(0, lattice.countOccupants(coord1A));
	assertEquals(0, lattice.countOccupants(coord1B));
	assertEquals(0, lattice.countOccupants(coord2));
	assertEquals(0, lattice.countOccupants(coord3));

        assertEquals(period, lattice.getPeriod());

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertNull(lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.viewOccupants().isEmpty());
        assertTrue(lattice.viewOccupants(coord1A).isEmpty());
        assertTrue(lattice.viewOccupants(coord1B).isEmpty());
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Vacating occupants that are not present should not trigger
        // an exception...
        lattice.vacate(occ1);
        lattice.vacate(occ2);
        lattice.vacate(occ3);

	lattice.occupy(occ1, coord1A);

        assertEquals(coord1A, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.contains(occ1));
        assertFalse(lattice.contains(occ2));
        assertFalse(lattice.contains(occ3));

	assertEquals(1, lattice.countOccupants());
	assertEquals(1, lattice.countOccupants(coord1A));
	assertEquals(1, lattice.countOccupants(coord1B));
	assertEquals(0, lattice.countOccupants(coord2));
	assertEquals(0, lattice.countOccupants(coord3));

        assertFalse(lattice.isAvailable(coord1A));
        assertFalse(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        lattice.occupy(occ1, coord1A);

        assertEquals(coord1A, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertFalse(lattice.isAvailable(coord1A));
        assertFalse(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        lattice.occupy(occ1, coord1B);

        assertEquals(coord1B, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertFalse(lattice.isAvailable(coord1A));
        assertFalse(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        lattice.occupy(occ1, coord2);

        assertEquals(coord2, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertFalse(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertTrue(lattice.viewOccupants(coord1A).isEmpty());
        assertTrue(lattice.viewOccupants(coord1B).isEmpty());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

	lattice.occupy(occ2, coord1B);

        assertEquals(coord2,  lattice.locate(occ1));
        assertEquals(coord1B, lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertFalse(lattice.isAvailable(coord1A));
        assertFalse(lattice.isAvailable(coord1B));
        assertFalse(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1, occ2), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1B));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

	lattice.vacate(occ1);
	lattice.occupy(occ3, coord2);

        assertNull(lattice.locate(occ1));
        assertEquals(coord1B, lattice.locate(occ2));
        assertEquals(coord2,  lattice.locate(occ3));

        assertFalse(lattice.isAvailable(coord1A));
        assertFalse(lattice.isAvailable(coord1B));
        assertFalse(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ2, occ3), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1B));
        assertEquals(SetUtil.fixed(occ3), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());
    }

    private void runOccupyVacateMO(Lattice<String> lattice) {
        //
        // The input lattice should be empty...
        //
        assertFalse(lattice.contains(occ1));
        assertFalse(lattice.contains(occ2));
        assertFalse(lattice.contains(occ3));

	assertEquals(0, lattice.countOccupants());
	assertEquals(0, lattice.countOccupants(coord1A));
	assertEquals(0, lattice.countOccupants(coord1B));
	assertEquals(0, lattice.countOccupants(coord2));
	assertEquals(0, lattice.countOccupants(coord3));

        assertEquals(period, lattice.getPeriod());

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertNull(lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.viewOccupants().isEmpty());
        assertTrue(lattice.viewOccupants(coord1A).isEmpty());
        assertTrue(lattice.viewOccupants(coord1B).isEmpty());
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Vacating occupants that are not present should not trigger
        // an exception...
        lattice.vacate(occ1);
        lattice.vacate(occ2);
        lattice.vacate(occ3);

        // Add a single occupant...
	lattice.occupy(occ1, coord1A);

        assertEquals(coord1A, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.contains(occ1));
        assertFalse(lattice.contains(occ2));
        assertFalse(lattice.contains(occ3));

	assertEquals(1, lattice.countOccupants());
	assertEquals(1, lattice.countOccupants(coord1A));
	assertEquals(1, lattice.countOccupants(coord1B));
	assertEquals(0, lattice.countOccupants(coord2));
	assertEquals(0, lattice.countOccupants(coord3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Reoccupy the same location...
        lattice.occupy(occ1, coord1A);

        assertEquals(coord1A, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Move to a different location but with the same periodic image...
        lattice.occupy(occ1, coord1B);

        assertEquals(coord1B, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord1B));
        assertTrue(lattice.viewOccupants(coord2).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Move to a different location and image...
        lattice.occupy(occ1, coord2);

        assertEquals(coord2, lattice.locate(occ1));
        assertNull(lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants());
        assertTrue(lattice.viewOccupants(coord1A).isEmpty());
        assertTrue(lattice.viewOccupants(coord1B).isEmpty());
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Add a second at a previous location...
	lattice.occupy(occ2, coord1B);

        assertEquals(coord2,  lattice.locate(occ1));
        assertEquals(coord1B, lattice.locate(occ2));
        assertNull(lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ1, occ2), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1B));
        assertEquals(SetUtil.fixed(occ1), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Replace an occupant...
	lattice.vacate(occ1);
	lattice.occupy(occ3, coord2);

        assertNull(lattice.locate(occ1));
        assertEquals(coord1B, lattice.locate(occ2));
        assertEquals(coord2,  lattice.locate(occ3));

        assertTrue(lattice.isAvailable(coord1A));
        assertTrue(lattice.isAvailable(coord1B));
        assertTrue(lattice.isAvailable(coord2));
        assertTrue(lattice.isAvailable(coord3));

        assertEquals(SetUtil.fixed(occ2, occ3), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1A));
        assertEquals(SetUtil.fixed(occ2), lattice.viewOccupants(coord1B));
        assertEquals(SetUtil.fixed(occ3), lattice.viewOccupants(coord2));
        assertTrue(lattice.viewOccupants(coord3).isEmpty());

        // Now put all occupants on a single site...
        lattice.occupy(occ1, coord2);
        lattice.occupy(occ2, coord2);
        lattice.occupy(occ3, coord2);

        assertTrue(lattice.isAvailable(coord2));
        assertEquals(3, lattice.countOccupants());
        
        assertEquals(SetUtil.fixed(occ1, occ2, occ3), lattice.viewOccupants());
        assertEquals(SetUtil.fixed(occ1, occ2, occ3), lattice.viewOccupants(coord2));
        
        assertTrue(lattice.viewOccupants(coord1A).isEmpty());
        assertTrue(lattice.viewOccupants(coord1B).isEmpty());
        assertTrue(lattice.viewOccupants(coord3).isEmpty());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LatticeTest");
    }
}
