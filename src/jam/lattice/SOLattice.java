
package jam.lattice;

import java.util.Collections;
import java.util.Set;

import jam.util.SetUtil;

/**
 * Provides the framework for lattice implementations that allow only
 * a single occupant (SO) per site.
 */
public abstract class SOLattice<T> extends LatticeImpl<T> {
    /**
     * Returns the occupant present at a specific location.
     *
     * @param image the periodic image of the location to examine.
     *
     * @return the occupant present at the specified location, or
     * {@code null} if the site is empty.
     */
    public abstract T occupantAt(Image image);

    /**
     * Removes the occupant at a given site (or returns silently if
     * the site is empty).
     *
     * @param image the periodic image of the location to vacate.
     */
    protected abstract void removeOccupant(Image image);

    @Override public int countOccupants(Image image) {
        return occupantAt(image) == null ? 0 : 1;
    }

    @Override public boolean isAvailable(Image image) {
        return occupantAt(image) == null;
    }

    @Override public boolean isEmpty(Image image) {
        return occupantAt(image) == null;
    }

    @Override public boolean isOccupied(Image image) {
        return occupantAt(image) != null;
    }

    @Override public boolean isOccupiedBy(Image image, T occupant) {
        //
        // The comparision is with "==" and not "equals()" because we
        // want to compare the physical identity of the objects...
        //
        return occupant == occupantAt(image);
    }

    @Override public int siteCapacity() {
        return 1;
    }

    @Override public void vacate(T occupant, Image image) {
        if (isOccupiedBy(image, occupant))
            removeOccupant(image);
    }

    @SuppressWarnings("unchecked")
    @Override public Set<T> viewOccupants(Image image) {
        T occupant = occupantAt(image);

        if (occupant == null)
            return Collections.emptySet();
        else
            return SetUtil.fixed(occupant);
    }
}
