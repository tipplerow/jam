
package jam.hist;

import java.util.List;

import jam.dist.NormalDistribution;
import jam.junit.NumericTestBase;
import jam.math.DoubleRange;
import jam.math.JamUnivariateFunction;
import jam.math.Point2D;

import org.junit.*;
import static org.junit.Assert.*;

public class HistogramTest extends NumericTestBase {
    @Test public void testLinear() {
        double[] data =
            new double[] { 0.99999999,
                           1.0, 1.5, 2.0,
                           2.1, 3.0,
                           3.1,
                           4.1, 4.2, 4.9, 5.0,
                           5.00000001 };

        Histogram hist = Histogram.compute(1.0, 5.0, 4, data);
        List<Bin> bins = hist.viewBins();

        assertEquals(10, hist.getTotalCount());
        assertEquals(4, bins.size());

        assertEquals(DoubleRange.closed(  1.0, 2.0), bins.get(0).getRange());
        assertEquals(DoubleRange.leftOpen(2.0, 3.0), bins.get(1).getRange());
        assertEquals(DoubleRange.leftOpen(3.0, 4.0), bins.get(2).getRange());
        assertEquals(DoubleRange.leftOpen(4.0, 5.0), bins.get(3).getRange());

        assertEquals(3, bins.get(0).getCount());
        assertEquals(2, bins.get(1).getCount());
        assertEquals(1, bins.get(2).getCount());
        assertEquals(4, bins.get(3).getCount());
    }

    @Test public void testLog() {
        double[] data =
            new double[] { 0.0099999999,
                           0.01, 0.02, 0.1,
                           0.11, 1.0,
                           10.0,
                           10.00000001 };

        Histogram hist = Histogram.computeLog(0.01, 10.0, 3, data);
        List<Bin> bins = hist.viewBins();

        assertEquals(6, hist.getTotalCount());
        assertEquals(3, bins.size());

        assertEquals(DoubleRange.closed(  0.01, 0.1), bins.get(0).getRange());
        assertEquals(DoubleRange.leftOpen(0.1,  1.0), bins.get(1).getRange());
        assertEquals(DoubleRange.leftOpen(1.0, 10.0), bins.get(2).getRange());

        assertEquals(3, bins.get(0).getCount());
        assertEquals(2, bins.get(1).getCount());
        assertEquals(1, bins.get(2).getCount());
    }

    @Test public void testNormal() {
        double[]  obs  = NormalDistribution.STANDARD.sample(random(), 1000000);
        Histogram hist = Histogram.compute(-4.0, 4.0, 16, obs);

        JamUnivariateFunction cdf = hist.getCDF();
        JamUnivariateFunction pdf = hist.getPDF();

        for (double x = -5.0; x <= 5.0; x += 0.1) {
            double actual   = cdf.evaluate(x);
            double expected = NormalDistribution.STANDARD.cdf(x);
            double error    = Math.abs(actual - expected);

            assertTrue(error < 0.01);
        }

        for (double x = -5.0; x <= 5.0; x += 0.1) {
            double actual   = pdf.evaluate(x);
            double expected = NormalDistribution.STANDARD.pdf(x);
            double error    = Math.abs(actual - expected);

            assertTrue(error < 0.02);
        }

        hist = Histogram.compute(-4.0, 4.0, 64, obs);
        cdf  = hist.getCDF();
        pdf  = hist.getPDF();

        for (double x = -5.0; x <= 5.0; x += 0.1) {
            double actual   = cdf.evaluate(x);
            double expected = NormalDistribution.STANDARD.cdf(x);
            double error    = Math.abs(actual - expected);

            assertTrue(error < 0.001);
        }

        for (double x = -5.0; x <= 5.0; x += 0.1) {
            double actual   = pdf.evaluate(x);
            double expected = NormalDistribution.STANDARD.pdf(x);
            double error    = Math.abs(actual - expected);

            assertTrue(error < 0.004);
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.hist.HistogramTest");
    }
}
