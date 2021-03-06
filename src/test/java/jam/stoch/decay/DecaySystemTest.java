
package jam.stoch.decay;

import jam.junit.NumericTestBase;

import jam.stoch.StochEvent;
import jam.stoch.StochRate;
import jam.stoch.StochTime;

import org.junit.*;
import static org.junit.Assert.*;

public class DecaySystemTest extends NumericTestBase {
    private static final int[] POPS = new int[] { 100, 200, 300 };
    private static final double[] RATES = new double[] { 1.0, 2.0, 3.0 };
    
    private static DecaySystem createSystem() {
        return DecaySystem.create(POPS, RATES);
    }

    private void assertPopulation(DecaySystem system, int... pops) {
        assertEquals(system.countProcesses(), pops.length);

        for (int index = 0; index < pops.length; ++index)
            assertEquals(pops[index], system.getProcess(index).getPopulation());
    }

    private void assertRates(DecaySystem system, double... rates) {
        assertEquals(system.countProcesses(), rates.length);

        for (int index = 0; index < rates.length; ++ index)
            assertEquals(StochRate.valueOf(rates[index]), system.getProcess(index).getStochRate());
    }

    @Test public void testEvent() {
        DecaySystem system = createSystem();

        DecayProc proc0 = system.getProcess(0);
        DecayProc proc1 = system.getProcess(1);
        DecayProc proc2 = system.getProcess(2);

        assertPopulation(system, 100, 200, 300);
        assertRates(system, 100.0, 400.0, 900.0);

        system.updateState(StochEvent.mark(proc0, StochTime.valueOf(0.1)));
        system.updateState(StochEvent.mark(proc1, StochTime.valueOf(0.2)));
        system.updateState(StochEvent.mark(proc1, StochTime.valueOf(0.3)));
        system.updateState(StochEvent.mark(proc2, StochTime.valueOf(0.4)));
        system.updateState(StochEvent.mark(proc2, StochTime.valueOf(0.5)));
        system.updateState(StochEvent.mark(proc2, StochTime.valueOf(0.6)));

        assertPopulation(system, 99, 198, 297);
        assertRates(system, 99.0, 396.0, 891.0);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.decay.DecaySystemTest");
    }
}
