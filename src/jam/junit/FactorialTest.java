
package jam.junit;

import jam.math.Factorial;

import org.junit.*;
import static org.junit.Assert.*;

public class FactorialTest {
    @Test public void testChoose() {
        assertEquals(1, Factorial.choose(  0, 0));
        assertEquals(1, Factorial.choose(  1, 0));
        assertEquals(1, Factorial.choose( 10, 0));
        assertEquals(1, Factorial.choose(100, 0));

        assertEquals(  1, Factorial.choose(  1, 1));
        assertEquals(  2, Factorial.choose(  2, 1));
        assertEquals( 10, Factorial.choose( 10, 1));
        assertEquals(100, Factorial.choose(100, 1));

        assertEquals(   1, Factorial.choose(  2, 2));
        assertEquals(  45, Factorial.choose( 10, 2));
        assertEquals(4950, Factorial.choose(100, 2));

        assertEquals( 1, Factorial.choose(6, 0));
        assertEquals( 6, Factorial.choose(6, 1));
        assertEquals(15, Factorial.choose(6, 2));
        assertEquals(20, Factorial.choose(6, 3));
        assertEquals(15, Factorial.choose(6, 4));
        assertEquals( 6, Factorial.choose(6, 5));
        assertEquals( 1, Factorial.choose(6, 6));

        assertEquals(4950, Factorial.choose(100,  98));
        assertEquals( 100, Factorial.choose(100,  99));
        assertEquals(   1, Factorial.choose(100, 100));

        assertEquals(1166803110, Factorial.choose(33, 17));
    }

    @Test public void testIntValue() {
        assertEquals(        1, Factorial.intValue(0));
        assertEquals(        1, Factorial.intValue(1));
        assertEquals(        2, Factorial.intValue(2));
        assertEquals(        6, Factorial.intValue(3));
        assertEquals(       24, Factorial.intValue(4));
        assertEquals(      120, Factorial.intValue(5));
        assertEquals(      720, Factorial.intValue(6));
        assertEquals(     5040, Factorial.intValue(7));
        assertEquals(    40320, Factorial.intValue(8));
        assertEquals(   362880, Factorial.intValue(9));
        assertEquals(  3628800, Factorial.intValue(10));
        assertEquals( 39916800, Factorial.intValue(11));
        assertEquals(479001600, Factorial.intValue(12));
    }

    @Test(expected = RuntimeException.class)
    public void testIntValueNegative() {
        Factorial.intValue(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testIntValueTooLarge() {
        Factorial.intValue(13);
    }

    @Test public void testLogValue() {
        for (int N = 0; N <= Factorial.MAX_INT_ARGUMENT; N++)
            assertEquals(Math.log(Factorial.intValue(N)), Factorial.logValue(N), 1.0E-12);

        assertEquals( 58.003605, Factorial.logValue(25), 0.000001);
        assertEquals(148.477767, Factorial.logValue(50), 0.000001);
    }

    @Test public void testPermute() {
        assertEquals(  1, Factorial.permute(5, 0));
        assertEquals(  5, Factorial.permute(5, 1));
        assertEquals( 20, Factorial.permute(5, 2));
        assertEquals( 60, Factorial.permute(5, 3));
        assertEquals(120, Factorial.permute(5, 4));
        assertEquals(120, Factorial.permute(5, 5));

        assertEquals(100,           Factorial.permute(100, 1));
        assertEquals(100 * 99,      Factorial.permute(100, 2));
        assertEquals(100 * 99 * 98, Factorial.permute(100, 3));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.FactorialTest");
    }
}
