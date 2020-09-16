
package jam.spin;

import org.junit.*;
import static org.junit.Assert.*;

public class SpinTest {
    @Test public void testBooleanValue() {
	assertTrue(Spin.UP.booleanValue());
	assertFalse(Spin.DOWN.booleanValue());
    }

    @Test public void testDoubleValue() {
	assertEquals(+1.0, Spin.UP.doubleValue(), 1.0e-15);
	assertEquals(-1.0, Spin.DOWN.doubleValue(),  1.0e-15);
    }

    @Test public void testFlip() {
	assertEquals(Spin.DOWN, Spin.UP.flip());
	assertEquals(Spin.UP, Spin.DOWN.flip());
    }

    @Test public void testIntValue() {
	assertEquals(+1, Spin.UP.intValue());
	assertEquals(-1, Spin.DOWN.intValue());
    }

    @Test public void testValueOfBoolean() {
	assertEquals(Spin.UP, Spin.valueOf(true));
	assertEquals(Spin.DOWN, Spin.valueOf(false));
    }

    @Test public void testValueOfChar() {
	assertEquals(Spin.UP, Spin.valueOf('+'));
	assertEquals(Spin.DOWN, Spin.valueOf('-'));
    }

    @Test public void testValueOfInt() {
	assertEquals(Spin.UP, Spin.valueOf(+1));
	assertEquals(Spin.DOWN, Spin.valueOf(-1));
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalidChar() {
	Spin.valueOf('1');
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalidInt() {
	Spin.valueOf(0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.spin.SpinTest");
    }
}
