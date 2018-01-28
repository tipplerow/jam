
package jam.junit;

import java.util.Arrays;
import jam.lang.Sequence;

import org.junit.*;
import static org.junit.Assert.*;

final class StringSequence implements Sequence<String> {
    private String[] strings;

    private StringSequence(String... strings) {
        this.strings = strings;
    }

    public static StringSequence wrap(String... strings) {
        return new StringSequence(strings);
    }

    @Override public int length() {
        return strings.length;
    }

    @Override public String objectAt(int index) {
        return strings[index];
    }
}

public class SequenceTest {
    private static final StringSequence S1 = StringSequence.wrap("A");
    private static final StringSequence S2 = StringSequence.wrap("P", "Q");
    private static final StringSequence S3 = StringSequence.wrap("A", "B", "C");

    @Test public void testEqualsSequence() {
        assertTrue(S3.equalsSequence(StringSequence.wrap("A", "B", "C")));

        assertFalse(S3.equalsSequence(StringSequence.wrap("A", "B")));
        assertFalse(S3.equalsSequence(StringSequence.wrap("B", "A", "C")));
        assertFalse(S3.equalsSequence(StringSequence.wrap("A", "B", "C", "D")));
    }

    @Test public void testIterable() {
        int index = 0;
        String[] expected = new String[] { "A", "B", "C" };

        for (String element : S3)
            assertEquals(expected[index++], element);

        assertEquals(3, index);
    }

    @Test public void testObjectAt() {
        assertEquals("A", S1.objectAt(0));
        assertEquals("A", S3.objectAt(0));
        assertEquals("B", S3.objectAt(1));
        assertEquals("C", S3.objectAt(2));
    }

    @Test(expected = RuntimeException.class)
    public void testObjectAtInvalid1() {
        S1.objectAt(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testObjectAtInvalid2() {
        S3.objectAt(10);
    }

    @Test public void testToArray() {
        assertTrue(Arrays.equals(new String[] { "A" }, S1.toArray()));
        assertTrue(Arrays.equals(new String[] { "A", "B", "C" }, S3.toArray()));
    }

    @Test public void testToList() {
        assertEquals(Arrays.asList("A"), S1.toList());
        assertEquals(Arrays.asList("A", "B", "C"), S3.toList());
    }

    @Test public void testConcat() {
        assertEquals(Arrays.asList("A", "P", "Q", "A", "B", "C"), Sequence.concat(S1, S2, S3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.SequenceTest");
    }
}
