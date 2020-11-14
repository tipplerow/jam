
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
        assertEquals(system.processCount(), sinkPops.length);
        assertEquals(sourcePop, system.getSource().getPopulation());

        for (int index = 0; index < sinkPops.length; ++ index)
            assertEquals(sinkPops[index], system.getSink(index).getPopulation());
    }

    private void assertRates(BifurcationSystem system, double... rates) {
        assertEquals(system.processCount(), rates.length);

        for (int index = 0; index < rates.length; ++ index)
            assertEquals(StochRate.valueOf(rates[index]), system.getPath(index).getStochRate());
    }

    @Test public void testAccessors() {
        BifurcationSystem system = createSystem();

        assertPopulation(system, 100, 0, 0, 0);

        assertDouble(1.0, system.getPath(0).getUnitRate());
        assertDouble(2.0, system.getPath(1).getUnitRate());
        assertDouble(3.0, system.getPath(2).getUnitRate());
    }

    @Test public void testEvent() {
        BifurcationSystem system = createSystem();

        BifurcationPath path0 = system.getPath(0);
        BifurcationPath path1 = system.getPath(1);
        BifurcationPath path2 = system.getPath(2);

        StochEvent<BifurcationPath> event0 = StochEvent.create(path0, StochTime.ZERO);
        StochEvent<BifurcationPath> event1 = StochEvent.create(path1, StochTime.ZERO);
        StochEvent<BifurcationPath> event2 = StochEvent.create(path2, StochTime.ZERO);

        assertPopulation(system, 100, 0, 0, 0);
        assertRates(system, 100.0, 200.0, 300.0);

        system.update(event0);
        system.update(event1);
        system.update(event1);
        system.update(event2);
        system.update(event2);
        system.update(event2);

        assertPopulation(system, 94, 1, 2, 3);
        assertRates(system, 94.0, 188.0, 282.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.time.BifurcationSystemTest");
    }
}
