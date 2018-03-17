
package jam.junit;

import jam.sim.DiscreteTimeSimulation;

import org.junit.*;
import static org.junit.Assert.*;

public class DiscreteTimeSimulationTest {
    private final static int TRIAL_COUNT = 5;
    private final static int STEP_COUNT = 10;
    
    private static final class Simulator extends DiscreteTimeSimulation {
        private int stepCounter;

        private Simulator() {
            super(new String[] { "data/app.prop" });
        }
        
        @Override protected void initializeSimulation() {
        }

        @Override protected boolean continueSimulation() {
            return getTrialIndex() < TRIAL_COUNT;
        }

        @Override protected void finalizeSimulation() {
        }

        @Override protected void initializeTrial() {
            stepCounter = 0;
        }

        @Override protected boolean continueTrial() {
            return getTimeStep() < STEP_COUNT;
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

        assertEquals(0, DiscreteTimeSimulation.getTrialIndex());
        assertEquals(0, DiscreteTimeSimulation.getTimeStep());

        sim.runSimulation();
        assertEquals(TRIAL_COUNT, DiscreteTimeSimulation.getTrialIndex());
        assertEquals(STEP_COUNT,  DiscreteTimeSimulation.getTimeStep());

        sim.runSimulation();
        assertEquals(TRIAL_COUNT, DiscreteTimeSimulation.getTrialIndex());
        assertEquals(STEP_COUNT,  DiscreteTimeSimulation.getTimeStep());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DiscreteTimeSimulationTest");
    }
}
