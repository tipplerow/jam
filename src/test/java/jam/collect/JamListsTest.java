
package jam.collect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public final class JamListsTest {
    private static Iterable<String> iterable() {
        return List.of("A", "B", "C");
    }

    @Test public void testArrayList() {
        assertEquals(List.of("A", "B", "C"), JamLists.arrayList(iterable()));
    }

    @Test public void testLinkedList() {
        assertEquals(List.of("A", "B", "C"), JamLists.linkedList(iterable()));
    }

    @Test public void testSelect() {
        int N = 10;
        List<Integer> list = new ArrayList<Integer>(N);

        for (int k = 0; k < N; k++)
            list.add(k);

        int niter     = 1000000;
        int expected  = niter / N;
        int tolerance = 1000;

        int[] count = new int[N];
        
        for (int k = 0; k < niter; k++)
            ++count[JamLists.select(list)];

        for (int k = 0; k < N; k++)
            assertTrue(expected - tolerance < count[k] && count[k] < expected + tolerance);

        list = List.of(123);
        assertEquals(Integer.valueOf(123), JamLists.select(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectEmpty() {
        JamLists.select(List.of());
    }

    @Test public void testSwap() {
        List<String> list = new ArrayList<String>(List.of("A" ,"B", "C", "D"));

        JamLists.swap(list, 1, 3);
        assertEquals(List.of("A" ,"D", "C", "B"), list);

        JamLists.swap(list, 1, 3);
        assertEquals(List.of("A" ,"B", "C", "D"), list);

        JamLists.swap(list, 3, 1);
        assertEquals(List.of("A" ,"D", "C", "B"), list);

        JamLists.swap(list, 3, 1);
        assertEquals(List.of("A" ,"B", "C", "D"), list);

        JamLists.swap(list, 0, 1);
        assertEquals(List.of("B" ,"A", "C", "D"), list);

        JamLists.swap(list, 3, 0);
        assertEquals(List.of("D" ,"A", "C", "B"), list);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.JamListsTest");
    }
}
