
package jam.junit;

import jam.lang.JamBoolean;

import org.junit.*;
import static org.junit.Assert.*;

public class JamBooleanTest {
    @Test public void testDoubleValue() {
	assertEquals(0.0, JamBoolean.doubleValue(false), 1.0e-15);
	assertEquals(1.0, JamBoolean.doubleValue(true),  1.0e-15);
    }

    @Test public void testIntValue() {
	assertEquals(0, JamBoolean.intValue(false));
	assertEquals(1, JamBoolean.intValue(true));
    }

    @Test public void testValueOf() {
	assertTrue(JamBoolean.valueOf('1'));
	assertTrue(JamBoolean.valueOf('T'));

	assertFalse(JamBoolean.valueOf('0'));
	assertFalse(JamBoolean.valueOf('F'));
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalid() {
	JamBoolean.valueOf('a');
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.JamBooleanTest");
    }
}
