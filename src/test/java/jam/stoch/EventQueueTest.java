
package jam.stoch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.math.JamRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class EventQueueTest {
    private final JamRandom random = JamRandom.generator(20210501);
    private final EventQueue<FixedRateProc> queue;
    private final List<FixedRateProc> procs = new ArrayList<FixedRateProc>();
    private final List<StochEvent<FixedRateProc>> events = new ArrayList<StochEvent<FixedRateProc>>();

    private static final int PROC_COUNT = 25;
    private static final int NEXT_COUNT = 1000;

    public EventQueueTest() {
        createProcesses();
        createEvents();

        this.queue = EventQueue.create(events);
    }

    private void createProcesses() {
        while (procs.size() < PROC_COUNT)
            procs.add(FixedRateProc.create(1.0));
    }

    private void createEvents() {
        for (FixedRateProc proc : procs)
            events.add(StochEvent.create(proc, sampleTime(StochTime.ZERO)));
    }

    private StochTime sampleTime(StochTime prevTime) {
        return StochTime.valueOf(prevTime.doubleValue() + random.nextDouble());
    }

    @Test public void testNext() {
        for (int trial = 0; trial < NEXT_COUNT; ++trial)
            executeTrial();
    }

    private void executeTrial() {
        Collections.sort(events);
        /*
        System.out.println();
        System.out.println(events);
        System.out.println();
        System.out.println(queue);
        System.out.println();
        */
        int actual = queue.next().getIndex();
        int expected = events.get(0).getIndex();

        assertEquals(expected, actual);

        StochTime time = sampleTime(queue.next().getTime());
        FixedRateProc proc = procs.get(actual);
        StochEvent<FixedRateProc> event = StochEvent.create(proc, time);

        queue.update(event);
        events.set(0, event);
        queue.validateOrder();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.EventQueueTest");
    }
}
