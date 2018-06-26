
package jam.lattice;

import java.util.Comparator;

/**
 * Orders coordinates by their distance from a reference coordinate.
 */
public final class DistanceComparator implements Comparator<Coord> {
    private final Coord reference;

    /**
     * Creates a new distance comparator with a fixed reference
     * coordinate.
     *
     * @param reference the reference coordinate.
     */
    public DistanceComparator(Coord reference) {
        this.reference = reference;
    }

    /**
     * Returns the reference coordinate for this comparator.
     *
     * @return the reference coordinate for this comparator.
     */
    public Coord getReference() {
        return reference;
    }

    @Override public int compare(Coord c1, Coord c2) {
        int d1 = Coord.computeSquaredDistance(c1, reference);
        int d2 = Coord.computeSquaredDistance(c2, reference);

        return Integer.compare(d1, d2);
    }
}
