
package jam.junit;

import java.util.Arrays;
import java.util.List;

import jam.util.ListMap;

import org.junit.*;
import static org.junit.Assert.*;

public class ListMapTest {
    @Test public void testCat() {
        ListMap<Integer, String> listMap = ListMap.tree();

        listMap.list(1).addAll(Arrays.asList("abc", "def", "ghi"));
        listMap.list(2).addAll(Arrays.asList("foo", "bar"));
        listMap.list(3).add("zoo");

        assertEquals(Arrays.asList("abc", "def", "ghi", "foo", "bar", "zoo"), listMap.cat());
    }

    @Test public void testList() {
        ListMap<Integer, String> listMap = ListMap.tree();

        assertEquals(0, listMap.size());
        assertFalse(listMap.containsKey(88));
        assertNull(listMap.get(88));

        List<String> list = listMap.list(88);
        assertEquals(0, list.size());
        assertEquals(1, listMap.size());

        listMap.list(88).add("abc");
        listMap.list(88).add("def");

        assertEquals(Arrays.asList("abc", "def"), listMap.get(88));
        assertEquals(Arrays.asList("abc", "def"), listMap.list(88));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ListMapTest");
    }
}
