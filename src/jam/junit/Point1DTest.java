
package jam.junit;

import java.util.Arrays;

import jam.math.Point1D;

import org.junit.*;
import static org.junit.Assert.*;

public class Point1DTest extends NumericTestBase {
    private final Point1D p1a = Point1D.at(0.0);
    private final Point1D p1b = Point1D.at(0.0);
    private final Point1D p2  = Point1D.at(1.0);

    @Test public void testDistance() {
        Point1D pt1 = Point1D.at( 1.0);
        Point1D pt2 = Point1D.at(-2.0);

        assertDouble(3.0, pt1.distance(pt2));
        assertDouble(3.0, pt2.distance(pt1));
    }

    @Test public void testEquals() {
        assertTrue(p1a.equals(p1a));
        assertTrue(p1a.equals(p1b));
        assertFalse(p1a.equals(p2));
    }

    @Test public void testX() {
        assertDouble(0.0, p1a.x);
        assertDouble(1.0, p2.x);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.Point1DTest");
    }
}
