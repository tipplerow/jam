
package jam.lang;

import java.util.Iterator;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

class RegObj {
    public final String key;
    public final int value;

    public RegObj(String key, int value) {
        this.key = key;
        this.value = value;
    }
}

class Registry extends ObjectRegistry<String, RegObj> {
    public Registry() {
        super(String.class, RegObj.class, x -> x.key);
    }
}

public class ObjectRegistryTest {
    private final String key1 = "abc";
    private final String key2 = "def";
    private final String key3 = "ghi";

    private final RegObj obj1 = new RegObj(key1, 1);
    private final RegObj obj2 = new RegObj(key2, 2);
    private final RegObj obj3 = new RegObj(key3, 3);

    @Test public void testAdd() {
        Registry registry = new Registry();

        assertFalse(registry.contains(key1));
        assertFalse(registry.contains(obj1));
        assertFalse(registry.contains(key2));
        assertFalse(registry.contains(obj2));

        assertTrue(registry.add(obj1));

        assertTrue(registry.contains(key1));
        assertTrue(registry.contains(obj1));
        assertFalse(registry.contains(key2));
        assertFalse(registry.contains(obj2));
    }

    @Test(expected = RuntimeException.class)
    public void testAddDuplicate() {
        Registry registry = new Registry();
        registry.add(obj1);
        registry.add(obj1);
    }

    @Test public void testAddAll() {
        Registry registry = new Registry();
        registry.addAll(List.of(obj1, obj2, obj3));

        assertEquals(3, registry.size());
        assertTrue(registry.contains(key1));
        assertTrue(registry.contains(obj1));
        assertTrue(registry.contains(key2));
        assertTrue(registry.contains(obj2));
        assertTrue(registry.contains(key3));
        assertTrue(registry.contains(obj3));
    }

    @Test public void testGet() {
        Registry registry = new Registry();

        assertTrue(registry.add(obj1));
        assertTrue(registry.add(obj2));

        assertEquals(obj1, registry.get(key1));
        assertEquals(obj2, registry.get(key2));
        assertNull(registry.get(key3));
    }

    @Test public void testIterator() {
        Registry registry = new Registry();
        registry.addAll(List.of(obj1, obj2, obj3));

        Iterator<RegObj> iterator = registry.iterator();

        assertTrue(iterator.hasNext());

        RegObj obj = iterator.next();
        assertTrue(registry.contains(obj));

        iterator.remove();
        assertFalse(registry.contains(obj));

        iterator.next();
        iterator.next();

        assertFalse(iterator.hasNext());
    }

    @Test public void testRemove() {
        Registry registry = new Registry();

        assertTrue(registry.add(obj1));
        assertTrue(registry.add(obj2));

        assertTrue(registry.remove(key1));
        assertFalse(registry.remove(key1));

        assertTrue(registry.remove(obj2));
        assertFalse(registry.remove(obj2));

        assertFalse(registry.remove(key3));
        assertFalse(registry.remove(obj3));
    }

    @Test public void testSize() {
        Registry registry = new Registry();

        assertEquals(0, registry.size());
        assertTrue(registry.isEmpty());

        registry.add(obj1);

        assertEquals(1, registry.size());
        assertFalse(registry.isEmpty());

        registry.add(obj2);

        assertEquals(2, registry.size());
        assertFalse(registry.isEmpty());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.ObjectRegistryTest");
    }
}
