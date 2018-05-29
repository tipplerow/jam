
package jam.tumor.carrier;

import jam.lang.OrdinalIndex;
import jam.lattice.Lattice;
import jam.lattice.LatticeView;

import jam.tumor.mutation.GrowthRate;
import jam.tumor.mutation.MutationList;

/**
 * Represents a single solid tumor.
 */
public abstract class Tumor<T extends TumorComponent> extends Carrier {
    private final TumorConfig config;
    private final Lattice<T> lattice;
    
    private static OrdinalIndex ordinalIndex = OrdinalIndex.create();

    /**
     * Creates all tumors.
     *
     * @param config the tumor configuration parameters.
     *
     * @param parent the parent of the new tumor; {@code null} for
     * original or independent tumors.
     */
    protected Tumor(TumorConfig config, Tumor parent) {
        super(ordinalIndex.next(), parent);

        this.config = config;
        this.lattice = config.createLattice();
    }

    /**
     * Allows subclasses access to the lattice of tumor components.
     *
     * @return the lattice of tumor components.
     */
    protected Lattice<T> getLattice() {
        return lattice;
    }

    /**
     * Computes the growth rate of a tumor component, adjusted for
     * its local environment.
     *
     * @param component the component under examination.
     *
     * @return the adjusted local growth rate for the specified
     * component.
     */
    public GrowthRate adjustGrowthRate(UniformComponent component) {
        throw new UnsupportedOperationException();
    }

    /**
     * Generates mutations (stochastically) for a tumor component,
     * with a mutation rate and mutation type as a function of the
     * local environment.
     *
     * <p>The returned list will often be empty, since mutations are
     * typically rare events.
     *
     * @param component the component under examination.
     *
     * @return the stochastically generated mutations for the given
     * component.
     */
    public MutationList generateMutations(UniformComponent component) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the configuration parameters for this tumor.
     *
     * @return the configuration parameters for this tumor.
     */
    public TumorConfig getConfig() {
        return config;
    }

    /**
     * Returns a read-only view of the lattice of tumor components.
     *
     * @return a read-only view of the lattice of tumor components.
     */
    public LatticeView<T> viewLattice() {
        return lattice;
    }
}
