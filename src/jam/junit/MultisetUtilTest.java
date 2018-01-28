
package jam.junit;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import jam.lang.ObjectFactory;
import jam.util.MultisetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class MultisetUtilTest extends NumericTestBase {
    private final Multiset<String> set;

    private static final List<String> list1 = Arrays.asList("A");
    private static final List<String> list2 = Arrays.asList("A", "B");
    private static final List<String> list3 = Arrays.asList("A", "B", "C");

    private static final List<List<String>> lists = Arrays.asList(list1, list2, list3);

    public MultisetUtilTest() {
        set = TreeMultiset.create();

        set.add("abc", 1);
        set.add("def", 2);
        set.add("ghi", 3);
    }

    @Test public void testCountUnique() {
        assertEquals(3, MultisetUtil.countUnique(set));
    }

    @Test public void testFactory() {
        ObjectFactory<HashMultiset<Integer>> hashFactory = MultisetUtil.hashFactory();
        ObjectFactory<TreeMultiset<String>>  treeFactory = MultisetUtil.treeFactory();

        Multiset<Integer> hashSet = hashFactory.newInstance();
        Multiset<String>  treeSet = treeFactory.newInstance();

        assertTrue(hashSet.isEmpty());
        assertTrue(treeSet.isEmpty());

        hashSet.add(1);
        hashSet.add(2);
        hashSet.add(2);
        hashSet.add(3);
        hashSet.add(3);
        hashSet.add(3);

        assertEquals(0, hashSet.count(0));
        assertEquals(1, hashSet.count(1));
        assertEquals(2, hashSet.count(2));
        assertEquals(3, hashSet.count(3));
        assertEquals(6, hashSet.size());

        treeSet.add("A");
        treeSet.add("B");
        treeSet.add("B");
        treeSet.add("C");
        treeSet.add("C");
        treeSet.add("C");

        assertEquals(0, treeSet.count(""));
        assertEquals(1, treeSet.count("A"));
        assertEquals(2, treeSet.count("B"));
        assertEquals(3, treeSet.count("C"));
        assertEquals(6, treeSet.size());
    }

    @Test public void testFrequency() {
        assertDouble(0.0, MultisetUtil.frequency(set, "foo"));
        assertDouble(0.0, MultisetUtil.frequency(set, Integer.valueOf(33)));

        assertDouble(1.0 / 6.0, MultisetUtil.frequency(set, "abc"));
        assertDouble(2.0 / 6.0, MultisetUtil.frequency(set, "def"));
        assertDouble(3.0 / 6.0, MultisetUtil.frequency(set, "ghi"));
    }

    @SuppressWarnings("unchecked")
    @Test public void testHash() {
        HashMultiset<String> set1 = MultisetUtil.hash(lists);
        HashMultiset<String> set2 = MultisetUtil.hash(list3, list2, list1);

        assertEquals(set1, set2);

        assertEquals(6, set1.size());
        assertEquals(3, set1.count("A"));
        assertEquals(2, set1.count("B"));
        assertEquals(1, set1.count("C"));
    }

    @Test public void testIntegerCount() {
        Multiset<Integer> set1 = MultisetUtil.hash(10, 20, 30, 20, 30, 30);
        Multiset<Integer> set2 = MultisetUtil.tree(10, 20, 30, 20, 30, 30);

        assertEquals(set1, set2);
        assertEquals(6, set1.size());
        assertEquals(1, set1.count(10));
        assertEquals(2, set1.count(20));
        assertEquals(3, set1.count(30));
    }

    @Test public void testIterator() {
        //
        // Ensuring that the iterator returns each OCCURRENCE of each
        // item, not just each unique item...
        //
        Iterator<String> iter = set.iterator();

        assertEquals("abc", iter.next());
        assertEquals("def", iter.next());
        assertEquals("def", iter.next());
        assertEquals("ghi", iter.next());
        assertEquals("ghi", iter.next());
        assertEquals("ghi", iter.next());

        assertFalse(iter.hasNext());
    }

    @Test public void testSize() {
        //
        // Ensuring that the size includes each OCCURRENCE of each
        // item, not just each unique item...
        //
        assertEquals(6, set.size());
    }

    @SuppressWarnings("unchecked")
    @Test public void testTree() {
        TreeMultiset<String> set1 = MultisetUtil.tree(lists);
        TreeMultiset<String> set2 = MultisetUtil.tree(list3, list2, list1);

        assertEquals(set1, set2);

        assertEquals(6, set1.size());
        assertEquals(3, set1.count("A"));
        assertEquals(2, set1.count("B"));
        assertEquals(1, set1.count("C"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MultisetUtilTest");
    }
}
