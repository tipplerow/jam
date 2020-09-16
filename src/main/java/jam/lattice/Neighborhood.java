
package jam.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.math.DoubleUtil;
import jam.math.JamRandom;
import jam.util.ListUtil;
import jam.util.ListView;

/**
 * Enumerated conventions for defining the nearest neighbors on a
 * lattice.
 */
public enum Neighborhood {
    /**
     * The Moore neighborhood of six first-nearest, 12 second-nearest,
     * and eight third-nearest neighbors (26 in all).
     */
    MOORE {
        @Override public ListView<Coord> viewBasis() {
            return NEAREST_123;
        }
    },

    /**
     * The six first-nearest and 12 second-nearest neighbors (18 in
     * all).
     */
    NEAR_NEXT {
        @Override public ListView<Coord> viewBasis() {
            return NEAREST_12;
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
    private static final ListView<Coord> NEAREST_12 =
        ListView.create(ListUtil.cat(FIRST_NEAREST, SECOND_NEAREST));

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

    /**
     * Selects one of the basis vectors in this neighborhood at
     * random, with each basis vector being equally likely.
     *
     * @param source a random number generator.
     *
     * @return a basis vector selected at random.
     */
    public Coord randomBasisVector(JamRandom source) {
        return ListUtil.select(viewBasis(), source);
    }

    /**
     * Selects a neighboring coordinate at random, with each neighbor
     * being equally likely.
     *
     * @param center the central coordinate in the neighborhood.
     *
     * @param source a random number generator.
     *
     * @return a neighboring coordinate selected at random; the
     * central coordinate is unchanged.
     */
    public Coord randomNeighbor(Coord center, JamRandom source) {
        return center.plus(randomBasisVector(source));
    }

    /**
     * Selects distinct neighboring coordinates at random, with each
     * neighbor being equally likely.
     *
     * @param center the central coordinate in the neighborhood.
     *
     * @param count the number of distinct neighbors to select.
     *
     * @param source a random number generator.
     *
     * @return the neighboring coordinates selected at random; the
     * central coordinate is unchanged.
     *
     * @throws IllegalArgumentException if the number of neighbors is
     * negative or is larger than the size of this neighborhood.
     */
    public Collection<Coord> randomNeighbors(Coord center, int count, JamRandom source) {
        if (count < 0)
            throw new IllegalArgumentException("Neighbor count is negative.");

        if (count == 0)
            return Collections.emptyList();

        if (count == 1)
            return List.of(randomNeighbor(center, source));

        if (DoubleUtil.ratio(count, size()) < 0.25)
            return sampleNeighbors(center, count, source);

        if (count > size())
            throw new IllegalArgumentException("Neighbor count exceeds the size of the neighborhood.");

        return shuffleNeighbors(center, count, source);
    }

    private Set<Coord> sampleNeighbors(Coord center, int count, JamRandom source) {
        Set<Coord> neighbors = new HashSet<Coord>(count);

        while (neighbors.size() < count) {
            Coord neighbor = randomNeighbor(center, source);

            if (!neighbors.contains(neighbor))
                neighbors.add(neighbor);
        }

        return neighbors;
    }

    private List<Coord> shuffleNeighbors(Coord center, int count, JamRandom source) {
        List<Coord> neighbors = getNeighbors(center);
        ListUtil.shuffle(neighbors, source);

        return neighbors.subList(0, count);
    }

    /**
     * Returns the number of sites in this neighborhood.
     *
     * @return the number of sites in this neighborhood.
     */
    public int size() {
        return viewBasis().size();
    }
}
