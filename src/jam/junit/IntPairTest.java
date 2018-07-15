
package jam.junit;

import jam.math.IntPair;

import org.junit.*;
import static org.junit.Assert.*;

public class IntPairTest {
    @Test public void testEqualsHash() {
        IntPair p1 = IntPair.of(1, 2);
        IntPair p2 = IntPair.of(1, 2);
        IntPair p3 = IntPair.of(2, 1);

        assertEquals(1, p1.first);
        assertEquals(2, p1.second);

        assertEquals(2, p3.first);
        assertEquals(1, p3.second);

        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));

        assertTrue(p1.hashCode() == p2.hashCode());
        assertTrue(p1.hashCode() != p3.hashCode());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IntPairTest");
    }
}
