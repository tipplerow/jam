
package jam.junit;

import com.google.common.collect.Multiset;

import jam.dist.PoissonDistribution;
import jam.tumor.mutation.MutationRate;
import jam.util.MultisetUtil;

import org.junit.*;
import static org.junit.Assert.*;

public class MutationRateTest extends NumericTestBase {

    @Test public void testPoisson() {
        double mean = 0.1;
        MutationRate rate = MutationRate.poisson(mean);
        PoissonDistribution dist = PoissonDistribution.create(mean);
        Multiset<Integer> counts = MultisetUtil.hash(rate.sample(100000));

        for (int count : counts.elementSet())
            assertEquals(dist.pdf(count), MultisetUtil.frequency(counts, count), 0.001);
    }

    @Test public void testUniform() {
        double mean = 0.1;
        MutationRate rate = MutationRate.uniform(mean);
        Multiset<Integer> counts = MultisetUtil.hash(rate.sample(100000));

        assertEquals(2, counts.elementSet().size());

        assertEquals(1.0 - mean, MultisetUtil.frequency(counts, 0), 0.002);
        assertEquals(      mean, MultisetUtil.frequency(counts, 1), 0.002);
    }

    @Test public void testZero() {
        for (int index = 0; index < 100000; ++index)
            assertEquals(0, MutationRate.ZERO.sample());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.MutationRateTest");
    }
}
