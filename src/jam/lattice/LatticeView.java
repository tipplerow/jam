
package jam.lattice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a read-only view of a periodic lattice.
 */
public abstract class LatticeView<T> {
    /**
     * Identifies occupants on this lattice.
     *
     * @param occupant the occupant to identify.
     *
     * @return {@code true} iff this lattice contains the specified
     * occupant.
     */
    public abstract boolean contains(T occupant);

    /**
     * Counts the total number of occupants on this lattice.
     *
     * @return the total number of occupants on this lattice.
     */
    public abstract int countOccupants();

    /**
     * Counts the number of occupants at a specific lattice site.
     *
     * @param coord the absolute coordinate of the site to examine.
     *
     * @return the number of occupants at the specified coordinate
     * <em>and all of its periodic images</em>.
     */
    public abstract int countOccupants(Coord coord);

    /**
     * Counts the number of occupants in the neighborhood of a
     * specific lattice site.
     *
     * @param center the absolute coordinate of the center of the
     * neighborhood.
     *
     * @param neighborhood the neighborhood (around the central site)
     * to search.
     *
     * @return the number of occupants at the neighboring lattice
     * sites <em>and all of their periodic images</em>.
     */
    public Map<Coord, Integer> countOccupants(Coord center, Neighborhood neighborhood) {
        Map<Coord, Integer> occupants = new HashMap<Coord, Integer>(neighborhood.size());

        for (Coord neighbor : neighborhood.getNeighbors(center))
            occupants.put(neighbor, countOccupants(neighbor));

        return occupants;
    }

    /**
     * Counts the number of neighboring lattice sites that can
     * accommodate new occupants.
     *
     * @param center the absolute coordinate of the central site.
     *
     * @param neighborhood the neighborhood (around the central site)
     * to search.
     *
     * @return the number of neighboring lattice sites that can
     * accommodate new occupants.
     */
    public int countAvailable(Coord center, Neighborhood neighborhood) {
        int available = 0;

        for (Coord neighbor : neighborhood.getNeighbors(center))
            if (isAvailable(neighbor))
                ++available;

        return available;
    }

    /**
     * Counts the number of lattice sites in the neighborhood
     * surrounding an occupant that can accommodate new occupants.
     *
     * @param occupant the occupant to examine.
     *
     * @param neighborhood the neighborhood (around the occupant) to
     * search.
     *
     * @return the number of neighboring lattice sites that can
     * accommodate new occupants.
     */
    public int countAvailable(T occupant, Neighborhood neighborhood) {
        Coord coord = locate(occupant);

        if (coord != null)
            return countAvailable(coord, neighborhood);
        else
            return 0;
    }

    /**
     * Finds all neighboring lattice sites that can accommodate new
     * occupants.
     *
     * @param center the absolute coordinate of the central site.
     *
     * @param neighborhood the neighborhood (around the central site)
     * to search.
     *
     * @return a list containing the absolute coordinates of all
     * neighboring sites that are available for new occupants.
     */
    public List<Coord> findAvailable(Coord center, Neighborhood neighborhood) {
        List<Coord> neighbors = neighborhood.getNeighbors(center);
        List<Coord> available = new ArrayList<Coord>(neighbors.size());

        for (Coord neighbor : neighbors)
            if (isAvailable(neighbor))
                available.add(neighbor);

        return available;
    }

    /**
     * Finds all lattice sites in the neighborhood of a given occupant
     * that can accommodate new occupants.
     *
     * @param occupant the occupant to examine.
     *
     * @param neighborhood the neighborhood (around the occupant) to
     * search.
     *
     * @return a list containing the absolute coordinates of all
     * neighboring sites that are available for new occupants.
     */
    public List<Coord> findAvailable(T occupant, Neighborhood neighborhood) {
        Coord coord = locate(occupant);

        if (coord != null)
            return findAvailable(coord, neighborhood);
        else
            return Collections.emptyList();
    }

    /**
     * Returns the periodic dimensions of this lattice.
     *
     * @return the periodic dimensions of this lattice.
     */
    public abstract Period getPeriod();

    /**
     * Translates an absolute coordinate into its periodic image on
     * this lattice.
     *
     * @param coord the absolute coordinate to translate.
     *
     * @return the periodic image of the input coordinate.
     */
    public Image imageOf(Coord coord) {
	return getPeriod().computeImage(coord);
    }

    /**
     * Identifies lattice sites that can accommodate new occupants.
     *
     * @param coord the absolute coordinate of the site to examine.
     * 
     * @return {@code true} iff the specified site contains fewer
     * occupants than its capacity.
     */
    public boolean isAvailable(Coord coord) {
        return countOccupants(coord) < siteCapacity();
    }

    /**
     * Identifies empty latticess.
     *
     * @return {@code true} iff there are no occupants on this
     * lattice.
     */
    public boolean isEmpty() {
        return countOccupants() == 0;
    }

    /**
     * Identifies empty lattice sites.
     *
     * @param coord the absolute coordinate of the site to examine.
     * 
     * @return {@code true} iff the specified coordinate <em>and all
     * of its periodic images</em> are unoccupied.
     */
    public boolean isEmpty(Coord coord) {
        return countOccupants(coord) == 0;
    }

    /**
     * Identifies occupied lattice sites.
     *
     * @param coord the absolute coordinate of the site to examine.
     * 
     * @return {@code true} iff there is at least one occupant at the
     * specified coordinate <em>or any of its periodic images</em>.
     */
    public boolean isOccupied(Coord coord) {
        return countOccupants(coord) > 0;
    }

    /**
     * Determines whether an occupant is present at an absolute
     * location.
     *
     * <p>Note that this method returns {@code false} even if the
     * occupant resides at a periodic image of the give absolute
     * coordinate.
     *
     * @param coord the absolute coordinate to examine.
     * 
     * @param occupant the occupant to identify.
     *
     * @return {@code true} iff the named occupant resides at the
     * specified absolute coordinate.
     */
    public boolean isOccupiedBy(Coord coord, T occupant) {
        return coord.equals(locate(occupant));
    }

    /**
     * Identifies lattice sites with at least one available neighbor.
     *
     * @param center the absolute coordinate of the central site.
     *
     * @param neighborhood the neighborhood (around the central site)
     * to search.
     *
     * @return {@code true} iff one or more neighbors of the central
     * site are availble.
     */
    public boolean hasAvailableNeighbor(Coord center, Neighborhood neighborhood) {
        for (Coord basis : neighborhood.viewBasis())
            if (isAvailable(center.plus(basis)))
                return true;

        return false;
    }

    /**
     * Identifies lattice occupants with at least one available
     * neighbor.
     *
     * @param occupant the occupant to examine.
     *
     * @param neighborhood the neighborhood (around the occupant) to
     * search.
     *
     * @return {@code true} iff the occupant resides on this lattice
     * and one or more of its neighboring sites are availble.
     */
    public boolean hasAvailableNeighbor(T occupant, Neighborhood neighborhood) {
        Coord coord = locate(occupant);

        if (coord != null)
            return hasAvailableNeighbor(coord, neighborhood);
        else
            return false;
    }

    /**
     * Returns the location of an occupant in this lattice.
     *
     * @param occupant the occupant to locate.
     *
     * @return the absolute coordinate of the specified occupant, or
     * {@code null} if the specified occupant does not reside in this
     * lattice.
     */
    public abstract Coord locate(T occupant);

    /**
     * Returns a mapping from occupied sites to their occupants.
     *
     * @return a map whose keys are the coordinates of all occupied
     * lattice sites with values containing all occupants of those
     * sites.
     */
    public Map<Coord, Set<T>> mapOccupiedSites() {
        Map<Coord, Set<T>> siteMap = new HashMap<Coord, Set<T>>();

        for (T occupant : viewOccupants()) {
            Coord coord = locate(occupant);

            if (coord == null)
                throw new IllegalStateException("Could not map lattice occupant.");

            Set<T> occupantList = siteMap.get(coord);

            if (occupantList == null) {
                occupantList = new HashSet<T>();
                siteMap.put(coord, occupantList);
            }

            occupantList.add(occupant);
        }

        return siteMap;
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
     * Returns a read-only view of all occupants on this lattice.
     *
     * @return an unmodifiable set containing all occupants on this
     * lattice.
     */
    public abstract Set<T> viewOccupants();

    /**
     * Returns a read-only view of the occupants at a particular
     * lattice site.
     *
     * @param coord the absolute coordinate of the site to examine.
     * 
     * @return an unmodifiable set containing all occupants at the
     * specified coordinate <em>and all of its periodic images</em>.
     */
    public abstract Set<T> viewOccupants(Coord coord);
}
