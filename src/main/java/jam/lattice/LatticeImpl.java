
package jam.lattice;

import java.util.Set;

/**
 * Provides physical storage of lattice sites that is optimized for
 * the expected density of occupation (e.g., dense or sparse).
 *
 * <p>The {@code LatticeImpl} classes are concerned only with the
 * periodic images of the occupants; the enclosing {@code Lattice}
 * class must maintain the full absolute coordinates.
 */
public abstract class LatticeImpl<T> {
    /**
     * Places an occupant at a specific location.
     *
     * <p>The base class will ensure that the site has sufficient
     * capacity to accomodate the occupant, so the implementing
     * subclass may update the site without additional validation.
     *
     * @param occupant the occupant for the specified location.
     *
     * @param image the periodic image of the location to examine.
     */
    protected abstract void assignOccupant(T occupant, Image image);

    /**
     * Counts the number of occupants at a specific location.
     *
     * @param image the periodic image of the location to examine.
     *
     * @return the number of occupants at the specified location.
     */
    public abstract int countOccupants(Image image);

    /**
     * Identifies lattice sites that can accommodate new occupants.
     *
     * @param image the periodic image of the location to examine.
     *
     * @return {@code true} iff the specified site contains fewer
     * occupants than its capacity.
     */
    public boolean isAvailable(Image image) {
        return countOccupants(image) < siteCapacity();
    }

    /**
     * Identifies empty lattice sites.
     *
     * @param image the periodic image of the location to examine.
     *
     * @return {@code true} iff the specified site contains no
     * occupants.
     */
    public boolean isEmpty(Image image) {
        return countOccupants(image) == 0;
    }

    /**
     * Identifies occupied lattice sites.
     *
     * @param image the periodic image of the location to examine.
     *
     * @return {@code true} iff the specified site contains one or
     * more occupants.
     */
    public boolean isOccupied(Image image) {
        return countOccupants(image) > 0;
    }

    /**
     * Determines whether an occupant is present at a given site.
     *
     * @param image the periodic image of the location to examine.
     *
     * @param occupant the occupant to identify.
     *
     * @return {@code true} iff the specified site contains the named
     * occupant.
     */
    public abstract boolean isOccupiedBy(Image image, T occupant);

    /**
     * Determines whether an occupant may occupy a specific site.
     *
     * @param occupant the potential occupant.
     *
     * @param image the periodic image of the location where the
     * occupant would be placed.
     *
     * @return {@code true} iff this lattice can accomodate the
     * occupant at the specified site.
     */
    public boolean mayOccupy(T occupant, Image image) {
        return isAvailable(image) || isOccupiedBy(image, occupant);
    }

    /**
     * Places an occupant on a specific lattice site (or returns
     * silently if the occupant is already present at that site).
     *
     * @param occupant the occupant to place.
     *
     * @param image the periodic image of the site to occupy.
     *
     * @throws IllegalStateException if the site is already occupied
     * to its capacity.
     */
    public void occupy(T occupant, Image image) {
        if (mayOccupy(occupant, image))
            assignOccupant(occupant, image);
        else
            throw new IllegalStateException("Site is fully occupied.");
    }

    /**
     * Returns the maximum number of occupants that can be
     * accommodated at a single lattice site.
     *
     * @return the maximum number of occupants that can be
     * accommodated at a single lattice site.
     */
    public abstract int siteCapacity();

    /**
     * Removes an occupant from a specific location (or returns
     * silently if the occupant is not present at that location).
     *
     * @param occupant the occupant to remove.
     *
     * @param image the periodic image of the location to vacate.
     */
    public abstract void vacate(T occupant, Image image);

    /**
     * Returns a read-only view of the occupants at a particular
     * lattice site.
     *
     * @param image the periodic image of the location to examine.
     * 
     * @return an unmodifiable set containing all occupants at the
     * specified location.
     */
    public abstract Set<T> viewOccupants(Image image);
}
