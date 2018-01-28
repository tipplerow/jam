
package jam.junit;

import jam.math.DoubleUtil;
import jam.math.Probability;
import jam.tumor.GrowthRate;
import jam.tumor.Lineage;
import jam.tumor.PerfectLineage;
import jam.tumor.TumorEnv;

import org.junit.*;
import static org.junit.Assert.*;

public class PerfectLineageTest extends NumericTestBase {
    @Test public void testAdvance() {
        assertAdvance(-0.5);
        assertAdvance(-0.2);
        assertAdvance( 0.2);
        assertAdvance( 0.5);
    }

    private void assertAdvance(double netRate) {
        int numSteps = 3;
        int initSize = 10000;

        GrowthRate growthRate = GrowthRate.net(netRate);
        Lineage    lineage    = PerfectLineage.founder(growthRate, initSize);
        TumorEnv   tumorEnv   = TumorEnv.unrestricted(lineage);

        for (int stepIndex = 0; stepIndex < numSteps; ++stepIndex) {
            //
            // Perfect lineages never mutate, so they never produce
            // daughter lineages...
            //
            assertTrue(lineage.advance(tumorEnv).isEmpty());
        }

        double actualGrowthFactor   = DoubleUtil.ratio(lineage.countCells(), initSize);
        double expectedGrowthFactor = Math.pow(growthRate.getGrowthFactor(), numSteps);

        assertEquals(expectedGrowthFactor, actualGrowthFactor, 0.001);
    }

    @Test public void testAdvanceEmpty() {
        GrowthRate growthRate = GrowthRate.net(-1.0);
        Lineage    founder    = PerfectLineage.founder(growthRate, 1);
        TumorEnv   tumorEnv   = TumorEnv.unrestricted(founder);

        assertTrue(founder.advance(tumorEnv).isEmpty());
        assertTrue(founder.isDead());
        assertTrue(founder.isEmpty());

        assertTrue(founder.advance(tumorEnv).isEmpty());
        assertTrue(founder.isDead());
        assertTrue(founder.isEmpty());
    }

    @Test public void testDivide() {
        assertDivide(100000, 0.25, 0.005);
        assertDivide(100000, 0.50, 0.005);
        assertDivide(100000, 0.75, 0.005);
    }

    private void assertDivide(int initSize, double retentionProb, double tolerance) {
        Lineage lin1 = PerfectLineage.founder(GrowthRate.net(0.0), initSize);
        Lineage lin2 = lin1.divide(Probability.valueOf(retentionProb));

        assertEquals(initSize, lin1.countCells() + lin2.countCells());
        assertEquals(retentionProb, DoubleUtil.ratio(lin1.countCells(), initSize), tolerance);
    }

    @Test public void testDivideSmall() {
        Lineage lin1 = PerfectLineage.founder(GrowthRate.net(0.0), 1);
        Lineage lin2 = lin1.divide(Probability.valueOf(1.0E-12));

        assertEquals(0, lin1.countCells());
        assertEquals(1, lin2.countCells());

        Lineage lin3 = PerfectLineage.founder(GrowthRate.net(0.0), 1);
        Lineage lin4 = lin3.divide(Probability.valueOf(0.9999999999));

        assertEquals(1, lin3.countCells());
        assertNull(lin4);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.PerfectLineageTest");
    }
}
