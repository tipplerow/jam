
package jam.stoch;

import java.util.ArrayList;
import java.util.List;

import jam.lang.Ordinal;
import jam.lang.OrdinalIndex;
import jam.math.DoubleUtil;
import jam.math.JamRandom;
import jam.math.StatUtil;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class ProcStackTest {
    private final List<FixedRateProc> procs = new ArrayList<FixedRateProc>();

    // Three fast processes with rates 2000, 3000, 4000...
    private static final double FAST_RATE1 = 2000.0;
    private static final double FAST_RATE2 = 3000.0;
    private static final double FAST_RATE3 = 4000.0;

    // 1000 slow processes with rate 1...
    private static final int SLOW_COUNT = 1000;
    private static final double SLOW_RATE = 1.0;

    // Total rate...
    private static final double TOTAL_RATE = 10000.0;

    // Random number source...
    private static final JamRandom RANDOM = JamRandom.generator(20210501);

    public ProcStackTest() {
        createProcesses();
    }

    private void createProcesses() {
        for (int index = 0; index < SLOW_COUNT; ++index)
            procs.add(new FixedRateProc(SLOW_RATE));

        procs.add(new FixedRateProc(FAST_RATE1));
        procs.add(new FixedRateProc(FAST_RATE2));
        procs.add(new FixedRateProc(FAST_RATE3));
    }

    private static final class FixedRateProc extends Ordinal implements StochProc {
        private final StochRate rate;

        private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

        private FixedRateProc(double rate) {
            super(ordinalIndex.next());
            this.rate = StochRate.valueOf(rate);
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

        ProcStack<FixedRateProc> stack = ProcStack.create(procs);

        for (int trialIndex = 0; trialIndex < trialCount; ++trialIndex) {
            FixedRateProc proc = stack.select(TOTAL_RATE * RANDOM.nextDouble());
            ++eventCounts[(int) proc.getIndex()];
        }

        for (int eventIndex = 0; eventIndex < SLOW_COUNT; ++eventIndex) {
            assertEquals(0.0001, DoubleUtil.ratio(eventCounts[eventIndex], trialCount), 0.00005);
        }

        assertEquals(0.2, DoubleUtil.ratio(eventCounts[SLOW_COUNT], trialCount), 0.0002);
        assertEquals(0.3, DoubleUtil.ratio(eventCounts[SLOW_COUNT + 1], trialCount), 0.0002);
        assertEquals(0.4, DoubleUtil.ratio(eventCounts[SLOW_COUNT + 2], trialCount), 0.0002);

        //System.out.println(stack.getEfficiencyGain());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.ProcStackTest");
    }
}
