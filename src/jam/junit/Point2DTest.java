
package jam.junit;

import java.util.Arrays;

import jam.math.Point2D;

import org.junit.*;
import static org.junit.Assert.*;

public class Point2DTest extends NumericTestBase {
    private final Point2D p1a = Point2D.at(0.0, 1.0);
    private final Point2D p1b = Point2D.at(0.0, 1.0);
    private final Point2D p2  = Point2D.at(1.0, 0.0);

    @Test public void testComparators() {
        Point2D pt1 = Point2D.at(-3.0, 9.0);
        Point2D pt2 = Point2D.at(-1.0, 1.0);
        Point2D pt3 = Point2D.at( 0.0, 0.0);
        Point2D pt4 = Point2D.at( 2.0, 4.0);

        Point2D[] pts = new Point2D[] { pt3, pt2, pt4, pt1 };

        Arrays.sort(pts, Point2D.X_COMPARATOR);

        assertEquals(pt1, pts[0]);
        assertEquals(pt2, pts[1]);
        assertEquals(pt3, pts[2]);
        assertEquals(pt4, pts[3]);

        Arrays.sort(pts, Point2D.Y_COMPARATOR);

        assertEquals(pt3, pts[0]);
        assertEquals(pt2, pts[1]);
        assertEquals(pt4, pts[2]);
        assertEquals(pt1, pts[3]);
    }

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
