
package jam.tumor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a collection of unique cell lineages located very close
 * together, e.g., at the same lattice site.
 */
public final class Deme extends Carrier {
    private final Set<Lineage> live = new HashSet<Lineage>();
    private final Set<Lineage> dead = new HashSet<Lineage>();

    // Number of instances created...
    private static int instanceCount = 0;

    private static int nextIndex() {
        return instanceCount++;
    }

    private Deme(Deme parent, Lineage lineage) {
        this(parent, Arrays.asList(lineage));
    }

    private Deme(Deme parent, Collection<Lineage> lineages) {
        super(nextIndex(), parent);
        updateLinages(lineages);
    }

    private void updateLinages(Collection<Lineage> lineages) {
        for (Lineage lineage : lineages)
            updateLineage(lineage);
    }

    private void updateLineage(Lineage lineage) {
        switch (lineage.getState()) {
        case ALIVE:
            live.add(lineage);
            dead.remove(lineage);
            break;

        case DEAD:
            dead.add(lineage);
            live.remove(lineage);
            break;

        default:
            throw new IllegalStateException("Lineage must be alive or dead.");
        }
    }

    /**
     * Creates a new deme originating with a single founding lineage.
     *
     * @param lineage the founding lineate.
     *
     * @return the new deme.
     */
    public static Deme create(Lineage lineage) {
        return new Deme(null, lineage);
    }

    /**
     * Returns the total number of cells in this deme.
     *
     * @return the total number of cells in this deme.
     */
    public final int countCells() {
        int cellCount = 0;

        for (Lineage lineage : live)
            cellCount += lineage.countCells();

        return cellCount;
    }

    /**
     * Returns the total number of lineages (live and dead) in this
     * deme.
     *
     * @return the total number of lineages (live and dead) in this
     * deme.
     */
    public final int countLineages() {
        return live.size() + dead.size();
    }

    /**
     * Returns the number of dead lineages in this deme.
     *
     * @return the number of dead lineages in this deme.
     */
    public final int countDeadLineages() {
        return dead.size();
    }

    /**
     * Returns the number of live lineages in this deme.
     *
     * @return the number of live lineages in this deme.
     */
    public final int countLiveLineages() {
        return live.size();
    }

    /**
     * Identifies empty (extinguished or dead) demes.
     *
     * @return {@code true} iff there are no cells remaining in this
     * deme.
     */
    public boolean isEmpty() {
        return live.isEmpty();
    }

    /**
     * Returns a read-only set view of the lineages contained within
     * this deme.
     *
     * @return a read-only set view of the lineages contained within
     * this deme.
     */
    public Set<Lineage> viewLineages() {
        Set<Lineage> all = new HashSet<Lineage>();

        all.addAll(live);
        all.addAll(dead);

        return Collections.unmodifiableSet(all);
    }

    /**
     * Returns a read-only set view of the dead lineages contained
     * within this deme.
     *
     * @return a read-only set view of the dead lineages contained
     * within this deme.
     */
    public Set<Lineage> viewDeadLineages() {
        return Collections.unmodifiableSet(dead);
    }

    /**
     * Returns a read-only set view of the live lineages contained
     * within this deme.
     *
     * @return a read-only set view of the live lineages contained
     * within this deme.
     */
    public Set<Lineage> viewLiveLineages() {
        return Collections.unmodifiableSet(live);
    }

    /**
     * Advances this deme by one time step.
     *
     * @param demeEnv the local environment where this deme resides
     * during the time step.
     *
     * @return a list with a single daughter if this deme exceeded its
     * maximum allowed size and the environment allowed deme division;
     * otherwise, an empty list.
     */
    @Override public List<Deme> advance(TumorEnv demeEnv) {
        TumorEnv lineageEnv = getLineageEnv(demeEnv);

        List<Lineage> deadParents   = new LinkedList<Lineage>();
        List<Lineage> liveDaughters = new LinkedList<Lineage>();

        for (Lineage parent : live) {
            liveDaughters.addAll(parent.advance(lineageEnv));

            if (parent.isDead())
                deadParents.add(parent);
        }

        updateLinages(deadParents);
        updateLinages(liveDaughters);

        if (mustDivide(demeEnv))
            return fissionList(lineageEnv);
        else
            return Collections.emptyList();
    }

    private TumorEnv getLineageEnv(TumorEnv demeEnv) {
        TumorEnv lineageEnv = demeEnv.getComponentEnv();

        if (mustRestrictLineageGrowth(demeEnv))
            lineageEnv = lineageEnv.noGrowth();

        return lineageEnv;
    }

    private boolean mustRestrictLineageGrowth(TumorEnv demeEnv) {
        //
        // Lineage growth must be restricted if (1) the local
        // environment does not allow the deme to divide, and 
        // (2) the deme exceeds its maximum allowed size...
        //
        return !demeEnv.allowDemeDivision() && exceedsMaximumSize(demeEnv);
    }

    private boolean exceedsMaximumSize(TumorEnv demeEnv) {
        return countCells() > demeEnv.getMaximumDemeSize();
    }

    private boolean mustDivide(TumorEnv demeEnv) {
        //
        // This deme must divide if (1) the local environment allows
        // deme division, and (2) this deme exceeds its maximum
        // allowed size...
        //
        return demeEnv.allowDemeDivision() && exceedsMaximumSize(demeEnv);
    }

    private List<Deme> fissionList(TumorEnv lineageEnv) {
        Deme daughter = divide(lineageEnv);

        if (daughter != null)
            return Arrays.asList(daughter);
        else
            return Collections.emptyList();
    }

    private Deme divide(TumorEnv lineageEnv) {
        Set<Lineage> fissionList = new HashSet<Lineage>();
        List<Lineage> deadParents = new LinkedList<Lineage>();

        for (Lineage lineage : live) {
            Lineage fission = lineage.divide(lineageEnv.getRetentionProb());

            if (lineage.isDead())
                deadParents.add(lineage);

            if (fission != null)
                fissionList.add(fission);
        }

        updateLinages(deadParents);

        if (fissionList.isEmpty())
            return null;
        else
            return new Deme(this, fissionList);
    }

    @Override public State getState() {
        return isEmpty() ? State.DEAD : State.ALIVE;
    }

    /**
     * Returns the mutations that originated in this deme.
     *
     * @return the mutations that originated in this deme.
     */
    @Override public MutationList getOriginalMutations() {
        return MutationList.cat(accumulate(live), accumulate(dead));
    }
}
