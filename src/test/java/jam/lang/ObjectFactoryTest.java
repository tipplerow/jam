
package jam.lang;

import java.util.Collection;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;

final class ZeroIntegerFactory implements ObjectFactory<Integer> {
    @SuppressWarnings("deprecation") @Override public Integer newInstance() {
        return new Integer(0);
    }
}

public class ObjectFactoryTest {
    @Test public void testCollection() {
        ObjectFactory<Integer> integerFactory = new ZeroIntegerFactory();
        Collection<Integer> integers = integerFactory.newInstances(5);

        assertEquals(5, integers.size());

        for (Integer integer : integers)
            assertEquals(0, integer.intValue());
    }

    @Test public void testForInteger() {
        ObjectFactory<Integer> factory = ObjectFactory.forInteger();

        assertEquals(Integer.valueOf(0), factory.newInstance());
        assertEquals(Integer.valueOf(1), factory.newInstance());
        assertEquals(Integer.valueOf(2), factory.newInstance());
        assertEquals(Integer.valueOf(3), factory.newInstance());
    }

    @Test public void testForString() {
        ObjectFactory<String> stringFactory = ObjectFactory.forClass(String.class);
        assertEquals("", stringFactory.newInstance());
    }

    @SuppressWarnings("unchecked")
    @Test public void testForHashSet() {
        ObjectFactory<HashSet> setFactory = ObjectFactory.forClass(HashSet.class);
        HashSet<Integer> set = (HashSet<Integer>) setFactory.newInstance();

        set.add(1);
        assertEquals(1, set.size());
        assertTrue(set.contains(1));
    }

    @Test public void testForNull() {
        ObjectFactory<Integer> nullFactory = ObjectFactory.forNull();
        assertNull(nullFactory.newInstance());
    }

    @Test public void testLikeString() {
        ObjectFactory<String> stringFactory = ObjectFactory.like("ABC");
        String newString = stringFactory.newInstance();

        assertTrue(newString instanceof String);
        assertEquals(newString, new String());
    }

    @Test public void testLikeHashSet() {
        ObjectFactory<HashSet<String>> setFactory = ObjectFactory.like(new HashSet<String>());
        HashSet<String> newSet = setFactory.newInstance();

        assertTrue(newSet instanceof HashSet);
        assertEquals(newSet, new HashSet<String>());
    }

    @Test public void testLikeNull() {
        ObjectFactory<String> nullFactory = ObjectFactory.like(null);
        assertNull(nullFactory.newInstance());
    }

    @Test public void testIntegerFactory() {
        ObjectFactory<Integer> integerFactory = new IntegerFactory();
        assertEquals(Integer.valueOf(0), integerFactory.newInstance());
    }

    @Test(expected = RuntimeException.class)
    public void testLikeIntegerFactory() {
        //
        // The Integer class has no default constructor, so the "like"
        // factory throws an exception...
        //
        ObjectFactory.like(Integer.valueOf(0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.ObjectFactoryTest");
    }
}
