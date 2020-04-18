
package jam.junit;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.util.FixedList;

import org.junit.*;
import static org.junit.Assert.*;

public class FixedListTest {
    private static final FixedList<String> FIXED0 = FixedList.create();
    private static final FixedList<String> FIXED1 = FixedList.create("A");
    private static final FixedList<String> FIXED2 = FixedList.create("A", "B");
    private static final FixedList<String> FIXED5 = FixedList.create("A", "B", "C", "D", "E");

    private void checkStandard(FixedList<String> list0,
                               FixedList<String> list1,
                               FixedList<String> list2,
                               FixedList<String> list5) {
        check0(list0);
        check1(list1);
        check2(list2);
        check5(list5);
    }

    private void check0(FixedList<String> list0) {
        checkList(list0, Collections.emptyList());

        assertFalse(list0.contains("A"));
        assertFalse(list0.contains("B"));
        assertFalse(list0.contains("C"));
        assertFalse(list0.contains("D"));
        assertFalse(list0.contains("E"));
        assertFalse(list0.contains("F"));

        Iterator<String> iter = list0.iterator();
        assertFalse(iter.hasNext());
    }

    private void check1(FixedList<String> list1) {
        checkList(list1, Arrays.asList("A"));

        assertEquals("A", list1.get(0));

        assertTrue(list1.contains("A"));
        assertFalse(list1.contains("B"));
        assertFalse(list1.contains("C"));
        assertFalse(list1.contains("D"));
        assertFalse(list1.contains("E"));
        assertFalse(list1.contains("F"));

        Iterator<String> iter = list1.iterator();

        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());

        assertFalse(iter.hasNext());
    }

    private void check2(FixedList<String> list2) {
        checkList(list2, Arrays.asList("A", "B"));

        assertEquals("A", list2.get(0));
        assertEquals("B", list2.get(1));

        assertTrue(list2.contains("A"));
        assertTrue(list2.contains("B"));
        assertFalse(list2.contains("C"));
        assertFalse(list2.contains("D"));
        assertFalse(list2.contains("E"));
        assertFalse(list2.contains("F"));

        Iterator<String> iter = list2.iterator();

        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());

        assertTrue(iter.hasNext());
        assertEquals("B", iter.next());

        assertFalse(iter.hasNext());
    }

    private void check5(FixedList<String> list5) {
        checkList(list5, Arrays.asList("A", "B", "C", "D", "E"));

        assertEquals("A", list5.get(0));
        assertEquals("B", list5.get(1));
        assertEquals("C", list5.get(2));
        assertEquals("D", list5.get(3));
        assertEquals("E", list5.get(4));

        assertTrue(list5.contains("A"));
        assertTrue(list5.contains("B"));
        assertTrue(list5.contains("C"));
        assertTrue(list5.contains("D"));
        assertTrue(list5.contains("E"));
        assertFalse(list5.contains("F"));

        Iterator<String> iter = list5.iterator();

        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());

        assertTrue(iter.hasNext());
        assertEquals("B", iter.next());

        assertTrue(iter.hasNext());
        assertEquals("C", iter.next());

        assertTrue(iter.hasNext());
        assertEquals("D", iter.next());

        assertTrue(iter.hasNext());
        assertEquals("E", iter.next());

        assertFalse(iter.hasNext());
    }

    private void checkList(FixedList<String> actual, List<String> expected) {
        checkClass(actual);
        assertEquals(expected, actual);
    }

    private void checkClass(FixedList<String> fixedList) {
        assertEquals(expectedClassName(fixedList), fixedList.getClass().getSimpleName());
    }

    private static String expectedClassName(FixedList<String> fixedList) {
        switch (fixedList.size()) {
        case 0:
            return "EmptyFixedList";

        case 1:
            return "SingleFixedList";

        case 2:
            return "PairFixedList";

        default:
            return "ArrayFixedList";
        }
    }

    @Test public void testAppendOne() {
        assertEquals(List.of("A"), FixedList.of().append("A"));
        assertEquals(List.of("A", "B"), FixedList.of("A").append("B"));
        assertEquals(List.of("A", "B", "C"), FixedList.of("A", "B").append("C"));
        assertEquals(List.of("A", "B", "C", "D"), FixedList.of("A", "B", "C").append("D"));
        assertEquals(List.of("A", "B", "C", "D", "E"), FixedList.of("A", "B", "C", "D").append("E"));
    }

    @Test public void testAppendTwo() {
        assertEquals(List.of("A", "B"), FixedList.of().append("A", "B"));
        assertEquals(List.of("A", "B", "C"), FixedList.of("A").append("B", "C"));
        assertEquals(List.of("A", "B", "C", "D"), FixedList.of("A", "B").append("C", "D"));
        assertEquals(List.of("A", "B", "C", "D", "E"), FixedList.of("A", "B", "C").append("D", "E"));
        assertEquals(List.of("A", "B", "C", "D", "E", "F"), FixedList.of("A", "B", "C", "D").append("E", "F"));
    }

    @Test public void testCatEmpty() {
        checkStandard(FIXED0.cat(FIXED0),
                      FIXED1.cat(FIXED0),
                      FIXED2.cat(FIXED0),
                      FIXED5.cat(FIXED0));
    }

    @Test public void testCat1() {
        checkList(FIXED0.cat(FIXED1), FIXED1);
        checkList(FIXED1.cat(FIXED1), Arrays.asList("A", "A"));
        checkList(FIXED2.cat(FIXED1), Arrays.asList("A", "B", "A"));
        checkList(FIXED5.cat(FIXED1), Arrays.asList("A", "B", "C", "D", "E", "A"));
    }

    @Test public void testCat2() {
        checkList(FIXED0.cat(FIXED2), FIXED2);
        checkList(FIXED1.cat(FIXED2), Arrays.asList("A", "A", "B"));
        checkList(FIXED2.cat(FIXED2), Arrays.asList("A", "B", "A", "B"));
        checkList(FIXED5.cat(FIXED2), Arrays.asList("A", "B", "C", "D", "E", "A", "B"));
    }

    @Test public void testCat5() {
        checkList(FIXED0.cat(FIXED5), FIXED5);
        checkList(FIXED1.cat(FIXED5), Arrays.asList("A", "A", "B", "C", "D", "E"));
        checkList(FIXED2.cat(FIXED5), Arrays.asList("A", "B", "A", "B", "C", "D", "E"));
        checkList(FIXED5.cat(FIXED5), Arrays.asList("A", "B", "C", "D", "E", "A", "B", "C", "D", "E"));
    }

    @Test public void testCreateFromArray() {
        String[] array0 = new String[] {};
        String[] array1 = new String[] { "A" };
        String[] array2 = new String[] { "A", "B" };
        String[] array5 = new String[] { "A", "B", "C", "D", "E" };

        FixedList<String> list0 = FixedList.create(array0);
        FixedList<String> list1 = FixedList.create(array1);
        FixedList<String> list2 = FixedList.create(array2);
        FixedList<String> list5 = FixedList.create(array5);

        // Ensure that the original array contents were copied...
        Arrays.fill(array0, null);
        Arrays.fill(array1, null);
        Arrays.fill(array2, null);
        Arrays.fill(array5, null);

        checkStandard(list0, list1, list2, list5);
    }

    @Test public void testCreateFromCollection() {
        Set<String> set0 = new TreeSet<String>();
        Set<String> set1 = new TreeSet<String>();
        Set<String> set2 = new TreeSet<String>();
        Set<String> set5 = new TreeSet<String>();

        set1.add("A");

        set2.add("A");
        set2.add("B");

        set5.add("A");
        set5.add("B");
        set5.add("C");
        set5.add("D");
        set5.add("E");

        FixedList<String> fixedList0 = FixedList.create(set0);
        FixedList<String> fixedList1 = FixedList.create(set1);
        FixedList<String> fixedList2 = FixedList.create(set2);
        FixedList<String> fixedList5 = FixedList.create(set5);

        // Ensure that the original list contents were copied...
        set0.clear();
        set1.clear();
        set2.clear();
        set5.clear();

        checkStandard(fixedList0, fixedList1, fixedList2, fixedList5);
    }

    @Test public void testCreateFromList() {
        List<String> arrayList0 = new ArrayList<String>();
        List<String> arrayList1 = new ArrayList<String>();
        List<String> arrayList2 = new ArrayList<String>();
        List<String> arrayList5 = new ArrayList<String>();

        arrayList1.add("A");

        arrayList2.add("A");
        arrayList2.add("B");

        arrayList5.add("A");
        arrayList5.add("B");
        arrayList5.add("C");
        arrayList5.add("D");
        arrayList5.add("E");

        FixedList<String> fixedList0 = FixedList.create(arrayList0);
        FixedList<String> fixedList1 = FixedList.create(arrayList1);
        FixedList<String> fixedList2 = FixedList.create(arrayList2);
        FixedList<String> fixedList5 = FixedList.create(arrayList5);

        // Ensure that the original list contents were copied...
        arrayList0.clear();
        arrayList1.clear();
        arrayList2.clear();
        arrayList5.clear();

        checkStandard(fixedList0, fixedList1, fixedList2, fixedList5);
    }

    @Test public void testCreateFromVarArgs() {
        checkStandard(FIXED0, FIXED1, FIXED2, FIXED5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        FIXED2.add("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll() {
        FIXED2.addAll(Arrays.asList("foo", "bar"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove1() {
        FIXED2.remove(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove2() {
        FIXED2.remove("A");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove3() {
        Iterator<String> iter = FIXED2.iterator();

        iter.next();
        iter.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {
        FIXED2.removeAll(Arrays.asList("foo", "bar"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSet() {
        FIXED2.set(0, "foo");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FixedListTest");
    }
}
