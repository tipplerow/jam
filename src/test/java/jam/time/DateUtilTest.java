
package jam.time;

import java.time.DayOfWeek;
import java.time.LocalDate;

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

    @Test public void testNextDayOfWeek() {
        LocalDate SAT1 = SAT;
        LocalDate SAT2 = SAT.plusDays(7);
        
        assertEquals(SAT1, DateUtil.nextDayOfWeek(WED, DayOfWeek.SATURDAY));
        assertEquals(SAT1, DateUtil.nextDayOfWeek(THU, DayOfWeek.SATURDAY));
        assertEquals(SAT1, DateUtil.nextDayOfWeek(FRI, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.nextDayOfWeek(SAT, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.nextDayOfWeek(SUN, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.nextDayOfWeek(MON, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.nextDayOfWeek(TUE, DayOfWeek.SATURDAY));
    }

    @Test public void testNextWeekday() {
        assertEquals(THU, DateUtil.nextWeekday(WED));
        assertEquals(FRI, DateUtil.nextWeekday(THU));
        assertEquals(MON, DateUtil.nextWeekday(FRI));
        assertEquals(MON, DateUtil.nextWeekday(SAT));
        assertEquals(MON, DateUtil.nextWeekday(SUN));
        assertEquals(TUE, DateUtil.nextWeekday(MON));
    }

    @Test public void testOnOrNextDayOfWeek() {
        LocalDate SAT1 = SAT;
        LocalDate SAT2 = SAT.plusDays(7);
        
        assertEquals(SAT1, DateUtil.onOrNext(WED, DayOfWeek.SATURDAY));
        assertEquals(SAT1, DateUtil.onOrNext(THU, DayOfWeek.SATURDAY));
        assertEquals(SAT1, DateUtil.onOrNext(FRI, DayOfWeek.SATURDAY));
        assertEquals(SAT1, DateUtil.onOrNext(SAT, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.onOrNext(SUN, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.onOrNext(MON, DayOfWeek.SATURDAY));
        assertEquals(SAT2, DateUtil.onOrNext(TUE, DayOfWeek.SATURDAY));
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
        org.junit.runner.JUnitCore.main("jam.time.DateUtilTest");
    }
}
