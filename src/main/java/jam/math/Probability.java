
package jam.math;

import java.util.Arrays;
import java.util.Collections;

import jam.lang.DomainDouble;
import jam.vector.VectorView;

/**
 * Represents the probability of a discrete event, encapsulates
 * validation of probability values, and implements arithmetic
 * operations for probability values.
 */
public final class Probability extends DomainDouble implements Comparable<Probability> {
    /**
     * Valid range for probabilities.
     */
    public static final DoubleRange RANGE = DoubleRange.FRACTIONAL;

    /**
     * A globally sharable instance representing zero probability.
     */
    public static final Probability ZERO = valueOf(0.0);

    /**
     * A globally sharable instance representing unit probability.
     */
    public static final Probability ONE = valueOf(1.0);

    /**
     * A globally sharable instance representing the probability of one-half.
     */
    public static final Probability ONE_HALF = valueOf(0.5);

    /**
     * Creates a new event probability.
     *
     * @param value the probability of the event occurring.
     *
     * @throws RuntimeException unless the probability is valid.
     */
    public Probability(double value) {
        super(value, RANGE);
    }

    /**
     * Accepts a trial event having this probability of occurrence.
     *
     * <p>The global random number generator is used as the random
     * source.
     *
     * @return the randomly generated acceptance decision.
     */
    public boolean accept() {
        return accept(JamRandom.global());
    }

    /**
     * Accepts a trial event having this probability of occurrence.
     *
     * @param random a source for random deviates.
     *
     * @return the randomly generated acceptance decision.
     */
    public boolean accept(JamRandom random) {
        return random.nextDouble() < doubleValue();
    }

    /**
     * Computes the probability that the event represented by this
     * probability occurs on every trial in an independent series of
     * trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the probability that the event occurs in every trial in
     * a series of {@code trialCount} independent trials.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability allOccur(int trialCount) {
        if (trialCount < 1)
            throw new IllegalArgumentException("Number of trials must be positive.");

        return valueOf(Math.pow(doubleValue(), trialCount));
    }

    /**
     * Computes the joint probability that all events (in a set of
     * independent events) occur.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to the <em>product</em> of the
     * event probabilities.
     */
    public static Probability allOccur(Probability... events) {
        return allOccur(Arrays.asList(events));
    }

    /**
     * Computes the joint probability that all events (in a set of
     * independent events) occur.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to the <em>product</em> of the
     * event probabilities.
     */
    public static Probability allOccur(Iterable<Probability> events) {
        double value = 1.0;

        for (Probability event : events)
            value *= event.doubleValue();

        return valueOf(value);
    }

    /**
     * Computes the joint probability of this event <em>and</em>
     * another event occuring independently.
     *
     * @param that the other event probability.
     *
     * @return a new probability equal to the <em>product</em> of this
     * probability and the input probability.
     */
    public Probability and(Probability that) {
        return valueOf(this.doubleValue() * that.doubleValue());
    }

    /**
     * Computes the joint probability that one or more event (in a set
     * of independent events) occurs.
     *
     * @param events the event probabilities.
     *
     * @return the joint probability that one or more event in the
     * input set occurs.
     */
    public static Probability anyOccur(Probability... events) {
        return anyOccur(Arrays.asList(events));
    }

    /**
     * Computes the joint probability that one or more event (in a set
     * of independent events) occurs.
     *
     * @param events the event probabilities.
     *
     * @return the joint probability that one or more event in the
     * input set occurs.
     */
    public static Probability anyOccur(Iterable<Probability> events) {
        return not(noneOccur(events));
    }

    /**
     * Divides this probability by a scalar factor.
     *
     * @param scalar the scalar divisor.
     *
     * @return a new probability with a value divided by the
     * specified factor.
     *
     * @throws IllegalArgumentException unless the scalar factor is
     * positive.
     */
    public Probability divide(double scalar) {
        return times(1.0 / scalar);
    }

    /**
     * Computes the probability of a single event ocurring given that
     * this object is the probability that the event occurred on every
     * trial in a series of independent trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the single-event probability.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability ifAllOccurred(int trialCount) {
        if (trialCount < 1)
            throw new IllegalArgumentException("Number of trials must be positive.");

        return valueOf(Math.pow(doubleValue(), 1.0 / trialCount));
    }

    /**
     * Computes the probability of a single event ocurring given that
     * this object is the probability that the event did not occur on
     * any trial in a series of independent trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the single-event probability.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability ifNoneOccurred(int trialCount) {
        if (trialCount < 1)
            throw new IllegalArgumentException("Number of trials must be positive.");

        return valueOf(1.0 - Math.pow(doubleValue(), 1.0 / trialCount));
    }

    /**
     * Computes the probability of a single event ocurring given that
     * this object is the probability that the event occurred at least
     * once in a series of independent trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the single-event probability.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability ifOneOrMoreOccurred(int trialCount) {
        if (trialCount < 1)
            throw new IllegalArgumentException("Number of trials must be positive.");

        return valueOf(1.0 - Math.pow(1.0 - doubleValue(), 1.0 / trialCount));
    }

    /**
     * Determines whether a series of event probabilities is
     * normalized (sums to one).
     *
     * @param events the event probabilities.
     *
     * @return {@code true} iff the event probabilities sum to one.
     */
    public static boolean isNormalized(Probability... events) {
        return isNormalized(Arrays.asList(events));
    }

    /**
     * Determines whether a series of event probabilities is
     * normalized (sums to one).
     *
     * @param events the event probabilities.
     *
     * @return {@code true} iff the event probabilities sum to one.
     */
    public static boolean isNormalized(Iterable<Probability> events) {
        return DoubleComparator.DEFAULT.EQ(1.0, or(events).doubleValue());
    }

    /**
     * Computes the probability that none of the events represented by
     * this probability occur in a series of independent trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the probability that no events occur in a series of
     * {@code trialCount} independent trials.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability noneOccur(int trialCount) {
        return not(this).allOccur(trialCount);
    }

    /**
     * Computes the joint probability that no events (in a set of
     * independent events) occur.
     *
     * @param events the event probabilities.
     *
     * @return the joint probability that no events in the input set
     * occur.
     */
    public static Probability noneOccur(Probability... events) {
        return noneOccur(Arrays.asList(events));
    }

    /**
     * Computes the joint probability that no events (in a set of
     * independent events) occur.
     *
     * @param events the event probabilities.
     *
     * @return the joint probability that no events in the input set
     * occur.
     */
    public static Probability noneOccur(Iterable<Probability> events) {
        double value = 1.0;

        for (Probability event : events)
            value *= (1.0 - event.doubleValue());

        return valueOf(value);
    }

    /**
     * Returns the probability that this event does <em>not</em> occur.
     *
     * @return a new probability with value {@code 1.0 - x}, where
     * {@code x} is the value of this probability.
     */
    public Probability not() {
        return not(this);
    }

    /**
     * Returns the probability that an event does <em>not</em> occur.
     *
     * @param event the event in question.
     *
     * @return a new probability with value {@code 1.0 - x}, where
     * {@code x} is the value of the input probability.
     */
    public static Probability not(Probability event) {
        return valueOf(1.0 - event.doubleValue());
    }

    /**
     * Computes the probability that at least one of the events
     * represented by this probability occur in a series of
     * independent trials.
     *
     * @param trialCount the number of independent trials.
     *
     * @return the probability that at least one event occurs in a
     * series of {@code trialCount} independent trials.
     *
     * @throws IllegalArgumentException unless the trial count is positive.
     */
    public Probability oneOrMoreOccur(int trialCount) {
        return not(noneOccur(trialCount));
    }

    /**
     * Computes the joint probability that this event <em>or</em>
     * another mutually exclusive event will occur.
     *
     * @param that the other event probability.
     *
     * @return a new probability equal to the <em>sum</em> of this
     * probability and the input probability.
     *
     * @throws IllegalArgumentException unless this event and the
     * input event can describe a set of mutually exclusive events
     * (with probabilities totaling 1.0 or less).
     */
    public Probability or(Probability that) {
        return valueOf(this.doubleValue() + that.doubleValue());
    }

    /**
     * Computes the joint probability that exactly one event in a set
     * of mutually exclusive events will occur.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to the <em>sum</em> of the
     * event probabilities.
     *
     * @throws IllegalArgumentException unless the events can describe
     * a set of mutually exclusive events (with probabilities totaling
     * 1.0 or less).
     */
    public static Probability or(Probability... events) {
        return or(Arrays.asList(events));
    }

    /**
     * Computes the joint probability that exactly one event in a set
     * of mutually exclusive events will occur.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to the <em>sum</em> of the
     * event probabilities.
     *
     * @throws IllegalArgumentException unless the events can describe
     * a set of mutually exclusive events (with probabilities totaling
     * 1.0 or less).
     */
    public static Probability or(Iterable<Probability> events) {
        double value = 0.0;

        for (Probability event : events)
            value += event.doubleValue();

        return valueOf(value);
    }

    /**
     * Computes the probability of the "other" event missing from a
     * set of mutually exclusive events.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to one minus the sum of the
     * event probabilities.
     *
     * @throws IllegalArgumentException unless the events can describe
     * a set of mutually exclusive events (with probabilities totaling
     * 1.0 or less).
     */
    public static Probability other(Probability... events) {
        return other(Arrays.asList(events));
    }

    /**
     * Computes the probability of the "other" event missing from a
     * set of mutually exclusive events.
     *
     * @param events the event probabilities.
     *
     * @return a new probability equal to one minus the sum of the
     * event probabilities.
     *
     * @throws IllegalArgumentException unless the events can describe
     * a set of mutually exclusive events (with probabilities totaling
     * 1.0 or less).
     */
    public static Probability other(Iterable<Probability> events) {
        return not(or(events));
    }

    /**
     * Parses the string representation of a probability.
     *
     * @param s the string representation of a probability. 
     *
     * @return a new probability with the value specfied by the input
     * string.
     *
     * @throws IllegalArgumentException unless the string is a valid
     * probability.
     */
    public static Probability parse(String s) {
        return valueOf(Double.parseDouble(s));
    }

    /**
     * Multiplies this probability by a scalar factor.
     *
     * @param scalar the scalar multiplier.
     *
     * @return a new probability with a value multiplied by the
     * specified factor.
     *
     * @throws IllegalArgumentException if the new probability falls
     * outside the valid range.
     */
    public Probability times(double scalar) {
        return valueOf(scalar * doubleValue());
    }

    /**
     * Validates an event probability.
     *
     * @param value the probability to validate.
     *
     * @throws IllegalArgumentException unless the probability lies in
     * the valid range {@code [0.0, 1.0]}.
     */
    public static void validate(double value) {
        if (!RANGE.contains(value))
            throw new IllegalArgumentException("Invalid probability.");
    }

    /**
     * Validates a series of event probabilities, assumed to be
     * independent and exhaustive (representing all possible events).
     *
     * @param values the probabilities to validate.
     *
     * @throws IllegalArgumentException unless all probabilities lie
     * in the valid range {@code [0.0, 1.0]} and are normalized (sum
     * to unity).
     */
    public static void validate(VectorView values) {
        //
        // Convert all to Probability objects and verify that they are
        // normalized. Any single values that are invalid will trigger
        // an error upon conversion...
        //
        if (!isNormalized(valueOf(values)))
            throw new IllegalArgumentException("Probabilities are not normalized.");
    }

    /**
     * Marks a {@code double} value as a {@code Probability}.
     *
     * @param value the probability value.
     *
     * @return a {@code Probability} object having the specified
     * probability value.
     *
     * @throws IllegalArgumentException if the probability is
     * negative.
     */
    public static Probability valueOf(double value) {
        return new Probability(value);
    }

    /**
     * Converts an array of {@code double} values into probabilities.
     *
     * @param values the values to convert.
     *
     * @return an array of corresponding {@code Probability} objects.
     */
    public static Probability[] valueOf(double... values) {
        return valueOf(VectorView.wrap(values));
    }

    /**
     * Converts a vector view into an array of probabilities.
     *
     * @param values the values to convert.
     *
     * @return an array of corresponding {@code Probability} objects.
     */
    public static Probability[] valueOf(VectorView values) {
        Probability[] result = new Probability[values.length()];

        for (int index = 0; index < values.length(); index++)
            result[index] = valueOf(values.getDouble(index));

        return result;
    }

    @Override public int compareTo(Probability that) {
        return compare(this, that);
    }
}
