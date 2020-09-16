
package jam.lattice;

/**
 * Enumerated lattice types, as a cross product of the occupancy type
 * (single or multiple) and storage type (dense or sparse).
 */
public enum LatticeType {
    /**
     * Dense storage for a lattice allowing only single occupants at
     * each site.
     */
    DENSE_SO {
        @Override public <T> Lattice<T> create(Period period) {
            return Lattice.denseSO(period);
        }
    },
    
    /**
     * Sparse storage for a lattice allowing multiple occupants at each
     * site.
     */
    SPARSE_MO {
        @Override public <T> Lattice<T> create(Period period) {
            return Lattice.sparseMO(period);
        }
    },
    
    /**
     * Sparse storage for a lattice allowing only single occupants at
     * each site.
     */
    SPARSE_SO {
        @Override public <T> Lattice<T> create(Period period) {
            return Lattice.sparseSO(period);
        }
    };
    
    /**
     * Creates a cubic lattice of this type with a specified side
     * length.
     *
     * @param <T> the lattice occupant type.
     *
     * @param length the periodic length along all dimensions.
     *
     * @return a cubic lattice of this type with the specified side
     * length.
     */
    public <T> Lattice<T> create(int length) {
        return create(Period.cubic(length));
    }
    
    /**
     * Creates a lattice of this type with a specified period.
     *
     * @param <T> the lattice occupant type.
     *
     * @param period the periodic dimensions of the lattice.
     *
     * @return a lattice of this type with the specified period.
     */
    public abstract <T> Lattice<T> create(Period period);
}
