
package jam.junit;

import java.util.List;

import jam.util.IterableUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class IterableUtilTest {
    @Test public void testEquals() {
        assertTrue(IterableUtil.equals(List.of(), List.of()));
        assertTrue(IterableUtil.equals(List.of("A"), List.of("A")));
        assertTrue(IterableUtil.equals(List.of("A", "B", "C"), List.of("A", "B", "C")));

        assertFalse(IterableUtil.equals(List.of(), List.of("A")));
        assertFalse(IterableUtil.equals(List.of("A"), List.of()));

        assertFalse(IterableUtil.equals(List.of("A", "B"), List.of("A", "A")));
        assertFalse(IterableUtil.equals(List.of("A", "A"), List.of("A", "B")));

        assertFalse(IterableUtil.equals(List.of("A", "B", "C"), List.of("A", "B")));
        assertFalse(IterableUtil.equals(List.of("A", "B", "C"), List.of("A", "B", "C", "D")));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.IterableUtilTest");
    }
}
