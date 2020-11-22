
package jam.stoch;

import jam.lang.DomainDouble;
import jam.math.DoubleRange;

/**
 * Represents the time at which a stochastic process occurs.
 */
public final class StochTime extends DomainDouble implements Comparable<StochTime> {
    private StochTime(double value) {
        super(value, RANGE);
    }

    /**
     * Valid range for times.
     */
    public static final DoubleRange RANGE = DoubleRange.NON_NEGATIVE;

    /**
     * A globally sharable instance representing zero time.
     */
    public static final StochTime ZERO = valueOf(0.0);

    /**
     * A globally sharable instance representing the end of time.
     */
    public static final StochTime INFINITY = valueOf(Double.POSITIVE_INFINITY);

    /**
     * Creates a new time from a floating-point value.
     *
     * @param value the stochastic time.
     *
     * @return a {@code StochTime} object having the specified time.
     *
     * @throws IllegalArgumentException if the time is
     * negative.
     */
    public static StochTime valueOf(double value) {
        return new StochTime(value);
    }

    /**
     * Adds a time interval to this time and returns the result in
     * a new object; this object is unchanged.
     *
     * @param time the time interval to add.
     *
     * @return a new stochastic time object representing the instant
     * occurring {@code time} units after this time.
     */
    public StochTime plus(double time) {
        return valueOf(this.doubleValue() + time);
    }

    @Override public int compareTo(StochTime that) {
        return compare(this, that);
    }
}
