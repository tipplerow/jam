
package jam.space;

import jam.bravais.Lattice;

/**
 * Defines a spatial arrangement of discrete points distributed on a
 * Bravais lattice with periodic boundary conditions.
 */
public final class LatticeSpace extends AbstractSpace {
    private final Lattice lattice;

    private LatticeSpace(Lattice lattice, SiteMap siteMap, NeighborMap neighborMap) {
        super(siteMap, neighborMap);
        this.lattice = lattice;
    }

    /**
     * Creates a new discrete space with sites distributed on a
     * Bravais lattice.
     *
     * @param lattice the lattice where the sites are located.
     *
     * @return a new discrete space with sites distributed on the
     * specified lattice.
     */
    public static LatticeSpace create(Lattice lattice) {
        SiteMap siteMap = SiteMap.create(lattice);
        NeighborMap neighborMap = NeighborMap.create(lattice, siteMap);

        return new LatticeSpace(lattice, siteMap, neighborMap);
    }
}
