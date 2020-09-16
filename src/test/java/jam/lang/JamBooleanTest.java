
package jam.lang;

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

    @Test public void testValueOfCharacter() {
	assertTrue(JamBoolean.valueOf('1'));
	assertTrue(JamBoolean.valueOf('T'));
	assertTrue(JamBoolean.valueOf('Y'));

	assertFalse(JamBoolean.valueOf('0'));
	assertFalse(JamBoolean.valueOf('F'));
	assertFalse(JamBoolean.valueOf('N'));
    }

    @Test public void testValueOfString() {
	assertTrue(JamBoolean.valueOf("1"));
	assertTrue(JamBoolean.valueOf("Y"));
	assertTrue(JamBoolean.valueOf("true"));

	assertFalse(JamBoolean.valueOf("0"));
	assertFalse(JamBoolean.valueOf("N"));
	assertFalse(JamBoolean.valueOf("false"));
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalidCharacter() {
	JamBoolean.valueOf('a');
    }

    @Test(expected = RuntimeException.class)
    public void testValueOfInvalidString() {
	JamBoolean.valueOf("no");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.lang.JamBooleanTest");
    }
}
