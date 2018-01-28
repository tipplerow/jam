
package jam.junit;

import java.util.Arrays;
import java.util.Map;

import jam.math.DoubleUtil;
import jam.math.VectorMoment;
import jam.tumor.Deme;
import jam.tumor.GrowthRate;
import jam.tumor.Lineage;
import jam.tumor.Mutation;
import jam.tumor.MutationList;
import jam.tumor.MutationRate;
import jam.tumor.MutationSurvey;
import jam.tumor.NeutralLineage;
import jam.tumor.Tumor;
import jam.tumor.TumorEnv;

import org.junit.*;
import static org.junit.Assert.*;

public class NeutralTumorTest extends NumericTestBase {
    @Test public void testAdvance() {
        checkAdvance(0.1, 0.02, 5000, 50);
    }

    private void checkAdvance(double netGrow, double mutProb, int initSize, int stepCount) {
        GrowthRate growthRate = GrowthRate.net(netGrow);
        MutationRate mutationRate = MutationRate.poisson(mutProb);

        Lineage  lineage  = NeutralLineage.transformer(growthRate, mutationRate, initSize);
        Tumor    tumor    = Tumor.create(lineage);
        TumorEnv tumorEnv = TumorEnv.unrestricted(tumor);

        while (tumorEnv.getTimeStep() <= stepCount) {
            MutationList mutations = tumor.getOriginalMutations();

            System.out.println(String.format("STEP:           %9d", tumorEnv.getTimeStep()));
            System.out.println(String.format("Deme count:     %9d", tumor.countDemes()));
            System.out.println(String.format("Lineage count:  %9d", tumor.countLineages()));
            System.out.println(String.format("Cell count:     %9d", tumor.countCells()));
            System.out.println(String.format("Mutation count: %9d", mutations.size()));
            System.out.println("-------------------------");

            tumorEnv.advance();
            assertEquals(Arrays.asList(tumor), tumorEnv.viewPropagators());
        }

        VectorMoment moment = tumor.computeMoment();
        Map<Mutation, MutationSurvey> surveys = tumor.surveyMutations(tumorEnv.getTimeStep());

        for (MutationSurvey survey : surveys.values())
            System.out.println(String.format("%d, %d, %8.4f", 
                                             survey.getMutation().getCreationTime(), 
                                             survey.getCarrierCount(),
                                             survey.getMoment().scalar() / moment.scalar()));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.NeutralTumorTest");
    }
}
