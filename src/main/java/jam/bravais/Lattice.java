
package jam.bravais;

import java.util.ArrayList;
import java.util.List;

import jam.app.JamProperties;
import jam.math.Point;
import jam.util.RegexUtil;

/**
 * Describes a Bravais lattice with periodic boundaries.
 */
public final class Lattice {
    private final Period period;
    private final UnitCell unitCell;

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
     * Creates a new lattice with a fixed unit cell and period defined
     * by the global system properties.
     *
     * @return a new lattice with the unit cell and period defined by
     * the global system properties.
     *
     * @throws RuntimeException unless the system properties required
     * to define a lattice are property assigned.
     */
    public static Lattice create() {
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
     * Creates a new lattice with a fixed unit cell and period.
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
    public static Lattice create(UnitCell unitCell, Period period) {
        return new Lattice(unitCell, period);
    }

    /**
     * Parses a single string that defines a Bravais lattice.
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
    public static Lattice parse(String def) {
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
     * Returns the number of unique (non-periodic) sites in this
     * lattice.
     *
     * @return the number of unique (non-periodic) sites in this
     * lattice.
     */
    public long countSites() {
        return period.countSites();
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
     * Returns a list of all primary (non-periodic) points on this
     * lattice.
     *
     * @return a list of all primary (non-periodic) points on this
     * lattice.
     */
    public List<Point> listPoints() {
        var indexes = period.enumerate();
        var points  = new ArrayList<Point>(indexes.size());

        for (UnitIndex index : indexes)
            points.add(unitCell.pointAt(index));

        return points;
    }

    /**
     * Returns a list of all neighbors to a point on this lattice.
     *
     * @param point a point on this lattice (or near a point on this
     * lattice).
     *
     * @return a list of all neighbors to the point on this lattice
     * that is closest to the input point.
     */
    public List<Point> listNeighbors(Point point) {
        var pointIndex      = unitCell.indexOf(point);
        var neighborIndexes = unitCell.getNeighbors(pointIndex);
        var neighborPoints  = new ArrayList<Point>(neighborIndexes.size());

        for (UnitIndex neighborIndex : neighborIndexes)
            neighborPoints.add(unitCell.pointAt(neighborIndex));

        return neighborPoints;
    }
}
