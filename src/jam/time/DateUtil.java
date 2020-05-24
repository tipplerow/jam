
package jam.time;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Provides utility methods operating on {@code LocalDate} objects.
 */
public final class DateUtil {
    /**
     * Identifies weekdays.
     *
     * @param date a date of interest.
     *
     * @return {@code true} iff the input date falls on a weekday.
     */
    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    /**
     * Identifies weekends.
     *
     * @param date a date of interest.
     *
     * @return {@code true} iff the input date falls on a weekend.
     */
    public static boolean isWeekend(LocalDate date) {
        switch (date.getDayOfWeek()) {
        case SATURDAY:
            return true;

        case SUNDAY:
            return true;

        default:
            return false;
        }
    }

    /**
     * Returns the next calendar date.
     *
     * @param date the date to advance.
     *
     * @return the next calendar date after the input date.
     */
    public static LocalDate next(LocalDate date) {
        return date.plusDays(1);
    }

    /**
     * Returns the next weekday after a given date.
     *
     * @param date the date to advance.
     *
     * @return the next weekday after the input date.
     */
    public static LocalDate nextWeekday(LocalDate date) {
        switch (date.getDayOfWeek()) {
        case FRIDAY:
            return date.plusDays(3);

        case SATURDAY:
            return date.plusDays(2);

        default:
            return date.plusDays(1);
        }
    }

    /**
     * Returns the previous calendar date.
     *
     * @param date the date of interest.
     *
     * @return the latest calendar date before the input date.
     */
    public static LocalDate prev(LocalDate date) {
        return date.minusDays(1);
    }

    /**
     * Returns the previous weekday before a given date.
     *
     * @param date the date of interest.
     *
     * @return the latest weekday before the input date.
     */
    public static LocalDate prevWeekday(LocalDate date) {
        switch (date.getDayOfWeek()) {
        case MONDAY:
            return date.minusDays(3);

        case SUNDAY:
            return date.minusDays(2);

        default:
            return date.minusDays(1);
        }
    }
}


