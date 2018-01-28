
package jam.lattice;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides the framework for lattice implementations that allow
 * multiple occupants (MO) at each site.
 */
public abstract class MOLattice<T> extends LatticeImpl<T> {
    /**
     * Initial capacity for new lattice sites.
     */ 
    protected static final int INITIAL_CAPACITY = 4;

    /**
     * Creates an empty lattice site.
     *
     * @return the new site.
     */
    protected Set<T> newSite() {
        return new HashSet<T>(INITIAL_CAPACITY);
    }

    /**
     * Creates an empty lattice site at a specified location.
     *
     * @param image the periodic image of the desired location.
     *
     * @return the new site at the specified location.
     */
    protected abstract Set<T> createSite(Image image);

    /**
     * Returns the site at a specified location, or {@code null} if
     * no site exists at that location.
     *
     * @param image the periodic image of the desired location.
     *
     * @return the site at the specified location, or {@code null} if
     * no site exists at that location.
     */
    protected abstract Set<T> lookupSite(Image image);

    /**
     * Returns the site at a specified location, creating an empty
     * site at that location if one does not already exist.
     *
     * @param image the periodic image of the desired location.
     *
     * @return the site at the specified location (never null).
     */
    protected Set<T> requireSite(Image image) {
        Set<T> site = lookupSite(image);

        if (site == null)
            site = createSite(image);

        return site;
    }

    @Override protected void assignOccupant(T occupant, Image image) {
        requireSite(image).add(occupant);
    }

    @Override public int countOccupants(Image image) {
        Set<T> site = lookupSite(image);

        if (site != null)
            return site.size();
        else
            return 0;
    }

    @Override public boolean isAvailable(Image image) {
        return true;
    }

    @Override public boolean isOccupiedBy(Image image, T occupant) {
        Set<T> site = lookupSite(image);

        if (site != null)
            return site.contains(occupant);
        else
            return false;
    }

    @Override public boolean mayOccupy(T occupant, Image image) {
        return true;
    }

    @Override public void occupy(T occupant, Image image) {
        assignOccupant(occupant, image);
    }

    @Override public int siteCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override public void vacate(T occupant, Image image) {
        Set<T> site = lookupSite(image);

        if (site != null)
            site.remove(occupant);
    }

    @Override public Set<T> viewOccupants(Image image) {
        Set<T> site = lookupSite(image);

        if (site != null)
            return Collections.unmodifiableSet(site);
        else
            return Collections.emptySet();
    }
}
