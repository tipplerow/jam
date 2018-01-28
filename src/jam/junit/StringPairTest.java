
package jam.junit;

import jam.lang.StringPair;

import org.junit.*;
import static org.junit.Assert.*;

public class StringPairTest {
    @Test public void testCompareTo() {
        StringPair pair1 = StringPair.valueOf("abc", "abc");
        StringPair pair2 = StringPair.valueOf("abc", "def");
        StringPair pair3 = StringPair.valueOf("def", "abc");
        StringPair pair4 = StringPair.valueOf("def", "def");

        assertTrue(pair1.compareTo(pair2) < 0);
        assertTrue(pair1.compareTo(pair3) < 0);
        assertTrue(pair1.compareTo(pair4) < 0);

        assertTrue(pair2.compareTo(pair1) > 0);
        assertTrue(pair2.compareTo(pair3) < 0);
        assertTrue(pair2.compareTo(pair4) < 0);

        assertTrue(pair3.compareTo(pair1) > 0);
        assertTrue(pair3.compareTo(pair2) > 0);
        assertTrue(pair3.compareTo(pair4) < 0);

        assertTrue(pair4.compareTo(pair1) > 0);
        assertTrue(pair4.compareTo(pair2) > 0);
        assertTrue(pair4.compareTo(pair3) > 0);

        assertEquals(0, pair1.compareTo(pair1));
        assertEquals(0, pair2.compareTo(pair2));
        assertEquals(0, pair3.compareTo(pair3));
        assertEquals(0, pair4.compareTo(pair4));
    }

    @Test public void testEquals() {
        StringPair pair1 = StringPair.valueOf("abc", "def");
        StringPair pair2 = StringPair.valueOf("abc", "def");
        StringPair pair3 = StringPair.valueOf("def", "abc");

        assertTrue(pair1.equals(pair2));
        assertFalse(pair1.equals(pair3));
    }

    @Test public void testHashCode() {
        StringPair pair1 = StringPair.valueOf("abc", "def");
        StringPair pair2 = StringPair.valueOf("abc", "def");
        StringPair pair3 = StringPair.valueOf("def", "abc");

        assertTrue(pair1.hashCode() == pair2.hashCode());
        assertTrue(pair1.hashCode() != pair3.hashCode());
    }

    @Test public void testFirst() {
        assertEquals("abc", StringPair.valueOf("abc", "def").first());
    }

    @Test public void testSecond() {
        assertEquals("def", StringPair.valueOf("abc", "def").second());
    }

    @Test public void testParse() {
        assertEquals(StringPair.valueOf("abc", "def"), StringPair.parse("  abc  ,    def   "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalid1() {
        StringPair.parse("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalid2() {
        StringPair.parse("abc, def, ghi");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StringPairTest");
    }
}
