
package jam.lang;

import java.util.ArrayList;
import java.util.Set;

import jam.util.SetUtil;

import org.junit.*;
import static org.junit.Assert.*;

class Keyed1 extends KeyedObject<String> {
    public Keyed1(String key) {
        super(key);
    }
}

class Keyed2 extends KeyedObject<String> {
    public Keyed2(String key) {
        super(key);
    }
}

class Keyed3 extends KeyedObject<Integer> {
    public Keyed3(int key) {
        super(Integer.valueOf(key));
    }
}

public class KeyedObjectTest {
    private final Keyed1 k11 = new Keyed1("abc");
    private final Keyed1 k12 = new Keyed1("abc");
    private final Keyed1 k13 = new Keyed1("def");
    private final Keyed1 k14 = new Keyed1("ghi");

    private final Keyed2 k21 = new Keyed2("abc");
    private final Keyed2 k22 = new Keyed2("abc");
    private final Keyed2 k23 = new Keyed2("def");

    private final Keyed3 k31 = new Keyed3(1);
    private final Keyed3 k32 = new Keyed3(1);
    private final Keyed3 k33 = new Keyed3(2);

    @Test public void testCompareTo() {
        assertTrue(k12.compareTo(k11) == 0);
        assertTrue(k12.compareTo(k12) == 0);
        assertTrue(k12.compareTo(k13) < 0);

        assertTrue(k13.compareTo(k11) > 0);
        assertTrue(k13.compareTo(k12) > 0);
        assertTrue(k13.compareTo(k13) == 0);
    }

    @Test(expected = ClassCastException.class)
    public void testCompareInvalid() {
        k11.compareTo(k21);
    }
        
    @Test public void testEquals() {
        assertTrue(k11.equals(k11));
        assertTrue(k11.equals(k12));
        assertFalse(k11.equals(k13));
        assertFalse(k11.equals(k21));
        assertFalse(k11.equals(k22));
        assertFalse(k11.equals(k23));
        assertFalse(k11.equals(k31));
        assertFalse(k11.equals(k32));
        assertFalse(k11.equals(k33));

        assertFalse(k21.equals(k11));
        assertFalse(k21.equals(k12));
        assertFalse(k21.equals(k13));
        assertTrue(k21.equals(k21));
        assertTrue(k21.equals(k22));
        assertFalse(k21.equals(k23));
        assertFalse(k21.equals(k31));
        assertFalse(k21.equals(k32));
        assertFalse(k21.equals(k33));

        assertFalse(k31.equals(k11));
        assertFalse(k31.equals(k12));
        assertFalse(k31.equals(k13));
        assertFalse(k31.equals(k21));
        assertFalse(k31.equals(k22));
        assertFalse(k31.equals(k23));
        assertTrue(k31.equals(k31));
        assertTrue(k31.equals(k32));
        assertFalse(k31.equals(k33));
    }

    @Test public void testHashCode() {
        assertTrue(k11.hashCode() == k12.hashCode());
        assertTrue(k11.hashCode() != k13.hashCode());

        assertTrue(k21.hashCode() == k22.hashCode());
        assertTrue(k21.hashCode() != k23.hashCode());
    }

    @Test public void testSets() {
        Set<Keyed1> hashSet = SetUtil.newHashSet(k11, k12, k13, k14, new Keyed1("abc"), new Keyed1("def"));
        Set<Keyed1> treeSet = SetUtil.newTreeSet(k14, k13, k12, k11, new Keyed1("ghi"), new Keyed1("abc"));

        assertEquals(3, hashSet.size());
        assertEquals(3, treeSet.size());
        assertEquals(hashSet, treeSet);

        // Verify that the tree set objects are in lexicographic order...
        ArrayList<Keyed1> list = new ArrayList<Keyed1>(treeSet);

        assertEquals("abc", list.get(0).getKey());
        assertEquals("def", list.get(1).getKey());
        assertEquals("ghi", list.get(2).getKey());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.KeyedObjectTest");
    }
}
