
package jam.space;

import java.util.LinkedHashMap;
import java.util.Map;

import jam.bravais.Lattice;
import jam.bravais.UnitCell;
import jam.bravais.UnitIndex;
import jam.math.Point;

/**
 * Represents a point in a discrete space.
 */
public final class Site {
    private final Point point;
    private final UnitIndex index;

    private Site(UnitIndex index, Point point) {
        this.index = index;
        this.point = point;
    }

    /**
     * Creates a new site with a fixed index and location.
     *
     * @param index the discrete index of the site.
     *
     * @param point the continuum coordinates of the site.
     */
    public static Site create(UnitIndex index, Point point) {
        return new Site(index, point);
    }

    /**
     * Creates a new site with a fixed index and location.
     *
     * @param index the discrete index of the site.
     *
     * @param cell the unit cell for the lattice where the site
     * resides.
     */
    public static Site create(UnitIndex index, UnitCell cell) {
        return create(index, cell.pointAt(index));
    }

    /**
     * Creates a new site with a fixed index and location.
     *
     * @param index the discrete index of the site.
     *
     * @param lattice the lattice where the site resides.
     */
    public static Site create(UnitIndex index, Lattice lattice) {
        return create(index, lattice.unitCell());
    }

    /**
     * Returns the discrete index of this site.
     *
     * @return the discrete index of this site.
     */
    public UnitIndex getIndex() {
        return index;
    }

    /**
     * Returns the continuum coordinates of this site.
     *
     * @return the continuum coordinates of this site.
     */
    public Point getPoint() {
        return point;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Site) && equalsSite((Site) obj);
    }

    private boolean equalsSite(Site that) {
        return this.index.equals(that.index) && this.point.equals(that.point);
    }

    @Override public int hashCode() {
        return index.hashCode();
    }

    @Override public String toString() {
        return String.format("Site(%s, %s)", index, point);
    }
}
