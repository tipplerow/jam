
package jam.util;

import org.junit.*;
import static org.junit.Assert.*;

class IntWrapper {
    private int value;

    public IntWrapper(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class AutoMapTest {
    @Test(expected = RuntimeException.class) 
    public void testBadGet() {
        AutoMap<Integer, String> map = AutoMap.tree(k -> k.toString());
        map.get("foo");
    }

    @Test public void testTree() {
        AutoMap<Integer, IntWrapper> map = AutoMap.tree(k -> new IntWrapper(k.intValue()));

        assertEquals(0, map.size());
        assertFalse(map.containsKey(88));

        IntWrapper w1 = map.get(88);
        IntWrapper w2 = map.get(88);

        assertEquals(88, w1.getValue());
        assertEquals(88, w2.getValue());

        // Verify that the second call to "get" returned the object
        // created by the first call...
        assertTrue(w1 == w2);

        // Now remove the first instance and make sure we get a new
        // instance...
        map.remove(88);
        assertFalse(map.containsKey(88));

        IntWrapper w3 = map.get(88);
        IntWrapper w4 = map.get(88);

        assertEquals(88, w3.getValue());
        assertEquals(88, w4.getValue());

        assertTrue(w3 == w4);
        assertTrue(w3 != w1);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.AutoMapTest");
    }
}
