
package jam.junit;

import jam.sim.DiscreteTimeSimulation;

import org.junit.*;
import static org.junit.Assert.*;

public class DiscreteTimeSimulationTest {
    private final static int TRIAL_OFFSET = 2;
    private final static int TRIAL_TARGET = 5;
    private final static int STEP_TARGET = 10;
    
    private static final class Simulator extends DiscreteTimeSimulation {
        private int stepCounter;

        private Simulator() {
            super(new String[] { "data/test/app.prop" });
        }

        @Override protected int getTrialIndexOffset() {
            return TRIAL_OFFSET;
        }

        @Override public int getTrialTarget() {
            return TRIAL_TARGET;
        }

        @Override protected void initializeSimulation() {
        }

        @Override protected void finalizeSimulation() {
        }

        @Override protected void initializeTrial() {
            stepCounter = 0;
        }

        @Override protected boolean continueTrial() {
            return getTimeStep() < STEP_TARGET;
        }

        @Override protected void advanceTrial() {
            ++stepCounter;
            assertEquals(stepCounter, getTimeStep());
        }

        @Override protected void finalizeTrial() {
        }
    }

    @Test public void testTimeStep() {
        Simulator sim = new Simulator();

        sim.runSimulation();
        assertEquals(STEP_TARGET, sim.getTimeStep());
        assertEquals(TRIAL_TARGET, sim.getTrialCount());
        assertEquals(TRIAL_TARGET + TRIAL_OFFSET, sim.getTrialIndex());

        sim.runSimulation();
        assertEquals(STEP_TARGET, sim.getTimeStep());
        assertEquals(TRIAL_TARGET, sim.getTrialCount());
        assertEquals(TRIAL_TARGET + TRIAL_OFFSET, sim.getTrialIndex());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DiscreteTimeSimulationTest");
    }
}
