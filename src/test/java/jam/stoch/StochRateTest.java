
package jam.stoch;

import java.util.ArrayList;
import java.util.List;

import jam.math.JamRandom;
import jam.math.StatUtil;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class StochRateTest {
    private static final StochRate rate1 = StochRate.valueOf(1.0);
    private static final StochRate rate2 = StochRate.valueOf(2.0);
    private static final StochRate rate3 = StochRate.valueOf(3.0);
    private static final StochRate rate4 = StochRate.valueOf(4.0);

    @Test public void testComparators() {
        List<StochRate> rates = new ArrayList<StochRate>();

        rates.add(rate3);
        rates.add(rate4);
        rates.add(rate1);
        rates.add(rate2);

        rates.sort(StochRate.ASCENDING_COMPARATOR);
        assertEquals(List.of(rate1, rate2, rate3, rate4), rates);

        rates.sort(StochRate.DESCENDING_COMPARATOR);
        assertEquals(List.of(rate4, rate3, rate2, rate1), rates);
    }

    @Test public void testSampleInterval() {
        int SAMPLE_COUNT = 10000;

        JamRandom random = JamRandom.generator(20201111);
        JamVector samples = new JamVector(SAMPLE_COUNT);

        for (int index = 0; index < samples.length(); ++index)
            samples.set(index, rate2.sampleInterval(random));

        assertEquals(0.5, StatUtil.mean(samples), 0.005);
        assertEquals(0.5 * Math.log(2.0),StatUtil.median(samples), 0.005);
    }

    @Test public void testTotal() {
        assertEquals(StochRate.valueOf(10.0), StochRate.total(List.of(rate1, rate2, rate3, rate4)));
        assertEquals(StochRate.valueOf(10.0), StochRate.total(List.of(rate4, rate3, rate2, rate1)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.time.StochRateTest");
    }
}
