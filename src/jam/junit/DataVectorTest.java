
package jam.junit;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.data.DataVector;

import org.junit.*;
import static org.junit.Assert.*;

public class DataVectorTest extends NumericTestBase {
    private final List<String> keyList1 = Arrays.asList("abc", "def", "ghi");
    private final List<String> keyList2 = Arrays.asList("ghi", "def", "abc");
    private final List<String> keyList3 = Arrays.asList("abc", "def", "foo");

    private final Set<String> keySet1 = new TreeSet<String>(keyList1);
    private final Set<String> keySet2 = new LinkedHashSet<String>(keyList2);
    private final Set<String> keySet3 = new TreeSet<String>(keyList3);

    private final DataVector vector1 = new DataVector(keySet1, 3.3);
    private final DataVector vector2 = new DataVector(keySet2, 3.3);
    private final DataVector vector3 = new DataVector(keySet3, 3.3);

    @Test public void testBasic() {
        assertEquals(3, vector1.length());

        assertTrue(vector1.contains("abc"));
        assertTrue(vector1.contains("def"));
        assertTrue(vector1.contains("ghi"));

        vector1.set("abc", 1.1);
        vector1.set("def", 2.2);

        assertDouble(1.1, vector1.get("abc"));
        assertDouble(2.2, vector1.get("def"));
        assertDouble(3.3, vector1.get("ghi"));

        Set<String> keys = vector1.keys();
        assertEquals(3, keys.size());

        assertTrue(keys.contains("abc"));
        assertTrue(keys.contains("def"));
        assertTrue(keys.contains("ghi"));
    }

    @Test public void testEquals() {
	assertTrue(vector1.equals(vector2));
	assertFalse(vector1.equals(vector3));

	vector1.set("abc", 1.1);
	vector1.set("def", 2.2);

	vector2.set("abc", 1.1);
	vector2.set("def", 2.2);

	assertTrue(vector1.equals(vector2));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DataVectorTest");
    }
}
