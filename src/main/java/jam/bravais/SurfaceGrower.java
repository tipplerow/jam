
package jam.bravais;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.IOUtil;
import jam.lang.ObjectFactory;
import jam.math.Point;
import jam.util.CollectionUtil;

/**
 * Simulates surface-limited growth on a lattice.
 */
public final class SurfaceGrower<T> {
    private final Population<T> population;
    private final ObjectFactory<T> factory;
    private final int targetSize;

    // The available neighbor sites, with count equal to the number of
    // nearest neighbor occupants (so that the probability of adding
    // the next occupant at site K is proportional to the number of
    // existing occupants adjacent to site K)...
    private final Multiset<UnitIndex> openSites = HashMultiset.create();

    private SurfaceGrower(Population<T> population, ObjectFactory<T> factory, int targetSize) {
        this.factory = factory;
        this.population = population;
        this.targetSize = targetSize;
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0###");

    /**
     * Name of the output file containing the coordinates of the grown
     * object.
     */
    public static final String OUTPUT_FILE = "grow.csv";

    /**
     * Simulates surface-limited growth on a lattice.
     *
     * <p>This method fills the empty lattice by placing the first
     * occupant at the origin and then adding occupants at randomly
     * selected <em>expansion</em> sites (sites that are unoccupied
     * and neighbors to occupied sites).
     *
     * @param <T> the run-time type of the lattice occupants.
     *
     * @param population the population to fill.
     *
     * @param factory the factory used to create new occupants.
     *
     * @param size the number of occupants to add.
     *
     * @throws IllegalArgumentException unless the population is empty
     * and large enough to contain the target number of occupants.
     */
    public static <T> void grow(Population<T> population, ObjectFactory<T> factory, int size) {
        if (size < 1)
            throw new IllegalArgumentException("Target size must be positive.");

        if (size > population.lattice().countSites())
            throw new IllegalArgumentException("Target size exceeds the number of lattice sites.");

        SurfaceGrower<T> grower = new SurfaceGrower<T>(population, factory, size);
        grower.grow();
    }

    /**
     * Simulates one realization of surface-limited growth.
     *
     * <p>This method fills the empty population by placing the first
     * occupant at the origin and then adding occupants at randomly
     * selected <em>expansion</em> sites (sites that are unoccupied
     * and neighbors to occupied sites).
     *
     * @param unitCell the unit cell of the supporting lattice.
     *
     * @param size the number of occupants to add.
     *
     * @throws IllegalArgumentException unless the size is positive.
     */
    public static void run(UnitCell unitCell, int size) {
        int dim = unitCell.dimensionality();
        Period period = Period.boxND(10000, dim);

        Lattice lattice = Lattice.create(unitCell, period);
        Population<Integer> population = Population.empty(lattice);
        ObjectFactory<Integer> factory = ObjectFactory.forInteger();

        grow(population, factory, size);

        PrintWriter writer = IOUtil.openWriter(OUTPUT_FILE, false);
        writer.println("cell," + Point.headerCSV(dim));

        Map<Integer, Point> points = new TreeMap<Integer, Point>(population.mapPoints());

        for (Map.Entry<Integer, Point> entry : points.entrySet())
            writer.println(entry.getKey().toString() + "," + entry.getValue().formatCSV(DECIMAL_FORMAT));

        writer.close();
    }

    private Population<T> grow() {
        addFirstOccupant();

        while (population.countOccupants() < targetSize)
            addNextOccupant();

        return population;
    }

    private void addFirstOccupant() {
        //
        // Start by placing the first occupant at the origin...
        //
        placeOccupant(factory.newInstance(), UnitIndex.origin(population.lattice().dimensionality()));
    }

    private void addNextOccupant() {
        placeOccupant(factory.newInstance(), selectOpenSite());
    }

    private void placeOccupant(T occupant, UnitIndex index) {
        if (population.contains(occupant))
            throw new IllegalStateException("Occupant is already in place.");

        if (population.isOccupied(index))
            throw new IllegalStateException("Site is already occupied.");

        population.place(occupant, index);

        openSites.setCount(index, 0);
        openSites.addAll(population.unoccupiedNeighbors(index));

        int occupantCount = population.countOccupants();

        if (occupantCount % 100 == 0)
            JamLogger.info("Occupant count: [%d]...", occupantCount);
    }

    private UnitIndex selectOpenSite() {
        return CollectionUtil.sampleOne(openSites);
    }

    public static void main(String[] args) {
        //
        // Change the parameters as desired to test different lattices
        // and sizes...
        //
        run(BCCUnitCell.FUNDAMENTAL, 100000);
    }
}
