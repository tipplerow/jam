
package jam.math;

import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class IndexPermutationTest {
    @Test public void test51() {
        IndexPermutation permut = new IndexPermutation(5, 1);

        assertTrue(Arrays.equals(new int[] { 0 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 2 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 4 }, permut.next()));

        assertFalse(permut.hasNext());
    }

    @Test public void test52() {
        IndexPermutation permut = new IndexPermutation(5, 2);

        assertTrue(Arrays.equals(new int[] { 0, 1 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 2 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 2 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 2, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 2, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 3, 4 }, permut.next()));

        assertFalse(permut.hasNext());
    }

    @Test public void test53() {
        IndexPermutation permut = new IndexPermutation(5, 3);

        assertTrue(Arrays.equals(new int[] { 0, 1, 2 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 1, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 1, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 2, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 2, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 3, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 2, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 2, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 3, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 2, 3, 4 }, permut.next()));

        assertFalse(permut.hasNext());
    }

    @Test public void test54() {
        IndexPermutation permut = new IndexPermutation(5, 4);

        assertTrue(Arrays.equals(new int[] { 0, 1, 2, 3 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 1, 2, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 1, 3, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 0, 2, 3, 4 }, permut.next()));
        assertTrue(Arrays.equals(new int[] { 1, 2, 3, 4 }, permut.next()));

        assertFalse(permut.hasNext());
    }

    @Test public void test55() {
        IndexPermutation permut = new IndexPermutation(5, 5);
        assertTrue(Arrays.equals(new int[] { 0, 1, 2, 3, 4 }, permut.next()));
        assertFalse(permut.hasNext());
    }

    @Test public void test105() {
        runTotalTest(10, 5);
    }

    @Test public void test126() {
        runTotalTest(12, 6);
    }

    @Test public void test2512() {
        //
        // 5,200,300 unique permutations!
        //
        runTotalTest(25, 12);
    }

    private void runTotalTest(int length, int choose) {
        //
        // Don't want to explicitly enumerate every permutation, but
        // verify that the total number is correct...
        //
        int count = 0;
        IndexPermutation permut = new IndexPermutation(length, choose);

        while (permut.hasNext()) {
            ++count;
            permut.next();
        }

        assertEquals(permut.count(), count);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidLength() {
        IndexPermutation permut = new IndexPermutation(0, 3);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidChoose1() {
        IndexPermutation permut = new IndexPermutation(5, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidChoose2() {
        IndexPermutation permut = new IndexPermutation(5, 6);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.IndexPermutationTest");
    }
}
