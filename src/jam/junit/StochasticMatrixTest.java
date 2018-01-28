
package jam.junit;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.dist.DiscretePDF;
import jam.markov.StochasticMatrix;
import jam.vector.JamVector;

import org.junit.*;
import static org.junit.Assert.*;

public class StochasticMatrixTest extends NumericTestBase {

    @Test public void testSimple() {
	StochasticMatrix matrix = new StochasticMatrix(new double[][] {{ 0.7, 0.3 }, { 0.5, 0.5 }});

	assertDouble(0.625, matrix.getStationaryPDF().evaluate(0));
        assertDouble(0.375, matrix.getStationaryPDF().evaluate(1));
    }

    @Test public void testReichl() {
        StochasticMatrix matrix = 
            StochasticMatrix.REICHL;

	assertDouble(0.1, matrix.getStationaryPDF().evaluate(0));
        assertDouble(0.6, matrix.getStationaryPDF().evaluate(1));
        assertDouble(0.3, matrix.getStationaryPDF().evaluate(2));

        runSampleTest(matrix, 0.002, 0.1, 0.6, 0.3);
    }

    private void runSampleTest(StochasticMatrix matrix, double tolerance, double... expected) {
        Multiset<Integer> states = matrix.getStationaryCDF().sample(random(), 1000000);
        DiscretePDF pdf = DiscretePDF.compute(states);

        for (int stateIndex = 0; stateIndex < matrix.countStates(); ++stateIndex)
            assertEquals(expected[stateIndex], pdf.evaluate(stateIndex), tolerance);
    }

    @Test public void testUpdateState() {
        runUpdateStateTest(StochasticMatrix.REICHL, 1000000, 0.001);
    }

    private void runUpdateStateTest(StochasticMatrix matrix, int updateCount, double tolerance) {
        int state = matrix.sampleStationaryState(random());
        Multiset<Integer> stateCount = HashMultiset.create();

        for (int updateIndex = 0; updateIndex < updateCount; ++updateIndex) {
            state = matrix.update(random(), state);
            stateCount.add(state);
        }

        DiscretePDF actual   = DiscretePDF.compute(stateCount);
        DiscretePDF expected = matrix.getStationaryPDF();

        assertTrue(actual.equals(expected, tolerance));
    }

    @Test public void testUpdateVector() {
        runUpdateVectorTest(StochasticMatrix.REICHL, JamVector.valueOf(1.0, 0.0, 0.0), 100, 1.0E-12);
        runUpdateVectorTest(StochasticMatrix.REICHL, JamVector.valueOf(0.0, 1.0, 0.0), 100, 1.0E-12);
        runUpdateVectorTest(StochasticMatrix.REICHL, JamVector.valueOf(0.0, 0.0, 1.0), 100, 1.0E-12);
    }

    private void runUpdateVectorTest(StochasticMatrix matrix, JamVector state, int updateCount, double tolerance) {
        for (int updateIndex = 0; updateIndex < updateCount; ++updateIndex)
            state = matrix.update(state);

        DiscretePDF actual   = DiscretePDF.create(0, state);
        DiscretePDF expected = matrix.getStationaryPDF();

        assertTrue(actual.equals(expected, tolerance));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.junit.StochasticMatrixTest");
    }
}
