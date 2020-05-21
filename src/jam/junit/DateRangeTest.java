
package jam.junit;

import java.time.LocalDate;

import jam.time.DateRange;

import org.junit.*;
import static org.junit.Assert.*;

public class DateRangeTest {
    private static final LocalDate LOWER = LocalDate.parse("2020-03-17");
    private static final LocalDate UPPER = LocalDate.parse("2020-05-21");

    private static final LocalDate LOWER_PREV = LOWER.minusDays(1);
    private static final LocalDate LOWER_NEXT = LOWER.plusDays(1);

    private static final LocalDate UPPER_PREV = UPPER.minusDays(1);
    private static final LocalDate UPPER_NEXT = UPPER.plusDays(1);

    @Test public void testClosed() {
        DateRange range = DateRange.closed(LOWER, UPPER);

	assertFalse(range.contains(LOWER_PREV));
	assertTrue( range.contains(LOWER));
	assertTrue( range.contains(LOWER_NEXT));
	assertTrue( range.contains(UPPER_PREV));
        assertTrue( range.contains(UPPER));
	assertFalse(range.contains(UPPER_NEXT));
    }

    @Test public void testLeftClosed() {
        DateRange range = DateRange.leftClosed(LOWER, UPPER);

	assertFalse(range.contains(LOWER_PREV));
	assertTrue( range.contains(LOWER));
	assertTrue( range.contains(LOWER_NEXT));
	assertTrue( range.contains(UPPER_PREV));
        assertFalse(range.contains(UPPER));
	assertFalse(range.contains(UPPER_NEXT));
    }

    @Test public void testLeftOpen() {
        DateRange range = DateRange.leftOpen(LOWER, UPPER);

	assertFalse(range.contains(LOWER_PREV));
	assertFalse(range.contains(LOWER));
	assertTrue( range.contains(LOWER_NEXT));
	assertTrue( range.contains(UPPER_PREV));
        assertTrue( range.contains(UPPER));
	assertFalse(range.contains(UPPER_NEXT));
    }

    @Test public void testOpen() {
        DateRange range = DateRange.open(LOWER, UPPER);

	assertFalse(range.contains(LOWER_PREV));
	assertFalse(range.contains(LOWER));
	assertTrue( range.contains(LOWER_NEXT));
	assertTrue( range.contains(UPPER_PREV));
        assertFalse(range.contains(UPPER));
	assertFalse(range.contains(UPPER_NEXT));
    }

    @Test public void testEmpty() {
        DateRange range = DateRange.EMPTY;

	assertFalse(range.contains(LOWER_PREV));
	assertFalse(range.contains(LOWER));
	assertFalse(range.contains(LOWER_NEXT));
	assertFalse(range.contains(UPPER_PREV));
        assertFalse(range.contains(UPPER));
	assertFalse(range.contains(UPPER_NEXT));
    }

    @Test public void testInfinite() {
        DateRange range = DateRange.INFINITE;

	assertTrue(range.contains(LOWER_PREV));
	assertTrue(range.contains(LOWER));
	assertTrue(range.contains(LOWER_NEXT));
	assertTrue(range.contains(UPPER_PREV));
        assertTrue(range.contains(UPPER));
	assertTrue(range.contains(UPPER_NEXT));
    }

    @Test public void testTrailingWeek() {
        LocalDate upperDate = LocalDate.parse("2020-03-06");
        DateRange dateRange = DateRange.trailingWeek(upperDate);

        assertFalse(dateRange.contains(LocalDate.parse("2020-02-28")));
        assertTrue( dateRange.contains(LocalDate.parse("2020-02-29")));
        assertTrue( dateRange.contains(LocalDate.parse("2020-03-06")));
        assertFalse(dateRange.contains(LocalDate.parse("2020-03-07")));
    }

    @Test public void testTrailingMonth() {
        LocalDate upperDate = LocalDate.parse("2020-03-31");
        DateRange dateRange = DateRange.trailingMonth(upperDate);

        assertFalse(dateRange.contains(LocalDate.parse("2020-02-29")));
        assertTrue( dateRange.contains(LocalDate.parse("2020-03-01")));
        assertTrue( dateRange.contains(LocalDate.parse("2020-03-31")));
        assertFalse(dateRange.contains(LocalDate.parse("2020-04-01")));
    }

    @Test public void testTrailingYear() {
        LocalDate upperDate = LocalDate.parse("2020-02-29");
        DateRange dateRange = DateRange.trailingYear(upperDate);

        assertFalse(dateRange.contains(LocalDate.parse("2019-02-28")));
        assertTrue( dateRange.contains(LocalDate.parse("2019-03-01")));
        assertTrue( dateRange.contains(LocalDate.parse("2020-02-29")));
        assertFalse(dateRange.contains(LocalDate.parse("2020-03-01")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DateRangeTest");
    }
}
