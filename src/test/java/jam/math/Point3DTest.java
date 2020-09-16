
package jam.math;

import java.util.Arrays;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class Point3DTest extends NumericTestBase {
    private final Point3D p1a = Point3D.at(0.0, 1.0, 2.0);
    private final Point3D p1b = Point3D.at(0.0, 1.0, 2.0);
    private final Point3D p2  = Point3D.at(1.0, 0.0, 2.0);

    @Test public void testDistance() {
        Point3D pt1 = Point3D.at( 1.0, 2.0, 9.0);
        Point3D pt2 = Point3D.at(-2.0, 6.0, 4.0);

        assertDouble(Math.sqrt(50.0), pt1.distance(pt2));
        assertDouble(Math.sqrt(50.0), pt2.distance(pt1));
    }

    @Test public void testEquals() {
        assertTrue(p1a.equals(p1a));
        assertTrue(p1a.equals(p1b));
        assertFalse(p1a.equals(p2));
    }

    @Test public void testXYZ() {
        assertDouble(0.0, p1a.x);
        assertDouble(1.0, p1a.y);
        assertDouble(2.0, p1a.z);
        
        assertDouble(1.0, p2.x);
        assertDouble(0.0, p2.y);
        assertDouble(2.0, p2.z);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.math.Point3DTest");
    }
}
