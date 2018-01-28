
package jam.junit;

import jam.math.Line2;
import jam.math.Point2;

import org.junit.*;
import static org.junit.Assert.*;

public class Line2Test extends NumericTestBase {
    // Lines with y = 1 + 0.5 * x...
    private static final Line2 line1 = new Line2(Point2.at(5.0, 3.5), 0.5);
    private static final Line2 line2 = Line2.through(Point2.at(-6.0, -2.0), Point2.at(8.0, 5.0));

    // Lines with y = 4 - 2 * x...
    private static final Line2 line3 = new Line2(Point2.at(3.0, -2.0), -2.0);
    private static final Line2 line4 = Line2.through(Point2.at(-1.0, 6.0), Point2.at(5.0, -6.0));

    // Horizontal lines at y = 3...
    private static final Line2 h1 = Line2.through(Point2.at(1.0, 3.0), Point2.at(-2.0, 3.0));
    private static final Line2 h2 = Line2.horizontal(3.0);

    // Horizontal line at y = 4...
    private static final Line2 h3 = Line2.horizontal(4.0);

    // Vertical lines at x = 1...
    private static final Line2 v1 = Line2.through(Point2.at(1.0, 3.0), Point2.at(1.0, -2.0));
    private static final Line2 v2 = Line2.vertical(1.0);

    // Vertical line at x = 4...
    private static final Line2 v3 = Line2.vertical(4.0);

    @Test public void testContains() {
        assertTrue(line1.contains(0.0, 1.0));
        assertTrue(line2.contains(0.0, 1.0));
        assertFalse(line3.contains(0.0, 1.0));
        assertFalse(line4.contains(0.0, 1.0));

        assertFalse(line1.contains(0.0, 4.0));
        assertFalse(line2.contains(0.0, 4.0));
        assertTrue(line3.contains(0.0, 4.0));
        assertTrue(line4.contains(0.0, 4.0));

        assertTrue(line1.contains(10.0, 6.0));
        assertTrue(line2.contains(10.0, 6.0));
        assertFalse(line3.contains(10.0, 6.0));
        assertFalse(line4.contains(10.0, 6.0));

        assertFalse(line1.contains(10.0, -16.0));
        assertFalse(line2.contains(10.0, -16.0));
        assertTrue(line3.contains(10.0, -16.0));
        assertTrue(line4.contains(10.0, -16.0));

        assertTrue(h1.contains(-10.0, 3.0));
        assertTrue(h1.contains(  0.0, 3.0));
        assertTrue(h1.contains( 10.0, 3.0));
        assertFalse(h1.contains( 5.0, 3.1));

        assertTrue(v1.contains(1.0, -3.0));
        assertTrue(v1.contains(1.0, 33.0));
        assertFalse(v1.contains(2.0, 4.0));
    }

    @Test public void testEquals() {
        assertTrue(line1.equals(line1));
        assertTrue(line1.equals(line2));
        assertFalse(line1.equals(line3));
        assertFalse(line1.equals(line4));
        
        assertFalse(line3.equals(line1));
        assertFalse(line3.equals(line2));
        assertTrue(line3.equals(line3));
        assertTrue(line3.equals(line4));

        assertTrue(h1.equals(h1));
        assertTrue(h1.equals(h2));
        assertFalse(h1.equals(h3));

        assertTrue(v1.equals(v1));
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test public void testGetPoint() {
        assertEquals(Point2.at(0.0, 1.0), line1.getPoint());
        assertEquals(Point2.at(0.0, 4.0), line3.getPoint());

        assertEquals(Point2.at(0.0, 3.0), h1.getPoint());
        assertEquals(Point2.at(0.0, 4.0), h3.getPoint());

        assertEquals(Point2.at(1.0, 0.0), v1.getPoint());
        assertEquals(Point2.at(4.0, 0.0), v3.getPoint());
    }

    @Test public void testGetSlope() {
        assertDouble(0.5, line1.getSlope());
        assertDouble(0.5, line2.getSlope());

        assertDouble(-2.0, line3.getSlope());
        assertDouble(-2.0, line4.getSlope());
    }

    @Test public void testGetX() {
        assertDouble(-4.0, line1.getX(-1.0));
        assertDouble(-2.0, line1.getX( 0.0));
        assertDouble( 0.0, line1.getX( 1.0));
        assertDouble( 2.0, line1.getX( 2.0));

        assertTrue(Double.isNaN(h1.getX(0.0)));

        assertDouble(1.0, v1.getX(-10.0));
        assertDouble(1.0, v1.getX( 10.0));

        assertDouble(1.0, v1.getX(-5.0));
        assertDouble(1.0, v1.getX( 5.0));
    }

    @Test public void testGetY() {
        assertDouble(0.5, line1.getY(-1.0));
        assertDouble(1.0, line1.getY( 0.0));
        assertDouble(1.5, line1.getY( 1.0));

        assertDouble(3.0, h1.getY(-10.0));
        assertDouble(3.0, h1.getY( 10.0));

        assertTrue(Double.isNaN(v1.getY(1.0)));
    }

    @Test public void testInterpolate() {
        Point2 p1 = Point2.at(-1.0, 1.0);
        Point2 p2 = Point2.at( 5.0, 1.0);
        Point2 p3 = Point2.at( 5.0, 4.0);

        assertDouble(1.0, Line2.interpolate(p1, p2, -2.0));
        assertDouble(1.0, Line2.interpolate(p1, p2, -1.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  0.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  1.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  2.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  3.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  4.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  5.0));
        assertDouble(1.0, Line2.interpolate(p1, p2,  6.0));

        assertDouble(0.5, Line2.interpolate(p1, p3, -2.0));
        assertDouble(1.0, Line2.interpolate(p1, p3, -1.0));
        assertDouble(1.5, Line2.interpolate(p1, p3,  0.0));
        assertDouble(2.0, Line2.interpolate(p1, p3,  1.0));
        assertDouble(2.5, Line2.interpolate(p1, p3,  2.0));
        assertDouble(3.0, Line2.interpolate(p1, p3,  3.0));
        assertDouble(3.5, Line2.interpolate(p1, p3,  4.0));
        assertDouble(4.0, Line2.interpolate(p1, p3,  5.0));
        assertDouble(4.5, Line2.interpolate(p1, p3,  6.0));
    }

    @Test public void testIsHorizontal() {
        assertTrue(h1.isHorizontal());
        assertTrue(h3.isHorizontal());

        assertFalse(line1.isHorizontal());
        assertFalse(line3.isHorizontal());

        assertFalse(v1.isHorizontal());
        assertFalse(v3.isHorizontal());
    }

    @Test public void testIsVertical() {
        assertTrue(v1.isVertical());
        assertTrue(v3.isVertical());

        assertFalse(line1.isVertical());
        assertFalse(line3.isVertical());

        assertFalse(h1.isVertical());
        assertFalse(h3.isVertical());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.Line2Test");
    }
}
