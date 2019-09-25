
package jam.bravais;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import jam.math.Point;

/**
 * Tracks occupants on a Bravais lattice with periodic boundaries.
 */
public final class Lattice<T> {
    private final Period period;
    private final UnitCell unitCell;

    // Mapping from occupants to their ABSOLUTE unit cell indexes...
    private final Map<T, UnitIndex> indexMap = new HashMap<T, UnitIndex>();

    // Bidirectional mapping between occupants and the PERIODIC IMAGES
    // of their unit cell indexes...
    private final BiMap<UnitIndex, T> imageBiMap = HashBiMap.create();

    private Lattice(UnitCell unitCell, Period period) {
        if (period.dimensionality() != unitCell.dimensionality())
            throw new IllegalArgumentException("Inconsistent unit cell and period dimensionality.");

        this.period = period;
        this.unitCell = unitCell;
    }

    /**
     * Creates a new empty lattice with a fixed unit cell and period.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param unitCell the unit cell for the lattice.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @return a new empty lattice with the specified unit cell and
     * period.
     *
     * @throws IllegalArgumentException unless the unit cell and
     * lattice period have the same dimensionality.
     */
    public static <T> Lattice<T> create(UnitCell unitCell, Period period) {
        return new Lattice<T>(unitCell, period);
    }

    /**
     * Returns the periodic dimensions of this lattice.
     *
     * @return the periodic dimensions of this lattice.
     */
    public Period period() {
        return period;
    }

    /**
     * Returns the unit cell for this lattice.
     *
     * @return the unit cell for this lattice.
     */
    public UnitCell unitCell() {
        return unitCell;
    }

    /**
     * Identifies occupants on this lattice.
     *
     * @param occupant an occupant to examine.
     *
     * @return {@code true} iff this lattice contains the specified
     * occupant.
     */
    public boolean contains(T occupant) {
        return indexMap.containsKey(occupant);
    }

    /**
     * Returns the number of occupants on this lattice.
     *
     * @return the number of occupants on this lattice.
     */
    public int countOccupants() {
        return indexMap.size();
    }

    /**
     * Returns the dimensionality of this lattice.
     *
     * @return the dimensionality of this lattice.
     */
    public int dimensionality() {
        return unitCell().dimensionality();
    }

    /**
     * Fills this lattice with occupants moving from left-to-right,
     * bottom-to-top in the order returned by the collection iterator.
     *
     * @param occupants the occupants to add to this lattice.
     *
     * @throws IllegalArgumentException unless the number of occupants
     * exactly matches the number of distinct sites on this lattice.
     */
    public void fill(Collection<? extends T> occupants) {
        if (occupants.size() != period.countSites())
            throw new IllegalArgumentException("Occupants do not exactly fill the lattice.");

        List<UnitIndex> images = period.enumerate();
        assert images.size() == occupants.size();

        int imageOrdinal = 0;

        for (T occupant : occupants) {
            place(occupant, images.get(imageOrdinal));
            ++imageOrdinal;
        }
    }

    /**
     * Returns the periodic image of a continuous-space point
     * coordinate.
     *
     * @param point an absolute continuous-space coordinate.
     *
     * @return the periodic image of the specified point coordinate.
     */
    public Point imageOf(Point point) {
        return unitCell().pointAt(imageOf(unitCell().indexOf(point)));
    }

    /**
     * Returns the periodic image of an absolute unit cell index.
     *
     * @param index an absolute unit cell index.
     *
     * @return the periodic image of the specified unit cell index.
     */
    public UnitIndex imageOf(UnitIndex index) {
        return period.imageOf(index);
    }

    /**
     * Returns the discrete-space index of the unit cell occupied by
     * an occupant on this lattice (in absolute coordinates, not as
     * a periodic image).
     *
     * @param occupant the occupant to locate.
     *
     * @return the discrete-space index of the specified occupant
     * ({@code null} if this lattice does not contain the occupant).
     */
    public UnitIndex indexOf(T occupant) {
        return indexMap.get(occupant);
    }

    /**
     * Identifies empty lattices.
     *
     * @return {@code true} iff this lattice contains no occupants.
     */
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    /**
     * Identifies occupied unit cells.
     *
     * @param point a continuous-space coordinate to examine (given as
     * an absolute coordinate, not necessarily a periodic image).
     *
     * @return {@code true} iff the unit cell containing the specified
     * point is occupied.
     */
    public boolean isOccupied(Point point) {
        return isOccupied(unitCell().indexOf(point));
    }

    /**
     * Identifies occupied unit cells.
     *
     * @param index the discrete index of the unit cell to examine
     * (given as an absolute index, not necessarily a periodic image).
     *
     * @return {@code true} iff the unit cell with the specified
     * discrete index is occupied.
     */
    public boolean isOccupied(UnitIndex index) {
        return imageBiMap.containsKey(imageOf(index));
    }

    /**
     * Returns the continuous-space position of an occupant on this
     * lattice (in absolute coordinates, not as a periodic image).
     *
     * @param occupant the occupant to locate.
     *
     * @return the continuous-space position of the specified occupant
     * ({@code null} if this lattice does not contain the occupant).
     */
    public Point locate(T occupant) {
        UnitIndex index = indexOf(occupant);

        if (index != null)
            return unitCell().pointAt(index);
        else
            return null;
    }

    /**
     * Returns the nearest neighbors of an occupant on this lattice.
     *
     * @param occupant the occupant to locate.
     *
     * @return the nearest neighbors of the specified occupant (empty
     * if this lattice does not contain the occupant).
     */
    public List<T> neighborsOf(T occupant) {
        UnitIndex occupantIndex = indexOf(occupant);

        if (occupantIndex == null)
            return List.of();

        List<UnitIndex> neighborIndexes = unitCell().getNeighbors(occupantIndex);
        List<T> neighborOccupants = new ArrayList<T>(neighborIndexes.size());

        for (UnitIndex neighborIndex : neighborIndexes) {
            T neighborOccupant = occupantAt(neighborIndex);

            if (neighborOccupant != null)
                neighborOccupants.add(neighborOccupant);
        }

        return neighborOccupants;
    }

    /**
     * Returns the occupant of the unit cell containing a given point
     * (applying periodic boundary conditions if necessary).
     *
     * @param point a continuous-space coordinate to examine.
     *
     * @return the occupant of the unit cell containing the specified
     * point ({@code null} if the cell is unoccupied).
     */
    public T occupantAt(Point point) {
        return occupantAt(unitCell().indexOf(point));
    }

    /**
     * Returns the occupant of a unit cell (applying periodic boundary
     * conditions if necessary).
     *
     * @param index the discrete index of the unit cell to examine.
     *
     * @return the occupant of the specified unit cell ({@code null}
     * if the cell is unoccupied).
     */
    public T occupantAt(UnitIndex index) {
        return imageBiMap.get(imageOf(index));
    }

    /**
     * Places an occupant on this lattice in the unit cell containing
     * a given point.
     *
     * @param occupant the occupant to place on this lattice.
     *
     * @param point the continuous-space coordinate of the desired
     * location.
     *
     * @return the previous occupant of the unit cell ({@code null} if
     * the cell was unoccupied).
     */
    public T place(T occupant, Point point) {
        return place(occupant, unitCell().indexOf(point));
    }
    
    /**
     * Places an occupant on this lattice in the unit cell containing
     * a given point.
     *
     * @param occupant the occupant to place on this lattice.
     *
     * @param index the discrete index of the destination unit cell.
     *
     * @return the previous occupant of the unit cell ({@code null} if
     * the cell was unoccupied).
     */
    public T place(T occupant, UnitIndex index) {
        T prevOcc = imageBiMap.forcePut(imageOf(index), occupant);

        if (prevOcc != null && prevOcc != occupant)
            indexMap.remove(prevOcc);

        indexMap.put(occupant, index);
        assert indexMap.size() == imageBiMap.size();

        return prevOcc;
    }

    /**
     * Removes an occupant from this lattice (has no effect if the
     * occupant is not present).
     *
     * @param occupant the occupant to remove.
     */
    public void remove(T occupant) {
        indexMap.remove(occupant);
        imageBiMap.inverse().remove(occupant);
    }
    
    /**
     * Removes an existing lattice occupant and adds a new occupant at
     * the same location.
     *
     * @param oldOccupant the existing occupant to replace.
     *
     * @param newOccupant the new occupant to add.
     *
     * @throws IllegalArgumentException unless this lattice contains
     * the old occupant.
     */
    public void replace(T oldOccupant, T newOccupant) {
        UnitIndex index = indexOf(oldOccupant);

        if (index != null)
            place(newOccupant, index);
        else
            throw new IllegalArgumentException("Missing lattice occupant.");
    }
    
    /**
     * Swaps the locations of two occupants on this lattice.
     *
     * @param occ1 the first occupant to swap.
     *
     * @param occ2 the second occupant to swap.
     *
     * @throws IllegalArgumentException unless this lattice contains
     * both occupants.
     */
    public void swap(T occ1, T occ2) {
        UnitIndex index1 = indexOf(occ1);
        UnitIndex index2 = indexOf(occ2);

        if (index1 != null && index2 != null) {
            place(occ1, index2);
            place(occ2, index1);
        }
        else
            throw new IllegalArgumentException("Missing lattice occupant.");
    }
}
