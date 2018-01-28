
package jam.junit;

import java.util.Arrays;

import jam.math.Discretization;

import org.junit.*;
import static org.junit.Assert.*;

public class DiscretizationTest extends NumericTestBase {

    @Test public void testArrays() {
        Discretization disc = Discretization.withResolution(0.02);

        assertTrue(Arrays.equals(new long[] { -50L, 0L, 50L }, disc.asDiscrete(new double[] { -1.0, 0.0, 1.0 })));
        assertTrue(Arrays.equals(new double[] { -1.0, 0.0, 1.0 }, disc.asContinuous(new long[] { -50L, 0L, 50L } )));
    }
                                 

    @Test public void testAsContinuous() {
        Discretization disc = Discretization.withResolution(0.001);

        assertDouble(-1.0, disc.asContinuous(-1000L));
        assertDouble( 0.0, disc.asContinuous(    0L));
        assertDouble( 0.5, disc.asContinuous(  500L));
    }

    @Test public void testAsDiscrete() {
        Discretization disc = Discretization.withResolution(0.01);

        assertEquals(-201L, disc.asDiscrete(-2.00500001));
        assertEquals(-200L, disc.asDiscrete(-2.00499999));
        assertEquals(-200L, disc.asDiscrete(-2.0));
        assertEquals(-200L, disc.asDiscrete(-1.99500001));
        assertEquals(-199L, disc.asDiscrete(-1.99499999));

        assertEquals(-1L, disc.asDiscrete(-0.00500001));
        assertEquals( 0L, disc.asDiscrete(-0.00499999));
        assertEquals( 0L, disc.asDiscrete( 0.0));
        assertEquals( 0L, disc.asDiscrete( 0.00499999));
        assertEquals( 1L, disc.asDiscrete( 0.00500001));

        assertEquals( 99L, disc.asDiscrete(0.99499999));
        assertEquals(100L, disc.asDiscrete(0.99500001));
        assertEquals(100L, disc.asDiscrete(1.0));
        assertEquals(100L, disc.asDiscrete(1.00499999));
        assertEquals(101L, disc.asDiscrete(1.00500001));
    }

    @Test public void testCompare() {
        Discretization disc = Discretization.withResolution(0.01);

        assertTrue(disc.compare(1.994, 2.0)  < 0);
        assertTrue(disc.compare(1.996, 2.0) == 0);
        assertTrue(disc.compare(2.0,   2.0) == 0);
        assertTrue(disc.compare(2.004, 2.0) == 0);
        assertTrue(disc.compare(2.006, 2.0)  > 0);

        assertTrue(disc.compare(2.0, 1.994)  > 0);
        assertTrue(disc.compare(2.0, 1.996) == 0);
        assertTrue(disc.compare(2.0, 2.0)   == 0);
        assertTrue(disc.compare(2.0, 2.004) == 0);
        assertTrue(disc.compare(2.0, 2.006)  < 0);
    }

    @Test public void testEquals() {
        Discretization disc = Discretization.withResolution(0.1);

        assertFalse(disc.equals(3.3, 3.24));
        assertTrue(disc.equals(3.3, 3.26));
        assertTrue(disc.equals(3.3, 3.3));
        assertTrue(disc.equals(3.3, 3.34));
        assertFalse(disc.equals(3.3, 3.36));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidResolution() {
        new Discretization(0.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DiscretizationTest");
    }
}
