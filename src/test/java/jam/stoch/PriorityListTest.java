
package jam.stoch;

import java.util.ArrayList;
import java.util.List;

import jam.lang.OrdinalIndex;
import jam.math.DoubleUtil;
import jam.math.JamRandom;
import jam.math.StatUtil;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class PriorityListTest {
    private final List<FixedRateProc> procs = new ArrayList<FixedRateProc>();

    // Three fast processes with rates 2000, 3000, 4000...
    private static final double FAST_RATE1 = 2000.0;
    private static final double FAST_RATE2 = 3000.0;
    private static final double FAST_RATE3 = 4000.0;

    // 1000 slow processes with rate 1...
    private static final int SLOW_COUNT = 1000;
    private static final double SLOW_RATE = 1.0;

    // Total rate...
    private static final StochRate TOTAL_RATE = StochRate.valueOf(10000.0);

    // Random number source...
    private static final JamRandom RANDOM = JamRandom.generator(20210501);

    public PriorityListTest() {
        createProcesses();
    }

    private void createProcesses() {
        for (int index = 0; index < SLOW_COUNT; ++index)
            procs.add(new FixedRateProc(SLOW_RATE));

        procs.add(new FixedRateProc(FAST_RATE1));
        procs.add(new FixedRateProc(FAST_RATE2));
        procs.add(new FixedRateProc(FAST_RATE3));
    }

    private static final class FixedRateProc implements StochProc {
        private final int index;
        private final StochRate rate;

        private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

        private FixedRateProc(double rate) {
            this.rate = StochRate.valueOf(rate);
            this.index = (int) ordinalIndex.next();
        }

        @Override public int getIndex() {
            return index;
        }

        @Override public StochRate getStochRate() {
            return rate;
        }

        @Override public String toString() {
            return String.format("FixedRateProc(%d, %f)", getIndex(), rate.doubleValue());
        }
    }

    @Test public void testSelect() {
        int trialCount = 1000000;
        int[] eventCounts = new int[procs.size()];

        long millis = System.currentTimeMillis();

        PriorityList<FixedRateProc> procList = PriorityList.create(procs);

        for (int trialIndex = 0; trialIndex < trialCount; ++trialIndex) {
            FixedRateProc proc = procList.select(RANDOM, TOTAL_RATE);
            ++eventCounts[(int) proc.getIndex()];
        }

        millis = System.currentTimeMillis() - millis;
        //System.out.println(millis);

        for (int eventIndex = 0; eventIndex < SLOW_COUNT; ++eventIndex) {
            assertEquals(0.0001, DoubleUtil.ratio(eventCounts[eventIndex], trialCount), 0.00005);
        }

        /*
        System.out.println(DoubleUtil.ratio(eventCounts[SLOW_COUNT], trialCount));
        System.out.println(DoubleUtil.ratio(eventCounts[SLOW_COUNT + 1], trialCount));
        System.out.println(DoubleUtil.ratio(eventCounts[SLOW_COUNT + 2], trialCount));
        */

        assertEquals(0.2, DoubleUtil.ratio(eventCounts[SLOW_COUNT], trialCount), 0.0005);
        assertEquals(0.3, DoubleUtil.ratio(eventCounts[SLOW_COUNT + 1], trialCount), 0.0005);
        assertEquals(0.4, DoubleUtil.ratio(eventCounts[SLOW_COUNT + 2], trialCount), 0.0005);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.PriorityListTest");
    }
}
