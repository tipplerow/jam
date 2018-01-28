
package jam.lattice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides a multi-occupancy lattice implementation backed by a
 * sparse mapping from images to occupant sets.
 */
public final class SparseMOLattice<T> extends MOLattice<T> {
    private final Map<Image, Set<T>> sites;

    /**
     * Creates an empty sparse multi-occupancy lattice.
     */
    public SparseMOLattice() {
	this.sites = new HashMap<Image, Set<T>>();
    }

    @Override public Set<T> createSite(Image image) {
        Set<T> site = newSite();
        sites.put(image, site);
        return site;
    }

    @Override public Set<T> lookupSite(Image image) {
        return sites.get(image);
    }

    @Override public void vacate(T occupant, Image image) {
        Set<T> site = lookupSite(image);

        if (site == null)
            return;
        
        site.remove(occupant);

        if (site.isEmpty())
            sites.remove(image);
    }
}
