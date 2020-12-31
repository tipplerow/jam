
package jam.space;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jam.bravais.Lattice;
import jam.bravais.UnitIndex;
import jam.lang.JamException;

/**
 * Maps each distinct unit index in a space to the site with that
 * index.
 */
public final class SiteMap {
    private Map<UnitIndex, Site> map;
    
    private SiteMap(Map<UnitIndex, Site> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    /**
     * Creates a site map for a lattice.
     *
     * @param lattice the lattice to map.
     *
     * @return a mapping from each distinct unit index on the lattice
     * to its corresponding site.
     */
    public static SiteMap create(Lattice lattice) {
        List<UnitIndex> indexList =
            lattice.period().enumerate(); 

        Map<UnitIndex, Site> siteMap =
            new LinkedHashMap<UnitIndex, Site>(indexList.size());

        for (UnitIndex index : indexList)
            siteMap.put(index, Site.create(index, lattice));

        return new SiteMap(siteMap);
    }

    /**
     * Identifies sites contained in this map.
     *
     * @param index the unit index of the site.
     *
     * @return {@code true} iff this map contains a site at the
     * specified location.
     */
    public boolean contains(UnitIndex index) {
        return map.containsKey(index);
    }

    /**
     * Identifies sites contained in this map.
     *
     * @param site the site of interest.
     *
     * @return {@code true} iff this map contains the specified site.
     */
    public boolean contains(Site site) {
        return contains(site.getIndex());
    }

    /**
     * Returns the site at a specified location.
     *
     * @param index the unit index of the site.
     *
     * @return the site at the specified location ({@code null}
     * if this map does not contain a site at that location).
     */
    public Site get(UnitIndex index) {
        return map.get(index);
    }

    /**
     * Returns the site at a specified location.
     *
     * @param coords the coordinates of the site.
     *
     * @return the site at the specified location ({@code null}
     * if this map does not contain a site at that location).
     *
     * @throws RuntimeException unless the number of coordinates
     * is valid.
     */
    public Site get(int... coords) {
        return get(UnitIndex.at(coords));
    }

    /**
     * Returns the site at a specified location.
     *
     * @param index the unit index of the site.
     *
     * @return the site at the specified location.
     *
     * @throws RuntimeException unless this map contains a site at
     * the specified location.
     */
    public Site require(UnitIndex index) {
        Site site = get(index);

        if (site != null)
            return site;
        else
            throw JamException.runtime("No site at index [%s].", index);
    }

    /**
     * Returns the site at a specified location.
     *
     * @param coords the coordinates of the site.
     *
     * @return the site at the specified location.
     *
     * @throws RuntimeException unless this map contains a site at
     * the specified location.
     */
    public Site require(int... coords) {
        return require(UnitIndex.at(coords));
    }

    /**
     * Returns the number of sites in this map.
     *
     * @return the number of sites in this map.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a read-only view of the sites in this map.
     *
     * @return a read-only view of the sites in this map.
     */
    public Collection<Site> viewSites() {
        return map.values();
    }

    @Override public String toString() {
        return map.toString();
    }
}
