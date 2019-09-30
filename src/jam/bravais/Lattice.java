
package jam.bravais;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import jam.app.JamProperties;
import jam.lang.ObjectFactory;
import jam.math.Point;
import jam.util.RegexUtil;

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
     * Name of the system property that defines the global unit cell
     * type.
     */
    public static final String UNIT_CELL_TYPE_PROPERTY = "jam.bravais.unitCellType";

    /**
     * Name of the system property that defines the lengths of the
     * sides of the global unit cell.  For unit cells with unequal
     * side lengths, the property value must be a comma-delimited
     * string containing the side lengths.
     */
    public static final String UNIT_CELL_SIZE_PROPERTY = "jam.bravais.unitCellSize";

    /**
     * Name of the system property that defines the dimensions of
     * the global periodic boundaries. The property value must be
     * a comma-delimited string containing the box lengths.
     */
    public static final String PERIODIC_BOX_PROPERTY = "jam.bravais.periodicBox";

    /**
     * Creates a new empty lattice with a fixed unit cell and period
     * defined by the global system properties.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @return a new empty lattice with the unit cell and period
     * defined by the global system properties.
     *
     * @throws RuntimeException unless the system properties required
     * to define a lattice are property assigned.
     */
    public static <T> Lattice<T> create() {
        return create(resolveUnitCell(), resolvePeriod());
    }

    private static UnitCell resolveUnitCell() {
        UnitCellType cellType = resolveUnitCellType();
        double[]     cellSize = resolveUnitCellSize();

        return cellType.create(cellSize);
    }

    private static UnitCellType resolveUnitCellType() {
        return JamProperties.getRequiredEnum(UNIT_CELL_TYPE_PROPERTY, UnitCellType.class);
    }

    private static double[] resolveUnitCellSize() {
        return RegexUtil.parseDouble(RegexUtil.COMMA, JamProperties.getRequired(UNIT_CELL_SIZE_PROPERTY));
    }

    private static Period resolvePeriod() {
        return Period.box(RegexUtil.parseInt(RegexUtil.COMMA, JamProperties.getRequired(PERIODIC_BOX_PROPERTY)));
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
     * Parses a single string that defines a Bravais lattice.
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param def the string defining a Bravais lattice, formatted as
     * {@code TYPE; side1, side2, ...; period1, period2, ...}, where
     * {@code TYPE} is the enumerated type code, {@code side1, side2,
     * ...} are the (floating-point) side lengths for the unit cell,
     * and {@code period1, period2, ...} are the (integer) periodic
     * dimensions.
     *
     * @return the empty Bravais lattice defined by the input string.
     *
     * @throws IllegalArgumentException unless the input string is a
     * properly formatted lattice definition.
     */
    public static <T> Lattice<T> parse(String def) {
        String[] fields = RegexUtil.split(RegexUtil.SEMICOLON, def, 3);

        String typeField   = fields[0];
        String sideField   = fields[1];
        String periodField = fields[2];

        UnitCellType cellType  = UnitCellType.valueOf(typeField);
        double[]     cellSides = RegexUtil.parseDouble(RegexUtil.COMMA, sideField);
        int[]        periodDim = RegexUtil.parseInt(RegexUtil.COMMA, periodField);

        UnitCell unitCell = cellType.create(cellSides);
        Period   period   = Period.box(periodDim);

        return create(unitCell, period);
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
     * Identifies occupants on this lattice.
     *
     * @param occupants a collection of occupants to examine.
     *
     * @return {@code true} iff this lattice contains every occupant
     * in the specified collection.
     */
    public boolean containsAll(Collection<? extends T> occupants) {
        for (T occupant : occupants)
            if (!contains(occupant))
                return false;

        return true;
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
     * Fills this lattice moving from left-to-right, bottom-to-top
     * with occupants created by an object factory.  Any previous
     * occupants will be removed.
     *
     * @param factory the source of new occupants.
     */
    public void fill(ObjectFactory<? extends T> factory) {
        List<UnitIndex> images = period.enumerate();

        for (UnitIndex image : images)
            place(factory.newInstance(), image);
    }

    /**
     * Fills this lattice with occupants moving from left-to-right,
     * bottom-to-top in the order returned by the collection iterator.
     * Any previous occupants will be removed.
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
        assert occupants.size() == images.size();

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
     * Identifies completely full lattices.
     *
     * @return {@code true} iff every site in this lattice is
     * occupied.
     */
    public boolean isFull() {
        return countOccupants() == period.countSites();
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
     * Returns a map containing the absolute continuous-space
     * positions of all lattice occupants.
     *
     * @return a map containing the absolute continuous-space
     * positions of all lattice occupants.
     */
    public Map<T, Point> mapPoints() {
        Map<T, Point> pointMap = new HashMap<T, Point>(indexMap.size());

        for (Map.Entry<T, UnitIndex> entry : indexMap.entrySet())
            pointMap.put(entry.getKey(), unitCell.pointAt(entry.getValue()));

        return pointMap;
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

    /**
     * Identifies unoccupied neighbors to a given lattice site.
     *
     * @param index the (absolute) index of a lattice site.
     *
     * @return a list containing the (absolute) indexes of all
     * unoccupied nearest neighbors to the specified site.
     */
    public List<UnitIndex> unoccupiedNeighbors(UnitIndex index) {
        List<UnitIndex> neighbors = unitCell().getNeighbors(index);
        List<UnitIndex> unoccupied = new ArrayList<UnitIndex>(neighbors.size());

        for (UnitIndex neighbor : neighbors)
            if (!isOccupied(neighbor))
                unoccupied.add(neighbor);

        return unoccupied;
    }
}
