
package jam.markov;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.dist.DiscretePDF;
import jam.junit.NumericTestBase;

import org.junit.*;
import static org.junit.Assert.*;

public class MarkovProcessTest extends NumericTestBase {
    @Test public void testReichl() {
        Multiset<Integer> stateCount = HashMultiset.create();
        MarkovProcess process = new MarkovProcess(random(), StochasticMatrix.REICHL);

        for (int k = 0; k < 1000000; ++k)
            stateCount.add(process.next());

        DiscretePDF actual   = DiscretePDF.compute(stateCount);
        DiscretePDF expected = process.getMatrix().getStationaryPDF();

        assertTrue(actual.equals(expected, 0.001));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.markov.MarkovProcessTest");
    }
}
