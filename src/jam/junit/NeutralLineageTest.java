
package jam.junit;

import java.util.List;

import jam.math.DoubleUtil;
import jam.tumor.GrowthRate;
import jam.tumor.Lineage;
import jam.tumor.MutationRate;
import jam.tumor.NeutralLineage;
import jam.tumor.TumorEnv;

import org.junit.*;
import static org.junit.Assert.*;

public class NeutralLineageTest extends NumericTestBase {
    @Test public void testAdvance() {
        checkAdvance(0.1, 0.01, 100000, 0.0005);
        checkAdvance(0.2, 0.02, 100000, 0.001);
        checkAdvance(0.3, 0.05, 100000, 0.002);
        checkAdvance(0.4, 0.1,  100000, 0.005);
    }

    private void checkAdvance(double netGrow, double mutProb, int initSize, double mutTolerance) {
        GrowthRate growthRate = GrowthRate.net(netGrow);
        MutationRate mutationRate = MutationRate.poisson(mutProb);

        Lineage founder = NeutralLineage.founder(growthRate, mutationRate, initSize);
        TumorEnv tumorEnv = TumorEnv.unrestricted(founder);
        List<Lineage> daughters = founder.advance(tumorEnv);

        // First, verify that each daughter lineage contains only one
        // cell...
        for (Lineage daughter : daughters)
            assertEquals(1, daughter.countCells());

        // Verify that each daughter lineage contains an original
        // mutation...
        for (Lineage daughter : daughters)
            assertFalse(daughter.getOriginalMutations().isEmpty());

        // The birth and death events occur before mutation, so verify
        // the actual growth rate...
        int    totalCellCount     = founder.countCells() + daughters.size();
        double actualGrowthFactor = DoubleUtil.ratio(totalCellCount, initSize);
        double expectedGrowthRate = 1.0 + netGrow;

        assertDouble(expectedGrowthRate, actualGrowthFactor);

        // Each mutation spawns a new daughter lineage, so verify the
        // actual mutation rate...
        double actualMutRate   = DoubleUtil.ratio(daughters.size(), totalCellCount);
        double expectedMutRate = mutProb;

        assertEquals(expectedMutRate, actualMutRate, mutTolerance);
    }

    @Test public void testAdvanceEmpty() {
        GrowthRate   growthRate   = GrowthRate.net(-1.0);
        MutationRate mutationRate = MutationRate.poisson(0.01);

        Lineage  founder  = NeutralLineage.founder(growthRate, mutationRate, 1);
        TumorEnv tumorEnv = TumorEnv.unrestricted(founder);

        assertTrue(founder.advance(tumorEnv).isEmpty());
        assertTrue(founder.isDead());
        assertTrue(founder.isEmpty());

        assertTrue(founder.advance(tumorEnv).isEmpty());
        assertTrue(founder.isDead());
        assertTrue(founder.isEmpty());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.NeutralLineageTest");
    }
}
