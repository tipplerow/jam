
package jam.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jam.util.MapUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class MapUtilTest {
    @Test public void testGroup() {
        List<String> strings = Arrays.asList("I", "did", "my", "taxes", "today", "it", "was", "not", "fun");
        Map<Integer, Collection<String>> grouped = MapUtil.group(strings, x -> x.length());

        assertEquals(4, grouped.size());

        assertEquals(grouped.get(1), Arrays.asList("I"));
        assertEquals(grouped.get(2), Arrays.asList("my", "it"));
        assertEquals(grouped.get(3), Arrays.asList("did", "was", "not", "fun"));
        assertEquals(grouped.get(5), Arrays.asList("taxes", "today"));

        assertFalse(grouped.containsKey(0));
        assertFalse(grouped.containsKey(4));
        assertFalse(grouped.containsKey(6));
    }

    @Test public void testZip() {
        List<String>  keys   = Arrays.asList("abc", "def", "ghi");
        List<Integer> values = Arrays.asList(1, 2, 3);

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
