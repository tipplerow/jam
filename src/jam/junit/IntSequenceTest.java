
package jam.junit;

import java.util.Arrays;
import java.util.Iterator;

import jam.math.IntSequence;
import jam.spin.SpinVector;

import org.junit.*;
import static org.junit.Assert.*;

public class IntSequenceTest {
    @Test public void testAlongArray() {
        runAlongTest(IntSequence.along(new double[3]));
    }

    @Test public void testAlongTraversable() {
        runAlongTest(IntSequence.along(new SpinVector(3)));
    }

    private void runAlongTest(IntSequence sequence) {
        assertEquals(3, sequence.length());

        assertFalse(sequence.contains(-1));
        assertTrue( sequence.contains( 0));
        assertTrue( sequence.contains( 1));
        assertTrue( sequence.contains( 2));
        assertFalse(sequence.contains( 3));

        assertEquals(0, sequence.get(0));
        assertEquals(1, sequence.get(1));
        assertEquals(2, sequence.get(2));

        assertTrue(Arrays.equals(new int[] { 0, 1, 2 }, sequence.toArray()));

        Iterator iterator = sequence.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(0, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());

        assertFalse(iterator.hasNext());

        assertEquals("0, 1, 2", sequence.format());
    }

    @Test(expected = RuntimeException.class)
    public void testAlongInvalid() {
        IntSequence.along("abc");
    }

    @Test public void testContiguous() {
        IntSequence sequence = IntSequence.contiguous(-2, 2);

        assertEquals(4, sequence.length());

        assertFalse(sequence.contains(-3));
        assertTrue( sequence.contains(-2));
        assertTrue( sequence.contains(-1));
        assertTrue( sequence.contains( 0));
        assertTrue( sequence.contains( 1));
        assertFalse(sequence.contains( 2));

        assertEquals(-2, sequence.get(0));
        assertEquals(-1, sequence.get(1));
        assertEquals( 0, sequence.get(2));
        assertEquals( 1, sequence.get(3));

        assertTrue(Arrays.equals(new int[] { -2, -1, 0, 1 }, sequence.toArray()));

        Iterator iterator = sequence.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(-2, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(-1, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(0, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        assertFalse(iterator.hasNext());

        assertEquals("-2, -1, 0, 1", sequence.format());
    }

    @Test(expected = RuntimeException.class)
    public void testContiguousInvalidConstructor() {
        IntSequence.contiguous(1, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testContiguousInvalidGet1() {
        IntSequence.contiguous(0, 10).get(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testContiguousInvalidGet2() {
        IntSequence.contiguous(0, 10).get(10);
    }

    @Test(expected = RuntimeException.class)
    public void testContiguousIteratorRemove() {
        IntSequence.contiguous(1, 10).iterator().remove();
    }

    @Test public void testEmpty() {
        IntSequence sequence = IntSequence.EMPTY;

        assertFalse(sequence.contains(Integer.MIN_VALUE));
        assertFalse(sequence.contains(0));
        assertFalse(sequence.contains(Integer.MAX_VALUE));

        assertEquals(0, sequence.length());
        assertEquals(0, sequence.toArray().length);

        Iterator iterator = sequence.iterator();
        assertFalse(iterator.hasNext());

        assertEquals("", sequence.format());
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyGet() {
        IntSequence.EMPTY.get(0);
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyIteratorNext() {
        IntSequence.EMPTY.iterator().next();
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyIteratorRemove() {
        IntSequence.EMPTY.iterator().remove();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IntSequenceTest");
    }
}
