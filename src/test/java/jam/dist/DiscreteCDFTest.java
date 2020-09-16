
package jam.dist;

import jam.junit.NumericTestBase;
import jam.math.DoubleUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class DiscreteCDFTest extends NumericTestBase {
    private final DiscretePDF pdf =
        DiscretePDF.compute(1, 2, 3, 5, 2, 3, 5, 3, 5, 5);

    private final DiscreteCDF cdf =
        DiscreteCDF.compute(pdf);

    @Test public void testEquals() {
        DiscretePDF pdf1 = DiscretePDF.create(0, new double[] { 0.1, 0.2, 0.3, 0.4 });
        DiscretePDF pdf2 = DiscretePDF.create(0, new double[] { 0.1, 0.2, 0.2999, 0.4001 });
        DiscretePDF pdf3 = DiscretePDF.create(5, new double[] { 0.1, 0.2, 0.3, 0.4 });

        DiscreteCDF cdf1 = DiscreteCDF.compute(pdf1);
        DiscreteCDF cdf2 = DiscreteCDF.compute(pdf2);
        DiscreteCDF cdf3 = DiscreteCDF.compute(pdf3);

        assertFalse(cdf1.equals(cdf2));
        assertFalse(cdf1.equals(cdf3));

        assertTrue(cdf1.equals(cdf2, 0.001));
        assertFalse(cdf1.equals(cdf2, 0.00001));
        assertFalse(cdf1.equals(cdf3, 0.1));
    }

    @Test public void testEvaluate() {
        assertDouble(0.0, cdf.evaluate(0));
        assertDouble(0.1, cdf.evaluate(1));
        assertDouble(0.3, cdf.evaluate(2));
        assertDouble(0.6, cdf.evaluate(3));
        assertDouble(0.6, cdf.evaluate(4));
        assertDouble(1.0, cdf.evaluate(5));
        assertDouble(1.0, cdf.evaluate(6));
    }

    @Test public void testInverse() {
        assertEquals(1, cdf.inverse(0.0));
        assertEquals(1, cdf.inverse(0.099999));
        assertEquals(1, cdf.inverse(0.1));

        assertEquals(2, cdf.inverse(0.100001));
        assertEquals(2, cdf.inverse(0.299999));
        assertEquals(2, cdf.inverse(0.3));

        assertEquals(3, cdf.inverse(0.300001));
        assertEquals(3, cdf.inverse(0.599999));
        assertEquals(3, cdf.inverse(0.6));

        assertEquals(5, cdf.inverse(0.600001));
        assertEquals(5, cdf.inverse(0.999999));
        assertEquals(5, cdf.inverse(1.0));
    }

    @Test public void testMedian() {
        assertDouble(3.0, cdf.median());

        assertDouble(2.0, DiscreteCDF.compute(1, 2, 3).median());
        assertDouble(5.0, DiscreteCDF.compute(5, 5, 5).median());

        assertDouble(1.5, DiscreteCDF.compute(1, 2).median());
        assertDouble(3.0, DiscreteCDF.compute(1, 5).median());
    }

    @Test public void testPDF() {
        DiscretePDF pdf2 = cdf.pdf();

        assertDouble(0.0, pdf2.evaluate(0));
        assertDouble(0.1, pdf2.evaluate(1));
        assertDouble(0.2, pdf2.evaluate(2));
        assertDouble(0.3, pdf2.evaluate(3));
        assertDouble(0.0, pdf2.evaluate(4));
        assertDouble(0.4, pdf2.evaluate(5));
        assertDouble(0.0, pdf2.evaluate(6));
    }

    @Test public void testSupport() {
        assertEquals(1, cdf.support().lower());
        assertEquals(5, cdf.support().upper());
    }

    @Test public void testSample() {
        int SAMPLE_COUNT = 100000;
        int[] count = new int[cdf.support().upper() + 1];

        for (int k = 0; k < SAMPLE_COUNT; k++)
            ++count[cdf.sample(random())];
            
        for (int k = 0; k < count.length; k++)
            assertEquals(pdf.evaluate(k), DoubleUtil.ratio(count[k], SAMPLE_COUNT), 0.005);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.dist.DiscreteCDFTest");
    }
}
