
package jam.lattice;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.util.MultisetUtil;

/**
 * Represents a single location in a lattice and maintains the
 * collection of occupants at that location.
 */
public final class Site<T> {
    private final Multiset<T> occupants = HashMultiset.create(SITE_CAPACITY);

    // Hopefully save a little memory relative to the default capacity...
    private static final int SITE_CAPACITY = 4;

    /**
     * Creates an empty site.
     */
    public Site() {
    }

    /**
     * Creates an occupied site.
     *
     * @param occupant the occupant of the site.
     */
    public Site(T occupant) {
	this.occupants.add(occupant);
    }

    /**
     * Creates an occupied site.
     *
     * @param occupants the occupants of the site.
     */
    public Site(Collection<T> occupants) {
	this.occupants.addAll(occupants);
    }

    /**
     * Identifies occupants of this site.
     *
     * @param occupant an occupant to examine.
     *
     * @return {@code true} iff the specified occupant resides at this
     * site.
     */
    public boolean contains(T occupant) {
	return occupants.contains(occupant);
    }

    /**
     * Identifies unique occupants of this site.
     *
     * @param occupant an occupant to examine.
     *
     * @return {@code true} iff the specified occupant is the only one
     * present at this site.
     */
    public boolean containsOnly(T occupant) {
	return countUniqueOccupants() == 1 && occupants.contains(occupant);
    }

    /**
     * Returns the number of occupants at this site.
     *
     * @return the number of occupants at this site.
     */
    public int countOccupants() {
	return occupants.size();
    }

    /**
     * Returns the number of occupants at this site.
     *
     * @return the number of occupants at this site.
     */
    public int countUniqueOccupants() {
	return MultisetUtil.countUnique(occupants);
    }

    /**
     * Returns a read-only view of the occupants of this site.
     *
     * @return an unmodifiable collection containing the occupants of
     * this site.
     */
    public Collection<T> getOccupants() {
	return Collections.unmodifiableCollection(occupants);
    }

    /**
     * Identifies empty sites.
     *
     * @return {@code true} iff this site has no occupants.
     */
    public boolean isEmpty() {
	return occupants.isEmpty();
    }

    /**
     * Identifies occupied sites.
     *
     * @return {@code true} iff this site has at least one occupant.
     */
    public boolean isOccupied() {
	return !isEmpty();
    }

    /**
     * Places an occupant on this site.
     *
     * @param occupant the arriving occupant.
     */
    public void occupy(T occupant) {
	occupants.add(occupant);
    }

    /**
     * Removes an occupant from this site.
     *
     * @param occupant the departing occupant.
     *
     * @throws IllegalStateException if the occupant was not present.
     */
    public void vacate(T occupant) {
	boolean success = occupants.remove(occupant);

	if (!success)
	    throw new IllegalStateException("Site was not occupied by the target occupant.");
    }
}
