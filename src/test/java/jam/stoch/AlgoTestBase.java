
package jam.stoch;

import com.google.common.base.Stopwatch;

import jam.math.DoubleUtil;
import jam.math.JamRandom;
import jam.stoch.decay.DecayProc;
import jam.stoch.decay.DecaySystem;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class AlgoTestBase {
    protected final JamRandom random;
    protected final DecaySystem system;

    protected AlgoTestBase() {
        this.system = createSystem();
        this.random = JamRandom.generator(20210501);
    }

    // Three fast decay processes with rates 1.0, 2.0, 3.0...
    private static final double FAST_RATE1 = 1.0;
    private static final double FAST_RATE2 = 2.0;
    private static final double FAST_RATE3 = 3.0;

    // 1000 slow decay processes with rate 0.1...
    private static final int SLOW_COUNT = 1000;
    private static final double SLOW_RATE = 0.1;

    // Fast processes start with a large population...
    private static final int FAST_POPULATION = 100000;

    // Slow processes start with a small population...
    private static final int SLOW_POPULATION = 10000;

    // Run the simulation long enough until about 10% of the initial
    // population has decayed...
    private static final int TRIAL_COUNT = 500000;

    private static DecaySystem createSystem() {
        int[] pops = new int[SLOW_COUNT + 3];
        double[] rates = new double[SLOW_COUNT + 3];

        for (int index = 0; index < SLOW_COUNT; ++index) {
            pops[index] = SLOW_POPULATION;
            rates[index] = SLOW_RATE;
        }

        pops[SLOW_COUNT] = FAST_POPULATION;
        pops[SLOW_COUNT + 1] = FAST_POPULATION;
        pops[SLOW_COUNT + 2] = FAST_POPULATION;

        rates[SLOW_COUNT] = FAST_RATE1;
        rates[SLOW_COUNT + 1] = FAST_RATE2;
        rates[SLOW_COUNT + 2] = FAST_RATE3;

        return DecaySystem.create(pops, rates);
    }

    public abstract StochAlgo<DecayProc> createAlgorithm();

    @Test public void testAlgorithm() {
        StochAlgo<DecayProc> algo = createAlgorithm();
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int trialIndex = 0; trialIndex < TRIAL_COUNT; ++trialIndex)
            algo.advance();

        stopwatch.stop();
        StochTime eventTime = system.lastEventTime();

        for (int index = 0; index < system.countProcesses(); ++index)
            System.out.println(system.getProcess(index).getPopulation());

        assertEquals(0.359, eventTime.doubleValue(), 0.001);

        for (int procIndex = 0; procIndex < system.countProcesses(); ++procIndex)
            assertPopulation(eventTime, procIndex, 0.01);

        System.out.println(eventTime);
        System.out.println(stopwatch);
    }

    private void assertPopulation(StochTime eventTime, int procIndex, double tolerance) {
        int actual = system.getProcess(procIndex).getPopulation();
        int expected = system.getProcess(procIndex).getExpectedPopulation(eventTime);
        double error = DoubleUtil.ratio(actual, expected) - 1.0;

        System.out.println(String.format("%5d, %5d, %8.4f", actual, expected, error));
        assertTrue(Math.abs(error) < tolerance);
    }
}
