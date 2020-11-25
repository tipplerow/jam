
package jam.stoch.bifur;

import jam.junit.NumericTestBase;

import jam.stoch.StochEvent;
import jam.stoch.StochRate;
import jam.stoch.StochTime;

import org.junit.*;
import static org.junit.Assert.*;

public class BifurcationSystemTest extends NumericTestBase {
    private static BifurcationSystem createSystem() {
        return BifurcationSystem.create(100, 1.0, 2.0, 3.0);
    }

    private void assertPopulation(BifurcationSystem system, int sourcePop, int... sinkPops) {
        assertEquals(system.countProcesses(), sinkPops.length);
        assertEquals(sourcePop, system.getSource().getPopulation());

        for (int index = 0; index < sinkPops.length; ++ index)
            assertEquals(sinkPops[index], system.getSink(index).getPopulation());
    }

    private void assertRates(BifurcationSystem system, double... rates) {
        assertEquals(system.countProcesses(), rates.length);

        for (int index = 0; index < rates.length; ++ index)
            assertEquals(StochRate.valueOf(rates[index]), system.getProcess(index).getStochRate());
    }

    @Test public void testAccessors() {
        BifurcationSystem system = createSystem();

        assertPopulation(system, 100, 0, 0, 0);

        assertDouble(1.0, system.getProcess(0).getUnitRate());
        assertDouble(2.0, system.getProcess(1).getUnitRate());
        assertDouble(3.0, system.getProcess(2).getUnitRate());
    }

    @Test public void testEvent() {
        BifurcationSystem system = createSystem();

        BifurcationPath path0 = system.getProcess(0);
        BifurcationPath path1 = system.getProcess(1);
        BifurcationPath path2 = system.getProcess(2);

        StochEvent<BifurcationPath> event0 = StochEvent.mark(path0, StochTime.ZERO);
        StochEvent<BifurcationPath> event1 = StochEvent.mark(path1, StochTime.ZERO);
        StochEvent<BifurcationPath> event2 = StochEvent.mark(path2, StochTime.ZERO);

        assertPopulation(system, 100, 0, 0, 0);
        assertRates(system, 100.0, 200.0, 300.0);

        system.processEvent(event0);
        system.processEvent(event1);
        system.processEvent(event1);
        system.processEvent(event2);
        system.processEvent(event2);
        system.processEvent(event2);

        assertPopulation(system, 94, 1, 2, 3);
        assertRates(system, 94.0, 188.0, 282.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.bifur.BifurcationSystemTest");
    }
}
