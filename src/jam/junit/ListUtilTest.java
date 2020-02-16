
package jam.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import jam.lang.ObjectFactory;
import jam.math.JamRandom;
import jam.math.StatUtil;
import jam.util.ListUtil;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class ListUtilTest {
    private final JamRandom random = JamRandom.global(20150413);

    @Test public void testApply() {
        List<String> strings = List.of("Ohio", "Vermont", "New Jersey");

        List<Integer> expected = List.of(4, 7, 10);
        List<Integer> actual   = ListUtil.apply(strings, s -> s.length());

        assertEquals(expected, actual);
    }

    @Test public void testCat() {
        assertEquals(0, ListUtil.cat().size());

        List<String> list1 = List.of("A", "B", "C");
        List<String> list2 = List.of("D");
        List<String> list3 = List.of("E", "F");

        assertEquals(List.of("A", "B", "C"), ListUtil.cat(list1));
        assertEquals(List.of("A", "B", "C", "D"), ListUtil.cat(list1, list2));
        assertEquals(List.of("A", "B", "C", "D", "E", "F"), ListUtil.cat(list1, list2, list3));
    }

    @Test public void testFactories() {
        ObjectFactory<ArrayList<String>> arrayFactory = ListUtil.arrayFactory();
        ObjectFactory<LinkedList<String>> linkedFactory = ListUtil.linkedFactory();

        List<String> list1 = arrayFactory.newInstance();
        List<String> list2 = linkedFactory.newInstance();

        assertTrue(list1 instanceof ArrayList);
        assertTrue(list2 instanceof LinkedList);
    }

    @Test public void testFilter() {
        List<String> original = List.of("a", "ab", "abc", "abcd", "abcde");
        List<String> filtered = ListUtil.filter(original, s -> s.startsWith("abc"));

        assertEquals(List.of("abc", "abcd", "abcde"), filtered);
    }

    @Test public void testFirst() {
        assertEquals("abc", ListUtil.first(List.of("abc")));
        assertEquals("abc", ListUtil.first(List.of("abc", "def", "ghi")));
    }

    @Test public void testGet() {
        assertEquals(List.of("C", "A", "D"), ListUtil.get(List.of("A", "B", "C", "D", "E", "F"), new int[] { 2, 0, 3 }));
    }

    @Test public void testIsSorted() {
        assertTrue(ListUtil.isSorted(List.of("A")));

        assertTrue(ListUtil.isSorted(List.of("A", "A", "A")));
        assertTrue(ListUtil.isSorted(List.of("A", "B", "B")));
        assertTrue(ListUtil.isSorted(List.of("A", "B", "C")));

        assertFalse(ListUtil.isSorted(List.of("C", "B", "C")));
        assertFalse(ListUtil.isSorted(List.of("C", "C", "A")));
    }

    @Test public void testLast() {
        assertEquals("abc", ListUtil.last(List.of("abc")));
        assertEquals("ghi", ListUtil.last(List.of("abc", "def", "ghi")));
    }

    @Test public void testNewArrayListIterator() {
        Iterator<String> iterator = List.of("abc", "def", "ghi").iterator();
        ArrayList<String> list = ListUtil.newArrayList(iterator);

        assertEquals(list, List.of("abc", "def", "ghi"));
    }

    @Test public void testSelect() {
        int N = 10;
        List<Integer> list = new ArrayList<Integer>(N);

        for (int k = 0; k < N; k++)
            list.add(k);

        int niter     = 1000000;
        int expected  = niter / N;
        int tolerance = 1000;

        int[] count = new int[N];
        
        for (int k = 0; k < niter; k++)
            ++count[ListUtil.select(list, random)];

        for (int k = 0; k < N; k++)
            assertTrue(expected - tolerance < count[k] && count[k] < expected + tolerance);

        list = List.of(123);
        assertEquals(Integer.valueOf(123), ListUtil.select(list, random));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectEmpty() {
        List<Integer> list = new ArrayList<Integer>();
        ListUtil.select(list, random);
    }

    @Test public void testShuffle() {
        int N = 100000;
        List<Integer> list = new ArrayList<Integer>(N);

        for (int k = 0; k < N; k++)
            list.add(k);

        ListUtil.shuffle(list, random);

        JamVector ordered  = new JamVector(N);
        JamVector shuffled = new JamVector(N);

        for (int k = 0; k < N; k++) {
            ordered.set(k, k);
            shuffled.set(k, list.get(k));
        }

        // This is not totally rigorous, but there should be
        // essentially no correlation between the original and
        // shuffled values...
        assertEquals(0.0, StatUtil.cor(ordered, shuffled), 0.01);
    }

    @Test public void testSplit() {
        List<String> list = List.of("A", "B", "C", "D", "E");
        List<List<String>> subLists = ListUtil.split(list, 1);

        assertEquals(5, subLists.size());
        assertEquals(List.of("A"), subLists.get(0));
        assertEquals(List.of("B"), subLists.get(1));
        assertEquals(List.of("C"), subLists.get(2));
        assertEquals(List.of("D"), subLists.get(3));
        assertEquals(List.of("E"), subLists.get(4));

        subLists = ListUtil.split(list, 2);

        assertEquals(3, subLists.size());
        assertEquals(List.of("A", "B"), subLists.get(0));
        assertEquals(List.of("C", "D"), subLists.get(1));
        assertEquals(List.of("E"), subLists.get(2));

        subLists = ListUtil.split(list, 3);

        assertEquals(2, subLists.size());
        assertEquals(List.of("A", "B", "C"), subLists.get(0));
        assertEquals(List.of("D", "E"), subLists.get(1));

        subLists = ListUtil.split(list, 5);

        assertEquals(1, subLists.size());
        assertEquals(List.of("A", "B", "C", "D", "E"), subLists.get(0));

        subLists = ListUtil.split(list, 10);

        assertEquals(1, subLists.size());
        assertEquals(List.of("A", "B", "C", "D", "E"), subLists.get(0));
    }

    @Test public void testTranspose() {
        List<String> row1 = List.of("A", "B");
        List<String> row2 = List.of("C", "D", "E", "F");
        List<String> row3 = List.of("G");

        List<List<String>> original = List.of(row1, row2, row3);
        List<List<String>> transpose = ListUtil.transpose(original);

        assertEquals(4, transpose.size());
        assertEquals(List.of("A", "C", "G"), transpose.get(0));
        assertEquals(List.of("B", "D"),      transpose.get(1));
        assertEquals(List.of("E"),           transpose.get(2));
        assertEquals(List.of("F"),           transpose.get(3));
    }

    @Test public void testView() {
        Collection<String> coll = new TreeSet<String>();
        coll.add("abc");
        coll.add("def");
        coll.add("ghi");

        assertEquals(List.of("abc", "def", "ghi"), ListUtil.view(coll));
    }

    @Test(expected = RuntimeException.class)
    public void testViewImmutability() {
        Collection<String> coll = new TreeSet<String>();
        coll.add("abc");
        coll.add("def");
        coll.add("ghi");

        ListUtil.view(coll).add("foo");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ListUtilTest");
    }
}
