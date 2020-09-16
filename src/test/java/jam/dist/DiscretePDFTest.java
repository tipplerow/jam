
package jam.dist;

import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class DiscretePDFTest extends NumericTestBase {
    private final DiscretePDF dist =
        DiscretePDF.compute(1, 2, 3, 5, 2, 3, 5, 3, 5, 5);

    @Test public void testEquals() {
        DiscretePDF pdf1 = DiscretePDF.create(0, new double[] { 0.1, 0.2, 0.3, 0.4 });
        DiscretePDF pdf2 = DiscretePDF.create(0, new double[] { 0.1, 0.2, 0.2999, 0.4001 });
        DiscretePDF pdf3 = DiscretePDF.create(5, new double[] { 0.1, 0.2, 0.3, 0.4 });

        assertFalse(pdf1.equals(pdf2));
        assertFalse(pdf1.equals(pdf3));

        assertTrue(pdf1.equals(pdf2, 0.001));
        assertFalse(pdf1.equals(pdf2, 0.00001));
        assertFalse(pdf1.equals(pdf3, 0.1));
    }

    @Test public void testEvaluate() {
        assertDouble(0.0, dist.evaluate(0));
        assertDouble(0.1, dist.evaluate(1));
        assertDouble(0.2, dist.evaluate(2));
        assertDouble(0.3, dist.evaluate(3));
        assertDouble(0.0, dist.evaluate(4));
        assertDouble(0.4, dist.evaluate(5));
        assertDouble(0.0, dist.evaluate(6));
    }

    @Test public void testMean() {
        assertDouble(3.4, dist.mean());
    }

    @Test public void testVariance() {
        assertDouble(2.04, dist.variance());
    }

    @Test public void testSupport() {
        assertEquals(1, dist.support().lower());
        assertEquals(5, dist.support().upper());
    }

    @Test public void testExplicit() {
        DiscretePDF pdf = DiscretePDF.create(10, new double[] { 0.2, 0.3, 0.5 });

        assertEquals(10, pdf.support().lower());
        assertEquals(12, pdf.support().upper());

        assertDouble(0.0, pdf.evaluate(9));
        assertDouble(0.2, pdf.evaluate(10));
        assertDouble(0.3, pdf.evaluate(11));
        assertDouble(0.5, pdf.evaluate(12));
        assertDouble(0.0, pdf.evaluate(13));
    }

    @Test(expected = RuntimeException.class)
    public void testExplicitInvalid() {
        DiscretePDF pdf = DiscretePDF.create(10, new double[] { 0.2, 0.3, 0.5, 0.1 });
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.dist.DiscretePDFTest");
    }
}
