
package jam.lattice;

import java.util.ArrayList;
import java.util.List;

import jam.util.ListUtil;
import jam.util.ListView;

/**
 * Enumerated conventions for defining the nearest neighbors on a
 * lattice.
 */
public enum Neighborhood {
    /**
     * The Moore neighborhood of 6 first-nearest, 12 second-nearest,
     * and eight third-nearest neighbors (26 in all).
     */
    MOORE {
        @Override public ListView<Coord> viewBasis() {
            return NEAREST_123;
        }
    },

    /**
     * The von Neumann neighborhood of 6 first-nearest neighbors.
     */
    VON_NEUMANN {
        @Override public ListView<Coord> viewBasis() {
            return FIRST_NEAREST;
        }
    };

    /**
     * The basis vectors for first-nearest neighbors on a cubic lattice.
     */
    public static final ListView<Coord> FIRST_NEAREST =
        ListView.create(Coord.at( 0,  0, -1),
                        Coord.at( 0, -1,  0),
                        Coord.at(-1,  0,  0),
                        Coord.at( 0,  1,  0),
                        Coord.at( 1,  0,  0),
                        Coord.at( 0,  0,  1));

    /**
     * The basis vectors for second-nearest neighbors on a cubic lattice.
     */
    public static final ListView<Coord> SECOND_NEAREST =
        ListView.create(Coord.at( 0, -1, -1),
                        Coord.at(-1,  0, -1),
                        Coord.at( 0,  1, -1),
                        Coord.at( 1,  0, -1),

                        Coord.at(-1, -1,  0),
                        Coord.at(-1,  1,  0),
                        Coord.at( 1,  1,  0),
                        Coord.at( 1, -1,  0),

                        Coord.at( 0, -1,  1),
                        Coord.at(-1,  0,  1),
                        Coord.at( 0,  1,  1),
                        Coord.at( 1,  0,  1));

    /**
     * The basis vectors for third-nearest neighbors on a cubic lattice.
     */
    public static final ListView<Coord> THIRD_NEAREST =
        ListView.create(Coord.at(-1, -1, -1),
                        Coord.at(-1,  1, -1),
                        Coord.at( 1,  1, -1),
                        Coord.at( 1, -1, -1),

                        Coord.at(-1, -1,  1),
                        Coord.at(-1,  1,  1),
                        Coord.at( 1,  1,  1),
                        Coord.at( 1, -1,  1));

    // This must be declared here to void an "illegal forward reference"...
    private static final ListView<Coord> NEAREST_123 =
        ListView.create(ListUtil.cat(FIRST_NEAREST, SECOND_NEAREST, THIRD_NEAREST));

    /**
     * Returns the basis vectors that define the nearest neighbors in this
     * neighborhood.
     *
     * @return a read-only view of the basis vectors for this neighborhood.
     */
    public abstract ListView<Coord> viewBasis();

    /**
     * Returns the number of sites in this neighborhood.
     *
     * @return the number of sites in this neighborhood.
     */
    public int size() {
        return viewBasis().size();
    }

    /**
     * Generates a list of neighbors surrounding a central coordinate.
     *
     * @param center the central coordinate.
     *
     * @return a list containing all neighboring coordinates.
     */
    public List<Coord> getNeighbors(Coord center) {
        List<Coord> neighbors = new ArrayList<Coord>(size());

        for (Coord basis : viewBasis())
            neighbors.add(center.plus(basis));

        return neighbors;
    }
}
