
package jam.tumor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.lattice.Coord;
import jam.lattice.Lattice;
import jam.lattice.LatticeView;
import jam.math.JamRandom;
import jam.math.VectorMoment;
import jam.util.ListUtil;

/**
 * Represents a single solid tumor as a set of demes distributed on a
 * lattice.
 *
 * <p><b>Monolithic tumors.</b> In the current implementation, a tumor
 * remains a single (monolithic) entity and never divides.
 */
public final class Tumor extends Carrier {
    //
    // The first deme is always placed at the origin of the lattice.
    // So, if this tumor is itself part of a larger structure (on a
    // super-lattice, say), then the coordinates of the demes on the
    // super-lattice can be computed by adding the tumor coordinate to
    // the deme coordinates.  In other words, the deme coordinates are
    // expressed relative to the tumor location in an enclosing frame.
    //
    private final Lattice<Deme> lattice = Lattice.sparseSO(LATTICE_LENGTH);

    // Only the live demes...
    private final HashSet<Deme> liveDemes = new HashSet<Deme>();

    // When a deme dies, remove it from the lattice, but remember its
    // location for subsequent analysis of mutation diffusion through
    // the tumor...
    private final HashMap<Deme, Coord> deadDemes = new HashMap<Deme, Coord>();

    // Global source of randomness...
    private final JamRandom random = JamRandom.global();

    // Number of instances created...
    private static int instanceCount = 0;

    // We use a sparse lattice, so there is no memory penalty for
    // having a large periodic box...
    private static final int LATTICE_LENGTH = 1000;

    // The first deme in the tumor is placed at this location...
    private static final Coord INITIAL_COORD = Coord.ORIGIN;

    private static int nextIndex() {
        return instanceCount++;
    }

    private Tumor(Deme deme) {
        super(nextIndex(), null);
        addDeme(deme, INITIAL_COORD);
    }

    private void addDeme(Deme deme, Coord coord) {
        liveDemes.add(deme);
        lattice.occupy(deme, coord);
    }

    private void removeDeme(Deme deme) {
        deadDemes.put(deme, lattice.locate(deme));
        liveDemes.remove(deme);
        lattice.vacate(deme);
    }

    /**
     * Creates a new tumor originating with a single founding lineage.
     *
     * @param lineage the founding lineage.
     *
     * @return the new tumor.
     */
    public static Tumor create(Lineage lineage) {
        return create(Deme.create(lineage));
    }

    /**
     * Creates a new tumor originating with a single founding deme.
     *
     * @param deme the founding deme.
     *
     * @return the new tumor.
     */
    public static Tumor create(Deme deme) {
        return new Tumor(deme);
    }

    /**
     * Computes the center of mass and gyration tensor for the cells
     * in this tumor.
     *
     * @return the vector moment (center of mass and gyration tensor)
     * for the cells in this tumor.
     */
    public VectorMoment computeMoment() {
        return VectorMoment.compute(mapCells());
    }

    /**
     * Returns the total number of cells in this tumor.
     *
     * @return the total number of cells in this tumor.
     */
    public final int countCells() {
        int cellCount = 0;

        for (Deme deme : liveDemes)
            cellCount += deme.countCells();

        return cellCount;
    }

    /**
     * Returns the total number of lineages in this tumor.
     *
     * @return the total number of lineages in this tumor.
     */
    public final int countLineages() {
        int lineageCount = 0;

        for (Deme deme : liveDemes)
            lineageCount += deme.countLineages();

        for (Deme deme : deadDemes.keySet())
            lineageCount += deme.countLineages();

        return lineageCount;
    }

    /**
     * Returns the total number of demes (live and dead) in this
     * tumor.
     *
     * @return the total number of demes (live and dead) in this
     * tumor.
     */
    public final int countDemes() {
        return liveDemes.size() + deadDemes.size();
    }

    /**
     * Returns the number of live demes in this tumor.
     *
     * @return the number of live demes in this tumor.
     */
    public final int countLiveDemes() {
        return liveDemes.size();
    }

    /**
     * Returns the number of dead demes in this tumor.
     *
     * @return the number of dead demes in this tumor.
     */
    public final int countDeadDemes() {
        return deadDemes.size();
    }

    /**
     * Identifies empty (extinguished or dead) tumors.
     *
     * @return {@code true} iff there are no cells remaining in this
     * tumor.
     */
    public boolean isEmpty() {
        return liveDemes.isEmpty();
    }

    /**
     * Finds the lineage of origin for each mutation in this tumor.
     *
     * @return a mapping from mutation to its originating lineage.
     */
    public Map<Mutation, Lineage> findOriginators() {
        Map<Mutation, Lineage> originators = new HashMap<Mutation, Lineage>();

        for (Deme deme : liveDemes)
            findOriginators(deme, originators);

        for (Deme deme : deadDemes.keySet())
            findOriginators(deme, originators);

        return originators;
    }

    private void findOriginators(Deme deme, Map<Mutation, Lineage> originators) {
        for (Lineage lineage : deme.viewLineages()) {
            for (Mutation mutation : lineage.getOriginalMutations()) {
                //
                // Mutations should originate in exactly one lineage...
                //
                if (originators.containsKey(mutation))
                    throw new IllegalStateException("Duplicate originator lineages.");
                else
                    originators.put(mutation, lineage);
            }
        }
    }

    /**
     * Locates each lineage in this tumor on the underlying lattice.
     *
     * <p>The location of a dead lineage is the last coordinate that
     * is occupied while alive.
     *
     * @return a mapping from lineage to its location on the lattice.
     */
    public Map<Lineage, Coord> locateLineages() {
        Map<Lineage, Coord> coords = new HashMap<Lineage, Coord>();

        for (Deme deme : liveDemes)
            locateLineages(deme, coords);

        for (Deme deme : deadDemes.keySet())
            locateLineages(deme, coords);

        return coords;
    }
    
    private void locateLineages(Deme deme, Map<Lineage, Coord> coords) {
        Coord coord = locateDeme(deme);

        for (Lineage lineage : deme.viewLineages()) {
            //
            // Lineages should be present in exactly one deme...
            //
            if (coords.containsKey(lineage))
                throw new IllegalStateException("Duplicate lineage locations.");
            else
                coords.put(lineage, coord);
        }
    }

    private Coord locateDeme(Deme deme) {
        Coord location = lattice.locate(deme);

        if (location != null)
            return location;

        location = deadDemes.get(deme);

        if (location != null)
            return location;

        // Should never happen for demes that originated in this tumor...
        throw new IllegalStateException("Deme cannot be located.");
    }

    /**
     * Maps the location of every cell in this tumor.
     *
     * @return a multiset containing the number of tumor cells at a
     * given location.
     */
    public Multiset<Coord> mapCells() {
        Multiset<Coord> locations = HashMultiset.create();

        for (Deme deme : liveDemes)
            for (Lineage lineage : deme.viewLiveLineages())
                locations.add(lattice.locate(deme), lineage.countCells());

        assert locations.size() == countCells();
        return locations;
    }

    /**
     * Surveys the spatial distribution of each mutation in this
     * tumor.
     *
     * @param timeStep the index of the latest simulation time step.
     *
     * @return a mapping from each mutation in this tumor to its
     * spatial survey.
     */
    public Map<Mutation, MutationSurvey> surveyMutations(int timeStep) {
        Map<Mutation, Lineage> originators  = findOriginators();
        Map<Lineage,  Coord>   lineageCoord = locateLineages();

        Map<Mutation, Coord>           originCoord   = mapMutationOrigin(originators, lineageCoord);
        Map<Mutation, Multiset<Coord>> mutationCoord = mapMutationCoord(lineageCoord);

        Map<Mutation, MutationSurvey> surveys = 
            survey(timeStep, originCoord, mutationCoord);

        return surveys;
    }

    private static Map<Mutation, Coord> mapMutationOrigin(Map<Mutation, Lineage> originators, 
                                                          Map<Lineage,  Coord>   lineageCoord) {
        Map<Mutation, Coord> originCoord = 
            new HashMap<Mutation, Coord>(originators.size());

        for (Mutation mutation : originators.keySet()) {
            Lineage lineage = originators.get(mutation);
            Coord   origin  = lineageCoord.get(lineage);

            if (origin != null)
                originCoord.put(mutation, origin);
            else
                throw new IllegalStateException("Unmapped lineage.");
        }

        return originCoord;
    }

    private static Map<Mutation, Multiset<Coord>> mapMutationCoord(Map<Lineage, Coord> lineageCoord) {
        //
        // Iterate over lineages and their ACCUMULATED mutations, and
        // record the location of each mutation...
        //
        Map<Mutation, Multiset<Coord>> mutationCoord = new HashMap<Mutation, Multiset<Coord>>();

        for (Lineage lineage : lineageCoord.keySet()) {
            if (lineage.isEmpty())
                continue;

            Coord coord = lineageCoord.get(lineage);

            for (Mutation mutation : lineage.getAccumulatedMutations()) {
                Multiset<Coord> coordSet = mutationCoord.get(mutation);

                if (coordSet == null) {
                    coordSet = HashMultiset.create();
                    mutationCoord.put(mutation, coordSet);
                }

                coordSet.add(coord, lineage.countCells());
            }
        }

        return mutationCoord;
    }

    private static Map<Mutation, MutationSurvey> survey(int timeStep,
                                                        Map<Mutation, Coord> originCoord,
                                                        Map<Mutation, Multiset<Coord>> mutationCoord) {
        Map<Mutation, MutationSurvey> surveys = 
            new HashMap<Mutation, MutationSurvey>(originCoord.size());

        for (Mutation mutation : mutationCoord.keySet()) {
            Coord origin = originCoord.get(mutation);
            Multiset<Coord> coordSet = mutationCoord.get(mutation);

            if (origin == null)
                throw new IllegalStateException("Unmapped origin.");

            surveys.put(mutation, MutationSurvey.create(mutation, timeStep, origin, coordSet));
        }

        return surveys;
    }
                
    /**
     * Returns a read-only set view of the demes contained within this
     * tumor.
     *
     * @return a read-only set view of the demes contained within this
     * tumor.
     */
    public Set<Deme> viewDemes() {
        Set<Deme> all = new HashSet<Deme>();

        all.addAll(liveDemes);
        all.addAll(deadDemes.keySet());

        return Collections.unmodifiableSet(all);
    }

    /**
     * Returns a read-only set view of the live demes contained within
     * this tumor.
     *
     * @return a read-only set view of the live demes contained within
     * this tumor.
     */
    public Set<Deme> viewLiveDemes() {
        return Collections.unmodifiableSet(liveDemes);
    }

    /**
     * Returns a read-only set view of the dead demes contained within
     * this tumor.
     *
     * @return a read-only set view of the dead demes contained within
     * this tumor.
     */
    public Set<Deme> viewDeadDemes() {
        return Collections.unmodifiableSet(deadDemes.keySet());
    }

    /**
     * Returns a read-only view of the spatial distribution of the
     * live demes composing this tumor.
     *
     * @return a read-only view of the spatial distribution of the
     * live demes composing this tumor.
     */
    public LatticeView<Deme> viewLattice() {
        return lattice;
    }

    /**
     * Advances this tumor by one time step.
     *
     * @param tumorEnv the local environment where this tumor resides
     * during the time step.
     *
     * @return an empty list (tumors never divide).
     */
    @Override public List<Tumor> advance(TumorEnv tumorEnv) {
        //
        // Traverse the demes in random order. Since we iterate over
        // the shuffled list (and not the population set itself), we
        // can modify the population set for birth and death events
        // during the iteration...
        //
        List<Deme> shuffled = new ArrayList<Deme>(liveDemes);
        ListUtil.shuffle(shuffled, random);

        for (Deme deme : shuffled) {
            TumorEnv demeEnv = getDemeEnv(tumorEnv, deme);
            List<Deme> daughters = deme.advance(demeEnv);

            // The criteria for allowing division assume that demes
            // will produce at most one daughter when dividing...
            if (daughters.size() > 1)
                throw new IllegalStateException("Deme produced more than one daughter.");

            for (Deme daughter : daughters)
                addDeme(daughter, chooseDaughterCoord(tumorEnv, deme));

            if (deme.isDead())
                removeDeme(deme);
        }
        
        return Collections.emptyList();
    }

    private TumorEnv getDemeEnv(TumorEnv tumorEnv, Deme deme) {
        TumorEnv demeEnv = tumorEnv.getComponentEnv();

        if (mustRestrictDivision(tumorEnv, deme))
            demeEnv = demeEnv.noDemeDivision();

        return demeEnv;
    }

    private boolean mustRestrictDivision(TumorEnv tumorEnv, Deme deme) {
        //
        // Demes cannot divide unless there is at least one available
        // neighboring lattice site...
        //
        return !lattice.hasAvailableNeighbor(deme, tumorEnv.getDemeNeighborhood());
    }

    private Coord chooseDaughterCoord(TumorEnv tumorEnv, Deme deme) {
        //
        // Select one of the available locations at random...
        //
        List<Coord> available = lattice.findAvailable(deme, tumorEnv.getDemeNeighborhood());

        if (available.isEmpty())
            throw new IllegalStateException("No available sites for a daughter deme.");

        return ListUtil.select(available, random);
    }

    @Override public State getState() {
        return isEmpty() ? State.DEAD : State.ALIVE;
    }

    /**
     * Returns the mutations that originated in this tumor.
     *
     * @return the mutations that originated in this tumor.
     */
    @Override public MutationList getOriginalMutations() {
        return MutationList.cat(accumulate(liveDemes), accumulate(deadDemes.keySet()));
    }
}
