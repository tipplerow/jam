
package jam.lattice;

/**
 * The eight divisions of a three-dimensional Euclidean coordinate system.
 */
public enum Octant {
    I(   Coord.at( 1,  1,  1)),
    II(  Coord.at(-1,  1,  1)),
    III( Coord.at(-1, -1,  1)),
    IV(  Coord.at( 1, -1,  1)),
    V(   Coord.at( 1,  1, -1)),
    VI(  Coord.at(-1,  1, -1)),
    VII( Coord.at(-1, -1, -1)),
    VIII(Coord.at( 1, -1, -1));

    private final Coord basis;

    private Octant(Coord basis) {
        this.basis = basis;
    }

    /**
     * Returns the basis vector for this octant.
     *
     * @return the basis vector for this octant.
     */
    public Coord getBasis() {
        return basis;
    }
}
