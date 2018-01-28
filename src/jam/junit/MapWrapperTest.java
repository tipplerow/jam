
package jam.junit;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jam.util.MapWrapper;

import org.junit.*;
import static org.junit.Assert.*;

class IntStrMap extends MapWrapper<Integer, String> {
    public IntStrMap() {
        super(new TreeMap<Integer, String>());
    }
}

public class MapWrapperTest {
    private static IntStrMap createMap() {
        IntStrMap map = new IntStrMap();

        map.put(1, "abc");
        map.put(2, "def");

        return map;
    }

    @Test public void testClear() {
        IntStrMap map = createMap();

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test public void testContains() {
        IntStrMap map = createMap();

        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));

        assertFalse(map.containsKey(0));
        assertFalse(map.containsKey(3));

        assertTrue(map.containsValue("abc"));
        assertTrue(map.containsValue("def"));

        assertFalse(map.containsValue("foo"));
        assertFalse(map.containsValue("bar"));
    }

    @Test public void testEntrySet() {
        IntStrMap map = createMap();

        Set<Map.Entry<Integer, String>> set = map.entrySet();
        assertEquals(2, set.size());
    }

    @Test public void testGet() {
        IntStrMap map = createMap();

        assertNull(map.get(0));
        assertNull(map.get(3));

        assertEquals("abc", map.get(1));
        assertEquals("def", map.get(2));
    }

    @Test public void testKeySet() {
        IntStrMap map = createMap();

        Set<Integer> set = map.keySet();
        assertEquals(2, set.size());

        set.remove(1);
        assertEquals(1, map.size());
        assertFalse(map.containsKey(1));

        set.remove(2);
        assertEquals(0, map.size());
        assertFalse(map.containsKey(2));
    }

    @Test public void testRemove() {
        IntStrMap map = createMap();

        map.remove(1);
        assertEquals(1, map.size());
        assertFalse(map.containsKey(1));

        map.remove(2);
        assertEquals(0, map.size());
        assertFalse(map.containsKey(2));
    }

    @Test public void testSize() {
        IntStrMap map = createMap();

        map.put(3, "foo");
        map.put(4, "foo");

        assertEquals(4, map.size());
    }

    @Test public void testValues() {
        IntStrMap map = createMap();
        Collection<String> values = map.values();

        assertEquals(2, values.size());
        assertTrue(values.contains("abc"));
        assertTrue(values.contains("def"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MapWrapperTest");
    }
}
