
package jam.space;

import java.util.Collection;
import java.util.Set;

import jam.bravais.UnitIndex;

/**
 * Defines a spatial arrangement of discrete points.
 */
public abstract class AbstractSpace implements Space {
    private final SiteMap siteMap;
    private final NeighborMap neighborMap;

    /**
     * Creates a new discrete space with fixed sites and neighbors.
     *
     * @param siteMap a mapping from each distinct unit index in
     * the space to the site located there.
     *
     * @param neighborMap a mapping from each site in the space to
     * its nearest neighbors.
     */
    protected AbstractSpace(SiteMap siteMap, NeighborMap neighborMap) {
        this.siteMap = siteMap;
        this.neighborMap = neighborMap;
    }
    
    @Override public boolean containsSite(UnitIndex index) {
        return siteMap.contains(index);
    }

    @Override public Site siteAt(UnitIndex index) {
        return siteMap.get(index);
    }

    @Override public Set<Site> neighborsOf(Site site) {
        return neighborMap.getNeighbors(site);
    }

    @Override public Collection<Site> viewSites() {
        return siteMap.viewSites();
    }
}
