
package jam.time;

import java.time.Duration;
import java.time.LocalTime;

import jam.lang.ObjectRange;

/**
 * Defines a half-open time interval with fixed boundaries.
 *
 * <p>Following the convention used by {@code String.substring()} and
 * {@code List.subList()}, the lower bound of the range is inclusive
 * but the upper bound is exclusive.
 */
public final class TimeRange extends ObjectRange<LocalTime> {
    private TimeRange(LocalTime begin, LocalTime end) {
        super(begin, end);
    }

    /**
     * The single empty time range.
     */
    public static final TimeRange EMPTY =
        new TimeRange(LocalTime.NOON, LocalTime.NOON);

    /**
     * Creates the half-open time range {@code [begin, end)}.
     *
     * @param begin the earliest time included in the range.
     *
     * @param end the earliest time <em>excluded from</em> the range.
     *
     * @throws RuntimeException if {@code begin > end}.
     */
    public static TimeRange create(LocalTime begin, LocalTime end) {
        return new TimeRange(begin, end);
    }

    /**
     * Returns the duration of this time interval.
     *
     * @return the duration of this time interval.
     */
    public Duration duration() {
        return Duration.between(begin(), end());
    }
}
