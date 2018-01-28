
package jam.junit;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jam.data.DataMatrix;

import org.junit.*;
import static org.junit.Assert.*;

public class DataMatrixTest extends NumericTestBase {
    private final List<String> keyList1 = Arrays.asList("abc", "def");
    private final List<String> keyList2 = Arrays.asList("def", "ghi", "jkl");
    private final List<String> keyList3 = Arrays.asList("def", "abc");
    private final List<String> keyList4 = Arrays.asList("jkl", "ghi", "def");
    private final List<String> keyList5 = Arrays.asList("abc", "ghi", "jkl");

    private final Set<String> keySet1 = new TreeSet<String>(keyList1);
    private final Set<String> keySet2 = new TreeSet<String>(keyList2);
    private final Set<String> keySet3 = new LinkedHashSet<String>(keyList3);
    private final Set<String> keySet4 = new LinkedHashSet<String>(keyList4);
    private final Set<String> keySet5 = new TreeSet<String>(keyList5);

    private final DataMatrix matrix1 = new DataMatrix(keySet1, keySet2, 3.3);
    private final DataMatrix matrix2 = new DataMatrix(keySet3, keySet4, 3.3);
    private final DataMatrix matrix3 = new DataMatrix(keySet1, keySet5, 3.3);

    @Test public void testBasic() {
        assertEquals(2, matrix1.nrow());
        assertEquals(3, matrix1.ncol());

        assertTrue(matrix1.contains("abc", "def"));
        assertTrue(matrix1.contains("abc", "ghi"));
        assertTrue(matrix1.contains("abc", "jkl"));
        assertTrue(matrix1.contains("def", "def"));
        assertTrue(matrix1.contains("def", "ghi"));
        assertTrue(matrix1.contains("def", "jkl"));

        assertFalse(matrix1.contains("def", "abc"));
        assertFalse(matrix1.contains("jkl", "abc"));
        assertFalse(matrix1.contains("ghi", "def"));
        assertFalse(matrix1.contains("jkl", "def"));

        matrix1.set("abc", "def", 1.1);
        matrix1.set("def", "jkl", 2.2);

        assertDouble(1.1, matrix1.get("abc", "def"));
        assertDouble(2.2, matrix1.get("def", "jkl"));
        assertDouble(3.3, matrix1.get("def", "ghi"));

        Set<String> rowKeys = matrix1.rowKeys();
        assertEquals(2, rowKeys.size());
        assertTrue(rowKeys.contains("abc"));
        assertTrue(rowKeys.contains("def"));

        Set<String> colKeys = matrix1.columnKeys();
        assertEquals(3, colKeys.size());
        assertTrue(colKeys.contains("def"));
        assertTrue(colKeys.contains("ghi"));
        assertTrue(colKeys.contains("jkl"));
    }

    @Test public void testEquals() {
	assertTrue(matrix1.equals(matrix2));
	assertFalse(matrix1.equals(matrix3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DataMatrixTest");
    }
}
