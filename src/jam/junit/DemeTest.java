
package jam.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jam.math.DoubleUtil;
import jam.tumor.Deme;
import jam.tumor.GrowthRate;
import jam.tumor.Lineage;
import jam.tumor.Mutation;
import jam.tumor.MutationList;
import jam.tumor.MutationRate;
import jam.tumor.NeutralLineage;
import jam.tumor.PerfectLineage;
import jam.tumor.TumorEnv;

import org.junit.*;
import static org.junit.Assert.*;

public class DemeTest extends NumericTestBase {
    @Test public void testNeutralRestricted() {
        //
        // Start with a deme that is double the maximum size, a 50%
        // growth rate, and an environment that forbids division. The
        // deme should never create a daughter.  Its total cell count
        // may inch slightly above its initial size because the new
        // mutated lineages (with sizes on the order one) undergo
        // birth and death processes stochastically rather than
        // deterministically.  The _expected_ net rate is zero but
        // there are fluctuations around that mean.
        //
        double mutProb = 0.05;
        double netGrow = 0.5;

        GrowthRate growthRate = GrowthRate.net(netGrow);
        MutationRate mutationRate = MutationRate.poisson(mutProb);
        
        int      initSize = 2 * TumorEnv.DEFAULT_MAXIMUM_DEME_SIZE;
        Lineage  lineage  = NeutralLineage.founder(growthRate, mutationRate, initSize);
        Deme     founder  = Deme.create(lineage);
        TumorEnv demeEnv  = TumorEnv.unrestricted(founder).noDemeDivision();

        // +----------+
        // |  STEP 1  |
        // +----------+
        List<Deme> daughters = founder.advance(demeEnv);
        assertTrue(daughters.isEmpty());
        
        int    cellCount1    = founder.countCells();
        int    lineageCount1 = founder.countLineages();
        double growthFactor1 = DoubleUtil.ratio(cellCount1, initSize);

        assertEquals(1.0, growthFactor1, 0.01);

        MutationList mutList1  = founder.getOriginalMutations();
        int          mutCount1 = mutList1.size();
        double       mutRate1  = DoubleUtil.ratio(mutCount1, initSize);
        
        assertTrue(mutCount1 >= lineageCount1);
        assertEquals(mutProb, mutRate1, 0.005);

        // +----------+
        // |  STEP 2  |
        // +----------+
        daughters = founder.advance(demeEnv);
        assertTrue(daughters.isEmpty());
        
        int    cellCount2    = founder.countCells();
        int    lineageCount2 = founder.countLineages();
        double growthFactor2 = DoubleUtil.ratio(cellCount2, cellCount1);

        assertEquals(1.0, growthFactor2, 0.01);

        MutationList mutList2  = founder.getOriginalMutations();
        int          mutCount2 = mutList2.size();
        double       mutRate2  = DoubleUtil.ratio(mutCount2, initSize) / 2.0;

        assertTrue(mutCount2 >= lineageCount2);
        assertEquals(mutProb, mutRate2, 0.002);
    }

    @Test public void testNeutralUnrestricted() {
        //
        // Start with a deme that is 75% of the maximum size, a 50%
        // growth rate, and an unrestricted environment.  It should
        // divide on the first time step...
        //
        double mutProb = 0.05;
        double netGrow = 0.5;

        GrowthRate growthRate = GrowthRate.net(netGrow);
        MutationRate mutationRate = MutationRate.poisson(mutProb);
        
        int      initSize = 3 * TumorEnv.DEFAULT_MAXIMUM_DEME_SIZE / 4;
        Lineage  lineage  = NeutralLineage.founder(growthRate, mutationRate, initSize);
        Deme     founder  = Deme.create(lineage);
        TumorEnv demeEnv  = TumorEnv.unrestricted(founder);

        assertEquals(1, founder.countLineages());

        List<Deme> daughters = founder.advance(demeEnv);
        assertEquals(1, daughters.size());

        Deme daughter = daughters.get(0);

        int cellCountFounder  = founder.countCells();
        int cellCountDaughter = daughter.countCells();

        double growthFactor = DoubleUtil.ratio(cellCountFounder + cellCountDaughter, initSize);

        assertDouble(1.0 + netGrow, growthFactor);
        assertEquals(1.0, DoubleUtil.ratio(cellCountDaughter, cellCountFounder), 0.05);

        // The founder keeps all original mutations; the daughter has
        // none (yet)...
        int mutCountFounder = founder.getOriginalMutations().size();
        int mutCountDaughter = daughter.getOriginalMutations().size();
        double mutRateFounder = DoubleUtil.ratio(mutCountFounder, cellCountFounder + cellCountDaughter);
            
        assertEquals(0, mutCountDaughter);
        assertEquals(mutProb, mutRateFounder, 0.001);
    }

    @Test public void testNeutralUnrestrictedSlow() {
        //
        // Start with a deme that is 10% of the maximum size, a 10%
        // growth rate, and an unrestricted environment.  It should
        // require 25 time steps to divide, in which time many large
        // (multi-cell) lineages should emerge...
        //
        double mutProb = 0.05;
        double netGrow = 0.1;

        GrowthRate growthRate = GrowthRate.net(netGrow);
        MutationRate mutationRate = MutationRate.poisson(mutProb);
        
        int      initSize = TumorEnv.DEFAULT_MAXIMUM_DEME_SIZE / 10;
        Lineage  lineage  = NeutralLineage.founder(growthRate, mutationRate, initSize);
        Deme     founder  = Deme.create(lineage);
        Deme     daughter = null;
        TumorEnv demeEnv  = TumorEnv.unrestricted(founder);

        while (daughter == null) {
            List<Deme> daughters = founder.advance(demeEnv);

            if (!daughters.isEmpty())
                daughter = daughters.get(0);
        }
            
        int cellCountFounder = founder.countCells();
        int cellCountDaughter = daughter.countCells();

        // Demes should be approximately equal in total cell count...
        assertEquals(1.0, DoubleUtil.ratio(cellCountDaughter, cellCountFounder), 0.05);

        // The founder keeps all original mutations; the daughter has
        // none (yet)...
        MutationList daughterList = daughter.getOriginalMutations();
        assertEquals(0, daughterList.size());
    }

    @Test public void testPerfectRestricted() {
        //
        // Start with a deme that is double the maximum size, a 50%
        // growth rate, and an environment that forbids division. The
        // deme should remain at its initial size and never create a
        // daughter...
        //
        int      initSize = 2 * TumorEnv.DEFAULT_MAXIMUM_DEME_SIZE;
        double   netGrow  = 0.5;
        Lineage  lineage  = PerfectLineage.founder(GrowthRate.net(netGrow), initSize);
        Deme     founder  = Deme.create(lineage);
        TumorEnv demeEnv  = TumorEnv.unrestricted(founder).noDemeDivision();

        for (int stepCount = 0; stepCount < 10; ++stepCount) {
            List<Deme> daughters = founder.advance(demeEnv);
            assertTrue(daughters.isEmpty());

            assertEquals(1, founder.countLineages());
            assertEquals(initSize, founder.countCells());
            assertEquals(initSize, lineage.countCells());
        }
    }

    @Test public void testPerfectUnrestricted() {
        //
        // Start with half of the maximum deme size and a 50% growth rate...
        //
        int      initSize = TumorEnv.DEFAULT_MAXIMUM_DEME_SIZE / 2;
        double   netGrow  = 0.5;
        Lineage  lineage  = PerfectLineage.founder(GrowthRate.net(netGrow), initSize);
        Deme     founder  = Deme.create(lineage);
        TumorEnv demeEnv  = TumorEnv.unrestricted(founder);

        assertEquals(1, founder.countLineages());
        assertEquals(initSize, founder.countCells());

        // Advance one step: Expect 1.5 times the initial cell count,
        // still only one lineage, no splitting of the deme...
        List<Deme> daughters = founder.advance(demeEnv);
        assertTrue(daughters.isEmpty());

        int    founderSize   = founder.countCells();
        double founderGrowth = DoubleUtil.ratio(founderSize, initSize);

        assertEquals(1, founder.countLineages());
        assertTrue(founderSize < demeEnv.getMaximumDemeSize());
        assertDouble(founderGrowth, 1.0 + netGrow);

        // Advance one more step: Expect 2.25 times the initial cell
        // count after the lineages advance, so the deme must divide
        // in two...
        daughters = founder.advance(demeEnv);
        assertEquals(1, daughters.size());

        Deme   daughter1   = daughters.get(0);
        int    totalSize   = founder.countCells() + daughter1.countCells();
        double totalGrowth = DoubleUtil.ratio(totalSize, initSize);

        // Still no new lineages...
        assertEquals(1, founder.countLineages());
        assertEquals(1, daughter1.countLineages());

        // The total growth rate is known exactly...
        assertDouble(totalGrowth, (1.0 + netGrow) * (1.0 + netGrow));

        // The founder and its daughter should have divided the cells
        // roughly evenly...
        assertEquals(1.0, DoubleUtil.ratio(founder.countCells(), daughter1.countCells()), 0.05);

        // No mutations from perfect lineages...
        assertEquals(MutationList.EMPTY, founder.getOriginalMutations());
        assertEquals(MutationList.EMPTY, founder.getAccumulatedMutations());
    }

    @Test public void testViewLineages() {
        Lineage lineage = PerfectLineage.founder(GrowthRate.NO_GROWTH, 1000);
        Deme    founder = Deme.create(lineage);

        assertEquals(1, founder.viewLineages().size());
        assertTrue(founder.viewLineages().contains(lineage));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testViewLineagesAdd() {
        Lineage lineage = PerfectLineage.founder(GrowthRate.NO_GROWTH, 1000);
        Deme    founder = Deme.create(lineage);

        founder.viewLineages().add(PerfectLineage.founder(GrowthRate.NO_GROWTH, 1000));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testViewLineagesRemove() {
        Lineage lineage = PerfectLineage.founder(GrowthRate.NO_GROWTH, 1000);
        Deme    founder = Deme.create(lineage);

        founder.viewLineages().remove(lineage);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.DemeTest");
    }
}
