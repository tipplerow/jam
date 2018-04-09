
package jam.junit;

import jam.math.Point2D;

import org.junit.*;
import static org.junit.Assert.*;

public class Point2DTest extends NumericTestBase {
    private final Point2D p1a = Point2D.at(0.0, 1.0);
    private final Point2D p1b = Point2D.at(0.0, 1.0);
    private final Point2D p2  = Point2D.at(1.0, 0.0);

    @Test public void testEquals() {
        assertTrue(p1a.equals(p1a));
        assertTrue(p1a.equals(p1b));
        assertFalse(p1a.equals(p2));
    }

    @Test public void testXY() {
        assertDouble(0.0, p1a.x);
        assertDouble(1.0, p1a.y);

        assertDouble(1.0, p2.x);
        assertDouble(0.0, p2.y);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.Point2DTest");
    }
}
