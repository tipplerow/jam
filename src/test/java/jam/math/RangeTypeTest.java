
package jam.math;

import org.junit.*;
import static org.junit.Assert.*;

public class RangeTypeTest {
    @Test public void testFormat() {
        assertEquals("(1.0, 2.0)", RangeType.OPEN.format(1.0, 2.0));
        assertEquals("[1.0, 2.0)", RangeType.LEFT_CLOSED.format(1.0, 2.0));
        assertEquals("(1.0, 2.0]", RangeType.LEFT_OPEN.format(1.0, 2.0));
        assertEquals("[1.0, 2.0]", RangeType.CLOSED.format(1.0, 2.0));
    }

    @Test public void testParse() {
        assertEquals(RangeType.OPEN,        RangeType.parse("(1.0, 2.0)"));
        assertEquals(RangeType.LEFT_CLOSED, RangeType.parse("[1.0, 2.0)"));
        assertEquals(RangeType.LEFT_OPEN,   RangeType.parse("(1.0, 2.0]"));
        assertEquals(RangeType.CLOSED,      RangeType.parse("[1.0, 2.0]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBad1() {
        RangeType.parse("{1, 2)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseBad2() {
        RangeType.parse("[1, 2}");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.RangeTypeTest");
    }
}
