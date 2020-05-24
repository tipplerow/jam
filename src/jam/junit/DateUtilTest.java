
package jam.junit;

import java.time.LocalDate;

import jam.time.DateUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class DateUtilTest {
    private static final LocalDate WED = LocalDate.parse("2020-05-27");
    private static final LocalDate THU = LocalDate.parse("2020-05-28");
    private static final LocalDate FRI = LocalDate.parse("2020-05-29");
    private static final LocalDate SAT = LocalDate.parse("2020-05-30");
    private static final LocalDate SUN = LocalDate.parse("2020-05-31");
    private static final LocalDate MON = LocalDate.parse("2020-06-01");
    private static final LocalDate TUE = LocalDate.parse("2020-06-02");

    @Test public void testIsWeekday() {
        assertTrue(DateUtil.isWeekday(MON));
        assertTrue(DateUtil.isWeekday(TUE));
        assertTrue(DateUtil.isWeekday(WED));
        assertTrue(DateUtil.isWeekday(THU));
        assertTrue(DateUtil.isWeekday(FRI));
        assertFalse(DateUtil.isWeekday(SAT));
        assertFalse(DateUtil.isWeekday(SUN));
    }

    @Test public void testIsWeekend() {
        assertFalse(DateUtil.isWeekend(MON));
        assertFalse(DateUtil.isWeekend(TUE));
        assertFalse(DateUtil.isWeekend(WED));
        assertFalse(DateUtil.isWeekend(THU));
        assertFalse(DateUtil.isWeekend(FRI));
        assertTrue(DateUtil.isWeekend(SAT));
        assertTrue(DateUtil.isWeekend(SUN));
    }

    @Test public void testNext() {
        assertEquals(THU, DateUtil.next(WED));
        assertEquals(FRI, DateUtil.next(THU));
        assertEquals(SAT, DateUtil.next(FRI));
        assertEquals(SUN, DateUtil.next(SAT));
        assertEquals(MON, DateUtil.next(SUN));
        assertEquals(TUE, DateUtil.next(MON));
    }


    @Test public void testNextWeekday() {
        assertEquals(THU, DateUtil.nextWeekday(WED));
        assertEquals(FRI, DateUtil.nextWeekday(THU));
        assertEquals(MON, DateUtil.nextWeekday(FRI));
        assertEquals(MON, DateUtil.nextWeekday(SAT));
        assertEquals(MON, DateUtil.nextWeekday(SUN));
        assertEquals(TUE, DateUtil.nextWeekday(MON));
    }

    @Test public void testPrev() {
        assertEquals(WED, DateUtil.prev(THU));
        assertEquals(THU, DateUtil.prev(FRI));
        assertEquals(FRI, DateUtil.prev(SAT));
        assertEquals(SAT, DateUtil.prev(SUN));
        assertEquals(SUN, DateUtil.prev(MON));
        assertEquals(MON, DateUtil.prev(TUE));
    }

    @Test public void testPrevWeekday() {
        assertEquals(WED, DateUtil.prevWeekday(THU));
        assertEquals(THU, DateUtil.prevWeekday(FRI));
        assertEquals(FRI, DateUtil.prevWeekday(SAT));
        assertEquals(FRI, DateUtil.prevWeekday(SUN));
        assertEquals(FRI, DateUtil.prevWeekday(MON));
        assertEquals(MON, DateUtil.prevWeekday(TUE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DateUtilTest");
    }
}
