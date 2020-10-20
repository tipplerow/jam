
package jam.collect;

import java.time.LocalDate;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public final class MultimapJoinIndexTest {
    private static final String name1 = "abc";
    private static final String name2 = "def";

    private static final LocalDate date1 = LocalDate.parse("2020-01-01");
    private static final LocalDate date2 = LocalDate.parse("2020-12-31");
    private static final LocalDate date3 = LocalDate.parse("2112-12-21");

    private static final CompKey comp11 = new CompKey(name1, date1);
    private static final CompKey comp12 = new CompKey(name1, date2);
    private static final CompKey comp13 = new CompKey(name1, date3);
    private static final CompKey comp21 = new CompKey(name2, date1);
    private static final CompKey comp22 = new CompKey(name2, date2);
    private static final CompKey comp23 = new CompKey(name2, date3);

    @Test public void testAll() {
        CompKeyJoinIndex index = CompKeyJoinIndex.create();

        assertEquals(Set.of(), index.selectKey1(name1));
        assertEquals(Set.of(), index.selectKey1(name2));
        assertEquals(Set.of(), index.selectKey2(date1));
        assertEquals(Set.of(), index.selectKey2(date2));
        assertEquals(Set.of(), index.selectKey2(date3));

        index.add(comp11);

        assertEquals(Set.of(comp11), index.selectKey1(name1));
        assertEquals(Set.of(), index.selectKey1(name2));
        assertEquals(Set.of(comp11), index.selectKey2(date1));
        assertEquals(Set.of(), index.selectKey2(date2));
        assertEquals(Set.of(), index.selectKey2(date3));

        index.add(comp23);

        assertEquals(Set.of(comp11), index.selectKey1(name1));
        assertEquals(Set.of(comp23), index.selectKey1(name2));
        assertEquals(Set.of(comp11), index.selectKey2(date1));
        assertEquals(Set.of(), index.selectKey2(date2));
        assertEquals(Set.of(comp23), index.selectKey2(date3));

        index.add(comp11);
        index.add(comp12);
        index.add(comp13);
        index.add(comp21);
        index.add(comp22);
        index.add(comp23);

        assertEquals(Set.of(comp11, comp12, comp13), index.selectKey1(name1));
        assertEquals(Set.of(comp21, comp22, comp23), index.selectKey1(name2));

        assertEquals(Set.of(comp11, comp21), index.selectKey2(date1));
        assertEquals(Set.of(comp12, comp22), index.selectKey2(date2));
        assertEquals(Set.of(comp13, comp23), index.selectKey2(date3));

        index.remove(comp12);

        assertEquals(Set.of(comp11,         comp13), index.selectKey1(name1));
        assertEquals(Set.of(comp21, comp22, comp23), index.selectKey1(name2));

        assertEquals(Set.of(comp11, comp21), index.selectKey2(date1));
        assertEquals(Set.of(        comp22), index.selectKey2(date2));
        assertEquals(Set.of(comp13, comp23), index.selectKey2(date3));

        index.remove(comp11);
        index.remove(comp23);

        assertEquals(Set.of(                comp13), index.selectKey1(name1));
        assertEquals(Set.of(comp21, comp22),         index.selectKey1(name2));

        assertEquals(Set.of(        comp21), index.selectKey2(date1));
        assertEquals(Set.of(        comp22), index.selectKey2(date2));
        assertEquals(Set.of(comp13),         index.selectKey2(date3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.MultimapJoinIndexTest");
    }
}
