
package jam.space;

import java.util.List;

import jam.bravais.UnitIndex;
import jam.math.Point;

/**
 * Defines a zero-dimensional space with a single point.
 */
public final class PointSpace implements Space {
    private final Point point = Point.at(0.0);
    private final UnitIndex index = UnitIndex.at(0);

    private final Site site = Site.create(index, point);
    private final List<Site> sites = List.of(site);
    private final List<Site> neighbors = List.of();

    private PointSpace() {
    }

    /**
     * The one and only point space.
     */
    public static PointSpace INSTANCE = new PointSpace();

    @Override public boolean containsSite(UnitIndex index) {
        return index == this.index;
    }

    @Override public Site siteAt(UnitIndex index) {
        if (index == this.index)
            return site;
        else
            return null;
    }

    @Override public List<Site> neighborsOf(Site site) {
        return neighbors;
    }

    @Override public List<Site> viewSites() {
        return sites;
    }
}
