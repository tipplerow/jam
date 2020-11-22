
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

public class EventQueueTest {
    private final List<FixedRateProc> procs = new ArrayList<FixedRateProc>();

    // Three fast processes with rates 2000, 3000, 4000...
    private static final double FAST_RATE1 = 2000.0;
    private static final double FAST_RATE2 = 3000.0;
    private static final double FAST_RATE3 = 4000.0;

    // 1000 slow processes with rate 1...
    //private static final int SLOW_COUNT = 1000;
    private static final int SLOW_COUNT = 10;
    private static final double SLOW_RATE = 1.0;

    // Random number source...
    private static final JamRandom RANDOM = JamRandom.generator(20210501);

    public EventQueueTest() {
        createProcesses();
    }

    private List<StochEvent<FixedRateProc>> createEvents() {
        List<StochEvent<FixedRateProc>> events =
            new ArrayList<StochEvent<FixedRateProc>>(procs.size());

        for (FixedRateProc proc : procs)
            events.add(StochEvent.next(proc, StochTime.ZERO, RANDOM));

        return events;
    }

    private void createProcesses() {
        for (int index = 0; index < SLOW_COUNT; ++index)
            procs.add(FixedRateProc.create(SLOW_RATE));

        procs.add(FixedRateProc.create(FAST_RATE1));
        procs.add(FixedRateProc.create(FAST_RATE2));
        procs.add(FixedRateProc.create(FAST_RATE3));
    }

    @Test public void testQueue() {
        EventQueue<FixedRateProc> queue = EventQueue.create(createEvents());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.EventQueueTest");
    }
}
