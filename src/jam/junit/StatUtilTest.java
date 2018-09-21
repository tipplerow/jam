
package jam.junit;

import org.apache.commons.collections.primitives.ArrayDoubleList;

import jam.math.StatUtil;
import jam.vector.VectorView;

import org.junit.*;
import static org.junit.Assert.*;

public class StatUtilTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0e-15;

    public StatUtilTest() {
        super(TOLERANCE);
    }

    @Test public void testCor() {
        VectorView x = VectorView.wrap( 1.0,  2.0,  3.0,  4.0,  5.0,  6.0,  7.0,  8.0,  9.0);
        VectorView y = VectorView.wrap(-0.9,  0.4, -0.1, -0.4, -0.8, -2.2, -0.2, -0.2,  1.2);

        assertEquals(0.224421, StatUtil.cor(x, y), 1.0e-06);
    }

    @Test public void testCov() {
        VectorView x = VectorView.wrap( 1.0,  2.0,  3.0,  4.0,  5.0,  6.0,  7.0,  8.0,  9.0);
        VectorView y = VectorView.wrap(-0.9,  0.4, -0.1, -0.4, -0.8, -2.2, -0.2, -0.2,  1.2);

        assertEquals(0.575000, StatUtil.cov(x, y, true), 1.0e-06);
        assertEquals(0.511111, StatUtil.cov(x, y, false), 1.0e-06);
    }

    @Test public void testCumSum() {
        VectorView values1 = VectorView.wrap(0.1, 0.2, 0.3, 0.4);
        VectorView expect1 = VectorView.wrap(0.1, 0.3, 0.6, 1.0);

        VectorView values2 = VectorView.wrap(Double.NaN, Double.NaN, 0.1, -0.2, 1.3, Double.NaN, 6.7);
        VectorView expect2 = VectorView.wrap(       0.0,        0.0, 0.1, -0.1, 1.2,        1.2, 7.9);

        assertEquals(expect1, StatUtil.cumsum(values1));
        assertEquals(expect2, StatUtil.cumsum(values2));
    }

    @Test public void testCumProd() {
        VectorView values1 = VectorView.wrap(2.0, 3.0,  4.0,  1.0, 0.0, 5.0);
        VectorView expect1 = VectorView.wrap(2.0, 6.0, 24.0, 24.0, 0.0, 0.0);

        VectorView values2 = VectorView.wrap(Double.NaN, Double.NaN, 0.1, -2.0, Double.NaN, -3.5, 7.0);
        VectorView expect2 = VectorView.wrap(       1.0,        1.0, 0.1, -0.2,       -0.2,  0.7, 4.9);

        assertEquals(expect1, StatUtil.cumprod(values1));
        assertEquals(expect2, StatUtil.cumprod(values2));
    }

    @Test public void testMax() {
        VectorView v1 = VectorView.wrap(1.0, 2.0, 3.0);
        VectorView v2 = VectorView.wrap(3.0, 4.0, -1.0, -10.0);
        VectorView v3 = VectorView.wrap(Double.NaN, Double.NaN, -3.0, 5.5, Double.NaN, -11.2);
        VectorView v4 = VectorView.wrap(Double.NaN, Double.POSITIVE_INFINITY, -3.0, 5.5, Double.NEGATIVE_INFINITY, -11.2);

        assertDouble(Double.NEGATIVE_INFINITY, StatUtil.max(VectorView.EMPTY));

        assertDouble(3.0, StatUtil.max(v1));
        assertDouble(4.0, StatUtil.max(v2));
        assertDouble(5.5, StatUtil.max(v3));

        assertDouble(Double.POSITIVE_INFINITY, StatUtil.max(v4));
    }

    @Test public void testMean() {
        VectorView v1 = VectorView.wrap(Double.NaN);
        VectorView v2 = VectorView.wrap(1.0, Double.NaN, 3.0, -0.5);
        VectorView v3 = VectorView.wrap(1.0, Double.NaN, 3.0, -0.5, Double.NEGATIVE_INFINITY);

        assertTrue(Double.isNaN(StatUtil.mean(VectorView.EMPTY)));
        assertTrue(Double.isNaN(StatUtil.mean(v1)));
        assertDouble(3.5 / 3.0, StatUtil.mean(v2));
        assertTrue(Double.NEGATIVE_INFINITY == StatUtil.mean(v3));
    }

    @Test public void testMeanSqr() {
        VectorView v1 = VectorView.wrap(Double.NaN);
        VectorView v2 = VectorView.wrap(1.0, Double.NaN, 3.0, -0.5);

        assertTrue(Double.isNaN(StatUtil.meansqr(VectorView.EMPTY)));
        assertTrue(Double.isNaN(StatUtil.meansqr(v1)));
        assertDouble(10.25 / 3.0, StatUtil.meansqr(v2));
    }

    @Test public void testWeightedMean() {
        VectorView values  = VectorView.wrap(0.5, Double.NaN, 3.0, -2.5, 2.0, 0.1);
        VectorView weights = VectorView.wrap(Double.NaN, 0.1, 1.0,  2.0, 3.0, 4.0);

        assertDouble(0.44, StatUtil.mean(values, weights));

        VectorView val1 = VectorView.wrap(1.5, 1.5, 1.5, 2.2, 2.2, 3.3);
        VectorView val2 = VectorView.wrap(1.5, 2.2, 3.3);
        VectorView wt2  = VectorView.wrap(3.0, 2.0, 1.0);

        assertDouble(StatUtil.mean(val1), StatUtil.mean(val2, wt2));
    }

    @Test public void testMedAbs() {
        assertTrue(Double.isNaN(StatUtil.medabs(VectorView.EMPTY)));
        assertTrue(Double.isNaN(StatUtil.medabs(VectorView.wrap(Double.NaN))));

        assertDouble(2.0, StatUtil.medabs(VectorView.wrap(-2.0, 1.0, 3.0)));
        assertDouble(2.5, StatUtil.medabs(VectorView.wrap(2.0, -3.0, -4.0, 1.0)));

        assertDouble(1.1, StatUtil.medabs(VectorView.wrap(-1.1, Double.NaN, 3.0, -0.5)));
        assertDouble(1.1, StatUtil.medabs(VectorView.wrap(-1.1, Double.NaN, 3.0, Double.NaN, -0.5)));

        assertDouble(2.0, StatUtil.medabs(VectorView.wrap(1.0, -2.0, 1.5, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)));
    }

    @Test public void testMedian() {
        assertTrue(Double.isNaN(StatUtil.median(VectorView.EMPTY)));
        assertTrue(Double.isNaN(StatUtil.median(VectorView.wrap(Double.NaN))));

        assertDouble(2.0, StatUtil.median(VectorView.wrap(2.0, 1.0, 3.0)));
        assertDouble(2.5, StatUtil.median(VectorView.wrap(2.0, 3.0, 4.0, 1.0)));

        assertDouble(1.1, StatUtil.median(VectorView.wrap(1.1, Double.NaN, 3.0, -0.5)));
        assertDouble(1.1, StatUtil.median(VectorView.wrap(1.1, Double.NaN, 3.0, Double.NaN, -0.5)));

        assertDouble(1.0, StatUtil.median(VectorView.wrap(1.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)));
    }

    @Test public void testMAD() {
        assertDouble(0.0, StatUtil.MAD(2.2, VectorView.wrap(2.2)));

        assertDouble(StatUtil.MAD_CONSTANT * 0.5, StatUtil.MAD(2.0, VectorView.wrap(2.0, 1.0, 2.5)));
        assertDouble(StatUtil.MAD_CONSTANT * 1.5, StatUtil.MAD(2.0, VectorView.wrap(2.0, 1.0, -1.0, 100.0, 3.5)));

        double[] values = new double[100000];

        for (int k = 0; k < values.length; k++)
            values[k] = random().nextGaussian(0.0, 1.0);

        assertEquals(1.0, StatUtil.MAD(0.0, VectorView.wrap(values)), 0.005);
    }

    @Test public void testMin() {
        VectorView v1 = VectorView.wrap(1.0, 2.0, 3.0);
        VectorView v2 = VectorView.wrap(3.0, 4.0, -1.0, -10.0);
        VectorView v3 = VectorView.wrap(-3.0, Double.NaN, 5.5, -11.2);
        VectorView v4 = VectorView.wrap(Double.NaN, Double.POSITIVE_INFINITY, -3.0, 5.5, Double.NEGATIVE_INFINITY, -11.2);

        assertDouble(Double.POSITIVE_INFINITY, StatUtil.min(VectorView.EMPTY));

        assertDouble(  1.0, StatUtil.min(v1));
        assertDouble(-10.0, StatUtil.min(v2));
        assertDouble(-11.2, StatUtil.min(v3));

        assertDouble(Double.NEGATIVE_INFINITY, StatUtil.min(v4));
    }

    @Test public void testNorm() {
        VectorView vec = VectorView.wrap(-1.0, 2.0, -3.0);

        assertDouble(6.0, StatUtil.norm1(vec));
        assertDouble(6.0, StatUtil.normp(vec, 1));

        assertEquals(3.741657, StatUtil.normp(vec, 2), 0.000001);
        assertEquals(3.301927, StatUtil.normp(vec, 3), 0.000001);

        assertEquals(3.741657, StatUtil.norm2(vec), 0.000001);
        assertDouble(3.0, StatUtil.normInf(vec));
    }

    @Test public void testProd() {
        assertDouble( 1.0, StatUtil.prod(VectorView.EMPTY));
        assertDouble(-1.5, StatUtil.prod(VectorView.wrap(1.0, Double.NaN, 3.0, -0.5)));
    }

    @Test public void testStDev() {
        assertDouble(Math.sqrt(2.5), StatUtil.stdev(VectorView.wrap(1.0, 2.0, 3.0, 4.0, 5.0)));
    }

    @Test public void testSum() {
        assertDouble(0.0, StatUtil.sum(VectorView.EMPTY));
        assertDouble(3.5, StatUtil.sum(VectorView.wrap(1.0, Double.NaN, 3.0, -0.5)));
    }

    @Test public void testSumSqr() {
        assertDouble(0.0, StatUtil.sumsqr(VectorView.EMPTY));
        assertDouble(10.25, StatUtil.sumsqr(VectorView.wrap(1.0, Double.NaN, 3.0, -0.5)));
    }

    @Test public void testVariance() {
        VectorView v1 = VectorView.wrap(1.0);
        VectorView v2 = VectorView.wrap(1.0, Double.NaN);
        VectorView v3 = VectorView.wrap(1.0, 2.0, 3.0, 4.0, 5.0);
        VectorView v4 = VectorView.wrap(1.0, 2.0, 3.0, Double.NaN, 4.0, 5.0);
        VectorView v5 = VectorView.wrap(1.0, 2.0, Double.NEGATIVE_INFINITY);
        VectorView v6 = VectorView.wrap(1.0, 2.0, Double.POSITIVE_INFINITY);
        
        assertTrue(Double.isNaN(StatUtil.variance(VectorView.EMPTY)));
        assertTrue(Double.isNaN(StatUtil.variance(v1)));
        assertTrue(Double.isNaN(StatUtil.variance(v2)));

        assertDouble(2.5, StatUtil.variance(v3));
        assertDouble(2.5, StatUtil.variance(v4));

        assertTrue(Double.isNaN(StatUtil.variance(v5)));
        assertTrue(Double.isNaN(StatUtil.variance(v6)));
    }

    @Test public void testWeightedVariance() {
        //
        // The normalization for weighted variance is 1 / (total weight),
        // not 1 / (N - 1) as it is for the equally-weighted method, but
        // the results will converge for large N...
        //
        double x1 = 1.23;
        double x2 = 3.21;
        double x3 = 4.84;
        double x4 = 7.77;

        ArrayDoubleList values1 = new ArrayDoubleList();

        for (int k1 = 0; k1 < 1000; k1++)
            values1.add(x1);

        for (int k2 = 0; k2 < 2000; k2++)
            values1.add(x2);

        for (int k3 = 0; k3 < 3000; k3++)
            values1.add(x3);

        for (int k4 = 0; k4 < 4000; k4++)
            values1.add(x4);

        VectorView values2 = VectorView.wrap(x1, x2, x3, x4);
        VectorView weight2 = VectorView.wrap(1.0, 2.0, 3.0, 4.0);
        VectorView weight3 = VectorView.wrap(0.1, 0.2, 0.3, 0.4);

        double var1 = StatUtil.variance(VectorView.wrap(values1.toArray()));
        double var2 = StatUtil.variance(values2, weight2);
        double var3 = StatUtil.variance(values2, weight3);

        assertEquals(5.033325, var1, 0.001);
        assertDouble(5.033325, var2);
        assertDouble(5.033325, var3);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StatUtilTest");
    }
}
