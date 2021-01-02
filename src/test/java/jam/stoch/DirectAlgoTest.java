
package jam.stoch;

import jam.stoch.decay.DecayProc;

import org.junit.*;
import static org.junit.Assert.*;

public class DirectAlgoTest extends AlgoTestBase {
    @Override public StochAlgo createAlgorithm() {
        return DirectAlgo.create(random, system);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("jam.stoch.DirectAlgoTest");
    }
}
