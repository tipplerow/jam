
package jam.lang;

import org.junit.*;
import static org.junit.Assert.*;

final class TraversableImpl implements Traversable {
    private final int length;

    public TraversableImpl(int length) {
        this.length = length;
    }

    @Override public int length() {
        return length;
    }
}

public class TraversableTest {
    private static final TraversableImpl T3 = new TraversableImpl(3);

    @Test public void testIsValidIndex() {
        assertFalse(T3.isValidIndex(-1));
        assertTrue(T3.isValidIndex(0));
        assertTrue(T3.isValidIndex(1));
        assertTrue(T3.isValidIndex(2));
        assertFalse(T3.isValidIndex(3));
    }

    @Test public void testTraverse() {
        int index = 0;

        for (Cursor cursor : T3.traverse())
            assertEquals(index++, cursor.index());

        assertEquals(3, index);
    }

    @Test public void testValidateIndexIn() {
        T3.validateIndex(0);
        T3.validateIndex(1);
        T3.validateIndex(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testValidateIndexOut1() {
        T3.validateIndex(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testValidateIndexOut2() {
        T3.validateIndex(10);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.TraversableTest");
    }
}
