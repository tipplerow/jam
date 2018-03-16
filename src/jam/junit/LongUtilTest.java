
package jam.junit;

import jam.math.LongUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class LongUtilTest {
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
        org.junit.runner.JUnitCore.main("jam.junit.LongUtilTest");
    }
}
