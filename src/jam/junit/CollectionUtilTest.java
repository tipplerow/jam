
package jam.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.util.CollectionUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class CollectionUtilTest extends NumericTestBase {
    private final List<String> emptyList = Arrays.asList();

    @Test public void testAddAll() {
        List<String> list1 = Arrays.asList("A");
        List<String> list2 = Arrays.asList("A", "B");
        List<String> list3 = Arrays.asList("A", "B", "C");

        Multiset<String> set = HashMultiset.create();
        CollectionUtil.addAll(set, Arrays.asList(list1, list2, list3));

        assertEquals(6, set.size());
        assertEquals(3, set.count("A"));
        assertEquals(2, set.count("B"));
        assertEquals(1, set.count("C"));
    }

    @Test public void testAverage() {
        assertTrue(Double.isNaN(CollectionUtil.average(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.average(Arrays.asList("abc"), s -> s.length()));
        assertDouble(2.5, CollectionUtil.average(Arrays.asList("abc", "de"), s -> s.length()));
        assertDouble(2.0, CollectionUtil.average(Arrays.asList("abc", "de", "f"), s -> s.length()));
    }

    @Test public void testCountCommon() {
        List<String> ABC   = Arrays.asList("A", "B", "C");
        List<String> ABCDE = Arrays.asList("A", "B", "C", "D", "E");
        List<String> BCD   = Arrays.asList("B", "C", "D");
        List<String> DEF   = Arrays.asList("D", "E", "F");

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
        assertEquals(1, CollectionUtil.countUnique(Arrays.asList("abc")));
        assertEquals(1, CollectionUtil.countUnique(Arrays.asList("abc", "abc", "abc")));
        assertEquals(2, CollectionUtil.countUnique(Arrays.asList("abc", "def", "abc")));
        assertEquals(3, CollectionUtil.countUnique(Arrays.asList("abc", "def", "ghi")));
    }

    @Test public void testPeek() {
        assertNull(CollectionUtil.peek(emptyList));
        assertEquals("abc", CollectionUtil.peek(Arrays.asList("abc")));
        assertEquals("abc", CollectionUtil.peek(Arrays.asList("abc", "def", "ghi")));
    }

    @Test public void testMax() {
        assertTrue(Double.isNaN(CollectionUtil.max(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.max(Arrays.asList("abc"), s -> s.length()));
        assertDouble(3.0, CollectionUtil.max(Arrays.asList("abc", "de"), s -> s.length()));
        assertDouble(5.0, CollectionUtil.max(Arrays.asList("abc", "de", "f", "ghijk"), s -> s.length()));
    }

    @Test public void testMin() {
        assertTrue(Double.isNaN(CollectionUtil.min(emptyList, s -> s.length())));
        assertDouble(3.0, CollectionUtil.min(Arrays.asList("abc"), s -> s.length()));
        assertDouble(2.0, CollectionUtil.min(Arrays.asList("abc", "de"), s -> s.length()));
        assertDouble(1.0, CollectionUtil.min(Arrays.asList("abc", "de", "f", "ghijk"), s -> s.length()));
    }

    @Test public void testSum() {
        assertDouble(0.0, CollectionUtil.sum(emptyList, s -> s.length()));
        assertDouble(3.0, CollectionUtil.sum(Arrays.asList("abc"), s -> s.length()));
        assertDouble(5.0, CollectionUtil.sum(Arrays.asList("abc", "de"), s -> s.length()));
        assertDouble(6.0, CollectionUtil.sum(Arrays.asList("abc", "de", "f"), s -> s.length()));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.CollectionUtilTest");
    }
}
