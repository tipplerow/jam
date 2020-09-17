
package jam.math;

import jam.vector.JamVector;
import jam.vector.VectorView;

/**
 * Generates random walks in three dimensions.
 */
public final class RandomWalk {
    private static final VectorView[] IDEAL_STEPS = createIdealSteps();

    private static VectorView[] createIdealSteps() {
        VectorView[] steps = new VectorView[6];

        steps[0] = JamVector.valueOf(-1.0,  0.0,  0.0);
        steps[1] = JamVector.valueOf( 1.0,  0.0,  0.0);
        steps[2] = JamVector.valueOf( 0.0, -1.0,  0.0);
        steps[3] = JamVector.valueOf( 0.0,  1.0,  0.0);
        steps[4] = JamVector.valueOf( 0.0,  0.0, -1.0);
        steps[5] = JamVector.valueOf( 0.0,  0.0,  1.0);

        return steps;
    }

    /**
     * Generates an ideal random walk on a cubic lattice with single
     * nearest-neighbor steps.  (Each step has unit length, and the
     * walker may visit the same location any number of times.)
     *
     * @param N the number of steps in the walk.
     *
     * @return an array of coordinates in the order visited, starting
     * at the origin.
     */
    public static JamVector[] ideal(int N) {
        return ideal(N, JamRandom.global());
    }

    /**
     * Generates an ideal random walk on a cubic lattice with single
     * nearest-neighbor steps.  (Each step has unit length, and the
     * walker may visit the same location any number of times.)
     *
     * @param N the number of steps in the walk.
     *
     * @param random the random number source.
     *
     * @return an array of coordinates in the order visited, starting
     * at the origin.
     */
    public static JamVector[] ideal(int N, JamRandom random) {
        JamVector[] coords = new JamVector[N];
        coords[0] = JamVector.valueOf(0.0, 0.0, 0.0);

        for (int k = 1; k < N; ++k)
            coords[k] = coords[k - 1].plus(idealStep(random));

        return coords;
    }

    private static VectorView idealStep(JamRandom random) {
        return IDEAL_STEPS[random.nextInt(IDEAL_STEPS.length)];
    }
}
