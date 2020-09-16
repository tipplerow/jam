
package jam.math;

import org.junit.*;
import static org.junit.Assert.*;

public class LongUtilTest {
    @Test public void testIntValue() {
        assertEquals(-99887766, LongUtil.intValue(-99887766L));
        assertEquals( 12345678, LongUtil.intValue( 12345678L));

        assertEquals(-2147483648, LongUtil.intValue(-(1L << 31)));
        assertEquals( 2147483647, LongUtil.intValue( (1L << 31) - 1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIntValueInvalid1() {
        LongUtil.intValue(-(1L << 33L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIntValueInvalid2() {
        LongUtil.intValue(1L << 33L);
    }

    @Test public void testParseLong() {
        assertEquals(-234L, LongUtil.parseLong("-234"));
        assertEquals(1234L, LongUtil.parseLong("1234"));

        assertEquals(1234L, LongUtil.parseLong("1.234E3"));
        assertEquals(1000000000000L, LongUtil.parseLong("1E12"));
    }

    @Test(expected = NumberFormatException.class)
    public void testParseLongInvalid1() {
        LongUtil.parseLong("1.234");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseLongInvalid2() {
        LongUtil.parseLong("1.234E2");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.LongUtilTest");
    }
}
