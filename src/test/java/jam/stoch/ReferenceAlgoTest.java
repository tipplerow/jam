
package jam.stoch;

import jam.stoch.decay.DecayProc;

import org.junit.*;
import static org.junit.Assert.*;

public class ReferenceAlgoTest extends AlgoTestBase {
    @Override public StochAlgo<DecayProc> createAlgorithm() {
        return ReferenceAlgo.create(random, system);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.ReferenceAlgoTest");
    }
}
