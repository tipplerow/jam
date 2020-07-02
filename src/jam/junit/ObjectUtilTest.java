
package jam.junit;

import java.util.HashSet;

import jam.lang.ObjectUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class ObjectUtilTest {
        
    @SuppressWarnings("deprecation")
    @Test public void testEquals() {
        Object o1 = new Integer(1);
        Object o2 = new Integer(1);
        Object o3 = new Double(3.3);
        
        assertTrue(ObjectUtil.equals(null, null));

        assertFalse(ObjectUtil.equals(null, o1));
        assertFalse(ObjectUtil.equals(null, o2));
        assertFalse(ObjectUtil.equals(null, o3));

        assertFalse(ObjectUtil.equals(o1, null));
        assertFalse(ObjectUtil.equals(o2, null));
        assertFalse(ObjectUtil.equals(o3, null));

        assertTrue(ObjectUtil.equals(o1, o1));
        assertTrue(ObjectUtil.equals(o1, o2));
        assertFalse(ObjectUtil.equals(o1, o3));
    }

    @Test public void testEqualsClass() {
        Object o1 = "abc";
        Object o2 = "def";
        Object o3 = Integer.valueOf(1);
        Object o4 = Integer.valueOf(2);

        assertTrue(ObjectUtil.equalsClass(null, null));

        assertFalse(ObjectUtil.equalsClass(o1, null));
        assertFalse(ObjectUtil.equalsClass(null, o1));

        assertTrue(ObjectUtil.equalsClass(o1, o1));
        assertTrue(ObjectUtil.equalsClass(o1, o2));
        assertFalse(ObjectUtil.equalsClass(o1, o3));
        assertFalse(ObjectUtil.equalsClass(o1, o4));
    }

    @Test public void testHasDefaultConstructor() {
        assertTrue(ObjectUtil.hasDefaultConstructor(String.class));
        assertFalse(ObjectUtil.hasDefaultConstructor(Integer.class));
    }
    
    @Test public void testLike() {
        String string = new String();
        StringBuilder builder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<String>();
        
        assertTrue(ObjectUtil.like(string) instanceof String);
        assertTrue(ObjectUtil.like(builder) instanceof StringBuilder);
        assertTrue(ObjectUtil.like(hashSet) instanceof HashSet);
    }

    @SuppressWarnings("unchecked")
    @Test public void testNewInstance() {
        assertEquals("", ObjectUtil.newInstance(String.class)); 
        assertEquals(new HashSet<Integer>(), ObjectUtil.newInstance(HashSet.class));

        HashSet<Integer> set = (HashSet<Integer>) ObjectUtil.newInstance(HashSet.class);
        set.add(1);
        assertEquals(1, set.size());
        assertTrue(set.contains(1));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ObjectUtilTest");
    }
}
