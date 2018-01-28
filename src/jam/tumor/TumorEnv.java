
package jam.tumor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jam.lattice.Neighborhood;
import jam.math.DoubleRange;
import jam.math.Probability;

/**
 * Represents the environment surrounding a tumor component (cell,
 * lineage, or deme) or the tumor itself.
 *
 * <p>The presence or absence of anatomical constraints and supporting
 * tissues (vasculature), or the availability of nutrients, affect the
 * growth rate of tumor cells.  This class quantifies that effect by
 * adjusting the intrinsic growth rate in a manner appropriate for the
 * (time-varying) local environment.
 */
public abstract class TumorEnv {
    private final TumorEnv parent;
    private final List<Propagator> propagators;

    // Index of the discrete time steps...
    private int timeStep = 0;

    protected TumorEnv(Propagator propagator) {
        this.parent = this;
        this.propagators = createPropagatorList(propagator);
    }

    private static List<Propagator> createPropagatorList(Propagator propagator) {
        List<Propagator> propagatorList = new LinkedList<Propagator>();
        propagatorList.add(propagator);

        return propagatorList;
    }

    protected TumorEnv(TumorEnv parent) {
        this.parent = parent;
        this.propagators = parent.propagators;
    }

    /**
     * The default value for the exact lineage enumeration limit.
     *
     * <p>Lineages with cell counts above this limit will be treated
     * in a <em>semi-stochastic</em> manner for improved efficiency;
     * those with cell counts at or below this limit will be treated
     * with exact iteration over all member cells.
     */
    public static final int DEFAULT_EXACT_ENUMERATION_LIMIT = 10;

    /**
     * The default value for the neighborhood that defines the lattice
     * sites where a new deme may be placed after division.
     */
    public static final Neighborhood DEFAULT_DEME_NEIGHBORHOOD = Neighborhood.MOORE;

    /**
     * The default value for the maximum number of tumor cells that
     * can be contained in a single deme.
     */
    public static final int DEFAULT_MAXIMUM_DEME_SIZE = 10000;

    /**
     * When a lineage divides, the default probability that it will
     * retain a component cell.
     */
    public static final Probability DEFAULT_RETENTION_PROB = Probability.valueOf(0.5);

    /**
     * Creates a completely unrestricted environment: Intrinsic growth
     * rates are never altered and cell and deme division is always
     * allowed.
     *
     * @param propagator the tumor component to be enclosed in the
     * environment.
     *
     * @return a new unrestricted environment for the propagator.
     */
    public static TumorEnv unrestricted(Propagator propagator) {
        return new UnrestrictedEnv(propagator);
    }

    /**
     * Advances the tumor components in this environment by one time
     * step.
     */
    public final void advance() {
        ++timeStep;
        LinkedList<Propagator> daughters = new LinkedList<Propagator>();

        for (Propagator propagator : propagators)
            daughters.addAll(propagator.advance(this));

        propagators.addAll(daughters);
    }

    /**
     * Returns the parent of this environment, or this environment
     * itself if it is the top-most environment.
     *
     * @return the parent of this environment.
     */
    public final TumorEnv getParent() {
        return parent;
    }

    /**
     * Returns the propagator for which this environment was created.
     *
     * @return the propagator for which this environment was created.
     */
    public final List<Propagator> viewPropagators() {
        return Collections.unmodifiableList(propagators);
    }

    /**
     * Returns the index of the most recently executed discrete time
     * step (the number of times that the {@code advance()} method has
     * been called).
     *
     * @return the index of the most recently executed discrete time
     * step.
     */
    public final int getTimeStep() {
        return timeStep;
    }

    /**
     * Adjusts the intrinsic growth rate to this local environment.
     *
     * @param intrinsicRate the intrinsic growth rate of a cell or
     * lineage.
     *
     * @return the adjusted growth rate.
     */
    public abstract GrowthRate adjustGrowthRate(GrowthRate intrinsicRate);

    /**
     * Determines whether this environment allows for cell divisions.
     *
     * @return {@code true} iff this environment allows for cell division.
     */
    public abstract boolean allowCellDivision();

    /**
     * Determines whether this environment allows for demes to divide.
     *
     * @return {@code true} iff this environment allows for deme division.
     */
    public abstract boolean allowDemeDivision();

    /**
     * Returns the local environment that applies to components within
     * a composite structure (the individual cells in a lineage or the
     * lineages within demes).
     *
     * @return the local environment that applies to component carriers.
     */
    public TumorEnv getComponentEnv() {
        return this;
    }

    /**
     * Returns the exact lineage enumeration limit.
     *
     * <p>Lineages with cell counts above this limit will be treated
     * in a <em>semi-stochastic</em> manner for improved efficiency;
     * those with cell counts at or below this limit will be treated
     * with exact iteration over all member cells.
     *
     * @return the exact lineage enumeration limit.
     */
    public int getExactEnumerationLimit() {
        return DEFAULT_EXACT_ENUMERATION_LIMIT;
    }

    /**
     * Returns neighborhood that defines the lattice sites where a new
     * deme may be placed after division.
     *
     * @return neighborhood that defines the lattice sites where a new
     * deme may be placed after division.
     */
    public Neighborhood getDemeNeighborhood() {
        return DEFAULT_DEME_NEIGHBORHOOD;
    }

    /**
     * Returns the maximum number of tumor cells that can be contained
     * in a single deme.
     *
     * <p>Demes larger than this size must either (1) divide into two
     * demes if the local environment permits (e.g., if adjacent space
     * is available), or (2) shrink in size via cell death.
     *
     * @return the maximum number of tumor cells that can be contained
     * in a single deme.
     */
    public int getMaximumDemeSize() {
        return DEFAULT_MAXIMUM_DEME_SIZE;
    }

    /**
     * Returns the probability that a lineage will retain a component
     * cell when dividing into a cloned lineage.
     *
     * @return the retention probability.
     */
    public Probability getRetentionProb() {
        return DEFAULT_RETENTION_PROB;
    }

    /**
     * Returns an <em>overriding environment</em> identical to this
     * environment but with all cell and deme division forbidden:
     * birth rates are set to zero but death rates are unchanged.
     *
     * @return an overriding no-birth environment.
     */
    public final TumorEnv noBirth() {
        return new NoBirthEnv(this);
    }

    /**
     * Returns an <em>overriding environment</em> identical to this
     * environment but with deme division forbidden.
     *
     * @return an overriding no-birth environment.
     */
    public final TumorEnv noDemeDivision() {
        return new NoDemeDivisionEnv(this);
    }

    /**
     * Returns an <em>overriding environment</em> identical to this
     * environment but with net population growth forbidden.
     *
     * <p>In a no-growth environment, growth rates with population
     * growth factors less than or equal to 1.0 are left unchanged.
     * In other growth rates, birth and death rates are adjusted so
     * that the population growth factor becomes 1.0 and the overall
     * event rate is unchanged.
     *
     * @return an overriding no-growth environment.
     */
    public final TumorEnv noGrowth() {
        return new NoGrowthEnv(this);
    }

    /**
     * Returns an <em>overriding environment</em> identical to this
     * environment but having a reduced population growth factor.
     *
     * @param fraction the fractional factor by which intrinsic growth
     * factors will be scaled.
     *
     * @return an overriding slow-growth environment with the given
     * reduction factor.
     *
     * @throws IllegalArgumentException unless the input value is a
     * valid fraction.
     */
    public final TumorEnv slowGrowth(double fraction) {
        return new SlowGrowthEnv(this, fraction);
    }
}

// ---------------------------------------------------------------------

abstract class OverrideEnv extends TumorEnv {
    protected OverrideEnv(TumorEnv parent) {
        super(parent);
    }

    @Override public GrowthRate adjustGrowthRate(GrowthRate intrinsicRate) {
        return getParent().adjustGrowthRate(intrinsicRate);
    }

    @Override public boolean allowCellDivision() {
        return getParent().allowCellDivision();
    }

    @Override public boolean allowDemeDivision() {
        return getParent().allowDemeDivision();
    }

    @Override public TumorEnv getComponentEnv() {
        return getParent().getComponentEnv();
    }

    @Override public int getExactEnumerationLimit() {
        return getParent().getExactEnumerationLimit();
    }

    @Override public int getMaximumDemeSize() {
        return getParent().getMaximumDemeSize();
    }

    @Override public Probability getRetentionProb() {
        return getParent().getRetentionProb();
    }
}

// ---------------------------------------------------------------------

final class NoBirthEnv extends OverrideEnv {
    NoBirthEnv(TumorEnv parent) {
        super(parent);
    }

    @Override public GrowthRate adjustGrowthRate(GrowthRate intrinsicRate) {
        return intrinsicRate.noBirth();
    }

    @Override public boolean allowCellDivision() {
        return false;
    }

    @Override public boolean allowDemeDivision() {
        return false;
    }
}

// ---------------------------------------------------------------------

final class NoDemeDivisionEnv extends OverrideEnv {
    NoDemeDivisionEnv(TumorEnv parent) {
        super(parent);
    }

    @Override public boolean allowDemeDivision() {
        return false;
    }
}

// ---------------------------------------------------------------------

final class NoGrowthEnv extends OverrideEnv {
    NoGrowthEnv(TumorEnv parent) {
        super(parent);
    }

    @Override public GrowthRate adjustGrowthRate(GrowthRate intrinsicRate) {
        return intrinsicRate.noGrowth();
    }
}

// ---------------------------------------------------------------------

final class SlowGrowthEnv extends OverrideEnv {
    private final double fraction;

    SlowGrowthEnv(TumorEnv parent, double fraction) {
        super(parent);

        validateFraction(fraction);
        this.fraction = fraction;
    }

    private static void validateFraction(double fraction) {
        DoubleRange.FRACTIONAL.validate(fraction);
    }

    @Override public GrowthRate adjustGrowthRate(GrowthRate intrinsicRate) {
        return getParent().adjustGrowthRate(intrinsicRate).rescaleGrowthFactor(fraction);
    }
}

final class UnrestrictedEnv extends TumorEnv {
    UnrestrictedEnv(Propagator propagator) {
        super(propagator);
    }

    @Override public GrowthRate adjustGrowthRate(GrowthRate intrinsicRate) {
        return intrinsicRate;
    }

    @Override public boolean allowCellDivision() {
        return true;
    }

    @Override public boolean allowDemeDivision() {
        return true;
    }
}
