
package jam.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.math.IntPair;

import org.junit.*;
import static org.junit.Assert.*;

public class JamStreamsTest {
    @Test public void testApply() {
        int sampleSize = 10000;

        List<Integer> source = createSource(sampleSize);
        List<IntPair> result = JamStreams.apply(source, x -> IntPair.of(x, x * x));

        assertSquare(result.get(0));
        assertSquare(result.get(sampleSize - 1));
    }

    @Test public void testApplyParallel() {
        int sampleSize = 100000;

        List<Integer> source = createSource(sampleSize);
        List<IntPair> result = JamStreams.applyParallel(source, x -> IntPair.of(x, x * x));

        assertSquare(result.get(0));
        assertSquare(result.get(sampleSize - 1));
    }

    private List<Integer> createSource(int size) {
        List<Integer> source = new ArrayList<Integer>(size);

        for (int index = 0; index < size; ++index)
            source.add(index);

        return source;
    }

    private void assertSquare(IntPair pair) {
        assertEquals(pair.first * pair.first, pair.second);
    }

    @Test public void testCount() {
        int N = 10;
        List<Integer> source = createSource(N);

        assertEquals(N / 2, JamStreams.count(source, x -> x %2 == 0));
    }

    @Test public void testFilter() {
        int N = 10;
        List<Integer> source = createSource(N);
        List<Integer> result = JamStreams.filter(source, x -> x %2 == 0);

        assertEquals(List.of(0, 2, 4, 6, 8), result);
    }

    @Test public void testToArrayList() {
        List<String> expected = List.of("abc", "def", "ghi");
        List<String> actual   = JamStreams.toArrayList(expected.stream());

        assertEquals(expected, actual);
        assertTrue(actual instanceof ArrayList);
    }

    @Test public void testToLinkedList() {
        List<String> expected = List.of("abc", "def", "ghi");
        List<String> actual   = JamStreams.toLinkedList(expected.stream());

        assertEquals(expected, actual);
        assertTrue(actual instanceof LinkedList);
    }

    @Test public void testToHashSet() {
        Set<String> expected = Set.of("abc", "def", "ghi");
        Set<String> actual   = JamStreams.toHashSet(expected.stream());

        assertEquals(expected, actual);
        assertTrue(actual instanceof HashSet);
    }

    @Test public void testToTreeSet() {
        Set<String> expected = Set.of("abc", "def", "ghi");
        Set<String> actual   = JamStreams.toTreeSet(expected.stream());

        assertEquals(expected, actual);
        assertTrue(actual instanceof TreeSet);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stream.JamStreamsTest");
    }
}
