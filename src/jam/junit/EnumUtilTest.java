
package jam.junit;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import jam.util.EnumUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class EnumUtilTest {
    public static enum ABC { A, B, C };

    @Test public void testCount() {
        assertEquals(3, EnumUtil.count(ABC.class));
    }

    @Test public void testList() {
        assertEquals(List.of(ABC.A, ABC.B, ABC.C), EnumUtil.list(ABC.class));
    }

    @Test public void testNames() {
        Set<String> names = EnumUtil.names(ABC.class);

        assertEquals(3, names.size());
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
        assertTrue(names.contains("C"));
    }

    @Test public void testValueOf() {
        assertEquals(ABC.A, EnumUtil.valueOf(ABC.class, "A"));
        assertEquals(ABC.B, EnumUtil.valueOf(ABC.class, "B"));
        assertEquals(ABC.C, EnumUtil.valueOf(ABC.class, "C"));
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalid() {
        EnumUtil.valueOf(ABC.class, "FOO");
    }

    @Test public void testValues() {
        assertTrue(Arrays.equals(ABC.values(), EnumUtil.values(ABC.class)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EnumUtilTest");
    }
}
