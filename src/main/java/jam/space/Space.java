
package jam.space;

import java.util.Collection;

import jam.bravais.UnitIndex;

/**
 * Defines a spatial arrangement of discrete points.
 */
public interface Space {
    /**
     * Identifies sites contained in this space.
     *
     * @param index the unit index of the site location.
     *
     * @return {@code true} iff this space contains a site at the
     * specified location.
     */
    public abstract boolean containsSite(UnitIndex index);

    /**
     * Returns the site at a specified discrete location.
     *
     * @param index the unit index of the site location.
     *
     * @return the site at the specified location (or {@code null} if
     * this space does not contain a site at that location).
     */
    public abstract Site siteAt(UnitIndex index);

    /**
     * Returns the nearest neighbors to a specified site.
     *
     * @param site the site of interest.
     *
     * @return a read-only view of the nearest neighbors to the
     * specified site.
     *
     * @throws RuntimeException unless this space contains the
     * specified site.
     */
    public abstract Collection<Site> neighborsOf(Site site);

    /**
     * Returns a read-only view of the discrete sites in this space.
     *
     * @return a read-only view of the discrete sites in this space.
     */
    public abstract Collection<Site> viewSites();
}
