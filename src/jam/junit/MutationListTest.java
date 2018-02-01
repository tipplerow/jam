
package jam.junit;

import java.util.Arrays;
import java.util.Set;

import jam.tumor.mutation.Mutation;
import jam.tumor.mutation.MutationList;

import org.junit.*;
import static org.junit.Assert.*;

public class MutationListTest {
    private static final Mutation neutral1 = Mutation.neutral(0);
    private static final Mutation neutral2 = Mutation.neutral(0);
    private static final Mutation neutral3 = Mutation.neutral(0);
    private static final Mutation neutral4 = Mutation.neutral(0);
    private static final Mutation neutral5 = Mutation.neutral(0);

    private static final MutationList founderList = 
        MutationList.create(neutral1, neutral2, neutral3);

    @Test(expected = UnsupportedOperationException.class) 
    public void testAdd() {
        MutationList list = MutationList.create(neutral1);
        list.add(neutral2);
    }

    @Test public void testContains() {
        assertFalse(MutationList.EMPTY.contains(neutral1));

        assertTrue(founderList.contains(neutral1));
        assertTrue(founderList.contains(neutral2));
        assertTrue(founderList.contains(neutral3));
        assertFalse(founderList.contains(neutral4));
    }

    @Test public void testDaughter1() {
        MutationList daughterList = founderList.append(MutationList.create(neutral4));

        assertEquals(4, daughterList.size());

        assertTrue(daughterList.contains(neutral1));
        assertTrue(daughterList.contains(neutral2));
        assertTrue(daughterList.contains(neutral3));
        assertTrue(daughterList.contains(neutral4));

        assertEquals(neutral1, daughterList.get(0));
        assertEquals(neutral2, daughterList.get(1));
        assertEquals(neutral3, daughterList.get(2));
        assertEquals(neutral4, daughterList.get(3));
    }

    @Test public void testDaughter2() {
        MutationList daughterList = founderList.append(MutationList.create(neutral4, neutral5));

        assertEquals(5, daughterList.size());

        assertTrue(daughterList.contains(neutral1));
        assertTrue(daughterList.contains(neutral2));
        assertTrue(daughterList.contains(neutral3));
        assertTrue(daughterList.contains(neutral4));
        assertTrue(daughterList.contains(neutral5));

        assertEquals(neutral1, daughterList.get(0));
        assertEquals(neutral2, daughterList.get(1));
        assertEquals(neutral3, daughterList.get(2));
        assertEquals(neutral4, daughterList.get(3));
        assertEquals(neutral5, daughterList.get(4));
    }

    @Test public void testDifference() {
        MutationList list1 = MutationList.create(neutral1, neutral2, neutral3, neutral4);
        MutationList list2 = MutationList.create(neutral3, neutral4, neutral5);
        MutationList list3 = MutationList.create(neutral1, neutral2);
        MutationList list4 = MutationList.create(neutral5);

        assertEquals(list3, list1.difference(list2));
        assertEquals(list4, list2.difference(list1));
    }

    @Test public void testGet() {
        assertEquals(neutral1, founderList.get(0));
        assertEquals(neutral2, founderList.get(1));
        assertEquals(neutral3, founderList.get(2));
    }

    @Test public void testIntersection() {
        MutationList list1 = MutationList.create(neutral1, neutral2, neutral3, neutral4);
        MutationList list2 = MutationList.create(neutral3, neutral4, neutral5);
        MutationList list3 = MutationList.create(neutral3, neutral4);

        assertEquals(list3, list1.intersection(list2));
        assertEquals(list3, list2.intersection(list1));
    }

    @Test(expected = UnsupportedOperationException.class) 
    public void testRemove1() {
        founderList.remove(neutral1);
    }

    @Test(expected = UnsupportedOperationException.class) 
    public void testRemove2() {
        founderList.remove("ABC");
    }

    @Test public void testSetView() {
        Set<Mutation> set = founderList.setView();

        assertEquals(3, set.size());
        assertTrue(set.contains(neutral1));
        assertTrue(set.contains(neutral2));
        assertTrue(set.contains(neutral3));

        assertTrue(MutationList.EMPTY.setView().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class) 
    public void testSetViewAdd() {
        founderList.setView().add(Mutation.neutral(0));
    }

    @Test(expected = UnsupportedOperationException.class) 
    public void testSetViewRemove() {
        founderList.setView().remove("ABC");
    }

    @Test(expected = UnsupportedOperationException.class) 
    public void testSetViewRemoveAll() {
        founderList.setView().removeAll(Arrays.asList("ABC", "DEF"));
    }

    @Test public void testSize() {
        assertEquals(0, MutationList.EMPTY.size());
        assertEquals(3, founderList.size());
    }

    @Test public void testUnion() {
        MutationList list1 = MutationList.create(neutral1, neutral2, neutral3, neutral4);
        MutationList list2 = MutationList.create(neutral3, neutral4, neutral5);
        MutationList list3 = MutationList.create(neutral1, neutral2, neutral3, neutral4, neutral5);

        assertEquals(list3.setView(), list1.union(list2).setView());
        assertEquals(list3.setView(), list2.union(list1).setView());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MutationListTest");
    }
}