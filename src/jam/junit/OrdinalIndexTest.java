
package jam.junit;

import jam.lang.OrdinalIndex;

import org.junit.*;
import static org.junit.Assert.*;

public class OrdinalIndexTest {
    @Test public void testAll() {
        OrdinalIndex index1 = OrdinalIndex.create();
        OrdinalIndex index2 = OrdinalIndex.create();

        assertTrue(index1.equals(index2));

        assertEquals(0, index1.peek());
        assertEquals(0, index1.peek());
        assertEquals(0, index1.next());

        assertEquals(1, index1.peek());
        assertEquals(1, index1.peek());
        assertEquals(1, index1.next());

        assertEquals(2, index1.peek());
        assertEquals(2, index1.next());

        assertFalse(index1.equals(index2));

        assertEquals(0, index2.peek());
        assertEquals(0, index2.peek());
        assertEquals(0, index2.next());

        assertEquals(1, index2.peek());
        assertEquals(1, index2.peek());
        assertEquals(1, index2.next());

        assertEquals(2, index2.peek());
        assertEquals(2, index2.peek());
        assertEquals(2, index2.next());

        assertTrue(index1.equals(index2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.OrdinalIndexTest");
    }
}
