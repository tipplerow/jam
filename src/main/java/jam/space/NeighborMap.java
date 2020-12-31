
package jam.space;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import jam.bravais.CoordType;
import jam.bravais.Lattice;
import jam.bravais.UnitIndex;

/**
 * Provides a mapping from each site in a discrete space to its
 * nearest neighbors.
 */
public final class NeighborMap {
    private final SetMultimap<Site, Site> map;

    private NeighborMap(SetMultimap<Site, Site> map) {
        this.map = ImmutableSetMultimap.copyOf(map);
    }

    /**
     * Creates a mapping from each site on a lattice to its nearest
     * neighbors (applying periodic boundary conditions).
     *
     * @param lattice the underlying lattice.
     *
     * @param siteMap a mapping from each distinct unit index on the
     * lattice to its corresponding site.
     *
     * @return a mapping from each site on the lattice to its nearest
     * neighbors (applying periodic boundary conditions).
     */
    public static NeighborMap create(Lattice lattice, SiteMap siteMap) {
        SetMultimap<Site, Site> neighborSiteMap = LinkedHashMultimap.create();
        Map<UnitIndex, List<UnitIndex>> neighborIndexMap = lattice.mapIndexNeighbors(CoordType.IMAGE);

        for (UnitIndex siteIndex : neighborIndexMap.keySet()) {
            Site site = siteMap.require(siteIndex);
            List<UnitIndex> neighborIndexList = neighborIndexMap.get(siteIndex);
            
            for (UnitIndex neighborIndex : neighborIndexList)
                neighborSiteMap.put(site, siteMap.require(neighborIndex));
        }

        return new NeighborMap(neighborSiteMap);
    }

    /**
     * Returns the nearest neighbors to a specified site.
     *
     * @param site the site of interest.
     *
     * @return a read-only view of the nearest neighbors to
     * the specified site.
     */
    public Set<Site> getNeighbors(Site site) {
        return map.get(site);
    }

    @Override public String toString() {
        return map.toString();
    }
}
