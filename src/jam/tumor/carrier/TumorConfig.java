
package jam.tumor.carrier;

import jam.app.JamProperties;
import jam.lattice.Lattice;
import jam.lattice.LatticeType;
import jam.lattice.Neighborhood;
import jam.lattice.Period;
import jam.math.IntRange;

/**
 * Encapsulates the configuration parameters for tumor simulations.
 */
public abstract class TumorConfig {
    private static TumorConfig global = null;

    /**
     * Name of the system property defining the length of each side of
     * the cubic tumor component lattice.
     */
    public static final String LATTICE_BOX_LENGTH_PROPERTY = "TumorConfig.latticeBoxLength";

    /**
     * Name of the system property defining the nearest-neighbors on
     * the tumor component lattice.
     */
    public static final String LATTICE_NEIGHBORHOOD_PROPERTY = "TumorConfig.latticeNeighborhood";

    /**
     * Name of the system property defining the enumerated tumor
     * component lattice type.
     */
    public static final String LATTICE_TYPE_PROPERTY = "TumorConfig.latticeType";

    /**
     * A tumor configuration with default parameters (useful for unit
     * testing).
     */
    public static final TumorConfig DEFAULT = new DefaultConfig();

    /**
     * A tumor configuration with parameters specified by global
     * system properties.
     */
    public static final TumorConfig GLOBAL = new GlobalConfig();

    /**
     * Creates the tumor component lattice defined by these
     * configuration parameters.
     *
     * @param <T> the runtime tumor component type.
     *
     * @return the tumor component lattice defined by these
     * configuration parameters.
     */
    public <T> Lattice<T> createLattice() {
        return getLatticeType().create(getLatticePeriod());
    }

    /**
     * Returns the enumerated nearest-neighbor type for the tumor
     * component lattice.
     *
     * @return the enumerated nearest-neighbor type for the tumor
     * component lattice.
     */
    public abstract Neighborhood getLatticeNeighborhood();

    /**
     * Returns the period for the lattice of tumor components.
     *
     * @return the period for the lattice of tumor components.
     */
    public abstract Period getLatticePeriod();

    /**
     * Returns the enumerated tumor component lattice type.
     *
     * @return the enumerated tumor component lattice type.
     */
    public abstract LatticeType getLatticeType();

    /**
     * Returns a tumor configuration with parameters specified by
     * global system properties.
     *
     */
    public static TumorConfig global() {
        if (global == null)
            global = new GlobalConfig();
        
        return global;
    }
}

final class DefaultConfig extends TumorConfig {
    @Override public Neighborhood getLatticeNeighborhood() {
        return Neighborhood.MOORE;
    }
    
    @Override public Period getLatticePeriod() {
        return Period.cubic(10000);
    }
    
    @Override public LatticeType getLatticeType() {
        return LatticeType.SPARSE_MO;
    }
}

final class GlobalConfig extends TumorConfig {
    @Override public Neighborhood getLatticeNeighborhood() {
        return JamProperties.getRequiredEnum(TumorConfig.LATTICE_NEIGHBORHOOD_PROPERTY, Neighborhood.class);
    }
    
    @Override public Period getLatticePeriod() {
        return Period.cubic(JamProperties.getRequiredInt(TumorConfig.LATTICE_BOX_LENGTH_PROPERTY, IntRange.POSITIVE));
    }
    
    @Override public LatticeType getLatticeType() {
        return JamProperties.getRequiredEnum(TumorConfig.LATTICE_TYPE_PROPERTY, LatticeType.class);
    }
}
