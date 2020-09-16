
package jam.util;

import java.util.HashMap;
import java.util.TreeMap;

import org.junit.*;
import static org.junit.Assert.*;

public class MapFactoryTest {
    private static final Integer ONE   = Integer.valueOf(1);
    private static final Integer TWO   = Integer.valueOf(2);
    private static final Integer THREE = Integer.valueOf(3);

    private static final String[] KEYS = new String[] { "one", "two", "three" };
    private static final Integer[] VALUES = new Integer[] { ONE, TWO, THREE };

    @Test public void testHash() {
        HashMap<String, Integer> map;

        map = MapFactory.hash();
        assertTrue(map.isEmpty());

        map = MapFactory.hash(KEYS, VALUES);
        assertEquals(KEYS.length, map.size());

        for (int k = 0; k < KEYS.length; k++)
            assertEquals(VALUES[k], map.get(KEYS[k]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashInvalid() {
        MapFactory.hash(new String[] { "abc", "def" }, new Integer[] { 1, 2, 3, 4 });
    }

    @Test public void testTree() {
        TreeMap<String, Integer> map;

        map = MapFactory.tree();
        assertTrue(map.isEmpty());

        map = MapFactory.tree(KEYS, VALUES);
        assertEquals(KEYS.length, map.size());

        for (int k = 0; k < KEYS.length; k++)
            assertEquals(VALUES[k], map.get(KEYS[k]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTreeInvalid() {
        MapFactory.tree(new String[] { "abc", "def" }, new Integer[] { 1, 2, 3, 4 });
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.MapFactoryTest");
    }
}
