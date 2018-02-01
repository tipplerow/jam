
package jam.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        List<String> strings = Arrays.asList("Ohio", "Vermont", "New Jersey");

        List<Integer> expected = Arrays.asList(4, 7, 10);
        List<Integer> actual   = ListUtil.apply(strings, s -> s.length());

        assertEquals(expected, actual);
    }

    @Test public void testCat() {
        assertEquals(0, ListUtil.cat().size());

        List<String> list1 = Arrays.asList("A", "B", "C");
        List<String> list2 = Arrays.asList("D");
        List<String> list3 = Arrays.asList("E", "F");

        assertEquals(Arrays.asList("A", "B", "C"), ListUtil.cat(list1));
        assertEquals(Arrays.asList("A", "B", "C", "D"), ListUtil.cat(list1, list2));
        assertEquals(Arrays.asList("A", "B", "C", "D", "E", "F"), ListUtil.cat(list1, list2, list3));
    }

    @Test public void testFactories() {
        ObjectFactory<ArrayList<String>> arrayFactory = ListUtil.arrayFactory();
        ObjectFactory<LinkedList<String>> linkedFactory = ListUtil.linkedFactory();

        List<String> list1 = arrayFactory.newInstance();
        List<String> list2 = linkedFactory.newInstance();

        assertTrue(list1 instanceof ArrayList);
        assertTrue(list2 instanceof LinkedList);
    }

    @Test public void testFirst() {
        assertEquals("abc", ListUtil.first(Arrays.asList("abc")));
        assertEquals("abc", ListUtil.first(Arrays.asList("abc", "def", "ghi")));
    }

    @Test public void testLast() {
        assertEquals("abc", ListUtil.last(Arrays.asList("abc")));
        assertEquals("ghi", ListUtil.last(Arrays.asList("abc", "def", "ghi")));
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

    @Test public void testTranspose() {
        List<String> row1 = Arrays.asList("A", "B");
        List<String> row2 = Arrays.asList("C", "D", "E", "F");
        List<String> row3 = Arrays.asList("G");

        List<List<String>> original = Arrays.asList(row1, row2, row3);
        List<List<String>> transpose = ListUtil.transpose(original);

        assertEquals(4, transpose.size());
        assertEquals(Arrays.asList("A", "C", "G"), transpose.get(0));
        assertEquals(Arrays.asList("B", "D"),      transpose.get(1));
        assertEquals(Arrays.asList("E"),           transpose.get(2));
        assertEquals(Arrays.asList("F"),           transpose.get(3));
    }

    @Test public void testView() {
        Collection<String> coll = new TreeSet<String>();
        coll.add("abc");
        coll.add("def");
        coll.add("ghi");

        assertEquals(Arrays.asList("abc", "def", "ghi"), ListUtil.view(coll));
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