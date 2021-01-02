
package jam.stoch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.math.JamRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class EventQueueTest {
    private final JamRandom random = JamRandom.generator(20210501);
    private final EventQueue queue;
    private final List<StochEvent> events = new ArrayList<StochEvent>();
    private final List<FixedRateProc> procs = new ArrayList<FixedRateProc>();

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
            events.add(StochEvent.first(proc, random));
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
        int actualIndex = queue.nextEvent().getProcIndex();
        int expectedIndex = events.get(0).getProcIndex();

        assertEquals(expectedIndex, actualIndex);

        StochEvent actualEvent = queue.nextEvent();
        StochEvent updatedEvent = actualEvent.next(random);

        queue.updateEvent(updatedEvent);
        events.set(0, updatedEvent);
        queue.validateOrder();
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.EventQueueTest");
    }
}
