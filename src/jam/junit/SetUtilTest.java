
package jam.junit;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import jam.util.SetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class SetUtilTest {
    public enum MyEnum { A, B, C, D, E, F, G }

    @Test public void testCloneEnumSet() {
        EnumSet<MyEnum> set1 = EnumSet.noneOf(MyEnum.class);
        EnumSet<MyEnum> set2 = EnumSet.noneOf(MyEnum.class);

        set1.add(MyEnum.A);
        set1.add(MyEnum.B);

        set2.add(MyEnum.A);
        set2.add(MyEnum.B);

        assertTrue(SetUtil.clone(set1) instanceof EnumSet);
        assertEquals(set2, SetUtil.clone(set1));
    }

    @Test public void testCloneHashSet() {
        HashSet<String> set1 = SetUtil.newHashSet("A", "B", "C");
        HashSet<String> set2 = SetUtil.newHashSet("A", "B", "C");

        assertTrue(SetUtil.clone(set1) instanceof HashSet);
        assertEquals(set2, SetUtil.clone(set1));
    }

    @Test public void testCloneTreeSet() {
        TreeSet<String> set1 = SetUtil.newTreeSet("ZZZ", "abc", "XYZ");
        TreeSet<String> set2 = SetUtil.newTreeSet("XYZ", "ZZZ", "abc");

        assertTrue(SetUtil.clone(set1) instanceof TreeSet);
        assertEquals(set2, SetUtil.clone(set1));

        TreeSet<String> cloned = (TreeSet<String>) SetUtil.clone(set1);
        assertEquals("XYZ", cloned.first());
        assertEquals("abc", cloned.last());

        TreeSet<String> set3 = SetUtil.newTreeSet(String.CASE_INSENSITIVE_ORDER, "abc", "XYZ", "ZZZ");
        TreeSet<String> set4 = SetUtil.newTreeSet(String.CASE_INSENSITIVE_ORDER, "abc", "XYZ", "ZZZ");

        assertTrue(SetUtil.clone(set3) instanceof TreeSet);
        assertEquals(set4, SetUtil.clone(set3));

        cloned = (TreeSet<String>) SetUtil.clone(set3);
        assertEquals("abc", cloned.first());
        assertEquals("ZZZ", cloned.last());
    }

    @Test public void testCountShared() {
        Set<String> s0 = Set.of();
        Set<String> s1 = Set.of("A", "B", "C", "D", "E");
        Set<String> s2 = Set.of("D", "E", "F", "G", "H");
        Set<String> s3 = Set.of("H", "I");

        assertEquals(0, SetUtil.countShared(s0, s1));
        assertEquals(0, SetUtil.countShared(s1, s0));

        assertEquals(2, SetUtil.countShared(s1, s2));
        assertEquals(2, SetUtil.countShared(s2, s1));

        assertEquals(1, SetUtil.countShared(s2, s3));
        assertEquals(1, SetUtil.countShared(s3, s2));

        assertEquals(0, SetUtil.countShared(s1, s3));
        assertEquals(0, SetUtil.countShared(s3, s1));
    }

    @Test public void testCountUnique() {
        Set<String> s0 = Set.of();
        Set<String> s1 = Set.of("A", "B", "C", "D", "E");
        Set<String> s2 = Set.of("D", "E", "F", "G", "H");
        Set<String> s3 = Set.of("H", "I");

        assertEquals(5, SetUtil.countUnique(s0, s1));
        assertEquals(5, SetUtil.countUnique(s1, s0));

        assertEquals(8, SetUtil.countUnique(s1, s2));
        assertEquals(8, SetUtil.countUnique(s2, s1));

        assertEquals(6, SetUtil.countUnique(s2, s3));
        assertEquals(6, SetUtil.countUnique(s3, s2));

        assertEquals(7, SetUtil.countUnique(s1, s3));
        assertEquals(7, SetUtil.countUnique(s3, s1));
    }

    @Test public void testDifference() {
        EnumSet<MyEnum> set1 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B, MyEnum.C);
        HashSet<MyEnum> set2 = SetUtil.newHashSet(MyEnum.C, MyEnum.D, MyEnum.E);
        TreeSet<MyEnum> set3 = SetUtil.newTreeSet(MyEnum.E, MyEnum.F, MyEnum.G);

        EnumSet<MyEnum> diff12 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B);
        EnumSet<MyEnum> diff13 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B, MyEnum.C);

        HashSet<MyEnum> diff21 = SetUtil.newHashSet(MyEnum.D, MyEnum.E);
        HashSet<MyEnum> diff23 = SetUtil.newHashSet(MyEnum.C, MyEnum.D);

        TreeSet<MyEnum> diff31 = SetUtil.newTreeSet(MyEnum.E, MyEnum.F, MyEnum.G);
        TreeSet<MyEnum> diff32 = SetUtil.newTreeSet(MyEnum.F, MyEnum.G);

        assertTrue(SetUtil.difference(set1, set2) instanceof EnumSet);
        assertTrue(SetUtil.difference(set1, set3) instanceof EnumSet);

        assertTrue(SetUtil.difference(set2, set1) instanceof HashSet);
        assertTrue(SetUtil.difference(set2, set3) instanceof HashSet);

        assertTrue(SetUtil.difference(set3, set1) instanceof TreeSet);
        assertTrue(SetUtil.difference(set3, set2) instanceof TreeSet);

        assertEquals(diff12, SetUtil.difference(set1, set2));
        assertEquals(diff13, SetUtil.difference(set1, set3));

        assertEquals(diff21, SetUtil.difference(set2, set1));
        assertEquals(diff23, SetUtil.difference(set2, set3));

        assertEquals(diff31, SetUtil.difference(set3, set1));
        assertEquals(diff32, SetUtil.difference(set3, set2));

        assertEquals(3, set1.size()); // Ensure that the input sets are unchanged...
        assertEquals(3, set2.size());
        assertEquals(3, set3.size());
    }

    @Test public void testIntersection() {
        EnumSet<MyEnum> set1 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B, MyEnum.C, MyEnum.D);
        HashSet<MyEnum> set2 = SetUtil.newHashSet(MyEnum.B, MyEnum.C, MyEnum.D, MyEnum.E);
        TreeSet<MyEnum> set3 = SetUtil.newTreeSet(MyEnum.C, MyEnum.D, MyEnum.E, MyEnum.F);

        EnumSet<MyEnum> inter12 = SetUtil.newEnumSet(MyEnum.class, MyEnum.B, MyEnum.C, MyEnum.D);
        EnumSet<MyEnum> inter13 = SetUtil.newEnumSet(MyEnum.class, MyEnum.C, MyEnum.D);

        HashSet<MyEnum> inter21 = SetUtil.newHashSet(MyEnum.B, MyEnum.C, MyEnum.D);
        HashSet<MyEnum> inter23 = SetUtil.newHashSet(MyEnum.C, MyEnum.D, MyEnum.E);

        TreeSet<MyEnum> inter31 = SetUtil.newTreeSet(MyEnum.C, MyEnum.D);
        TreeSet<MyEnum> inter32 = SetUtil.newTreeSet(MyEnum.C, MyEnum.D, MyEnum.E);

        assertTrue(SetUtil.intersection(set1, set2) instanceof EnumSet);
        assertTrue(SetUtil.intersection(set1, set3) instanceof EnumSet);

        assertTrue(SetUtil.intersection(set2, set1) instanceof HashSet);
        assertTrue(SetUtil.intersection(set2, set3) instanceof HashSet);

        assertTrue(SetUtil.intersection(set3, set1) instanceof TreeSet);
        assertTrue(SetUtil.intersection(set3, set2) instanceof TreeSet);

        assertEquals(inter12, SetUtil.intersection(set1, set2));
        assertEquals(inter13, SetUtil.intersection(set1, set3));

        assertEquals(inter21, SetUtil.intersection(set2, set1));
        assertEquals(inter23, SetUtil.intersection(set2, set3));

        assertEquals(inter31, SetUtil.intersection(set3, set1));
        assertEquals(inter32, SetUtil.intersection(set3, set2));

        assertEquals(4, set1.size()); // Ensure that the input sets are unchanged...
        assertEquals(4, set2.size());
        assertEquals(4, set3.size());
    }

    @Test public void testNewHashSetFunction() {
        Set<String> strings = Set.of("A", "BC", "DEF");
        Set<Integer> lengths = SetUtil.newHashSet(strings, x -> Integer.valueOf(x.length()));

        assertEquals(Set.of(1, 2, 3), lengths);
    }

    @Test public void testUnion() {
        EnumSet<MyEnum> set1 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B);
        HashSet<MyEnum> set2 = SetUtil.newHashSet(MyEnum.B, MyEnum.C);
        TreeSet<MyEnum> set3 = SetUtil.newTreeSet(MyEnum.C, MyEnum.D);

        EnumSet<MyEnum> union12 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B, MyEnum.C);
        EnumSet<MyEnum> union13 = SetUtil.newEnumSet(MyEnum.class, MyEnum.A, MyEnum.B, MyEnum.C, MyEnum.D);

        HashSet<MyEnum> union21 = SetUtil.newHashSet(MyEnum.A, MyEnum.B, MyEnum.C);
        HashSet<MyEnum> union23 = SetUtil.newHashSet(MyEnum.B, MyEnum.C, MyEnum.D);

        TreeSet<MyEnum> union31 = SetUtil.newTreeSet(MyEnum.A, MyEnum.B, MyEnum.C, MyEnum.D);
        TreeSet<MyEnum> union32 = SetUtil.newTreeSet(MyEnum.B, MyEnum.C, MyEnum.D);

        assertTrue(SetUtil.union(set1, set2) instanceof EnumSet);
        assertTrue(SetUtil.union(set1, set3) instanceof EnumSet);

        assertTrue(SetUtil.union(set2, set1) instanceof HashSet);
        assertTrue(SetUtil.union(set2, set3) instanceof HashSet);

        assertTrue(SetUtil.union(set3, set1) instanceof TreeSet);
        assertTrue(SetUtil.union(set3, set2) instanceof TreeSet);

        assertEquals(union12, SetUtil.union(set1, set2));
        assertEquals(union13, SetUtil.union(set1, set3));

        assertEquals(union21, SetUtil.union(set2, set1));
        assertEquals(union23, SetUtil.union(set2, set3));

        assertEquals(union31, SetUtil.union(set3, set1));
        assertEquals(union32, SetUtil.union(set3, set2));

        assertEquals(2, set1.size()); // Ensure that the input sets are unchanged...
        assertEquals(2, set2.size());
        assertEquals(2, set3.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SetUtilTest");
    }
}
