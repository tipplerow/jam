
package jam.markov;

import jam.dist.DiscreteCDF;
import jam.dist.DiscretePDF;
import jam.math.DoubleComparator;
import jam.math.DoubleRange;
import jam.math.JamRandom;
import jam.math.Probability;
import jam.matrix.JamEigen;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.vector.JamVector;
import jam.vector.VectorAggregator;
import jam.vector.VectorView;

/**
 * Provides analysis of regular stochastic (transition probability)
 * matrices.
 *
 * <p><em>Regular</em> stochastic matrices have exactly one unit
 * eigenvalue; all other eigenvalues have magnitude less than one.
 * The normalized eigenvector corresponding to the unit eigenvalue
 * "survives" repeated application of the transition matrix and
 * emerges as the stationary distribution; all other eigenvectors
 * eventually decay.
 *
 * <p>We adopt the <em>right</em> (as opposed to left) stochastic
 * matrix convention:
 * <ul>
 *   <li>
 *     Element {@code [j, k]} of the transition matrix is the 
 *     probability of moving <em>from</em> state {@code j} <em>to</em> 
 *     state {@code k}.
 *   </li>
 *   <li>
 *     Each <em>row</em> of the transition matrix has unit sum.
 *   </li>
 *   <li>
 *     Probability state vectors {@code p} are row vectors which
 *     evolve with equation of motion {@code p(t + 1) = p(t) T},
 *     where {@code T} is the right stochastic matrix.
 *   </li>
 *   <li>
 *     The stationary probability vector is a <em>left</em>
 *     eigenvector of the transition matrix having eigenvalue
 *     equal to one (e.g., a conventional eigenvector of the
 *     transpose of the transition matrix).
 *   </li>
 * </ul>
 *
 * <p>Note that Feller (An Introduction to Probability Theory and Its
 * Applications) and Reichl (A Modern Course in Statistical Physics)
 * follow this convention, while Van Kampen (Stochastic Processes in
 * Physics and Chemistry) uses the opposite.
 */
public final class StochasticMatrix {
    private final JamMatrix transProb;
    private final JamEigen  transEigen;

    // Distribution functions for the stationary state...
    private final DiscretePDF stationaryPDF;
    private final DiscreteCDF stationaryCDF;

    // Element transitionPDF[k] contains the probability distribution
    // function for transitions LEAVING state "k"...
    private final DiscretePDF[] transitionPDF;

    // Element transitionCDF[k] contains the cumulative distribution
    // function for transitions LEAVING state "k" (the cumulative sum
    // of the transition probabilities in row k).
    private final DiscreteCDF[] transitionCDF;

    // Valid range for all eigenvalues...
    private static final DoubleRange EIGENVALUE_RANGE = DoubleRange.closed(-1.0, 1.0);

    /**
     * The transition matrix from Section 6.C.1 of Reichl, 
     * "A Modern Course in Statistical Physics".
     */
    public static final StochasticMatrix REICHL =
        new StochasticMatrix(new double[][] {{ 0.0,       1.0,       0.0 },
                                             { 1.0 / 6.0, 3.0 / 6.0, 2.0 / 6.0 },
                                             { 0.0,       4.0 / 6.0, 2.0 / 6.0 }});

    /**
     * Creates a new stochastic matrix.
     *
     * @param transProb the transition probability matrix to analyze.
     *
     * @throws IllegalArgumentException unless the input matrix is a
     * valid transition matrix.
     */
    public StochasticMatrix(double[][] transProb) {
	this(new JamMatrix(transProb));
    }

    /**
     * Creates a new stochastic matrix.
     *
     * @param transProb the transition probability matrix to analyze.
     *
     * @throws IllegalArgumentException unless the input matrix is a
     * valid transition matrix.
     */
    public StochasticMatrix(JamMatrix transProb) {
	validateTransitionProbability(transProb);
	this.transProb = transProb;

	// We need the left eigenvectors: the eigenvectors of the
	// transpose of the transition matrix...
	this.transEigen = new JamEigen(transProb.transpose());
	validateEigenvalueDecomposition(transEigen);

	this.stationaryPDF = DiscretePDF.create(0, transEigen.findUniqueUnitEigenvector(true));
        this.stationaryCDF = DiscreteCDF.compute(stationaryPDF);

        this.transitionPDF = computeTransitionPDF(transProb);
        this.transitionCDF = computeTransitionCDF(transitionPDF);
    }

    /**
     * Validates a transition probability matrix.
     *
     * <p>Valid transition probability matrices must be square, have
     * elements on the interval {@code [0, 1]}, and have rows adding
     * to 1.
     *
     * @param transProb the transition probability matrix to validate.
     *
     * @throws IllegalArgumentException unless the input matrix is a
     * valid transition matrix.
     */
    public static void validateTransitionProbability(JamMatrix transProb) {
	if (!transProb.isSquare())
	    throw new IllegalArgumentException("Non-square transition matrix.");

	for (int row = 0; row < transProb.nrow(); row++)
	    for (int col = 0; col < transProb.ncol(); col++)
		validateElement(transProb.get(row, col));

        for (int row = 0; row < transProb.nrow(); row++)
	    validateRowSum(VectorAggregator.sum(transProb.viewRow(row)));
    }

    private static void validateElement(double element) {
        Probability.validate(element);
    }

    private static void validateRowSum(double rowSum) {
	if (DoubleComparator.DEFAULT.NE(rowSum, 1.0))
	    throw new IllegalArgumentException("Non-normalized transition matrix row.");
    }

    private static void validateEigenvalueDecomposition(JamEigen transEigen) {
        if (!transEigen.hasUniqueUnitEigenvalue())
            throw new IllegalStateException("Eigenvector decomposition does not have a unique unit eigenvalue.");

	for (double eigenvalue : transEigen.viewValues().elements())
	    EIGENVALUE_RANGE.validate(eigenvalue);
    }

    private static DiscretePDF[] computeTransitionPDF(JamMatrix transProb) {
        DiscretePDF[] transitionPDF = new DiscretePDF[transProb.nrow()];

        for (int stateIndex = 0; stateIndex < transitionPDF.length; ++stateIndex)
            transitionPDF[stateIndex] = DiscretePDF.create(0, transProb.viewRow(stateIndex));

        return transitionPDF;
    }

    private static DiscreteCDF[] computeTransitionCDF(DiscretePDF[] transitionPDF) {
        DiscreteCDF[] transitionCDF = new DiscreteCDF[transitionPDF.length];

        for (int stateIndex = 0; stateIndex < transitionCDF.length; ++stateIndex)
            transitionCDF[stateIndex] = DiscreteCDF.compute(transitionPDF[stateIndex]);

        return transitionCDF;
    }

    /**
     * Returns the number of states in this stochastic matrix.
     *
     * @return the number of states in this stochastic matrix.
     */
    public int countStates() {
        return transProb.nrow();
    }

    /**
     * Returns the CDF for the stationary state distribution.
     *
     * @return the CDF for the stationary state distribution.
     */
    public DiscreteCDF getStationaryCDF() {
	return stationaryCDF;
    }

    /**
     * Returns the PDF for the stationary state distribution.
     *
     * @return the PDF for the stationary state distribution.
     */
    public DiscretePDF getStationaryPDF() {
	return stationaryPDF;
    }

    /**
     * Returns the transition probability from one state to another.
     *
     * @param fromState the index of the initial state.
     *
     * @param toState the index of the final state.
     *
     * @return the probability of transition from the state with index
     * {@code fromState} to the state with index {@code toState}.
     *
     * @throws IllegalArgumentException unless the state indexes are
     * valid.
     */
    public double getTransitionProbability(int fromState, int toState) {
        return transProb.get(fromState, toState);
    }

    /**
     * Returns the probability distribution for transitions from one
     * state to all other states.
     *
     * @param fromState the index of the initial state.
     *
     * @return the transition distribution function {@code pdf} where
     * {@code pdf.evaluate(toState)} is the probability of transition
     * from state {@code fromState} to state {@code toState}.
     *
     * @throws IllegalArgumentException unless the initial state index
     * is valid.
     */
    public DiscretePDF getTransitionProbability(int fromState) {
        return transitionPDF[fromState];
    }

    /**
     * Returns a read-only view of the underlying transition
     * probability matrix.
     *
     * @return a read-only view of the underlying transition
     * probability matrix.
     */
    public MatrixView getTransitionProbability() {
	return transProb;
    }

    /**
     * Draws a state at random from the stationary distribution for
     * this stochastic matrix.
     *
     * @param random the random number source.
     *
     * @return the index of the state, generated randomly with the
     * probabilities of the stationary state.
     */
    public int sampleStationaryState(JamRandom random) {
        return stationaryCDF.sample(random);
    }

    /**
     * Simulates the movement of a system described by this stochastic
     * matrix from one state to another.
     *
     * @param random the random number source.
     *
     * @param state the index of the initial state.
     *
     * @return the index of the new state, generated randomly with the
     * transition probabilities for the initial state.
     *
     * @throws IllegalArgumentException unless the initial state index
     * is valid.
     */
    public int update(JamRandom random, int state) {
        return transitionCDF[state].sample(random);
    }

    /**
     * Updates the state probability distribution corresponding to one
     * step in the Markov process described by this stochastic matrix.
     *
     * @param state the initial state probability distribution.
     *
     * @return the updated probability distribution of states.
     *
     * @throws IllegalArgumentException unless the initial distribution
     * has the required dimensionality.
     */
    public DiscretePDF update(DiscretePDF state) {
        JamVector stateVector = new JamVector(countStates());

        for (int stateIndex = 0; stateIndex < countStates(); ++stateIndex)
            stateVector.set(stateIndex, state.evaluate(stateIndex));

        return DiscretePDF.create(0, update(stateVector));
    }

    /**
     * Updates the state probability distribution corresponding to one
     * step in the Markov process described by this stochastic matrix.
     *
     * @param state the initial state probability distribution.
     *
     * @return the updated probability distribution of states.
     *
     * @throws IllegalArgumentException unless the initial distribution
     * has the required dimensionality.
     */
    public JamVector update(VectorView state) {
        return JamMatrix.times(state, transProb);
    }
}
