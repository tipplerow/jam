
package jam.lattice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a period lattice with fixed dimensions.
 *
 * <p><b>Absolute coordinates.</b> All coordinates used in the public
 * interface are <em>absolute</em>, not periodic images. The occupant
 * locations should always be tracked as absolute coordinates; this
 * lattice class will convert to periodic images as necessary.
 */
public class Lattice<T> extends LatticeView<T> {
    private final Period period;
    private final LatticeImpl<T> impl;
    private final Map<T, Coord> locations = new HashMap<T, Coord>();

    /**
     * Creates an empty lattice with a fixed period and underlying
     * storage.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @param impl the underlying lattice implementation.
     */
    protected Lattice(Period period, LatticeImpl<T> impl) {
        this.impl = impl;
	this.period = period;
    }

    /**
     * Creates a new single-occupancy cubic lattice (allowing at most
     * one occupant per site) backed by a dense representation of the
     * occupants (suitable when many of the sites will be occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param N the periodic length along all directions.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> denseSO(int N) {
        return denseSO(Period.cubic(N));
    }

    /**
     * Creates a new single-occupancy cubic lattice (allowing at most
     * one occupant per site) backed by a dense representation of the
     * occupants (suitable when many of the sites will be occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> denseSO(Period period) {
        return new Lattice<T>(period, new DenseSOLattice<T>(period));
    }

    /**
     * Creates a new multi-occupancy cubic lattice (allowing multiple
     * occupants at each site) backed by a sparse representation of
     * the sites (suitable when only a small fraction of the sites
     * will be occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param N the periodic length along all directions.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> sparseMO(int N) {
        return sparseMO(Period.cubic(N));
    }

    /**
     * Creates a new multi-occupancy cubic lattice (allowing multiple
     * occupants at each site) backed by a sparse representation of
     * the sites (suitable when only a small fraction of the sites
     * will be occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> sparseMO(Period period) {
        return new Lattice<T>(period, new SparseMOLattice<T>());
    }

    /**
     * Creates a new single-occupancy cubic lattice (allowing at most
     * one occupant per site) backed by a sparse representation of the
     * sites (suitable when only a small fraction of the sites will be
     * occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param N the periodic length along all directions.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> sparseSO(int N) {
        return sparseSO(Period.cubic(N));
    }

    /**
     * Creates a new single-occupancy cubic lattice (allowing at most
     * one occupant per site) backed by a sparse representation of the
     * sites (suitable when only a small fraction of the sites will be
     * occupied).
     *
     * @param <T> the type lattice occupants.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @return the new lattice.
     */
    public static <T> Lattice<T> sparseSO(Period period) {
        return new Lattice<T>(period, new SparseSOLattice<T>());
    }

    /**
     * Places an occupant on a lattice site.
     *
     * <p>If the occupant is already present in this lattice at a
     * different site, it is moved from that site to the specified
     * new site.
     *
     * @param occupant the occupant to place.
     *
     * @param coord the absolute coordinate of the site to occupy.
     *
     * @throws IllegalStateException if the site is already occupied
     * to its capacity.
     */
    public void occupy(T occupant, Coord coord) {
        vacate(occupant);
	impl.occupy(occupant, imageOf(coord));
        locations.put(occupant, coord);
    }

    /**
     * Removes an occupant from this lattice (or returns silently if
     * the occupant is not present in this lattice).
     *
     * @param occupant the occupant to remove.
     */
    public void vacate(T occupant) {
        Coord coord = locate(occupant);

        if (coord != null) {
            impl.vacate(occupant, imageOf(coord));
            locations.remove(occupant);
        }
    }
    
    @Override public boolean contains(T occupant) {
        return locations.containsKey(occupant);
    }

    @Override public int countOccupants() {
        return locations.size();
    }

    @Override public int countOccupants(Coord coord) {
        return impl.countOccupants(imageOf(coord));
    }

    @Override public Period getPeriod() {
        return period;
    }

    @Override public boolean isAvailable(Coord coord) {
        return impl.isAvailable(imageOf(coord));
    }

    @Override public Coord locate(T occupant) {
        return locations.get(occupant);
    }

    @Override public int siteCapacity() {
        return impl.siteCapacity();
    }

    @Override public Set<T> viewOccupants() {
        return Collections.unmodifiableSet(locations.keySet());
    }

    @Override public Set<T> viewOccupants(Coord coord) {
        return impl.viewOccupants(imageOf(coord));
    }
}
