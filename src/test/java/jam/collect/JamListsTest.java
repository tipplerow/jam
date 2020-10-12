
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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.collect.JamListsTest");
    }
}
