
package jam.lattice;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a single-occupancy lattice implementation backed by a
 * sparse mapping from images to occupants.
 */
public final class SparseSOLattice<T> extends SOLattice<T> {
    private final Map<Image, T> sites;

    /**
     * Creates an empty sparse single-occupancy lattice.
     */
    public SparseSOLattice() {
	this.sites = new HashMap<Image, T>();
    }

    @Override public T occupantAt(Image image) {
        return sites.get(image);
    }

    @Override public void assignOccupant(T occupant, Image image) {
        sites.put(image, occupant);
    }

    @Override public void removeOccupant(Image image) {
        sites.remove(image);
    }
}
