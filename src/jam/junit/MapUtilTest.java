
package jam.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jam.util.MapUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class MapUtilTest {
    @Test public void testGet() {
        Map<String, Integer> map =
            MapUtil.zipHash(List.of("abc", "def", "ghi"), List.of(1, 2, 3));

        List<Integer> values = MapUtil.get(map, List.of("def", "ghi", "foo", "abc"));

        assertEquals(Integer.valueOf(2), values.get(0));
        assertEquals(Integer.valueOf(3), values.get(1));
        assertEquals(Integer.valueOf(1), values.get(3));
        assertNull(values.get(2));
    }

    @Test public void testGroup() {
        List<String> strings = List.of("I", "did", "my", "taxes", "today", "it", "was", "not", "fun");
        Map<Integer, Collection<String>> grouped = MapUtil.group(strings, x -> x.length());

        assertEquals(4, grouped.size());

        assertEquals(grouped.get(1), List.of("I"));
        assertEquals(grouped.get(2), List.of("my", "it"));
        assertEquals(grouped.get(3), List.of("did", "was", "not", "fun"));
        assertEquals(grouped.get(5), List.of("taxes", "today"));

        assertFalse(grouped.containsKey(0));
        assertFalse(grouped.containsKey(4));
        assertFalse(grouped.containsKey(6));
    }

    @Test public void testPutUnique1() {
        Map<String, Integer> map = new TreeMap<String, Integer>();

        MapUtil.putUnique(map, "abc", 1);
        MapUtil.putUnique(map, "def", 2);

        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(1), map.get("abc"));
        assertEquals(Integer.valueOf(2), map.get("def"));
    }

    @Test(expected = RuntimeException.class)
    public void testPutUnique2() {
        Map<String, Integer> map = new TreeMap<String, Integer>();

        MapUtil.putUnique(map, "abc", 1);
        MapUtil.putUnique(map, "abc", 2);
    }

    @Test public void testZip() {
        List<String>  keys   = List.of("abc", "def", "ghi");
        List<Integer> values = List.of(1, 2, 3);

        Map<String, Integer> map = MapUtil.zipHash(keys, values);

        assertEquals(3, map.size());
        assertEquals(Integer.valueOf(1), map.get("abc"));
        assertEquals(Integer.valueOf(2), map.get("def"));
        assertEquals(Integer.valueOf(3), map.get("ghi"));

        map = MapUtil.zipHash(keys.toArray(new String[3]), values.toArray(new Integer[3]));

        assertEquals(3, map.size());
        assertEquals(Integer.valueOf(1), map.get("abc"));
        assertEquals(Integer.valueOf(2), map.get("def"));
        assertEquals(Integer.valueOf(3), map.get("ghi"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MapUtilTest");
    }
}
