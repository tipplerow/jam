
package jam.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class CollectionUtilTest extends NumericTestBase {
    private final List<String> emptyList = List.of();

    @Test public void testAddAll1() {
        List<String> list1 = List.of("A");
        List<String> list2 = List.of("A", "B");
        List<String> list3 = List.of("A", "B", "C");

        List<String> concat = new ArrayList<String>();

        CollectionUtil.addAll(concat, list1.iterator());
        CollectionUtil.addAll(concat, list2.iterator());
        CollectionUtil.addAll(concat, list3.iterator());

        assertEquals(List.of("A", "A", "B", "A", "B", "C"), concat);
    }

    @Test public void testAddAll2() {
        List<String> list1 = List.of("A");
        List<String> list2 = List.of("A", "B");
        List<String> list3 = List.of("A", "B", "C");

        Multiset<String> set = HashMultiset.create();
        CollectionUtil.addAll(set, List.of(list1, list2, list3));

        assertEquals(6, set.size());
        assertEquals(3, set.count("A"));
        assertEquals(2, set.count("B"));
        assertEquals(1, set.count("C"));
    }

    @Test public void testAllUnique() {
        List<String> list = new ArrayList<String>();
        assertTrue(CollectionUtil.allUnique(list));

        list.add("ABC");
        assertTrue(CollectionUtil.allUnique(list));

        list.add("DEF");
        assertTrue(CollectionUtil.allUnique(list));

        list.add("GHI");
        assertTrue(CollectionUtil.allUnique(list));

        list.add("DEF");
        assertFalse(CollectionUtil.allUnique(list));

        list.add("JKL");
        assertFalse(CollectionUtil.allUnique(list));

        list.add("ABC");
        assertFalse(CollectionUtil.allUnique(list));
    }

    @Test public void testAnyDuplicates() {
        List<String> list = new ArrayList<String>();
        assertFalse(CollectionUtil.anyDuplicates(list));

        list.add("ABC");
        assertFalse(CollectionUtil.anyDuplicates(list));

        list.add("DEF");
        assertFalse(CollectionUtil.anyDuplicates(list));

        list.add("GHI");
        assertFalse(CollectionUtil.anyDuplicates(list));

        list.add("DEF");
        assertTrue(CollectionUtil.anyDuplicates(list));

        list.add("JKL");
        assertTrue(CollectionUtil.anyDuplicates(list));

        list.add("ABC");
        assertTrue(CollectionUtil.anyDuplicates(list));
    }

    @Test public void testAverage() {
        assertTrue(Double.isNaN(CollectionUtil.average(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.average(List.of("abc"), s -> s.length()));
        assertDouble(2.5, CollectionUtil.average(List.of("abc", "de"), s -> s.length()));
        assertDouble(2.0, CollectionUtil.average(List.of("abc", "de", "f"), s -> s.length()));
    }

    @Test public void testCompareIterationOrder() {
        Collection<String> set1 = new TreeSet<String>();
        Collection<String> set2 = new TreeSet<String>();

        Collection<String> list1 = new ArrayList<String>();
        Collection<String> list2 = new ArrayList<String>();

        assertTrue(CollectionUtil.compareIterationOrder(set1,  set2)  == 0);
        assertTrue(CollectionUtil.compareIterationOrder(list1, list2) == 0);

        set1.add("def");
        list1.add("def");

        assertTrue(CollectionUtil.compareIterationOrder(set1,  set2)  > 0);
        assertTrue(CollectionUtil.compareIterationOrder(list1, list2) > 0);

        set2.add("ghi");
        list2.add("ghi");

        assertTrue(CollectionUtil.compareIterationOrder(set1, set2)   < 0);
        assertTrue(CollectionUtil.compareIterationOrder(list1, list2) < 0);

        // Note the difference between the list and set, reflecting
        // differences in the iteration order...
        set2.add("abc");
        list2.add("abc");

        assertTrue(CollectionUtil.compareIterationOrder(set1,  set2)  > 0);
        assertTrue(CollectionUtil.compareIterationOrder(list1, list2) < 0);
    }

    @Test public void testCountIterator() {
        assertEquals(0, CollectionUtil.count(List.of().iterator()));
        assertEquals(2, CollectionUtil.count(List.of("abc", "def").iterator()));
    }

    @Test public void testCountCommon() {
        List<String> ABC   = List.of("A", "B", "C");
        List<String> ABCDE = List.of("A", "B", "C", "D", "E");
        List<String> BCD   = List.of("B", "C", "D");
        List<String> DEF   = List.of("D", "E", "F");

        assertEquals(0, CollectionUtil.countCommon(ABC, DEF));
        assertEquals(0, CollectionUtil.countCommon(DEF, ABC));

        assertEquals(2, CollectionUtil.countCommon(ABC, BCD));
        assertEquals(2, CollectionUtil.countCommon(BCD, ABC));

        assertEquals(3, CollectionUtil.countCommon(ABC, ABC));
        assertEquals(3, CollectionUtil.countCommon(ABC, ABCDE));
        assertEquals(3, CollectionUtil.countCommon(ABCDE, ABC));
    }

    @Test public void testCountUnique() {
        assertEquals(0, CollectionUtil.countUnique(emptyList));
        assertEquals(1, CollectionUtil.countUnique(List.of("abc")));
        assertEquals(1, CollectionUtil.countUnique(List.of("abc", "abc", "abc")));
        assertEquals(2, CollectionUtil.countUnique(List.of("abc", "def", "abc")));
        assertEquals(3, CollectionUtil.countUnique(List.of("abc", "def", "ghi")));
    }

    @Test public void testGet() {
        Collection<String> strings = new TreeSet<String>();

        strings.add("A");
        strings.add("B");
        strings.add("C");
        strings.add("D");
        strings.add("E");

        assertEquals("A", CollectionUtil.get(strings, 0));
        assertEquals("B", CollectionUtil.get(strings, 1));
        assertEquals("C", CollectionUtil.get(strings, 2));
        assertEquals("D", CollectionUtil.get(strings, 3));
        assertEquals("E", CollectionUtil.get(strings, 4));
    }

    @Test public void testPeek() {
        assertNull(CollectionUtil.peek(emptyList));
        assertEquals("abc", CollectionUtil.peek(List.of("abc")));
        assertEquals("abc", CollectionUtil.peek(List.of("abc", "def", "ghi")));
    }

    @Test public void testMax() {
        assertTrue(Double.isNaN(CollectionUtil.max(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.max(List.of("abc"), s -> s.length()));
        assertDouble(3.0, CollectionUtil.max(List.of("abc", "de"), s -> s.length()));
        assertDouble(5.0, CollectionUtil.max(List.of("abc", "de", "f", "ghijk"), s -> s.length()));
    }

    @Test public void testMin() {
        assertTrue(Double.isNaN(CollectionUtil.min(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.min(List.of("abc"), s -> s.length()));
        assertDouble(2.0, CollectionUtil.min(List.of("abc", "de"), s -> s.length()));
        assertDouble(1.0, CollectionUtil.min(List.of("abc", "de", "f", "ghijk"), s -> s.length()));
    }

    @Test public void testSample() {
        Set<String>  strings = new HashSet<String>(List.of("A", "B", "C", "D", "E"));
        List<String> sampled = CollectionUtil.sample(strings, 100000, random());

        Multiset<String> counts = HashMultiset.create();
        counts.addAll(sampled);

        for (String s : strings)
            assertEquals(0.20, MultisetUtil.frequency(counts, s), 0.002);
    }

    @Test public void testSampleOne() {
        Multiset<String> strings = HashMultiset.create();
        Multiset<String> sampled = HashMultiset.create();

        strings.addAll(List.of("A", "B", "B", "C", "C", "C", "D", "D", "D", "D"));

        for (int trial = 0; trial < 100000; ++trial)
            sampled.add(CollectionUtil.sampleOne(strings));

        assertEquals(0.1, MultisetUtil.frequency(sampled, "A"), 0.002);
        assertEquals(0.2, MultisetUtil.frequency(sampled, "B"), 0.002);
        assertEquals(0.3, MultisetUtil.frequency(sampled, "C"), 0.002);
        assertEquals(0.4, MultisetUtil.frequency(sampled, "D"), 0.002);
    }

    @Test public void testSplit() {
        Set<String> strings = new TreeSet<String>(List.of("1", "2", "3", "4", "5", "6"));

        List<List<String>> subLists = CollectionUtil.split(strings, 2);

        assertEquals(3, subLists.size());
        assertEquals(List.of("1", "2"), subLists.get(0));
        assertEquals(List.of("3", "4"), subLists.get(1));
        assertEquals(List.of("5", "6"), subLists.get(2));

        subLists = CollectionUtil.split(strings, 3);

        assertEquals(2, subLists.size());
        assertEquals(List.of("1", "2", "3"), subLists.get(0));
        assertEquals(List.of("4", "5", "6"), subLists.get(1));

        subLists = CollectionUtil.split(strings, 4);

        assertEquals(2, subLists.size());
        assertEquals(List.of("1", "2", "3", "4"), subLists.get(0));
        assertEquals(List.of("5", "6"), subLists.get(1));

        subLists = CollectionUtil.split(strings, 5);

        assertEquals(2, subLists.size());
        assertEquals(List.of("1", "2", "3", "4", "5"), subLists.get(0));
        assertEquals(List.of("6"), subLists.get(1));

        subLists = CollectionUtil.split(strings, 6);

        assertEquals(1, subLists.size());
        assertEquals(List.of("1", "2", "3", "4", "5", "6"), subLists.get(0));

        subLists = CollectionUtil.split(strings, 10);

        assertEquals(1, subLists.size());
        assertEquals(List.of("1", "2", "3", "4", "5", "6"), subLists.get(0));
    }

    @Test public void testSum() {
        assertDouble(0.0, CollectionUtil.sum(emptyList, s -> s.length()));
        assertDouble(3.0, CollectionUtil.sum(List.of("abc"), s -> s.length()));
        assertDouble(5.0, CollectionUtil.sum(List.of("abc", "de"), s -> s.length()));
        assertDouble(6.0, CollectionUtil.sum(List.of("abc", "de", "f"), s -> s.length()));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.util.CollectionUtilTest");
    }
}
