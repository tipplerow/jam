
package jam.markov;

import jam.math.JamRandom;

/**
 * Maintains the state of a Markov process and evolves the state
 * according to a governing transition probability matrix.
 */
public final class MarkovProcess {
    private final JamRandom random;
    private final StochasticMatrix matrix;

    // The current state...
    private int state;

    /**
     * Creates a new Markov process with an initial state drawn at
     * random from the stationary distribution of states.
     *
     * @param random the random number source.
     *
     * @param matrix the stochastic transition probability matrix.
     */
    public MarkovProcess(JamRandom random, StochasticMatrix matrix) {
        this(random, matrix, matrix.sampleStationaryState(random));
    }

    /**
     * Creates a new Markov process with a specific initial state.
     *
     * @param random the random number source.
     *
     * @param matrix the stochastic transition probability matrix.
     *
     * @param state the index of the initial state.
     *
     * @throws IllegalArgumentException unless the initial state index
     * is within bounds.
     */
    public MarkovProcess(JamRandom random, StochasticMatrix matrix, int state) {
        validateStateIndex(matrix, state);

        this.random = random;
        this.matrix = matrix;
        this.state  = state;
    }

    private static void validateStateIndex(StochasticMatrix matrix, int state) {
        if (state < 0 || state >= matrix.countStates())
            throw new IllegalArgumentException("Invalid state index.");
    }

    /**
     * Returns the random number source.
     *
     * @return the random number source.
     */
    public JamRandom getRandom() {
        return random;
    }

    /**
     * Returns the stochastic transition probability matrix.
     *
     * @return the stochastic transition probability matrix.
     */
    public StochasticMatrix getMatrix() {
        return matrix;
    }

    /**
     * Returns the index of the current state of the Markov process.
     *
     * @return the index of the current state of the Markov process.
     */
    public int getState() {
        return state;
    }

    /**
     * Advances the Markov process with a fixed number of updates.
     *
     * @param steps the number of steps to advance.
     */
    public void advance(int steps) {
        for (int count = 0; count < steps; ++count)
            next();
    }

    /**
     * Moves the Markov process to the next state.
     *
     * @return the index of the updated state.
     */
    public int next() {
        state = matrix.update(random, state);
        return state;
    }
}
