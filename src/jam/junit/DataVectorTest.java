
package jam.junit;

import java.util.List;
import java.util.Set;

import jam.data.DataVector;

import org.junit.*;
import static org.junit.Assert.*;

public class DataVectorTest extends NumericTestBase {
    @Test public void testIndexing() {
        DataVector<String> vector = DataVector.dense(List.of("abc", "def", "ghi"));
        assertEquals(3, vector.length());

        assertFalse(vector.contains("foo"));
        assertTrue( vector.contains("abc"));
        assertTrue( vector.contains("def"));
        assertTrue( vector.contains("ghi"));

        assertEquals(-1, vector.indexOf("foo"));
        assertEquals( 0, vector.indexOf("abc"));
        assertEquals( 1, vector.indexOf("def"));
        assertEquals( 2, vector.indexOf("ghi"));

        assertEquals("abc", vector.keyAt(0));
        assertEquals("def", vector.keyAt(1));
        assertEquals("ghi", vector.keyAt(2));

        assertEquals(List.of("abc", "def", "ghi"), vector.keyList());
        assertEquals(Set.of("abc", "def", "ghi"), vector.keySet());

        vector.set("abc", 1.1);
        vector.set(1,     2.2);

        assertDouble(1.1, vector.get(0));
        assertDouble(2.2, vector.get("def"));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DataVectorTest");
    }
}
