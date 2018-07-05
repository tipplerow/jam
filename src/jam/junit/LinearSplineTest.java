
package jam.junit;

import jam.math.SplineFunction;
import jam.math.Point2D;

import org.junit.*;
import static org.junit.Assert.*;

public class LinearSplineTest extends NumericTestBase {
    @Test public void testEvaluate() {
        Point2D p1 = Point2D.at(0.0, 0.0);
        Point2D p2 = Point2D.at(1.0, 3.0);
        Point2D p3 = Point2D.at(2.0, 5.0);
        Point2D p4 = Point2D.at(3.0, 6.0);

        SplineFunction spline = SplineFunction.linear(p2, p4, p1, p3);

        assertDouble(-3.0, spline.evaluate(-1.0));
        assertDouble(-0.3, spline.evaluate(-0.1));
        assertDouble( 0.0, spline.evaluate( 0.0));
        assertDouble( 0.3, spline.evaluate( 0.1));
        assertDouble( 2.7, spline.evaluate( 0.9));
        assertDouble( 3.0, spline.evaluate( 1.0));
        assertDouble( 3.2, spline.evaluate( 1.1));
        assertDouble( 4.8, spline.evaluate( 1.9));
        assertDouble( 5.0, spline.evaluate( 2.0));
        assertDouble( 5.1, spline.evaluate( 2.1));
        assertDouble( 5.9, spline.evaluate( 2.9));
        assertDouble( 6.0, spline.evaluate( 3.0));
        assertDouble( 6.1, spline.evaluate( 3.1));
        assertDouble( 7.0, spline.evaluate( 4.0));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.LinearSplineTest");
    }
}
