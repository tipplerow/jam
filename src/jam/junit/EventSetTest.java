
package jam.junit;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.EnumMultiset;

import jam.math.DoubleUtil;
import jam.math.EventSet;
import jam.math.Probability;

import org.junit.*;
import static org.junit.Assert.*;

public class EventSetTest extends NumericTestBase {
    public static enum ABC { A, B, C };

    private Map<ABC, Probability> createEventProb() {
        Map<ABC, Probability> eventProb = new EnumMap<ABC, Probability>(ABC.class);

        eventProb.put(ABC.A, Probability.valueOf(0.20));
        eventProb.put(ABC.B, Probability.valueOf(0.35));
        eventProb.put(ABC.C, Probability.valueOf(0.45));

        return eventProb;
    }

    @Test public void testEventProbability() {
        EventSet<ABC> eventSet = EventSet.create(createEventProb());

        assertDouble(0.20, eventSet.getEventProbability(ABC.A).doubleValue());
        assertDouble(0.35, eventSet.getEventProbability(ABC.B).doubleValue());
        assertDouble(0.45, eventSet.getEventProbability(ABC.C).doubleValue());
    }

    @Test public void testSelect() {
        runSelectTest(createEventProb(), 100000, 0.01);
    }

    private void runSelectTest(Map<ABC, Probability> eventProb, int sampleCount, double tolerance) {
        EventSet<ABC> eventSet = EventSet.create(eventProb);
        EnumMultiset<ABC> eventCount = EnumMultiset.create(ABC.class);

        for (int index = 0; index < sampleCount; index++)
            eventCount.add(eventSet.select());

        for (ABC key : eventProb.keySet()) {
            double actual   = DoubleUtil.ratio(eventCount.count(key), sampleCount);
            double expected = eventProb.get(key).doubleValue();

            assertEquals(expected, actual, tolerance);
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.EventSetTest");
    }
}
