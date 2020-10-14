
package jam.time;

import java.time.LocalTime;

import org.junit.*;
import static org.junit.Assert.*;

public class TimeRangeTest {
    LocalTime BEGIN = LocalTime.of( 9, 30);
    LocalTime END   = LocalTime.of(16, 15);
    TimeRange RANGE = TimeRange.create(BEGIN, END);

    @Test public void testContains() {
        assertFalse(RANGE.contains(LocalTime.MIDNIGHT));
        assertFalse(RANGE.contains(LocalTime.of(9, 29, 59)));
        assertTrue(RANGE.contains(BEGIN));
        assertTrue(RANGE.contains(LocalTime.of(9, 30, 1)));
        assertTrue(RANGE.contains(LocalTime.NOON));
        assertTrue(RANGE.contains(LocalTime.of(16, 14, 59)));
        assertFalse(RANGE.contains(END));
        assertFalse(RANGE.contains(LocalTime.of(16, 15, 1)));
        assertFalse(RANGE.contains(LocalTime.MAX));
    }

    @Test public void testDuration() {
        assertTrue(TimeRange.EMPTY.duration().isZero());
        assertEquals(0, TimeRange.EMPTY.duration().toSeconds());

        assertFalse(RANGE.duration().isZero());
        assertEquals(6 * 60 + 45 , RANGE.duration().toMinutes());
    }

    @Test public void testEmpty() {
        assertFalse(TimeRange.EMPTY.contains(LocalTime.MIDNIGHT));
        assertFalse(TimeRange.EMPTY.contains(LocalTime.NOON));
        assertFalse(TimeRange.EMPTY.contains(LocalTime.MAX));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.time.TimeRangeTest");
    }
}
