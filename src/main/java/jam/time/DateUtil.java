
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
     * Returns the earliest date after a given date having a specified
     * day of the week. This method <em>always</em> advances the input
     * date.
     *
     * @param date the date to advance.
     *
     * @param dow the desired day of the week.
     *
     * @return the earliest date after the given date having the specified
     * day of the week.
     */
    public static LocalDate nextDayOfWeek(LocalDate date, DayOfWeek dow) {
        do {
            date = next(date);
        } while (!date.getDayOfWeek().equals(dow));

        return date;
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
     * Returns the earliest date on or after a given date that falls
     * on a specified day of the week.  This method will not advance
     * the input date if it already falls on the desired day of the
     * week.
     *
     * @param date the date to advance (if necessary).
     *
     * @param dow the desired day of the week.
     *
     * @return either the input date, if it falls on the desired day
     * of the week, or the earliest date after the input date that
     * falls on the desired day of the week.
     */
    public static LocalDate onOrNext(LocalDate date, DayOfWeek dow) {
        while (!date.getDayOfWeek().equals(dow))
            date = next(date);

        return date;
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

    /**
     * Returns the current date.
     *
     * @return the current date.
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns the date that will occur tomorrow.
     *
     * @return the date that will occur tomorrow.
     */
    public static LocalDate tomorrow() {
        return next(today());
    }

    /**
     * Returns the date that occurred yesterday.
     *
     * @return the date that occurred yesterday.
     */
    public static LocalDate yesterday() {
        return prev(today());
    }
}
