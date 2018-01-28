
package jam.junit;

import java.util.Arrays;
import jam.util.ArrayUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class ArrayUtilTest {

    @Test public void testCat() {
        assertEquals(0, ArrayUtil.cat().length);

        String[] array1 = new String[] { "A", "B", "C" };
        String[] array2 = new String[] { "D" };
        String[] array3 = new String[] { "E", "F" };

        String[] expected1 = new String[] { "A", "B", "C" };
        String[] expected2 = new String[] { "A", "B", "C", "D" };
        String[] expected3 = new String[] { "A", "B", "C", "D", "E", "F" };

        assertTrue(Arrays.equals(expected1, ArrayUtil.cat(array1)));
        assertTrue(Arrays.equals(expected2, ArrayUtil.cat(array1, array2)));
        assertTrue(Arrays.equals(expected3, ArrayUtil.cat(array1, array2, array3)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.ArrayUtilTest");
    }
}
