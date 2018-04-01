
package jam.junit;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import org.junit.*;
import static org.junit.Assert.*;

public class Object2LongOpenHashMapTest {
    @Test public void testBasic() {
        Object2LongOpenHashMap<String> map = new Object2LongOpenHashMap<String>();

        assertEquals(0, map.defaultReturnValue());

        assertFalse(map.containsKey("abc"));
        assertEquals(0, map.getLong("abc"));

        map.put("abc", 3);
        assertEquals(3, map.getLong("abc"));

        map.addTo("abc", -7);
        assertEquals(-4, map.getLong("abc"));

        map.addTo("def", 5);
        assertEquals(5, map.getLong("def"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.Object2LongOpenHashMapTest");
    }
}
